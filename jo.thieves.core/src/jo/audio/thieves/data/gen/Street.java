package jo.audio.thieves.data.gen;

public class Street
{
    public static final int STREET = 0;
    public static final int QUAY = 1;
    public static final int BRIDGE = 2;
    
    private String          mID;
    private int             mType;
    private String          mName;
    private Intersection    mHighIntersection;
    private Intersection    mLowIntersection;
    private int             mHouses;
    private int             mHighDir;
    private int             mLowDir;
    private long            mSeed;
    
    // utilities
    public boolean isStreet()
    {
        return mType == STREET;
    }
    
    public boolean isQuay()
    {
        return mType == QUAY;
    }
    
    public boolean isBridge()
    {
        return mType == BRIDGE;
    }
    
    public double getPosh()
    {
        return (mHighIntersection.getPosh() + mLowIntersection.getPosh())/2;
    }
    
    // getters and setters
    
    public String getID()
    {
        return mID;
    }
    public void setID(String iD)
    {
        mID = iD;
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public Intersection getHighIntersection()
    {
        return mHighIntersection;
    }
    public void setHighIntersection(Intersection highIntersection)
    {
        mHighIntersection = highIntersection;
    }
    public Intersection getLowIntersection()
    {
        return mLowIntersection;
    }
    public void setLowIntersection(Intersection lowIntersection)
    {
        mLowIntersection = lowIntersection;
    }
    public int getHouses()
    {
        return mHouses;
    }
    public void setHouses(int houses)
    {
        mHouses = houses;
    }
    public int getType()
    {
        return mType;
    }
    public void setType(int type)
    {
        mType = type;
    }

    public int getHighDir()
    {
        return mHighDir;
    }

    public void setHighDir(int highDir)
    {
        mHighDir = highDir;
    }

    public int getLowDir()
    {
        return mLowDir;
    }

    public void setLowDir(int lowDir)
    {
        mLowDir = lowDir;
    }

    public long getSeed()
    {
        return mSeed;
    }

    public void setSeed(long seed)
    {
        mSeed = seed;
    }
}
