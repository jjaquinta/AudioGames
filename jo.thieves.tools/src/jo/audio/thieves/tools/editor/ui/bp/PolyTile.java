package jo.audio.thieves.tools.editor.ui.bp;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jo.audio.thieves.data.template.PLocation;
import jo.audio.thieves.data.template.PLocationRef;

class PolyTile
{
    BluePrintPanel mPanel;
    PLocation   mTile;
    int         mZ;
    List<Point> mPoints = new ArrayList<>();
    Polygon     mPoly;
    
    public PolyTile(BluePrintPanel panel, PLocationRef loc)
    {
        mPanel = panel;
        mTile = mPanel.mSquareIndex.get(loc.getID());
        if (mTile == null)
            System.err.println("No such square "+loc.getID()+" at "+loc.getX()+","+loc.getY()+","+loc.getZ());
        mZ = loc.getZ();
        mPoints.add(new Point(loc.getX(), loc.getY()));
    }

    public static boolean equals(PolyTile t1, PolyTile t2)
    {
        if (t1 == null)
            if (t2 == null)
                return true;
            else
                return false;
        else if (t2 == null)
            return false;
        else
            return t1 == t2;
    }

    public boolean adjacent(PLocationRef loc)
    {
        if (!loc.getID().equals(mTile.getID()))
            return false;
        if (loc.getZ() != mZ)
            return false;
        for (Point p : mPoints)
        {
            int dx = loc.getX() - p.x;
            int dy = loc.getY() - p.y;
            if (Math.abs(dx)  + Math.abs(dy) <= 2)
                return true;
        }
        return false;
    }
    
    public boolean adjacent(PolyTile poly)
    {
        if (!poly.mTile.getID().equals(mTile.getID()))
            return false;
        if (poly.mZ != mZ)
            return false;
        for (Point p2 : poly.mPoints)
            for (Point p : mPoints)
            {
                int dx = p2.x - p.x;
                int dy = p2.y - p.y;
                if (Math.abs(dx)  + Math.abs(dy) <= 2)
                    return true;
            }
        return false;
    }
    
    private void addSeg(List<int[]> segs, int x1, int y1, int x2, int y2)
    {
        for (Iterator<int[]> i = segs.iterator(); i.hasNext(); )
        {
            int[] seg = i.next();
            if ((seg[0] == x2) && (seg[1] == y2) && (seg[2] == x1) && (seg[3] == y1))
            {
                i.remove();
                return;
            }
        }
        segs.add(new int[] { x1, y1, x2, y2 });
    }
    
    public Polygon toPolygon()
    {
        if (mPoly != null)
            return mPoly;
        int[] oxy = BluePrintPaintLogic.getOrigin(mPanel, mZ);
        List<int[]> segs = new ArrayList<>();
        for (Point p : mPoints)
        {
            addSeg(segs, p.x, p.y, p.x + 2, p.y);
            addSeg(segs, p.x + 2, p.y, p.x + 2, p.y + 2);
            addSeg(segs, p.x + 2, p.y + 2, p.x, p.y + 2);
            addSeg(segs, p.x, p.y + 2, p.x, p.y);
        }
//        System.out.println(mPoints.size()+" points -> "+segs.size()+" segments");
//        System.out.println("Before sort:");
//        for (int[] seg : segs)
//            System.out.print(seg[0]+","+seg[1]+"->"+seg[2]+","+seg[3]+"  ");
//        System.out.println();
        for (int i = 0; i < segs.size() - 1; i++)
        {
            int[] seg1 = segs.get(i);
            for (int j = i + 1; j < segs.size(); j++)
            {
                int[] seg2 = segs.get(j);
                if ((seg1[2] == seg2[0]) && (seg1[3] == seg2[1]))
                {
                    if (j > i + 1)
                    {
                        segs.remove(j);
                        segs.add(i+1, seg2);
                    }
                    break;
                }
            }
        }
//        System.out.println("after sort:");
//        for (int[] seg : segs)
//            System.out.print(seg[0]+","+seg[1]+"->"+seg[2]+","+seg[3]+"  ");
//        System.out.println();
        
        int[] xpoints = new int[segs.size()];
        int[] ypoints = new int[segs.size()];
        for (int i = 0; i < segs.size(); i++)
        {
            int[] seg = segs.get(i);
            xpoints[i] = (seg[0]-1)*mPanel.ICON_SIZE/2 + oxy[0];
            ypoints[i] = (seg[1]-1)*mPanel.ICON_SIZE/2 + oxy[1];
        }
        mPoly = new Polygon(xpoints, ypoints, xpoints.length);
        return mPoly;
    }
}
