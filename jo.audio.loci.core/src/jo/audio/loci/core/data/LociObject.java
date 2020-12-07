package jo.audio.loci.core.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import jo.audio.loci.core.logic.DataStoreLogic;

public class LociObject extends LociBase
{
    public static String NAME_DELIM = ",";
    
    public static final String ID_NAME = "name";
    public static final String ID_DECRIPTION = "description";
    public static final String ID_CONTAINEDBY = "containedBy";
    public static final String ID_CONTAINS = "containeds";

    protected Pattern mNamePattern = null;
    
    public LociObject(String uri)
    {
        super(uri);
    }
    
    public LociObject(JSONObject json)
    {
        super(json);
    }
    
    // utils
    
    public String getPrimaryName()
    {
        String name = getName();
        if (name == null)
            name = "";
        int o = name.indexOf(NAME_DELIM);
        if (o > 0)
            name = name.substring(0, o);
        return name;
    }
    
    public Pattern getNamePattern()
    {
        if (mNamePattern == null)
            mNamePattern = Verb.listToPattern(getName(), NAME_DELIM);
        return mNamePattern;
    }
    
    @Override
    public String toString()
    {
        return "["+getDataProfile()+":"+getPrimaryName()+"]";
    }
    
    @Override
    public void fromJSON(JSONObject o)
    {
        super.fromJSON(o);
        mNamePattern = null;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getContainsStuff(Class<T> clazz)
    {
        List<T> items = new ArrayList<>();
        String[] contents = getContains();
        if (contents == null)
            return items;
        for (String itemURI : contents)
        {
            LociBase item = DataStoreLogic.load(itemURI);
            if (clazz.isInstance(item))
                items.add((T)item);
        }
        return items;
    }

    public List<LociObject> getContainsObjects()
    {
        return getContainsStuff(LociObject.class);
    }

    public List<String> getContainsNames()
    {
        List<String> itemNames = new ArrayList<>();
        for (LociObject item : getContainsObjects())
            itemNames.add(item.getPrimaryName());
        return itemNames;
    }

    // getters and setters
    
    public String getName()
    {
        return getString(ID_NAME);
    }
    
    public void setName(String value)
    {
        setString(ID_NAME, value);
        mNamePattern = null;
    }

    public String getDescription()
    {
        return getString(ID_DECRIPTION);
    }
    
    public void setDescription(String value)
    {
        setString(ID_DECRIPTION, value);
    }

    public String getContainedBy()
    {
        return getString(ID_CONTAINEDBY);
    }
    
    public void setContainedBy(String value)
    {
        setString(ID_CONTAINEDBY, value);
    }

    public String[] getContains()
    {
        return getStringArray(ID_CONTAINS);
    }
    
    public void setContains(String[] value)
    {
        setStringArray(ID_CONTAINS, value);
    }
}
