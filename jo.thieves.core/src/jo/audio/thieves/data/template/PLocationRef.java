package jo.audio.thieves.data.template;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.util.beans.PCSBean;

public class PLocationRef extends PCSBean implements IJSONAble
{ 
    public static final String ID_ID = "ID";
    public static final String ID_X = "x";
    public static final String ID_Y = "y";
    public static final String ID_Z = "z";

    private String  mID;
    private int     mX;
    private int     mY;
    private int     mZ;
    
    public PLocationRef()
    {        
    }
    
    public PLocationRef(String id, int x, int y, int z)
    {        
        mID = id;
        mX = x;
        mY = y;
        mZ = z;
    }
    
    // utilities
    
    @Override
    public String toString()
    {
        return getID();
    }
    
    public String toKey()
    {
        return getX()+","+getY()+","+getZ();
    }
    
    // I/O
    
    @Override
    public void fromJSON(JSONObject o)
    {
        setID(JSONUtils.getString(o, ID_ID));
        setX(JSONUtils.getInt(o, ID_X));
        setY(JSONUtils.getInt(o, ID_Y));
        setZ(JSONUtils.getInt(o, ID_Z));
    }
    
    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put(ID_ID, getID());
        json.put(ID_X, getX());
        json.put(ID_Y, getY());
        json.put(ID_Z, getZ());
        return json;
    }
    
    // utilities

    public boolean isApature()
    {
        return PTemplate.isApature(getX(), getY(), getZ());
    }

    public boolean isSquare()
    {
        return PTemplate.isSquare(getX(), getY(), getZ());
    }

    public int getType()
    {
        return PTemplate.getType(getX(), getY(), getZ());
    }


    // getters and setters

    public String getID()
    {
        return mID;
    }
    
    public void setID(String value)
    {
        queuePropertyChange(ID_ID, mID, value);
        mID = value;
        firePropertyChange();
    }

    public int getX()
    {
        return mX;
    }
    
    public void setX(int value)
    {
        queuePropertyChange(ID_X, mX, value);
        mX = value;
        firePropertyChange();
    }

    public int getY()
    {
        return mY;
    }
    
    public void setY(int value)
    {
        queuePropertyChange(ID_Y, mY, value);
        mY = value;
        firePropertyChange();
    }

    public int getZ()
    {
        return mZ;
    }
    
    public void setZ(int value)
    {
        queuePropertyChange(ID_Z, mZ, value);
        mZ = value;
        firePropertyChange();
    }
}
