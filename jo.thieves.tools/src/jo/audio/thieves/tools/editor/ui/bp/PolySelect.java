package jo.audio.thieves.tools.editor.ui.bp;

import java.awt.Rectangle;

import jo.audio.thieves.data.template.PLocation;

class PolySelect
{
    BluePrintPanel mPanel;
    PLocation      mItem;
    Rectangle      mRect;

    public PolySelect(BluePrintPanel panel, PLocation item, Rectangle rect)
    {
        mPanel = panel;
        mItem = item;
        mRect = rect;
    }
}
