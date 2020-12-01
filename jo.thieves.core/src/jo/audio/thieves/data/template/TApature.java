package jo.audio.thieves.data.template;

import jo.util.beans.JSONBean;

public class TApature extends JSONBean
{ 
    public static String ID_TRANSITION = "transition";// "You use the stairs to get to the next floor.",
    public static String ID_DESCRIPTION = "description";// "A narrow set of wooden stairs connect the floors of this building.",
    public static String ID_NAME = "name";// "Starway",
    public static String ID_OPENABLE = "openable";// false,
    public static String ID_LOCKABLE = "lockable";// false,
    public static String ID_TRANSPARENT = "transparent";// true,
    public static String ID_ID = "ID";//"STAIRWAY"

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

    public String getID()
    {
        return getString(ID_ID);
    }
    
    public void setID(String value)
    {
        setString(ID_ID, value);
    }

    public String getName()
    {
        return getString(ID_NAME);
    }
    
    public void setName(String value)
    {
        setString(ID_NAME, value);
    }

    public String getDescription()
    {
        return getString(ID_DESCRIPTION);
    }
    
    public void setDescription(String value)
    {
        setString(ID_DESCRIPTION, value);
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
