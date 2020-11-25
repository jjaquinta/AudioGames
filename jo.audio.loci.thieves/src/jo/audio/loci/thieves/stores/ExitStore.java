package jo.audio.loci.thieves.stores;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.IDataStore;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.logic.LocationLogic;

public class ExitStore implements IDataStore
{
    public static final String PREFIX = "exit://";
    
    public ExitStore()
    {
    }

    public static String toURI(String sourceURI, String destinationURI)
    {
        return PREFIX+stripPrefix(sourceURI)+"/"+stripPrefix(destinationURI);
    }
    
    public static String stripPrefix(String uri)
    {
        int o = uri.indexOf("://");
        if (o > 0)
            return uri.substring(o + 3);
        else
            return uri;
    }

    public static String[] fromURI(String uri)
    {
        uri = uri.substring(PREFIX.length());
        int o = uri.indexOf('/');
        String sourceURI = addPrefix(uri.substring(0, o));
        String destinationURI = addPrefix(uri.substring(o+1));
        return new String[] { sourceURI, destinationURI };
    }
    
    public static String addPrefix(String id)
    {
        if (id.startsWith("INT"))
            return SquareStore.PREFIX + id;
        if (id.startsWith("STR"))
            return StreetStore.PREFIX + id;
        throw new IllegalArgumentException("Unknown ID: id");
    }
    
//    private String toexitURI(String memoryURI)
//    {
//        String base = memoryURI.substring(MemoryStore.PREFIX.length()+7);
//        return MemoryStore.PREFIX+base;
//    }
    
    @Override
    public boolean isStoreFor(String uri)
    {
        return uri.startsWith(PREFIX);
    }

    @Override
    public LociBase load(String uri)
    {
        String[] uris = fromURI(uri);
        JSONObject json = new JSONObject();                
        json.put(LociBase.ID_URI, uri);
        json.put(LociBase.ID_DATA_PROFILE, LociExit.PROFILE);
        json.put(LociExit.ID_SOURCE, uris[0]);
        json.put(LociExit.ID_DESTINATION, uris[1]);
        LociObject target = (LociObject)DataStoreLogic.load(uris[1]);
        json.put(LociObject.ID_NAME, target.getName());
        json.put(LociObject.ID_DECRIPTION, target.getDescription());
        LociExit obj = new LociExit(json);
        return obj;
    }

    @Override
    public void save(LociBase obj)
    {
        // NOP
    }

    @Override
    public void delete(String uri)
    {
        //NOP Generated objects cannot be deleted
    }

    @Override
    public <T> List<T> findSome(String dataProfile,
            Function<T, Boolean> matcher, int limit)
    {
        List<T> found = new ArrayList<>();
        if (!dataProfile.equals(LociExit.PROFILE))
            return found;
        for (Intersection i : LocationLogic.getCity().getIntersections().values())
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
        throw new IllegalStateException("Not implemented yet");
    }

}
