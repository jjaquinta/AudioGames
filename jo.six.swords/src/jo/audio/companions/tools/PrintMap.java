package jo.audio.companions.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.RegionGenBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.util.utils.obj.StringUtils;

public class PrintMap
{
    private String[] mArgs;
    private int      mX;
    private int      mY;
    private boolean  mFeatures = true;
    private boolean  mRuins = true;
    
    public PrintMap(String[] args)
    {
        mArgs = args;
        CoordBean ords = new CoordBean(CompConstLogic.INITIAL_LOCATION);
        mX = ords.getX();
        mY = ords.getY();
    }
    
    public void run() throws IOException
    {
        final BufferedWriter wtr = new BufferedWriter(new FileWriter("d:\\temp\\astar.log"));
        parseArgs();
        CoordBean ords = new CoordBean(mX, mY);
        GenerationLogic.getSquare(ords);
        RegionBean region = GenerationLogic.getRegion(ords);
        List<SquareBean> featureSquares = new ArrayList<>();
        printRegion(ords, region, featureSquares);
        if (mFeatures)
        {
            for (SquareBean sq : featureSquares)
                printFeature(region, sq);
        }
        wtr.close();
    }
    
    private void printFeature(RegionBean region, SquareBean sq)
    {
        System.out.println();
        System.out.println();
        FeatureBean feature = FeatureLogic.getFeature(region, sq, null);
        System.out.println("Feature: "+feature.getName());
        System.out.println("Type: "+CompConstLogic.FEATURE_NAMES[sq.getFeature()]);
        System.out.println("Ords: "+sq.getOrds());
        if ((sq.getFeature() == CompConstLogic.FEATURE_RUIN) && !mRuins)
            return;

        Map<String, CompRoomBean> roomMap = new HashMap<>();
        CompRoomBean startRoom = FeatureLogic.findRoom(feature, feature.getEntranceID());
        CoordBean startOrd = new CoordBean();
        mapRooms(feature, startRoom, startOrd, roomMap, new HashSet<>());
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;
        int maxName = 0;
        for (String s : roomMap.keySet())
        {
            CoordBean o = new CoordBean(s);
            left = Math.min(left, o.getX());
            right = Math.max(right, o.getX());
            top = Math.min(top, o.getY());
            bottom = Math.max(bottom, o.getY());
            CompRoomBean r = roomMap.get(o.toString());
            maxName = Math.max(maxName, r.getName().getIdent().length());
        }
        int namePrefix = maxName/2;
        int nameSuffix = (maxName - namePrefix) - 1;
        // print room map
        for (int y = top; y <= bottom; y++)
        {
            StringBuffer l1 = new StringBuffer();
            StringBuffer l2 = new StringBuffer();
            StringBuffer l3 = new StringBuffer();
            for (int x = left; x <= right; x++)
            {
                CoordBean o = new CoordBean(x, y);
                CompRoomBean r = roomMap.get(o.toString());
                if (r == null)
                {
                    l1.append(" ");
                    l1.append(StringUtils.spacePrefix("", maxName));
                    l1.append(" ");
                    l2.append(" ");
                    l2.append(StringUtils.spacePrefix("", maxName));
                    l2.append(" ");
                    l3.append(" ");
                    l3.append(StringUtils.spacePrefix("", maxName));
                    l3.append(" ");
                }
                else
                {
                    l1.append(" ");
                    l1.append(StringUtils.spacePrefix("", namePrefix));
                    if (StringUtils.isTrivial(r.getNorth()))
                        l1.append(" ");
                    else
                        l1.append("|");
                    l1.append(StringUtils.spacePrefix("", nameSuffix));
                    l1.append(" ");
                    if (StringUtils.isTrivial(r.getWest()))
                        l2.append(" ");
                    else
                        l2.append("-");
                    l2.append(StringUtils.spacePrefix(r.getName().getIdent(), maxName));
                    if (StringUtils.isTrivial(r.getEast()))
                        l2.append(" ");
                    else
                        l2.append("-");
                    l3.append(" ");
                    l3.append(StringUtils.spacePrefix("", namePrefix));
                    if (StringUtils.isTrivial(r.getSouth()))
                        l3.append(" ");
                    else
                        l3.append("|");
                    l3.append(StringUtils.spacePrefix("", nameSuffix));
                    l3.append(" ");
                }
            }
            System.out.println(l1.toString());
            System.out.println(l2.toString());
            System.out.println(l3.toString());
        }
    }

