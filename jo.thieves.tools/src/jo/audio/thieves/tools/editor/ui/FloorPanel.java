package jo.audio.thieves.tools.editor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.data.PHouse;
import jo.audio.thieves.tools.editor.data.PLocation;
import jo.audio.thieves.tools.editor.data.PTile;
import jo.audio.thieves.tools.editor.logic.EditorHouseLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.audio.thieves.tools.editor.logic.EditorTileLogic;
import jo.audio.thieves.tools.logic.RuntimeLogic;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

@SuppressWarnings("serial")
public class FloorPanel extends JComponent
{
    private static int      ICON_SIZE = 32;
    private static int      DOOR_WIDTH = 4;
    private static int      DOOR_HEIGHT = 24;

    private Dimension             mSize;
    private PLocation             mLocation;
    private PHouse                mHouse;
    private int                   mNumFloors;
    private int                   mTilesWide;
    private int                   mTilesHigh;
    private Map<Rectangle, int[]> mLocMap  = new HashMap<>();
    private Map<Rectangle, PTile> mTileMap  = new HashMap<>();
    private Rectangle             mLastClickedRect;
    private PTile                 mLastClickedTile;
    private int[]                 mLastClickedLocation;
    private Character             mLastSetTile = null;
    
    private JPopupMenu            mBackPopup;
    private JPopupMenu            mApaturePopup;
    private JPopupMenu            mLocationPopup;
    
