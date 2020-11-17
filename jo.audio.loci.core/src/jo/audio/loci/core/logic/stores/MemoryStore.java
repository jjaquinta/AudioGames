package jo.audio.loci.core.logic.stores;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.logic.IDataStore;

public class MemoryStore implements IDataStore
{
    public static final String PREFIX = "memory://";
    
    private Map<String, JSONObject> mStore = new HashMap<String, JSONObject>();
    
    public MemoryStore()
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
        JSONObject json = mStore.get(uri);
        return load(json);
    }
    private LociBase load(JSONObject json)
    {
        if (json == null)
            return null;
        LociBase obj = new LociBase(json);
        obj.fromJSON(json);
        return obj;
    }

    @Override
    public void save(LociBase obj)
    {
        mStore.put(obj.getURI(), obj.toJSON());
    }

    @Override
    public LociBase findFirst(String dataProfile,
            Function<LociBase, Boolean> matcher)
    {
        for (JSONObject json : mStore.values())
        {
            LociBase ret = load(json);
            if (ret.getDataProfile().equals(dataProfile) && matcher.apply(ret))
                return ret;
        }
        return null;
    }

    @Override
    public void delete(String uri)
    {
        mStore.remove(uri);
    }

    @Override
    public void clearCache()
    {
        mStore.clear();
    }
}
