package jo.audio.thieves.test;

import jo.audio.thieves.data.gen.City;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.logic.LocationLogic;
import jo.audio.thieves.slu.ThievesModelConst;

public class DumpCity
{
    public void run()
    {
        City city = LocationLogic.getCity();
        System.out.println("City name: "+ThievesModelConst.expand(city.getName()));
        System.out.println("River name: "+ThievesModelConst.expand(city.getRiverName()));
        System.out.println("Num Streets: "+city.getStreets().size());
        boolean firstStreet = true;
        boolean firstHouse = true;
        int numHouses = 0;
        int numRooms = 0;
        for (Street street : city.getStreets().values())
        {
            System.out.println("  Street "+ThievesModelConst.expand(street.getName())
                    +", posh="+(int)(100*street.getPosh())
                    +", houses="+street.getHouses()
                    +", type="+street.getType()
                    +", id="+street.getID());
            numHouses += street.getHouses();
            for (int i = 1; i <= street.getHouses(); i++)
            {
                //DebugUtils.mDebugLevel = DebugUtils.TRACE;
                House house = LocationLogic.getHouse(street.getID()+":"+i);
                numRooms += house.getTemplate().getLocations().size();
                if (firstStreet)
                {
                    firstStreet = false;
                    System.out.println("    House: #"+house.getHouseNumber()+", squares=#"+house.getTemplate().getNumSquares()+", apateurs=#"+house.getTemplate().getNumApatures());
                    if (firstHouse)
                    {
                        firstHouse = false;
                        for (PLocationRef l : house.getTemplate().getLocations().values())
                        {
                            System.out.println("      "+l.getID()+", "+l.getX()+","+l.getY()+","+l.getZ());
                        }
                    }
                }
            }
        }
        System.out.println("Num Intersections: "+city.getIntersections().size());
        for (Intersection i : city.getIntersections().values())
        {
            System.out.println("  Intersection: id="+i.getID()
                +", name="+ThievesModelConst.expand(i.getName())
                +", desc="+ThievesModelConst.expand(i.getDescription()));
        }
        System.out.println("Total houses: "+numHouses);
        System.out.println("Total rooms : "+numRooms);
    }
    
    public static void main(String[] argv)
    {
        DumpCity app = new DumpCity();
        app.run();
    }
}
