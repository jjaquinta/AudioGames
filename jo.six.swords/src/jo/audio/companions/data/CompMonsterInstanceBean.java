package jo.audio.companions.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.companions.logic.MonsterLogic;
import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;

public class CompMonsterInstanceBean implements IJSONAble, ICombatant
{
    private String  mID;
    private int     mHitPoints;
    private int     mFullHitPoints;
    private List<CompEffectInstanceBean> mEffects = new ArrayList<>();
    private int     mLimitedAttackUse;
    
    // utilities
    @Override
    public String getSize()
    {
        return getType().getSize();
    }
    @Override
    public String getPhylum()
    {
        return getType().getType();
    }
    @Override
    public boolean isLessThanOneHD()
    {
        return getType().getHDRoll().average() < 4.5f;
    }
    @Override
    public int getEffectiveLevel()
    {
        return getType().getHDRoll().getHitDice().getNumber();
    }
    @Override
    public float getWidth()
    {
        return getType().getWidth();
    }
    @Override
    public int getTHAC(int ac)
    {
        return getType().getTHAC(ac);
    }
    @Override
    public int getAC()
    {
        return Integer.parseInt(getType().getAC());
    }
    public IAttack[] getAttacks(CompContextBean context, boolean firstRank, List<ICombatant> targets) 
    {
        return getType().getAttacks();
    }
    @Override
    public int getCurrentHitPoints()
    {
        return getHitPoints();
    }
    @Override
    public void setCurrentHitPoints(int hitPoints)
    {
        setHitPoints(hitPoints);
    }
    @Override
    public String getLogName()
    {
        return getType().getName();
    }
    @Override
    public boolean isEffect(String type)
    {
        return getType().isSpecial(type);
    }
    
    @Override
    public String toString()
    {
        return mID+" x "+mHitPoints;
    }

    public CompMonsterTypeBean getType()
    {
        return MonsterLogic.getMonsterType(getID());
    }
    
    public int getExperience()
    {
        return getType().getExperienceBase() + mFullHitPoints*getType().getExperiencePerHP();
    }

    @Override
    public JSONObject toJSON()
    {
        return ToJSONLogic.toJSONFromBean(this);
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

    public int getHitPoints()
    {
        return mHitPoints;
    }

    public void setHitPoints(int hitPoints)
    {
        mHitPoints = hitPoints;
    }

    public int getFullHitPoints()
    {
        return mFullHitPoints;
    }

    public void setFullHitPoints(int fullHitPoints)
    {
        mFullHitPoints = fullHitPoints;
    }

    public List<CompEffectInstanceBean> getEffects()
    {
        return mEffects;
    }

    public void setEffects(List<CompEffectInstanceBean> effects)
    {
        mEffects = effects;
    }

    public int getLimitedAttackUse()
    {
        return mLimitedAttackUse;
    }

    public void setLimitedAttackUse(int limitedAttackUse)
    {
        mLimitedAttackUse = limitedAttackUse;
    }
}
