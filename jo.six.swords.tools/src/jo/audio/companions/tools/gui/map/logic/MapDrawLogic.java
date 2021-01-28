package jo.audio.companions.tools.gui.map.logic;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.data.SquareHandBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.audio.companions.tools.gui.map.MapAssets;
import jo.audio.companions.tools.gui.map.MapData;
import jo.util.geom2d.Point2D;
import jo.util.geom2d.Spline2D;
import jo.util.utils.MathUtils;

public class MapDrawLogic
{
    public static MapAssets mAssets = new MapAssets();    
    static Graphics2D mG;
    static MapData mData;

    public static void doPaint(Graphics2D g, MapData data)
    {
        doPaint(g, data, mData.getSize(), mData.getDX(), mData.getDY());
    }

    public static void doPaint(Graphics2D g, MapData data, Dimension size, int dx, int dy)
    {
        mG = g;
        mData = data;
        int ox = ((int)size.getWidth())/2 + dx;
        int oy = ((int)size.getHeight())/2 + dy;
        int xRad = ((int)size.getWidth())/mData.getPixelScale()/2 + 1;
        int yRad = ((int)size.getHeight())/mData.getPixelScale()/2 + 1;
        int roadThick = Math.max(1,  mData.getPixelScale()/6);
        int roadOff = roadThick/2;
        BaseDrawLogic.paintBase(g, ox, oy, xRad, yRad);
        if (mData.isDrawRivers())
            RiverDrawLogic.paintRivers(g, ox, oy, xRad, yRad, roadThick, roadOff);
        if (mData.isDrawBorders() || mData.isDrawTownBorders())
            BorderDrawLogic.paintBorders(g, ox, oy, xRad, yRad, roadThick);
        if (mData.isCountryNames())
            NameDrawLogic.paintCountryNames(g, ox, oy, xRad, yRad);
        if (mData.isDrawRoads())
            RoadDrawLogic.paintRoads(g, ox, oy, xRad, yRad, roadThick, roadOff);
        IconDrawLogic.paintIcons(g, ox, oy, xRad, yRad);
        if (mData.isFeatureNames() || mData.isTownNames())
            NameDrawLogic.paintFeatureNames(g, ox, oy, xRad, yRad);
    }

    private static Map<Long, SquareBean> mCache = new HashMap<>();
    private static Map<Integer,Map<Long, SquareBean>> mMergeCache = new HashMap<>();
    
    public static  SquareBean getSquare(int x, int y)
    {
        if (x < 0)
            x = 0;
        else if (x > 2406)
            x = 2406;
        if (y < 0)
            y = 0;
        else if (y > 1604)
            y = 1604;
        long hash = makeHash(x, y);
        if (mCache.containsKey(hash))
            return mCache.get(hash);
        SquareBean sq = GenerationLogic.getSquare(new CoordBean(x, y, mData.getZ()));
        if (FeatureLogic.isStaticFeature(sq.getOrds(), null))
            sq.setFeature(CompConstLogic.FEATURE_STATIC);
        indexDemenses(sq);
        return sq;
    }

    public static long makeHash(int x, int y)
    {
        return (x + 32768)*65536L + (y + 32768);
    }
    
    public static  SquareBean getSquareD(int x, int y)
    {
        if (mData.getScale() >= 1)
        {
            x += MapDrawLogic.mData.getX();
            y += MapDrawLogic.mData.getY();
            return getSquare(x, y);
        }
        else
        {   // aggregate data
            int mergeSize = (-mData.getScale()) + 1;
            int ox = MapDrawLogic.mData.getX() + x*mergeSize;
            int oy = MapDrawLogic.mData.getY() + y*mergeSize;
            return getSquareMerged(ox, oy);
        }
    }

