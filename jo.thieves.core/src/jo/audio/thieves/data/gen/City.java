package jo.audio.thieves.data.gen;

import java.util.HashMap;
import java.util.Map;

public class City
{
    private String                      mID;
    private String                      mName;
    private String                      mRiverName;
    private Map<String, Intersection>   mIntersections = new HashMap<>();
    private Map<String, Street>         mStreets = new HashMap<>();
    private long                        mSeed;
    
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
    public Map<String, Intersection> getIntersections()
    {
        return mIntersections;
    }
    public void setIntersections(Map<String, Intersection> intersections)
    {
        mIntersections = intersections;
    }
    public Map<String, Street> getStreets()
    {
        return mStreets;
    }
    public void setStreets(Map<String, Street> streets)
    {
        mStreets = streets;
    }
    public String getRiverName()
    {
        return mRiverName;
    }
    public void setRiverName(String riverName)
    {
        mRiverName = riverName;
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
