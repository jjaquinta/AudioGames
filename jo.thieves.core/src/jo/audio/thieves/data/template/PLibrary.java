package jo.audio.thieves.data.template;

import java.util.Map;

import jo.util.beans.JSONBean;

public class PLibrary extends JSONBean
{
    public static final String ID_APATURES = "apatures";
    public static final String ID_SQUARES = "squares";
    public static final String ID_TEMPLATES = "templates";
    
    // utilities
    
    // getters and setters
    
    public Map<String,PApature> getApatures()
    {
        return getMap(ID_APATURES, PApature.class);
    }
    
    public void setSquares(Map<String,PSquare> value)
    {
        setMap(ID_SQUARES, value);
    }
    
    public Map<String,PTemplate> getTemplates()
    {
        return getMap(ID_TEMPLATES, PTemplate.class);
    }
    
    public void setTemplates(Map<String,PTemplate> value)
    {
        setMap(ID_TEMPLATES, value);
    }
    
    public Map<String,PSquare> getSquares()
    {
        return getMap(ID_SQUARES, PSquare.class);

    }
    
    public void setApatures(Map<String,PApature> value)
    {
        setMap(ID_APATURES, value);
    }
}
