package jo.audio.companions.tools.gui.map.logic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.tools.gui.map.MapAssets;

public class IconDrawLogic
{

    public static void paintIcons(Graphics2D g, int ox, int oy, int xRad, int yRad)
    {
        int scale = MapDrawLogic.mData.getPixelScale();
        Composite defaultComposite = g.getComposite();
        int mult = 0;
        if (scale < 24)
            mult = 24/scale + 1;
        for (int x = -xRad; x <= xRad; x++)
            for (int y = -yRad; y <= yRad; y++)
            {
                SquareBean sq = MapDrawLogic.getSquareD(x, y);
                if (!MapDrawLogic.doWeDraw(sq))
                    continue;
                int px = x*scale + ox;
                int py = y*scale + oy;
                if (scale < 24)
                {
                    if ((x%mult == 0) && (y%mult == 0))
                    {
                        int terrain = sq.getTerrain();
                        if (sq.getFeature() != CompConstLogic.FEATURE_NONE)
                            terrain = -1;
                        else
                            for (int dx = 0; dx < mult; dx++)
                                for (int dy = 0; dy < mult; dy++)
                                    if ((dx != 0) && (dy != 0))
                                    {
                                        SquareBean sq2 = MapDrawLogic.getSquareD(x+dx, y+dy);
                                        if (IconDrawLogic.isCivilized(sq2)
                                                || (sq2.getTerrain() != terrain))
                                        {
                                            terrain = -1;
                                            dx = mult;
                                            break;
                                        }
                                    }
                        if (terrain != -1)
                            drawImage(g, scale*mult, defaultComposite, sq, px, py);
                    }
                    drawOutline(g, scale, sq, px, py);
                }
                else
                    drawImage(g, scale, defaultComposite, sq, px, py);
            }
    }

    private static boolean isCivilized(SquareBean sq)
    {
        return CompConstLogic.isCastle(sq.getFeature())
                || CompConstLogic.isTown(sq.getFeature());
    }
    
    private static void drawImage(Graphics2D g, int scale,
            Composite defaultComposite, SquareBean sq, int px, int py)
    {
        if (isFadedIcon(sq))
            g.setComposite(MapAssets.mTerrainComposite);
        BufferedImage img = MapDrawLogic.mAssets.getImage(sq);
        g.drawImage(img, px, py, px+scale, py+scale, 0, 0, 48, 48, null, null);
        g.setComposite(defaultComposite);
    }

    private static final int RECT = 0;
    private static final int CIRC = 1;
    //private static final int DIAM = 2;
    //private static final int TRI = 3;
    
    private static void drawOutline(Graphics2D g, int scale, SquareBean sq,
            int px, int py)
    {
        Color c = null;
        int thick = 1;
        int inset = 0;
        int shape = RECT;
        switch (sq.getFeature())
        {
            case CompConstLogic.FEATURE_CASTLE:
                c = Color.RED;
                thick = 3;
                inset = 0;
                shape = RECT;
                break;
            case CompConstLogic.FEATURE_FORT:
                c = Color.RED;
                thick = 2;
                inset = 1;
                shape = RECT;
                break;
            case CompConstLogic.FEATURE_OUTPOST:
                c = Color.RED;
                thick = 1;
                inset = 2;
                shape = RECT;
                break;
            case CompConstLogic.FEATURE_CITY:
                c = Color.BLACK;
                thick = 4;
                inset = 0;
                shape = CIRC;
                break;
            case CompConstLogic.FEATURE_TOWN:
                c = Color.BLACK;
                thick = 3;
                inset = 1;
                shape = CIRC;
                break;
            case CompConstLogic.FEATURE_VILLAGE:
                c = Color.BLACK;
                thick = 2;
                inset = 2;
                shape = CIRC;
                break;
            case CompConstLogic.FEATURE_HAMLET:
                c = Color.BLACK;
                thick = 1;
                inset = 3;
                shape = CIRC;
                break;
        }
        if (c != null)
        {
            if (inset > scale/2)
                inset = scale/2;
            Stroke s = new BasicStroke(thick);
            g.setColor(c);
            g.setStroke(s);
            Shape sh = null;
            if (shape == RECT)
                sh = new Rectangle(px+inset, py+inset, scale-inset*2, scale-inset*2);
            else if (shape == CIRC)
                sh = new Ellipse2D.Double(px+inset, py+inset, scale-inset*2, scale-inset*2);
            if (sh != null)
                g.draw(sh);
        }
    }

    private static boolean isFadedIcon(SquareBean sq)
    {
        if (MapDrawLogic.mData.isNoTerrainIcons() && (sq.getFeature() == CompConstLogic.FEATURE_NONE))
            return true;
        if (MapDrawLogic.mData.isNoRuinIcons() && CompConstLogic.isRuin(sq.getFeature()))
            return true;
        return false;
    }

}
