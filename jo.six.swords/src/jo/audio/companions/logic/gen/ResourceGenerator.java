package jo.audio.companions.logic.gen;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.DomainBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.RegionHandBean;
import jo.audio.companions.data.RegionVortexBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.data.SquareBean.SignPost;
import jo.audio.companions.data.SquareHandBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.DemenseLogic;
import jo.audio.companions.logic.IGenerator;
import jo.audio.companions.logic.feature.FortLogic;
import jo.audio.companions.logic.feature.RuinLogic;
import jo.audio.companions.logic.feature.dungeon.DungeonLogic;
import jo.audio.companions.logic.feature.town.TownLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.noise.Noise;
import jo.util.utils.ArrayUtils;
import jo.util.utils.DebugUtils;
import jo.util.utils.MathUtils;

public class ResourceGenerator implements IGenerator
{    
    private String mBasePath;
    private Map<Integer, DomainBean> mDomains = new HashMap<>();    
    private Map<CoordBean,DockSpec> mDocSpecs = new HashMap<>();
    private JSONObject mArchSpecs;
    private int[][] mSubTypeTable = null;
    private boolean mVorticies;
    private Noise mClouds;
    private Noise mRainfall;
    
    public ResourceGenerator(String path)
    {
        mBasePath = path;
        bootstrapDemense();
        bootstrapDocks();
        bootstrapArches();
        bootstrapSubTypes();
    }
    
    public ResourceGenerator(String path, boolean vorticies)
    {
        this(path);
        mVorticies = vorticies;
    }
    
    private static final String[] TERRAIN_TYPES = {
            "PLAINS",
            "HILLS",
            "MOUNTAINS",
            "ARTIC",
            "FOREST",
            "DESERT",
            "JUNGLE",
            "SWAMP",
            "FRESHWATER",
            "SALTWATER",
        };
    private static final String[] SUB_TYPES = {
            "",
            "DEN",
            "MINE",
            "RUIN",
            "TEMPLE",
            "DUNGEON",
            "DEMON",
            "DEVIL",
            "DINO",
            "GROVE",
            "GIANT",
            "DRAGON",      
    };
    
    private void bootstrapSubTypes()
    {
        String subTypeFile = mBasePath + "/sub_type_table.json";
        try
        {
            JSONObject json = JSONUtils.readJSON(subTypeFile);
            int[][] subTypeTable = new int[10][21];
            for (int j = 0; j < TERRAIN_TYPES.length; j++)
            {
                String type = TERRAIN_TYPES[j];
                JSONArray arr = (JSONArray)json.get(type);
                if ((arr == null) || (arr.size() != 18))
                {
                    subTypeTable = null;
                    break;
                }
                for (int i = 0; i < 18; i++)
                {
                    String sub = (String)arr.get(i);
                    int idx = ArrayUtils.indexOf(SUB_TYPES, sub);
                    if (idx < 0)
                    {
                        subTypeTable = null;
                        break;
                    }
                    subTypeTable[j][i+3] = idx;
                }
                if (subTypeTable == null)
                    break;
            }
            mSubTypeTable = subTypeTable;
        }
        catch (Exception e)
        {    
            e.printStackTrace();
        }
    }

    public void bootstrapArches()
    {
        String archesFile = mBasePath + "/arch_locations.json";
        try
        {
            mArchSpecs = JSONUtils.readJSON(archesFile);
        }
        catch (Exception e)
        {    
            e.printStackTrace();
        }
    }

    public void bootstrapDemense()
    {
        String demenseFile = mBasePath + "/demense.json";
        try
        {
            JSONObject json = JSONUtils.readJSON(demenseFile);
            JSONArray demenses = (JSONArray)json.get("demenses");
            while (demenses.size() > 0)
                for (int i = 0; i < demenses.size(); i++)
                {
                    JSONObject dem = (JSONObject)demenses.get(i);
                    String liege = dem.getString("liegeID");
                    if ((liege == null) || (DemenseLogic.get(liege) != null))
                    {
                        DemenseBean d = new DemenseBean();
                        d.fromJSON(dem);
                        DemenseLogic.register(d);
                        demenses.remove(i);
                        i--;
                    }
                }
        }
        catch (Exception e)
        {    
            e.printStackTrace();
        }
    }

