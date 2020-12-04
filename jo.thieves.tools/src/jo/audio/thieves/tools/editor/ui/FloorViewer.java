package jo.audio.thieves.tools.editor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.data.template.PLocation;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorApatureLogic;
import jo.audio.thieves.tools.editor.logic.EditorHouseLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.audio.thieves.tools.editor.logic.EditorSquareLogic;
import jo.audio.thieves.tools.logic.RuntimeLogic;
import jo.util.geom2d.Point2D;
import jo.util.geom2d.Polygon2D;
import jo.util.geom3d.Point3D;
import jo.util.geom3d.Polygon3D;
import jo.util.geom3d.Transform3D;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.ui.swing.utils.MouseUtils;
import jo.util.utils.MathUtils;
import jo.util.utils.obj.StringUtils;

@SuppressWarnings("serial")
public class FloorViewer extends JComponent
{
    private static int                   ICON_SIZE       = 32;
    private static int                   DOOR_WIDTH      = 6;
    private static int                   DOOR_HEIGHT     = 26;

    private Dimension                    mSize;
    private PTemplate                    mHouse;
    private Map<String, PSquare>         mSquareIndex;
    private Map<String, PApature>        mApatureIndex;
    private Map<String, PLocationRef>    mLocations      = new HashMap<>();
    private int[][]                      mBounds;
    private Map<Polygon, PLocation>      mTileMap        = new HashMap<>();
    private int                          mPolyStackSig   = 0;
    private Map<Polygon3D, PLocationRef> mRawPolyIndex   = new HashMap<>();
    private Map<Polygon2D, PLocationRef> mTransPolyIndex = new HashMap<>();
    private List<Polygon2D>              mTransPolyStack = new ArrayList<>();
    private int                          mOriginX;
    private int                          mOriginY;
    private int                          mTransformSig   = 0;
    private Transform3D                  mTransform      = new Transform3D();
    private Integer                      mSingleFloor;
    private Polygon2D                    mLastClickedPoly;
    private PLocationRef                 mLastClickedTile;

    private Point                        mMouseDown;

    private JPopupMenu                   mBackPopup;
    private JPopupMenu                   mApaturePopup;
    private JPopupMenu                   mLocationPopup;

    private JMenuItem                    mNewTile;
    private JMenuItem                    mSetTile;
    private JMenuItem                    mRemoveTile;
    private JMenuItem                    mAddEdge;

