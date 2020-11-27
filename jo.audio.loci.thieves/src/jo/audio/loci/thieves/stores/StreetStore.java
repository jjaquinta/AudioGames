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
import jo.audio.loci.thieves.data.LociStreet;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.logic.LocationLogic;
import jo.util.beans.WeakCache;
import jo.util.utils.DebugUtils;

public class StreetStore implements IDataStore
{
    public static final String PREFIX = "street://";
    
    private DiskCache   mDisk;
    
    public StreetStore()
    {
        mDisk = new DiskCache(DiskStore.PREFIX, "streets");
    }
    
    @Override
    public boolean isStoreFor(String uri)
    {
        return uri.startsWith(PREFIX);
    }

    @Override
    public LociBase load(String uri)
    {
        Street i = LocationLogic.getStreet(uri.substring(PREFIX.length()));
        if (i == null)
            throw new IllegalArgumentException("No street for "+uri);
        JSONObject json = mDisk.loadJSON(uri);
        if (json == null)
        {
            DebugUtils.debug("Loading "+uri+", no disk image");
            json = new JSONObject();
        }
        else
            DebugUtils.debug("Loading "+uri+", with disk image "+json.toJSONString());
        json.put(LociBase.ID_URI, uri);
        json.put(LociBase.ID_DATA_PROFILE, LociStreet.class.getSimpleName());
        LociStreet obj = new LociStreet(json, i);
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findSome(String dataProfile,
            Function<T, Boolean> matcher, int limit, WeakCache<String, LociBase> cache)
    {
        List<T> found = new ArrayList<>();
        if (!dataProfile.equals(LociStreet.class.getSimpleName()))
            return found;
        for (Street i : LocationLogic.getCity().getStreets().values())
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
