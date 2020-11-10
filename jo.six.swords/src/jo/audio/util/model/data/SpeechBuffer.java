package jo.audio.util.model.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.util.utils.obj.IntegerUtils;

public class SpeechBuffer implements IJSONAble
{
    private Map<Integer, StringBuffer> mText = new HashMap<>();
    
    @Override
    public void fromJSON(JSONObject json)
    {
        mText.clear();
        for (String key : json.keySet())
            mText.put(IntegerUtils.parseInt(key), new StringBuffer(json.getString(key)));
    }
    
    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        for (Integer key : mText.keySet())
            json.put(key.toString(), mText.get(key).toString());
        return json;
    }
    
    public void append(String txt)
    {
        if (txt == null)
            return;
        append(0, txt);
    }

    public void append(int priority, String txt)
    {
        //if (txt.indexOf("java.lang.NullPointerException") >= 0)
        //    throw new IllegalStateException("I think someone is passing a null somewhere.");
        StringBuffer orig = mText.get(priority);
        if (orig == null)
        {
            orig = new StringBuffer();
            mText.put(priority, orig);
        }
        if (txt.length() == 0)
            return;
        if (orig.length() == 0)
            orig.append(txt);
        else
        {
            if (!orig.toString().endsWith(" ") && !txt.startsWith(" "))
                orig.append(" ");
            orig.append(txt);
        }
    }
    
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        Integer[] keys = mText.keySet().toArray(new Integer[0]);
        Arrays.sort(keys);
        for (Integer key : keys)
        {
            StringBuffer txt = mText.get(key);
            if (!sb.toString().endsWith(" ") && !txt.toString().startsWith(" "))
                sb.append(" ");
            sb.append(txt);
        }
        return sb.toString();
    }
}
