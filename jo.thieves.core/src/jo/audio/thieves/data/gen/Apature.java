package jo.audio.thieves.data.gen;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.thieves.logic.ThievesConstLogic;

public class Apature extends Actionable
{
    private String           mID;
    private String           mName;
    private String           mDescription;
    private boolean          mOpenable;
    private boolean          mLockable;
    private boolean          mTransparent;
    // locations
    private String           mNorth;
    private String           mSouth;
    private String           mEast;
    private String           mWest;
    private String           mUp;
    private String           mDown;

    // utilities
    public void fromJSON(JSONObject o)
    {
        if (o.containsKey("ID"))
            setID(o.getString("ID"));
        if (o.containsKey("name"))
            mName = new String(o.getString("name"));
        if (o.containsKey("description"))
            mDescription = new String(o.getString("description"));
        if (o.containsKey("openable"))
            mOpenable = JSONUtils.getBoolean(o, "openable");
        if (o.containsKey("lockable"))
            mLockable = JSONUtils.getBoolean(o, "lockable");
        if (o.containsKey("transparent"))
            mTransparent = JSONUtils.getBoolean(o, "transparent");
        super.fromJSON(o);
    }

    public String getLocation(int dir)
    {
        switch (dir)
        {
            case ThievesConstLogic.NORTH:
                return getNorth();
            case ThievesConstLogic.SOUTH:
                return getSouth();
            case ThievesConstLogic.EAST:
                return getEast();
            case ThievesConstLogic.WEST:
                return getWest();
            case ThievesConstLogic.UP:
                return getUp();
            case ThievesConstLogic.DOWN:
                return getDown();
        }
        throw new IllegalArgumentException("Unknown dir=" + dir);
    }
    
    public void setLocation(int dir, String id)
    {
        switch (dir)
        {
            case ThievesConstLogic.NORTH:
                setNorth(id);
                return;
            case ThievesConstLogic.SOUTH:
                setSouth(id);
                return;
            case ThievesConstLogic.EAST:
                setEast(id);
                return;
            case ThievesConstLogic.WEST:
                setWest(id);
                return;
            case ThievesConstLogic.UP:
                setUp(id);
                return;
            case ThievesConstLogic.DOWN:
                setDown(id);
                return;
        }
        throw new IllegalArgumentException("Unknown dir=" + dir);
    }
    
    public boolean isDestination(Location l)
    {
        return isDestination(l.getID());
    }
    
    public boolean isDestination(String locID)
    {
        return locID.equals(mNorth) || locID.equals(mWest)
                || locID.equals(mSouth) || locID.equals(mEast)
                || locID.equals(mUp) || locID.equals(mDown);
    }

    // getters and setters

    public String getNorth()
    {
        return mNorth;
    }

    public void setNorth(String north)
    {
        mNorth = north;
    }

    public String getSouth()
    {
        return mSouth;
    }

    public void setSouth(String south)
    {
        mSouth = south;
    }

    public String getEast()
    {
        return mEast;
    }

    public void setEast(String east)
    {
        mEast = east;
    }

    public String getWest()
    {
        return mWest;
    }

    public void setWest(String west)
    {
        mWest = west;
    }

    public String getUp()
    {
        return mUp;
    }

    public void setUp(String up)
    {
        mUp = up;
    }

    public String getDown()
    {
        return mDown;
    }

    public void setDown(String down)
    {
        mDown = down;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public void setDescription(String description)
    {
        mDescription = description;
    }

    public String getID()
    {
        return mID;
    }

    public void setID(String iD)
    {
        mID = iD;
    }

    public boolean isOpenable()
    {
        return mOpenable;
    }

    public void setOpenable(boolean openable)
    {
        mOpenable = openable;
    }

    public boolean isLockable()
    {
        return mLockable;
    }

    public void setLockable(boolean lockable)
    {
        mLockable = lockable;
    }

    public boolean isTransparent()
    {
        return mTransparent;
    }

    public void setTransparent(boolean transparent)
    {
        mTransparent = transparent;
    }

}
