package jo.audio.companions.tools.gui.map.logic;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.tools.gui.map.MapAssets;
import jo.util.geom2d.Point2D;

public class RoadDrawLogic
{

    public static void paintRoads(Graphics2D g, int ox, int oy, int xRad, int yRad,
            int roadThick, int roadOff)
    {
        int scale = MapDrawLogic.mData.getScale();
        Map<Integer,List<Point2D>> roadSegments = new HashMap<>();
        if (scale >= 1)
        {
            roadSegments.put(SquareBean.T_BRIDGE, new ArrayList<Point2D>());
            roadSegments.put(SquareBean.T_HIGHWAY, new ArrayList<Point2D>());
        }
        if (scale >= 2)
            roadSegments.put(SquareBean.T_ROAD, new ArrayList<Point2D>());
        if (scale >= 4)
            roadSegments.put(SquareBean.T_TRACK, new ArrayList<Point2D>());
        for (int x = -xRad; x <= xRad; x++)
            for (int y = -yRad; y <= yRad; y++)
            {
                SquareBean sq = MapDrawLogic.getSquareD(x, y);
                if (!MapDrawLogic.doWeDraw(sq))
                    continue;
                if (CompConstLogic.isWater(sq.getTerrain()))
                    continue;
                int px = x*MapDrawLogic.mData.getPixelScale() + ox;
                int py = y*MapDrawLogic.mData.getPixelScale() + oy;
                calcRoadSegments(sq, px, py, roadSegments);
            }
        for (Integer rt : roadSegments.keySet())
        {
            setRoadColor(g, rt, roadThick);
            List<Point2D> segs = roadSegments.get(rt);
            List<Path2D> groups = MapDrawLogic.groupSegments(segs);
            for (Path2D group : groups)
            {
                g.draw(group);
            }
        }
    }

    private static void calcRoadSegments(SquareBean sq, int px, int py, Map<Integer,List<Point2D>> roadSegments)
    {
        int h = MapDrawLogic.mData.getPixelScale();
        int h2 = h/2;
        MapPoint2D c = new MapPoint2D(px+h2, py+h2);
        int linkCount = 0;
        linkCount = calcRoadSegment(sq.isRoadNorth(), sq.getRoadNorth(), roadSegments, c, new Point2D(px+h2, py), linkCount);
        linkCount = calcRoadSegment(sq.isRoadSouth(), sq.getRoadSouth(), roadSegments, c, new Point2D(px+h2, py+h), linkCount);
        linkCount = calcRoadSegment(sq.isRoadEast(), sq.getRoadEast(), roadSegments, c, new Point2D(px+h, py+h2), linkCount);
        linkCount = calcRoadSegment(sq.isRoadWest(), sq.getRoadWest(), roadSegments, c, new Point2D(px, py+h2), linkCount);
        c.setAnchor(linkCount > 2);
    }
    
    private static int calcRoadSegment(boolean isRoad, int road, Map<Integer,List<Point2D>> roadSegments, Point2D c, Point2D rad, int linkCount)
    {
        if (isRoad)
        {
            List<Point2D> segs = roadSegments.get(road);
            if (segs != null)
            {
                segs.add(rad);
                segs.add(c);
                linkCount++;
            }
        }
        return linkCount;
    }

    private static void setRoadColor(Graphics2D g, int road, int thick)
    {
        if (road == SquareBean.T_TRACK)
            g.setColor(MapAssets.ROAD1);
        else if (road == SquareBean.T_ROAD)
            g.setColor(MapAssets.ROAD2);
        else if (road == SquareBean.T_HIGHWAY)
            g.setColor(MapAssets.ROAD3);
        Stroke s = new BasicStroke(thick, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(s);
    }

}
