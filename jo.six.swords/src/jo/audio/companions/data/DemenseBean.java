package jo.audio.companions.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.companions.logic.DemenseLogic;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.obj.StringUtils;

public class DemenseBean implements IJSONAble
{
    private String              mID;
    private AudioMessageBean    mName;
    private String              mLiegeID;
    private DemenseBean         mLiege;
    private List<DemenseBean>   mVassals = new ArrayList<>();
    private String              mPantheon;
    private String              mLanguage;
    
    public DemenseBean()
    {
        super();
    }
    
    // utilities    
    @Override
    public String toString()
    {
        return mID;
    }    
    
    public String toFullString()
    {
        if (mLiege != null)
            return mLiege.toFullString()+"/"+mID;
        else
            return mID;
    }    
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof DemenseBean))
            return false;
        return getID().equals(((DemenseBean)obj).getID());
    }
    
    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("ID", mID);
        json.put("name", mName.getIdent());
        if (!StringUtils.isTrivial(mLanguage))
            json.put("language", mLanguage);
        if (!StringUtils.isTrivial(mLiegeID))
            json.put("liegeID", mLiegeID);
        if (!StringUtils.isTrivial(mPantheon))
            json.put("pantheon", mPantheon);
        return json;
    }
    @Override
    public void fromJSON(JSONObject o)
    {
        mID = o.getString("ID");
        mName = new AudioMessageBean(o.getString("name"));
        mLiegeID = o.getString("liegeID");
        mPantheon = o.getString("pantheon");
        mLanguage = o.getString("language");
    }
    
    public AudioMessageBean getFullName()
    {
        AudioMessageBean full = new AudioMessageBean();
        full.setArgs(new Object[] { mName });
        String lang = DemenseLogic.getLanguage(this);
        if (mID.startsWith("H"))
            full.setIdent(lang+"_HAMLET_OF");
        else if (mID.startsWith("V"))
            full.setIdent(lang+"_VILLAGE_OF");
        else if (mID.startsWith("T"))
            full.setIdent(lang+"_TOWN_OF");
        else if (mID.startsWith("C"))
            full.setIdent(lang+"_CITY_OF");
        else
            full.setIdent(mName.getIdent());
        return full;
    }
    
    // getters and setters
    
    public AudioMessageBean getName()
    {
        return mName;
    }
    public void setName(AudioMessageBean name)
    {
        mName = name;
    }
    public String getLiegeID()
    {
        return mLiegeID;
    }
    public void setLiegeID(String liegeID)
    {
        mLiegeID = liegeID;
    }
    public DemenseBean getLiege()
    {
        return mLiege;
    }
    public void setLiege(DemenseBean liege)
    {
        mLiege = liege;
    }
    public List<DemenseBean> getVassals()
    {
        return mVassals;
    }
    public void setVassals(List<DemenseBean> vassals)
    {
        mVassals = vassals;
    }
    public String getPantheon()
    {
        return mPantheon;
    }
    public void setPantheon(String pantheon)
    {
        mPantheon = pantheon;
    }
    public String getLanguage()
    {
        return mLanguage;
    }
    public void setLanguage(String language)
    {
        mLanguage = language;
    }
    public String getID()
    {
        return mID;
    }
    public void setID(String iD)
    {
        mID = iD;
    }
}
