package jo.audio.companions.logic.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.RegionGenBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.data.SquareGenBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.util.astar.AStarNode;
import jo.util.astar.AStarSearch;

public class RoadsLogic
{

    static void generateRegionRoads(RegionGenBean region, Random rnd)
    {
        if (region.getGovernmentalStructure() == CompConstLogic.GOVERNMENT_ANARCHY)
            return; // no roads
        List<int[]> todo = new ArrayList<>();
        if (region.getLord() != null)
            addBoundaryPOI(region, region.getLord(), todo);
        for (RegionBean c : region.getVassals())
            addBoundaryPOI(region, c, todo);
        roadStrategy2(region, todo);
        for (int x = 0; x < CompConstLogic.SQUARES_PER_REGION; x++)
            for (int y = 0; y < CompConstLogic.SQUARES_PER_REGION; y++)
            {
                SquareGenBean sq = (SquareGenBean)region.getSquare(x, y);
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
        //roadStrategy3(region, todo);
        //roadStrategy1(region, todo);
    }

    /*
    private static void roadStrategy1(RegionBean region, List<int[]> todo)
    {
        for (int dx = 0; dx < CompConstLogic.SQUARES_PER_REGION; dx++)
            for (int dy = 0; dy < CompConstLogic.SQUARES_PER_REGION; dy++)
            {
                int f = region.getSquares()[dx][dy].getFeature();
                if ((f >= CompConstLogic.FEATURE_HAMLET) && (f <= CompConstLogic.FEATURE_CITY))
                    todo.add(new int[] { dx, dy });
            }
        List<int[]> poiPairs = new ArrayList<>();
        for (int i = 0; i < todo.size() - 1; i++)
        {
            int[] poisA = todo.get(i);
            for (int j = i + 1; j < todo.size(); j++)
            {
                int[] poisB = todo.get(j);
                int[] poiPair = new int[] { poisA[0], poisA[1], poisB[0], poisB[1], Math.abs(poisA[0] - poisB[0]) + Math.abs(poisA[1] - poisB[1])};
                poiPairs.add(poiPair);
            }
        }
        Collections.sort(poiPairs, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2)
            {
                return o1[4] - o2[4];
            }
        });
        for (int[] poiPair : poiPairs)
            generateRoad(region.getSquares(), poiPair[0], poiPair[1], poiPair[2], poiPair[3], null);
    }
    */

    private static void roadStrategy2(RegionBean region, List<int[]> todo)
    {
        int[] capital = null;
        int capSize = -1;
        for (int dx = 0; dx < CompConstLogic.SQUARES_PER_REGION; dx++)
            for (int dy = 0; dy < CompConstLogic.SQUARES_PER_REGION; dy++)
            {
                int f = region.getSquares()[dx][dy].getFeature();
                if ((f >= CompConstLogic.FEATURE_HAMLET) && (f <= CompConstLogic.FEATURE_CITY))
                {
                    if ((capital == null) || (f > capSize))
                    {
                        if (capital != null)
                            todo.add(capital);
                        capital = new int[] { dx, dy };
                        capSize = f;
                    }
                    else
                        todo.add(new int[] { dx, dy });
                }
                else if ((f >= CompConstLogic.FEATURE_FORT) && (f <= CompConstLogic.FEATURE_CASTLE))
                {
                    todo.add(new int[] { dx, dy });
                }
            }
        List<int[]> done = new ArrayList<>();
        done.add(capital);
        while (todo.size() > 0)
        {
            int bestTodo = -1;
            int bestDone = -1;
            int bestv = 0;
            for (int i = 0; i < todo.size(); i++)
            {
                int[] testTodo = todo.get(i);
                for (int j = 0; j < done.size(); j++)
                {
                    int[] testDone = done.get(j);
                    int v = Math.abs(testTodo[0] - testDone[0]) + Math.abs(testTodo[1] - testDone[1]);
                    if ((bestTodo < 0) || (v < bestv))
                    {
                        bestTodo = i;
                        bestDone = j;
                        bestv = v;
                    }
                }
            }
            int[] winnerTodo = todo.get(bestTodo);
            int[] winnerDone = done.get(bestDone);
            generateRoad(region.getSquares(), winnerTodo[0], winnerTodo[1], winnerDone[0], winnerDone[1], null);
            done.add(winnerTodo);
            todo.remove(bestTodo);
        }
    }

    /*
    private static void roadStrategy3(RegionBean region, List<int[]> todo)
    {
        int[] capital = null;
        int capSize = -1;
        for (int dx = 0; dx < CompConstLogic.SQUARES_PER_REGION; dx++)
            for (int dy = 0; dy < CompConstLogic.SQUARES_PER_REGION; dy++)
            {
                int f = region.getSquares()[dx][dy].getFeature();
                if ((f >= CompConstLogic.FEATURE_HAMLET) && (f <= CompConstLogic.FEATURE_CITY))
                {
                    if ((capital == null) || (f > capSize))
                    {
                        if (capital != null)
                            todo.add(capital);
                        capital = new int[] { dx, dy };
                        capSize = f;
                    }
                    else
                        todo.add(new int[] { dx, dy });
                }
            }
        final int[] theCapital = capital;
        Collections.sort(todo, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2)
            {
                int d1 = Math.abs(o1[0] - theCapital[0]) + Math.abs(o1[1] - theCapital[1]);
                int d2 = Math.abs(o2[0] - theCapital[0]) + Math.abs(o2[1] - theCapital[1]);
                return d1 - d2;
            }
        });
        for (int[] xy : todo)
            generateRoad(region.getSquares(), capital[0], capital[1], xy[0], xy[1], null);
    }
    */

    private static void addBoundaryPOI(RegionBean region, RegionBean region2, List<int[]> pois)
    {
        if (region2.getOrds().getX() < region.getOrds().getX())
            pois.add(new int[] { 0, CompConstLogic.SQUARES_PER_REGION/2});
        if (region2.getOrds().getX() > region.getOrds().getX())
            pois.add(new int[] { CompConstLogic.SQUARES_PER_REGION-1, CompConstLogic.SQUARES_PER_REGION/2});
        if (region2.getOrds().getY() < region.getOrds().getY())
            pois.add(new int[] { CompConstLogic.SQUARES_PER_REGION/2, 0 });
        if (region2.getOrds().getY() > region.getOrds().getY())
            pois.add(new int[] { CompConstLogic.SQUARES_PER_REGION/2, CompConstLogic.SQUARES_PER_REGION-1});
    }
    
    private static void generateRoad(SquareBean[][] squares, int x1, int y1, int x2, int y2, List<int[]> done)
    {
        AStarSearch srch = new AStarSearch();
        //srch.setListener(this);
        //srch.setListenFrequency(10000);
        SquareNode.init();
        SquareNode startNode = SquareNode.getInstance(squares, x1, y1);
        SquareNode goalNode = SquareNode.getInstance(squares, x2, y2);
        List<AStarNode> apath = srch.findPath(startNode, goalNode);
        SquareNode.term();
        if (apath == null)
            return;
        for (int i = -1; i < apath.size() - 1; i++)
        {
            SquareNode s1 = (i >= 0) ? (SquareNode)apath.get(i) : startNode;
            SquareNode s2 = (SquareNode)apath.get(i+1);
            if (s1.mX < s2.mX)
            {
                s1.mS.setRoadEast(SquareBean.T_ROAD);
                s2.mS.setRoadWest(SquareBean.T_ROAD);
            }
            else if (s1.mX > s2.mX)
            {
                s1.mS.setRoadWest(SquareBean.T_ROAD);
                s2.mS.setRoadEast(SquareBean.T_ROAD);
            }
            else if (s1.mY < s2.mY)
            {
                s1.mS.setRoadSouth(SquareBean.T_ROAD);
                s2.mS.setRoadNorth(SquareBean.T_ROAD);
            }
            else if (s1.mY > s2.mY)
            {
                s1.mS.setRoadNorth(SquareBean.T_ROAD);
                s2.mS.setRoadSouth(SquareBean.T_ROAD);
            }
            if (done != null)
                done.add(new int[] { s1.mX, s1.mY });
        }
        if (startNode.isBorder())
            startNode.addBorderRoad();
        if (goalNode.isBorder())
            startNode.addBorderRoad();
    }

    static void generateRegionSigns(RegionBean region)
    {
        for (int dx = 0; dx < CompConstLogic.SQUARES_PER_REGION; dx++)
            for (int dy = 0; dy < CompConstLogic.SQUARES_PER_REGION; dy++)
            {
                SquareGenBean sq = (SquareGenBean)region.getSquares()[dx][dy];
                if (!sq.isAnyRoads())
                    continue;
                if (sq.isCastle() || sq.isTown() || (sq.countRoads() >= 3))
                    generateSigns(region, sq);
            }
    }
    
    private static void generateSigns(RegionBean region, SquareGenBean sq)
    {
        if (sq.isRoadNorth())
            followRoad(region, sq, CompOperationBean.NORTH);
        if (sq.isRoadSouth())
            followRoad(region, sq, CompOperationBean.SOUTH);
        if (sq.isRoadEast())
            followRoad(region, sq, CompOperationBean.EAST);
        if (sq.isRoadWest())
            followRoad(region, sq, CompOperationBean.WEST);
    }
    
    private static void followRoad(RegionBean region, SquareGenBean sq, int initialDir)
    {
        int dx = sq.getOrds().getX() - region.getOrds().getX();
        int dy = sq.getOrds().getY() - region.getOrds().getY();
        int directionOfTravel = initialDir;
        int dist = 0;
        for (;;)
        {
            // move in direction, check if we fall off map
            switch (directionOfTravel)
            {
                case CompOperationBean.NORTH:
                    dy--;
                    if (dy < 0)
                        return;
                    break;
                case CompOperationBean.SOUTH:
                    dy++;
                    if (dy >= CompConstLogic.SQUARES_PER_REGION)
                        return;
                    break;
                case CompOperationBean.EAST:
                    dx++;
                    if (dx >= CompConstLogic.SQUARES_PER_REGION)
                        return;
                    break;
                case CompOperationBean.WEST:
                    dx--;
                    if (dx < 0)
                        return;
                    break;
            }
            dist++;
            // check if a destination
            SquareGenBean s = (SquareGenBean)region.getSquare(dx, dy);
            if (s.getFeature() != CompConstLogic.FEATURE_NONE)
            {
                CoordBean dest = new CoordBean(region.getOrds().getX() + dx, region.getOrds().getY() + dy, region.getOrds().getZ());
                sq.addSign(dest, dist, initialDir);
                return;
            }
            if (s.countRoads() != 2)
                return; // no turns
            if (!s.isRoad(directionOfTravel))
                switch (directionOfTravel)
                {
                    case CompOperationBean.NORTH:
                    case CompOperationBean.SOUTH:
                        if (s.isRoadEast())
                            directionOfTravel = CompOperationBean.EAST;
                        else
                            directionOfTravel = CompOperationBean.WEST;
                        break;
                    case CompOperationBean.EAST:
                    case CompOperationBean.WEST:
                        if (s.isRoadNorth())
                            directionOfTravel = CompOperationBean.NORTH;
                        else
                            directionOfTravel = CompOperationBean.SOUTH;
                        break;
                }
        }
    }
}
