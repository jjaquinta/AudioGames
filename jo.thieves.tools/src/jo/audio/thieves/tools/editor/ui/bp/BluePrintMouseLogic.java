package jo.audio.thieves.tools.editor.ui.bp;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorApatureLogic;
import jo.audio.thieves.tools.editor.logic.EditorHouseLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.audio.thieves.tools.editor.logic.EditorSquareLogic;
import jo.audio.thieves.tools.logic.RuntimeLogic;

public class BluePrintMouseLogic
{
    static void doMouseMoved(MouseEvent e)
    {
        BluePrintPanel panel = (BluePrintPanel)e.getSource();
        updateHoverTile(panel, e);
        updateHoverItem(panel, e);
        updateHoverSelector(panel, e);
//        System.out.println("mouse moved repaint");
        panel.repaint();
    }
    
    private static void updateHoverSelector(BluePrintPanel panel, MouseEvent e)
    {
        for (int i = 0; i < panel.mSelectors.size(); i++)
        {
            PolySelect ps = panel.mSelectors.get(i);
            if (ps.mRect.contains(e.getPoint()))
            {
                panel.mSelectorIndex = i;
                return;
            }
        }
        panel.mSelectorIndex = -1;
    }

    private static final int[][] INSERT_MATCH = { { 0, 0 }, { -2, 0 }, { 2, 0 },
            { 0, -2 }, { 0, 2 } };

    private static void updateHoverItem(BluePrintPanel panel, MouseEvent e)
    {
        if (EditorSettingsLogic.getInstance().getActionMode() == EditorSettings.ACTION_SQUARE)
            updateHoverSquare(panel, e);
        else if (EditorSettingsLogic.getInstance().getActionMode() == EditorSettings.ACTION_APATURE)
            updateHoverApature(panel, e);
    }

    private static int[] findClosestSquare(BluePrintPanel panel, MouseEvent e, int mode)
    {
        for (int floor = 0; floor < panel.mOrigins.length; floor++)
        {
            int x = (e.getX() - panel.mOrigins[floor][0]) / panel.ICON_SIZE * 2 + 1;
            int y = (e.getY() - panel.mOrigins[floor][1]) / panel.ICON_SIZE * 2 + 1;
            int z = panel.mBounds[0][2] + floor * 2;
            boolean match = false;
            if (mode == BluePrintPanel.MODE_INSERT)
            {
                for (int i = 0; i < INSERT_MATCH.length; i++)
                {
                    String k = (x + INSERT_MATCH[i][0]) + ","
                            + (y + INSERT_MATCH[i][1]) + "," + z;
                    // System.out.print(k+" ");
                    match = panel.mLocations.containsKey(k);
                    if (match)
                        break;
                }
                // System.out.println(match ? "match" : "no match");
            }
            else if (mode == BluePrintPanel.MODE_DEL)
            {
                String k = x + "," + y + "," + z;
                match = panel.mLocations.containsKey(k);
            }
            if (match)
            {
                return new int[] { x, y, z };
            }
        }
        return null;
    }
    
    private static void updateHoverSquare(BluePrintPanel panel, MouseEvent e)
    {
        if (panel.mMode == BluePrintPanel.MODE_NONE)
            panel.mHoverSquare = null;
        else
            panel.mHoverSquare = findClosestSquare(panel, e, panel.mMode);
    }

    private static void updateHoverApature(BluePrintPanel panel, MouseEvent e)
    {
        if (panel.mMode == BluePrintPanel.MODE_NONE)
        {
            panel.mHoverApature = null;
        }
    }

    private static void updateHoverTile(BluePrintPanel panel, MouseEvent e)
    {
        if (EditorSettingsLogic.getInstance().getActionMode() == EditorSettings.ACTION_SQUARE)
            updateHoverTileSquare(panel, e);
        else if (EditorSettingsLogic.getInstance().getActionMode() == EditorSettings.ACTION_APATURE)
            updateHoverTileApature(panel, e);
    }
    
    private static void updateHoverTileSquare(BluePrintPanel panel, MouseEvent e)
    {
        PolySquare over = panel.findTile(e.getX(), e.getY());
        if (PolySquare.equals(over, panel.mHoverTile))
            return;
        panel.mHoverTile = over;
        panel.mHoverApature = null;
        if (panel.mHoverTile == null)
            RuntimeLogic.status("");
        else
            RuntimeLogic.status(panel.mHoverTile.mTile.toString());
    }
    
    private static void updateHoverTileApature(BluePrintPanel panel, MouseEvent e)
    {
        PolyApature over = panel.findApature(e.getX(), e.getY());
        if (PolyApature.equals(over, panel.mHoverApature))
            return;
        panel.mHoverApature = over;
        panel.mHoverTile = null;
        if (panel.mHoverApature == null)
            RuntimeLogic.status("");
        else if (panel.mHoverApature.mApature != null)
            RuntimeLogic.status(panel.mHoverApature.mApature.toString());
        else
            RuntimeLogic.status("WALL");
    }

