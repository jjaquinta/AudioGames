package jo.audio.thieves.logic;

import jo.audio.thieves.data.ThievesPositionBean;
import jo.audio.thieves.data.gen.City;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.data.gen.Location;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.logic.gen.CityLogic;
import jo.audio.thieves.logic.gen.HouseLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;

public class LocationLogic
{
    private static City mCity = null;

    public static City getCity()
    {
        if (mCity == null)
            mCity = CityLogic.generateCity(ThievesConstLogic.CITY_SEED);
        return mCity;
    }
    
    public static ThievesPositionBean getPosition(String uri)
    {
        if (uri == null)
            return null;
        ThievesPositionBean p = new ThievesPositionBean();
        p.setCity(getCity());
        if (uri.startsWith(ThievesConstLogic.INTERSECTION_URI) || uri.startsWith("INT"))
            p.setIntersection(getIntersection(uri));
        else if (uri.startsWith(ThievesConstLogic.STREET_URI) || uri.startsWith("STR"))
            p.setStreet(getStreet(uri));
        else if (uri.startsWith(ThievesConstLogic.HOUSE_URI))
            getHouse(uri, p);
        else if (uri.startsWith(ThievesConstLogic.LOCATION_URI))
            getLocation(uri, p);
        return p;
    }
    
    public static Object getFromURI(String id)
    {
        if (id == null)
            return null;
        if (id.startsWith(ThievesConstLogic.STREET_URI) || id.startsWith("STR"))
            return getStreet(id);
        if (id.startsWith(ThievesConstLogic.INTERSECTION_URI) || id.startsWith("INT"))
            return getIntersection(id);
        if (id.startsWith(ThievesConstLogic.HOUSE_URI))
            return getHouse(id, null);
        if (id.startsWith(ThievesConstLogic.LOCATION_URI))
            return getLocation(id);
        return null;
    }
    
    public static Location getLocation(String id)
    {
        if (id == null)
            return null;
        if (id.startsWith(ThievesConstLogic.LOCATION_URI))
            id = id.substring(ThievesConstLogic.LOCATION_URI.length());
        int o = id.lastIndexOf('/');
        String locationID = id.substring(o + 1);
        String houseID = id.substring(0, o);
        House h = LocationLogic.getHouse(houseID, null);
        return h.getLocations().get(locationID);
    }
    
    public static Street getStreet(String id)
    {
        if (id == null)
            return null;
        if (id.startsWith(ThievesConstLogic.STREET_URI))
            id = id.substring(ThievesConstLogic.STREET_URI.length());
        return getCity().getStreets().get(id);
    }
    
    public static Intersection getIntersection(String id)
    {
        if (id == null)
            return null;
        if (id.startsWith(ThievesConstLogic.INTERSECTION_URI))
            id = id.substring(ThievesConstLogic.INTERSECTION_URI.length());
        if (getCity().getIntersections().containsKey(id))
            return getCity().getIntersections().get(id);
        DebugUtils.trace("Can't resolve intersection '"+id+"'");
        DebugUtils.trace("Intersections present:");
        for (Intersection i : getCity().getIntersections().values())
            DebugUtils.trace(i.getID());
        return null;
    }
    
    public static House getHouse(String id, ThievesPositionBean pos)
    {
        if (id == null)
            return null;
        if (id.startsWith(ThievesConstLogic.HOUSE_URI))
            id = id.substring(ThievesConstLogic.HOUSE_URI.length());
        int o = id.indexOf(":");
        if (o < 0)
            throw new IllegalArgumentException("Ill formed house ID: '"+id+"'");
        String streetID = id.substring(0, o);
        Street street = getStreet(streetID);
        if (pos != null)
            pos.setStreet(street);
        id = id.substring(o + 1);
        o = id.indexOf('/');
        int houseNumber = IntegerUtils.parseInt((o < 0) ? id : id.substring(0, o));
        House h = HouseLogic.getHouse(street, houseNumber);
        if (pos != null)
            pos.setHouse(h);
        return h;
    }
    
    public static Location getLocation(String id, ThievesPositionBean pos)
    {
        if (id == null)
            return null;
        DebugUtils.trace("Resolving location '"+id+"'");
        if (id.startsWith(ThievesConstLogic.LOCATION_URI))
            id = id.substring(ThievesConstLogic.LOCATION_URI.length());
        int o = id.indexOf(":");
        if (o < 0)
            throw new IllegalArgumentException("Ill formed house ID: '"+id+"'");
        String streetID = id.substring(0, o);
        DebugUtils.trace("StreetID='"+streetID+"'");
        Street street = getStreet(streetID);
        if (pos != null)
            pos.setStreet(street);
        String houseID = id.substring(o + 1);
        DebugUtils.trace("House ID '"+id+"' -> '"+houseID+"', o="+o);
        o = houseID.indexOf('/');
        String houseStr = (o < 0) ? houseID : houseID.substring(0, o);
        int houseNumber = IntegerUtils.parseInt(houseStr);
        DebugUtils.trace("House#='"+id+"' -> '"+houseStr+"' -> "+houseNumber);
        House h = HouseLogic.getHouse(street, houseNumber);
        if (pos != null)
            pos.setHouse(h);
        String locID = (o < 0) ? h.getEntry() : houseID.substring(o + 1);
        DebugUtils.trace("locID.1='"+locID+"'");
        o = locID.indexOf('?');
        if (o > 0)
        {
            if (pos != null)
                pos.setQueryParams(locID.substring(o + 1));
            locID = locID.substring(0, o);
        }
        else if (o == 0)
            locID = h.getEntry();
        DebugUtils.trace("locID.2='"+locID+"'");
        Location l = h.getLocations().get(locID);
        if (pos != null)
            pos.setLocation(l);
        return l;
    }
}
