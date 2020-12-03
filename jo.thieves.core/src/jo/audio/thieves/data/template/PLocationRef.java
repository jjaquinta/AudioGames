package jo.audio.thieves.data.template;

import jo.util.beans.JSONBean;

public class PLocationRef extends JSONBean
{ 
    public static final String ID_ID = "ID";
    public static final String ID_X = "x";
    public static final String ID_Y = "y";
    public static final String ID_Z = "z";

    // utilities
    
    @Override
    public String toString()
    {
        return getID();
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

    public int getX()
    {
        return getInt(ID_X);
    }
    
    public void setX(int value)
    {
        setInt(ID_X, value);
    }

    public int getY()
    {
        return getInt(ID_Y);
    }
    
    public void setY(int value)
    {
        setInt(ID_Y, value);
    }

    public int getZ()
    {
        return getInt(ID_Z);
    }
    
    public void setZ(int value)
    {
        setInt(ID_Z, value);
    }
}