    static void doMouseClicked(MouseEvent e)
    {
        BluePrintPanel panel = (BluePrintPanel)e.getSource();
        if (panel.mModeButton.contains(e.getPoint()))
            doModeButton(panel);
        else if (panel.mActionButton.contains(e.getPoint()))
            doActionButton(panel);
        else if (doMouseClickedSelector(panel, e))
            return;
        else if (EditorSettingsLogic.getInstance().getActionMode() == EditorSettings.ACTION_SQUARE)
            doMouseClickedSquare(panel, e);
        else if (EditorSettingsLogic.getInstance().getActionMode() == EditorSettings.ACTION_APATURE)
            doMouseClickedApature(panel, e);
    }
    
    
    private static boolean doMouseClickedSelector(BluePrintPanel panel, MouseEvent e)
    {
        for (int i = 0; i < panel.mSelectors.size(); i++)
        {
            PolySelect ps = panel.mSelectors.get(i);
            if (ps.mRect.contains(e.getPoint()))
            {
                if (ps.mItem instanceof PSquare)
                    EditorSquareLogic.select((PSquare)ps.mItem);
                else if (ps.mItem instanceof PApature)
                    EditorApatureLogic.select((PApature)ps.mItem);
                return true;
            }
        }
        return false;
    }

    
    private static void doMouseClickedSquare(BluePrintPanel panel, MouseEvent e)
    {
        if (panel.mHoverSquare == null)
            return;
        if (panel.mMode == BluePrintPanel.MODE_INSERT)
        {
            if (e.isControlDown())
            {
                if ((panel.mHoverTile != null) && (panel.mHoverTile.mTile != null))
                    EditorSquareLogic.select((PSquare)panel.mHoverTile.mTile);
            }
            else
            {
                if (EditorSettingsLogic.getInstance().getSelectedSquare() != null)
                    EditorHouseLogic.insertSquare(panel.mHoverSquare[2], panel.mHoverSquare[1],
                            panel.mHoverSquare[0],
                            EditorSettingsLogic.getInstance().getSelectedSquare());
            }
        }
        else if (panel.mMode == BluePrintPanel.MODE_DEL)
        {
            EditorHouseLogic.removeTile(panel.mHoverSquare[2], panel.mHoverSquare[1],
                    panel.mHoverSquare[0]);
        }
    }
    
    private static void doMouseClickedApature(BluePrintPanel panel, MouseEvent e)
    {
        PolyApature ap = panel.mHoverApature;
        if (ap == null)
            return;
        if (panel.mMode == BluePrintPanel.MODE_INSERT)
        {
            if (e.isControlDown())
            {
                if ((panel.mHoverApature != null) && (panel.mHoverApature.mApature != null))
                    EditorApatureLogic.select(panel.mHoverApature.mApature);
            }
            else
            {
                EditorHouseLogic.setTile(ap.mLocation.getZ(), ap.mLocation.getY(), ap.mLocation.getX(), 
                        EditorSettingsLogic.getInstance().getSelectedApature());
            }
        }
        else if (panel.mMode == BluePrintPanel.MODE_DEL)
        {
            // You "delete" a "solid wall" by inserting an empty apature
            if (ap.mApature == null)
                EditorHouseLogic.setTile(ap.mLocation.getZ(), ap.mLocation.getY(), ap.mLocation.getX(), panel.mApatureIndex.get("EMPTY"));
            else
                EditorHouseLogic.removeTile(ap.mLocation.toKey());
        }
    }

    private static void doModeButton(BluePrintPanel panel)
    {
        if (panel.mMode == BluePrintPanel.MODE_NONE)
            panel.mMode = BluePrintPanel.MODE_INSERT;
        else if (panel.mMode == BluePrintPanel.MODE_INSERT)
            panel.mMode = BluePrintPanel.MODE_DEL;
        else
            panel.mMode = BluePrintPanel.MODE_NONE;
//            System.out.println("mode change ");
        panel.repaint();
    }

    private static void doActionButton(BluePrintPanel panel)
    {
        if (EditorSettingsLogic.getInstance().getActionMode() == EditorSettings.ACTION_SQUARE)
            EditorSettingsLogic.getInstance().setActionMode(EditorSettings.ACTION_APATURE);
        else if (EditorSettingsLogic.getInstance().getActionMode() == EditorSettings.ACTION_APATURE)
            EditorSettingsLogic.getInstance().setActionMode(EditorSettings.ACTION_STUFF);
        else
            EditorSettingsLogic.getInstance().setActionMode(EditorSettings.ACTION_SQUARE);
//            System.out.println("mode change ");
        panel.repaint();
    }

    static void doMouseWheelMoved(MouseWheelEvent e)
    {
        BluePrintPanel panel = (BluePrintPanel)e.getSource();
        int delta = e.getWheelRotation();
        panel.DOOR_WIDTH += delta;
        if (panel.DOOR_WIDTH < 1)
            panel.DOOR_WIDTH = 1;
        panel.ICON_SIZE = panel.DOOR_WIDTH * 8;
//        System.out.println("mouse wheel repaint");
        panel.repaint();
    }
    
    static PLocationRef makeLocation(BluePrintPanel panel, int x, int y, int z)
    {
        PLocationRef ref = panel.mHouse.getLocation(x, y, z);
        if (ref == null)
            ref = new PLocationRef(null, x, y, z);
        return ref;
    }

    static void doMousePressed(MouseEvent e)
    {
        if (e.getButton() != MouseEvent.BUTTON3)
            return;
        BluePrintPanel panel = (BluePrintPanel)e.getSource();
        int[] xyz = findClosestSquare(panel, e, BluePrintPanel.MODE_DEL);
        if (xyz == null)
            return;
        PLocationRef here = makeLocation(panel, xyz[0], xyz[1], xyz[2]);
        PLocationRef up = makeLocation(panel, xyz[0], xyz[1], xyz[2]+1);
        PLocationRef down = makeLocation(panel, xyz[0], xyz[1], xyz[2]-1);
        panel.doPopUp(e.getPoint(), here, up, down);
    }
}
