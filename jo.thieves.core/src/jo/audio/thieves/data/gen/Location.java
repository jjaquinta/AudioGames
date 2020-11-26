package jo.audio.thieves.data.gen;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.thieves.logic.ThievesConstLogic;

public class Location extends Actionable
{
    private String         mID;
    private House          mHouse;
    private String         mName;
    private String         mDescription;
    private List<Observer> mOccupants = new ArrayList<>();
    private Treasure       mBooty;
    private boolean        mInside;
    private boolean        mBedroom;
    // apatures
    private String         mNorth;
    private String         mSouth;
    private String         mEast;
    private String         mWest;
    private String         mUp;
    private String         mDown;
    
    public Location(House house)
    {
        mHouse = house;
    }

    // utilities
    public void fromJSON(JSONObject o)
    {
        if (o.containsKey("ID"))
            setID(o.getString("ID"));
        if (o.containsKey("name"))
            mName = new String(o.getString("name"));
        if (o.containsKey("description"))
            mDescription = new String(o.getString("description"));
        mInside = JSONUtils.getBoolean(o, "Inside");
        mBedroom = JSONUtils.getBoolean(o, "Bedroom");
        super.fromJSON(o);
    }

    public String getApature(int dir)
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

    public void setApature(int dir, String val)
    {
        switch (dir)
        {
            case ThievesConstLogic.NORTH:
                setNorth(val);
                return;
            case ThievesConstLogic.SOUTH:
                setSouth(val);
                return;
            case ThievesConstLogic.EAST:
                setEast(val);
                return;
            case ThievesConstLogic.WEST:
                setWest(val);
                return;
            case ThievesConstLogic.UP:
                setUp(val);
                return;
            case ThievesConstLogic.DOWN:
                setDown(val);
                return;
        }
        throw new IllegalArgumentException("Unknown dir=" + dir);
    }

    public List<String> getApatures()
    {
        List<String> aps = new ArrayList<>();
        if (mNorth != null)
            aps.add(mNorth);
        if (mSouth != null)
            aps.add(mSouth);
        if (mEast != null)
            aps.add(mEast);
        if (mWest != null)
            aps.add(mWest);
        if (mUp != null)
            aps.add(mUp);
        if (mDown != null)
            aps.add(mDown);
        return aps;
    }

    public int dirTo(Location location)
    {
        String toID = location.getID();
        if ((getNorth() != null) && getNorth().endsWith(toID))
            return ThievesConstLogic.NORTH;
        if ((getSouth() != null) && getSouth().endsWith(toID))
            return ThievesConstLogic.SOUTH;
        if ((getEast() != null) && getEast().endsWith(toID))
            return ThievesConstLogic.EAST;
        if ((getWest() != null) && getWest().endsWith(toID))
            return ThievesConstLogic.WEST;
        if ((getUp() != null) && getUp().endsWith(toID))
            return ThievesConstLogic.UP;
        if ((getDown() != null) && getDown().endsWith(toID))
            return ThievesConstLogic.DOWN;
        return -1;
    }

    // getters and setters

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

    public Treasure getBooty()
    {
        return mBooty;
    }

    public void setBooty(Treasure booty)
    {
        mBooty = booty;
    }

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

    public String getID()
    {
        return mID;
    }

    public void setID(String iD)
    {
        mID = iD;
    }

    public boolean isInside()
    {
        return mInside;
    }

    public void setInside(boolean inside)
    {
        mInside = inside;
    }

    public boolean isBedroom()
    {
        return mBedroom;
    }

    public void setBedroom(boolean bedroom)
    {
        mBedroom = bedroom;
    }

    public List<Observer> getOccupants()
    {
        return mOccupants;
    }

    public void setOccupants(List<Observer> occupants)
    {
        mOccupants = occupants;
    }

    public House getHouse()
    {
        return mHouse;
    }

    public void setHouse(House house)
    {
        mHouse = house;
    }
}
