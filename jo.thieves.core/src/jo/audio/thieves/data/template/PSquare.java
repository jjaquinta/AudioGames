package jo.audio.thieves.data.template;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.util.utils.obj.StringUtils;

public class PSquare extends PLocation implements Comparable<PSquare>
{
    public static final String ID_INSIDE  = "inside";
    public static final String ID_BEDROOM = "bedroom";

    private boolean            mInside;
    private boolean            mBedroom;

    // I/O

    @Override
    public void fromJSON(JSONObject o)
    {
        super.fromJSON(o);
        setInside(JSONUtils.getBoolean(o, ID_INSIDE));
        setBedroom(JSONUtils.getBoolean(o, ID_BEDROOM));
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject o = super.toJSON();
        o.put(ID_INSIDE, getInside());
        o.put(ID_BEDROOM, getBedroom());
        return o;
    }

    // utilities

    @Override
    public int compareTo(PSquare o)
    {
        return StringUtils.compareTo(getName(), o.getName());
    }

    public static boolean equals(PSquare s1, PSquare s2)
    {
        if (s1 == null)
            if (s2 == null)
                return true;
            else
                return false;
        else if (s2 == null)
            return false;
        else
            return s1.getID().equals(s2.getID());
    }

    // getters and setters

    public boolean getInside()
    {
        return mInside;
    }

    public void setInside(boolean value)
    {
        queuePropertyChange(ID_INSIDE, mInside, value);
        mInside = value;
        firePropertyChange();
    }

    public boolean getBedroom()
    {
        return mBedroom;
    }

    public void setBedroom(boolean value)
    {
        queuePropertyChange(ID_BEDROOM, mBedroom, value);
        mBedroom = value;
        firePropertyChange();
    }
}
