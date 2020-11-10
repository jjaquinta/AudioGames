package jo.audio.companions.data;

import java.util.StringTokenizer;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.util.utils.obj.IntegerUtils;

public class CoordBean implements IJSONAble
{
    private int mX;
    private int mY;
    private int mZ;
    
    public CoordBean()
    {
        mX = 0;
        mY = 0;
        mZ = 0;
    }
    
    public CoordBean(String str)
    {
        if (str == null)
        {
            mZ = -1;
            return;
        }
        int o = str.indexOf(')');
        if (o >= 0)
            str = str.substring(0,  o);
        o = str.indexOf('/');
        if (o >= 0)
            str = str.substring(0,  o);
        StringTokenizer st = new StringTokenizer(str, "(,)");
        if (st.hasMoreTokens())
            mX = IntegerUtils.parseInt(st.nextToken());
        if (st.hasMoreTokens())
            mY = IntegerUtils.parseInt(st.nextToken());
        if (st.hasMoreTokens())
            mZ = IntegerUtils.parseInt(st.nextToken());
    }
    
    public CoordBean(int x, int y)
    {
        mX = x;
        mY = y;
        mZ = 0;
    }
    
    public CoordBean(int x, int y, int z)
    {
        mX = x;
        mY = y;
        mZ = z;
    }
    
    public CoordBean(CoordBean c)
    {
        this(c.mX, c.mY, c.mZ);
    }
    
    public CoordBean(CoordBean c, int x, int y)
    {
        mX = c.getX() + x;
        mY = c.getY() + y;
        mZ = c.getZ();
    }
    
    public CoordBean(CoordBean c, int x, int y, int z)
    {
        mX = c.getX() + x;
        mY = c.getY() + y;
        mZ = c.getZ() + z;
    }

    // utils
    @Override
    public String toString()
    {
        String str = "("+mX+","+mY;
        if (mZ != 0)
            str += ","+mZ;
        str += ")";
        return str;
    }
    
    @Override
    public boolean equals(Object obj)
    {        
        if (obj == null)
            return false;
        return (mX == ((CoordBean)obj).mX) && (mY == ((CoordBean)obj).mY) && (mZ == ((CoordBean)obj).mZ);
    }

    public CoordBean north()
    {
        return new CoordBean(this, 0, -1);
    }

    public CoordBean south()
    {
        return new CoordBean(this, 0, 1);
    }

    public CoordBean east()
    {
        return new CoordBean(this, 1, 0);
    }

    public CoordBean west()
    {
        return new CoordBean(this, -1, 0);
    }
    
    public int dist(CoordBean o2)
    {   // manhattan distance
        return Math.abs(o2.mX - mX) + Math.abs(o2.mY - mY);
    }
    
    @Override
    public int hashCode()
    {
        int hash = 23;
        hash = hash * 31 + mX;
        hash = hash * 31 + mY;
        hash = hash * 31 + mZ;
        return hash;
    }
    
    public long toSeed()
    {
        //return (((long)mX)<<32)|((long)mY);
        //return (((long)(mX&0xffff))<<16)|((long)(mY&0xFFFF));
        if (mZ == 0)
            return ((mX<<8)^mY);
        else
            return (((mZ<<8)^mY)<<8)^mX;
    }
    
    public void roundToNearest(int div)
    {
        if (mX >= 0)
            mX = (mX/div)*div;
        else
            mX = ((mX - (div-1))/div)*div;
        if (mY >= 0)
            mY = (mY/div)*div;
        else
            mY = ((mY - (div-1))/div)*div;
    }
    
    public CoordBean toNearest(int div)
    {
        CoordBean c = new CoordBean(this);
        c.roundToNearest(div);
        return c;
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("X", mX);
        json.put("Y", mY);
        if (mZ != 0)
            json.put("Z", mZ);
        return json;
    }

    @Override
    public void fromJSON(JSONObject o)
    {
        mX = ((Number)o.get("X")).intValue();
        mY = ((Number)o.get("Y")).intValue();
        if (o.containsKey("Z"))
            mZ = ((Number)o.get("Z")).intValue();
    }
    
    // getters and setters
    
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
    public int getZ()
    {
        return mZ;
    }
    public void setZ(int z)
    {
        mZ = z;
    }
}
