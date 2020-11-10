package jo.audio.companions.data;

import org.json.simple.IJSONAble;

public abstract class RegionBean implements IJSONAble
{
    private long    mLastUsed;
    
    // utilities

    public SquareBean getSquare(int dx, int dy)
    {
        return getSquares()[dx][dy];    
    }
    
    public SquareBean getSquare(CoordBean ords)
    {
        int dx = (ords.getX() - getOrds().getX());
        int dy = (ords.getY() - getOrds().getY());
        return getSquare(dx, dy);
    }
    
    public abstract String getTitle();
    public abstract DemenseBean getLiege();
    public abstract CoordBean getOrds();
    public abstract int getPredominantRace();
    public abstract int getGovernmentalStructure();
    public abstract SquareBean[][] getSquares();

    public long getLastUsed()
    {
        return mLastUsed;
    }

    public void setLastUsed(long lastUsed)
    {
        mLastUsed = lastUsed;
    }
}
