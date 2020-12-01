package jo.audio.thieves.data.template;

import java.util.Map;

import jo.util.beans.JSONBean;
import jo.util.utils.obj.StringUtils;

public class PTemplate extends JSONBean implements Comparable<PTemplate>
{
    public static String ID_DESCRIPTION = "description";// "A narrow set of wooden stairs connect the tiles of this building.",
    public static String ID_NAME = "name";// "Starway",
    public static String ID_ID = "ID";//"STAIRWAY"
    public static String ID_SQUARES = "squares";
    public static String ID_APATURES = "apatures";

    // utilities
    
    @Override
    public int compareTo(PTemplate o)
    {
        return StringUtils.compareTo(getName(), o.getName());
    }
    
    // getters and setters

    public String getID()
    {
        return getString(ID_ID);
    }
    
    public void setID(String value)
    {
        setString(ID_ID, value);
    }

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
        return getString(ID_DESCRIPTION);
    }
    
    public void setDescription(String value)
    {
        setString(ID_DESCRIPTION, value);
    }

    public Map<String,PSquare> getSquares()
    {
        return getMap(ID_SQUARES, PSquare.class);
    }
    
    public void setSquares(Map<String,PSquare> value)
    {
        setMap(ID_SQUARES, value);
    }

    public Map<String,PApature> getApatures()
    {
        return getMap(ID_APATURES, PApature.class);
    }
    
    public void setApatures(Map<String,PApature> value)
    {
        setMap(ID_APATURES, value);
    }
    
}
