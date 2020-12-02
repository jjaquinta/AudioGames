package jo.audio.thieves.data.template;

import java.awt.Color;

import jo.util.beans.JSONBean;

public class PLocation extends JSONBean
{ 
    public static final String ID_DESCRIPTION = "description";// "A narrow set of wooden stairs connect the floors of this building.",
    public static final String ID_NAME = "name";// "Starway",
    public static final String ID_ID = "ID";//"STAIRWAY"
    public static final String ID_COLOR = "color";//"#010203"
    public static final String ID_MOVE_SILENTLY_MOD = "MoveSilentlyMod";//-10
    public static final String ID_HIDE_IN_SHADOWS_MOD = "HideInShadowsMod";//-10,
    public static final String ID_CLIMB_WALLS_MOD = "ClimbWallsMod";//-10,
    public static final String ID_FIND_TRAPS_MOD = "FindTrapsMod";//-10,
    public static final String ID_OPEN_LOCKS_MOD = "OpenLocksMod";//-10,

    // utilities
    public Color getColorObject()
    {
        String c = getColor();
        Color co;
        if (c == null)
            co = Color.BLACK;
        else
        {
            int rgb = Integer.parseInt(c.substring(1), 16);
            co = new Color(rgb);
        }
        System.out.println(c+"->"+co);
        return co;
    }
    
    @Override
    public String toString()
    {
        return getName();
    }

    // getters and setters

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

    public String getColor()
    {
        return getString(ID_COLOR);
    }
    
    public void setColor(String value)
    {
        setString(ID_COLOR, value);
    }
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
}
