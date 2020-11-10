package jo.audio.companions.logic.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.RegionGenBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.data.SquareGenBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.obj.StringUtils;

public class FeaturesLogic
{

    static void generateRegionFeatures(RegionGenBean region,
            List<SquareGenBean> squares, Random rnd)
    {
        for (int dx = 0; dx < CompConstLogic.SQUARES_PER_REGION; dx++)
            for (int dy = 0; dy < CompConstLogic.SQUARES_PER_REGION; dy++)
            {
                SquareGenBean square = (SquareGenBean)region.getSquare(dx, dy);
                float[] terrainMap = new float[CompConstLogic.TERRAIN_SALTWATER+1];
                float incr = 2.0f;
                addTerrain(terrainMap, region, dx, dy, 0, 0, incr);
                for (int band = 1; band <= 3; band++)
                {
                    incr *= .5f;
                    for (int i = 0; i < band*2; i++)
                    {
                        addTerrain(terrainMap, region, dx, dy, -band+i, -band, incr);
                        addTerrain(terrainMap, region, dx, dy, band, -band+i, incr);
                        addTerrain(terrainMap, region, dx, dy, band-i, band, incr);
                        addTerrain(terrainMap, region, dx, dy, -band, band-i, incr);
                    }
                }
                float cityScore = 0;
                float fortScore = 0;
                for (int i = 0; i < CompConstLogic.TABLE_CITY_SITE.length; i++)
                {
                    cityScore += Math.abs(terrainMap[i] - CompConstLogic.TABLE_CITY_SITE[i]);
                    fortScore += Math.abs(terrainMap[i] - CompConstLogic.TABLE_FORT_SITE[i]);
                }
                square.setCityScore(cityScore);
                square.setFortScore(fortScore);
            }        
        placeCities(region, squares);
        placeForts(region, squares);
        placeRuins(region, rnd);
        if (region.getOrds().getZ() == CompConstLogic.DIM_FREEPLAY)
            placeDungeons(region, rnd);
    }

    public static void placeDungeons(RegionBean region, Random rnd)
    {
        int todo = CompConstLogic.TABLE_FEATURES_PER_GOVERNMENT[region.getGovernmentalStructure()][CompConstLogic.FEATURE_DUNGEON];
        int maxTries = todo*8;
        while ((todo > 0) && (maxTries-- > 0))
        {
            int x = rnd.nextInt(CompConstLogic.SQUARES_PER_REGION);
            int y = rnd.nextInt(CompConstLogic.SQUARES_PER_REGION);
            SquareBean sq = region.getSquares()[x][y];
            if ((sq.getTerrain() == CompConstLogic.TERRAIN_FRESHWATER) || (sq.getTerrain() == CompConstLogic.TERRAIN_SALTWATER))
                continue;
            if (sq.getFeature() != CompConstLogic.FEATURE_NONE)
                continue;
            if (sq.isAnyRoads())
                continue;
            sq.setFeature(CompConstLogic.FEATURE_DUNGEON);
            todo--;
        }
    }

    public static void placeRuins(RegionBean region, Random rnd)
    {
        int todo = CompConstLogic.TABLE_FEATURES_PER_GOVERNMENT[region.getGovernmentalStructure()][CompConstLogic.FEATURE_RUIN];
        int maxTries = todo*8;
        while ((todo > 0) && (maxTries-- > 0))
        {
            int x = rnd.nextInt(CompConstLogic.SQUARES_PER_REGION);
            int y = rnd.nextInt(CompConstLogic.SQUARES_PER_REGION);
            SquareBean sq = region.getSquares()[x][y];
            if ((sq.getTerrain() == CompConstLogic.TERRAIN_FRESHWATER) || (sq.getTerrain() == CompConstLogic.TERRAIN_SALTWATER))
                continue;
            if (sq.getFeature() != CompConstLogic.FEATURE_NONE)
                continue;
            if (sq.isAnyRoads())
                continue;
            sq.setFeature(CompConstLogic.FEATURE_RUIN);
            todo--;
        }
    }

