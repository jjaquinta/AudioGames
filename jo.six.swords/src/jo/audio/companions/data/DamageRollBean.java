package jo.audio.companions.data;

import java.util.Properties;
import java.util.StringTokenizer;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;
import jo.util.utils.obj.BooleanUtils;

public class DamageRollBean extends DiceRollBean implements IJSONAble
{
    private Properties  mParams = new Properties();
    
    // constructors
    public DamageRollBean()
    {
        super();
        mParams = null;
    }
    
    public DamageRollBean(int dice)
    {
        super(dice);
    }
    
    public DamageRollBean(int number, int dice)
    {
        super(number, dice);
    }
    
    public DamageRollBean(int number, int dice, int mod)
    {
        super(number, dice, mod);
    }
    
    public DamageRollBean(int number, int dice, int mod, int mult)
    {
        super(number, dice, mod, mult);
    }

    public DamageRollBean(DamageRollBean r2)
    {
        super(r2);
    }
    
    public DamageRollBean(String txt)
    {
        super(txt);
    }
    
    @Override
    protected void fromString(String txt)
    {
        int s = txt.indexOf('{');
        if (s >= 0)
        {
            int e = txt.indexOf('}', s);
            if (e >= 0)
            {
                String param = txt.substring(s+1, e);
                txt = txt.substring(0, s) + txt.substring(e + 1);
                for (StringTokenizer st = new StringTokenizer(param, ","); st.hasMoreTokens(); )
                {
                    if (mParams == null)
                        mParams = new Properties();
                    String p = st.nextToken();
                    int o = p.indexOf('=');
                    if (o >= 0)
                        mParams.put(p.substring(0, o), p.substring(o + 1));
                    else
                        mParams.put(p, "true");
                }
            }
        }
        super.fromString(txt);
    }
    
    
    // utilities
    
    public boolean hasParam(String key)
    {
        if (mParams == null)
            return false;
        else
            return mParams.containsKey(key);
    }
    
    public String getParam(String key)
    {
        if (mParams == null)
            return null;
        else
            return mParams.getProperty(key);
    }
    
    public boolean isParam(String key)
    {
        return BooleanUtils.parseBoolean(getParam(key));
    }
    
    public boolean isParam(String key, String val)
    {
        if (mParams == null)
            return false;
        else
            return mParams.getProperty(key).equalsIgnoreCase(val);
    }
    
    @Override
    public String toString()
    {
        String txt = super.toString();
        if (mParams != null)            
        {
            txt += "{";
            for (Object k : mParams.keySet())
            {
                if (!txt.endsWith("{"))
                    txt += ",";
                txt += k+"="+mParams.get(k);
            }
            txt += "}";
        }
        return txt;
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

    public Properties getParams()
    {
        return mParams;
    }

    public void setParams(Properties params)
    {
        mParams = params;
    }
}
