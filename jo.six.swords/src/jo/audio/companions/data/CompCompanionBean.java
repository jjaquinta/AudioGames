package jo.audio.companions.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.common.data.StringIntegerMap;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.ItemLogic;
import jo.audio.companions.logic.effect.EffectCompanionLogic;
import jo.audio.companions.logic.effect.EffectMonsterLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;

public class CompCompanionBean implements IJSONAble, ICombatant
{
    private String  mID;
    private String  mName;
    private int     mGender;
    private int     mRace;
    private int     mSTR;
    private int     mSTRpc;
    private int     mDEX;
    private int     mCON;
    private int     mINT;
    private int     mWIS;
    private int     mCHA;
    private int     mClazz;
    private int     mLevel;
    private int     mHitPoints;
    private int     mExperiencePoints;
    private int     mWeaponProficiences;
    private int     mNonProficientPenalty;
    private int     mCurrentHitPoints;
    private List<CompItemInstanceBean> mItems = new ArrayList<>();
    private List<String> mProficiencies = new ArrayList<>();
    private StringIntegerMap mProfUse = new StringIntegerMap();
    private List<CompEffectInstanceBean> mEffects = new ArrayList<>();
    private int     mLimitedAttackUse;
    
    // utilities
    @Override
    public String getPhylum()
    {
        return "Companion";
    }
    public DamageRollBean[] getAttRolls() 
    {
        throw new IllegalStateException();
    }
    @Override
    public String getLogName()
    {
        return getName();
    }
    @Override  
    public float getWidth()
    {
        if ((mRace == CompConstLogic.RACE_HALFLING) || (mRace == CompConstLogic.RACE_GNOME))
            return 1.0f/6.0f;
        else
            return 1.0f/4.0f;
    }
    @Override  
    public String getSize()
    {
        if ((mRace == CompConstLogic.RACE_HALFLING) || (mRace == CompConstLogic.RACE_GNOME))
            return "S";
        else
            return "M";
    }
    @Override
    public boolean isLessThanOneHD()
    {
        return mLevel < 1;
    }
    @Override
    public IAttack[] getAttacks(CompContextBean context, boolean firstRank, List<ICombatant> targets)
    {
        // find weapon
        CompItemInstanceBean weapon = null;
        CompItemInstanceBean ammo = null;
        int numAttacks = 1;
        if (firstRank)
        {
            weapon = getHand();
            if (getEffectiveLevel() >= 13)
                numAttacks = 2;
            else if ((getEffectiveLevel() >= 7)
                    && (context.getUser().getEncounter().getRound()
                            % 2 == 1))
                numAttacks++;
            if ((targets.size() > numAttacks) && (getLevel() > numAttacks))
                numAttacks = Math.min(targets.size(), getLevel());
        }
        else
        {
            weapon = getMissile(context.getUser());
            if (weapon != null)
            {
                if (weapon.getType().getType() == CompItemTypeBean.TYPE_LAUNCHER)
                {
                    ammo = ItemLogic.getAmmoInstanceFor(context.getUser(), this, weapon);
                    if (ammo == null)
                        return new IAttack[0];
                    //if (ammo != null)
                    //    log("Selecting ammo of "+ammo.getFullName());
                }
                numAttacks = (int)weapon.getType().getRateOfFire();
            }
        }
        if (weapon == null)
            return new IAttack[0];
        //log("using " + weapon.getID() + " " + weapon.getType().getDamageSMRoll());
        int magic = weapon.getType().getMagic();
        if (ammo != null)
            magic = Math.max(magic, ammo.getType().getMagic());

        if (numAttacks < 1)
            numAttacks = 1;
        if (isEffect(CompEffectTypeBean.SPEED))
            numAttacks *= 2;

        IAttack[] attacks = new IAttack[numAttacks];
        attacks[0] = new PlayerAttack(context.getUser(), firstRank, weapon, ammo, magic);
        for (int i = 1; i < attacks.length; i++)
            attacks[i] = attacks[0];
        return attacks;
    }