    private void mapRooms(FeatureBean feature, CompRoomBean room,
            CoordBean ord, Map<String, CompRoomBean> roomMap, Set<String> done)
    {
        if (roomMap.containsKey(ord.toString()))
            return;
        if (done.contains(room.getID()))
            return;
        done.add(room.getID());
        roomMap.put(ord.toString(), room);
        CompRoomBean north = FeatureLogic.findRoom(feature, room.getNorth());
        if (north != null)
            mapRooms(feature, north, ord.north(), roomMap, done);
        CompRoomBean south = FeatureLogic.findRoom(feature, room.getSouth());
        if (south != null)
            mapRooms(feature, south, ord.south(), roomMap, done);
        CompRoomBean east = FeatureLogic.findRoom(feature, room.getEast());
        if (east != null)
            mapRooms(feature, east, ord.east(), roomMap, done);
        CompRoomBean west = FeatureLogic.findRoom(feature, room.getWest());
        if (west != null)
            mapRooms(feature, west, ord.west(), roomMap, done);
    }

    private void printRegion(CoordBean ords, RegionBean region, List<SquareBean> featureSquares)
    {
        System.out.println("Region: "+region.getTitle());
        if (region instanceof RegionGenBean)
        System.out.println("Race: "+CompConstLogic.RACE_NAMES[((RegionGenBean)region).getPredominantRace()]);
        System.out.println("Ords: "+ords);
        System.out.print("    ");
        for (int x = 0; x < CompConstLogic.SQUARES_PER_REGION; x += 2)
            System.out.print(pad(region.getOrds().getX()+x, 3)+" ");
        System.out.println();
        System.out.print("      ");
        for (int x = 1; x < CompConstLogic.SQUARES_PER_REGION; x += 2)
            System.out.print(pad(region.getOrds().getX()+x, 3)+" ");
        System.out.println();
        for (int y = 0; y < CompConstLogic.SQUARES_PER_REGION; y++)
        {
            System.out.print(pad(region.getOrds().getY()+y, 3)+" ");
            for (int x = 0; x < CompConstLogic.SQUARES_PER_REGION; x++)
            {
                SquareBean sq = region.getSquare(x, y);
                switch (sq.getTerrain())
                {
                    case CompConstLogic.TERRAIN_PLAINS:
                        System.out.print(".");
                        break;
                    case CompConstLogic.TERRAIN_HILLS:
                        System.out.print("n");
                        break;
                    case CompConstLogic.TERRAIN_MOUNTAINS:
                        System.out.print("M");
                        break;
                    case CompConstLogic.TERRAIN_ARTIC:
                        System.out.print("*");
                        break;
                    case CompConstLogic.TERRAIN_FOREST:
                        System.out.print("@");
                        break;
                    case CompConstLogic.TERRAIN_DESERT:
                        System.out.print("_");
                        break;
                    case CompConstLogic.TERRAIN_JUNGLE:
                        System.out.print("#");
                        break;
                    case CompConstLogic.TERRAIN_SWAMP:
                        System.out.print("=");
                        break;
                    case CompConstLogic.TERRAIN_FRESHWATER:
                        System.out.print("-");
                        break;
                    case CompConstLogic.TERRAIN_SALTWATER:
                        System.out.print("~");
                        break;
                }
                if (sq.getFeature() > 0)
                {
                    if ((sq.getFeature() >= CompConstLogic.FEATURE_HAMLET) && (sq.getFeature() <= CompConstLogic.FEATURE_CITY))
                        System.out.print("&");
                    else if ((sq.getFeature() >= CompConstLogic.FEATURE_OUTPOST) && (sq.getFeature() <= CompConstLogic.FEATURE_CASTLE))
                        System.out.print("#");
                    else if (sq.getFeature() == CompConstLogic.FEATURE_RUIN)
                        System.out.print("?");
                    featureSquares.add(sq);
                }
                else if (sq.isAnyRoads())
                    System.out.print("+");
                else
                    System.out.print(" ");
            }
            System.out.print(" "+pad(region.getOrds().getY()+y, 3));
            System.out.println();
        }
        System.out.print("    ");
        for (int x = 0; x < CompConstLogic.SQUARES_PER_REGION; x += 2)
            System.out.print(pad(region.getOrds().getX()+x, 3)+" ");
        System.out.println();
        System.out.print("      ");
        for (int x = 1; x < CompConstLogic.SQUARES_PER_REGION; x += 2)
            System.out.print(pad(region.getOrds().getX()+x, 3)+" ");
        System.out.println();
    }
    
    private String pad(int v, int w)
    {
        String str = String.valueOf(v);
        while (str.length() < w)
            str += " ";
        return str;
    }
    
    private void parseArgs()
    {
        if (mArgs.length >  0)
        {
            mX = Integer.parseInt(mArgs[0]);
            if (mArgs.length >  1)
            {
                mY = Integer.parseInt(mArgs[1]);
            }
        }
    }
    
    public static void main(String[] argv) throws IOException
    {
        PrintMap app = new PrintMap(argv);
        app.run();
    }
}
