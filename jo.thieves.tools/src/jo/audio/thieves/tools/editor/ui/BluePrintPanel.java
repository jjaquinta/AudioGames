package jo.audio.thieves.tools.editor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.data.template.PLocation;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorHouseLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.util.ui.swing.utils.MouseUtils;

@SuppressWarnings("serial")
public class BluePrintPanel extends JComponent
{
    private static int                ICON_SIZE    = 32;
    private static int                DOOR_WIDTH   = 6;
    private static int                DOOR_HEIGHT  = 26;

    private Dimension                 mSize;
    private PTemplate                 mHouse;
    private Map<String,PSquare>       mSquareIndex;
    private Map<String,PApature>      mApatureIndex;
    private Map<String, PLocationRef> mLocations      = new HashMap<>();
    private int[][]                   mBounds;
    private int[][]                   mSquareBounds;
    private int                       mNumFloors;
    private int                       mTilesWide;
    private int                       mTilesHigh;
    private Font                      mBaseFont;

    public BluePrintPanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
    }

    private void initLayout()
    {
    }

    private void initLink()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PropertyChangeListener pcl = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                repaint();
            }
        };
        es.addPropertyChangeListener("selectedHouse", pcl);
        es.addPropertyChangeListener("location.floor", pcl);
        es.addPropertyChangeListener("location.tile", pcl);
        MouseUtils.mouseMoved(this, (e) -> doMouseMoved(e));
        MouseUtils.mouseClicked(this, (e) -> doMouseClicked(e));
        MouseUtils.mouseWheelMoved(this, (e) -> doMouseWheelMoved(e));
    }

    @Override
    public void paint(Graphics g1)
    {
        if (mBaseFont == null)
            mBaseFont = getFont();
        Graphics2D g = (Graphics2D)g1;
        mSize = getSize();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, mSize.width, mSize.height);
        if (!updateInfo())
            return;
        g.setColor(Color.BLACK);
        g.drawString("Floors: " + mNumFloors + ", Width: " + mTilesWide
                + ", Height: " + mTilesHigh + ", Area: "
                + mNumFloors * mTilesWide * mTilesHigh, 16, 32);
        if (mNumFloors == 0)
            return;
        int floorsWide = (int)Math.ceil(Math.sqrt(mNumFloors));
        int dx = mSize.width / floorsWide;
        int dy = mTilesHigh * ICON_SIZE + 3 * ICON_SIZE;
        for (int z = mBounds[0][2]; z <= mBounds[1][2]; z += 2)
        {
            int nx = (z/2) % floorsWide;
            int ny = (z/2) / floorsWide;
            int ox = nx * dx;
            int oy = ny * dy;
            int floorW = mTilesWide * ICON_SIZE;
            int floorH = mTilesHigh * ICON_SIZE;
            ox += (dx - floorW) / 2;
            oy += (dy - floorH) / 2;
            paintFloor(g, ox, oy, z);
        }
        g.setColor(Color.DARK_GRAY);
    }

    private boolean updateInfo()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLibrary lib = es.getLibrary();
        mSquareIndex = lib.getSquares();
        mApatureIndex = lib.getApatures();
        mHouse = es.getSelectedHouse();
        Map<String, PLocationRef> squares = mHouse.getSquares();
        Map<String, PLocationRef> apatures = mHouse.getApatures();
        mLocations.clear();
        for (String id : squares.keySet())
        {
            PLocationRef square = squares.get(id);
            mLocations.put(id, square);
        }
        for (String id : apatures.keySet())
        {
            PLocationRef apature = apatures.get(id);
            mLocations.put(id, apature);
        }
        if ((mHouse == null) || (mLocations.size() == 0))
            return false;
        mBounds = EditorHouseLogic.getBoundary();
        mSquareBounds = new int[2][3];
        if (mBounds[0][0]%2 == 0)
            mSquareBounds[0][0] = mBounds[0][0] + 1;
        else
            mSquareBounds[0][0] = mBounds[0][0];
        if (mBounds[0][1]%2 == 0)
            mSquareBounds[0][1] = mBounds[0][1] + 1;
        else
            mSquareBounds[0][1] = mBounds[0][1];
        mSquareBounds[0][2] = mBounds[0][2];
        if (mBounds[1][0]%2 == 0)
            mSquareBounds[1][0] = mBounds[1][0] - 1;
        else
            mSquareBounds[1][0] = mBounds[1][0];
        if (mBounds[1][1]%2 == 0)
            mSquareBounds[1][1] = mBounds[1][1] - 1;
        else
            mSquareBounds[1][1] = mBounds[1][1];
        mSquareBounds[1][2] = mBounds[1][2];
        mNumFloors = (mBounds[1][2] - mBounds[0][2] + 2)/2;
        mTilesHigh = (mBounds[1][1] - mBounds[0][1] - 1)/2;
        mTilesWide = (mBounds[1][0] - mBounds[0][0] - 1)/2;
        return true;
    }

    private PLocationRef getLocation(int x, int y, int z)
    {
        String k = x+","+y+","+z;
        return mLocations.get(k);
    }

    private void paintFloor(Graphics2D g, int ox, int oy, int z)
    {
        List<PolyTile> rooms = createRooms(z);
        consolidateRooms(rooms);
        g.drawString(String.valueOf(z + 1), ox - 16, oy);
        for (PolyTile room : rooms)
            paintRoom(g, ox, oy, room);
        paintWalls(g, ox, oy, z);
        g.setColor(Color.blue);
        for (PolyTile room : rooms)
            paintRoomLabel(g, ox, oy, room);
    }
    
    private Rectangle getRectangle(int x, int y, int z, int ox, int oy, int type)
    {
        if (type == PTemplate.APATURE_HORZ)
        {
            int px = x*ICON_SIZE/2 + ox;
            int py = (y+1)*ICON_SIZE/2 + oy;
            return new Rectangle(px, py - DOOR_WIDTH/2, ICON_SIZE, DOOR_WIDTH);
        }
        else
        {
            int px = (x+1)*ICON_SIZE/2 + ox;
            int py = y*ICON_SIZE/2 + oy;
            return new Rectangle(px - DOOR_WIDTH/2, py, DOOR_WIDTH, ICON_SIZE);
        }
    }
    
    private Rectangle[] getSplitRectangles(int x, int y, int z, int ox, int oy, int type)
    {
        Rectangle base = getRectangle(x, y, z, ox, oy, type);
        Rectangle[] ret = new Rectangle[3];
        if (base.getWidth() > base.getHeight())
        {
            ret[0] = new Rectangle(base.x, base.y, DOOR_WIDTH*2, DOOR_WIDTH);
            ret[1] = new Rectangle(base.x + DOOR_WIDTH*2, base.y, ICON_SIZE - DOOR_WIDTH*4, DOOR_WIDTH);
            ret[2] = new Rectangle(base.x + ICON_SIZE - DOOR_WIDTH*2, base.y, DOOR_WIDTH*2, DOOR_WIDTH);
        }
        else
        {
            ret[0] = new Rectangle(base.x, base.y, DOOR_WIDTH, DOOR_WIDTH*2);
            ret[1] = new Rectangle(base.x, base.y + DOOR_WIDTH*2, DOOR_WIDTH, ICON_SIZE - DOOR_WIDTH*4);
            ret[2] = new Rectangle(base.x, base.y + ICON_SIZE - DOOR_WIDTH*2, DOOR_WIDTH, DOOR_WIDTH*2);
        }
        return ret;
    }
    
    private PLocationRef[] getNeighbors(int x, int y, int z)
    {
        int type = PTemplate.getType(x, y, z);
        PLocationRef[] ret = new PLocationRef[2];
        if (type == PTemplate.APATURE_VERT)
        {
            ret[0] = getLocation(x-1, y, z);
            ret[1] = getLocation(x+1, y, z);
        }
        else if (type == PTemplate.APATURE_HORZ)
        {
            ret[0] = getLocation(x, y-1, z);
            ret[1] = getLocation(x, y+1, z);
        }
        else
            throw new IllegalArgumentException();
        return ret;
    }

    private void paintWalls(Graphics2D g, int ox, int oy, int z)
    {
        for (int y = mBounds[0][1]; y <= mBounds[1][1]; y++)
            for (int x = mBounds[0][0]; x <= mBounds[1][0]; x++)
            {
                int type = PTemplate.getType(x, y, z);
                if (type >= PTemplate.APATURE_HORZ)
                {
                    PLocationRef loc = getLocation(x, y, z);
                    if (loc == null)
                    {
                        PLocationRef[] n = getNeighbors(x, y, z);
                        if ((n[0] != null) && (n[1] != null))
                        {
                            Rectangle r = getRectangle(x, y, z, ox, oy, type);
                            g.setColor(Color.DARK_GRAY);
                            g.fill(r);
                        }
                    }
                    else if ("EMPTY".equals(loc.getID()))
                    {
                        continue;
                    }
                    else
                    {
                        PApature apature = mApatureIndex.get(loc.getID());
                        if (apature.getOpenable())
                        {
                            Rectangle[] r = getSplitRectangles(x, y, z, ox, oy, type);
                            g.setColor(Color.DARK_GRAY);
                            g.fill(r[0]);
                            g.fill(r[2]);
                            g.setColor(apature.getColorObject());
                            g.fill(r[1]);
                        }
                        else
                        {
                            
                        }
                    }
                }
            }
    }
    private void paintRoom(Graphics2D g, int ox, int oy, PolyTile room)
    {
        Polygon p = room.toPolygon(ox, oy);
        Color c = room.mTile.getColorObject();
        g.setColor(c);
        g.fill(p);
        drawCenterText(g, room.mTile.getName(), p.getBounds());
    }
    
    private void paintRoomLabel(Graphics2D g, int ox, int oy, PolyTile room)
    {
        Polygon p = room.toPolygon(ox, oy);
        drawCenterText(g, room.mTile.getName(), p.getBounds());
    }
    
    private void drawCenterText(Graphics2D g, String txt, Rectangle bounds)
    {
        for (int size = mBaseFont.getSize(); size > 4; size--)
        {
            Font f = new Font(mBaseFont.getName(), mBaseFont.getStyle(), size);
            FontMetrics fm = g.getFontMetrics(f);
            Rectangle2D r = fm.getStringBounds(txt, g);
            if (r.getWidth() < bounds.width)
            {
                setFont(f);
                int sx = bounds.x + (int)(bounds.width - r.getWidth())/2;
                int sy = bounds.y + (int)(bounds.height - r.getHeight())/2
                        + (fm.getAscent() - fm.getDescent())/2;
                //System.out.println(txt+" Bounds: "+bounds+", extent: "+r.getWidth()+","+r.getHeight()+" -> "+sx+","+sy);
                g.drawString(txt, sx, sy);
                return;
            }
        }
    }

    private List<PolyTile> createRooms(int z)
    {
        List<PolyTile> rooms = new ArrayList<>();
        for (int y = mSquareBounds[0][1]; y <= mSquareBounds[1][1]; y += 2)
            for (int x = mSquareBounds[0][0]; x <= mSquareBounds[1][0]; x += 2)
            {
                PLocationRef loc = getLocation(x, y, z);
                if (loc == null)
                    continue;
                for (PolyTile room : rooms)
                    if (room.adjacent(loc))
                    {
                        room.mPoints.add(new Point(x, y));
                        loc = null;
                        break;
                    }
                if (loc != null)
                {
                    PolyTile tile = new PolyTile(loc);
                    rooms.add(tile);
                }
            }
        return rooms;
    }
    
    private void consolidateRooms(List<PolyTile> rooms)
    {
        for (int i = 0; i < rooms.size() - 1; i++)
        {
            PolyTile r1 = rooms.get(i);
            for (int j = i + 1; j < rooms.size(); j++)
            {
                PolyTile r2 = rooms.get(j);
                if (r1.adjacent(r2))
                {
                    r1.mPoints.addAll(r2.mPoints);
                    rooms.remove(j);
                    j--;
                }
            }
        }
    }
    
    private void findRect(int x, int y)
    {
    }

    private void doMouseMoved(MouseEvent e)
    {
    }

    private void doMouseClicked(MouseEvent e)
    {
    }

    public void doMouseWheelMoved(MouseWheelEvent e)
    {
        int delta = e.getWheelRotation();
        DOOR_WIDTH += delta;
        if (DOOR_WIDTH < 1)
            DOOR_WIDTH = 1;
        DOOR_HEIGHT = DOOR_WIDTH * 6;
        ICON_SIZE = DOOR_WIDTH * 8;
        repaint();
    }

    class PolyTile
    {
        PLocation   mTile;
        List<Point> mPoints = new ArrayList<>();
        Polygon     mPoly;
        
        public PolyTile(PLocationRef loc)
        {
            mTile = mSquareIndex.get(loc.getID());
            if (mTile == null)
                System.err.println("No such square "+loc.getID()+" at "+loc.getX()+","+loc.getY()+","+loc.getZ());
            mPoints.add(new Point(loc.getX(), loc.getY()));
        }
        
        public boolean adjacent(PLocationRef loc)
        {
            if (!loc.getID().equals(mTile.getID()))
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
        
        public Polygon toPolygon(int ox, int oy)
        {
            if (mPoly != null)
                return mPoly;
            List<int[]> segs = new ArrayList<>();
            for (Point p : mPoints)
            {
                addSeg(segs, p.x, p.y, p.x + 2, p.y);
                addSeg(segs, p.x + 2, p.y, p.x + 2, p.y + 2);
                addSeg(segs, p.x + 2, p.y + 2, p.x, p.y + 2);
                addSeg(segs, p.x, p.y + 2, p.x, p.y);
            }
//            System.out.println(mPoints.size()+" points -> "+segs.size()+" segments");
//            System.out.println("Before sort:");
//            for (int[] seg : segs)
//                System.out.print(seg[0]+","+seg[1]+"->"+seg[2]+","+seg[3]+"  ");
//            System.out.println();
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
//            System.out.println("after sort:");
//            for (int[] seg : segs)
//                System.out.print(seg[0]+","+seg[1]+"->"+seg[2]+","+seg[3]+"  ");
//            System.out.println();
            
            int[] xpoints = new int[segs.size()];
            int[] ypoints = new int[segs.size()];
            for (int i = 0; i < segs.size(); i++)
            {
                int[] seg = segs.get(i);
                xpoints[i] = seg[0]*ICON_SIZE/2 + ox;
                ypoints[i] = seg[1]*ICON_SIZE/2 + oy;
            }
            mPoly = new Polygon(xpoints, ypoints, xpoints.length);
            return mPoly;
        }
    }
}
