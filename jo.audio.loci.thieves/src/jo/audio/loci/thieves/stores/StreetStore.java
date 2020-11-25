package jo.audio.loci.thieves.stores;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.IDataStore;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.thieves.data.LociStreet;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.logic.LocationLogic;

public class StreetStore implements IDataStore
{
    public static final String PREFIX = "street://";
    
    private DiskStore   mDisk;
    
    public StreetStore()
    {
        mDisk = (DiskStore)DataStoreLogic.getStore(DiskStore.PREFIX);
    }

    private String toDiskURI(String streetURI)
    {
        String base = streetURI.substring(PREFIX.length());
        return DiskStore.PREFIX+"street/"+base;
    }
    
//    private String toSquareURI(String diskURI)
//    {
//        String base = diskURI.substring(DiskStore.PREFIX.length()+7);
//        return DiskStore.PREFIX+base;
//    }
    
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
        String diskURL = toDiskURI(uri);
        JSONObject json = mDisk.loadJSON(diskURL);
        if (json == null)
            json = new JSONObject();
        json.put(LociBase.ID_URI, uri);
        json.put(LociBase.ID_DATA_PROFILE, LociStreet.PROFILE);
        LociStreet obj = new LociStreet(json, i);
        return obj;
    }

    @Override
    public void save(LociBase obj)
    {
        JSONObject json = obj.toJSON();
        json.remove(LociObject.ID_NAME);
        json.remove(LociObject.ID_DECRIPTION);
        String streetURI = (String)json.get(LociBase.ID_URI);
        String newURI = toDiskURI(streetURI);
        json.put(LociBase.ID_URI, newURI);
        mDisk.saveJSON(json);
    }

    @Override
    public void delete(String uri)
    {
        // NOP Generated objects cannot be deleted
    }

    @Override
    public <T> List<T> findSome(String dataProfile,
            Function<T, Boolean> matcher, int limit)
    {
        List<T> found = new ArrayList<>();
        if (!dataProfile.equals(LociStreet.PROFILE))
            return found;
        for (Street i : LocationLogic.getCity().getStreets().values())
        {
            @SuppressWarnings("unchecked")
            T base = (T)load(PREFIX+i.getID());
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
