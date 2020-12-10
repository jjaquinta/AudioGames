package jo.audio.thieves.data.gen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class City
{
    private String                      mID;
    private String                      mName;
    private String                      mRiverName;
    private Map<String, Intersection>   mIntersections = new HashMap<>();
    private Map<String, Street>         mStreets = new HashMap<>();
    private long                        mSeed;
    private Random                      mRND;
    private String                      mNorthGuildStreet;
    private String                      mSouthGuildStreet;
    
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
    public Random getRND()
    {
        return mRND;
    }
    public void setRND(Random rND)
    {
        mRND = rND;
    }
    public String getNorthGuildStreet()
    {
        return mNorthGuildStreet;
    }
    public void setNorthGuildStreet(String northGuildStreet)
    {
        mNorthGuildStreet = northGuildStreet;
    }
    public String getSouthGuildStreet()
    {
        return mSouthGuildStreet;
    }
    public void setSouthGuildStreet(String southGuildStreet)
    {
        mSouthGuildStreet = southGuildStreet;
    }
}
