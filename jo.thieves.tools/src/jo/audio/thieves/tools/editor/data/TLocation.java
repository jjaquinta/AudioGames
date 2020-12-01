package jo.audio.thieves.tools.editor.data;

import jo.util.beans.JSONBean;

public class TLocation extends JSONBean implements INamed
{
    public static final String ID_ID = "ID";//"ROOF",
    public static final String ID_DESCRIPTION = "description";// "Weathered boards lie at a slant to shed water.",
    public static final String ID_NAME = "name";// "Roof",
    public static final String ID_MOVE_SILENTLY_MOD = "MoveSilentlyMod";//-10
    public static final String ID_HIDE_IN_SHADOWS_MOD = "HideInShadowsMod";//-10,
    public static final String ID_CLIMB_WALLS_MOD = "ClimbWallsMod";//-10,
    public static final String ID_FIND_TRAPS_MOD = "FindTrapsMod";//-10,
    public static final String ID_OPEN_LOCKS_MOD = "OpenLocksMod";//-10,
    public static final String ID_INSIDE = "inside";
    public static final String ID_BEDROOM = "bedroom";

    // getters and setters
    public int getOpenLocksMod()
    {
        return getInt(ID_OPEN_LOCKS_MOD);
    }
    
    public void setOpenLocksMod(int value)
    {
        setInt(ID_OPEN_LOCKS_MOD, value);
    }

    public int getFindTrapsMod()
    {
        return getInt(ID_FIND_TRAPS_MOD);
    }
    
    public void setFindTrapsMod(int value)
    {
        setInt(ID_FIND_TRAPS_MOD, value);
    }

    public int getClimbWallsMod()
    {
        return getInt(ID_CLIMB_WALLS_MOD);
    }
    
    public void setClimbWallsMod(int value)
    {
        setInt(ID_CLIMB_WALLS_MOD, value);
    }

    public int getHideInShadowsMod()
    {
        return getInt(ID_HIDE_IN_SHADOWS_MOD);
    }
    
    public void setHideInShadowsMod(int value)
    {
        setInt(ID_HIDE_IN_SHADOWS_MOD, value);
    }

    public int getMoveSilentlyMod()
    {
        return getInt(ID_MOVE_SILENTLY_MOD);
    }
    
    public void setMoveSilentlyMod(int value)
    {
        setInt(ID_MOVE_SILENTLY_MOD, value);
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

    public boolean getInside()
    {
        return getBoolean(ID_INSIDE);
    }
    
    public void setInside(boolean value)
    {
        setBoolean(ID_INSIDE, value);
    }

    public boolean getBedroom()
    {
        return getBoolean(ID_BEDROOM);
    }
    
    public void setBedroom(boolean value)
    {
        setBoolean(ID_BEDROOM, value);
    }
}
