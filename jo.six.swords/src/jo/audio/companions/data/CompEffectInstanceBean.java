package jo.audio.companions.data;

import java.util.Random;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.companions.logic.CompConstLogic;
import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;

public class CompEffectInstanceBean implements IJSONAble
{
    private String  mID;
    private String  mSubType;
    private int     mDurationType;
    private int     mTimeExpiry;

    public CompEffectInstanceBean()
    {        
    }

    public CompEffectInstanceBean(CompEffectTypeBean type, CompUserBean user, Random rnd)
    {        
        mID = type.getID();
        mSubType = type.getSubType();
        mDurationType = type.getDurationType();
        mTimeExpiry = user.getTotalTime();
        if (mDurationType == CompEffectTypeBean.COMBAT)
            mTimeExpiry += type.getDurationLengthRoll().roll(rnd)*CompConstLogic.TIME_COMBAT;
        else if (mDurationType == CompEffectTypeBean.TACTICAL)
            mTimeExpiry += type.getDurationLengthRoll().roll(rnd)*CompConstLogic.TIME_TACTICAL;
        else if (mDurationType == CompEffectTypeBean.STRATEGIC)
            mTimeExpiry += type.getDurationLengthRoll().roll(rnd)*CompConstLogic.TIME_STRATEGIC;
    }
    
    // utilities
    
    public boolean isTimeBased()
    {
        return ((mDurationType == CompEffectTypeBean.COMBAT) 
                || (mDurationType == CompEffectTypeBean.TACTICAL) 
                || (mDurationType == CompEffectTypeBean.STRATEGIC));
    }
    
    public void fromJSON(JSONObject json) 
    {
        FromJSONLogic.fromJSON(this, json);
    }
    
    @Override
    public JSONObject toJSON()
    {
        return ToJSONLogic.toJSONFromBean(this);
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
    public int getDurationType()
    {
        return mDurationType;
    }
    public void setDurationType(int durationType)
    {
        mDurationType = durationType;
    }

    public String getSubType()
    {
        return mSubType;
    }

    public void setSubType(String subType)
    {
        mSubType = subType;
    }

    public int getTimeExpiry()
    {
        return mTimeExpiry;
    }

    public void setTimeExpiry(int timeExpiry)
    {
        mTimeExpiry = timeExpiry;
    }
}
