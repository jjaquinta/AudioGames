package jo.audio.thieves.data.template;

public class PSquare extends PLocation
{
    public static final String ID_INSIDE = "inside";
    public static final String ID_BEDROOM = "bedroom";

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
