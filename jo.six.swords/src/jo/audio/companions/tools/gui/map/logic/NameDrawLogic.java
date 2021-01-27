package jo.audio.companions.tools.gui.map.logic;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.audio.companions.tools.gui.map.MapAssets;
import jo.audio.companions.tools.gui.map.MapData;

public class NameDrawLogic
{

    public static void paintCountryNames(Graphics2D g, int ox, int oy, int xRad,
            int yRad)
    {
        g.setColor(MapAssets.BORDER1);
        Map<String,CountryStats> cs = new HashMap<>();
        MapData data = MapDrawLogic.mData;
        int h = data.getPixelScale();
        for (int x = -xRad; x <= xRad; x++)
            for (int y = -yRad; y <= yRad; y++)
            {
                SquareBean sq = MapDrawLogic.getSquareD(x, y);
                if (!MapDrawLogic.doWeDraw(sq))
                    continue;
                String country = getCountry(sq);
                if (country == null)
                    continue;
                CountryStats stats = cs.get(country);
                if (stats == null)
                {
                    stats = new CountryStats();
                    stats.id = country;
                    cs.put(country, stats);
                }
                if (stats.tot == 0)
                {
                    stats.minx = x;
                    stats.miny = y;
                    stats.maxx = x;
                    stats.maxy = y;
                }
                else
                {
                    stats.minx = Math.min(stats.minx, x);
                    stats.miny = Math.min(stats.miny, y);
                    stats.maxx = Math.max(stats.maxx, x);
                    stats.maxy = Math.max(stats.maxy, y);
                }
                stats.totx += x;
                stats.toty += y;
                stats.tot++;
            }
        for (CountryStats stats : cs.values())
        {
            int x = stats.totx/stats.tot;
            int y = stats.toty/stats.tot;
            int px = x*h + ox;
            int py = y*h + oy;
            drawName(g, px, py, stats.id, h*4);
        }
    }
    