    public CompItemInstanceBean getHand()
    {
        for (CompItemInstanceBean item : getItems())
            if (item.getType().isHand())
                return item;
        return null;
    }

    public CompItemInstanceBean getMissile(CompUserBean user)
    {
        for (CompItemInstanceBean item : getItems())
            if (item.getType().isMissile() && (ItemLogic.getAmmoInstanceFor(user, this, item) != null))
                return item;
        return null;
    }

    private int getStatModified(int base, String PLUS, String SET)
    {
        int mod = 0;
        int set = 0;
        for (CompEffectInstanceBean effect : mEffects)
            if (effect.getID().equals(PLUS))
            {
                int m = IntegerUtils.parseInt(effect.getSubType());
                if ((m > mod) || (mod == 0))
                    mod = m;
            }
            else if (effect.getID().equals(SET))
            {
                int s = IntegerUtils.parseInt(effect.getSubType());
                if ((s > set) || (set == 0))
                    set = s;
            }
        if (mod == 0)
            if (set == 0)
                return base;
            else
                return set;
        else
            if (set == 0)
                return base + mod;
            else
                return Math.max(set, base + mod);
    }

    public int getSTRModified()
    {
        return getStatModified(mSTR, CompEffectTypeBean.STRENGTH_PLUS, CompEffectTypeBean.STRENGTH_SET);
    }

    public int getINTModified()
    {
        return getStatModified(mINT, CompEffectTypeBean.INTELLIGENCE_PLUS, CompEffectTypeBean.INTELLIGENCE_SET);
    }

    public int getWISModified()
    {
        return getStatModified(mWIS, CompEffectTypeBean.WISDOM_PLUS, CompEffectTypeBean.WISDOM_SET);
    }

    public int getCONModified()
    {
        return getStatModified(mCON, CompEffectTypeBean.CONSTITUTION_PLUS, CompEffectTypeBean.CONSTITUTION_SET);
    }

    public int getDEXModified()
    {
        return getStatModified(mDEX, CompEffectTypeBean.DEXTERITY_PLUS, CompEffectTypeBean.DEXTERITY_SET);
    }

    public int getCHAModified()
    {
        return getStatModified(mCHA, CompEffectTypeBean.CHARISMA_PLUS, CompEffectTypeBean.CHARISMA_SET);
    }
    
    @Override
    public String toString()
    {
        return mName;
    }
    
    public boolean isMale()
    {
        return mGender == 0;
    }
    
    public CompItemInstanceBean getItem(String id)
    {
        for (CompItemInstanceBean c : getItems())
            if (c.getID().equals(id))
                return c;
        return null;
    }
    
    public int getEffectiveLevel()
    {
        int level = mLevel;
        for (CompEffectInstanceBean e : mEffects)
            if (e.getID().equals(CompEffectTypeBean.LEVEL_DRAIN))
                level -= IntegerUtils.parseInt(e.getSubType());
        return level;
    }
    
    public int getEffectiveHitPoints()
    {
        int elevel = getEffectiveLevel();
        int level = getLevel();
        if (elevel == level)
            return getHitPoints();
        int hp = getHitPoints()*elevel/level;
        return hp;
    }

    public String getOldestProficiency()
    {
        String oldest = null;
        for (String prof : mProfUse.keySet())
            if ((oldest == null) || (mProfUse.get(prof) < mProfUse.get(oldest)))
                oldest = prof;
        return oldest;
    }
    
    public int getTHAC0()
    {
        return 21 - getEffectiveLevel();
    }
    
    public int getTHAC(int ac)
    {
        return getTHAC0() - ac;
    }

