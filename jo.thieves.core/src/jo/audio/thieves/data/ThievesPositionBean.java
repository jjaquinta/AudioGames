package jo.audio.thieves.data;

import jo.audio.thieves.data.gen.City;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.data.gen.Location;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.logic.ThievesConstLogic;

public class ThievesPositionBean
{
    private City          mCity;
    private Street        mStreet;
    private Intersection  mIntersection;
    private House         mHouse;
    private Location      mLocation;
    private String        mQueryParams;
    
    // utilities
    public String getURI()
    {
        String uri = null;
        if (mLocation != null)
            uri = ThievesConstLogic.LOCATION_URI+mStreet.getID()+":"+mHouse.getHouseNumber()+"/"+mLocation.getID();
        else if (mHouse != null)
            uri = ThievesConstLogic.HOUSE_URI+mStreet.getID()+":"+mHouse.getHouseNumber();
        else if (mStreet != null)
            uri =  ThievesConstLogic.STREET_URI+mStreet.getID();
        else if (mIntersection != null)
            uri = ThievesConstLogic.INTERSECTION_URI+mIntersection.getID();
        if (mQueryParams != null && uri != null)
            uri += "?" + mQueryParams;
        return uri;
    }
    
    public boolean isLocation()
    {
        return (mLocation != null);
    }
    
    public boolean isHouse()
    {
        return (mHouse != null);
    }
    
    public boolean isStreet()
    {
        return (mStreet != null);
    }
    
    public boolean isIntersection()
    {
        return (mIntersection != null);
    }
    
    @Override
    public String toString()
    {
        return getURI();
    }
    
    // getters and setters

    public City getCity()
    {
        return mCity;
    }
    public void setCity(City city)
    {
        mCity = city;
    }
    public Street getStreet()
    {
        return mStreet;
    }
    public void setStreet(Street street)
    {
        mStreet = street;
    }
    public Intersection getIntersection()
    {
        return mIntersection;
    }
    public void setIntersection(Intersection intersection)
    {
        mIntersection = intersection;
    }
    public House getHouse()
    {
        return mHouse;
    }
    public void setHouse(House house)
    {
        mHouse = house;
    }
    public Location getLocation()
    {
        return mLocation;
    }
    public void setLocation(Location location)
    {
        mLocation = location;
    }

    public String getQueryParams()
    {
        return mQueryParams;
    }

    public void setQueryParams(String queryParams)
    {
        mQueryParams = queryParams;
    }
}
