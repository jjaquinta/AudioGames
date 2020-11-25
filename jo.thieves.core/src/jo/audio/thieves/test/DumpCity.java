package jo.audio.thieves.test;

import jo.audio.thieves.data.gen.City;
import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.data.gen.Street;
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
        for (Street street : city.getStreets().values())
        {
            System.out.println("  Street "+ThievesModelConst.expand(street.getName())
                    +", posh="+(int)(100*street.getPosh())
                    +", houses="+street.getHouses()
                    +", type="+street.getType()
                    +", id="+street.getID());
        }
        System.out.println("Num Intersections: "+city.getIntersections().size());
        for (Intersection i : city.getIntersections().values())
        {
            System.out.println("  Intersection: id="+i.getID()
                +", name="+ThievesModelConst.expand(i.getName())
                +", desc="+ThievesModelConst.expand(i.getDescription()));
        }
    }
    
    public static void main(String[] argv)
    {
        DumpCity app = new DumpCity();
        app.run();
    }
}
