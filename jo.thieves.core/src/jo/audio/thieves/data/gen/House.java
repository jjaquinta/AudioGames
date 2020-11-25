package jo.audio.thieves.data.gen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

public class House
{
    private int                   mHouseNumber;
    private String                mStreet;
    private Map<String, Location> mLocations = new HashMap<>();
    private Map<String, Apature>  mApatures  = new HashMap<>();
    private String                mEntry;
    private int                   mElevation;
    private double                mPosh;
    private long                  mSeed;
    private JSONObject            mRaw;
    
    // utilities
    public Apature getApatureBetween(Location from, Location to)
    {
        Set<String> fromAps = new HashSet<>(from.getApatures());
        Set<String> toAps = new HashSet<>(to.getApatures());
        fromAps.retainAll(toAps);
        if (fromAps.size() == 0)
            return null;
        return mApatures.get(fromAps.iterator().next());
    }
    
    // getters and setters
    
    public int getHouseNumber()
    {
        return mHouseNumber;
    }

    public void setHouseNumber(int houseNumber)
    {
        mHouseNumber = houseNumber;
    }

    public String getStreet()
    {
        return mStreet;
    }

    public void setStreet(String street)
    {
        mStreet = street;
    }

    public Map<String, Location> getLocations()
    {
        return mLocations;
    }

    public void setLocations(Map<String, Location> locations)
    {
        mLocations = locations;
    }

    public Map<String, Apature> getApatures()
    {
        return mApatures;
    }

    public void setApatures(Map<String, Apature> apatures)
    {
        mApatures = apatures;
    }

    public String getEntry()
    {
        return mEntry;
    }

    public void setEntry(String entry)
    {
        mEntry = entry;
    }

    public int getElevation()
    {
        return mElevation;
    }

    public void setElevation(int elevation)
    {
        mElevation = elevation;
    }

    public double getPosh()
    {
        return mPosh;
    }

    public void setPosh(double posh)
    {
        mPosh = posh;
    }

    public long getSeed()
    {
        return mSeed;
    }

    public void setSeed(long seed)
    {
        mSeed = seed;
    }

    public JSONObject getRaw()
    {
        return mRaw;
    }

    public void setRaw(JSONObject raw)
    {
        mRaw = raw;
    }
}
