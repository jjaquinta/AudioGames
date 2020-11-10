package jo.audio.companions.data;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;
import jo.audio.util.model.data.AudioMessageBean;

public class CompRoomBean implements IJSONAble
{
    public static final String TYPE_SCENIC = "scenic";
    public static final String TYPE_ITEM_SHOP = "shop";
    public static final String TYPE_FIGHTERS_GUILD = "hall";
    public static final String TYPE_ENCOUNTER = "encounter";

    public static final String MD_ENCOUNTER_ID = "encounterID";
    public static final String MD_ENCOUNTER_NUMBER = "encounterNumber";
    public static final String MD_ENCOUNTER_CHALLENGE = "encounterChallenge";
    public static final String MD_ENCOUNTER_ANNOUNCE = "encounterAnnounce";
    public static final String MD_ENCOUNTER_TREASURE = "encounterTreasure";
    public static final String MD_ENCOUNTER_TREASURE_ITEM = "encounterTreasureItem";
    public static final String MD_WAIT_TIME = "waitTime";
    public static final String MD_POST_ENTER = "postEnter";
    public static final String MD_POPULATE = "populate";
    public static final String MD_WIDTH = "width";
    public static final String MD_HEIGHT = "height";
    public static final String MD_AREA = "area";
    public static final String MD_X = "x";
    public static final String MD_Y = "y";
    public static final String MD_Z = "z";
    public static final String MD_LOCK_NORTH = "northLock";
    public static final String MD_LOCK_SOUTH = "southLock";
    public static final String MD_LOCK_EAST = "eastLock";
    public static final String MD_LOCK_WEST = "westLock";
    public static final String MD_DESC_NORTH = "northDesc";
    public static final String MD_DESC_SOUTH = "southDesc";
    public static final String MD_DESC_EAST = "eastDesc";
    public static final String MD_DESC_WEST = "westDesc";
    public static final String MD_ITEM_NORTH = "northItem";
    public static final String MD_ITEM_SOUTH = "southItem";
    public static final String MD_ITEM_EAST = "eastItem";
    public static final String MD_ITEM_WEST = "westItem";

    public static final int DIR_NORTH = 0;
    public static final int DIR_SOUTH = 1;
    public static final int DIR_EAST = 2;
    public static final int DIR_WEST = 3;
    
    private String  mType;
    private String  mID;
    private AudioMessageBean mName = new AudioMessageBean();
    private String  mNorth;
    private String  mSouth;
    private String  mEast;
    private String  mWest;
    private AudioMessageBean mDescription = new AudioMessageBean();
    private JSONObject mParams;
    
    // utilities

    @Override
    public JSONObject toJSON()
    {
        return ToJSONLogic.toJSONFromBean(this);
    }

    @Override
    public void fromJSON(JSONObject o)
    {
        FromJSONLogic.fromJSON(this, o);
    }

    public String getDirection(int dir)
    {
        switch (dir)
        {
            case DIR_NORTH:
                return mNorth;
            case DIR_SOUTH:
                return mSouth;
            case DIR_EAST:
                return mEast;
            case DIR_WEST:
                return mWest;
        }
        throw new IllegalArgumentException();
    }
    
    public static final String[] DIR_LOCK = new String[] { MD_LOCK_NORTH, MD_LOCK_SOUTH, MD_LOCK_EAST, MD_LOCK_WEST }; 

    public JSONObject getDirectionLock(int dir)
    {
        if (getParams() == null)
            return null;
        return (JSONObject)getParams().get(DIR_LOCK[dir%4]);
    }

    public void setDirectionLock(int dir, JSONObject lock)
    {
        if (getParams() == null)
            setParams(new JSONObject());
        if (lock == null)
            getParams().remove(DIR_LOCK[dir%4]);
        else
            getParams().put(DIR_LOCK[dir%4], lock);
    }

    public static final String[] DIR_DESC = new String[] { MD_DESC_NORTH, MD_DESC_SOUTH, MD_DESC_EAST, MD_DESC_WEST }; 
    public static final String[] DIR_ITEM = new String[] { MD_ITEM_NORTH, MD_ITEM_SOUTH, MD_ITEM_EAST, MD_ITEM_WEST }; 

    public AudioMessageBean getDirectionDesc(int dir)
    {
        if ((getParams() == null) || !getParams().containsKey(DIR_DESC[dir%4]))
            return null;
        return new AudioMessageBean((JSONObject)getParams().get(DIR_DESC[dir%4]));
    }

    public void setDirectionDesc(int dir, AudioMessageBean desc)
    {
        if (getParams() == null)
            setParams(new JSONObject());
        if (desc == null)
            getParams().remove(DIR_DESC[dir%4]);
        else
            getParams().put(DIR_DESC[dir%4], desc.toJSON());
    }
    
    public void setDirection(int dir, String val)
    {
        switch (dir)
        {
            case DIR_NORTH:
                mNorth = val;
                return;
            case DIR_SOUTH:
                mSouth = val;
                return;
            case DIR_EAST:
                mEast = val;
                return;
            case DIR_WEST:
                mWest = val;
                return;
        }
        throw new IllegalArgumentException();
    }

    public static int opposite(int dir)
    {
        switch (dir%4)
        {
            case DIR_NORTH:
                return DIR_SOUTH;
            case DIR_SOUTH:
                return DIR_NORTH;
            case DIR_EAST:
                return DIR_WEST;
            case DIR_WEST:
                return DIR_EAST;
        }
        throw new IllegalStateException();
    }

    public void rotate(int angle)
    {
        if (angle == 90)
        {
            String tmp = mNorth;
            mNorth = mEast;
            mEast = mSouth;
            mSouth = mWest;
            mWest = tmp;
        }
        else if (angle == 180)
        {
            String tmp = mNorth;
            mNorth = mSouth;
            mSouth = tmp;
            tmp = mEast;
            mEast = mWest;
            mWest = tmp;
        }
        else if (angle == 180)
        {
            String tmp = mNorth;
            mNorth = mWest;
            mWest = mSouth;
            mSouth = mEast;
            mEast = tmp;
        }
    }
    
    public void putParam(String key, Object val)
    {
        if (mParams == null)
            mParams = new JSONObject();
        mParams.put(key, val);
    }
    
    // getters and setters
    
    public String getID()
    {
        return mID;
    }
    public void setID(String iD)
    {
        mID = iD;
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
    public JSONObject getParams()
    {
        return mParams;
    }
    public void setParams(JSONObject params)
    {
        mParams = params;
    }
    public String getType()
    {
        return mType;
    }
    public void setType(String type)
    {
        mType = type;
    }

    public AudioMessageBean getName()
    {
        return mName;
    }

    public void setName(AudioMessageBean name)
    {
        mName = name;
    }

    public AudioMessageBean getDescription()
    {
        return mDescription;
    }

    public void setDescription(AudioMessageBean description)
    {
        mDescription = description;
    }
}
