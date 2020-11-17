package jo.audio.loci.core.logic;

import java.util.function.Function;

import jo.audio.loci.core.data.LociBase;

public interface IDataStore
{
    public boolean isStoreFor(String uri);
    public LociBase load(String uri);
    public void save(LociBase obj);
    public void delete(String uri);
    public LociBase findFirst(String dataProfile, Function<LociBase, Boolean> matcher);
    public void clearCache();
}
