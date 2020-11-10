package jo.audio.companions.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.IJSONAble;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

public class CompItemTypeBean implements IJSONAble
{
    public static final int TYPE_HAND = 0;
    public static final int TYPE_HURLED = 1;
    public static final int TYPE_AMMO = 2;
    public static final int TYPE_LAUNCHER = 3;
    public static final int TYPE_ARMOR = 4;
    public static final int TYPE_SHIELD = 5;
    public static final int TYPE_POTION = 6;
    public static final int TYPE_CASTLE = 7;
    public static final int TYPE_PERMIT = 8;

    public static final int[] TYPES = {
            TYPE_HAND, TYPE_HURLED, TYPE_AMMO, TYPE_LAUNCHER, TYPE_ARMOR, TYPE_SHIELD,
            TYPE_POTION, TYPE_CASTLE, TYPE_PERMIT,
        };

    public static final boolean[] GENERATE = {
            true, true, true, true, true, true,
            true, false, false,
        };
        
    public static final String TYPE_NAME_HAND = "hand";
    public static final String TYPE_NAME_HURLED = "hurled";
    public static final String TYPE_NAME_AMMO = "ammo";
    public static final String TYPE_NAME_LAUNCHER = "launcher";
    public static final String TYPE_NAME_ARMOR = "armor";
    public static final String TYPE_NAME_SHIELD = "shield";
    public static final String TYPE_NAME_POTION = "potion";
    public static final String TYPE_NAME_CASTLE = "castle";
    public static final String TYPE_NAME_PERMIT = "permit";
    
    public static final String[] TYPE_NAMES = {
        TYPE_NAME_HAND, TYPE_NAME_HURLED, TYPE_NAME_AMMO, TYPE_NAME_LAUNCHER, TYPE_NAME_ARMOR, TYPE_NAME_SHIELD,
        TYPE_NAME_POTION, TYPE_NAME_CASTLE, TYPE_NAME_PERMIT,
    };
    
    private String  mID;
    private int     mType;
    private String  mName;
    private int     mCount;
    private int     mMagic;
    private String  mDamageSM;
    private String  mDamageL;
    private String  mAmmo;
    private float   mEncumbrance;
    private float   mCost;
    private float   mRateOfFire;
    private int     mHandsNeeded;
    private int     mACMod;
    private List<CompEffectTypeBean>  mEffects = new ArrayList<>();
    private String  mSpecial;
    // transient
    private DamageRollBean mDamageSMRoll = null;
    private DamageRollBean mDamageLRoll = null;
    
    // utilities
    
    public boolean isEquipable()
    {
        return ((CompItemTypeBean.TYPE_HURLED == mType) || (CompItemTypeBean.TYPE_LAUNCHER == mType) 
                || (CompItemTypeBean.TYPE_HAND == mType) || (CompItemTypeBean.TYPE_ARMOR == mType)
                || (CompItemTypeBean.TYPE_SHIELD == mType));
    }
    
    public boolean isLearnable()
    {
        return ((CompItemTypeBean.TYPE_HURLED == mType) || (CompItemTypeBean.TYPE_LAUNCHER == mType) 
                || (CompItemTypeBean.TYPE_HAND == mType));
    }
    
    public boolean isWeapon()
    {
        return ((CompItemTypeBean.TYPE_HURLED == mType) || (CompItemTypeBean.TYPE_LAUNCHER == mType) 
                || (CompItemTypeBean.TYPE_HAND == mType));
    }

    public boolean isMissile()
    {
        return ((CompItemTypeBean.TYPE_HURLED == mType) || (CompItemTypeBean.TYPE_LAUNCHER == mType));
    }

    public boolean isHand()
    {
        return ((CompItemTypeBean.TYPE_HURLED == mType) || (CompItemTypeBean.TYPE_HAND == mType));
    }

    public boolean isPotion()
    {
        return (CompItemTypeBean.TYPE_POTION == mType);
    }
    
    public boolean isSpecial(String type)
    {
        if (mSpecial == null)
            return false;
        return mSpecial.indexOf(type) >= 0;
    }

    public boolean isTest()
    {
        return isSpecial("test");
    }

    public boolean isMustHave()
    {
        return isSpecial("mustHave");
    }
    
    public DamageRollBean getDamageSMRoll()
    {
        if ((mDamageSMRoll == null) && (mDamageSM != null))
            mDamageSMRoll = new DamageRollBean(mDamageSM);
        return mDamageSMRoll;
    }
    