    public int getSTRBonusToHit()
    {
        int str = getSTRModified();
        if (str <= 3)
            return -3;
        else if (str <= 2)
            return -2;
        else if (str <= 1)
            return -1;
        else if (str <= 16)
            return 0;
        else if (str == 17)
            return 1;
        else if (str >= 25)
            return 7;
        else if (str >= 24)
            return 6;
        else if (str >= 23)
            return 5;
        else if (str >= 21)
            return 4;
        else if (str >= 19)
            return 3;
        else if (mSTRpc <= 50)
            return 1;
        else
            return 2;
    }
    
    public int getSTRBonusToDamage()
    {
        int str = getSTRModified();
        if (str <= 5)
            return -1;
        else if (str <= 15)
            return 0;
        else if (str <= 17)
            return 1;
        else if (str >= 25)
            return 14;
        else if (str >= 24)
            return 12;
        else if (str >= 23)
            return 11;
        else if (str >= 22)
            return 10;
        else if (str >= 21)
            return 9;
        else if (str >= 20)
            return 8;
        else if (str >= 19)
            return 7;
        else if (mSTRpc <= 50)
            return 3;
        else if (mSTRpc <= 90)
            return 4;
        else
            return 5;
    }
    
    public int getDEXBonusToHit()
    {
        int dex = getDEXModified();
        if (dex <= 3)
            return -3;
        else if (dex == 4)
            return -2;
        else if (dex == 4)
            return 1;
        else if (dex <= 15)
            return 0;
        else
            return dex - 15;
    }
    
    public int getDEXBonusToAC()
    {
        int dex = getDEXModified();
        if (dex <= 6)
            return 7 - dex;
        else if (dex >= 15)
            return 14 - dex;
        return 0;
    }

    public int getAC()
    {
        int ac = 10;
        for (CompItemInstanceBean item : getItems())
            if (item.getType().getType() == CompItemTypeBean.TYPE_ARMOR)
                ac += item.getType().getACMod();
            else if (item.getType().getType() == CompItemTypeBean.TYPE_SHIELD)
                ac += item.getType().getACMod();
        ac += getDEXBonusToAC();
        return ac;
    }

    public CompEffectInstanceBean getEffect(String type, String subType)
    {
        for (CompEffectInstanceBean e : getEffects())
            if (e.getID().equalsIgnoreCase(type) && ((subType == null) || subType.equalsIgnoreCase(e.getSubType())))
                return e;
        return null;
    }
    
    public boolean isEffect(String type)
    {
        return getEffect(type, null) != null;
    }
    
    public boolean isEffect(String type, String subType)
    {
        return getEffect(type, subType) != null;
    }

    public int getSaveRodStaffWand()
    {
        int lvl = getEffectiveLevel();
        int save = 18;
        if (lvl <= 0)
            save = 18;
        else if (lvl >= 19)
            save = 4;
        else
            switch (lvl)
            {
                case 1:
                case 2:
                    save = 16;
                    break;
                case 3:
                case 4:
                    save = 15;
                    break;
                case 5:
                case 6:
                    save = 13;
                    break;
                case 7:
                case 8:
                    save = 12;
                    break;
                case 9:
                case 10:
                    save = 10;
                    break;
                case 11:
                case 12:
                    save = 9;
                    break;
                case 13:
                case 14:
                    save = 7;
                    break;
                case 15:
                case 16:
                    save = 6;
                    break;
                case 17:
                case 18:
                    save = 5;
                    break;
            }
        return save;
    }

    public int getSaveBreathWeapon()
    {
        int lvl = getEffectiveLevel();
        int save = 20;
        if (lvl <= 0)
            save = 20;
        else if (lvl >= 19)
            save = 3;
        else
            switch (lvl)
            {
                case 1:
                case 2:
                    save = 17;
                    break;
                case 3:
                case 4:
                    save = 16;
                    break;
                case 5:
                case 6:
                    save = 13;
                    break;
                case 7:
                case 8:
                    save = 12;
                    break;
                case 9:
                case 10:
                    save = 9;
                    break;
                case 11:
                case 12:
                    save = 8;
                    break;
                case 13:
                case 14:
                    save = 5;
                    break;
                case 15:
                case 16:
                    save = 4;
                    break;
                case 17:
                case 18:
                    save = 4;
                    break;
            }
        return save;
    }

