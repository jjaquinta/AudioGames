package jo.audio.loci.core.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.core.logic.stores.MemoryStore;
import jo.util.beans.WeakCache;

public class DataStoreLogic
{
    private static final List<IDataStore> mDataStores = new LinkedList<>();
    static
    {
        registerDataStore(new DiskStore());
        registerDataStore(new MemoryStore());
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

    private static IDataStore getStore(String uri)
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
    }
    
    public static void delete(LociBase obj)
    {
        IDataStore store = getStore(obj);
        store.delete(obj.getURI());
    }
    
    public static LociBase findFirst(String dataProfile, Function<LociBase, Boolean> matcher)
    {
        // check cache first
        for (LociBase ret : mCache.getAll())
            if (ret.getDataProfile().equals(dataProfile) && matcher.apply(ret))
                return DataProfileLogic.cast(ret);
        // now do expensive lookup
        for (IDataStore store : mDataStores)
        {
            LociBase ret = store.findFirst(dataProfile, matcher);
            if (ret != null)
                return DataProfileLogic.cast(ret);
        }
        return null;
    }
}
