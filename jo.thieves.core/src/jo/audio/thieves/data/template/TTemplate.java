package jo.audio.thieves.data.template;

import jo.util.beans.JSONBean;
import jo.util.utils.obj.StringUtils;

public class TTemplate extends JSONBean implements Comparable<TTemplate>
{
    public static String ID_DESCRIPTION = "description";// "A narrow set of wooden stairs connect the floors of this building.",
    public static String ID_NAME = "name";// "Starway",
    public static String ID_ID = "ID";//"STAIRWAY"
    public static String ID_FLOORS = "floors";//"STAIRWAY"

    // utilities
    
    @Override
    public int compareTo(TTemplate o)
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

    public char[][][] getFloors()
    {
        return getCharArrayx3(ID_FLOORS);
    }
    
    public void setFloors(char[][][] value)
    {
        setCharArrayx3(ID_FLOORS, value);
    }
    
}
