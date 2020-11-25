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
import jo.audio.loci.thieves.data.LociIntersection;
import jo.audio.loci.thieves.data.LociStreet;
import jo.audio.loci.thieves.data.LociThing;
import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.data.gen.Street;
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
        LociObject source = (LociObject)DataStoreLogic.load(uris[0]);
        LociObject target = (LociObject)DataStoreLogic.load(uris[1]);
        JSONObject json = new JSONObject();                
        json.put(LociBase.ID_URI, uri);
        json.put(LociBase.ID_DATA_PROFILE, LociExit.PROFILE);
        json.put(LociExit.ID_SOURCE, uris[0]);
        json.put(LociExit.ID_DESTINATION, uris[1]);
        json.put(LociExit.ID_DIRECTION, getDirection(source, target));
        json.put(LociExit.ID_ELEVATION, getElevation(source, target));
        json.put(LociThing.ID_PUBLIC, true);
        json.put(LociObject.ID_NAME, target.getName());
        json.put(LociObject.ID_DECRIPTION, target.getDescription());
        LociExit obj = new LociExit(json);
        return obj;
    }
    
    private int getDirection(LociObject source, LociObject target)
    {
        if (source instanceof LociIntersection)
        {
            Intersection i = ((LociIntersection)source).getIntersection();
            if (target instanceof LociStreet)
            {
                Street s = ((LociStreet)target).getStreet();
                Street[] streets = i.getCardinalStreets();
                for (int dir = 0; dir < streets.length; dir++)
                    if ((streets[dir] != null) && (streets[dir].getID().equals(s.getID())))
                        return dir;
                return -1;
            }
        }
        else if (source instanceof LociStreet)
        {
            Street s = ((LociStreet)source).getStreet();
            if (target instanceof LociIntersection)
            {
                Intersection i = ((LociIntersection)target).getIntersection();
                if (s.getHighIntersection().getID().equals(i.getID()))
                    return s.getHighDir();
                if (s.getLowIntersection().getID().equals(i.getID()))
                    return s.getLowDir();
                return -1;
            }
        }
        throw new IllegalArgumentException("Cannot map direction from "+source+" to "+target);
    }
    
    private int getElevation(LociObject source, LociObject target)
    {
        if (target instanceof LociIntersection)
        {
            Intersection i = ((LociIntersection)target).getIntersection();
            return i.getElevation();
        }
        else if (target instanceof LociStreet)
        {
            Street s = ((LociStreet)target).getStreet();
            return (s.getHighIntersection().getElevation() + s.getLowIntersection().getElevation())/2;
        }
        throw new IllegalArgumentException("Cannot map direction from "+source+" to "+target);
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
        // NOP
    }

}
