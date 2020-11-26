package jo.audio.loci.thieves.stores;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import jo.audio.loci.thieves.data.LociRoom;
import jo.audio.loci.thieves.data.LociStreet;
import jo.audio.loci.thieves.data.LociThing;
import jo.audio.thieves.data.gen.Apature;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.logic.LocationLogic;
import jo.audio.thieves.slu.ThievesModelConst;
import jo.util.beans.WeakCache;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

public class ExitStore implements IDataStore
{
    public static final String PREFIX = "exit://";
    
    public ExitStore()
    {
    }
    
    private static String encode(String txt)
    {
        try
        {
            return URLEncoder.encode(txt, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return txt;
        }
    }
    
    private static String decode(String txt)
    {
        try
        {
            return URLDecoder.decode(txt, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return txt;
        }
    }

    private static String makeURI(String... ids)
    {
        StringBuffer sb = new StringBuffer(PREFIX);
        for (int i = 0; i < ids.length; i++)
        {
            if (i > 0)
                sb.append("/");
            sb.append(encode(stripPrefix(ids[i])));
        }
        return sb.toString();
    }

    public static String toURI(Intersection from, Street to)
    {
        return makeURI(from.getID(), to.getID());
    }

    public static String toURI(Street from, Intersection to, int dir)
    {
        return makeURI(from.getID(), to.getID(), String.valueOf(dir));
    }

    public static String toURI(String from, int dir, String to)
    {
        String uri;
        if ("$exit".equals(to))
        {
            to = from;
            to = to.substring(HouseStore.PREFIX.length());
            int o = to.indexOf(":");
            to = StreetStore.PREFIX+to.substring(0, o);
            uri = makeURI(from, to, String.valueOf(dir));
        }
        else
        {
            String houseID = StringUtils.stripAfterLast(StringUtils.stripBeforeFirst(from, "://"), "/");
            House h = LocationLogic.getHouse(houseID, null);
            Apature a = h.getApatures().get(to);
            String toID = a.getLocation(dir);
            to = houseID + "/" + toID;
            uri = makeURI(from, to, String.valueOf(dir), a.getID());
        }            
        return uri;
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
        String[] segs = uri.split("/");
        for (int i = 0; i < segs.length; i++)
        {
            segs[i] = decode(segs[i]);
            if (i < 2)
                segs[i] = addPrefix(segs[i]);
        }
        return segs;
    }
    
    public static String addPrefix(String id)
    {
        if (id.startsWith("INT"))
            return SquareStore.PREFIX + id;
        if (id.startsWith("STR"))
            if (id.indexOf('/') >= 0)
                return HouseStore.PREFIX + id;
            else
                return StreetStore.PREFIX + id;
        throw new IllegalArgumentException("Unknown ID: "+id);
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
        json.put(LociExit.ID_ELEVATION, getElevation(source, target));
        json.put(LociThing.ID_PUBLIC, true);
        int dir;
        if (uris.length >= 3)
            dir = IntegerUtils.parseInt(uris[2]);
        else
            dir = getDirection(source, target);
        json.put(LociExit.ID_DIRECTION, dir);
        json.put(LociObject.ID_DECRIPTION, target.getDescription());
        if (uris.length >= 4)
        {
            Apature a = ((LociRoom)source).getLocation().getHouse().getApatures().get(uris[3]);
            json.put(LociObject.ID_NAME, getName(a, target, dir));
            if (!StringUtils.isTrivial(a.getDescription()))
                json.put(LociObject.ID_DECRIPTION, a.getDescription());
            json.put(LociExit.ID_APATURE, uris[3]);
        }
        else
            json.put(LociObject.ID_NAME, getName(null, target, dir));
        LociExit obj = new LociExit(json);
        return obj;
    }
    
    private String getName(Apature a, LociObject target, int dir)
    {
        List<String> names = new ArrayList<>();
        if ((a != null) && !StringUtils.isTrivial(a.getName()))
            names.add(a.getName());
        if (target != null)
        {
            String name = target.getName();
            if (!StringUtils.isTrivial(name))
            {
                name = ThievesModelConst.expand(name);
                names.add(name);
                int o = name.indexOf(' ');
                if (o > 0)
                    names.add(name.substring(0, o));
            }
        }
        if (dir >= 0)
        {
            names.add("{{DIRECTION_NAME#"+dir+"}}");
            names.add("{{DIRECTION_ABBREVIATION#"+dir+"}}");
        }
        return StringUtils.listize(names, "|");
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
        if (source instanceof LociRoom)
        {
            if (target instanceof LociRoom)
            {
                return ((LociRoom)source).getLocation().dirTo(((LociRoom)target).getLocation());
            }
            else if (target instanceof LociStreet)
            {
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
        else if (target instanceof LociRoom)
            return -1;
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findSome(String dataProfile,
            Function<T, Boolean> matcher, int limit, WeakCache<String, LociBase> cache)
    {
        List<T> found = new ArrayList<>();
        if (!dataProfile.equals(LociExit.PROFILE))
            return found;
        for (Intersection i : LocationLogic.getCity().getIntersections().values())
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
