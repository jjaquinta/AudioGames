package jo.audio.companions.tools.gui.map.logic;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import jo.audio.companions.data.SquareBean;
import jo.audio.companions.tools.gui.map.MapData;

public class BaseDrawLogic
{

    public static void paintBase(Graphics2D g, int ox, int oy, int xRad, int yRad)
    {
        MapData data = MapDrawLogic.mData;
        int h = data.getPixelScale();
        int h2 = h/2;
        for (int x = -xRad; x <= xRad; x++)
            for (int y = -yRad; y <= yRad; y++)
            {
                SquareBean sq = MapDrawLogic.getSquareD(x, y);
                Color sqColor = MapDrawLogic.mAssets.getColor(sq, false);
                int px = x*h + ox;
                int py = y*h + oy;
                SquareBean northWest = MapDrawLogic.getSquareD(x - 1, y - 1);
                if (h >= 12)
                {
                    SquareBean north = MapDrawLogic.getSquareD(x, y - 1);
                    SquareBean south = MapDrawLogic.getSquareD(x, y + 1);
                    SquareBean east = MapDrawLogic.getSquareD(x + 1, y);
                    SquareBean west = MapDrawLogic.getSquareD(x - 1, y);
                    SquareBean northEast = MapDrawLogic.getSquareD(x + 1, y - 1);
                    SquareBean southEast = MapDrawLogic.getSquareD(x + 1, y + 1);
                    SquareBean southWest = MapDrawLogic.getSquareD(x - 1, y + 1);
                    Color northColor = MapDrawLogic.mAssets.getColor(north, false);
                    Color southColor = MapDrawLogic.mAssets.getColor(south, false);
                    Color eastColor = MapDrawLogic.mAssets.getColor(east, false);
                    Color westColor = MapDrawLogic.mAssets.getColor(west, false);
                    Color northEastColor = MapDrawLogic.mAssets.getColor(northEast, false);
                    Color southEastColor = MapDrawLogic.mAssets.getColor(southEast, false);
                    Color northWestColor = MapDrawLogic.mAssets.getColor(northWest, false);
                    Color southWestColor = MapDrawLogic.mAssets.getColor(southWest, false);
                    Path2D.Double path = new Path2D.Double();
                    path.moveTo(px+h2, py+0);
                    addPath(g, path, sqColor, northColor, northEastColor, eastColor, px+h2, py+0, px+h, py+0, px+h, py+h2, 0);
                    addPath(g, path, sqColor, eastColor, southEastColor, southColor, px+h, py+h2, px+h, py+h, px+h2, py+h, 1);
                    addPath(g, path, sqColor, southColor, southWestColor, westColor, px+h2, py+h, px+0, py+h, px+0, py+h2, 2);
                    addPath(g, path, sqColor, westColor, northWestColor, northColor, px+0, py+h2, px+0, py+0, px+h2, py+0, 3);
                    g.setColor(sqColor);
                    g.fill(path);
                }
                else
                {
                    g.setColor(sqColor);
                    g.fillRect(px,  py, h, h);
                }
                float slope = northWest.getAltitude() - sq.getAltitude();
                if (slope > 100)
                {
                    int alpha = (int)(slope/100);
                    if (alpha > 128)
                        alpha = 128;
                    AlphaComposite ac = MapDrawLogic.mAssets.getAlpha(alpha);
                    if (ac != null)
                    {
                        Composite old = g.getComposite();
                        g.setComposite(ac);
                        g.setColor(Color.BLACK);
                        g.fillRect(px,  py, h, h);
                        g.setComposite(old);
                    }
                }
            }
    }

    private static void addPath(Graphics2D g, Path2D path, Color sqColor, Color northColor, Color neColor, Color eastColor, 
            int x0, int y0, int x1, int y1, int x2, int y2, int dir)
    {
        if ((sqColor != northColor) && (northColor == neColor) && (neColor == eastColor))
        {
            addPathRound(path, x1, y1, x2, y2);
            fillPathCorner(g, northColor, x0, y0, x1, y1, x2, y2);
        }
        else if ((sqColor != northColor) && (northColor == eastColor) && (sqColor == neColor))
        {
            if (dir%2 == 0)
            {
                addPathRound(path, x1, y1, x2, y2);
                fillPathCorner(g, northColor, x0, y0, x1, y1, x2, y2);
            }
            else
            {
                addPathSquare(path, x1, y1, x2, y2);
            }
        }
        else if ((sqColor != northColor) && (northColor == eastColor))
        {
            addPathRound(path, x1, y1, x2, y2);
            fillPathCorner(g, northColor, x0, y0, x1, y1, x2, y2);
        }
        else
        {
            addPathSquare(path, x1, y1, x2, y2);
        }
        /*
        if ((sqColor == northColor) || (sqColor == eastColor) || (sqColor != neColor))
        {
            addPathSquare(path, x1, y1, x2, y2);
        }
        else
        {
            addPathRound(path, x1, y1, x2, y2);
            if (northColor == eastColor)
            {
                fillPathCorner(g, (dir%2 == 0) ? northColor : sqColor, x0, y0, x1, y1, x2, y2);
            }
            else
            {
                int x3 = (x1 + x0)/2;
                int y3 = (y2 + y1)/2;
                fillPathUpperCorner(g, northColor, x0, y0, x1, y1, x2, y2);
                fillPathLowerCorner(g, eastColor, x1, y1, x2, y2, x3, y3);
            }
        }
        */
    }

    private static void addPathRound(Path2D path, int x1, int y1, int x2, int y2)
    {
        path.quadTo(x1, y1, x2, y2);
    }

    private static void addPathSquare(Path2D path, int x1, int y1, int x2,
            int y2)
    {
        path.lineTo(x1, y1);
        path.lineTo(x2, y2);
    }

    private static void fillPathCorner(Graphics2D g, Color color, int x0, int y0, int x1, int y1, int x2, int y2)
    {
        Path2D corner = new Path2D.Double();
        corner.moveTo(x0, y0);
        addPathSquare(corner, x1, y1, x2, y2);
        addPathRound(corner, x1, y1, x0, y0);
        g.setColor(color);
        g.fill(corner);
    }
    
    /*
    private static void fillPathUpperCorner(Graphics2D g, Color color, int x0, int y0, int x1, int y1, int x3, int y3)
    {
        Path2D ncorner = new Path2D.Double();
        ncorner.moveTo(x0, y0);
        addPathSquare(ncorner, x1, y1, x3, y3);
        ncorner.lineTo(x0, y0);
        g.setColor(color);
        g.fill(ncorner);
    }
    
    private static void fillPathLowerCorner(Graphics2D g, Color color, int x1, int y1, int x2, int y2, int x3, int y3)
    {
        Path2D ecorner = new Path2D.Double();
        ecorner.moveTo(x2, y2);
        addPathSquare(ecorner, x1, y1, x3, y3);
        ecorner.lineTo(x2, y2);
        g.setColor(color);
        g.fill(ecorner);
    }
    */
}
