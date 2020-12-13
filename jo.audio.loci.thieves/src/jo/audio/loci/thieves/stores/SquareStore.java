package jo.audio.loci.thieves.stores;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.IDataStore;
import jo.audio.loci.core.logic.stores.DiskCache;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.thieves.data.LociSquare;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.logic.LocationLogic;
import jo.audio.thieves.logic.gen.HouseLogic;
import jo.util.beans.WeakCache;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;

public class SquareStore implements IDataStore
{
    // square://<streetID<:<house#>/<x,y,z>
    public static final String PREFIX = "square://";
    
    private DiskCache                   mDisk;
    private static SquareStore          mInstance;
    
    public SquareStore()
    {
        mDisk = new DiskCache(DiskStore.PREFIX, "squares");
        mInstance = this;
    }
    
    public static SquareStore getInstance()
    {
        return mInstance;
    }
    
    @Override
    public boolean isStoreFor(String uri)
    {
        return uri.startsWith(PREFIX);
    }
    
    public static String makeURI(House house, String key)
    {
        return SquareStore.PREFIX+house.getStreet()+":"+house.getHouseNumber()+"/"+key;
    }
    
    public static String makeURI(Street street, int houseNum, PLocationRef location)
    {
        return SquareStore.PREFIX+street.getID()+":"+houseNum+"/"+location.toKey();
    }
    
    public static String makeURIEntry(Street street, int houseNum)
    {
        House house = HouseLogic.getHouse(street, houseNum);
        DebugUtils.trace("Retrieved house at "+street.getID()+", #"+houseNum+", with template "+house.getTemplate().getID()+", and entry "+house.getTemplate().getEntry()+".");
        return makeURI(street, houseNum, house.getTemplate().getEntry());
    }

    @Override
    public LociBase load(String uri)
    {
        //SquareURI u = new SquareURI(uri);
        JSONObject json = mDisk.loadJSON(uri);
        if (json == null)
        {
            json = new JSONObject();
            json.put("$firstTime", true);
        }
        else
            json.remove("$firstTime");
        json.put(LociBase.ID_URI, uri);
        LociSquare obj = new LociSquare(json);
        return obj;
    }

    @Override
    public void save(LociBase obj)
    {
        JSONObject json = obj.toJSON();
        json.remove(LociObject.ID_NAME);
        json.remove(LociObject.ID_DECRIPTION);
        mDisk.saveJSON(json);
    }

    @Override
    public void delete(String uri)
    {
        mDisk.delete(uri);
    }

    @Override
    public <T> List<T> findSome(String dataProfile,
            Function<T, Boolean> matcher, int limit, WeakCache<String, LociBase> cache)
    {
        List<T> found = new ArrayList<>();
        return found;
    }

    @Override
    public void clearCache()
    {
    }

    public class SquareURI
    {
        public String mURI;
        public Street mStreet;
        public int mHouseNum;
        public House mHouse;
        public PLocationRef mLocation;
        
        public SquareURI(Street street, int houseNum, PLocationRef location)
        {
            mStreet = street;
            mHouseNum = houseNum;
            mHouse = HouseLogic.getHouse(mStreet, mHouseNum);
            mLocation = location;
            mURI = PREFIX+mStreet.getID()+":"+mHouseNum+"/"+mLocation.getID();
        }
        
        public SquareURI(String uri)
        {
            mURI = uri;
            uri = uri.substring(PREFIX.length());
            int o = uri.indexOf(':');
            String streetID = uri.substring(0, o);
            uri = uri.substring(o + 1);
            mStreet = LocationLogic.getStreet(streetID);
            o = uri.indexOf('/');
            mHouseNum = IntegerUtils.parseInt(uri.substring(0, o));
            uri = uri.substring(o + 1);
            mHouse = HouseLogic.getHouse(mStreet, mHouseNum);
            mLocation = mHouse.getLocation(uri);
        }
        
        public String toURI()
        {
            return mURI;
        }
        
        public PApature getApature(int dir)
        {
            PLocationRef id = getApatureRef(dir);
            PApature apature = HouseLogic.getApature(id.getID());
            if (apature == null)
                throw new IllegalArgumentException("No source for "+mURI+", id="+id);
            return apature;
        }
        
        public PLocationRef getApatureRef(int dir)
        {
            PLocationRef id = mHouse.getLocation(mLocation, dir);
            return id;
        }
        
        public PSquare getThis()
        {
            PSquare ret = HouseLogic.getSquare(mLocation.getID());
            if (ret == null)
                throw new IllegalArgumentException("Cannot find square coresponding to "+mLocation.getID());
            return ret;
        }
    }
}
