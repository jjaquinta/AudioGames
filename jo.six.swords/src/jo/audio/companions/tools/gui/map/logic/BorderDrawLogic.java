package jo.audio.companions.tools.gui.map.logic;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.tools.gui.map.MapAssets;
import jo.util.geom2d.Point2D;
import jo.util.utils.MathUtils;

public class BorderDrawLogic
{
    public static final int B_COUNTRY = 5;
    public static final int B_CITY = 4;
    public static final int B_TOWN = 3;
    public static final int B_VILLAGE = 2;
    public static final int B_HAMLET = 1;

    public static void paintBorders(Graphics2D g, int ox, int oy, int xRad, int yRad,
            int roadThick)
    {
        int scale = MapDrawLogic.mData.getScale();
        int pscale = MapDrawLogic.mData.getPixelScale();
        Map<Integer,List<BorderSegment>> borderSegments = new HashMap<>();
        if (MapDrawLogic.mData.isDrawBorders())
        {
            if (scale >= 1)
                borderSegments.put(B_COUNTRY, new ArrayList<BorderSegment>());
        }
        if (MapDrawLogic.mData.isDrawTownBorders())
        {
            if (scale >= 4)
                borderSegments.put(B_CITY, new ArrayList<BorderSegment>());
            if (scale >= 12)
                borderSegments.put(B_TOWN, new ArrayList<BorderSegment>());
            if (scale >= 24)
                borderSegments.put(B_VILLAGE, new ArrayList<BorderSegment>());
            if (scale >= 36)
                borderSegments.put(B_HAMLET, new ArrayList<BorderSegment>());
        }
        for (int x = -xRad; x <= xRad; x++)
            for (int y = -yRad; y <= yRad; y++)
            {
                SquareBean sq = MapDrawLogic.getSquareD(x, y);
                int px = x*pscale + ox;
                int py = y*pscale + oy;
                calcBorderSegments(sq, px, py, borderSegments);
            }
        // draw borders
        for (Integer rt : borderSegments.keySet())
        {
            int t = (int)MathUtils.interpolate(rt, B_HAMLET, B_COUNTRY, 1, roadThick*2);
            setBorderColor(g, rt, t);
            List<BorderSegment> bsegs = borderSegments.get(rt);
            List<Point2D> segs = new ArrayList<>();
            for (BorderSegment bs : bsegs)
            {
                segs.add(bs.p1);
                segs.add(bs.p2);
            }
            List<Path2D> groups = MapDrawLogic.groupSegments(segs);
            for (Path2D group : groups)
            {
                g.draw(group);
            }
            if (rt.equals(B_COUNTRY) && MapDrawLogic.mData.isBorderNames())
            {
                mergeSegments(bsegs);
                for (BorderSegment bs : bsegs)
                    if (bs.span >= 7)
                    {
                        if (bs.p1.y == bs.p2.y)
                        {   // horizontal
                            double x = (bs.p1.x + bs.p2.x)/2;
                            NameDrawLogic.drawName(g, (int)x, (int)(bs.p1.y-roadThick*2), bs.d2.getID(), scale);
                            FontMetrics fm = g.getFontMetrics();
                            NameDrawLogic.drawName(g, (int)x, (int)(bs.p1.y+fm.getAscent()), bs.d1.getID(), scale);
                        }
                        else
                        {   // vertical
                            double y = (bs.p1.y + bs.p2.y)/2;
                            NameDrawLogic.drawNameVertical(g, (int)(bs.p1.x+roadThick*2), (int)y, bs.d2.getID(), scale);
                            FontMetrics fm = g.getFontMetrics();
                            NameDrawLogic.drawNameVertical(g, (int)(bs.p1.x-fm.getAscent()), (int)y, bs.d1.getID(), scale);
                        }
                    }
            }
        }
    }

