package jo.audio.thieves.data.template;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.util.beans.PCSBean;
import jo.util.utils.obj.StringUtils;

public class PLocation extends PCSBean implements IJSONAble, Comparable<PLocation>
{
    public static final String              ID_DESCRIPTION         = "description";
    public static final String              ID_NAME                = "name";            
    public static final String              ID_ID                  = "ID";              
    public static final String              ID_COLOR               = "color";           
    public static final String              ID_MOVE_SILENTLY_MOD   = "MoveSilentlyMod"; 
    public static final String              ID_HIDE_IN_SHADOWS_MOD = "HideInShadowsMod";
    public static final String              ID_CLIMB_WALLS_MOD     = "ClimbWallsMod";   
    public static final String              ID_FIND_TRAPS_MOD      = "FindTrapsMod";    
    public static final String              ID_OPEN_LOCKS_MOD      = "OpenLocksMod";    

    private String                          mID;
    private String                          mName;
    private String                          mDescription;
    private String                          mColor;
    private int                             mMoveSilentlyMod;
    private int                             mHideInShadowsMod;
    private int                             mClimbWallsMod;
    private int                             mFindTrapsMod;
    private int                             mOpenLocksMod;

    private static final Map<String, Color> mColorCache            = new HashMap<>();

    // I/O

    @Override
    public void fromJSON(JSONObject o)
    {
        setDescription(JSONUtils.getString(o, ID_DESCRIPTION));
        setName(JSONUtils.getString(o, ID_NAME));
        setID(JSONUtils.getString(o, ID_ID));
        setColor(JSONUtils.getString(o, ID_COLOR));
        setMoveSilentlyMod(JSONUtils.getInt(o, ID_MOVE_SILENTLY_MOD));
        setHideInShadowsMod(JSONUtils.getInt(o, ID_HIDE_IN_SHADOWS_MOD));
        setClimbWallsMod(JSONUtils.getInt(o, ID_CLIMB_WALLS_MOD));
        setFindTrapsMod(JSONUtils.getInt(o, ID_FIND_TRAPS_MOD));
        setOpenLocksMod(JSONUtils.getInt(o, ID_OPEN_LOCKS_MOD));
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject o = new JSONObject();
        o.put(ID_DESCRIPTION, getDescription());
        o.put(ID_NAME, getName());
        o.put(ID_ID, getID());
        o.put(ID_COLOR, getColor());
        o.put(ID_MOVE_SILENTLY_MOD, getMoveSilentlyMod());
        o.put(ID_HIDE_IN_SHADOWS_MOD, getHideInShadowsMod());
        o.put(ID_CLIMB_WALLS_MOD, getClimbWallsMod());
        o.put(ID_FIND_TRAPS_MOD, getFindTrapsMod());
        o.put(ID_OPEN_LOCKS_MOD, getOpenLocksMod());
        return o;
    }

    // utilities
    
    @Override
    public int compareTo(PLocation o)
    {
        return StringUtils.compareTo(getName(), o.getName());
    }

    public Color getColorObject()
    {
        String c = getColor();
        Color co;
        if (c == null)
            co = Color.BLACK;
        else
        {
            co = mColorCache.get(c);
            if (co == null)
            {
                c = c.substring(1);
                if (c.length() > 6)
                    c = c.substring(c.length() - 6);
                int rgb = Integer.parseInt(c, 16);
                co = new Color(rgb);
                mColorCache.put(c, co);
            }
        }
        return co;
    }

    @Override
    public String toString()
    {
        return getName() + " (" + getID() + ")";
    }

    // getters and setters

    public String getID()
    {
        return mID;
    }

    public void setID(String value)
    {
        queuePropertyChange(ID_ID, mID, value);
        mID = value;
        firePropertyChange();
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String value)
    {
        queuePropertyChange(ID_NAME, mName, value);
        mName = value;
        firePropertyChange();
    }

    public String getDescription()
    {
        return mDescription;
    }

    public void setDescription(String value)
    {
        queuePropertyChange(ID_DESCRIPTION, mDescription, value);
        mDescription = value;
        firePropertyChange();
    }

    public String getColor()
    {
        return mColor;
    }

    public void setColor(String value)
    {
        queuePropertyChange(ID_COLOR, mColor, value);
        mColor = value;
        firePropertyChange();
    }

    public int getOpenLocksMod()
    {
        return mOpenLocksMod;
    }

    public void setOpenLocksMod(int value)
    {
        queuePropertyChange(ID_OPEN_LOCKS_MOD, mOpenLocksMod, value);
        mOpenLocksMod = value;
        firePropertyChange();
    }

    public int getFindTrapsMod()
    {
        return mFindTrapsMod;
    }

    public void setFindTrapsMod(int value)
    {
        queuePropertyChange(ID_FIND_TRAPS_MOD, mFindTrapsMod, value);
        mFindTrapsMod = value;
        firePropertyChange();
    }

    public int getClimbWallsMod()
    {
        return mClimbWallsMod;
    }

    public void setClimbWallsMod(int value)
    {
        queuePropertyChange(ID_CLIMB_WALLS_MOD, mClimbWallsMod, value);
        mClimbWallsMod = value;
        firePropertyChange();
    }

    public int getHideInShadowsMod()
    {
        return mHideInShadowsMod;
    }

    public void setHideInShadowsMod(int value)
    {
        queuePropertyChange(ID_HIDE_IN_SHADOWS_MOD, mHideInShadowsMod,
                value);
        mHideInShadowsMod = value;
        firePropertyChange();
    }

    public int getMoveSilentlyMod()
    {
        return mMoveSilentlyMod;
    }

    public void setMoveSilentlyMod(int value)
    {
        queuePropertyChange(ID_MOVE_SILENTLY_MOD, mMoveSilentlyMod, value);
        mMoveSilentlyMod = value;
        firePropertyChange();
    }
}
