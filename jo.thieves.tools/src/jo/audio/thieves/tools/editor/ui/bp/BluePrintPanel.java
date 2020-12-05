package jo.audio.thieves.tools.editor.ui.bp;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLocation;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorHouseLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.ui.swing.utils.MouseUtils;
import jo.util.utils.obj.IntegerUtils;

public class BluePrintPanel extends JComponent
{
    int                       ICON_SIZE      = 32;
    int                       DOOR_WIDTH     = 4;

    static int                MODE_NONE      = 0;
    static int                MODE_INSERT    = 1;
    static int                MODE_DEL       = 2;

    Dimension                 mSize;
    PTemplate                 mHouse;
    Map<String, PSquare>      mSquareIndex;
    Map<String, PApature>     mApatureIndex;
    Map<String, PLocationRef> mLocations     = new HashMap<>();
    int[][]                   mBounds;
    int[][]                   mSquareBounds;
    int                       mNumFloors;
    int                       mTilesWide;
    int                       mTilesHigh;
    Font                      mBaseFont;
    int[][]                   mOrigins;
    List<PolySquare>          mSquares         = new ArrayList<>();
    List<PolyApature>         mApatures         = new ArrayList<>();
    PolySquare                mHoverTile;
    PolyApature               mHoverApature;
    List<PolySelect>          mSelectors = new ArrayList<>();
    int                       mSelectorIndex = -1;
    int[]                     mHoverSquare;

    int                       mMode          = MODE_NONE;
    Rectangle                 mModeButton;
    Rectangle                 mActionButton;

    private JPopupMenu        mApaturePopup;
    private JMenuItem         mDigDown;
    private JMenuItem         mDigUp;
    private JMenuItem         mClearDown;
    private JMenuItem         mClearUp;

    public BluePrintPanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mApaturePopup = new JPopupMenu();
        mDigDown = new JMenuItem("Dig Down...");
        mDigUp = new JMenuItem("Dig Up...");
        mClearDown = new JMenuItem("Clear Down");
        mClearUp = new JMenuItem("Clear Up");
    }

    private void initLayout()
    {
        mApaturePopup.add(mDigDown);
        mApaturePopup.add(mDigUp);
        mApaturePopup.add(mClearDown);
        mApaturePopup.add(mClearUp);
    }

    private void initLink()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.listen("selectedHouse", (ov,nv) -> repaint());
        es.listen("location.floor", (ov,nv) -> repaint());
        es.listen("location.tile", (ov,nv) -> repaint());
        MouseUtils.mouseMoved(this, (e) -> BluePrintMouseLogic.doMouseMoved(e));
        MouseUtils.mouseClicked(this,
                (e) -> BluePrintMouseLogic.doMouseClicked(e));
        MouseUtils.mousePressed(this,
                (e) -> BluePrintMouseLogic.doMousePressed(e));
        MouseUtils.mouseWheelMoved(this,
                (e) -> BluePrintMouseLogic.doMouseWheelMoved(e));
        ListenerUtils.listen(mDigDown, (e) -> doDigDir(e, -1));
        ListenerUtils.listen(mDigUp, (e) -> doDigDir(e, 1));
        ListenerUtils.listen(mClearDown, (e) -> EditorHouseLogic.removeTile(e.getActionCommand()));
        ListenerUtils.listen(mClearUp, (e) -> EditorHouseLogic.removeTile(e.getActionCommand()));
    }

    @Override
    public void paint(Graphics g1)
    {
        BluePrintPaintLogic.paint(this, g1);
    }

    PolySquare findTile(int x, int y)
    {
        for (PolySquare tile : mSquares)
            if (tile.toPolygon().contains(x, y))
                return tile;
        return null;
    }

    PolyApature findApature(int x, int y)
    {
        for (PolyApature tile : mApatures)
            if (tile.mRect.contains(x, y))
                return tile;
        return null;
    }

    void doPopUp(Point point, PLocationRef here, PLocationRef up,
            PLocationRef down)
    {
        mDigUp.setEnabled(up.getID() == null);
        mDigUp.setActionCommand(up.toKey());
        mDigDown.setEnabled(down.getID() == null);
        mDigDown.setActionCommand(down.toKey());
        mClearUp.setEnabled(up.getID() != null);
        mClearUp.setActionCommand(up.toKey());
        mClearDown.setEnabled(down.getID() != null);
        mClearDown.setActionCommand(down.toKey());
        mApaturePopup.show(this, point.x, point.y);
    }
    
    private void doDigDir(ActionEvent e, int dz)
    {
        PApature[] choices = EditorSettingsLogic.getInstance().getLibrary().getApatures().values().toArray(new PApature[0]);
        PLocation choice = (PLocation)JOptionPane.showInputDialog(this,
                "Choose type", "Set Apature Type", JOptionPane.QUESTION_MESSAGE,
                null, choices, // Array of choices
                EditorSettingsLogic.getInstance().getSelectedApature()); // Initial choice
        if (choice == null)
            return;
        String[] ks = e.getActionCommand().split(",");
        int z = IntegerUtils.parseInt(ks[2]);
        int y = IntegerUtils.parseInt(ks[1]);
        int x = IntegerUtils.parseInt(ks[0]);
        EditorHouseLogic.setTile(z, y, x, choice);
        PLocationRef sq = mHouse.getLocation(x, y, z+dz);
        if (sq == null)
            EditorHouseLogic.setTile(z+dz, y, x, mSquareIndex.get("HALL"));        
    }
}
