package jo.audio.loci.core.data;

import org.json.simple.JSONObject;

import jo.audio.loci.core.logic.DataStoreLogic;
import jo.util.beans.JSONBean;

public class LociBase extends JSONBean
{
    public static final String ID_URI = "uri";
    public static final String ID_DATA_PROFILE = "dataProfile";

    public LociBase(String uri)
    {
        // hack this for constructor, otherwise save will be triggered and fail
        mProperties.put(ID_URI, uri);
        mProperties.put(ID_DATA_PROFILE, getClass().getSimpleName());
    }

    public LociBase(JSONObject json)
    {
        fromJSON(json);
    }
    
    // utils
    
    @Override
    protected void setThing(String key, Object value)
    {
        super.setThing(key, value);
        DataStoreLogic.save(this);
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