    public int getSaveParalyzationPoisonDeath()
    {
        int lvl = getEffectiveLevel();
        int save = 16;
        if (lvl <= 0)
            save = 16;
        else if (lvl >= 19)
            save = 2;
        else
            switch (lvl)
            {
                case 1:
                case 2:
                    save = 14;
                    break;
                case 3:
                case 4:
                    save = 13;
                    break;
                case 5:
                case 6:
                    save = 11;
                    break;
                case 7:
                case 8:
                    save = 10;
                    break;
                case 9:
                case 10:
                    save = 8;
                    break;
                case 11:
                case 12:
                    save = 7;
                    break;
                case 13:
                case 14:
                    save = 5;
                    break;
                case 15:
                case 16:
                    save = 4;
                    break;
                case 17:
                case 18:
                    save = 2;
                    break;
            }
        return save;
    }

    public int getSavePetrificationPolymorph()
    {
        int lvl = getEffectiveLevel();
        int save = 17;
        if (lvl <= 0)
            save = 17;
        else if (lvl >= 19)
            save = 3;
        else
            switch (lvl)
            {
                case 1:
                case 2:
                    save = 15;
                    break;
                case 3:
                case 4:
                    save = 14;
                    break;
                case 5:
                case 6:
                    save = 12;
                    break;
                case 7:
                case 8:
                    save = 11;
                    break;
                case 9:
                case 10:
                    save = 9;
                    break;
                case 11:
                case 12:
                    save = 8;
                    break;
                case 13:
                case 14:
                    save = 6;
                    break;
                case 15:
                case 16:
                    save = 5;
                    break;
                case 17:
                case 18:
                    save = 4;
                    break;
            }
        return save;
    }

    public int getSaveSpells()
    {
        int lvl = getEffectiveLevel();
        int save = 19;
        if (lvl <= 0)
            save = 19;
        else if (lvl >= 19)
            save = 5;
        else
            switch (lvl)
            {
                case 1:
                case 2:
                    save = 17;
                    break;
                case 3:
                case 4:
                    save = 16;
                    break;
                case 5:
                case 6:
                    save = 14;
                    break;
                case 7:
                case 8:
                    save = 13;
                    break;
                case 9:
                case 10:
                    save = 11;
                    break;
                case 11:
                case 12:
                    save = 10;
                    break;
                case 13:
                case 14:
                    save = 8;
                    break;
                case 15:
                case 16:
                    save = 7;
                    break;
                case 17:
                case 18:
                    save = 6;
                    break;
            }
        return save;
    }
    
