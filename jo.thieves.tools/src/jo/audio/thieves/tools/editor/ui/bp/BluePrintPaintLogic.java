package jo.audio.thieves.tools.editor.ui.bp;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorHouseLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;

public class BluePrintPaintLogic
{
    static void paint(BluePrintPanel panel, Graphics g1)
    {
        if (panel.mBaseFont == null)
        {
            panel.mBaseFont = panel.getFont();
            panel.mBaseFont = new Font(panel.mBaseFont.getName(), panel.mBaseFont.getStyle(), 12);
        }
        Graphics2D g = (Graphics2D)g1;
        panel.mSize = panel.getSize();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, panel.mSize.width, panel.mSize.height);
        if (!updateInfo(panel))
            return;
        g.setColor(Color.BLACK);
        g.setFont(panel.mBaseFont);
        g.drawString("Floors: " + panel.mNumFloors + ", Width: " + panel.mTilesWide
                + ", Height: " + panel.mTilesHigh + ", Area: "
                + panel.mNumFloors * panel.mTilesWide * panel.mTilesHigh, 16, 32);
        if (panel.mNumFloors == 0)
            return;
        int floorsWide = (int)Math.ceil(Math.sqrt(panel.mNumFloors));
        int dx = panel.mSize.width / floorsWide;
        int dy = panel.mTilesHigh * panel.ICON_SIZE + 3 * panel.ICON_SIZE;
        panel.mOrigins = new int[panel.mNumFloors][2];
        for (int z = panel.mBounds[0][2]; z <= panel.mBounds[1][2]; z += 2)
        {
            int nx = (z / 2) % floorsWide;
            int ny = (z / 2) / floorsWide;
            int ox = nx * dx;
            int oy = ny * dy;
            int floorW = panel.mTilesWide * panel.ICON_SIZE;
            int floorH = panel.mTilesHigh * panel.ICON_SIZE;
            ox += (dx - floorW) / 2;
            oy += (dy - floorH) / 2;
            panel.mOrigins[(z - panel.mBounds[0][2]) / 2] = new int[] { ox, oy };
        }
        createRooms(panel);
        paintFloors(panel, g);
        paintButtons(panel, g);
    }

    static boolean updateInfo(BluePrintPanel panel)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLibrary lib = es.getLibrary();
        panel.mSquareIndex = lib.getSquares();
        panel.mApatureIndex = lib.getApatures();
        panel.mHouse = es.getSelectedHouse();
        panel.mLocations = panel.mHouse.getLocations();
        if ((panel.mHouse == null) || (panel.mLocations.size() == 0))
            return false;
        panel.mBounds = EditorHouseLogic.getBoundary();
        panel.mSquareBounds = new int[2][3];
        if (panel.mBounds[0][0] % 2 == 0)
            panel.mSquareBounds[0][0] = panel.mBounds[0][0] + 1;
        else
            panel.mSquareBounds[0][0] = panel.mBounds[0][0];
        if (panel.mBounds[0][1] % 2 == 0)
            panel.mSquareBounds[0][1] = panel.mBounds[0][1] + 1;
        else
            panel.mSquareBounds[0][1] = panel.mBounds[0][1];
        panel.mSquareBounds[0][2] = panel.mBounds[0][2];
        if (panel.mBounds[1][0] % 2 == 0)
            panel.mSquareBounds[1][0] = panel.mBounds[1][0] - 1;
        else
            panel.mSquareBounds[1][0] = panel.mBounds[1][0];
        if (panel.mBounds[1][1] % 2 == 0)
            panel.mSquareBounds[1][1] = panel.mBounds[1][1] - 1;
        else
            panel.mSquareBounds[1][1] = panel.mBounds[1][1];
        panel.mSquareBounds[1][2] = panel.mBounds[1][2];
        panel.mNumFloors = (panel.mBounds[1][2] - panel.mBounds[0][2] + 2) / 2;
        panel.mTilesHigh = (panel.mBounds[1][1] - panel.mBounds[0][1] - 1) / 2;
        panel.mTilesWide = (panel.mBounds[1][0] - panel.mBounds[0][0] - 1) / 2;
        return true;
    }

    private static PLocationRef getLocation(BluePrintPanel panel, int x, int y, int z)
    {
        String k = x + "," + y + "," + z;
        return panel.mLocations.get(k);
    }

    private static final int MODE_MARGIN = 4;
    private static final int MODE_WIDTH  = 48;
    private static final int MODE_HEIGHT = 24;

    private static void paintButtons(BluePrintPanel panel, Graphics2D g)
    {
        panel.mModeButton = new Rectangle(panel.mSize.width - MODE_WIDTH - MODE_MARGIN * 2,
                MODE_MARGIN, MODE_WIDTH, MODE_HEIGHT);
        String txt;
        if (panel.mMode == BluePrintPanel.MODE_DEL)
        {
            g.setColor(Color.RED);
            txt = "DEL";
        }
        else if (panel.mMode == BluePrintPanel.MODE_INSERT)
        {
            g.setColor(Color.BLUE);
            txt = "INS";
        }
        else
        {
            g.setColor(Color.LIGHT_GRAY);
            txt = "VIEW";
        }
        g.fill(panel.mModeButton);
        g.setColor(Color.DARK_GRAY);
        g.draw(panel.mModeButton);
        drawCenterText(panel, g, txt, panel.mModeButton);
    }

    private static void paintFloors(BluePrintPanel panel, Graphics2D g)
    {
        g.setColor(Color.black);
        g.setFont(panel.mBaseFont);
        for (int z = panel.mBounds[0][2]; z <= panel.mBounds[1][2]; z += 2)
        {
            int[] oxy = getOrigin(panel, z);
            g.drawString(String.valueOf(z + 1), oxy[0] - 16, oxy[1]);
        }
        for (PolyTile room : panel.mTiles)
            paintRoom(panel, g, room);
        paintWalls(panel, g);
        if (panel.mHoverTile != null)
        {
            g.setColor(Color.blue);
            g.draw(panel.mHoverTile.toPolygon());
            paintRoomLabel(panel, g, panel.mHoverTile);
        }
        if (panel.mHoverSquare != null)
        {
            Rectangle r = getRectangle(panel, panel.mHoverSquare[0], panel.mHoverSquare[1],
                    panel.mHoverSquare[2], PTemplate.SQUARE);
            g.setColor(Color.RED);
            g.draw(r);
        }
    }

    static int[] getOrigin(BluePrintPanel panel, int z)
    {
        return panel.mOrigins[(z - panel.mBounds[0][2]) / 2];
    }

    private static Rectangle getRectangle(BluePrintPanel panel, int x, int y, int z, int type)
    {
        int[] oxy = getOrigin(panel, z);
        if (type == PTemplate.APATURE_HORZ)
        {
            int px = (x - 1) * panel.ICON_SIZE / 2 + oxy[0];
            int py = y * panel.ICON_SIZE / 2 + oxy[1];
            return new Rectangle(px, py - panel.DOOR_WIDTH / 2, panel.ICON_SIZE,
                    panel.DOOR_WIDTH);
        }
        else if (type == PTemplate.APATURE_VERT)
        {
            int px = x * panel.ICON_SIZE / 2 + oxy[0];
            int py = (y - 1) * panel.ICON_SIZE / 2 + oxy[1];
            return new Rectangle(px - panel.DOOR_WIDTH / 2, py, panel.DOOR_WIDTH,
                    panel.ICON_SIZE);
        }
        else if (type == PTemplate.SQUARE)
        {
            int px = (x - 1) / 2 * panel.ICON_SIZE + oxy[0];
            int py = (y - 1) / 2 * panel.ICON_SIZE + oxy[1];
            return new Rectangle(px, py, panel.ICON_SIZE, panel.ICON_SIZE);
        }
        else
            throw new IllegalArgumentException();
    }

    private static Rectangle[] getSplitRectangles(BluePrintPanel panel, int x, int y, int z, int type)
    {
        Rectangle base = getRectangle(panel, x, y, z, type);
        Rectangle[] ret = new Rectangle[3];
        if (base.getWidth() > base.getHeight())
        {
            ret[0] = new Rectangle(base.x, base.y, panel.DOOR_WIDTH * 2, panel.DOOR_WIDTH);
            ret[1] = new Rectangle(base.x + panel.DOOR_WIDTH * 2, base.y,
                    panel.ICON_SIZE - panel.DOOR_WIDTH * 4, panel.DOOR_WIDTH);
            ret[2] = new Rectangle(base.x + panel.ICON_SIZE - panel.DOOR_WIDTH * 2, base.y,
                    panel.DOOR_WIDTH * 2, panel.DOOR_WIDTH);
        }
        else
        {
            ret[0] = new Rectangle(base.x, base.y, panel.DOOR_WIDTH, panel.DOOR_WIDTH * 2);
            ret[1] = new Rectangle(base.x, base.y + panel.DOOR_WIDTH * 2, panel.DOOR_WIDTH,
                    panel.ICON_SIZE - panel.DOOR_WIDTH * 4);
            ret[2] = new Rectangle(base.x, base.y + panel.ICON_SIZE - panel.DOOR_WIDTH * 2,
                    panel.DOOR_WIDTH, panel.DOOR_WIDTH * 2);
        }
        return ret;
    }

    private static PLocationRef[] getNeighbors(BluePrintPanel panel, int x, int y, int z)
    {
        int type = PTemplate.getType(x, y, z);
        PLocationRef[] ret = new PLocationRef[2];
        if (type == PTemplate.APATURE_VERT)
        {
            ret[0] = getLocation(panel, x - 1, y, z);
            ret[1] = getLocation(panel, x + 1, y, z);
        }
        else if (type == PTemplate.APATURE_HORZ)
        {
            ret[0] = getLocation(panel, x, y - 1, z);
            ret[1] = getLocation(panel, x, y + 1, z);
        }
        else
            throw new IllegalArgumentException();
        return ret;
    }

    private static void paintWalls(BluePrintPanel panel, Graphics2D g)
    {
        for (int z = panel.mBounds[0][2]; z <= panel.mBounds[1][2]; z += 2)
            for (int y = panel.mBounds[0][1]; y <= panel.mBounds[1][1]; y++)
                for (int x = panel.mBounds[0][0]; x <= panel.mBounds[1][0]; x++)
                {
                    int type = PTemplate.getType(x, y, z);
                    if (type >= PTemplate.APATURE_HORZ)
                    {
                        PLocationRef loc = getLocation(panel, x, y, z);
                        if (loc == null)
                        {
                            PLocationRef[] n = getNeighbors(panel, x, y, z);
                            if ((n[0] != null) && (n[1] != null))
                            {
                                Rectangle r = getRectangle(panel, x, y, z, type);
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
                            PApature apature = panel.mApatureIndex.get(loc.getID());
                            if (apature.getOpenable())
                            {
                                Rectangle[] r = getSplitRectangles(panel, x, y, z,
                                        type);
                                g.setColor(Color.DARK_GRAY);
                                g.fill(r[0]);
                                g.fill(r[2]);
                                g.setColor(apature.getColorObject());
                                g.fill(r[1]);
                            }
                            else
                            {
                                Rectangle r = getRectangle(panel, x, y, z, type);
                                g.setColor(apature.getColorObject());
                                g.fill(r);
                            }
                        }
                    }
                }
    }

    private static void paintRoom(BluePrintPanel panel, Graphics2D g, PolyTile room)
    {
        Polygon p = room.toPolygon();
        Color c = room.mTile.getColorObject();
        g.setColor(c);
        g.fill(p);
    }

    private static void paintRoomLabel(BluePrintPanel panel, Graphics2D g, PolyTile room)
    {
        Polygon p = room.toPolygon();
        drawCenterText(panel, g, room.mTile.getName(), p.getBounds());
    }

    private static void drawCenterText(BluePrintPanel panel, Graphics2D g, String txt, Rectangle bounds)
    {
        for (int size = 16; size > 4; size--)
        {
            Font f = new Font(panel.mBaseFont.getName(), panel.mBaseFont.getStyle(), size);
            FontMetrics fm = g.getFontMetrics(f);
            Rectangle2D r = fm.getStringBounds(txt, g);
            if (r.getWidth() < bounds.width)
            {
                panel.setFont(f);
                int sx = bounds.x + (int)(bounds.width - r.getWidth()) / 2;
                int sy = bounds.y + bounds.height / 2
                        + (fm.getAscent() - fm.getDescent()) / 2;
                // System.out.println(txt+" Bounds: "+bounds+", extent:
                // "+r.getWidth()+","+r.getHeight()+" -> "+sx+","+sy);
                g.drawString(txt, sx, sy);
                return;
            }
        }
    }

    private static void createRooms(BluePrintPanel panel)
    {
        panel.mTiles.clear();
        for (PLocationRef loc : panel.mLocations.values())
        {
            int type = PTemplate.getType(loc);
            // System.out.println(loc.getX()+","+loc.getY()+","+loc.getZ()+"
            // "+type+" "+loc.getID());
            if (type != PTemplate.SQUARE)
                continue;
            for (PolyTile room : panel.mTiles)
                if (room.adjacent(loc))
                {
                    room.mPoints.add(new Point(loc.getX(), loc.getY()));
                    loc = null;
                    break;
                }
            if (loc != null)
            {
                PolyTile tile = new PolyTile(panel, loc);
                panel.mTiles.add(tile);
            }
        }
        consolidateRooms(panel);
    }

    private static void consolidateRooms(BluePrintPanel panel)
    {
        for (int i = 0; i < panel.mTiles.size() - 1; i++)
        {
            PolyTile r1 = panel.mTiles.get(i);
            for (int j = i + 1; j < panel.mTiles.size(); j++)
            {
                PolyTile r2 = panel.mTiles.get(j);
                if (r1.adjacent(r2))
                {
                    r1.mPoints.addAll(r2.mPoints);
                    panel.mTiles.remove(j);
                    j--;
                }
            }
        }
    }

}
