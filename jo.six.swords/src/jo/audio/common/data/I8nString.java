package jo.audio.common.data;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

public class I8nString implements IJSONAble
{
    private Map<String, String> mValue = new HashMap<>();
    
    // utilities

    @Override
    public JSONObject toJSON()
    {
        JSONObject ret = new JSONObject();
        for (String key : mValue.keySet())
            ret.put(key, mValue.get(key));
        return ret;
    }

    @Override
    public void fromJSON(JSONObject o)
    {
        for (String key : o.keySet())
            mValue.put(key, o.getString(key));
    }

    public String getValue(String lang)
    {
        if (mValue.containsKey(lang))
            return mValue.get(lang);
        int o = lang.indexOf('_');
        String l = lang.substring(0, o);
        if (mValue.containsKey(l))
            return mValue.get(l);
        if (mValue.containsKey("en_US"))
            return mValue.get("en_US");
        if (mValue.containsKey("en"))
            return mValue.get("en");
        return null;
    }
    public void setValue(String lang, String name)
    {
        mValue.put(lang, name);
    }

    // getters and setters

    public Map<String, String> getValue()
    {
        return mValue;
    }

    public void setValue(Map<String, String> value)
    {
        mValue = value;
    }
    
}
