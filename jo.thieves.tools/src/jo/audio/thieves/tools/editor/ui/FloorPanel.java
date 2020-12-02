package jo.audio.thieves.tools.editor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.data.template.PLocation;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorApatureLogic;
import jo.audio.thieves.tools.editor.logic.EditorHouseLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.audio.thieves.tools.editor.logic.EditorSquareLogic;
import jo.audio.thieves.tools.logic.RuntimeLogic;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.utils.obj.StringUtils;

@SuppressWarnings("serial")
public class FloorPanel extends JComponent
{
    private static int                ICON_SIZE    = 32;
    private static int                DOOR_WIDTH   = 6;
    private static int                DOOR_HEIGHT  = 26;

    private Dimension                 mSize;
    private PTemplate                 mHouse;
    private Map<String,PSquare>       mSquareIndex;
    private Map<String,PApature>      mApatureIndex;
    private Map<String,PSquare>       mSquares;
    private Map<String,PApature>      mApatures;
    private int[][]                   mBounds;
    private int                       mNumFloors;
    private int                       mTilesWide;
    private int                       mTilesHigh;
    private Map<Rectangle, int[]>     mLocMap      = new HashMap<>();
    private Map<Rectangle, PLocation> mTileMap     = new HashMap<>();
    private Rectangle                 mLastClickedRect;
    private PLocation                 mLastClickedTile;
    private int[]                     mLastClickedLocation;
    private PLocation                 mLastSetTile = null;

    private JPopupMenu                mBackPopup;
    private JPopupMenu                mApaturePopup;
    private JPopupMenu                mLocationPopup;

    private JMenuItem                 mNewTile;
    private JMenuItem                 mSetTile;
    private JMenuItem                 mRemoveTile;
    private JMenuItem                 mAddEdge;

