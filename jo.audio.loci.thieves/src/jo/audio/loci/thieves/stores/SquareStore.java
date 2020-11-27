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
import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.logic.LocationLogic;
import jo.util.beans.WeakCache;

public class SquareStore implements IDataStore
{
    public static final String PREFIX = "square://";
    
    private DiskCache   mDisk;
    
    public SquareStore()
    {
        mDisk = new DiskCache(DiskStore.PREFIX, "squares");
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
        JSONObject json = mDisk.loadJSON(uri);
        if (json == null)
            json = new JSONObject();
        json.put(LociBase.ID_URI, uri);
        json.put(LociBase.ID_DATA_PROFILE, LociIntersection.class.getSimpleName());
        LociIntersection obj = new LociIntersection(json, i);
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
