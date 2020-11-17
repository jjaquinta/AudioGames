package jo.audio.loci.core.logic.stores;

import java.util.function.Function;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.logic.IDataStore;

public class NullStore implements IDataStore
{
    public static final String PREFIX = "null://";
    
    public NullStore()
    {
    }

    @Override
    public boolean isStoreFor(String uri)
    {
        return uri.startsWith(PREFIX);
    }

    @Override
    public LociBase load(String uri)
    {
        return null;
    }

    @Override
    public void save(LociBase obj)
    {
    }

    @Override
    public LociBase findFirst(String dataProfile,
            Function<LociBase, Boolean> matcher)
    {
        return null;
    }

    @Override
    public void delete(String uri)
    {
    }

    @Override
    public void clearCache()
    {
    }
}