    public int getSave(String what)
    {
        switch (what.toLowerCase())
        {
            case "rsw":
            case "rod":
            case "staff":
            case "wand":
            {
                int save = getSaveRodStaffWand();
                if (mRace == CompConstLogic.RACE_DWARF)
                    save -= (getCONModified()*2/7/2);
                return save;
            }
            case "bw":
            case "breath":
                return getSaveBreathWeapon();
            case "ppd":
            case "paralysation":
            case "poison":
            case "death":
            {
                int save = getSaveParalyzationPoisonDeath();
                if (what.equalsIgnoreCase("poison") && (mRace == CompConstLogic.RACE_DWARF))
                    save -= (getCONModified()*2/7/2);
                return save;
            }
            case "pop":
            case "petrification":
            case "polymorph":
                return getSavePetrificationPolymorph();
            case "sp":
            case "spell":
                return getSaveSpells();
        }
        throw new IllegalStateException("Unknown save: "+what);
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
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public int getSTR()
    {
        return mSTR;
    }
    public void setSTR(int sTR)
    {
        mSTR = sTR;
    }
    public int getDEX()
    {
        return mDEX;
    }
    public void setDEX(int dEX)
    {
        mDEX = dEX;
    }
    public int getCON()
    {
        return mCON;
    }
    public void setCON(int cON)
    {
        mCON = cON;
    }
    public int getINT()
    {
        return mINT;
    }
    public void setINT(int iNT)
    {
        mINT = iNT;
    }
    public int getWIS()
    {
        return mWIS;
    }
    public void setWIS(int wIS)
    {
        mWIS = wIS;
    }
    public int getCHA()
    {
        return mCHA;
    }
    public void setCHA(int cHA)
    {
        mCHA = cHA;
    }
    public int getSTRpc()
    {
        return mSTRpc;
    }
    public void setSTRpc(int sTRpc)
    {
        mSTRpc = sTRpc;
    }
    public int getRace()
    {
        return mRace;
    }
    public void setRace(int race)
    {
        mRace = race;
    }
    public int getClazz()
    {
        return mClazz;
    }
    public void setClazz(int clazz)
    {
        mClazz = clazz;
    }
    public int getLevel()
    {
        return mLevel;
    }
    public void setLevel(int level)
    {
        mLevel = level;
    }
    public int getHitPoints()
    {
        return mHitPoints;
    }
    public void setHitPoints(int hitPoints)
    {
        mHitPoints = hitPoints;
    }
    public int getExperiencePoints()
    {
        return mExperiencePoints;
    }
    public void setExperiencePoints(int experiencePoints)
    {
        mExperiencePoints = experiencePoints;
    }
    public int getWeaponProficiences()
    {
        return mWeaponProficiences;
    }
    public void setWeaponProficiences(int weaponProficiences)
    {
        mWeaponProficiences = weaponProficiences;
    }
    public int getNonProficientPenalty()
    {
        return mNonProficientPenalty;
    }
    public void setNonProficientPenalty(int nonProficientPenalty)
    {
        mNonProficientPenalty = nonProficientPenalty;
    }
    public List<CompItemInstanceBean> getItems()
    {
        return mItems;
    }
    public void setItems(List<CompItemInstanceBean> items)
    {
        mItems = items;
    }

    public String getID()
    {
        return mID;
    }

    public void setID(String iD)
    {
        mID = iD;
    }

    public int getCurrentHitPoints()
    {
        return mCurrentHitPoints;
    }

    public void setCurrentHitPoints(int currentHitPoints)
    {
        mCurrentHitPoints = currentHitPoints;
    }

    public List<String> getProficiencies()
    {
        return mProficiencies;
    }

    public void setProficiencies(List<String> proficiencies)
    {
        mProficiencies = proficiencies;
    }

    public int getGender()
    {
        return mGender;
    }

    public void setGender(int gender)
    {
        mGender = gender;
    }

    public List<CompEffectInstanceBean> getEffects()
    {
        return mEffects;
    }

    public void setEffects(List<CompEffectInstanceBean> effects)
    {
        mEffects = effects;
    }

    public StringIntegerMap getProfUse()
    {
        return mProfUse;
    }

    public void setProfUse(StringIntegerMap profUse)
    {
        mProfUse = profUse;
    }

    public int getLimitedAttackUse()
    {
        return mLimitedAttackUse;
    }

    public void setLimitedAttackUse(int limitedAttackUse)
    {
        mLimitedAttackUse = limitedAttackUse;
    }
    
    class PlayerAttack implements IAttack
    {
        private CompUserBean mUser;
        private boolean mFirstRank;
        private CompItemInstanceBean mWeapon;
        private CompItemInstanceBean mAmmo;
        private int mMagic;
        
        public PlayerAttack(CompUserBean user, boolean firstRank, CompItemInstanceBean weapon, CompItemInstanceBean ammo, int magic)
        {
            mUser = user;
            mFirstRank = firstRank;
            mWeapon = weapon;
            mAmmo = ammo;
            mMagic = magic;
        }
        
        @Override
        public boolean hasParam(String param)
        {
            return false;
        }
        
        @Override
        public String getParam(String param)
        {
            return null;
        }
        
        @Override
        public int rollDamage(CompContextBean context, Random rnd, ICombatant target)
        {
            // compute damage
            DamageRollBean diceRoll;
            if (target.getSize().startsWith("S")
                    || target.getSize().startsWith("M"))
                diceRoll = (mAmmo != null) ? mAmmo.getType().getDamageSMRoll() : mWeapon.getType().getDamageSMRoll();
            else
                diceRoll = (mAmmo != null) ? mAmmo.getType().getDamageLRoll() : mWeapon.getType().getDamageLRoll();
            int damage = diceRoll.roll(BaseUserState.RND);
            if (mFirstRank)
                damage += CompCompanionBean.this.getSTRBonusToDamage();
            // use ammo
            if (!mFirstRank)
                useAmmo(context, context.getUser(), CompCompanionBean.this, mWeapon, (mAmmo != null) ? mAmmo : mWeapon);
            // effect
            if (target instanceof CompMonsterInstanceBean)
                damage = EffectCompanionLogic.modifyDamage(CompCompanionBean.this, (CompMonsterInstanceBean)target, diceRoll, damage);

            return damage;
        }

        private void useAmmo(CompContextBean context, CompUserBean user, CompCompanionBean player, CompItemInstanceBean weapon, CompItemInstanceBean ammo)
        {
            if (ammo == null)
                return;
            if (ItemLogic.isAny(user.getItems(), ammo.getID()))
            {
                ItemLogic.removeItem(user.getItems(), ammo.getID(), 1);
                ItemLogic.addItem(user.getEncounter().getDiscards(), ammo.getID(), 1);
            }
            else if (ItemLogic.isAny(player.getItems(), ammo.getID()))
            {
                ItemLogic.removeItem(player.getItems(), ammo.getID(), 1);
                ItemLogic.addItem(user.getEncounter().getDiscards(), ammo.getID(), 1);
            }
            else
                DebugUtils.trace("How could we fight with "+weapon.getFullName()+" when we don't have any "+ammo.getFullName()+"?");
            if (ItemLogic.getAmmoInstanceFor(user, player, weapon) == null)
                context.addMessage(CompanionsModelConst.TEXT_YOU_ARE_OUT_OF_XXX, ammo.getType().getName());
        }

        @Override
        public int bonusToHit(ICombatant target)
        {
            int bonus = 0;
            if (mFirstRank)
                bonus += CompCompanionBean.this.getSTRBonusToHit();
            else
                bonus += CompCompanionBean.this.getDEXBonusToHit();
            if (!CompCompanionBean.this.getProficiencies().contains(mWeapon.getType().getBaseID()))
                bonus -= CompCompanionBean.this.getNonProficientPenalty();
            else
                CompCompanionBean.this.getProfUse().put(mWeapon.getType().getBaseID(), (int)mUser.getTotalTime());
            bonus += mWeapon.getType().getMagic();
            if (mWeapon.getType().isSpecial("bane-"+target.getPhylum()))
                bonus += mWeapon.getType().getMagic();
            if (mAmmo != null)
            {
                bonus += mAmmo.getType().getMagic();
                if (mAmmo.getType().isSpecial("bane-"+target.getPhylum()))
                    bonus += mAmmo.getType().getMagic();
            }
            return bonus;
        }
        
        @Override
        public int getMagic()
        {
            return mMagic;
        }
        
        @Override
        public String getName()
        {
            return mWeapon.getFullName();
        }
        
        @Override
        public void effect(FightDetails fight, ICombatant target)
        {
            for (CompEffectTypeBean effect : mWeapon.getType().getEffects())
            {
                if (!(target instanceof CompMonsterInstanceBean))
                    DebugUtils.trace("Effect from weapon="+mWeapon.toJSON().toJSONString()+", type="+mWeapon.getType().toJSON().toJSONString());
                EffectMonsterLogic.effect(fight, CompCompanionBean.this, (CompMonsterInstanceBean)target, effect);
            }
        }
    }
}
