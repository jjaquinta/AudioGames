package jo.audio.companions.tools.gui.map.logic;

import jo.util.geom2d.Point2D;

public class MapPoint2D extends Point2D
{
    private boolean mAnchor;

    public MapPoint2D(int i, int j)
    {
        super(i, j);
    }

    public boolean isAnchor()
    {
        return mAnchor;
    }

    public void setAnchor(boolean anchor)
    {
        mAnchor = anchor;
    }
    
}
