package jo.audio.loci.core.data;

import org.json.simple.JSONObject;

import jo.audio.loci.core.logic.vprofile.VerbProfileObject;

public class LociObject extends LociBase
{
    public static final String PROFILE = "object";
    
    public static final String ID_NAME = "name";
    public static final String ID_DECRIPTION = "description";
    public static final String ID_VERB_PROFILE = "verbProfile";
    public static final String ID_CONTAINEDBY = "containedBy";
    public static final String ID_CONTAINS = "containeds";

    public LociObject(String uri)
    {
        super(uri, PROFILE);
        setVerbProfile(VerbProfileObject.class.getSimpleName());
    }
    
    public LociObject(String uri, String profile)
    {
        super(uri, profile);
        setVerbProfile(VerbProfileObject.class.getSimpleName());
    }
    
    public LociObject(JSONObject json)
    {
        super(json);
        setVerbProfile(VerbProfileObject.class.getSimpleName());
    }
    
    // utils
    
    public void setVerbProfile(Class<? extends VerbProfile> clazz)
    {
        setVerbProfile(clazz.getSimpleName());
    }

    @Override
    public String toString()
    {
        return "["+getDataProfile()+":"+getName()+"]";
    }
    
    // getters and setters
    
    public String getName()
    {
        return getString(ID_NAME);
    }
    
    public void setName(String value)
    {
        setString(ID_NAME, value);
    }

    public String getDescription()
    {
        return getString(ID_DECRIPTION);
    }
    
    public void setDescription(String value)
    {
        setString(ID_DECRIPTION, value);
    }

    public String getVerbProfile()
    {
        return getString(ID_VERB_PROFILE);
    }
    
    public void setVerbProfile(String value)
    {
        setString(ID_VERB_PROFILE, value);
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
