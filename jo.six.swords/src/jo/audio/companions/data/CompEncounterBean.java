package jo.audio.companions.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;

public class CompEncounterBean implements IJSONAble
{
    private int mRound;
    private List<CompMonsterInstanceBean> mMonsters = new ArrayList<>();
    private List<CompMonsterInstanceBean> mAllies = new ArrayList<>();
    private List<DiceRollBean> mTreasures = new ArrayList<>();
    private List<ItemSelectBean> mTreasureItems = new ArrayList<>();
    private List<CompItemInstanceBean> mDiscards = new ArrayList<>();
    private List<CompMonsterInstanceBean> mKills = new ArrayList<>();
    private int mXP;
    private String mLastSpoorID;
    private int    mLastSpoorInteraction;
    private int    mInitialHits;
    private float  mMorale;
    private String mAnnounce;
    private String mAnnouncedWeapons = "";
    
    // utilities
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
    
    public int getCurrentHits()
    {
        int hits = 0;
        for (CompMonsterInstanceBean monst : getMonsters())
            hits += monst.getHitPoints();
        return hits;
    }

    // getters and setters

    public List<CompMonsterInstanceBean> getMonsters()
    {
        return mMonsters;
    }

    public void setMonsters(List<CompMonsterInstanceBean> monsters)
    {
        mMonsters = monsters;
    }

    public int getRound()
    {
        return mRound;
    }

    public void setRound(int round)
    {
        mRound = round;
    }

    public List<DiceRollBean> getTreasures()
    {
        return mTreasures;
    }

    public void setTreasures(List<DiceRollBean> treasures)
    {
        mTreasures = treasures;
    }

    public int getXP()
    {
        return mXP;
    }

    public void setXP(int xP)
    {
        mXP = xP;
    }

    public List<CompItemInstanceBean> getDiscards()
    {
        return mDiscards;
    }

    public void setDiscards(List<CompItemInstanceBean> discards)
    {
        mDiscards = discards;
    }

    public String getLastSpoorID()
    {
        return mLastSpoorID;
    }

    public void setLastSpoorID(String lastSpoorID)
    {
        mLastSpoorID = lastSpoorID;
    }

    public int getLastSpoorInteraction()
    {
        return mLastSpoorInteraction;
    }

    public void setLastSpoorInteraction(int lastSpoorInteraction)
    {
        mLastSpoorInteraction = lastSpoorInteraction;
    }

    public int getInitialHits()
    {
        return mInitialHits;
    }

    public void setInitialHits(int initialHits)
    {
        mInitialHits = initialHits;
    }

    public float getMorale()
    {
        return mMorale;
    }

    public void setMorale(float morale)
    {
        mMorale = morale;
    }

    public String getAnnounce()
    {
        return mAnnounce;
    }

    public void setAnnounce(String announce)
    {
        mAnnounce = announce;
    }

    public List<ItemSelectBean> getTreasureItems()
    {
        return mTreasureItems;
    }

    public void setTreasureItems(List<ItemSelectBean> treasureItems)
    {
        mTreasureItems = treasureItems;
    }

    public List<CompMonsterInstanceBean> getAllies()
    {
        return mAllies;
    }

    public void setAllies(List<CompMonsterInstanceBean> allies)
    {
        mAllies = allies;
    }

    public List<CompMonsterInstanceBean> getKills()
    {
        return mKills;
    }

    public void setKills(List<CompMonsterInstanceBean> kills)
    {
        mKills = kills;
    }

    public String getAnnouncedWeapons()
    {
        return mAnnouncedWeapons;
    }

    public void setAnnouncedWeapons(String announcedWeapons)
    {
        mAnnouncedWeapons = announcedWeapons;
    }
}