    private static SquareBean getSquareMerged(int ox, int oy)
    {
        int mergeSize = (-mData.getScale()) + 1;
        Map<Long, SquareBean> cache = mMergeCache.get(mergeSize);
        if (cache == null)
        {
            cache = new HashMap<>();
            mMergeCache.put(mergeSize, cache);
        }
        long hash = makeHash(ox, oy);
        if (cache.containsKey(hash))
            return cache.get(hash);
        SquareHandBean root = new SquareHandBean();
        Map<Integer, Integer> terrainDist = new HashMap<>();
        Map<DemenseBean, Integer> demenseDist = new HashMap<>();
        float altitude = 0;
        for (int dx = 0; dx < mergeSize; dx++)
            for (int dy = 0; dy < mergeSize; dy++)
            {
                SquareBean s = getSquare(ox + dx, oy + dy);
                incr(terrainDist, s.getTerrain());
                incr(demenseDist, s.getDemense());
                altitude += s.getAltitude();
            }
        root.setTerrain(getMostFrequent(terrainDist));
        root.setAltitude(altitude/(mergeSize*mergeSize));
        root.setDemense(getMostFrequent(demenseDist));
        cache.put(hash, root);
        return root;
    }
    
    public static  SquareBean getSquareDirD(SquareBean sq, int x, int y)
    {
        if (mData.getScale() < 0)
        {
            int mergeSize = (-mData.getScale()) + 1;
            x *= mergeSize;
            y *= mergeSize;
            x += sq.getOrds().getX();
            y += sq.getOrds().getY();
            return getSquareMerged(x, y);
        }
        else
        {
            x += sq.getOrds().getX();
            y += sq.getOrds().getY();
            return getSquare(x, y);
        }
    }
    
    public static SquareBean getSquareNorth(SquareBean sq)
    {
        return getSquareDirD(sq, 0, -1);
    }
    
    public static SquareBean getSquareSouth(SquareBean sq)
    {
        return getSquareDirD(sq, 0, 1);
    }
    
    public static SquareBean getSquareWest(SquareBean sq)
    {
        return getSquareDirD(sq, -1, 0);
    }
    
    public static SquareBean getSquareEast(SquareBean sq)
    {
        return getSquareDirD(sq, 1, 0);
    }

    private static <T> T getMostFrequent(Map<T, Integer> terrainDist)
    {
        T best = null;
        int bestValue = -1;
        for (T key : terrainDist.keySet())
        {
            int v = terrainDist.get(key);
            if ((best == null) || (v > bestValue))
            {
                best = key;
                bestValue = v;
            }
        }
        return best;
    }

    private static <T> void incr(Map<T, Integer> terrainDist, T terrain)
    {
        if (terrainDist.containsKey(terrain))
            terrainDist.put(terrain, terrainDist.get(terrain) + 1);
        else
            terrainDist.put(terrain, 1);
    }

    public static List<Path2D> groupSegments(List<Point2D> segs)
    {
        List<List<Point2D>> groups = makeGroups(segs);
        mergeGroups(groups);
        removeCollinear(groups);
        List<Path2D> paths = makePaths(groups);
        return paths;
    }

    private static void removeCollinear(List<List<Point2D>> groups)
    {
        for (List<Point2D> group : groups)
        {
            for (int i = 1; i < group.size() - 1; i++)
            {
                Point2D p = group.get(i);
                if ((p instanceof MapPoint2D) && ((MapPoint2D)p).isAnchor())
                    continue;
                if (isCollinear(group.get(i-1), p, group.get(i+1)))
                {
                    group.remove(i);
                    //i--;
                }
            }
        }
    }
    
    private static boolean isCollinear(Point2D p1, Point2D p2, Point2D p3)
    {
        if (MathUtils.equals(p1.x, p2.x) && MathUtils.equals(p2.x, p3.x))
            return true;
        if (MathUtils.equals(p1.y, p2.y) && MathUtils.equals(p2.y, p3.y))
            return true;
        return false;
    }

