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

public class RiverDrawLogic
{

    public static void paintRivers(Graphics2D g, int ox, int oy, int xRad, int yRad,
            int roadThick, int roadOff)
    {
        int scale = MapDrawLogic.mData.getScale();
        int pscale = MapDrawLogic.mData.getPixelScale();
        Map<Integer,List<Point2D>> riverSegments = new HashMap<>();
        if (scale >= 1)
            riverSegments.put(SquareBean.R_RIVER, new ArrayList<Point2D>());
        if (scale >= 2)
            riverSegments.put(SquareBean.R_STREAM, new ArrayList<Point2D>());
        if (scale >= 4)
            riverSegments.put(SquareBean.R_BROOK, new ArrayList<Point2D>());
        for (int x = -xRad; x <= xRad; x++)
            for (int y = -yRad; y <= yRad; y++)
            {
                SquareBean sq = MapDrawLogic.getSquareD(x, y);
                if (CompConstLogic.isWater(sq.getTerrain()))
                    continue;
                int px = x*pscale + ox;
                int py = y*pscale + oy;
                calcRiverSegments(sq, px, py, riverSegments);
            }
        for (Integer rt : riverSegments.keySet())
        {
            setRiverColor(g, rt, roadThick);
            List<Point2D> segs = riverSegments.get(rt);
            List<Path2D> groups = MapDrawLogic.groupSegments(segs);
            for (Path2D group : groups)
            {
                g.draw(group);
            }
        }
    }

    private static void calcRiverSegments(SquareBean sq, int px, int py, Map<Integer,List<Point2D>> riverSegments)
    {
        int h = MapDrawLogic.mData.getPixelScale();
        int h2 = h/2;
        MapPoint2D c = new MapPoint2D(px+h2, py+h2);
        int linkCount = 0;
        linkCount = calcRiverSegment(sq.isRiverNorth(), sq.getRiverNorth(), riverSegments, c, new Point2D(px+h2, py), linkCount);
        linkCount = calcRiverSegment(sq.isRiverSouth(), sq.getRiverSouth(), riverSegments, c, new Point2D(px+h2, py+h), linkCount);
        linkCount = calcRiverSegment(sq.isRiverEast(), sq.getRiverEast(), riverSegments, c, new Point2D(px+h, py+h2), linkCount);
        linkCount = calcRiverSegment(sq.isRiverWest(), sq.getRiverWest(), riverSegments, c, new Point2D(px, py+h2), linkCount);
        if (linkCount > 2)
            c.setAnchor(true);
    }
    
    private static int calcRiverSegment(boolean isRiver, int river, Map<Integer,List<Point2D>> riverSegments, Point2D c, Point2D rad, int linkCount)
    {
        if (isRiver)
        {
            List<Point2D> segs = riverSegments.get(river);
            if (segs != null)
            {
                segs.add(rad);
                segs.add(c);
                linkCount++;
            }
        }
        return linkCount;
    }

    private static void setRiverColor(Graphics2D g, int river, int thick)
    {
        if (river == SquareBean.R_BROOK)
            g.setColor(MapAssets.RIVER1);
        else if (river == SquareBean.R_STREAM)
            g.setColor(MapAssets.RIVER2);
        else if (river == SquareBean.R_RIVER)
            g.setColor(MapAssets.RIVER3);
        Stroke s = new BasicStroke(thick, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(s);
    }

}

