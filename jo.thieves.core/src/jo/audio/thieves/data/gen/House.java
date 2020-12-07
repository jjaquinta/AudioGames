package jo.audio.thieves.data.gen;

import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.logic.ThievesConstLogic;

public class House
{
    private int                   mHouseNumber;
    private String                mStreet;
    private int                   mElevation;
    private double                mPosh;
    private long                  mSeed;
    private PTemplate             mTemplate;
    private int                   mHouseDir;
    
    // utilities
    
    public int rotate(int dir)
    {
        if (dir < ThievesConstLogic.UP)
            return (dir + mHouseDir)%8;
        else
            return dir;
    }
    
    public PLocationRef getLocation(String key)
    {
        return mTemplate.getLocations().get(key);
    }
    
    public PLocationRef getLocation(PLocationRef wrt, int dir)
    {
        dir = rotate(dir);
        return mTemplate.getLocation(wrt, dir);
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

    public PTemplate getTemplate()
    {
        return mTemplate;
    }

    public void setTemplate(PTemplate raw)
    {
        mTemplate = raw;
    }

    public int getHouseDir()
    {
        return mHouseDir;
    }

    public void setHouseDir(int houseDir)
    {
        mHouseDir = houseDir;
    }
}
