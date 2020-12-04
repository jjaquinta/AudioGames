package jo.audio.thieves.tools.editor.ui.bp;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import jo.audio.thieves.tools.editor.logic.EditorHouseLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.audio.thieves.tools.logic.RuntimeLogic;

public class BluePrintMouseLogic
{
    static void doMouseMoved(MouseEvent e)
    {
        BluePrintPanel panel = (BluePrintPanel)e.getSource();
        updateHoverTile(panel, e);
        updateHoverSquare(panel, e);
//        System.out.println("mouse moved repaint");
        panel.repaint();
    }

    private static final int[][] INSERT_MATCH = { { 0, 0 }, { -2, 0 }, { 2, 0 },
            { 0, -2 }, { 0, 2 } };

    private static void updateHoverSquare(BluePrintPanel panel, MouseEvent e)
    {
        if (panel.mMode == BluePrintPanel.MODE_NONE)
        {
            panel.mHoverSquare = null;
        }
        else
        {
            for (int floor = 0; floor < panel.mOrigins.length; floor++)
            {
                int x = (e.getX() - panel.mOrigins[floor][0]) / panel.ICON_SIZE * 2 + 1;
                int y = (e.getY() - panel.mOrigins[floor][1]) / panel.ICON_SIZE * 2 + 1;
                int z = floor * 2;
                boolean match = false;
                if (panel.mMode == BluePrintPanel.MODE_INSERT)
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
                else if (panel.mMode == BluePrintPanel.MODE_DEL)
                {
                    String k = x + "," + y + "," + z;
                    match = panel.mLocations.containsKey(k);
                }
                if (match)
                {
                    panel.mHoverSquare = new int[] { x, y, z };
                    return;
                }
            }
            panel.mHoverSquare = null;
        }
    }

    private static void updateHoverTile(BluePrintPanel panel, MouseEvent e)
    {
        PolyTile over = panel.findTile(e.getX(), e.getY());
        if (PolyTile.equals(over, panel.mHoverTile))
            return;
        panel.mHoverTile = over;
        if (panel.mHoverTile == null)
            RuntimeLogic.status("");
        else
            RuntimeLogic.status(panel.mHoverTile.mTile.toString());
    }

    static void doMouseClicked(MouseEvent e)
    {
        BluePrintPanel panel = (BluePrintPanel)e.getSource();
        if (panel.mModeButton.contains(e.getPoint()))
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
        else if (panel.mMode == BluePrintPanel.MODE_INSERT)
        {
            if ((panel.mHoverSquare != null) && (EditorSettingsLogic.getInstance()
                    .getSelectedSquare() != null))
                EditorHouseLogic.insertSquare(panel.mHoverSquare[2], panel.mHoverSquare[1],
                        panel.mHoverSquare[0],
                        EditorSettingsLogic.getInstance().getSelectedSquare());
        }
        else if (panel.mMode == BluePrintPanel.MODE_DEL)
        {
            if (panel.mHoverSquare != null)
                EditorHouseLogic.removeTile(panel.mHoverSquare[2], panel.mHoverSquare[1],
                        panel.mHoverSquare[0]);
        }
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
}
