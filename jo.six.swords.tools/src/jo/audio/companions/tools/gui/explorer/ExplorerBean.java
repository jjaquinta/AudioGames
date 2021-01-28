package jo.audio.companions.tools.gui.explorer;

import jo.audio.companions.data.LocationBean;
import jo.util.beans.PCSBean;

public class ExplorerBean extends PCSBean
{
    private int             mScale = 64;
    private LocationBean    mLocation;

    public LocationBean getLocation()
    {
        return mLocation;
    }

    public void setLocation(LocationBean location)
    {
        queuePropertyChange("location", mLocation, location);
        mLocation = location;
        firePropertyChange();
    }

    public int getScale()
    {
        return mScale;
    }

    public void setScale(int scale)
    {
        queuePropertyChange("scale", mScale, scale);
        mScale = scale;
        firePropertyChange();
    }
}