    public FloorPanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mSetTile = new JMenuItem("Set...");
        mNewTile = new JMenuItem("New...");
        mRemoveTile = new JMenuItem("Remove");
        mAddEdge = new JMenuItem("Add Roof Edge");
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
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                doMouseMoved(e);
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                doMouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e)
            {
                if (e.isPopupTrigger())
                    doShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
                    doShowPopup(e);
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                doMouseWheelMoved(e);
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
        ListenerUtils.listen(mRemoveTile, (e) -> doRemoveTile());
        ListenerUtils.listen(mSetTile, (e) -> doSetTile());
        ListenerUtils.listen(mNewTile, (e) -> doNewTile());
        ListenerUtils.listen(mAddEdge, (e) -> doAddEdge());
    }

    private JPopupMenu makeLocationPopup()
    {
        mLocationPopup = new JPopupMenu();
        mLocationPopup.add(mSetTile);
        mLocationPopup.add(mRemoveTile);
        mLocationPopup.add(mNewTile);
        mLocationPopup.add(mAddEdge);
        return mLocationPopup;
    }

    private JPopupMenu makeApaturePopup()
    {
        mApaturePopup = new JPopupMenu();
        mApaturePopup.add(mSetTile);
        mApaturePopup.add(mRemoveTile);
        mApaturePopup.add(mNewTile);
        mApaturePopup.add(mAddEdge);
        return mApaturePopup;
    }

    private JPopupMenu makeBackPopup()
    {
        mBackPopup = new JPopupMenu();
        return mBackPopup;
    }

    @Override
    public void paint(Graphics g1)
    {
        Graphics2D g = (Graphics2D)g1;
        mSize = getSize();
        mLocMap.clear();
        mTileMap.clear();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, mSize.width, mSize.height);
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLibrary lib = es.getLibrary();
        mSquareIndex = lib.getSquares();
        mApatureIndex = lib.getApatures();
        mHouse = es.getSelectedHouse();
        mSquares = mHouse.getSquares();
        mApatures = mHouse.getApatures();
        if ((mHouse == null) || (mSquares.size() + mApatures.size() == 0))
            return;
        mBounds = EditorHouseLogic.getBoundary();
        mNumFloors = (mBounds[1][2] - mBounds[0][2] + 1)/2;
        mTilesHigh = (mBounds[1][1] - mBounds[0][1] - 1)/2;
        mTilesWide = (mBounds[1][0] - mBounds[0][0] - 1)/2;
        g.setColor(Color.BLACK);
        g.drawString("Floors: " + mNumFloors + ", Width: " + mTilesWide
                + ", Height: " + mTilesHigh + ", Area: "
                + mNumFloors * mTilesWide * mTilesHigh, 16, 32);
        if (mNumFloors == 0)
            return;
        int floorsWide = (int)Math.ceil(Math.sqrt(mNumFloors));
        int dx = mSize.width / floorsWide;
        int dy = mTilesHigh * ICON_SIZE + 3 * ICON_SIZE;
        for (int i = 0; i < mNumFloors; i++)
        {
            int nx = i % floorsWide;
            int ny = i / floorsWide;
            int ox = nx * dx;
            int oy = ny * dy;
            int floorW = mTilesWide * ICON_SIZE;
            int floorH = mTilesHigh * ICON_SIZE;
            ox += (dx - floorW) / 2;
            oy += (dy - floorH) / 2;
            paintFloor(g, ox, oy, i);
        }
    }
    
    private PLocation getLocation(int x, int y, int z)
    {
        String k = x+","+y+","+z;
        if (mSquares.containsKey(k))
        {
            String id = mSquares.get(k).getID();
            return mSquareIndex.get(id);
        }
        if (mApatures.containsKey(k))
        {
            String id = mApatures.get(k).getID();
            return mApatureIndex.get(id);
        }
        return null;
    }
    
    private Rectangle getRectangle(int ox, int oy, int x, int y)
    {
        int dx = x - mBounds[0][0];
        int dy = y - mBounds[0][1];
        int px = (dx/2)*(ICON_SIZE);
        if (dx%2 == 1)
            px += DOOR_WIDTH;
        int py = (dy/2)*(ICON_SIZE);
        if (dy%2 == 1)
            py += DOOR_WIDTH;
        int width = (dx%2 == 0) ? DOOR_WIDTH : DOOR_HEIGHT;
        int height = (dy%2 == 0) ? DOOR_WIDTH : DOOR_HEIGHT;
        Rectangle r = new Rectangle(ox + px, oy + py, width, height);
        //System.out.println(x+","+y+" -> "+dx+","+dy+" -> "+px+","+py+"x"+width+","+height);
        return r;
    }

    private void paintFloor(Graphics2D g, int ox, int oy, int f)
    {
        g.drawString(String.valueOf(f + 1), ox - 16, oy);
        int z = f*2;
        for (int y = mBounds[0][1]; y <= mBounds[1][1]; y++)
            for (int x = mBounds[0][0]; x <= mBounds[1][0]; x++)
            {
                Rectangle r = getRectangle(ox, oy, x, y);
                PLocation tile = paintLocation(g, r, x, y, z);
                if (tile instanceof PSquare)
                {
                    /*
                    Rectangle rup = new Rectangle((int)r.getX() + DOOR_WIDTH,
                            (int)r.getY() + DOOR_WIDTH, DOOR_WIDTH * 2,
                            DOOR_WIDTH * 2);
                    paintLocation(g, rup, x, y, z+1);
                    Rectangle rdown = new Rectangle((int)r.getMaxX() - DOOR_WIDTH*2,
                            (int)r.getMaxY() - DOOR_WIDTH*2, DOOR_WIDTH * 2,
                            DOOR_WIDTH * 2);
                    paintLocation(g, rdown, x, y, z-1);
                    */
                }
            }
    }

    private PLocation paintLocation(Graphics2D g, Rectangle r, int x, int y, int z)
    {
        mLocMap.put(r, new int[] { x, y, z });
        PLocation tile = getLocation(x, y, z);
        if (tile != null)
        {
            mTileMap.put(r, tile);
            g.setColor(tile.getColorObject());
            g.fill(r);
        }
        g.setColor(Color.BLACK);
        g.draw(r);
        return tile;
    }
    
    private void findRect(int x, int y)
    {
        mLastClickedRect = null;
        for (Rectangle r : mLocMap.keySet())
            if (r.contains(x, y))
                if ((mLastClickedRect == null) || (mLastClickedRect.width
                        * mLastClickedRect.height > r.width * r.height))
                    mLastClickedRect = r;
        mLastClickedTile = mTileMap.get(mLastClickedRect);
        mLastClickedLocation = (mLastClickedRect == null) ? null
                : mLocMap.get(mLastClickedRect);
    }

    private void doMouseMoved(MouseEvent e)
    {
        findRect(e.getX(), e.getY());
        if (mLastClickedTile == null)
            if (mLastClickedLocation == null)
                RuntimeLogic.status("");
            else
                RuntimeLogic.status("f:" + mLastClickedLocation[0] + " "
                        + mLastClickedLocation[2] + ","
                        + mLastClickedLocation[1]);
        else if (mLastClickedTile instanceof PSquare)
        {
            PSquare l = (PSquare)mLastClickedTile;
            RuntimeLogic.status((StringUtils.isTrivial(l.getName()) ? l.getID()
                    : l.getName()) + " f:" + mLastClickedLocation[0] + " "
                    + mLastClickedLocation[2] + "," + mLastClickedLocation[1]);
        }
        else
        {
            PApature a = (PApature)mLastClickedTile;
            RuntimeLogic.status((StringUtils.isTrivial(a.getName()) ? a.getID()
                    : a.getName()) + " f:" + mLastClickedLocation[0] + " "
                    + mLastClickedLocation[2] + "," + mLastClickedLocation[1]);
        }
    }

    private void doMouseClicked(MouseEvent e)
    {
        findRect(e.getX(), e.getY());
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            if (e.getClickCount() == 2)
            {
                if ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0)
                {
                    if (mLastClickedLocation != null)
                    {
                        mLastSetTile = getLocation(mLastClickedLocation[0],mLastClickedLocation[1],mLastClickedLocation[2]);
                        RuntimeLogic.status("Copied '" + mLastSetTile + "'");
                    }
                }
                else
                {
                    if ((mLastSetTile != null)
                            && (mLastClickedLocation != null))
                        EditorHouseLogic.setTile(mLastClickedLocation[0],
                                mLastClickedLocation[1],
                                mLastClickedLocation[2], mLastSetTile);
                }
            }
            else if (mLastClickedTile instanceof PSquare)
                EditorSquareLogic.select((PSquare)mLastClickedTile);
            else if (mLastClickedTile instanceof PApature)
                EditorApatureLogic.select((PApature)mLastClickedTile);
        }
        else if (e.getButton() == MouseEvent.BUTTON2)
            doShowPopup(e);
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

    private void doShowPopup(MouseEvent e)
    {
        findRect(e.getX(), e.getY());
        Class<?> type = null;
        if (mLastClickedLocation != null)
            type = inferType();
        else if (mLastClickedTile != null)
            type = mLastClickedTile.getClass();
        System.out.println("Popup type=" + type);
        if (type == null)
            makeBackPopup().show(e.getComponent(), e.getX(), e.getY());
        else if (type == PApature.class)
            makeApaturePopup().show(e.getComponent(), e.getX(), e.getY());
        else if (type == PLocation.class)
            makeLocationPopup().show(e.getComponent(), e.getX(), e.getY());
    }

    public Class<?> inferType()
    {
        Class<?> type;
        if (mLastClickedLocation[0] % 2 == 1)
            type = PApature.class;
        else if ((mLastClickedLocation[1] % 2 == 1)
                && (mLastClickedLocation[2] % 2 == 1))
            type = PSquare.class;
        else
            type = PApature.class;
        return type;
    }

    private void doRemoveTile()
    {
        if (mLastClickedLocation == null)
            return;
        EditorHouseLogic.removeTile(mLastClickedLocation[0],
                mLastClickedLocation[1], mLastClickedLocation[2]);
    }

    private void doSetTile()
    {
        if (mLastClickedLocation == null)
            return;
        PLocation[] choices;
        Class<?> type = inferType();
        if (type == PSquare.class)
            choices = mSquares.values().toArray(new PLocation[0]);
        else
            choices = mApatures.values().toArray(new PApature[0]);
        PLocation choice = (PLocation)JOptionPane.showInputDialog(this,
                "Choose type", "Set Map Value", JOptionPane.QUESTION_MESSAGE,
                null, choices, // Array of choices
                mLastClickedTile); // Initial choice
        if (choice == null)
            return;
        mLastSetTile = choice;
        EditorHouseLogic.setTile(mLastClickedLocation[0],
                mLastClickedLocation[1], mLastClickedLocation[2], mLastSetTile);
    }

    private void doNewTile()
    {
        if (mLastClickedLocation == null)
            return;
        String newTileID = (String)JOptionPane.showInputDialog(this, "New Tile",
                "Create New Tile", JOptionPane.QUESTION_MESSAGE, null, null,
                "NEW_TILE");
        if (newTileID == null)
            return;
        Class<?> type = inferType();
        PLocation tile;
        if (type == PLocation.class)
            tile = EditorSquareLogic.newSquare(newTileID);
        else
            tile = EditorApatureLogic.newApature(newTileID);
        if (tile != null)
            EditorHouseLogic.setTile(mLastClickedLocation[0],
                    mLastClickedLocation[1], mLastClickedLocation[2],
                    mLastSetTile);
    }

    private void doAddEdge()
    {
        if (mLastClickedLocation == null)
            return;
        EditorHouseLogic.addRoofEdge(mLastClickedLocation[0]);
    }
}
