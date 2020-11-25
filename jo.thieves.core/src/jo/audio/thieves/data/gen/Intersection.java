package jo.audio.thieves.data.gen;

import java.util.ArrayList;
import java.util.List;

import jo.audio.thieves.logic.ThievesConstLogic;

public class Intersection
{
    private String       mID;
    private String       mName;
    private String       mDescription;
    private List<Street> mStreets = new ArrayList<>();
    private int          mX;
    private int          mY;
    private int          mElevation;
    private boolean      mRiverside;
    private double       mPosh;
    private Street[]     mCardinalStreets = new Street[8];
    private long         mSeed;
    
    public Intersection()
    {        
    }
    
    // utilities
    
    public int bearing(Street s)
    {
        return ThievesConstLogic.bearing(this, s);
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
    public List<Street> getStreets()
    {
        return mStreets;
    }
    public void setStreets(List<Street> streets)
    {
        mStreets = streets;
    }
    public int getX()
    {
        return mX;
    }
    public void setX(int x)
    {
        mX = x;
    }
    public int getY()
    {
        return mY;
    }
    public void setY(int y)
    {
        mY = y;
    }
    public int getElevation()
    {
        return mElevation;
    }
    public void setElevation(int elevation)
    {
        mElevation = elevation;
    }
    public boolean isRiverside()
    {
        return mRiverside;
    }
    public void setRiverside(boolean riverside)
    {
        mRiverside = riverside;
    }
    public double getPosh()
    {
        return mPosh;
    }
    public void setPosh(double posh)
    {
        mPosh = posh;
    }
    public Street[] getCardinalStreets()
    {
        return mCardinalStreets;
    }
    public void setCardinalStreets(Street[] cardinalStreets)
    {
        mCardinalStreets = cardinalStreets;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public void setDescription(String description)
    {
        mDescription = description;
    }

    public long getSeed()
    {
        return mSeed;
    }

    public void setSeed(long seed)
    {
        mSeed = seed;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }
}
