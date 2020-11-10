package jo.audio.companions.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.util.ArrayType;
import jo.audio.util.model.data.AudioMessageBean;

public class FeatureBean
{
    private AudioMessageBean mName = new AudioMessageBean();
    private List<CompRoomBean> mRooms = new ArrayList<>();
    private String mEntranceID;
    private int mSubType;
    private Boolean mMonsterTreasure;
    private Boolean mMonsterPopulous;
    private String  mMonsterType;
    private JSONObject mParams;

    // utilities

    public String getLocation()
    {
        return getParam("location");
    }
    public void setLocation(String location)
    {
        setParam("location", location);
    }
    public String getEnabledBy()
    {
        return getParam("enabledBy");
    }
    public void setEnabledBy(String enabledBy)
    {
        setParam("enabledBy", enabledBy);
    }
    public String getAccount()
    {
        return getParam("account");
    }
    public void setAccount(String account)
    {
        setParam("account", account);
    }

    public String getParam(String name)
    {
        if ((mParams == null) || !mParams.containsKey(name))
            return null;
        else
            return mParams.getString(name);
    }
    
    public void setParam(String name, String value)
    {
        if (mParams == null)
            mParams = new JSONObject();
        if (value == null)
            mParams.remove(name);
        else
            mParams.put(name, value);
    }
    
    public void rotate(int angle)
    {
        if (angle == 0)
            return;
        for (CompRoomBean room : mRooms)
            room.rotate(angle);
    }
    
    // getters and setters
    
    @ArrayType(type=CompRoomBean.class)
    public List<CompRoomBean> getRooms()
    {
        return mRooms;
    }
    public void setRooms(List<CompRoomBean> rooms)
    {
        mRooms = rooms;
    }
    public AudioMessageBean getName()
    {
        return mName;
    }
    public void setName(AudioMessageBean name)
    {
        mName = name;
    }
    public String getEntranceID()
    {
        return mEntranceID;
    }
    public void setEntranceID(String entranceID)
    {
        mEntranceID = entranceID;
    }

    public int getSubType()
    {
        return mSubType;
    }

    public void setSubType(int subType)
    {
        mSubType = subType;
    }

    public Boolean getMonsterTreasure()
    {
        return mMonsterTreasure;
    }

    public void setMonsterTreasure(Boolean monsterTreasure)
    {
        mMonsterTreasure = monsterTreasure;
    }

    public Boolean getMonsterPopulous()
    {
        return mMonsterPopulous;
    }

    public void setMonsterPopulous(Boolean monsterPopulous)
    {
        mMonsterPopulous = monsterPopulous;
    }

    public String getMonsterType()
    {
        return mMonsterType;
    }

    public void setMonsterType(String monsterType)
    {
        mMonsterType = monsterType;
    }

    public JSONObject getParams()
    {
        return mParams;
    }

    public void setParams(JSONObject params)
    {
        if (mParams != null)
            for (String key : params.keySet())
                mParams.put(key, params.get(key));
        else
            mParams = params;
    }
}
