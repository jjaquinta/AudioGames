package jo.audio.thieves.tools.editor.ui.bp;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.util.ui.swing.utils.MouseUtils;

public class BluePrintPanel extends JComponent
{
    int                       ICON_SIZE   = 32;
    int                       DOOR_WIDTH  = 4;

    static int                MODE_NONE   = 0;
    static int                MODE_INSERT = 1;
    static int                MODE_DEL    = 2;

    Dimension                 mSize;
    PTemplate                 mHouse;
    Map<String, PSquare>      mSquareIndex;
    Map<String, PApature>     mApatureIndex;
    Map<String, PLocationRef> mLocations  = new HashMap<>();
    int[][]                   mBounds;
    int[][]                   mSquareBounds;
    int                       mNumFloors;
    int                       mTilesWide;
    int                       mTilesHigh;
    Font                      mBaseFont;
    int[][]                   mOrigins;
    List<PolyTile>            mTiles      = new ArrayList<>();
    PolyTile                  mHoverTile;
    int[]                     mHoverSquare;

    int                       mMode       = MODE_NONE;
    Rectangle                 mModeButton;

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
//                System.out.println("pcl repaint");
                repaint();
            }
        };
        es.addPropertyChangeListener("selectedHouse", pcl);
        es.addPropertyChangeListener("location.floor", pcl);
        es.addPropertyChangeListener("location.tile", pcl);
        MouseUtils.mouseMoved(this, (e) -> BluePrintMouseLogic.doMouseMoved(e));
        MouseUtils.mouseClicked(this, (e) -> BluePrintMouseLogic.doMouseClicked(e));
        MouseUtils.mouseWheelMoved(this, (e) -> BluePrintMouseLogic.doMouseWheelMoved(e));
    }

    @Override
    public void paint(Graphics g1)
    {
        BluePrintPaintLogic.paint(this, g1);
    }

    PolyTile findTile(int x, int y)
    {
        for (PolyTile tile : mTiles)
            if (tile.toPolygon().contains(x, y))
                return tile;
        return null;
    }
}
