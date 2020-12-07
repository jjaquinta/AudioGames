package jo.audio.thieves.logic;

import jo.audio.thieves.data.gen.City;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Intersection;
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
    
    public static Object getFromURI(String id)
    {
        if (id == null)
            return null;
        if (id.startsWith(ThievesConstLogic.STREET_URI) || id.startsWith("STR"))
            return getStreet(id);
        if (id.startsWith(ThievesConstLogic.INTERSECTION_URI) || id.startsWith("INT"))
            return getIntersection(id);
        if (id.startsWith(ThievesConstLogic.HOUSE_URI))
            return getHouse(id);
        return null;
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
    
    public static House getHouse(String id)
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
        id = id.substring(o + 1);
        o = id.indexOf('/');
        int houseNumber = IntegerUtils.parseInt((o < 0) ? id : id.substring(0, o));
        House h = HouseLogic.getHouse(street, houseNumber);
        return h;
    }
}
