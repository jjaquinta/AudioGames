package jo.audio.companions.data;

import java.util.Random;
import java.util.StringTokenizer;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.effect.EffectCompanionLogic;
import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

public class CompMonsterTypeBean implements IJSONAble, IMonsterType
{
    private String         mID;
    private String         mATT;
    private String         mMove;
    private String         mAC;
    private String         mNumAtt;
    private String         mSize;
    private String         mFreq;
    private String         mName;
    private String         mEnc;
    private String         mType;
    private String         mHD;
    private String         mTerrain;
    private String         mSpecial = "";
    private JSONObject     mDetails;
    private String         mIntelligence = "";
    private String         mAlignment = "";
    private String         mLairProbability = "";
    // transients
    private int            mChallenge;
    private DiceRollBean   mEncRoll  = null;
    private HitDiceBean    mHDRoll   = null;
    private IAttack[]      mAttacks = null;
    private DamageRollBean[] mAttRolls = null;
    private Float          mAverageDamage = null;
    private Integer        mEquivalentLevel = null;

    // utilities
    
    @Override
    public String toString()
    {
        return "[id="+mID+", name="+mName+"]";
    }

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
    
    public boolean isSpecial(String type)
    {
        if (mSpecial == null)
            return false;
        return mSpecial.indexOf(type) >= 0;
    }
    
    public float getAverageDamage()
    {
        if (mAverageDamage == null)
        {
            float dam = 0;
            for (DamageRollBean att : getAttRolls())
            {
                if (att.hasParam("chanceUse"))
                    continue;
                if (att.hasParam("maxTimes"))
                    continue;
                dam += att.average();
            }
            dam *= getEncRoll().average();
            mAverageDamage = dam;
        }
        return mAverageDamage;
    }
    
    public float getAverageDamage(int ac)
    {
        int th = getTHAC(ac);
        float pc = (21 - th)/20.0f;
        if (pc > 1.0f)
            pc = 1.0f;
        else if (pc < 0)
            pc = 0.0f;
        return getAverageDamage()*pc;
    }

    public DiceRollBean getEncRoll()
    {
        if (mEncRoll == null)
            mEncRoll = new DiceRollBean(mEnc);
        return mEncRoll;
    }

    public int getEnc(Random rnd)
    {
        return getEncRoll().roll(rnd);
    }

    public HitDiceBean getHDRoll()
    {
        if (mHDRoll == null)
            mHDRoll = new HitDiceBean(mHD);
        return mHDRoll;
    }

    public int getHD(Random rnd)
    {
        return getHDRoll().roll(rnd);
    }

    public DamageRollBean[] getAttRolls()
    {
        if (mAttRolls == null)
        {
            int numAtt = Integer.parseInt(getNumAtt());
            mAttRolls = new DamageRollBean[numAtt];
            StringTokenizer st = new StringTokenizer(getATT(), "/");
            int idx = 0;
            while (st.hasMoreTokens())
                mAttRolls[idx++] = new DamageRollBean(st.nextToken());
            while (idx < mAttRolls.length)
                mAttRolls[idx++] = mAttRolls[0];
        }
        return mAttRolls;
    }
    
    public IAttack[] getAttacks()
    {
        if (mAttacks == null)
        {
            getAttRolls();
            mAttacks = new IAttack[mAttRolls.length];
            for (int i = 0; i < mAttRolls.length; i++)
                mAttacks[i] = new MonsterAttack(mAttRolls[i]);
        }
        return mAttacks;
    }
    
    public int getEquivalentLevel()
    {
        if (mEquivalentLevel == null)
        {
            float hp = getHDRoll().average();
            int level = 0;
            if (hp < 3.5)
                level = 0;
            else if (hp < 4.5)
                level = 1;
            else if (hp < 5.5)
                level = 2;
            else if (hp < 10)
                level = 3;
            else if (hp < 14.5)
                level = 4;
            else if (hp < 19)
                level = 5;
            else
            {
               level = (int)(hp*.222 + 0.7777);
            }
            mEquivalentLevel = level;
        }
        return mEquivalentLevel;
    }
    
    public int getTHAC0()
    {
        return 21 - getEquivalentLevel();
    }
    
    public int getTHAC(int ac)
    {
        return getTHAC0() - ac;
    }
    
    public int getExperienceBase()
    {
        int hd = (int)(getHDRoll().average()/4.5f);
        if (hd > CompConstLogic.TABLE_XP_PER_HD.length)
            hd = CompConstLogic.TABLE_XP_PER_HD.length - 1;
        return CompConstLogic.TABLE_XP_PER_HD[hd];
    }
    
    public int getExperiencePerHP()
    {
        int hd = (int)(getHDRoll().average()/4.5f);
        if (hd > CompConstLogic.TABLE_XP_PER_HP.length)
            hd = CompConstLogic.TABLE_XP_PER_HP.length - 1;
        return CompConstLogic.TABLE_XP_PER_HP[hd];
    }
    
