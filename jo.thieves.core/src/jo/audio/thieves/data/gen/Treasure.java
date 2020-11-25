package jo.audio.thieves.data.gen;

public class Treasure extends Actionable
{
    private String  mName;
    private String  mDescription;
    private int     mValue;
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public String getDescription()
    {
        return mDescription;
    }
    public void setDescription(String description)
    {
        mDescription = description;
    }
    public int getValue()
    {
        return mValue;
    }
    public void setValue(int value)
    {
        mValue = value;
    }
}
