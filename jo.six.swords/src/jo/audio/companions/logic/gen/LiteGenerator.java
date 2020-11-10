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
import jo.audio.companions.data.RegionGenBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.IGenerator;
import jo.audio.companions.logic.NameLogic;
import jo.audio.companions.logic.feature.FortLogic;
import jo.audio.companions.logic.feature.RuinLogic;
import jo.audio.companions.logic.feature.dungeon.DungeonLogic;
import jo.audio.companions.logic.feature.town.TownLogic;
import jo.util.noise.Noise;
import jo.util.utils.MathUtils;

public class LiteGenerator implements IGenerator
{    
    private Map<Integer, DomainBean> mDomains = new HashMap<>();
    private Long mSeed;
    private Noise mClouds;
    private Noise mRainfall;
    
    public LiteGenerator()
    {
        mSeed = null;
    }

    public LiteGenerator(Long seed)
    {
        mSeed = seed;
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
            region = generateRegion(domain, ord, mSeed);
            domain.setRegion(ord,  region);
        }
        domain.setLastUsed(System.currentTimeMillis());
        return region;
    }
    
    public SquareBean getSquare(CoordBean ord)
    {
        RegionBean region = getRegion(ord);
        if (region instanceof RegionGenBean)
            if (!((RegionGenBean)region).isDetails())
                DetailsLogic.generateRegionDetails((RegionGenBean)region, mSeed);
        SquareBean square = region.getSquare(ord);
//        if (square == null)
//        {
//            square = generateSquare(ord);
//            region.setSquare(ord, square);
//        }
        return square;
    }
    
    public FeatureBean getFeature(RegionBean region, SquareBean sq, boolean athiest)
    {
        FeatureBean feature = new FeatureBean();
        Random rnd = CompConstLogic.getRandom(sq.getOrds(), mSeed);
        List<String> expansions = new ArrayList<>();
        switch (sq.getFeature())
        {
            case CompConstLogic.FEATURE_OUTPOST:
            case CompConstLogic.FEATURE_FORT:
            case CompConstLogic.FEATURE_CASTLE:
                FortLogic.generateCastle(region, sq, feature, sq.getFeature(), rnd);
                break;
            case CompConstLogic.FEATURE_HAMLET:
            case CompConstLogic.FEATURE_TOWN:
            case CompConstLogic.FEATURE_VILLAGE:
            case CompConstLogic.FEATURE_CITY:
                TownLogic.generateCity(region, sq, feature, sq.getFeature(), rnd, expansions, athiest);
                break;
            case CompConstLogic.FEATURE_RUIN:
                RuinLogic.generateRuin(feature, sq, rnd, expansions, athiest);
                break;
            case CompConstLogic.FEATURE_DUNGEON:
                DungeonLogic.generateDungeon(feature, sq, rnd);
                break;
            default:
                // some sort of static feature
                return null;
        }
        FeaturesLogic.insertExpansions(feature, expansions);
        FeaturesLogic.ensureEntrance(feature);
        return feature;
    }
    
    private static RegionGenBean generateRegion(DomainBean domain, CoordBean ord, Long seed)
    {
        RegionGenBean region = new RegionGenBean();
        region.setOrds(new CoordBean(ord));
        Random rnd = CompConstLogic.getRandom(ord, seed);
        region.setGovernmentalStructure(CompConstLogic.roll(CompConstLogic.TABLE_GOVERNMENT_NOT_IN_DOMAIN, rnd));
        region.setPredominantRace(CompConstLogic.roll(CompConstLogic.TABLE_RACE_IN_REGION[domain.getPredominantRace()], rnd));
        region.setName(NameLogic.kingdomName(rnd, region.getPredominantRace()));
        return region;
    }

    private DomainBean generateDomain(CoordBean ord)
    {
        return DomainLogic.generateDomain(ord);
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
        html.append("<h2>Lite Generator, seed="+mSeed+"</h2>");
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
                        html.append("Region "+region.getOrds());
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
