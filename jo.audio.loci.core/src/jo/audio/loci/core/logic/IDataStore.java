package jo.audio.loci.core.logic;

import java.util.List;
import java.util.function.Function;

import jo.audio.loci.core.data.LociBase;

public interface IDataStore
{
    public boolean isStoreFor(String uri);
    public LociBase load(String uri);
    public void save(LociBase obj);
    public void delete(String uri);
    public <T> List<T> findSome(String dataProfile, Function<T, Boolean> matcher, int limit);
    public void clearCache();
}
