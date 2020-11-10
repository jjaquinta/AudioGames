package jo.audio.companions.logic.gen;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.DomainBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.RegionHandBean;
import jo.audio.companions.data.RegionVortexBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.data.SquareHandBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.IGenerator;
import jo.util.noise.Noise;
import jo.util.utils.MathUtils;

public class NullGenerator implements IGenerator
{    
    private String                   mName = "Eternal Plains";
    private int                      mTerrain = CompConstLogic.TERRAIN_PLAINS;
    private Long                     mSeed;
    private boolean                  mVorticies;
    private Noise mClouds;
    private Noise mRainfall;

    private Map<Integer, DomainBean> mDomains = new HashMap<>();
    
    public NullGenerator()
    {
        mSeed = 0L;
    }
    
    public NullGenerator(String name, int terrain)
    {        
        this();
        mName = name;
        mTerrain = terrain;
    }
    
    public NullGenerator(String name, int terrain, boolean vorticies)
    {        
        this(name, terrain);
        mVorticies = vorticies;
    }
    
    public DomainBean getDomain(CoordBean oord)
    {
        CoordBean ord = oord.toNearest(CompConstLogic.SQUARES_PER_DOMAIN);
        DomainBean domain = mDomains.get(ord.hashCode());
        if (domain == null)
        {
            domain = generateDomain(ord);
            mDomains.put(ord.hashCode(),  domain);
        }
        return domain;
    }
    
    public RegionBean getRegion(CoordBean oord)
    {
        CoordBean ord = oord.toNearest(CompConstLogic.SQUARES_PER_REGION);
        DomainBean domain = getDomain(ord);
        RegionBean region = domain.getRegion(ord);
        if (region == null)
        {
            region = generateRegion(domain, ord);
            domain.setRegion(ord,  region);
        }
        domain.setLastUsed(System.currentTimeMillis());
        return region;
    }
    
    public SquareBean getSquare(CoordBean ord)
    {
        RegionBean region = getRegion(ord);
        SquareBean square = region.getSquare(ord);
        return square;
    }
    
    private RegionHandBean generateRegion(DomainBean domain, CoordBean ord)
    {
        RegionHandBean region = new RegionVortexBean();
        region.setOrds(new CoordBean(ord));
        region.setGovernmentalStructure(CompConstLogic.GOVERNMENT_ANARCHY);
        region.setPredominantRace(CompConstLogic.RACE_MIXED);
        region.setTitle(mName);
        generateRegionDetails(region);
        return region;
    }

    private DomainBean generateDomain(CoordBean ord)
    {
        DomainBean domain = new DomainBean();
        domain.setOrds(new CoordBean(ord));
        domain.setPredominantRace(CompConstLogic.RACE_MIXED);
        domain.setGovernmentStructure(CompConstLogic.GOVERNMENT_ANARCHY);
        return domain;
    }

    private void generateRegionDetails(RegionHandBean region)
    {
        region.setSquares(new SquareHandBean[CompConstLogic.SQUARES_PER_REGION][CompConstLogic.SQUARES_PER_REGION]);
        for (int x = 0; x < CompConstLogic.SQUARES_PER_REGION; x++)
            for (int y = 0; y < CompConstLogic.SQUARES_PER_REGION; y++)
            {
                SquareHandBean sq = new SquareHandBean();
                region.getSquares()[x][y] = sq;
                sq.setAltitude(0);
                sq.setChallenge(0);
                sq.setChallenge2(0);
                if (((region.getOrds().getX() + x)%60 == 0) && ((region.getOrds().getY() + y)%60 == 0))
                    sq.setFeature(CompConstLogic.FEATURE_ARCH);
                else
                    sq.setFeature(CompConstLogic.FEATURE_NONE);
                sq.setOrds(new CoordBean(region.getOrds(), x, y));
                sq.setRoadEast(0);
                sq.setRoadNorth(0);
                sq.setRoadSouth(0);
                sq.setRoadWest(0);
                sq.setTerrain(mTerrain);
                sq.setTerrainDepth(0);
                sq.setSignposts(new ArrayList<>());
            }
    }

    @Override
    public FeatureBean getFeature(RegionBean region, SquareBean sq, boolean athiest)
    {
        FeatureBean feature = new FeatureBean();
        Random rnd = CompConstLogic.getRandom(sq.getOrds(), mSeed);
        //List<String> expansions = new ArrayList<>();
        switch (sq.getFeature())
        {
            case CompConstLogic.FEATURE_ARCH:
                DimArchLogic.generateArch(feature, sq, rnd);
                break;
            case CompConstLogic.FEATURE_VORTEX:
                VortexLogic.generateVortex(feature, sq, rnd);
                break;
            default:
                // some sort of static feature
                return null;
        }
        //FeaturesLogic.insertExpansions(feature, expansions);
        FeaturesLogic.ensureEntrance(feature);
        return feature;
    }


    public boolean isVorticies()
    {
        return mVorticies;
    }

    public void setVorticies(boolean vorticies)
    {
        mVorticies = vorticies;
    }
    
