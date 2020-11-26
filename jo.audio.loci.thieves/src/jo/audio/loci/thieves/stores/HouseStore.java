package jo.audio.loci.thieves.stores;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.IDataStore;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.thieves.data.LociIntersection;
import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.logic.LocationLogic;
import jo.util.beans.WeakCache;

public class HouseStore implements IDataStore
{
    public static final String PREFIX = "house://";
    
    private DiskStore   mDisk;
    
    public HouseStore()
    {
        mDisk = (DiskStore)DataStoreLogic.getStore(DiskStore.PREFIX);
    }

    private String toDiskURI(String squareURI)
    {
        String base = squareURI.substring(PREFIX.length());
        return DiskStore.PREFIX+"house/"+base;
    }
    
    @Override
    public boolean isStoreFor(String uri)
    {
        return uri.startsWith(PREFIX);
    }

    @Override
    public LociBase load(String uri)
    {
        Intersection i = LocationLogic.getIntersection(uri.substring(PREFIX.length()));
        String diskURL = toDiskURI(uri);
        JSONObject json = mDisk.loadJSON(diskURL);
        if (json == null)
            json = new JSONObject();
        json.put(LociBase.ID_URI, uri);
        json.put(LociBase.ID_DATA_PROFILE, LociIntersection.PROFILE);
        LociIntersection obj = new LociIntersection(json, i);
        return obj;
    }

    @Override
    public void save(LociBase obj)
    {
        JSONObject json = obj.toJSON();
        json.remove(LociObject.ID_NAME);
        json.remove(LociObject.ID_DECRIPTION);
        String squareURI = (String)json.get(LociBase.ID_URI);
        String newURI = toDiskURI(squareURI);
        json.put(LociBase.ID_URI, newURI);
        mDisk.saveJSON(json);
    }

    @Override
    public void delete(String uri)
    {
        // NOP Generated objects cannot be deleted
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findSome(String dataProfile,
            Function<T, Boolean> matcher, int limit, WeakCache<String, LociBase> cache)
    {
        List<T> found = new ArrayList<>();
        if (!dataProfile.equals(LociIntersection.PROFILE))
            return found;
        for (Intersection i : LocationLogic.getCity().getIntersections().values())
        {
            String uri = PREFIX+i.getID();
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
                    break;
            }
        }
        return found;
    }

    @Override
    public void clearCache()
    {
        // NOP
    }

}