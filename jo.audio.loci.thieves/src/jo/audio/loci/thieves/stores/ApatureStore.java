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
import jo.audio.loci.thieves.data.LociApature;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.loci.thieves.data.LociThing;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.logic.LocationLogic;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.audio.thieves.logic.gen.HouseLogic;
import jo.util.beans.WeakCache;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

public class ApatureStore implements IDataStore
{
    // apature://<streetID<:<house#>/<x,y,z>/dir
    public static final String PREFIX = "apature://";
    
    private DiskCache   mDisk;
    private static ApatureStore mInstance;
    
    public ApatureStore()
    {
        mDisk = new DiskCache(DiskStore.PREFIX, "apatures");
        mInstance = this;
    }

    @Override
    public boolean isStoreFor(String uri)
    {
        return uri.startsWith(PREFIX);
    }
    
    public static String flipURI(String uri)
    {
        ApatureURI u = mInstance.new ApatureURI(uri);
        return u.flip().toURI();
    }

    @Override
    public LociBase load(String uri)
    {
        ApatureURI u = new ApatureURI(uri);
        JSONObject json = mDisk.loadJSON(uri);
        if (json == null)
        {
            DebugUtils.debug("Loading "+uri+", no disk image");
            json = new JSONObject();
        }
        else
            DebugUtils.debug("Loading "+uri+", with disk image "+json.toJSONString());
        PApature apature = u.getThis();
        json.put(LociBase.ID_URI, uri);
        json.put(LociExit.ID_SOURCE, SquareStore.makeURI(u.mStreet, u.mHouseNum, u.getSourceRef()));
        json.put(LociExit.ID_DESTINATION, SquareStore.makeURI(u.mStreet, u.mHouseNum, u.getDestinationRef()));
        json.put(LociThing.ID_PUBLIC, true);
        json.put(LociExit.ID_DIRECTION, u.mDir);
        json.put(LociObject.ID_NAME, makeName(apature, u));
        json.put(LociObject.ID_DECRIPTION, apature.getDescription());
        LociApature obj = new LociApature(json);
        return obj;
    }

    private Object makeName(PApature apature, ApatureURI u)
    {
        String name = apature.getName();
        if (name == null)
            name = "";
        else
            name += "|";
        name += "{{DIRECTION_NAME#"+u.mDir+"}}";
        name += "|{{DIRECTION_ABBREVIATION#"+u.mDir+"}}";
        PLocationRef destRef = u.getDestinationRef();
        PSquare dest = HouseLogic.getSquare(destRef.getID());
        String destName = dest.getName();
        if (!StringUtils.isTrivial(destName))
            name += "|"+destName;
        return name;
    }

    @Override
    public void save(LociBase obj)
    {
        mDisk.saveJSON(obj.toJSON());
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
        // NOP
    }

    public class ApatureURI
    {
        String mURI;
        Street mStreet;
        int mHouseNum;
        House mHouse;
        PLocationRef mLocation;
        int mDir;
        
        public ApatureURI(Street street, int houseNum, PLocationRef location, int dir)
        {
            mStreet = street;
            mHouseNum = houseNum;
            mHouse = HouseLogic.getHouse(mStreet, mHouseNum);
            mLocation = location;
            mDir = dir;
            mURI = PREFIX+mStreet.getID()+":"+mHouseNum+"/"+mLocation.toKey()+"/"+mDir;
        }
        
        public ApatureURI(String uri)
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
            o = uri.indexOf('/');
            mLocation = mHouse.getLocation(uri.substring(0, o));
            uri = uri.substring(o + 1);
            mDir = IntegerUtils.parseInt(uri);
        }
        
        public String toURI()
        {
            return mURI;
        }
        
        public ApatureURI flip()
        {
            return new ApatureURI(mStreet, mHouseNum, mLocation, ThievesConstLogic.opposite(mDir));
        }
        
        private PSquare getSquare(int dir)
        {
            PLocationRef id = mHouse.getLocation(mLocation, dir);
            if (id == null)
                throw new IllegalArgumentException("No source for "+mURI);
            PSquare square = HouseLogic.getSquare(id.getID());
            if (square == null)
                throw new IllegalArgumentException("No source for "+mURI+", id="+id);
            return square;
        }
        
        public PApature getThis()
        {
            PApature a = HouseLogic.getApature(mLocation.getID());
            if (a == null)
                throw new IllegalStateException("Cannot get apature for "+mURI);
            return a;
        }
        
        public PSquare getSource()
        {
            return getSquare(ThievesConstLogic.opposite(mDir));
        }
        
        public PSquare getDestination()
        {
            return getSquare(mDir);
        }
        
        private PLocationRef getRef(int dir)
        {
            PLocationRef id = mHouse.getLocation(mLocation, dir);
            if (id == null)
                throw new IllegalArgumentException("No source for "+mURI);
            return id;
        }
        
        public PLocationRef getSourceRef()
        {
            return getRef(ThievesConstLogic.opposite(mDir));
        }
        
        public PLocationRef getDestinationRef()
        {
            return getRef(mDir);
        }
    }
}
