package jo.audio.thieves.data.template;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

public class PApature extends PLocation
{
    public static final String ID_TRANSITION  = "transition"; 
    public static final String ID_OPENABLE    = "openable";   
    public static final String ID_LOCKABLE    = "lockable";   
    public static final String ID_TRANSPARENT = "transparent";

    private String             mTransition;
    private boolean            mOpenable;
    private boolean            mLockable;
    private boolean            mTransparent;

    // I/O

    @Override
    public void fromJSON(JSONObject o)
    {
        super.fromJSON(o);
        setTransition(JSONUtils.getString(o, ID_TRANSITION));
        setOpenable(JSONUtils.getBoolean(o, ID_OPENABLE));
        setLockable(JSONUtils.getBoolean(o, ID_LOCKABLE));
        setTransparent(JSONUtils.getBoolean(o, ID_TRANSPARENT));
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject o = super.toJSON();
        o.put(ID_TRANSITION, getTransition());
        o.put(ID_OPENABLE, getOpenable());
        o.put(ID_LOCKABLE, getLockable());
        o.put(ID_TRANSPARENT, getTransparent());
        return o;
    }

    // utilities

    public static boolean equals(PApature a1, PApature a2)
    {
        if (a1 == null)
            if (a2 == null)
                return true;
            else
                return false;
        else if (a2 == null)
            return false;
        else
            return a1.getID().equals(a2.getID());
    }

    // getters and setters
    public boolean getTransparent()
    {
        return mTransparent;
    }

    public void setTransparent(boolean value)
    {
        queuePropertyChange(ID_TRANSPARENT, mTransparent, value);
        mTransparent = value;
        firePropertyChange();
    }

    public boolean getLockable()
    {
        return mLockable;
    }

    public void setLockable(boolean value)
    {
        queuePropertyChange(ID_LOCKABLE, mLockable, value);
        mLockable = value;
        firePropertyChange();
    }

    public boolean getOpenable()
    {
        return mOpenable;
    }

    public void setOpenable(boolean value)
    {
        queuePropertyChange(ID_OPENABLE, mOpenable, value);
        mOpenable = value;
        firePropertyChange();
    }

    public String getTransition()
    {
        return mTransition;
    }

    public void setTransition(String value)
    {
        queuePropertyChange(ID_TRANSITION, mTransition, value);
        mTransition = value;
        firePropertyChange();
    }
}