    public static void mergeSegments(List<BorderSegment> bsegs)
    {
        Map<Integer, List<BorderSegment>> hsegs = new HashMap<>();
        Map<Integer, List<BorderSegment>> vsegs = new HashMap<>();
        divideSegments(bsegs, hsegs, vsegs);
        // merge horizontal
        for (List<BorderSegment> segs : hsegs.values())
        {
            Collections.sort(segs, new Comparator<BorderSegment>() {
                @Override
                public int compare(BorderSegment o1, BorderSegment o2)
                {                    
                    return (int)Math.signum(o1.p1.x - o1.p1.x);
                }
            });
            for (int i = 0; i < segs.size() - 1; i++)
            {
                BorderSegment bs1 = segs.get(i);
                BorderSegment bs2 = segs.get(i+1);
                if (MathUtils.equals(bs1.p2.x, bs2.p1.x) && (bs1.d1 == bs2.d1) && (bs1.d2 == bs2.d2))
                {
                    bs1.p2 = bs2.p2;
                    bs1.span++;
                    segs.remove(i+1);
                    i--;
                }
            }
        }
        // merge vertical
        for (List<BorderSegment> segs : vsegs.values())
        {
            Collections.sort(segs, new Comparator<BorderSegment>() {
                @Override
                public int compare(BorderSegment o1, BorderSegment o2)
                {                    
                    return (int)Math.signum(o1.p1.y - o2.p2.y);
                }
            });
            for (int i = 0; i < segs.size() - 1; i++)
            {
                BorderSegment bs1 = segs.get(i);
                BorderSegment bs2 = segs.get(i+1);
                if (MathUtils.equals(bs1.p2.y, bs2.p1.y) && (bs1.d1 == bs2.d1) && (bs1.d2 == bs2.d2))
                {
                    bs1.p2 = bs2.p2;
                    bs1.span++;
                    segs.remove(i+1);
                    i--;
                }
            }
        }
    }

    public static void divideSegments(List<BorderSegment> bsegs,
            Map<Integer, List<BorderSegment>> hsegs,
            Map<Integer, List<BorderSegment>> vsegs)
    {
        for (BorderSegment bs : bsegs)
        {
            if (MathUtils.equals(bs.p1.x, bs.p2.x))
            {   // vertical
                int x = (int)bs.p1.x;
                List<BorderSegment> segs = vsegs.get(x);
                if (segs == null)
                {
                    segs = new ArrayList<>();
                    vsegs.put(x,  segs);
                }
                segs.add(bs);
            }
            else
            {   // horizontal
                int y = (int)bs.p1.y;
                List<BorderSegment> segs = hsegs.get(y);
                if (segs == null)
                {
                    segs = new ArrayList<>();
                    hsegs.put(y,  segs);
                }
                segs.add(bs);
            }
        }
    }

    private static void calcBorderSegments(SquareBean sq, int px, int py, Map<Integer,List<BorderSegment>> borderSegments)
    {
        if (CompConstLogic.isWater(sq.getTerrain()))
            return;
        int h = MapDrawLogic.mData.getPixelScale();
        //System.out.print(sq.getOrds().getX()+","+sq.getOrds().getY()+" n ");
        calcBorderSegment(sq, MapDrawLogic.getSquareNorth(sq), borderSegments, px, py, px+h, py);
        //calcBorderSegment(sq, MapDrawLogic.getSquareSouth(sq), borderSegments, px, py+h, px+h, py+h);
        calcBorderSegment(sq, MapDrawLogic.getSquareEast(sq), borderSegments, px+h, py, px+h, py+h);
        //calcBorderSegment(sq, MapDrawLogic.getSquareWest(sq), borderSegments, px, py, px, py+h);
    }
    
