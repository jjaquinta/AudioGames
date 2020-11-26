package jo.audio.loci.core.logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.core.logic.stores.MemoryStore;
import jo.audio.loci.core.logic.stores.NullStore;
import jo.util.beans.WeakCache;

public class DataStoreLogic
{
    private static final List<IDataStore> mDataStores = new LinkedList<>();
    static
    {
        registerDataStore(new DiskStore());
        registerDataStore(new MemoryStore());
        registerDataStore(new NullStore());
    }
    
    private static final WeakCache<String, LociBase>    mCache = new WeakCache<>();
    static
    {
        mCache.setTimeout(15*60*1000L);
    }
    
    public static void registerDataStore(IDataStore store)
    {
        mDataStores.add(0, store);
    }

    public static IDataStore getStore(String uri)
    {
        for (IDataStore store : mDataStores)
            if (store.isStoreFor(uri))
                return store;
        throw new IllegalArgumentException("No store for '"+uri+"'");
    }

    private static IDataStore getStore(LociBase obj)
    {
        return getStore(obj.getURI());
    }
    
    public static LociBase load(String uri)
    {
        if (uri == null)
            return null;
        LociBase raw = mCache.get(uri);
        if (raw != null)
            return raw;
        IDataStore store = getStore(uri);
        raw = store.load(uri);
        if (raw == null)
            return null;
        LociBase cooked = DataProfileLogic.cast(raw);
        mCache.put(uri, cooked);
        return cooked;
    }
    
    public static void save(LociBase obj)
    {
        IDataStore store = getStore(obj);
        store.save(obj);
        mCache.put(obj.getURI(), obj);
    }
    
    public static void delete(LociBase obj)
    {
        IDataStore store = getStore(obj);
        store.delete(obj.getURI());
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T findFirst(String dataProfile, Function<T, Boolean> matcher)
    {
        // check cache first
        for (LociBase ret : mCache.getAll())
            if (ret.getDataProfile().equals(dataProfile) && matcher.apply((T)ret))
                return (T)DataProfileLogic.cast(ret);
        // now do expensive lookup
        for (IDataStore store : mDataStores)
        {
            List<T> ret = store.findSome(dataProfile, matcher, 1, mCache);
            if ((ret != null) && (ret.size() > 0))
                return ret.get(0);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> List<T> findAll(String dataProfile, Function<T, Boolean> matcher)
    {
        List<T> found = new ArrayList<>();
        // now do expensive lookup
        for (IDataStore store : mDataStores)
        {
            List<T> ret = store.findSome(dataProfile, matcher, -1, mCache);
            if (ret != null)
                for (T item : ret)
                {
                    LociBase i = (LociBase)item;
                    LociBase ci = mCache.get(i.getURI());
                    if (ci != null)
                        found.add((T)ci);
                    else
                        found.add(item);
                }
        }
        return found;
    }

    public static void clearCache()
    {
        mCache.clear();
        for (IDataStore store : mDataStores)
            store.clearCache();
    }
}