    public DamageRollBean getDamageLRoll()
    {
        if ((mDamageLRoll == null) && (mDamageL != null))
            mDamageLRoll = new DamageRollBean(mDamageL);
        return mDamageLRoll;
    }

    private static String[] SIMPLE_DATA_FIELDS = {
            "id", "name", "count", "damageSM",
            "damageL", "encumbrance", "cost", "rateOfFire", "handsNeeded", "ACMod" ,
            "ammo", "magic", "special"
        };

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject toJSON()
    {
        JSONObject u = new JSONObject();
        JSONUtils.setToData(this, u, SIMPLE_DATA_FIELDS);
        u.put("type", TYPE_NAMES[getType()]);
        JSONArray effects = new JSONArray();
        for (CompEffectTypeBean effect : mEffects)
            effects.add(effect.toJSON());
        u.put("effects", effects);
        return u;
    }
    @Override
    public void fromJSON(JSONObject o)
    {
        JSONUtils.getFromData(this, o, SIMPLE_DATA_FIELDS);
        setType(convType2Int((String)o.getString("type")));
        Object e = o.get("effect"); 
        if (e instanceof JSONObject)
        {
            CompEffectTypeBean effect = new CompEffectTypeBean();
            effect.fromJSON((JSONObject)e);
            mEffects.add(effect);
        }
        else if (e instanceof JSONArray)
        {
            JSONArray effects = (JSONArray)e;
            for (Object ef : effects)
                mEffects.add(new CompEffectTypeBean((JSONObject)ef));
        }
    }
    
    public static int convType2Int(String type)
    {
        switch (type)
        {
            case TYPE_NAME_HAND:
                return TYPE_HAND;
            case TYPE_NAME_HURLED:
                return TYPE_HURLED;
            case TYPE_NAME_AMMO:
                return TYPE_AMMO;
            case TYPE_NAME_LAUNCHER:
                return TYPE_LAUNCHER;
            case TYPE_NAME_ARMOR:
                return TYPE_ARMOR;
            case TYPE_NAME_SHIELD:
                return TYPE_SHIELD;
            case TYPE_NAME_POTION:
                return TYPE_POTION;
            case TYPE_NAME_CASTLE:
                return TYPE_CASTLE;
            case TYPE_NAME_PERMIT:
                return TYPE_PERMIT;
            default:
                throw new IllegalArgumentException("Unknown type "+type);
        }
    }

    public String getBaseID()
    {
        int o = mID.indexOf('+');
        if (o < 0)
            return mID;
        else
            return mID.substring(0, o);
    }
    
    // getters and setters
    
    public int getType()
    {
        return mType;
    }
    public void setType(int type)
    {
        mType = type;
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
    public String getDamageSM()
    {
        return mDamageSM;
    }
    public void setDamageSM(String damageSM)
    {
        mDamageSM = damageSM;
    }
    public String getDamageL()
    {
        return mDamageL;
    }
    public void setDamageL(String damageL)
    {
        mDamageL = damageL;
    }
    public float getCost()
    {
        return mCost;
    }
    public void setCost(float cost)
    {
        mCost = cost;
    }
    public float getRateOfFire()
    {
        return mRateOfFire;
    }
    public void setRateOfFire(float rateOfFire)
    {
        mRateOfFire = rateOfFire;
    }
    public int getHandsNeeded()
    {
        return mHandsNeeded;
    }
    public void setHandsNeeded(int handsNeeded)
    {
        mHandsNeeded = handsNeeded;
    }
    public int getACMod()
    {
        return mACMod;
    }
    public void setACMod(int aCMod)
    {
        mACMod = aCMod;
    }
    public float getEncumbrance()
    {
        return mEncumbrance;
    }
    public void setEncumbrance(float encumbrance)
    {
        mEncumbrance = encumbrance;
    }
    public String getID()
    {
        return mID;
    }
    public void setID(String iD)
    {
        mID = iD;
    }

    public String getAmmo()
    {
        return mAmmo;
    }

    public void setAmmo(String ammo)
    {
        mAmmo = ammo;
    }

    public int getMagic()
    {
        return mMagic;
    }

    public void setMagic(int magic)
    {
        mMagic = magic;
    }

    public String getSpecial()
    {
        return mSpecial;
    }

    public void setSpecial(String special)
    {
        mSpecial = special;
    }

    public List<CompEffectTypeBean> getEffects()
    {
        return mEffects;
    }

    public void setEffects(List<CompEffectTypeBean> effects)
    {
        mEffects = effects;
    }
}
