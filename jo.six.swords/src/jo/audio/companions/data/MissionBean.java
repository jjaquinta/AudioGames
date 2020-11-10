package jo.audio.companions.data;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.obj.LongUtils;

public class MissionBean implements IJSONAble
{
    private static final String FEATURE = "FEATURE";
    private static final String MODULE = "MODULE";
    private static final String MESSAGE = "MESSAGE";
    private static final String LOCATION = "location";
    private static final String TYPE = "type";
    private static final String ID = "id";
    private static final String EXPIRY_DATE = "expiryDate";
    
    private long        mExpiryDate;
    private String      mID;
    private String      mType;
    private String      mModule;
    private String      mFeature;
    private GeoBean     mLocation;
    private AudioMessageBean mMessage;
    private JSONObject  mData;
    
    // constructors
    public MissionBean()
    {
        fromJSON(new JSONObject());
    }
    
    public MissionBean(JSONObject json)
    {
        fromJSON(json);
    }
    
    // utilities
    
    public boolean isExpired()
    {
        if (mExpiryDate <= 0L)
            return false;
        return mExpiryDate > System.currentTimeMillis();
    }

    @Override
    public JSONObject toJSON()
    {
        mData.put(EXPIRY_DATE, mExpiryDate);
        mData.put(ID, mID);
        mData.put(TYPE, mType);
        mData.put(MODULE, mModule);
        mData.put(FEATURE, mFeature);
        if (mLocation != null)
            mData.put(LOCATION, mLocation.toString());
        if (mMessage == null)
            mData.put(MESSAGE, mMessage.toJSON());
        return mData;
    }

    @Override
    public void fromJSON(JSONObject o)
    {
        mData = o;
        mExpiryDate = LongUtils.parseLong(mData.get(EXPIRY_DATE));
        mID = mData.getString(ID);
        mType = mData.getString(TYPE);
        mModule = mData.getString(MODULE);
        mFeature = mData.getString(FEATURE);
        if (mData.containsKey(LOCATION))
            mLocation = new GeoBean(mData.getString(LOCATION));
        if (mData.containsKey(MESSAGE))
            mMessage = new AudioMessageBean((JSONObject)mData.get(MESSAGE));
    }

    // setters and getters

    public long getExpiryDate()
    {
        return mExpiryDate;
    }

    public void setExpiryDate(long expiryDate)
    {
        mExpiryDate = expiryDate;
    }

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

    public GeoBean getLocation()
    {
        return mLocation;
    }

    public void setLocation(GeoBean location)
    {
        mLocation = location;
    }

    public JSONObject getData()
    {
        return mData;
    }

    public void setData(JSONObject data)
    {
        mData = data;
    }

    public AudioMessageBean getMessage()
    {
        return mMessage;
    }

    public void setMessage(AudioMessageBean message)
    {
        mMessage = message;
    }

    public String getModule()
    {
        return mModule;
    }

    public void setModule(String module)
    {
        mModule = module;
    }

    public String getFeature()
    {
        return mFeature;
    }

    public void setFeature(String feature)
    {
        mFeature = feature;
    }
}
