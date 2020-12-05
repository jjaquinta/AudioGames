package jo.audio.thieves.data.template;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.FromJSONLogic;
import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;
import org.json.simple.ToJSONLogic;

import jo.util.beans.PCSBean;
import jo.util.utils.obj.StringUtils;

public class PTemplate extends PCSBean implements IJSONAble,Comparable<PTemplate>
{
    public static final String        ID_DESCRIPTION = "description";
    public static final String        ID_NAME        = "name";
    public static final String        ID_ID          = "ID";
    public static final String        ID_CATEGORY    = "category";
    public static final String        ID_LOCATIONS   = "locations";

    private String                    mID;
    private String                    mName;
    private String                    mDescription;
    private String                    mCategory;
    private Map<String, PLocationRef> mLocations     = new HashMap<>();

    // I/O

    @Override
    public void fromJSON(JSONObject o)
    {
        setDescription(JSONUtils.getString(o, ID_DESCRIPTION));
        setName(JSONUtils.getString(o, ID_NAME));
        setID(JSONUtils.getString(o, ID_ID));
        setCategory(JSONUtils.getString(o, ID_CATEGORY));
        FromJSONLogic.fromJSON(JSONUtils.getObject(o, ID_LOCATIONS), mLocations, PLocationRef.class);
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject o = new JSONObject();
        o.put(ID_DESCRIPTION, getDescription());
        o.put(ID_NAME, getName());
        o.put(ID_ID, getID());
        o.put(ID_CATEGORY, getCategory());
        o.put(ID_LOCATIONS, ToJSONLogic.toJSONMap(mLocations));
        return o;
    }

    // utilities

    public static final int NOTHING       = 0;
    public static final int SQUARE        = 1;
    public static final int APATURE_HORZ  = 2;
    public static final int APATURE_VERT  = 3;
    public static final int APATURE_TWEEN = 4;

    public static int getType(PLocationRef loc)
    {
        return getType(loc.getX(), loc.getY(), loc.getZ());
    }

    public static int getType(int x, int y, int z)
    {
        if ((z % 2) != 0)
        { // tween
            if ((x % 2 != 0) && (y % 2 != 0))
                return APATURE_TWEEN;
            else
                return NOTHING;
        }
        if ((x % 2 != 0) && (y % 2 == 0))
            return APATURE_HORZ;
        else if ((x % 2 == 0) && (y % 2 != 0))
            return APATURE_VERT;
        else if ((x % 2 != 0) && (y % 2 != 0))
            return SQUARE;
        else // if ((x%2 == 0) && (y%2 == 0))
            return NOTHING;
    }

    public static boolean isApature(int x, int y, int z)
    {
        return getType(x, y, z) >= APATURE_HORZ;
    }

    public static boolean isSquare(int x, int y, int z)
    {
        return getType(x, y, z) == SQUARE;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    @Override
    public int compareTo(PTemplate o)
    {
        return StringUtils.compareTo(getName(), o.getName());
    }

    public PLocationRef getLocation(int x, int y, int z)
    {
        String k = x + "," + y + "," + z;
        return mLocations.get(k);
    }

    public PLocationRef[] getNeighbors(int x, int y, int z)
    {
        int type = PTemplate.getType(x, y, z);
        PLocationRef[] ret = new PLocationRef[2];
        if (type == PTemplate.APATURE_VERT)
        {
            ret[0] = getLocation(x - 1, y, z);
            ret[1] = getLocation(x + 1, y, z);
        }
        else if (type == PTemplate.APATURE_HORZ)
        {
            ret[0] = getLocation(x, y - 1, z);
            ret[1] = getLocation(x, y + 1, z);
        }
        else if (type == PTemplate.APATURE_TWEEN)
        {
            ret[0] = getLocation(x, y, z - 1);
            ret[1] = getLocation(x, y, z + 1);
        }
        else
            throw new IllegalArgumentException();
        return ret;
    }

    public PLocationRef[] getNeighbors(PLocationRef loc)
    {
        return getNeighbors(loc.getX(), loc.getY(), loc.getZ());
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

    public String getName()
    {
        return mName;
    }

    public void setName(String value)
    {
        queuePropertyChange(ID_NAME, mName, value);
        mName= value;
        firePropertyChange();
    }

    public String getDescription()
    {
        return mDescription;
    }

    public void setDescription(String value)
    {
        queuePropertyChange(ID_DESCRIPTION, mDescription, value);
        mDescription = value;
        firePropertyChange();
    }

    public String getCategory()
    {
        return mCategory;
    }

    public void setCategory(String value)
    {
        queuePropertyChange(ID_CATEGORY, mCategory, value);
        mCategory = value;
        firePropertyChange();
    }

    public Map<String, PLocationRef> getLocations()
    {
        return mLocations;
    }

    public void setLocations(Map<String, PLocationRef> value)
    {
        queuePropertyChange(ID_LOCATIONS, mLocations, value);
        mLocations = value;
        firePropertyChange();
    }

}
