package jo.audio.thieves.data.template;

public class PApature extends PLocation
{ 
    public static final String ID_TRANSITION = "transition";// "You use the stairs to get to the next floor.",
    public static final String ID_OPENABLE = "openable";// false,
    public static final String ID_LOCKABLE = "lockable";// false,
    public static final String ID_TRANSPARENT = "transparent";// true,

    // getters and setters
    public boolean getTransparent()
    {
        return getBoolean(ID_TRANSPARENT);
    }
    
    public void setTransparent(boolean value)
    {
        setBoolean(ID_TRANSPARENT, value);
    }

    public boolean getLockable()
    {
        return getBoolean(ID_LOCKABLE);
    }
    
    public void setLockable(boolean value)
    {
        setBoolean(ID_LOCKABLE, value);
    }

    public boolean getOpenable()
    {
        return getBoolean(ID_OPENABLE);
    }
    
    public void setOpenable(boolean value)
    {
        setBoolean(ID_OPENABLE, value);
    }

    public String getTransition()
    {
        return getString(ID_TRANSITION);
    }
    
    public void setTransition(String value)
    {
        setString(ID_TRANSITION, value);
    }
}
