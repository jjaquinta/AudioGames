package jo.audio.companions.data;

import jo.audio.companions.logic.CompConstLogic;

public class GeoBean extends LocationBean
{
    private int mLongitude;
    private int mLattitude;
    
    public GeoBean()
    {
        super();
        setX(CompConstLogic.INITIAL_DIM_LOCATION_X[getZ()]);
        setY(CompConstLogic.INITIAL_DIM_LOCATION_Y[getZ()]);
        align();
    }
    
    public GeoBean(String str)
    {
        super(str);
        align();
    }
    
    public GeoBean(int x, int y, int z)
    {
        super(x, y, z);
        align();
    }
    
    public GeoBean(int x, int y, int z, String id)
    {
        super(x, y, z, id);
        align();
    }
    
    public GeoBean(CoordBean c)
    {
        super(c);
        align();
    }
    
    public GeoBean(LocationBean l)
    {
        super(l);
        align();
    }
    
    public GeoBean(GeoBean c)
    {
        super(c.getX(), c.getY(), c.getZ(), c.getRoomID());
        align();
    }

    // utils

    private void align()
    {
        mLongitude = getX() - CompConstLogic.INITIAL_DIM_LOCATION_X[getZ()];
        mLattitude = getY() - CompConstLogic.INITIAL_DIM_LOCATION_Y[getZ()];
    }
    
    // getters and setters

    public int getLongitude()
    {
        return mLongitude;
    }

    public void setLongitude(int longitude)
    {
        mLongitude = longitude;
        setX(CompConstLogic.INITIAL_DIM_LOCATION_X[getZ()] + mLongitude);
    }

    public int getLattitude()
    {
        return mLattitude;
    }

    public void setLattitude(int lattitude)
    {
        mLattitude = lattitude;
        setY(CompConstLogic.INITIAL_DIM_LOCATION_Y[getZ()] + mLattitude);
    }
}
