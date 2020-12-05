package jo.audio.thieves.tools.editor.ui.bp;

import java.awt.Rectangle;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLocationRef;

class PolyApature
{
    BluePrintPanel mPanel;
    PApature       mApature;
    PLocationRef   mLocation;
    Rectangle      mRect;

    public PolyApature(BluePrintPanel panel, PLocationRef loc, Rectangle rect)
    {
        mPanel = panel;
        mLocation = loc;
        mApature = mPanel.mApatureIndex.get(loc.getID());
        mRect = rect;
    }

    public static boolean equals(PolyApature t1, PolyApature t2)
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
}
