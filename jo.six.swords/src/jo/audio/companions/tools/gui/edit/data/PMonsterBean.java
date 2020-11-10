package jo.audio.companions.tools.gui.edit.data;

import java.util.StringTokenizer;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.DamageRollBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.HitDiceBean;
import jo.audio.companions.data.IMonsterType;
import jo.util.beans.PCSBean;

public class PMonsterBean extends PCSBean implements IJSONAble, IMonsterType
{
    private String mID;
    private String mATT;
    private String mName;
    private String mMove;
    private String mAC;
    private String mNumAtt;
    private String mFreq;
    private String mSize;
    private String mTreasure;
    private String mEnc;
    private String mType;
    private String mHD;
    private String mTerrain;
    private String mSpecial = "";
    private JSONObject mDetails;

    // utilities
    public JSONObject   toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("ID", mID);
        json.put("att", mATT);
        json.put("move", mMove);
        json.put("ac", mAC);
        json.put("numAtt", mNumAtt);
        json.put("size", mSize);
        json.put("freq", mFreq);
        json.put("name", mName);
        json.put("treasure", mTreasure);
        json.put("enc", mEnc);
        json.put("type", mType);
        json.put("HD", mHD);
        json.put("terrain", mTerrain);
        json.put("special", mSpecial);
        json.put("details", mDetails);
        return json;
    }
    public void fromJSON(JSONObject o)
    {
        mID = o.getString("ID");
        if (o.containsKey("ATT"))
            mATT = o.getString("ATT");
        else
            mATT = o.getString("att");
        mMove = o.getString("move");
        mAC = o.getString("ac");
        mNumAtt = o.getString("numAtt");
        mSize = o.getString("size");
        mFreq = o.getString("freq");
        mName = o.getString("name");
        mTreasure = o.getString("treasure");
        mEnc = o.getString("enc");
        mType = o.getString("type");
        mHD = o.getString("HD");
        mTerrain = o.getString("terrain");
        mSpecial = o.getString("special");
        mDetails = JSONUtils.getObject(o, "details");
    }
    
    @Override
    public String toString()
    {
        return "[id="+mID+", name="+mName+"]";
    }
    
    // +ve values in favor of monster, -ve values in favor of player
    public float defeatMargin(float playerHP, float playerDPR, int playerAC)
    {
        DiceRollBean encRoll = getEncRoll();
        HitDiceBean hdRoll = getHDRoll();
        float monsterHP = encRoll.average()*hdRoll.average();
        float monsterDPR = getAverageDamage(playerAC);
        float roundsToKillMonster = monsterHP/playerDPR;
        float roundsToKillPlayer = playerHP/monsterDPR;
        return roundsToKillMonster - roundsToKillPlayer;
    }
    public DiceRollBean getEncRoll()
    {
        DiceRollBean encRoll = new DiceRollBean(mEnc);
        return encRoll;
    }
    public HitDiceBean getHDRoll()
    {
        HitDiceBean hdRoll = new HitDiceBean(mHD);
        return hdRoll;
    }
    
    public float getAverageDamage()
    {
        float dam = 0;
        for (DiceRollBean att : getAttRolls())
            dam += att.average();
        dam *= getEncRoll().average();
        return dam;
    }

    public DamageRollBean[] getAttRolls()
    {
        int numAtt = Integer.parseInt(getNumAtt());
        DamageRollBean[] attRolls = new DamageRollBean[numAtt];
        StringTokenizer st = new StringTokenizer(getATT(), "/");
        int idx = 0;
        while (st.hasMoreTokens())
            attRolls[idx++] = new DamageRollBean(st.nextToken());
        while (idx < attRolls.length)
            attRolls[idx++] = attRolls[0];
        return attRolls;
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

    
    public int getEquivalentLevel()
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
        return level;
    }
    
    public int getTHAC0()
    {
        return 21 - getEquivalentLevel();
    }
    
    public int getTHAC(int ac)
    {
        return getTHAC0() - ac;
    }

    // getters and setters

    public String getATT()
    {
        return mATT;
    }
    public void setATT(String att)
    {
        queuePropertyChange("att", mATT, att);
        mATT = att;
        firePropertyChange();
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        queuePropertyChange("name", mName, name);
        mName = name;
        firePropertyChange();
    }
    public String getMove()
    {
        return mMove;
    }
    public void setMove(String move)
    {
        queuePropertyChange("move", mMove, move);
        mMove = move;
        firePropertyChange();
    }
    public String getAC()
    {
        return mAC;
    }
    public void setAC(String aC)
    {
        queuePropertyChange("AC", mAC, aC);
        mAC = aC;
        firePropertyChange();
    }
    public String getNumAtt()
    {
        return mNumAtt;
    }
    public void setNumAtt(String numAtt)
    {
        queuePropertyChange("numAtt", mNumAtt, numAtt);
        mNumAtt = numAtt;
        firePropertyChange();
    }
    public String getFreq()
    {
        return mFreq;
    }
    public void setFreq(String freq)
    {
        queuePropertyChange("freq", mFreq, freq);
        mFreq = freq;
        firePropertyChange();
    }
    public String getSize()
    {
        return mSize;
    }
    public void setSize(String size)
    {
        queuePropertyChange("size", mSize, size);
        mSize = size;
        firePropertyChange();
    }
    public String getTreasure()
    {
        return mTreasure;
    }
    public void setTreasure(String treasure)
    {
        queuePropertyChange("treasure", mTreasure, treasure);
        mTreasure = treasure;
        firePropertyChange();
    }
    public String getEnc()
    {
        return mEnc;
    }
    public void setEnc(String enc)
    {
        queuePropertyChange("enc", mEnc, enc);
        mEnc = enc;
        firePropertyChange();
    }
    public String getType()
    {
        return mType;
    }
    public void setType(String type)
    {
        queuePropertyChange("type", mType, type);
        mType = type;
        firePropertyChange();
    }
    public String getHD()
    {
        return mHD;
    }
    public void setHD(String hD)
    {
        queuePropertyChange("HD", mHD, hD);
        mHD = hD;
        firePropertyChange();
    }
    public String getTerrain()
    {
        return mTerrain;
    }
    public void setTerrain(String terrain)
    {
        queuePropertyChange("terrain", mTerrain, terrain);
        mTerrain = terrain;
        firePropertyChange();
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
}
