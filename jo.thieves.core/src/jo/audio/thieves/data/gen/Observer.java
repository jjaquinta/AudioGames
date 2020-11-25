package jo.audio.thieves.data.gen;

public class Observer
{
    public final int RESIDENT = 0;
    public final int GUARD = 1;
    public final int PLAYER = 2;
    
    public final int DEEP_SLEEP = -20;
    public final int LIGHT_SLEEP = -10;
    public final int AWAKE = 0;
    public final int ALERT = 10;
    public final int ALARMED = 20;
    
    private String  mName;
    private int     mAlertness;
    private int     mType;
    private long    mLastMove;
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public int getAlertness()
    {
        return mAlertness;
    }
    public void setAlertness(int alertness)
    {
        mAlertness = alertness;
    }
    public int getType()
    {
        return mType;
    }
    public void setType(int type)
    {
        mType = type;
    }
    public long getLastMove()
    {
        return mLastMove;
    }
    public void setLastMove(long lastMove)
    {
        mLastMove = lastMove;
    }
}
