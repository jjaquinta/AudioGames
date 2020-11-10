package jo.audio.util.model.data;

public class SlotBean
{
    public static final int CUSTOM = 1;
    public static final int BUILT_IN = 2;
    
    private String  mName;
    private String  mType;
    private int     mClassification;
    private String  mDictionary;
    private String  mHint;
    
    public SlotBean()
    {
    }
    
    @Override
    public String toString()
    {
        return mName+" ("+mType+")";
    }
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public String getType()
    {
        return mType;
    }
    public void setType(String type)
    {
        mType = type;
    }

    public int getClassification()
    {
        return mClassification;
    }

    public void setClassification(int classification)
    {
        mClassification = classification;
    }

    public String getDictionary()
    {
        return mDictionary;
    }

    public void setDictionary(String dictionary)
    {
        mDictionary = dictionary;
    }

    public String getHint()
    {
        return mHint;
    }

    public void setHint(String hint)
    {
        mHint = hint;
    }
}
