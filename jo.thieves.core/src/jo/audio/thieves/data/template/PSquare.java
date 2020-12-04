package jo.audio.thieves.data.template;

import jo.util.utils.obj.StringUtils;

public class PSquare extends PLocation implements Comparable<PSquare>
{
    public static final String ID_INSIDE = "inside";
    public static final String ID_BEDROOM = "bedroom";

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
        else
            if (s2 == null)
                return false;
            else
                return s1.getID().equals(s2.getID());
    }

    // getters and setters

    public boolean getInside()
    {
        return getBoolean(ID_INSIDE);
    }
    
    public void setInside(boolean value)
    {
        setBoolean(ID_INSIDE, value);
    }

    public boolean getBedroom()
    {
        return getBoolean(ID_BEDROOM);
    }
    
    public void setBedroom(boolean value)
    {
        setBoolean(ID_BEDROOM, value);
    }
}
