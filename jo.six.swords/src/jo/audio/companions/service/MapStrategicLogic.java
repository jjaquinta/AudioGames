package jo.audio.companions.service;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.LocationBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.GenerationLogic;

public class MapStrategicLogic
{
    public static void drawMapStrategic(BufferedImage img, LocationBean ords, int scale)
    {
        int width = img.getWidth();
        int height = img.getHeight();
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int ox = ((int)width)/2 + 0;
        int oy = ((int)height)/2 + 0;
        int xRad = ((int)width)/scale/2 + 1;
        int yRad = ((int)height)/scale/2 + 1;
        int roadThick = Math.max(1,  scale/6);
        int roadOff = roadThick/2;
        for (int x = -xRad; x <= xRad; x++)
            for (int y = -yRad; y <= yRad; y++)
            {
                SquareBean sq = getSquare(ords.getX() + x, ords.getY() + y, ords.getZ());
                int px = x*scale + ox;
                int py = y*scale + oy;
                Color c = MapLogic.mAssets.getColor(sq, false);
                g.setColor(c);
                g.fillRect(px, py, scale, scale);
                paintRivers(g, scale, roadThick, roadOff, sq, px, py);
                paintRoads(g, scale, roadThick, roadOff, sq, px, py);
                if (scale >= 24)
                {
                    BufferedImage tile = MapLogic.mAssets.getImage(sq);
                    g.drawImage(tile, px, py, px+scale, py+scale, 0, 0, 48, 48, null, null);
                }
            }
        g.setColor(Color.red);
        for (int i = 1; i <= scale/24; i++)
            g.drawRect(ox-i, oy-i, scale+i*2, scale+i*2);
        g.dispose();
    }

    private static void paintRoads(Graphics g, int scale, int roadThick, int roadOff,
            SquareBean sq, int px, int py)
    {
        if (sq.isAnyRoads())
        {
            g.setColor(MapAssets.ROAD1);
            g.fillOval(px+scale/2-roadOff, py+scale/2-roadOff, roadThick, roadThick);
        }
        if (sq.isRoadNorth())
        {
            setRoadColor(g, sq.getRoadNorth());
            g.fillRect(px+scale/2-roadOff, py+0, roadThick, scale/2);
        }
        if (sq.isRoadSouth())
        {
            setRoadColor(g, sq.getRoadSouth());
            g.fillRect(px+scale/2-roadOff, py+scale/2, roadThick, scale/2);
        }
        if (sq.isRoadEast())
        {
            setRoadColor(g, sq.getRoadEast());
            g.fillRect(px+scale/2, py+scale/2-roadOff, scale/2, roadThick);
        }
        if (sq.isRoadWest())
        {
            setRoadColor(g, sq.getRoadWest());
            g.fillRect(px+0, py+scale/2-roadOff, scale/2, roadThick);
        }
    }

    private static void paintRivers(Graphics g, int scale, int riverThick, int riverOff,
            SquareBean sq, int px, int py)
    {
        if (sq.isAnyRivers())
        {
            g.setColor(MapAssets.ROAD1);
            g.fillOval(px+scale/2-riverOff, py+scale/2-riverOff, riverThick, riverThick);
        }
        if (sq.isRiverNorth())
        {
            setRiverColor(g, sq.getRiverNorth());
            g.fillRect(px+scale/2-riverOff, py+0, riverThick, scale/2);
        }
        if (sq.isRiverSouth())
        {
            setRiverColor(g, sq.getRiverSouth());
            g.fillRect(px+scale/2-riverOff, py+scale/2, riverThick, scale/2);
        }
        if (sq.isRiverEast())
        {
            setRiverColor(g, sq.getRiverEast());
            g.fillRect(px+scale/2, py+scale/2-riverOff, scale/2, riverThick);
        }
        if (sq.isRiverWest())
        {
            setRiverColor(g, sq.getRiverWest());
            g.fillRect(px+0, py+scale/2-riverOff, scale/2, riverThick);
        }
    }

    private static void setRoadColor(Graphics g, int road)
    {
        if (road == SquareBean.T_TRACK)
            g.setColor(MapAssets.ROAD1);
        else if (road == SquareBean.T_ROAD)
            g.setColor(MapAssets.ROAD2);
        else if (road == SquareBean.T_HIGHWAY)
            g.setColor(MapAssets.ROAD3);
    }

    private static void setRiverColor(Graphics g, int river)
    {
        if (river == SquareBean.R_BROOK)
            g.setColor(MapAssets.RIVER1);
        else if (river == SquareBean.R_STREAM)
            g.setColor(MapAssets.RIVER2);
        else if (river == SquareBean.R_RIVER)
            g.setColor(MapAssets.RIVER3);
    }

    
    public static SquareBean getSquare(int x, int y, int z)
    {
        SquareBean sq = GenerationLogic.getSquare(new CoordBean(x, y, z));
        if (FeatureLogic.isStaticFeature(sq.getOrds(), null))
            sq.setFeature(CompConstLogic.FEATURE_STATIC);
        return sq;
    }

}
