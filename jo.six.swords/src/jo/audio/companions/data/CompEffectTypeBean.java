package jo.audio.companions.data;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;

public class CompEffectTypeBean implements IJSONAble
{
    public static final String HEALING = "healing";
    public static final String CONTROL = "control";
    public static final String CLAIRAUDIENCE = "clairaudience";
    public static final String CLAIRVOYANCE = "clairvoyance";
    public static final String STRENGTH_SET = "strength_set";
    public static final String STRENGTH_PLUS = "strength_plus";
    public static final String INTELLIGENCE_SET = "intelligence_set";
    public static final String INTELLIGENCE_PLUS = "intelligence_plus";
    public static final String WISDOM_SET = "wisdom_set";
    public static final String WISDOM_PLUS = "wisdom_plus";
    public static final String CONSTITUTION_SET = "constitution_set";
    public static final String CONSTITUTION_PLUS = "constitution_plus";
    public static final String DEXTERITY_SET = "dexterity_set";
    public static final String DEXTERITY_PLUS = "dexterity_plus";
    public static final String CHARISMA_SET = "charisma_set";
    public static final String CHARISMA_PLUS = "charisma_plus";
    public static final String EXTRA_HEALING = "extra_healing";
    public static final String RESIST = "resist";
    public static final String INVISIBILITY = "invisibility";
    public static final String INVULNERABILITY = "invulnerability";
    public static final String SPEED = "speed";
    public static final String DAMAGE = "damage";
    public static final String LEVEL_DRAIN = "level_drain";
    public static final String SUMMON = "summon";
    public static final String SOUND = "sound";
    
    public static final int INSTANTANEOUS = 0;
    public static final int COMBAT = 1;
    public static final int TACTICAL = 2;
    public static final int STRATEGIC = 3;
    public static final int PERMANENT = 4;
    
    private String  mID;
    private String  mSubType;
    private int     mDurationType;
    private String  mDurationLength;
    private DiceRollBean    mDurationLengthRoll = null;
    private JSONObject mMetadata;
    
    public CompEffectTypeBean()
    {        
        mMetadata = new JSONObject();
    }
    
    public CompEffectTypeBean(String id, String subType, int durationType, String durationLength)
    {        
        mID = id;
        mSubType = subType;
        mDurationType = durationType;
        mDurationLength = durationLength;
    }
    
    public CompEffectTypeBean(JSONObject json)
    {        
        fromJSON(json);
    }
    
    // utilities
    
    public DiceRollBean getDurationLengthRoll()
    {
        if (mDurationLengthRoll == null)
            mDurationLengthRoll = new DiceRollBean(mDurationLength);
        return mDurationLengthRoll;
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
    public String getDurationLength()
    {
        return mDurationLength;
    }
    public void setDurationLength(String durationLength)
    {
        mDurationLength = durationLength;
    }

    public String getSubType()
    {
        return mSubType;
    }

    public void setSubType(String subType)
    {
        mSubType = subType;
    }

    public JSONObject getMetadata()
    {
        return mMetadata;
    }

    public void setMetadata(JSONObject metadata)
    {
        mMetadata = metadata;
    }
}
