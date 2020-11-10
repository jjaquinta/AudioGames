package jo.audio.companions.data;

import jo.audio.companions.logic.CompConstLogic;

public class DomainBean
{
    private CoordBean mOrds;
    private int mPredominantRace;
    private int mGovernmentStructure;
    private RegionBean[][] mRegions = new RegionBean[CompConstLogic.REGIONS_PER_DOMAIN][CompConstLogic.REGIONS_PER_DOMAIN];
    private long    mLastUsed;
    
    // utilities
    @Override
    public int hashCode()
    {
        return mOrds.hashCode();
    }

    public boolean contains(CoordBean tst)
    {
        int dx = tst.getX() - mOrds.getX();
        if ((dx < 0) || (dx >= CompConstLogic.SQUARES_PER_DOMAIN))
            return false;
        int dy = tst.getY() - mOrds.getY();
        if ((dy < 0) || (dy >= CompConstLogic.SQUARES_PER_DOMAIN))
            return false;
        return true;
    }
    
    public RegionBean getRegion(int dx, int dy)
    {
        return mRegions[dx][dy];
    }
    
    public void setRegion(int dx, int dy, RegionBean region)
    {
        mRegions[dx][dy] = region;
    }
    
    public RegionBean getRegion(CoordBean ords)
    {
        int dx = (ords.getX() - mOrds.getX())/CompConstLogic.SQUARES_PER_REGION;
        int dy = (ords.getY() - mOrds.getY())/CompConstLogic.SQUARES_PER_REGION;
        return getRegion(dx, dy);
    }
    
    public void setRegion(CoordBean ords, RegionBean region)
    {
        int dx = (ords.getX() - mOrds.getX())/CompConstLogic.SQUARES_PER_REGION;
        int dy = (ords.getY() - mOrds.getY())/CompConstLogic.SQUARES_PER_REGION;
        setRegion(dx, dy, region);
    }
    
    // getters and setters

    public int getPredominantRace()
    {
        return mPredominantRace;
    }
    public void setPredominantRace(int predominantRace)
    {
        mPredominantRace = predominantRace;
    }
    public CoordBean getOrds()
    {
        return mOrds;
    }
    public void setOrds(CoordBean ords)
    {
        mOrds = ords;
    }

    public RegionBean[][] getRegions()
    {
        return mRegions;
    }

    public void setRegions(RegionBean[][] regions)
    {
        mRegions = regions;
    }

    public int getGovernmentStructure()
    {
        return mGovernmentStructure;
    }

    public void setGovernmentStructure(int governmentStructure)
    {
        mGovernmentStructure = governmentStructure;
    }

    public long getLastUsed()
    {
        return mLastUsed;
    }

    public void setLastUsed(long lastUsed)
    {
        mLastUsed = lastUsed;
    }
    
}
