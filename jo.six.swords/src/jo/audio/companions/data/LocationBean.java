package jo.audio.companions.data;

public class LocationBean extends CoordBean
{
    private String mRoomID = "";
    
    public LocationBean()
    {
        super();
    }
    
    public LocationBean(String str)
    {
        super(str);
        if (str == null)
            return;
        int o = str.indexOf('/');
        if (o >= 0)
            mRoomID = str.substring(o + 1);
    }
    
    public LocationBean(int x, int y, int z)
    {
        super(x, y, z);
    }
    
    public LocationBean(int x, int y, int z, String id)
    {
        super(x, y, z);
        mRoomID = id;
    }
    
    public LocationBean(CoordBean c)
    {
        this(c.getX(), c.getY(), c.getZ());
    }
    
    public LocationBean(LocationBean c)
    {
        this(c.getX(), c.getY(), c.getZ(), c.getRoomID());
    }
    
    public LocationBean(LocationBean c, int x, int y)
    {
        this(c, x, y, "");
    }
    
    public LocationBean(LocationBean c, String id)
    {
        this(c, 0, 0, id);
    }
    
    public LocationBean(LocationBean c, int x, int y, String id)
    {
        setX(c.getX() + x);
        setY(c.getY() + y);
        setZ(c.getZ());
        setRoomID(id);
    }

    // utils
    @Override
    public String toString()
    {
        String str = super.toString();
        if (mRoomID != null)
            str += "/"+mRoomID;
        return str;
    }

    public LocationBean move(String id)
    {
        return new LocationBean(this, 0, 0, id);
    }
    
    @Override
    public int hashCode()
    {
        int hash = 23;
        hash = hash * 31 + getX();
        hash = hash * 31 + getY();
        hash = hash * 31 + mRoomID.hashCode();
        return hash;
    }
    
    public long toSeed()
    {
        //return (((long)mX)<<32)|((long)mY);
        //return (((long)(mX&0xffff))<<16)|((long)(mY&0xFFFF));
        return ((getX()<<8)^(getY()<<4)^mRoomID.hashCode());
    }
    
    // getters and setters

    public String getRoomID()
    {
        return mRoomID;
    }

    public void setRoomID(String roomID)
    {
        mRoomID = roomID;
    }
}