    public void moveVorticies(Random rnd)
    {
        if (!mVorticies)
            return;
        for (DomainBean d : mDomains.values())
        {
            RegionBean[][] regions = d.getRegions();
            for (int i = 0; i < regions.length; i++)
                for (int j = 0; j < regions[i].length; j++)
                    if (regions[i][j] instanceof RegionVortexBean)
                        ((RegionVortexBean)regions[i][j]).moveVortex(rnd);
                        
        }
    }
    
    public void getVorticies(List<CoordBean> vorticies)
    {
        if (!mVorticies)
            return;
        for (DomainBean d : mDomains.values())
        {
            RegionBean[][] regions = d.getRegions();
            for (int i = 0; i < regions.length; i++)
                for (int j = 0; j < regions[i].length; j++)
                    if (regions[i][j] instanceof RegionVortexBean)
                    {
                        RegionVortexBean rgn = (RegionVortexBean)regions[i][j];
                        int vx = rgn.getVortexX();
                        int vy = rgn.getVortexY();
                        if ((vx == -1) || (vy == -1))
                            continue;
                        CoordBean v = new CoordBean(rgn.getOrds(), vx, vy);
                        vorticies.add(v);
                    }
        }
    }
    
    private void initNoise()
    {
        if (mSeed == null)
        {
            mClouds = new Noise(100);
            mRainfall = new Noise(101);
        }
        else
        {
            mClouds = new Noise(mSeed.intValue()*100);
            mRainfall = new Noise(mSeed.intValue()*101);
        }
    }

    @Override
    public int getCloudCover(CoordBean ord, int time)
    {
        if (mTerrain == CompConstLogic.TERRAIN_DESERT)
            return CompConstLogic.CLOUD_SUNNY;
        initNoise();
        double cloud = mClouds.noise(ord.getX()/128.0, ord.getX()/128.0, time/24.0/60.0) + .5;
        if (cloud < 1/8.0)
            return CompConstLogic.CLOUD_SUNNY;
        else if (cloud < 2/8.0)
            return CompConstLogic.CLOUD_MOSTLY_SUNNY;
        else if (cloud < 5/8.0)
            return CompConstLogic.CLOUD_PARTLY_SUNNY;
        else if (cloud < 4/8.0)
            return CompConstLogic.CLOUD_PARTLY_CLOUDY;
        else if (cloud < 7/8.0)
            return CompConstLogic.CLOUD_MOSTLY_CLOUDY;
        else
            return CompConstLogic.CLOUD_CLOUDY;
    }

    @Override
    public int getPrecipitation(CoordBean ord, int time)
    {
        if (mTerrain == CompConstLogic.TERRAIN_DESERT)
            return CompConstLogic.PRECIPITATION_NONE;
        initNoise();
        double cloud = mClouds.noise(ord.getX()/128.0, ord.getX()/128.0, time/24.0/60.0) + .5;
        double rain = mRainfall.noise(ord.getX()/128.0, ord.getX()/128.0, time/24.0/60.0) + .5;
        if (rain <= cloud)
            return CompConstLogic.PRECIPITATION_NONE;
        double heavy = MathUtils.interpolate(rain, cloud, 1.0, 0, 1.0);
        if (heavy < 1/8.0)
            return CompConstLogic.PRECIPITATION_NONE;
        else if (heavy < 2/8.0)
            return CompConstLogic.PRECIPITATION_DRIZZLE;
        else if (heavy < 5/8.0)
            return CompConstLogic.PRECIPITATION_LIGHT;
        else if (heavy < 4/8.0)
            return CompConstLogic.PRECIPITATION_SHOWERS;
        else if (heavy < 7/8.0)
            return CompConstLogic.PRECIPITATION_HEAVY;
        else
            return CompConstLogic.PRECIPITATION_TORRENTS;
    }

    @Override
    public String dumpCache()
    {
        StringBuffer html = new StringBuffer();
        html.append("<h2>Null Generator, name="+mName+"</h2>");
        html.append("<ol>");
        for (DomainBean domain : mDomains.values())
        {
            html.append("<li>");
            html.append("Domain "+domain.getOrds());
            boolean first = true;
            for (int i = 0; i < domain.getRegions().length; i++)
                for (int j = 0; j < domain.getRegions()[i].length; j++)
                    if (domain.getRegions()[i][j] != null)
                    {
                        if (first)
                        {
                            html.append("<ol>");
                            first = false;
                        }
                        RegionBean region = domain.getRegions()[i][j];
                        html.append("<li>");
                        html.append("Region "+region.getTitle());
                        html.append(", "+(new Date(region.getLastUsed())));
                        html.append("</li>");
                    }
            if (!first)
                html.append("</ol>");
            html.append("</li>");
        }
        html.append("</ol>");
        return html.toString();
    }

    @Override
    public void cleanup()
    {
        long cutoff = System.currentTimeMillis() - CACHE_TIMEOUT;
        for (Integer key : mDomains.keySet().toArray(new Integer[0]))
            if (mDomains.get(key).getLastUsed() < cutoff)
                mDomains.remove(key);
    }
}
