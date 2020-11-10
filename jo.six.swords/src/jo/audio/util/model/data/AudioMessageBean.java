package jo.audio.util.model.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.json.simple.IJSONAble;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.util.utils.obj.IntegerUtils;

public class AudioMessageBean implements IJSONAble
{
    public static final String AND = "$$AND";
    public static final String OR = "$$OR";
    public static final String GROUP = "$$GROUP";
    public static final String LIST = "$$LIST";
    public static final String RAW = "$$RAW";
    
    private int     mPriority = 0;
    private String  mIdent;
    private Object[] mArgs = new Object[0];
    
    // constructors
    public AudioMessageBean()
    {
    }

    public AudioMessageBean(String key)
    {
        mIdent = key;
    }

    public AudioMessageBean(String key, Object... args)
    {
        mIdent = key;
        mArgs = args;
    }        

    public AudioMessageBean(int priority, String key)
    {
        mPriority = priority;
        mIdent = key;
    }

    public AudioMessageBean(int priority, String key, Object... args)
    {
        mPriority = priority;
        mIdent = key;
        mArgs = args;
    }        

    public AudioMessageBean(JSONObject json)
    {
        fromJSON(json);
    }        
    
    // utilities
    
    public static AudioMessageBean and(Object... args)
    {
        return new AudioMessageBean(AudioMessageBean.AND, args);
    }
    
    public static AudioMessageBean and(Collection<?> args)
    {
        return and(args.toArray());
    }
    
    public static AudioMessageBean or(Object... args)
    {
        return new AudioMessageBean(AudioMessageBean.OR, args);
    }
    
    public static AudioMessageBean or(Collection<?> args)
    {
        return or(args.toArray());
    }
    
    public static AudioMessageBean group(Object... args)
    {
        return new AudioMessageBean(AudioMessageBean.GROUP, args);
    }
    
    public static AudioMessageBean GROUP(Collection<?> args)
    {
        return group(args.toArray());
    }
    
    public static AudioMessageBean list(Object... args)
    {
        return new AudioMessageBean(AudioMessageBean.LIST, args);
    }
    
    public static AudioMessageBean raw(Object... args)
    {
        return new AudioMessageBean(AudioMessageBean.RAW, args);
    }
    
    public static AudioMessageBean LIST(Collection<?> args)
    {
        return list(args.toArray());
    }
    
    public void addToGroup(AudioMessageBean msg)
    {
        Object[] newArgs = new Object[mArgs.length + 1];
        System.arraycopy(mArgs, 0, newArgs, 0, mArgs.length);
        newArgs[newArgs.length - 1] = msg;
        mArgs = newArgs;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject toJSON()
    {
        JSONObject msg = new JSONObject();
        msg.put("priority", mPriority);
        msg.put("ident", mIdent);
        JSONArray args = new JSONArray();
        msg.put("args", args);
        for (int i = 0; i < mArgs.length; i++)
            args.add(mArgs[i]);
        return msg;
    }
    @Override
    public void fromJSON(JSONObject o)
    {
        mPriority = JSONUtils.getInt(o, "priority");
        mIdent = JSONUtils.getString(o, "ident");
        JSONArray args = JSONUtils.getArray(o, "args");
        if (args != null)
        {
            mArgs = args.toArray();
            for (int i = 0; i < mArgs.length; i++)
                if (mArgs[i] instanceof JSONObject)
                    mArgs[i] = new AudioMessageBean((JSONObject)mArgs[i]);
                else if (mArgs[i] instanceof String)
                {
                    String arg = (String)mArgs[i];
                    if (arg.startsWith("{") && arg.endsWith("}") && (arg.indexOf("\"ident\":") > 0))
                        mArgs[i] = new AudioMessageBean((JSONObject)JSONUtils.readJSONString(arg));
                }
        }
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof AudioMessageBean))
            return false;
        AudioMessageBean o2 = (AudioMessageBean)obj;
        if (!mIdent.equals(o2.mIdent))
            return false;
        if (mArgs.length != o2.mArgs.length)
            return false;
        for (int i = 0; i < mArgs.length; i++)
            if (!mArgs[i].equals(o2.mArgs[i]))
                return false;
        return true;
    }
    
    @Override
    public String toString()
    {
        StringBuffer txt = new StringBuffer();
        txt.append(mIdent);
        if (mPriority != 0)
            txt.append("!"+mPriority);
        if ((mArgs != null) && (mArgs.length > 0))
        {
            txt.append("(");
            for (int i = 0; i < mArgs.length; i++)
            {
                if (i > 0)
                    txt.append(",");
                if (mArgs[i] == null)
                    txt.append("NULL!!!");
                else
                    txt.append(mArgs[i].toString());
            }
            txt.append(")");
        }
        return txt.toString();
    }

    public static AudioMessageBean fromString(String str)
    {
        AudioMessageBean ret = new AudioMessageBean();
        String key = str;
        List<String> args = new ArrayList<>();
        int o = key.indexOf('(');
        if ((o > 0) && key.endsWith(")"))
        {
            for (StringTokenizer st = new StringTokenizer(key.substring(o + 1,  key.length() - 1), ","); st.hasMoreTokens(); )
                args.add(st.nextToken());
            key = key.substring(0, o);
        }
        o = key.indexOf('!');
        if (o > 0)
        {
            ret.setPriority(IntegerUtils.parseInt(key.substring(o + 1)));
            key = key.substring(0, o);
        }
        ret.setIdent(key);
        ret.setArgs(args.toArray());
        return ret;
    }
    
    // getters and setters
    
    public String getIdent()
    {
        return mIdent;
    }
    public void setIdent(String key)
    {
        mIdent = key;
        if (mIdent.startsWith("{{") && (mIdent.indexOf("}}") == mIdent.length() - 2))
            mIdent = mIdent.substring(2, mIdent.length() - 2);
    }
    public Object[] getArgs()
    {
        return mArgs;
    }
    public void setArgs(Object[] args)
    {
        mArgs = args;
    }

    public int getPriority()
    {
        return mPriority;
    }

    public void setPriority(int priority)
    {
        mPriority = priority;
    }
}