    private JMenuItem             mNewTile;
    private JMenuItem             mSetTile;
    private JMenuItem             mRemoveTile;
    private JMenuItem             mAddFloor;
    private JMenuItem             mRemoveFloor;
    private JMenuItem             mSetWidth;
    private JMenuItem             mSetHeight;
    private JMenuItem             mAddBorder;
    private JMenuItem             mAddEdge;
    private JMenuItem             mCleanup;
    
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
        mAddFloor = new JMenuItem("Add Floor");
        mRemoveFloor = new JMenuItem("Remove Floor");
        mSetWidth = new JMenuItem("Set Width...");
        mSetHeight = new JMenuItem("Set Height...");
        mAddBorder = new JMenuItem("Add Border");
        mAddEdge = new JMenuItem("Add Roof Edge");
        mCleanup = new JMenuItem("Cleanup");
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
        mRemoveTile.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doRemoveTile();
            }
        });
        mSetTile.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSetTile();
            }
        });
        mNewTile.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doNewTile();
            }
        });
        mSetWidth.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSetWidth();
            }
        });
        mSetHeight.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSetHeight();
            }
        });
        mAddFloor.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                EditorHouseLogic.addFloor();
            }
        });
        mRemoveFloor.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                EditorHouseLogic.removeFloor();
            }
        });
        mAddBorder.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doAddBorder();
            }
        });
        mAddEdge.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doAddEdge();
            }
        });
        mCleanup.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doCleanup();
            }
        });
    }
    
    private JPopupMenu makeLocationPopup()
    {
        mLocationPopup = new JPopupMenu();
        mLocationPopup.add(mSetTile);
        mLocationPopup.add(mRemoveTile);
        mLocationPopup.add(mNewTile);
        mLocationPopup.add(mAddBorder);
        mLocationPopup.add(mAddEdge);
        mLocationPopup.add(mCleanup);
        return mLocationPopup;
    }
    
    private JPopupMenu makeApaturePopup()
    {
        mApaturePopup = new JPopupMenu();
        mApaturePopup.add(mSetTile);
        mApaturePopup.add(mRemoveTile);
        mApaturePopup.add(mNewTile);
        mApaturePopup.add(mAddBorder);
        mApaturePopup.add(mAddEdge);
        mApaturePopup.add(mCleanup);
        return mApaturePopup;
    }
    
    private JPopupMenu makeBackPopup()
    {
        mBackPopup = new JPopupMenu();
        mBackPopup.add(mSetWidth);
        mBackPopup.add(mSetHeight);
        mBackPopup.add(mAddFloor);
        mBackPopup.add(mRemoveFloor);
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
        mLocation =  es.getSelectedLocation();
        mHouse = es.getSelectedHouse();
        if ((mHouse == null) || (mHouse.getFloors().length == 0))
            return;
        mNumFloors = (mHouse.getFloors().length + 1) / 2;
        mTilesHigh = (mHouse.getFloors()[0].length - 1) / 2;
        mTilesWide = (mHouse.getFloors()[0][0].length - 1) / 2;
        g.setColor(Color.BLACK);
        g.drawString("Floors: "+mNumFloors+", Width: "+mTilesWide+", Height: "+mTilesHigh+", Area: "+mNumFloors*mTilesWide*mTilesHigh, 
                16, 32);
        int floorsWide = (int)Math.ceil(Math.sqrt(mNumFloors));
        int dx = mSize.width / floorsWide;
        int dy = mTilesHigh*ICON_SIZE + 3*ICON_SIZE;
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

    private void paintFloor(Graphics2D g, int ox, int oy, int f)
    {
        g.drawString(String.valueOf(f+1), ox-16, oy);
        char[][] floor = mHouse.getFloors()[f*2];
        paintLocations(g, ox, oy, f, floor);
        paintGrid(g, ox, oy);
        paintVerticalDoors(g, ox, oy, f, floor);
        paintHorizontalDoors(g, ox, oy, f, floor);
        paintUpDoors(g, ox, oy, f);
        paintDownDoors(g, ox, oy, f);
    }

    public void paintUpDoors(Graphics2D g, int ox, int oy, int f)
    {
        if (f*2 + 1 >= mHouse.getFloors().length)
            return;
        char[][] floor = mHouse.getFloors()[f*2 + 1];
        for (int x = 0; x < mTilesWide; x++)
            for (int y = 0; y < mTilesHigh; y++)
            {
                Rectangle r = new Rectangle(ox + x * ICON_SIZE + DOOR_WIDTH,
                        oy + y * ICON_SIZE + DOOR_WIDTH, DOOR_WIDTH*2, DOOR_WIDTH*2);
                int[] fyx = new int[] { f*2 + 1, y * 2 + 1, x * 2 + 1 };
                paintDoor(g, floor, r, fyx, true);
            }
    }

    public void paintDownDoors(Graphics2D g, int ox, int oy, int f)
    {
        if (f*2 - 1 < 0)
            return;
        char[][] floor = mHouse.getFloors()[f*2 - 1];
        for (int x = 0; x < mTilesWide; x++)
            for (int y = 0; y < mTilesHigh; y++)
            {
                Rectangle r = new Rectangle(ox + (x+1) * ICON_SIZE - DOOR_WIDTH*3,
                        oy + (y+1) * ICON_SIZE - DOOR_WIDTH*3, DOOR_WIDTH*2, DOOR_WIDTH*2);
                int[] fyx = new int[] { f*2 - 1, y * 2 + 1, x * 2 + 1 };
                paintDoor(g, floor, r, fyx, true);
            }
    }

    public void paintHorizontalDoors(Graphics2D g, int ox, int oy, int f,
            char[][] floor)
    {
        for (int x = 0; x < mTilesWide; x++)
            for (int y = 0; y <= mTilesHigh; y++)
            {
                Rectangle leftr = new Rectangle(ox + x * ICON_SIZE + (ICON_SIZE - DOOR_HEIGHT)/2,
                        oy + y * ICON_SIZE - DOOR_WIDTH/2 , DOOR_HEIGHT, DOOR_WIDTH);
                int[] fyx = new int[] { f*2, y * 2, x * 2 + 1 };
                paintDoor(g, floor, leftr, fyx, false);
            }
    }

    public void paintVerticalDoors(Graphics2D g, int ox, int oy, int f,
            char[][] floor)
    {
        for (int x = 0; x <= mTilesWide; x++)
            for (int y = 0; y < mTilesHigh; y++)
            {
                Rectangle leftr = new Rectangle(ox + x * ICON_SIZE - DOOR_WIDTH/2,
                        oy + y * ICON_SIZE + (ICON_SIZE - DOOR_HEIGHT)/2, DOOR_WIDTH, DOOR_HEIGHT);
                int[] fyx = new int[] { f*2, y * 2 + 1, x * 2 };
                paintDoor(g, floor, leftr, fyx, false);
            }
    }

    public void paintDoor(Graphics2D g, char[][] floor, Rectangle r, int[] fyx, boolean trapdoor)
    {
        mLocMap.put(r, fyx);
        String id = String.valueOf(floor[fyx[1]][fyx[2]]);
        PTile tile = mLocation.getIDMap().get(id);
        if (tile != null)
        {
            mTileMap.put(r, tile);
            if (trapdoor)
            {
                if (!".".equals(tile.getID()))
                {
                    mTileMap.put(r, tile);
                    if (tile.getID().equals("EMPTY"))
                        g.setColor(Color.LIGHT_GRAY);
                    else
                        g.setColor(tile.getColor());
                    g.fill(r);
                    g.setColor(Color.BLACK);
                    g.draw(r);
                }
            }
            else
            {
                if (!"EMPTY".equals(tile.getID()))
                {
                    mTileMap.put(r, tile);
                    if (tile.getID().equals("EXIT"))
                        g.setColor(Color.GREEN);
                    else if (tile.getID().equals("."))
                        g.setColor(Color.BLACK);
                    else
                        g.setColor(tile.getColor());
                    g.fill(r);
                    g.setColor(Color.BLACK);
                    g.draw(r);
                }
            }
        }
        else if (!".".equals(id))
            System.err.println("Unmapped tile '"+id+"'");
    }

    public void paintLocations(Graphics2D g, int ox, int oy, int f,
            char[][] floor)
    {
        for (int x = 0; x < mTilesWide; x++)
            for (int y = 0; y < mTilesHigh; y++)
            {
                Rectangle r = new Rectangle(ox + x * ICON_SIZE,
                        oy + y * ICON_SIZE, ICON_SIZE, ICON_SIZE);
                int[] fyx = new int[] { f*2, y * 2 + 1, x * 2 + 1 };
                mLocMap.put(r, fyx);
                String id = String.valueOf(floor[y*2 + 1][x*2 + 1]);
                PTile tile = mLocation.getIDMap().get(id);
                if (tile != null)
                {
                    mTileMap.put(r, tile);
                    g.setColor(tile.getColor());
                    g.fill(r);
                }
                else if (!".".equals(id))
                    System.err.println("Unmapped tile '"+id+"'");
            }
    }

    public void paintGrid(Graphics g, int ox, int oy)
    {
        g.setColor(Color.BLACK);
        for (int x = 0; x <= mTilesWide; x++)
            g.drawLine(ox + x * ICON_SIZE, oy, ox + x * ICON_SIZE,
                    oy + mTilesHigh * ICON_SIZE);
        for (int y = 0; y <= mTilesHigh; y++)
            g.drawLine(ox, oy + y * ICON_SIZE, ox + mTilesWide * ICON_SIZE,
                    oy + y * ICON_SIZE);
    }
    
    private void findRect(int x, int y)
    {
        mLastClickedRect = null;
        for (Rectangle r : mLocMap.keySet())
            if (r.contains(x, y))
                if ((mLastClickedRect == null) || (mLastClickedRect.width*mLastClickedRect.height > r.width*r.height))
                    mLastClickedRect = r;
        mLastClickedTile = mTileMap.get(mLastClickedRect);
        mLastClickedLocation = (mLastClickedRect == null) ? null : mLocMap.get(mLastClickedRect);
    }

    private void doMouseMoved(MouseEvent e)
    {
        findRect(e.getX(), e.getY());
        if (mLastClickedTile == null)
            if (mLastClickedLocation == null)
                RuntimeLogic.status("");
            else
                RuntimeLogic.status("f:"+mLastClickedLocation[0]+" "+mLastClickedLocation[2]+","+mLastClickedLocation[1]);
        else
            RuntimeLogic.status((StringUtils.isTrivial(mLastClickedTile.getName()) ? mLastClickedTile.getID() : mLastClickedTile.getName())
                    +" f:"+mLastClickedLocation[0]+" "+mLastClickedLocation[2]+","+mLastClickedLocation[1]);
    }
    private void doMouseClicked(MouseEvent e)
    {
        findRect(e.getX(), e.getY());
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            if (e.getClickCount() == 2)
            {
                if ((e.getModifiers()&ActionEvent.SHIFT_MASK)!=0)
                {
                    if (mLastClickedLocation != null)
                    {
                        mLastSetTile = mHouse.getFloors()[mLastClickedLocation[0]][mLastClickedLocation[1]][mLastClickedLocation[2]];
                        RuntimeLogic.status("Copied '"+mLastSetTile+"'");
                    }
                }
                else
                {
                    if ((mLastSetTile != null) && (mLastClickedLocation != null))
                        EditorHouseLogic.setTile(mLastClickedLocation[0], mLastClickedLocation[1], mLastClickedLocation[2], mLastSetTile);
                }
            }
            else
                EditorTileLogic.select(mLastClickedTile);
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
        DOOR_HEIGHT = DOOR_WIDTH*6;
        ICON_SIZE = DOOR_WIDTH*8;
        repaint();
    }
    private void doShowPopup(MouseEvent e)
    {
        findRect(e.getX(), e.getY());
        int type = 0;
        if (mLastClickedLocation != null)
            type = inferType();
        else if (mLastClickedTile != null)
            type = mLastClickedTile.getType();
        System.out.println("Popup type="+type);
        if (type == 0)
            makeBackPopup().show(e.getComponent(), e.getX(), e.getY());
        else if (type == PTile.APATURE)
            makeApaturePopup().show(e.getComponent(), e.getX(), e.getY());
        else if (type == PTile.LOCATION)
            makeLocationPopup().show(e.getComponent(), e.getX(), e.getY());
    }

    public int inferType()
    {
        int type;
        if (mLastClickedLocation[0]%2 == 1)
            type = PTile.APATURE;
        else if ((mLastClickedLocation[1]%2 == 1) && (mLastClickedLocation[2]%2 == 1))
            type = PTile.LOCATION;
        else
            type = PTile.APATURE;
        return type;
    }
    
    private void doRemoveTile()
    {
        if (mLastClickedLocation == null)
            return;
        EditorHouseLogic.removeTile(mLastClickedLocation[0], mLastClickedLocation[1], mLastClickedLocation[2]);
    }
    
    private void doSetTile()
    {
        if (mLastClickedLocation == null)
            return;
        PTile[] choices;
        int type = inferType();
        if (type == PTile.LOCATION)
            choices = mLocation.getLocations().values().toArray(new PTile[0]);
        else
            choices = mLocation.getApatures().values().toArray(new PTile[0]);
        PTile choice = (PTile)JOptionPane.showInputDialog(this, "Choose type",
            "Set Map Value", JOptionPane.QUESTION_MESSAGE, null,
            choices, // Array of choices
            mLastClickedTile); // Initial choice
        if (choice == null)
            return;
        mLastSetTile = choice.getChar().charAt(0);
        EditorHouseLogic.setTile(mLastClickedLocation[0], mLastClickedLocation[1], mLastClickedLocation[2], mLastSetTile);
    }
    
    private void doNewTile()
    {
        if (mLastClickedLocation == null)
            return;
        String newTileID = (String)JOptionPane.showInputDialog(this, "New Tile", "Create New Tile",
                JOptionPane.QUESTION_MESSAGE, null, null, "NEW_TILE");
        if (newTileID == null)
            return;
        PTile tile = EditorTileLogic.newTile(newTileID, inferType());
        if (tile != null)
            EditorHouseLogic.setTile(mLastClickedLocation[0], mLastClickedLocation[1], mLastClickedLocation[2], mLastSetTile);
    }
    
    private void doSetWidth()
    {
        String oldWidthS = String.valueOf(mTilesWide);
        String newWidthS = (String)JOptionPane.showInputDialog(this, "New Width", "Set Floor Width",
                JOptionPane.QUESTION_MESSAGE, null, null, oldWidthS);
        if (newWidthS == null)
            return;
        int newWidth = IntegerUtils.parseInt(newWidthS);
        if (newWidth == mTilesWide)
            return;
        EditorHouseLogic.setWidth(newWidth);
    }
    
    private void doSetHeight()
    {
        String oldHeightS = String.valueOf(mTilesHigh);
        String newHeightS = (String)JOptionPane.showInputDialog(this, "New Height", "Set Floor Height",
                JOptionPane.QUESTION_MESSAGE, null, null, oldHeightS);
        if (newHeightS == null)
            return;
        int newHeight = IntegerUtils.parseInt(newHeightS);
        if (newHeight == mTilesHigh)
            return;
        EditorHouseLogic.setHeight(newHeight);
    }
    
    private void doAddBorder()
    {
        if (mLastClickedLocation == null)
            return;
        EditorHouseLogic.addBorder(mLastClickedLocation[0]);
    }
    
    private void doAddEdge()
    {
        if (mLastClickedLocation == null)
            return;
        EditorHouseLogic.addRoofEdge(mLastClickedLocation[0]);
    }
    
    private void doCleanup()
    {
        if (mLastClickedLocation == null)
            return;
        EditorHouseLogic.cleanup(mLastClickedLocation[0]);
    }
}