    public static void placeForts(RegionBean region, List<SquareGenBean> squares)
    {
        List<SquareGenBean> fortSpots = new ArrayList<>();
        fortSpots.addAll(squares);
        Collections.sort(fortSpots, new Comparator<SquareGenBean>() {
            @Override
            public int compare(SquareGenBean o1, SquareGenBean o2)
            {
                return (int)Math.signum(o1.getFortScore() - o2.getFortScore());
            }
        });
        for (int feature = CompConstLogic.FEATURE_CASTLE; feature >= CompConstLogic.FEATURE_OUTPOST; feature--)
        {
            for (int i = 0; i < CompConstLogic.TABLE_FEATURES_PER_GOVERNMENT[region.getGovernmentalStructure()][feature]; i++)
                placeFeature(region, fortSpots, feature);
        }
    }

    private static void placeCities(RegionGenBean region, List<SquareGenBean> squares)
    {
        List<SquareGenBean> citySpots = new ArrayList<>();
        citySpots.addAll(squares);
        Collections.sort(citySpots, new Comparator<SquareGenBean>() {
            @Override
            public int compare(SquareGenBean o1, SquareGenBean o2)
            {
                return (int)Math.signum(o1.getCityScore() - o2.getCityScore());
            }
        });
        for (int feature = CompConstLogic.FEATURE_CITY; feature >= CompConstLogic.FEATURE_HAMLET; feature--)
        {
            for (int i = 0; i < CompConstLogic.TABLE_FEATURES_PER_GOVERNMENT[region.getGovernmentalStructure()][feature]; i++)
                placeFeature(region, citySpots, feature);
        }
    }
    
    public static void placeFeature(RegionBean region,
            List<? extends SquareBean> citySpots, int feature)
    {
        SquareBean square;
        for (;;)
        {
            if (citySpots.size() == 0)
                return;
            square = citySpots.get(0);
            citySpots.remove(0);
            if (square.getFeature() == CompConstLogic.FEATURE_NONE)
                break;
        }
        square.setFeature(feature);
        int r = CompConstLogic.TABLE_FEATURE_RADIUS[feature];
        for (Iterator<? extends SquareBean> i = citySpots.iterator(); i.hasNext(); )
            if (square.getOrds().dist(i.next().getOrds()) <= r)
                i.remove();
    }

    public static void addTerrain(float[] map, RegionBean region, int x, int y, int dx, int dy, float incr)
    {
        x += dx;
        if ((x < 0) || (x >= CompConstLogic.SQUARES_PER_REGION))
            return;
        y += dy;
        if ((y < 0) || (y >= CompConstLogic.SQUARES_PER_REGION))
            return;
        SquareBean s = region.getSquares()[x][y];
        map[s.getTerrain()] += incr;
    }


    public static void insertExpansions(FeatureBean feature,
            List<String> expansions)
    {
        if (expansions.size() == 0)
            return;
        for (CompRoomBean room : feature.getRooms())
            insertExpansions(room, expansions);
    }

    public static void insertExpansions(CompRoomBean room,
            List<String> expansions)
    {
        if ((expansions == null) || (expansions.size() == 0))
            return;
        FeaturesLogic.expandString(room.getName(), expansions);
        FeaturesLogic.expandString(room.getDescription(), expansions);
    }

    private static void expandString(AudioMessageBean msg, List<String> expansions)
    {
        if (msg.getArgs() == null)
            return;
        for (int j = 0; j < msg.getArgs().length; j++)
            if (msg.getArgs()[j] instanceof String)
            {
                String val = (String)msg.getArgs()[j];
                for (int i = 0; i < expansions.size(); i++)
                    val = val.replace("?"+i+"?", expansions.get(i));
                msg.getArgs()[j] = val;
            }
    }

    public static void ensureEntrance(FeatureBean feature)
    {
        if (StringUtils.isTrivial(feature.getEntranceID()))
        {
            for (CompRoomBean room : feature.getRooms())
                if ("$exit".equals(room.getNorth()) || "$exit".equals(room.getSouth()) || "$exit".equals(room.getEast()) || "$exit".equals(room.getWest()))
                {
                    feature.setEntranceID(room.getID());
                    break;
                }
            if (StringUtils.isTrivial(feature.getEntranceID()))
            {
                if (feature.getRooms().size() == 0)
                    System.err.println(feature.getName()+" has no rooms!");
                feature.setEntranceID(feature.getRooms().get(0).getID());
            }
        }
    }
}