    public void bootstrapDocks()
    {
        String docksFile = mBasePath + "/docks.json";
        try
        {
            JSONObject json = JSONUtils.readJSON(docksFile);
            for (String key : json.keySet())
            {
                JSONObject dock = (JSONObject)json.get(key);
                DockSpec spec = new DockSpec();
                spec.mOrds = new CoordBean(key);
                JSONArray links = (JSONArray)dock.get("links");
                spec.mLinks = new CoordBean[links.size()];
                for (int i = 0; i < links.size(); i++)
                    spec.mLinks[i] = new CoordBean((String)links.get(i));
                spec.mName = dock.getString("town");
                spec.mDir = dock.getString("dir");
                mDocSpecs.put(spec.mOrds, spec);
            }
        }
        catch (Exception e)
        {    
            e.printStackTrace();
        }
        for (DockSpec ds : mDocSpecs.values())
        {
            ds.mLinkSpecs = new DockSpec[ds.mLinks.length];
            for (int i = 0; i < ds.mLinks.length; i++)
                ds.mLinkSpecs[i] = mDocSpecs.get(ds.mLinks[i]);
        }
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
            domain.setRegion(region.getOrds(),  region);
        }
        domain.setLastUsed(System.currentTimeMillis());
        return region;
    }
    
    public SquareBean getSquare(CoordBean ord)
    {
        RegionBean region = getRegion(ord);
        try
        {
            SquareBean square = region.getSquare(ord);
            return square;
        }
        catch (NullPointerException e)
        {
            throw new IllegalStateException("Null value while getting square for "+ord+" -> region "+region.getOrds(), e);
        }
    }

    @Override
    public FeatureBean getFeature(RegionBean region, SquareBean sq,
            boolean athiest)
    {
        if (region == null)
            region = getRegion(sq.getOrds());
        FeatureBean feature = new FeatureBean();
        Random rnd = CompConstLogic.getRandom(sq.getOrds());
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
                DemenseBean d = sq.getDemense();
                if (d != null)
                {
                    AudioMessageBean name = new AudioMessageBean();
                    //String lang = DemenseLogic.getLanguage(d);
                    if (d.getID().startsWith("H"))
                        name.setIdent(CompanionsModelConst.TEXT_HAMLET_OF_XXX/*lang+"_HAMLET_OF"*/);
                    else if (d.getID().startsWith("V"))
                        name.setIdent(CompanionsModelConst.TEXT_VILLAGE_OF_XXX/*lang+"_VILLAGE_OF"*/);
                    else if (d.getID().startsWith("T"))
                        name.setIdent(CompanionsModelConst.TEXT_TOWN_OF_XXX/*lang+"_TOWN_OF"*/);
                    else if (d.getID().startsWith("C"))
                        name.setIdent(CompanionsModelConst.TEXT_CITY_OF_XXX/*lang+"_CITY_OF"*/);
                    else
                        name.setIdent(d.getName().getIdent());
                    name.setArgs(new Object[] { d.getName() });
                    feature.setName(name);
                }
                break;
            case CompConstLogic.FEATURE_RUIN:
                RuinLogic.generateRuin(feature, sq, rnd, expansions, athiest, mSubTypeTable);
                break;
            case CompConstLogic.FEATURE_DOCK:
                DockSpec ds = mDocSpecs.get(sq.getOrds());
                DockLogic.generateDock(feature, sq, rnd, ds);
                break;
            case CompConstLogic.FEATURE_ARCH:
                ArchLogic.generateArch(feature, sq, rnd, mArchSpecs);
                break;
            case CompConstLogic.FEATURE_VORTEX:
                VortexLogic.generateVortex(feature, sq, rnd);
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

    
    private RegionHandBean generateRegion(DomainBean domain, CoordBean ord)
    {
        String fname = "rgn"+ord.getX()+"x"+ord.getY()+".json";
        String path = mBasePath + "/" + fname;
        RegionHandBean region = null;
        try
        {
            JSONObject json = JSONUtils.readJSON(path);
            region = new RegionVortexBean();
            region.fromJSON(json);
            region.getOrds().setZ(ord.getZ());
            for (int x = 0; x < CompConstLogic.SQUARES_PER_REGION; x++)
                for (int y = 0; y < CompConstLogic.SQUARES_PER_REGION; y++)
                {
                    SquareHandBean sq = (SquareHandBean)region.getSquare(x, y);
                    sq.getOrds().setZ(ord.getZ());
                    sq.setDemense(DemenseLogic.get(sq.getLiege()));
                    if (mDocSpecs.containsKey(sq.getOrds()))
                        sq.setFeature(CompConstLogic.FEATURE_DOCK);
                    else if (mArchSpecs.containsKey(sq.getOrds().toString()))
                        sq.setFeature(CompConstLogic.FEATURE_ARCH);
                    setupRoads(sq);
                    setupDescription(region, sq, x, y);
                    for (SignPost sp : sq.getSignposts())
                        sp.getDestination().setZ(ord.getZ());
                }
        }
        catch (Exception e)
        {    
            System.out.println("Cannot read '"+path+"' - "+e.getLocalizedMessage());
            //e.printStackTrace();
        }
        if (region == null)
        {
            region = new RegionVortexBean();
            region.setOrds(new CoordBean(ord));
            region.setGovernmentalStructure(CompConstLogic.GOVERNMENT_ANARCHY);
            region.setPredominantRace(CompConstLogic.RACE_MIXED);
            region.setTitle("Eternal Plains");
            generateRegionDetails(region);
        }
        return region;
    }

    public void setupDescription(RegionBean region, SquareHandBean sq, int x, int y)
    {
        if ((x == 0) || (y == 0) || (x == CompConstLogic.SQUARES_PER_REGION - 1) || (y == CompConstLogic.SQUARES_PER_REGION - 1))
            return;
        if (CompConstLogic.isWater(sq.getTerrain()))
            return;
        List<AudioMessageBean> salts = new ArrayList<>();
        if (region.getSquare(x - 1, y).getTerrain() == CompConstLogic.TERRAIN_SALTWATER)
            salts.add(new AudioMessageBean(CompanionsModelConst.TEXT_WEST));
        if (region.getSquare(x + 1, y).getTerrain() == CompConstLogic.TERRAIN_SALTWATER)
            salts.add(new AudioMessageBean(CompanionsModelConst.TEXT_EAST));
        if (region.getSquare(x, y - 1).getTerrain() == CompConstLogic.TERRAIN_SALTWATER)
            salts.add(new AudioMessageBean(CompanionsModelConst.TEXT_NORTH));
        if (region.getSquare(x, y + 1).getTerrain() == CompConstLogic.TERRAIN_SALTWATER)
            salts.add(new AudioMessageBean(CompanionsModelConst.TEXT_SOUTH));
        if (salts.size() > 0)
            sq.setDescription(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_LEADS_TO_THE_OCEAN,
                    AudioMessageBean.and(salts)));
        List<AudioMessageBean> freshs = new ArrayList<>();
        if (region.getSquare(x - 1, y).getTerrain() == CompConstLogic.TERRAIN_FRESHWATER)
            freshs.add(new AudioMessageBean(CompanionsModelConst.TEXT_WEST));
        if (region.getSquare(x + 1, y).getTerrain() == CompConstLogic.TERRAIN_FRESHWATER)
            freshs.add(new AudioMessageBean(CompanionsModelConst.TEXT_EAST));
        if (region.getSquare(x, y - 1).getTerrain() == CompConstLogic.TERRAIN_FRESHWATER)
            freshs.add(new AudioMessageBean(CompanionsModelConst.TEXT_NORTH));
        if (region.getSquare(x, y + 1).getTerrain() == CompConstLogic.TERRAIN_FRESHWATER)
            freshs.add(new AudioMessageBean(CompanionsModelConst.TEXT_SOUTH));
        if (freshs.size() > 0)
            sq.setDescription(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_LEADS_TO_A_LAKE,
                AudioMessageBean.and(freshs)));
    }

    public void setupRoads(SquareHandBean sq)
    {
        if (sq.isAnyRoads() && ((sq.getTerrain() == CompConstLogic.TERRAIN_SALTWATER) || (sq.getTerrain() == CompConstLogic.TERRAIN_FRESHWATER)))
        {
            if (sq.getRoadNorth() != 0)
                sq.setRoadNorth(SquareBean.T_BRIDGE);
            if (sq.getRoadSouth() != 0)
                sq.setRoadSouth(SquareBean.T_BRIDGE);
            if (sq.getRoadEast() != 0)
                sq.setRoadEast(SquareBean.T_BRIDGE);
            if (sq.getRoadWest() != 0)
                sq.setRoadWest(SquareBean.T_BRIDGE);
        }
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
                sq.setFeature(CompConstLogic.FEATURE_NONE);
                sq.setOrds(new CoordBean(region.getOrds(), x, y));
                sq.setRoadEast(0);
                sq.setRoadNorth(0);
                sq.setRoadSouth(0);
                sq.setRoadWest(0);
                sq.setTerrain(CompConstLogic.TERRAIN_SALTWATER);
                sq.setTerrainDepth(0);
                sq.setSignposts(new ArrayList<>());
            }
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
            DebugUtils.trace("Vortex for domain "+d.getOrds());
            RegionBean[][] regions = d.getRegions();
            for (int i = 0; i < regions.length; i++)
                for (int j = 0; j < regions[i].length; j++)
                    if (regions[i][j] instanceof RegionVortexBean)
                    {
                        DebugUtils.trace("Vortex for region "+regions[i][j].getOrds());
                        ((RegionVortexBean)regions[i][j]).moveVortex(rnd);
                    }
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
        mClouds = new Noise(100);
        mRainfall = new Noise(101);
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
        html.append("<h2>Resource Generator, path="+mBasePath+"</h2>");
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

    @Override
    public void clearCache()
    {
        mDomains.clear();
    }
}
