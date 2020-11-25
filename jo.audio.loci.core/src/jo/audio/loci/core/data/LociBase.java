package jo.audio.loci.core.data;

import org.json.simple.IJSONAble;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.loci.core.logic.DataStoreLogic;

public class LociBase implements IJSONAble
{
    public static final String ID_URI = "uri";
    public static final String ID_DATA_PROFILE = "dataProfile";

    protected JSONObject  mProperties = new JSONObject();

    public LociBase(String uri, String profile)
    {
        // hack this for constructor, otherwise save will be triggered and fail
        mProperties.put(ID_URI, uri);
        mProperties.put(ID_DATA_PROFILE, profile);
    }

    public LociBase(JSONObject json)
    {
        fromJSON(json);
    }
    
    // I/O
    
    @Override
    public JSONObject toJSON()
    {
        return (JSONObject)JSONUtils.deepCopy(mProperties);
    }

    @Override
    public void fromJSON(JSONObject o)
    {
        mProperties = (JSONObject)JSONUtils.deepCopy(o);
    }
    
    // utils
    
    protected void setThing(String key, Object value)
    {
        int o = key.lastIndexOf('.');
        if (o < 0)
            mProperties.put(key, value);
        else
            JSONUtils.getObject(mProperties, key.substring(0, o)).put(key.substring(o+1), value);
        DataStoreLogic.save(this);
    }

    protected String getString(String key)
    {
        return JSONUtils.getString(mProperties, key);
    }
    
    protected void setString(String key, String value)
    {
        setThing(key, value);
    }
    
    protected String[] getStringArray(String key)
    {
        JSONArray jvalue = JSONUtils.getArray(mProperties, key);
        if (jvalue == null)
            return null;
        String[] svalue = new String[jvalue.size()];
        for (int i = 0; i < jvalue.size(); i++)
        {
            Object tvalue = jvalue.get(i);
            if (tvalue != null)
                svalue[i] = tvalue.toString();
        }
        return svalue;
    }
    
    @SuppressWarnings("unchecked")
    protected void setStringArray(String key, String[] value)
    {
        JSONArray json = new JSONArray();
        for (String s : value)
            json.add(s);
        setThing(key, json);
    }

    protected boolean getBoolean(String key)
    {
        return JSONUtils.getBoolean(mProperties, key);
    }
    
    protected void setBoolean(String key, boolean value)
    {
        setThing(key, value);
    }

    protected long getLong(String key)
    {
        return JSONUtils.getLong(mProperties, key);
    }
    
    protected void setLong(String key, Long value)
    {
        setThing(key, value);
    }
    
    // getters and setters
    
    public String getURI()
    {
        return getString(ID_URI);
    }
    
//    public void setURI(String value)
//    {
//        setString(ID_URI, value);
//    }

    public String getDataProfile()
    {
        return getString(ID_DATA_PROFILE);
    }
    
//    public void setDataProfile(String value)
//    {
//        setString(ID_DATA_PROFILE, value);
//    }
}
