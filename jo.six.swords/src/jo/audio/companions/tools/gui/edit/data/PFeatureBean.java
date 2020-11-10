package jo.audio.companions.tools.gui.edit.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.tools.gui.edit.rich.FeatureLogic;
import jo.util.beans.PCSBean;

public class PFeatureBean extends PCSBean
{
    private String mName;
    private String mLocation;
    private String mEnabledBy;
    private String mEntranceID;
    private String mMonsterType;
    private List<PRoomBean> mRooms = new ArrayList<>();
    private JSONObject mParams;

    // utilities
    @Override
    public String toString()
    {
        return mName;
    }

    public PRoomBean findRoom(String id)
    {
        for (PRoomBean room : mRooms)
            if (room.getID().equals(id))
                return room;
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public JSONObject   toJSON(PModuleBean module)
    {
        JSONObject json = new JSONObject();
        json.put("oid", (int)getOID());
        json.put("location", mLocation);
        json.put("entranceID", mEntranceID);
        json.put("monsterType", mMonsterType);
        json.put("enabledBy", mEnabledBy);        
        if (mParams != null)
            json.put("params", mParams);        
        json.put("name", FeatureLogic.nameToJSON(FeatureLogic.setText(module, mName)));
        JSONArray rooms = new JSONArray();
        json.put("rooms", rooms);
        for (PRoomBean room : mRooms)
            rooms.add(room.toJSON(module));
        return json;
    }
    public void fromJSON(PModuleBean module, JSONObject o)
    {
        setOID(o.getInt("oid"));
        mLocation = o.getString("location");
        mEntranceID = o.getString("entranceID");
        mMonsterType = o.getString("monsterType");
        mEnabledBy = o.getString("enabledBy");
        if (o.containsKey("params"))
            mParams = (JSONObject)o.get("params");
        mName = FeatureLogic.getText(module, FeatureLogic.nameFromJSON(o, "name"));
        mRooms.clear();
        for (Object oo : JSONUtils.getArray(o, "rooms"))
        {
            PRoomBean room = new PRoomBean();
            room.fromJSON(module, (JSONObject)oo);
            mRooms.add(room);
        }
    }
    
    // getters and setters
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
    public String getLocation()
    {
        return mLocation;
    }
    public void setLocation(String location)
    {
        queuePropertyChange("location", mLocation, location);
        mLocation = location;
        firePropertyChange();
    }
    public String getEnabledBy()
    {
        return mEnabledBy;
    }
    public void setEnabledBy(String enabledBy)
    {
        queuePropertyChange("enabledBy", mEnabledBy, enabledBy);
        mEnabledBy = enabledBy;
        firePropertyChange();
    }
    public String getEntranceID()
    {
        return mEntranceID;
    }
    public void setEntranceID(String entranceID)
    {
        queuePropertyChange("entranceID", mEntranceID, entranceID);
        mEntranceID = entranceID;
        firePropertyChange();
    }
    public List<PRoomBean> getRooms()
    {
        return mRooms;
    }
    public void setRooms(List<PRoomBean> rooms)
    {
        mRooms = rooms;
    }

    public JSONObject getParams()
    {
        return mParams;
    }

    public void setParams(JSONObject params)
    {
        mParams = params;
    }

    public String getMonsterType()
    {
        return mMonsterType;
    }

    public void setMonsterType(String monsterType)
    {
        mMonsterType = monsterType;
    }
}