    private static List<Path2D> makePaths(List<List<Point2D>> groups)
    {
        List<Path2D> paths = new ArrayList<>();
        for (List<Point2D> group : groups)
        {
            Spline2D spline = new Spline2D(group);
            List<Point2D> splined = spline.getInterpolatedPath(4);
            Path2D.Double path = new Path2D.Double();
            Point2D p = splined.get(0);
            path.moveTo(p.x, p.y);
            for (int i = 1; i < splined.size(); i++)
            {
                p = splined.get(i);
                path.lineTo(p.x, p.y);
            }
            paths.add(path);
        }
        return paths;
    }

    private static void mergeGroups(List<List<Point2D>> groups)
    {
        for (;;)
        {
            boolean anyJoins = false;
            for (int i = 0; i < groups.size()-1; i++)
            {
                List<Point2D> g1 = groups.get(i);
                Point2D g1p1 = g1.get(0);
                Point2D g1p2 = g1.get(g1.size() - 1);
                for (int j = i + 1; j < groups.size(); j++)
                {
                    List<Point2D> g2 = groups.get(j);
                    Point2D g2p1 = g2.get(0);
                    Point2D g2p2 = g2.get(g2.size() - 1);
                    boolean didJoin = false;
                    if (g1p2.equals(g2p1))
                    {
                        for (int k = 1; k < g2.size(); k++)
                            g1.add(g2.get(k));
                        didJoin = true;
                    }
                    else if (g1p2.equals(g2p2))
                    {
                        for (int k = g2.size() - 2; k >= 0; k--)
                            g1.add(g2.get(k));
                        didJoin = true;
                    }
                    else if (g1p1.equals(g2p1))
                    {
                        for (int k = 1; k < g2.size(); k++)
                            g1.add(0, g2.get(k));
                        didJoin = true;
                    }
                    else if (g1p1.equals(g2p2))
                    {
                        for (int k = g2.size() - 2; k >= 0; k--)
                            g1.add(0, g2.get(k));
                        didJoin = true;
                    }
                    if (didJoin)
                    {
                        groups.remove(j);
                        anyJoins = true;
                        j--;
                        g1p1 = g1.get(0);
                        g1p2 = g1.get(g1.size() - 1);
                    }
                }
            }
            if (!anyJoins)
                break;
        }
    }

    private static List<List<Point2D>> makeGroups(List<Point2D> segs)
    {
        List<List<Point2D>> groups = new ArrayList<>();
        for (int i = 0; i < segs.size(); i += 2)
        {
            Point2D p1 = segs.get(i);
            Point2D p2 = segs.get(i+1);
            List<Point2D> group = new ArrayList<>();
            group.add(p1);
            group.add(p2);
            groups.add(group);
        }
        return groups;
    }

    public static boolean doWeDraw(SquareBean sq)
    {
        String demenseID = mData.getDrawOnlyDemense();
        if (demenseID == null)
            return true;
        for (DemenseBean d = sq.getDemense(); d != null; d = d.getLiege())
            if (d.getID().equals(demenseID))
                return true;
        return false;
    }

    private static Map<String, List<String>> mDemenseTree = new HashMap<>();
    private static Map<String, int[]> mDemenseBounds = new HashMap<>();
    
    private static void indexDemenses(SquareBean sq)
    {
        for (DemenseBean d = sq.getDemense(); d != null; d = d.getLiege())
        {
            String child = d.getID();
            String parent = d.getLiegeID();
            if (parent == null)
                parent = "";
            List<String> vassals = mDemenseTree.get(parent);
            if (vassals == null)
            {
                vassals = new ArrayList<>();
                mDemenseTree.put(parent, vassals);
            }
            if (!vassals.contains(child))
                vassals.add(child);
            int[] bounds = mDemenseBounds.get(child);
            if (bounds == null)
            {
                bounds = MathUtils.getEmptyBounds();
                mDemenseBounds.put(child, bounds);
            }
            MathUtils.extendBounds(bounds, sq.getOrds().getX(), sq.getOrds().getY());
        }
    }
    
    public static List<String> getVassals(String parent)
    {
        return mDemenseTree.get(parent);
    }
    
    public static int[] getBounds(String parent)
    {
        return mDemenseBounds.get(parent);
    }
}
