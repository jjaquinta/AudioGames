package jo.audio.loci.core.logic.stores;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.logic.DataProfileLogic;
import jo.audio.loci.core.logic.IDataStore;
import jo.util.beans.WeakCache;

public class DiskStore implements IDataStore
{
    public static final String PREFIX = "disk://";
    
    private DiskCache   mCache;
    
    public DiskStore()
    {
        mCache = new DiskCache(PREFIX);
    }

    public String getDiskLocation()
    {
        return mCache.getBaseDir().toString();
    }

    @Override
    public boolean isStoreFor(String uri)
    {
        return uri.startsWith(PREFIX);
    }

    @Override
    public LociBase load(String uri)
    {
        JSONObject json = mCache.loadJSON(uri);
        if (json == null)
            return null;
        LociBase obj = DataProfileLogic.instantiate(json);
        return obj;
    }

    @Override
    public void save(LociBase obj)
    {
        JSONObject json = obj.toJSON();
        mCache.saveJSON(json);
    }

    @Override
    public <T> List<T> findSome(final String dataProfile,
            final Function<T, Boolean> matcher, final int limit, final WeakCache<String, LociBase> cache)
    {
        final List<T> found = new ArrayList<>();
        mCache.iterate((json) -> doFindSome(json, found, dataProfile, matcher, limit, cache));
        return found;
    }

    private <T> boolean doFindSome(JSONObject json, List<T> found, String dataProfile,
            Function<T, Boolean> matcher, int limit, WeakCache<String, LociBase> cache)
    {
        if (!dataProfile.equals(json.get(LociBase.ID_DATA_PROFILE)))
            return false;
        LociBase obj = DataProfileLogic.instantiate(json);
        cache.put(obj.getURI(), obj);
        @SuppressWarnings("unchecked")
        T item = (T)obj;
        if (matcher.apply(item))
        {
            found.add(item);
            if ((limit > 0) && (found.size() >= limit))
                return true;
        }
        return false;
    }

    @Override
    public void delete(String uri)
    {
        mCache.delete(uri);
    }

    @Override
    public void clearCache()
    {
    }
}
