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
import jo.audio.loci.thieves.data.LociIntersection;
import jo.audio.loci.thieves.data.LociRoom;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Location;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.logic.LocationLogic;
import jo.util.beans.WeakCache;

public class HouseStore implements IDataStore
{
    // house://street:house#/houseid
    public static final String PREFIX = "house://";
    
    private DiskCache                   mDisk;
    private WeakCache<String, Location> mCache = new WeakCache<>(15*60*1000L);
    
    public HouseStore()
    {
        mDisk = new DiskCache(DiskStore.PREFIX, "houses");
    }
    
    @Override
    public boolean isStoreFor(String uri)
    {
        return uri.startsWith(PREFIX);
    }

    @Override
    public LociBase load(String uri)
    {
        Location location = mCache.get(uri);
        if (location == null)
        {
            String houseID = uri.substring(PREFIX.length());
            int o = houseID.lastIndexOf('/');
            String locationID = houseID.substring(o + 1);
            houseID = houseID.substring(0, o);
            House h = LocationLogic.getHouse(houseID, null);
            for (Location l : h.getLocations().values())
            {
                String u = PREFIX+houseID+"/"+l.getID();
                mCache.put(u, l);
                if (locationID.equals(l.getID()))
                    location = l;
            }
            if (location == null)
                throw new IllegalArgumentException("Cannot find "+locationID+" in house "+h.getStreet()+":"+h.getHouseNumber());
        }
        mCache.put(uri, location);
        JSONObject json = mDisk.loadJSON(uri);
        if (json == null)
            json = new JSONObject();
        json.put(LociBase.ID_URI, uri);
        json.put(LociBase.ID_DATA_PROFILE, LociRoom.class.getSimpleName());
        LociRoom obj = new LociRoom(json, location);
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
    @SuppressWarnings("unchecked")
    public <T> List<T> findSome(String dataProfile,
            Function<T, Boolean> matcher, int limit, WeakCache<String, LociBase> cache)
    {
        List<T> found = new ArrayList<>();
        if (!dataProfile.equals(LociIntersection.class.getSimpleName()))
            return found;
        for (Street i : LocationLogic.getCity().getStreets().values())
            for (int h = 1; h <= i.getHouses(); h++)
            {
                House house = LocationLogic.getHouse(i.getID()+":"+h, null);
                for (String l : house.getLocations().keySet())
                {
                    String uri = PREFIX+i.getID()+":"+h+"/"+l;
                    mCache.put(uri, house.getLocations().get(l));
                    T base = (T)cache.get(uri);
                    if (base == null)
                    {
                        base = (T)load(uri);
                        cache.put(uri, (LociBase)base);
                    }
                    if (matcher.apply(base))
                    {
                        found.add(base);
                        if ((limit > 0) && (found.size() >= limit))
                            return found;
                    }
                }
            }
        return found;
    }

    @Override
    public void clearCache()
    {
        mCache.clear();
    }

}
