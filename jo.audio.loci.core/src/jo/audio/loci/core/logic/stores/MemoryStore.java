package jo.audio.loci.core.logic.stores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.logic.DataProfileLogic;
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
        JSONObject json = loadJSON(uri);
        return load(json);
    }

    public JSONObject loadJSON(String uri)
    {
        JSONObject json = mStore.get(uri);
        return json;
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
        saveJSON(obj.getURI(), obj.toJSON());
    }

    public void saveJSON(String uri, JSONObject json)
    {
        mStore.put(uri, json);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findSome(String dataProfile,
            Function<T, Boolean> matcher, int limit)
    {
        List<T> found = new ArrayList<>();
        for (JSONObject json : mStore.values())
        {
            LociBase ret = load(json);
            if (!ret.getDataProfile().equals(dataProfile))
                continue;
            T item = (T)DataProfileLogic.cast(ret);
            if (matcher.apply(item))
            {
                found.add(item);
                if ((limit > 0) && (found.size() >= limit))
                    break;
            }
        }
        return found;
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
