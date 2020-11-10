package jo.audio.companions.data;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;

public class ItemSelectBean implements IJSONAble
{
    // search params
    private String  mID;
    private String  mType;
    private int     mMagic = -1;
    // set params
    private String  mName;
    private int     mCount;
    
    // utilities
    @Override
    public JSONObject toJSON()
    {
        JSONObject u = ToJSONLogic.toJSONFromBean(this);
        return u;
    }

    @Override
    public void fromJSON(JSONObject json)
    {
        FromJSONLogic.fromJSON(this, json);
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
    public String getType()
    {
        return mType;
    }
    public void setType(String type)
    {
        mType = type;
    }
    public int getMagic()
    {
        return mMagic;
    }
    public void setMagic(int magic)
    {
        mMagic = magic;
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public int getCount()
    {
        return mCount;
    }
    public void setCount(int count)
    {
        mCount = count;
    }
}