    private static final Stroke DASHED = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);    
    
    public static void placeName(Graphics2D g, Rectangle ref, String name, int pointSize, List<Rectangle> obscured)
    {
        Rectangle2D bounds = findFont(g, name, pointSize);
        Rectangle place = findPlace(ref, (int)bounds.getWidth(), (int)bounds.getHeight(), obscured);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(name, place.x, place.y + fm.getAscent());
        g.setStroke(DASHED);
        g.drawLine(ref.x + ref.width/2, ref.y + ref.height/2, 
                place.x + place.width/2, place.y + place.height/2);
        //g.draw(ref);
        //g.draw(place);
        obscured.add(place);
    }
    
    private static Placer[] PLACERS = {
            new Placer() { // above
                @Override
                public Rectangle calcPlace(Rectangle r, int width, int height)
                {
                    return new Rectangle(r.x + r.width/2 - width/2, r.y, width, height);
                }
            },
            new Placer() { // below
                @Override
                public Rectangle calcPlace(Rectangle r, int width, int height)
                {
                    return new Rectangle(r.x + r.width/2 - width/2, r.y + r.height, width, height);
                }
            },
            new Placer() { // left
                @Override
                public Rectangle calcPlace(Rectangle r, int width, int height)
                {
                    return new Rectangle(r.x - width, r.y + r.height/2 - height/2, width, height);
                }
            },
            new Placer() { // right
                @Override
                public Rectangle calcPlace(Rectangle r, int width, int height)
                {
                    return new Rectangle(r.x + r.width, r.y + r.height/2 - height/2, width, height);
                }
            },
            new Placer() { // upper left
                @Override
                public Rectangle calcPlace(Rectangle r, int width, int height)
                {
                    return new Rectangle(r.x - width, r.y - height, width, height);
                }
            },
            new Placer() { // upper right
                @Override
                public Rectangle calcPlace(Rectangle r, int width, int height)
                {
                    return new Rectangle(r.x - r.width, r.y - height, width, height);
                }
            },
    };
    
    private static Rectangle findPlace(Rectangle ref, int width, int height, List<Rectangle> obscured)
    {
        Rectangle fallback = null;
        for (Placer p : PLACERS)
        {
            Rectangle place = p.calcPlace(ref, width, height);
            if (isFit(place, obscured))
                return place;
            if (fallback == null)
                fallback = place;
        }
        return fallback;
    }
    
    private static boolean isFit(Rectangle r, List<Rectangle> obscured)
    {
        for (Rectangle o : obscured)
            if (o.intersects(r))
                return false;
        return true;
    }
    
    public static void drawName(Graphics2D g, int x, int y, String name, int pointSize)
    {
        Rectangle2D r = findFont(g, name, pointSize);
        g.drawString(name, (float)(x - r.getWidth()/2), y);
    }
    
    public static void drawNameVertical(Graphics2D g, int x, int y, String name, int pointSize)
    {
        Rectangle2D r = findFont(g, name, pointSize);
        //g.drawString(name, (float)(x - r.getWidth()/2), y);
        g.translate((float)x,(float)y);
        g.rotate(Math.toRadians(90));
        g.drawString(name,-(int)(r.getWidth()/2),0);
        g.rotate(-Math.toRadians(90));
        g.translate(-(float)x,-(float)y);    
    }

    public static Rectangle2D findFont(Graphics2D g, String name, int pointSize)
    {
        float fontSize = pointSize;
        FontMetrics fm = null;
        for (int i = 0; i < 3; i++)
        {
            Font f = g.getFont().deriveFont(fontSize);
            g.setFont(f);
            fm = g.getFontMetrics();
            double height = fm.getStringBounds(name, g).getHeight();
            int delta = (int)(pointSize - height);
            if (delta < 0)
                fontSize *= (float)pointSize/(float)height;
            else if (delta > 0)
                fontSize *= (float)height/(float)pointSize;
            else
                break;
        }
        Rectangle2D r = fm.getStringBounds(name, g);
        return r;
    }
    
    private static String getCountry(SquareBean sq)
    {
        for (DemenseBean d = sq.getDemense(); d != null; d = d.getLiege())
            if (BorderDrawLogic.toType(d.getID()) == BorderDrawLogic.B_COUNTRY)
                return d.getID();
        return null;
    }

    public static void paintFeatureNames(Graphics2D g, int ox, int oy, int xRad,
            int yRad)
    {
        MapData data = MapDrawLogic.mData;
        int h = data.getPixelScale();
        Map<SquareBean,Rectangle> refIndex = new HashMap<>();
        Map<SquareBean,String> nameIndex = new HashMap<>();
        List<Rectangle> obscured = new ArrayList<>();
        for (int x = -xRad; x <= xRad; x++)
            for (int y = -yRad; y <= yRad; y++)
            {
                SquareBean sq = MapDrawLogic.getSquareD(x, y);
                if (!MapDrawLogic.doWeDraw(sq))
                    continue;
                if (!data.isTownNames() && CompConstLogic.isTown(sq.getFeature()))
                    continue;
                if (!data.isFeatureNames() && CompConstLogic.isCastle(sq.getFeature()))
                    continue;
                String name = getSquareName(sq);
                if (name != null)
                {
                    int px = x*h + ox;
                    int py = y*h + oy;
                    Rectangle r = new Rectangle(px, py, h, h);
                    obscured.add(r);
                    refIndex.put(sq, r);
                    nameIndex.put(sq, name);
                }
            }
        for (int x = -xRad; x <= xRad; x++)
            for (int y = -yRad; y <= yRad; y++)
            {
                SquareBean sq = MapDrawLogic.getSquareD(x, y);
                if (!refIndex.containsKey(sq))
                    continue;
                String name = nameIndex.get(sq);
                Rectangle ref = refIndex.get(sq);
                int size = getSquareSize(sq, h);
                setSquareColor(g, sq);
                placeName(g, ref, name, size, obscured);
            }
    }

    private static void setSquareColor(Graphics2D g, SquareBean sq)
    {
        if (sq.getFeature() == CompConstLogic.FEATURE_HAMLET)
        {
            g.setColor(MapAssets.BORDER5);
        }
        else if (sq.getFeature() == CompConstLogic.FEATURE_VILLAGE)
        {
            g.setColor(MapAssets.BORDER4);
        }
        else if (sq.getFeature() == CompConstLogic.FEATURE_TOWN)
        {
            g.setColor(MapAssets.BORDER3);
        }
        else if (sq.getFeature() == CompConstLogic.FEATURE_CITY)
        {
            g.setColor(MapAssets.BORDER2);
        }
        else if (sq.getFeature() == CompConstLogic.FEATURE_CASTLE)
        {
            g.setColor(MapAssets.BORDER3);
        }
        else if (sq.getFeature() == CompConstLogic.FEATURE_FORT)
        {
            g.setColor(MapAssets.BORDER4);
        }
        else if (sq.getFeature() == CompConstLogic.FEATURE_OUTPOST)
        {
            g.setColor(MapAssets.BORDER5);
        }
    }
    
    private static int getSquareSize(SquareBean sq, int size)
    {
        if (sq.getFeature() == CompConstLogic.FEATURE_HAMLET)
            return size;
        else if (sq.getFeature() == CompConstLogic.FEATURE_VILLAGE)
            return size * 2;
        else if (sq.getFeature() == CompConstLogic.FEATURE_TOWN)
            return size * 3;
        else if (sq.getFeature() == CompConstLogic.FEATURE_CITY)
            return size * 4;
        else if (sq.getFeature() == CompConstLogic.FEATURE_CASTLE)
            return size * 3;
        else if (sq.getFeature() == CompConstLogic.FEATURE_FORT)
            return size * 2;
        else if (sq.getFeature() == CompConstLogic.FEATURE_OUTPOST)
            return size * 1;
        return size;
    }

    private static String getSquareName(SquareBean sq)
    {
        if (sq.getFeature() == CompConstLogic.FEATURE_HAMLET)
            return BorderDrawLogic.findName(sq, BorderDrawLogic.B_HAMLET);
        else if (sq.getFeature() == CompConstLogic.FEATURE_VILLAGE)
            return BorderDrawLogic.findName(sq, BorderDrawLogic.B_VILLAGE);
        else if (sq.getFeature() == CompConstLogic.FEATURE_TOWN)
            return BorderDrawLogic.findName(sq, BorderDrawLogic.B_TOWN);
        else if (sq.getFeature() == CompConstLogic.FEATURE_CITY)
            return BorderDrawLogic.findName(sq, BorderDrawLogic.B_CITY);
        else if (sq.getFeature() == CompConstLogic.FEATURE_CASTLE)
            return getFeatureName(sq);
        else if (sq.getFeature() == CompConstLogic.FEATURE_FORT)
            return getFeatureName(sq);
        else if (sq.getFeature() == CompConstLogic.FEATURE_OUTPOST)
            return getFeatureName(sq);
        return null;
    }
    
    private static String getFeatureName(SquareBean sq)
    {
        RegionBean region = GenerationLogic.getRegion(sq.getOrds());
        FeatureBean feature = FeatureLogic.getFeature(region, sq, null);
        String name = MapDrawLogic.mAssets.expandInserts(feature.getName());
        return name;
    }
}

class CountryStats
{
    String id;
    int minx;
    int maxx;
    int miny;
    int maxy;
    int totx;
    int toty;
    int tot;
}

interface Placer
{
    public Rectangle calcPlace(Rectangle ref, int width, int height);
}