    private static void calcBorderSegment(SquareBean s1, SquareBean s2, Map<Integer,List<BorderSegment>> borderSegments, int x1, int y1, int x2, int y2)
    {
        if (CompConstLogic.isWater(s2.getTerrain()))
            return;
        DemenseBean d1 = s1.getDemense();
        DemenseBean d2 = s2.getDemense();
        if ((d1 == null) && (d2 == null))
            return;
        int type = -1;
        if (d1 == null)
            type = toType(d2.getID());
        else if (d2 == null)
            type = toType(d1.getID());
        else
        {
            Set<DemenseBean> id1 = toSet(d1);
            Set<DemenseBean> id2 = toSet(d2);
            Set<DemenseBean> u = new HashSet<>();
            u.addAll(id1);
            u.addAll(id2);
            Set<DemenseBean> i = new HashSet<>();
            i.addAll(id1);
            i.retainAll(id2);
            Set<DemenseBean> diff = new HashSet<>();
            diff.addAll(u);
            diff.removeAll(i);
            for (DemenseBean d : diff)
                type = Math.max(type,  toType(d.getID()));
            //System.out.print("D1: "+id1+", D2: "+id2+", diff: "+diff);
        }
        List<BorderSegment> segs = borderSegments.get(type);
        if (segs != null)
        {
            BorderSegment bs = new BorderSegment(new Point2D(x1, y1), new Point2D(x2, y2), d1, d2);
            segs.add(bs);
        }
        //System.out.println(" = "+type);
    }
    
    private static Map<DemenseBean,Set<DemenseBean>> mDemenseCache = new HashMap<>();
    
    private static Set<DemenseBean> toSet(DemenseBean d1)
    {
        Set<DemenseBean> s = mDemenseCache.get(d1);
        if (s == null)
        {
            s = new HashSet<>();
            for (DemenseBean d = d1; d != null; d = d.getLiege())
                s.add(d);
            mDemenseCache.put(d1, s);
        }
        return s;
    }
    
    public static int toType(String id)
    {
        if (id == null)
            return -1;
        if (id.indexOf('_') < 0)
            return B_COUNTRY;
        if (id.startsWith("H"))
            return B_HAMLET;
        if (id.startsWith("V"))
            return B_VILLAGE;
        if (id.startsWith("T"))
            return B_TOWN;
        if (id.startsWith("C"))
            return B_CITY;
        return -1;
    }

    private static void setBorderColor(Graphics2D g, int border, int thick)
    {
        if (border == B_COUNTRY)
            g.setColor(MapAssets.BORDER1);
        else if (border == B_CITY)
            g.setColor(MapAssets.BORDER2);
        else if (border == B_TOWN)
            g.setColor(MapAssets.BORDER3);
        else if (border == B_VILLAGE)
            g.setColor(MapAssets.BORDER4);
        else if (border == B_HAMLET)
            g.setColor(MapAssets.BORDER5);
        else
            System.err.println("Unknown border="+border);
        Stroke s = new BasicStroke(thick, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(s);
    }

    public static String findName(SquareBean sq, int type)
    {
        for (DemenseBean d = sq.getDemense(); d != null; d = d.getLiege())
            if (toType(d.getID()) == type)
            {
                if (type == B_COUNTRY)
                    return d.getID();
                else
                {
                    String name = MapDrawLogic.mAssets.expandInserts(d.getName());
                    int o = name.lastIndexOf('<');
                    if (o >= 0)
                        name = name.substring(0, o);
                    o = name.lastIndexOf('>');
                    if (o >= 0)
                        name = name.substring(o + 1);
                    return name;
                }
            }
        return null;
    }
}

class BorderSegment
{
    Point2D p1;
    Point2D p2;
    DemenseBean d1;
    DemenseBean d2;
    int span;
    
    public BorderSegment(Point2D p1, Point2D p2, DemenseBean d1, DemenseBean d2)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.d1 = d1;
        this.d2 = d2;
        normalize();
    }
    
    private void normalize()
    {
        if (d1 != null)
            while (d1.getLiege() != null)
                d1 = d1.getLiege();
        if (d2 != null)
            while (d2.getLiege() != null)
                d2 = d2.getLiege();
    }
    
    @Override
    public String toString()
    {
        return p1+" to "+p2+" seperating "+d1+" and "+d2;
    }
}