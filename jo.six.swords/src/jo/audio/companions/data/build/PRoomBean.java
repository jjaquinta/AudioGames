package jo.audio.companions.data.build;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.logic.feature.BuildLogic;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.beans.PCSBean;

public class PRoomBean extends PCSBean
{
    private String mID;
    private String mName;
    private String mDescription;
    private String mType;
    private String mNorth;
    private String mSouth;
    private String mEast;
    private String mWest;
    private JSONObject  mParams;
    
    // utilities
    
    @Override
    public String toString()
    {
        return mName;
    }
    
    public JSONObject   toJSON(PModuleBean module)
    {
        JSONObject json = new JSONObject();
        json.put("ID", mID);
        json.put("name", BuildLogic.nameToJSON(BuildLogic.setText(module, mName)));
        json.put("description", BuildLogic.nameToJSON(BuildLogic.setText(module, mDescription)));
        json.put("type", mType);
        json.put("north", mNorth);
        json.put("south", mSouth);
        json.put("east", mEast);
        json.put("west", mWest);
        if (mParams == null)
            json.put("params", mParams);
        else
        {
            JSONObject params = (JSONObject)JSONUtils.deepCopy(mParams);
            if (params.containsKey(CompRoomBean.MD_ENCOUNTER_ANNOUNCE))
            {
                String text = params.getString(CompRoomBean.MD_ENCOUNTER_ANNOUNCE);
                String ident = BuildLogic.setText(module, text);
                params.put(CompRoomBean.MD_ENCOUNTER_ANNOUNCE, ident);
            }
            json.put("params", params);
        }
        return json;
    }
    public void fromJSON(PModuleBean module, JSONObject o)
    {
        mID = o.getString("ID");
        mName = BuildLogic.getText(module, BuildLogic.nameFromJSON(o, "name"));
        mDescription = BuildLogic.getText(module, BuildLogic.nameFromJSON(o, "description"));
        mType = o.getString("type");
        mNorth = o.getString("north");
        mSouth = o.getString("south");
        mEast = o.getString("east");
        mWest = o.getString("west");
        mParams = JSONUtils.getObject(o, "params");
        if ((mParams != null) && mParams.containsKey(CompRoomBean.MD_ENCOUNTER_ANNOUNCE))
        {
            String id = mParams.getString(CompRoomBean.MD_ENCOUNTER_ANNOUNCE);
            String text = BuildLogic.getText(module, id);
            mParams.put(CompRoomBean.MD_ENCOUNTER_ANNOUNCE, text);
        }
    }
    
    public static final String NORTH = "north";
    public static final String SOUTH = "south";
    public static final String EAST = "east";
    public static final String WEST = "west";
    public static final String[] DIRS = new String[] {NORTH, SOUTH, EAST, WEST};
    
    public static String opposite(String dir)
    {
        switch (dir)
        {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
        }
        throw new IllegalStateException();
    }
    
    public static int opposite(int dir)
    {
        switch (dir)
        {
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
                return 3;
            case 3:
                return 2;
        }
        throw new IllegalStateException();
    }
    
    private static final String[] DIR_LOCK = new String[] { CompRoomBean.MD_LOCK_NORTH, CompRoomBean.MD_LOCK_SOUTH, CompRoomBean.MD_LOCK_EAST, CompRoomBean.MD_LOCK_WEST }; 

    public JSONObject getDirectionLock(int dir)
    {
        if (getParams() == null)
            return null;
        return (JSONObject)getParams().get(DIR_LOCK[dir%4]);
    }

    public void setDirectionLock(int dir, JSONObject lock)
    {
        if (getParams() == null)
            setParams(new JSONObject());
        if (lock == null)
            getParams().remove(DIR_LOCK[dir%4]);
        else
            getParams().put(DIR_LOCK[dir%4], lock);
    }

    private static final String[] DIR_DESC = new String[] { CompRoomBean.MD_DESC_NORTH, CompRoomBean.MD_DESC_SOUTH, CompRoomBean.MD_DESC_EAST, CompRoomBean.MD_DESC_WEST }; 

    public AudioMessageBean getDirectionDesc(int dir)
    {
        if ((getParams() == null) || !getParams().containsKey(DIR_DESC[dir%4]))
            return null;
        return new AudioMessageBean((JSONObject)getParams().get(DIR_DESC[dir%4]));
    }

    public void setDirectionDesc(int dir, AudioMessageBean desc)
    {
        if (getParams() == null)
            setParams(new JSONObject());
        if (desc == null)
            getParams().remove(DIR_DESC[dir%4]);
        else
            getParams().put(DIR_DESC[dir%4], desc.toJSON());
    }

    public void setDir(String dir, String value)
    {
        switch (dir.toLowerCase())
        {
            case "north":
                setNorth(value);
                break;
            case "south":
                setSouth(value);
                break;
            case "east":
                setEast(value);
                break;
            case "west":
                setWest(value);
                break;
        }
    }
    
    public void setDir(int dir, String value)
    {
        switch (dir)
        {
            case 0:
                setNorth(value);
                break;
            case 1:
                setSouth(value);
                break;
            case 2:
                setEast(value);
                break;
            case 3:
                setWest(value);
                break;
        }
    }
    
    public String getDir(String dir)
    {
        switch (dir.toLowerCase())
        {
            case "north":
                return getNorth();
            case "south":
                return getSouth();
            case "east":
                return getEast();
            case "west":
                return getWest();
        }
        return null;
    }
    
    public String getDir(int dir)
    {
        switch (dir)
        {
            case 0:
                return getNorth();
            case 1:
                return getSouth();
            case 2:
                return getEast();
            case 3:
                return getWest();
        }
        return null;
    }
    
    // getters and setters
    public String getID() { return mID; } public void setID(String ID) { queuePropertyChange("ID", mID, ID); mID = ID; firePropertyChange(); }
    public String getName() { return mName; } public void setName(String Name) { queuePropertyChange("Name", mName, Name); mName = Name; firePropertyChange(); }
    public String getDescription() { return mDescription; } public void setDescription(String Description) { queuePropertyChange("Description", mDescription, Description); mDescription = Description; firePropertyChange(); }
    public String getType() { return mType; } public void setType(String Type) { queuePropertyChange("Type", mType, Type); mType = Type; firePropertyChange(); }
    public String getNorth() { return mNorth; } public void setNorth(String North) { queuePropertyChange("North", mNorth, North); mNorth = North; firePropertyChange(); }
    public String getSouth() { return mSouth; } public void setSouth(String South) { queuePropertyChange("South", mSouth, South); mSouth = South; firePropertyChange(); }
    public String getEast() { return mEast; } public void setEast(String East) { queuePropertyChange("East", mEast, East); mEast = East; firePropertyChange(); }
    public String getWest() { return mWest; } public void setWest(String West) { queuePropertyChange("West", mWest, West); mWest = West; firePropertyChange(); }
    public JSONObject getParams() { return mParams; } public void setParams(JSONObject Params) { queuePropertyChange("Params", mParams, Params); mParams = Params; firePropertyChange(); }
}