    public FloorViewer()
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
        mTransform.rotate(1, 0, 1, 45 / 180 * Math.PI);
    }

    private void initLayout()
    {
    }

    private void initLink()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.listen("selectedHouse", (ov, nv) -> doNewHouse());
        es.listen("location.floor,location.tile", (ov, nv) -> repaint());
        MouseUtils.mouseMoved(this, (e) -> doMouseMoved(e));
        MouseUtils.mouseDragged(this, (e) -> doMouseDragged(e));
        MouseUtils.mouseClicked(this, (e) -> doMouseClicked(e));
        MouseUtils.mousePressed(this, (e) -> doMousePressed(e));
        MouseUtils.mouseReleased(this, (e) -> doMouseReleased(e));
        MouseUtils.mouseWheelMoved(this, (e) -> doMouseWheelMoved(e));
        ListenerUtils.listen(mRemoveTile, (e) -> doRemoveTile());
        ListenerUtils.listen(mNewTile, (e) -> doNewTile());
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
        mTileMap.clear();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, mSize.width, mSize.height);
        updateInfo();
        if ((mHouse == null) || (mLocations.size() == 0))
            return;
        mBounds = EditorHouseLogic.getBoundary(mLocations.keySet());
        buildRawPolyStack();
        buildTransPolyStack();
        int zsize = (mBounds[1][2] - mBounds[0][2] + 1) / 2;
        int ysize = (mBounds[1][1] - mBounds[0][1] - 1) / 2;
        int xsize = (mBounds[1][0] - mBounds[0][0] - 1) / 2;
        g.setColor(Color.BLACK);
        g.drawString("Floors: " + zsize + ", Width: " + xsize + ", Height: "
                + ysize + ", Area: " + zsize * xsize * ysize, 16, 32);
        if (mSingleFloor != null)
            g.drawString("Showing floor " + mSingleFloor, 16, 48);
        mOriginX = mSize.width / 2;
        mOriginY = mSize.height / 2;
        for (Polygon2D p : mTransPolyStack)
        {
            PLocationRef tile = mTransPolyIndex.get(p);
            if (mSingleFloor != null)
            {
                if (!mSingleFloor.equals(tile.getZ()))
                    continue;
            }
            paintFacet(g, p, tile);
        }
    }

    private void updateInfo()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLibrary lib = es.getLibrary();
        mSquareIndex = lib.getSquares();
        mApatureIndex = lib.getApatures();
        mHouse = es.getSelectedHouse();
        mLocations = mHouse.getLocations();
    }

    private Polygon toAWT(Polygon2D p3d)
    {
        int[] xpoints = new int[p3d.size()];
        int[] ypoints = new int[p3d.size()];
        for (int i = 0; i < xpoints.length; i++)
        {
            Point2D p = p3d.p(i);
            xpoints[i] = (int)p.x + mOriginX;
            ypoints[i] = (int)p.y + mOriginY;
        }
        return new Polygon(xpoints, ypoints, xpoints.length);
    }
    
    private PLocation dereference(PLocationRef ref)
    {
        if (mApatureIndex.containsKey(ref.getID()))
            return mApatureIndex.get(ref.getID());
        else if (mSquareIndex.containsKey(ref.getID()))
            return mSquareIndex.get(ref.getID());
        throw new IllegalArgumentException();
    }

    private void paintFacet(Graphics2D g, Polygon2D p3d, PLocationRef ref)
    {
        PLocation tile = dereference(ref);
        Polygon p2d = toAWT(p3d);
        mTileMap.put(p2d, tile);
        if (!"EMPTY".equals(tile.getID()))
        {
            g.setColor(tile.getColorObject());
            g.fill(p2d);
        }
        g.setColor(Color.BLACK);
        g.draw(p2d);
    }

    private void findPoly(int x, int y)
    {
        x -= mOriginX;
        y -= mOriginY;
        mLastClickedPoly = null;
        for (int i = mTransPolyStack.size() - 1; i >= 0; i--)
        {
            Polygon2D p = mTransPolyStack.get(i);
            if (p.contains(x, y))
            {
                mLastClickedPoly = p;
                break;
            }
        }
        mLastClickedTile = mTransPolyIndex.get(mLastClickedPoly);
    }

    private void doNewHouse()
    {
        mTransform = new Transform3D();
        int[][] bounds = EditorHouseLogic.getBoundary();
        if (bounds == null)
            return;
        int ox = (bounds[0][0] + bounds[1][0]) / 2;
        int oy = (bounds[0][1] + bounds[1][1]) / 2;
        int oz = (bounds[0][2] + bounds[1][2]) / 2;
        mTransform.translate(-ox * ICON_SIZE / 2, -oy * ICON_SIZE / 2,
                -oz * ICON_SIZE / 2);
        repaint();
    }

    private void doMouseMoved(MouseEvent e)
    {
        findPoly(e.getX(), e.getY());
        if (mLastClickedTile == null)
            RuntimeLogic.status("");
        else
        {
            PLocation tile = dereference(mLastClickedTile);
            RuntimeLogic
                    .status((StringUtils.isTrivial(tile.getName())
                            ? tile.getID()
                            : tile.getName()) + " @" + mLastClickedTile.getX()+","+mLastClickedTile.getY()+" f:"+mLastClickedTile.getZ())
                    ;
        }
    }

    private void doMouseClicked(MouseEvent e)
    {
        findPoly(e.getX(), e.getY());
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            PLocation tile = dereference(mLastClickedTile);
            if (tile instanceof PSquare)
                EditorSquareLogic.select((PSquare)tile);
            else if (tile instanceof PApature)
                EditorApatureLogic.select((PApature)tile);
        }
        else if (e.getButton() == MouseEvent.BUTTON3)
            doShowPopup(e);
        else if (e.getButton() == MouseEvent.BUTTON2)
        {
            if (mSingleFloor == null)
                mSingleFloor = 0;
            else
                mSingleFloor = null;
            repaint();
        }
    }

    private void doMousePressed(MouseEvent e)
    {
        if (e.isPopupTrigger())
            doShowPopup(e);
        else
            mMouseDown = e.getPoint();
    }

    private void doMouseReleased(MouseEvent e)
    {
        if (e.isPopupTrigger())
            doShowPopup(e);
        else
        {
            doMouseMoved(e);
            mMouseDown = null;
        }
    }

    private void doMouseDragged(MouseEvent e)
    {
        if (mMouseDown == null)
            return;
        Point newDown = e.getPoint();
        int dx = newDown.x - mMouseDown.x;
        int dy = newDown.y - mMouseDown.y;
        mMouseDown = newDown;
        mTransform.yrot(-dx / 10.0 * Math.PI);
        mTransform.xrot(-dy / 10.0 * Math.PI);
        repaint();
    }

    public void doMouseWheelMoved(MouseWheelEvent e)
    {
        int delta = e.getWheelRotation();
        if (mSingleFloor == null)
            mTransform.scale(1 + .1f * Math.signum(delta));
        else
        {
            mSingleFloor += delta;
            if (mSingleFloor < mBounds[0][2])
                mSingleFloor = mBounds[0][2];
            else if (mSingleFloor > mBounds[1][2])
                mSingleFloor = mBounds[1][2];
        }
        repaint();
    }

    private void doShowPopup(MouseEvent e)
    {
        findPoly(e.getX(), e.getY());
        Class<?> type = null;
        if (mLastClickedTile != null)
            type = mLastClickedTile.getClass();
        System.out.println("Popup type=" + type);
        if (type == null)
            makeBackPopup().show(e.getComponent(), e.getX(), e.getY());
        else if (type == PApature.class)
            makeApaturePopup().show(e.getComponent(), e.getX(), e.getY());
        else if (type == PLocation.class)
            makeLocationPopup().show(e.getComponent(), e.getX(), e.getY());
    }

    private void doRemoveTile()
    {
        if (mLastClickedTile == null)
            return;
        EditorHouseLogic.removeTile(mLastClickedTile.getX(), mLastClickedTile.getY(), mLastClickedTile.getZ());
    }

    private void doNewTile()
    {
        /*
         * if (mLastClickedLocation == null) return; String newTileID =
         * (String)JOptionPane.showInputDialog(this, "New Tile",
         * "Create New Tile", JOptionPane.QUESTION_MESSAGE, null, null,
         * "NEW_TILE"); if (newTileID == null) return; Class<?> type =
         * inferType(); PLocation tile; if (type == PLocation.class) tile =
         * EditorSquareLogic.newSquare(newTileID); else tile =
         * EditorApatureLogic.newApature(newTileID); if (tile != null)
         * EditorHouseLogic.setTile(mLastClickedLocation[0],
         * mLastClickedLocation[1], mLastClickedLocation[2], mLastSetTile);
         */
    }

    private void buildRawPolyStack()
    {
        List<String> tags = new ArrayList<>();
        tags.addAll(mLocations.keySet());
        Collections.sort(tags);
        String sig = StringUtils.toCommaString(tags.toArray(new String[0]));
        if (sig.hashCode() == mPolyStackSig)
            return; // no change
        mRawPolyIndex.clear();
        for (String xyz : mLocations.keySet())
            addPoly(xyz, mLocations.get(xyz));
        mPolyStackSig = sig.hashCode();
        mTransformSig = 0;
    }

    private void buildTransPolyStack()
    {
        String sig = mTransform.toString();
        if (sig.hashCode() == mTransformSig)
            return; // no change
        mTransPolyIndex.clear();
        mTransPolyStack.clear();
        Map<Polygon2D, Double> centers = new HashMap<>();
        for (Polygon3D poly : mRawPolyIndex.keySet())
        {
            List<Point3D> ps = mTransform.transformNew(poly.getPoints());
            Polygon2D poly2 = new Polygon2D(ps.toArray(new Point2D[0]));
            mTransPolyIndex.put(poly2, mRawPolyIndex.get(poly));
            mTransPolyStack.add(poly2);
            double z = MathUtils.average(ps.get(0).z, ps.get(1).z, ps.get(2).z,
                    ps.get(3).z);
            centers.put(poly2, z);
        }
        mTransformSig = sig.hashCode();
        Collections.sort(mTransPolyStack, new Comparator<Polygon2D>() {
            @Override
            public int compare(Polygon2D o1, Polygon2D o2)
            {
                double z1 = centers.get(o1);
                double z2 = centers.get(o2);
                return (int)Math.signum(z2 - z1);
            }
        });
    }

    private int gridToPixel(int grid)
    {
        int pixel = (grid / 2) * ICON_SIZE;
        if (grid % 2 != 0)
            pixel += DOOR_WIDTH;
        return pixel;
    }

    private int gridToSize(int grid)
    {
        int pixel = (grid % 2 == 0) ? DOOR_WIDTH : DOOR_HEIGHT;
        return pixel;
    }

    private Point3D getBase(int x, int y, int z)
    {
        return new Point3D(gridToPixel(x), gridToPixel(y), gridToPixel(z + 1));
    }

    private Point3D getSize(int x, int y, int z)
    {
        return new Point3D(gridToSize(x), gridToSize(y), gridToSize(z + 1));
    }

    private void addPoly(String xyz, PLocationRef tile)
    {
        int type = PTemplate.getType(tile.getX(), tile.getY(), tile.getZ());
        if (type == PTemplate.NOTHING)
            System.out.println("Quack - "+tile.getID()+" "+tile.getX()+","+tile.getY()+","+tile.getZ());
        Point3D base = getBase(tile.getX(), tile.getY(), tile.getZ());
        Point3D size = getSize(tile.getX(), tile.getY(), tile.getZ());
        addFacet(base.x, base.y, base.z, size.x, size.y, 0, tile);
        addFacet(base.x, base.y, base.z + size.z, size.x, size.y, 0, tile);
        addFacet(base.x, base.y, base.z, size.x, 0, size.z, tile);
        addFacet(base.x, base.y + size.y, base.z, size.x, 0, size.z, tile);
        addFacet(base.x, base.y, base.z, 0, size.y, size.z, tile);
        addFacet(base.x + size.x, base.y, base.z, 0, size.y, size.z, tile);
    }

    private void addFacet(double x, double y, double z, double dx, double dy,
            double dz, PLocationRef tile)
    {
        Polygon3D p = new Polygon3D();
        p.getPoints().add(new Point3D(x, y, z));
        if (dz == 0)
        {
            p.getPoints().add(new Point3D(x + dx, y, z));
            p.getPoints().add(new Point3D(x + dx, y + dy, z));
            p.getPoints().add(new Point3D(x, y + dy, z));
        }
        else if (dy == 0)
        {
            p.getPoints().add(new Point3D(x, y, z + dz));
            p.getPoints().add(new Point3D(x + dx, y, z + dz));
            p.getPoints().add(new Point3D(x + dx, y, z));
        }
        else if (dx == 0)
        {
            p.getPoints().add(new Point3D(x, y + dy, z));
            p.getPoints().add(new Point3D(x, y + dy, z + dz));
            p.getPoints().add(new Point3D(x, y, z + dz));
        }
        mRawPolyIndex.put(p, tile);
    }
}
