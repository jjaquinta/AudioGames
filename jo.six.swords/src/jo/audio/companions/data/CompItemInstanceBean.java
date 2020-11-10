package jo.audio.companions.data;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.companions.logic.ItemLogic;
import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;
import jo.util.utils.obj.StringUtils;

public class CompItemInstanceBean implements IJSONAble
{
    private String              mID;
    private String              mName;
    private int                 mQuantity;
    
    // utilities
    public String getFullName()
    {
        if (StringUtils.isTrivial(mName))
            return getType().getName();
        else
            return mName;
    }
    
    @Override
    public String toString()
    {
        return mID+" x "+mQuantity;
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

    public CompItemTypeBean getType()
    {
        return ItemLogic.getItemType(getID());
    }

    public CompItemTypeBean getBaseType()
    {
        return ItemLogic.getItemType(getType().getBaseID());
    }
    
    // getters and setters
    
    public int getQuantity()
    {
        return mQuantity;
    }
    public void setQuantity(int type)
    {
        mQuantity = type;
    }
    public String getID()
    {
        return mID;
    }
    public void setID(String iD)
    {
        mID = iD;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }
}
