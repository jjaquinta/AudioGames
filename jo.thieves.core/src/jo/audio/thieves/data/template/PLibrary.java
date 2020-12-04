package jo.audio.thieves.data.template;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.FromJSONLogic;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;
import org.json.simple.ToJSONLogic;

import jo.util.beans.JSONBean;

public class PLibrary extends JSONBean
{
    public static final String ID_APATURES = "apatures";
    public static final String ID_SQUARES = "squares";
    public static final String ID_TEMPLATES = "templates";

    private Map<String, PApature> mApatures = new HashMap<>();
    private Map<String, PSquare> mSquares = new HashMap<>();
    private Map<String, PTemplate> mTemplates = new HashMap<>();
    
    // I/O

    @Override
    public void fromJSON(JSONObject o)
    {
        FromJSONLogic.fromJSON(JSONUtils.getObject(o, ID_APATURES), mApatures, PApature.class);
        FromJSONLogic.fromJSON(JSONUtils.getObject(o, ID_SQUARES), mSquares, PSquare.class);
        FromJSONLogic.fromJSON(JSONUtils.getObject(o, ID_TEMPLATES), mTemplates, PTemplate.class);
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject o = new JSONObject();
        o.put(ID_APATURES, ToJSONLogic.toJSONMap(mApatures));
        o.put(ID_SQUARES, ToJSONLogic.toJSONMap(mSquares));
        o.put(ID_TEMPLATES, ToJSONLogic.toJSONMap(mTemplates));
        return o;
    }

    // utilities
    
    // getters and setters
    
    public Map<String,PApature> getApatures()
    {
        return mApatures;
    }
    
    public void setSquares(Map<String,PSquare> value)
    {
        queuePropertyChange(ID_SQUARES, mSquares, value);
        mSquares = value;
        firePropertyChange();
    }
    
    public Map<String,PTemplate> getTemplates()
    {
        return mTemplates;
    }
    
    public void setTemplates(Map<String,PTemplate> value)
    {
        queuePropertyChange(ID_TEMPLATES, mTemplates, value);
        mTemplates = value;
        firePropertyChange();
    }
    
    public Map<String,PSquare> getSquares()
    {
        return mSquares;

    }
    
    public void setApatures(Map<String,PApature> value)
    {
        queuePropertyChange(ID_APATURES, mApatures, value);
        mApatures = value;
        firePropertyChange();
    }
}