    // +ve values in favor of monster, -ve values in favor of player
    public float defeatMargin(float playerHP, float playerDPR, int playerAC)
    {
        float monsterHP = getEncRoll().average()*getHDRoll().average();
        float monsterDPR = getAverageDamage(playerAC);
        float roundsToKillMonster = monsterHP/playerDPR;
        float roundsToKillPlayer = playerHP/monsterDPR;
        return roundsToKillMonster - roundsToKillPlayer;
    }

    public int getFrequency()
    {
        switch (mFreq)
        {
            case "Common":
                return 8;
            case "Uncommon":
                return 4;
            case "Rare":
                return 2;
            case "Very Rare":
                return 1;
        }
        throw new IllegalArgumentException("Unknown frequency:"+mFreq);
    }
    
    public float getWidth()
    {
        if (mSize.startsWith("S"))
            return 1.0f/6.0f;
        if (mSize.startsWith("M"))
            return 1.0f/4.0f;
        if (mSize.startsWith("L"))
            return 1.0f/2.0f;
        return 1.0f;
    }
    
    // getters and setters
    public String getATT()
    {
        return mATT;
    }

    public void setATT(String aTT)
    {
        mATT = aTT;
    }

    public String getMove()
    {
        return mMove;
    }

    public void setMove(String move)
    {
        mMove = move;
    }

    public String getAC()
    {
        return mAC;
    }

    public void setAC(String aC)
    {
        mAC = aC;
    }

    public String getNumAtt()
    {
        return mNumAtt;
    }

    public void setNumAtt(String numAtt)
    {
        mNumAtt = numAtt;
    }

    public String getSize()
    {
        return mSize;
    }

    public void setSize(String size)
    {
        mSize = size;
    }

    public String getFreq()
    {
        return mFreq;
    }

    public void setFreq(String freq)
    {
        mFreq = freq;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getEnc()
    {
        return mEnc;
    }

    public void setEnc(String enc)
    {
        mEnc = enc;
    }

    public String getType()
    {
        return mType;
    }

    public void setType(String type)
    {
        mType = type;
    }

    public String getHD()
    {
        return mHD;
    }

    public void setHD(String hD)
    {
        mHD = hD;
    }

    public String getTerrain()
    {
        return mTerrain;
    }

    public void setTerrain(String terrain)
    {
        mTerrain = terrain;
    }

    public String getID()
    {
        return mID;
    }

    public void setID(String iD)
    {
        mID = iD;
    }

    public String getSpecial()
    {
        return mSpecial;
    }

    public void setSpecial(String special)
    {
        mSpecial = special;
    }

    public JSONObject getDetails()
    {
        return mDetails;
    }

    public void setDetails(JSONObject details)
    {
        mDetails = details;
    }

    public int getChallenge()
    {
        return mChallenge;
    }

    public void setChallenge(int challenge)
    {
        mChallenge = challenge;
    }

    
    class MonsterAttack implements IAttack
    {
        private DamageRollBean    mRoll;
        
        public MonsterAttack(DamageRollBean roll)
        {
            mRoll = roll;
        }
        
        @Override
        public boolean hasParam(String param)
        {
            return mRoll.hasParam(param);
        }
        
        @Override
        public String getParam(String param)
        {
            return mRoll.getParam(param);
        }
        
        @Override
        public int rollDamage(CompContextBean context, Random rnd, ICombatant target)
        {
            return mRoll.roll(rnd);
        }
        
        @Override
        public int bonusToHit(ICombatant target)
        {
            int bonus = 0;
            if (hasParam("plusToHit"))
            {
                int plusToHit = IntegerUtils.parseInt(getParam("plusToHit"));
                bonus += plusToHit;
            }
            return bonus;
        }
        
        @Override
        public int getMagic()
        {
            return 0;
        }
        
        @Override
        public String getName()
        {
            String verb = mRoll.getParam("VERB");
            if (StringUtils.isTrivial(verb))
                return "thing";
            return "{{NOUN_"+verb+"}}";
        }
        
        @Override
        public void effect(FightDetails fight, ICombatant target)
        {
            if (target instanceof CompCompanionBean)
                EffectCompanionLogic.attackEffect(fight, (CompCompanionBean)target, mRoll);
        }
    }


    public String getIntelligence()
    {
        return mIntelligence;
    }

    public void setIntelligence(String intelligence)
    {
        mIntelligence = intelligence;
    }

    public String getAlignment()
    {
        return mAlignment;
    }

    public void setAlignment(String alignment)
    {
        mAlignment = alignment;
    }

    public String getLairProbability()
    {
        return mLairProbability;
    }

    public void setLairProbability(String lairProbability)
    {
        mLairProbability = lairProbability;
    }
}
