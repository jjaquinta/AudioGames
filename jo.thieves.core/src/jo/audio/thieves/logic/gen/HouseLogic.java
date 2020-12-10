package jo.audio.thieves.logic.gen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import jo.audio.thieves.data.gen.City;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.logic.LocationLogic;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.audio.thieves.logic.template.LibraryLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.MathUtils;

public class HouseLogic
{
    private static final String LIB_FILE = "resource://jo/audio/thieves/slu/locationLibrary.json";
    private static PLibrary mLibrary;
    private static final List<String> mStreetTemplates = new ArrayList<>();
    private static final List<String> mWarehouseTemplates = new ArrayList<>();
    private static final List<String> mDwarfTemplates = new ArrayList<>();
    private static final List<String> mGuildTemplates = new ArrayList<>();
    static
    {
        HouseLogic.readLocations();
    }
    
    public static void init()
    {
        // force loading of locations
    }

    public static House getHouse(Street street, int houseNumber)
    {
        if ((houseNumber < 1) || (houseNumber > street.getHouses()))
            throw new IllegalArgumentException("No house number #"+houseNumber+" on street "+street.getID()+", 1-"+street.getHouses());
        House h = new House();
        h.setStreet(street.getID());
        h.setSeed(street.getSeed() + houseNumber);
        h.setHouseNumber(houseNumber);
        h.setPosh(MathUtils.interpolate(houseNumber, 1, street.getHouses(), 
                street.getLowIntersection().getPosh(), street.getHighIntersection().getPosh()));
        h.setElevation((int)MathUtils.interpolate(houseNumber, 1, street.getHouses(), 
                street.getLowIntersection().getElevation(), street.getHighIntersection().getElevation()));
        Random rnd = new Random(h.getSeed());
        switch (street.getType())
        {
            case Street.STREET:
            case Street.DWARF:
                generateStreetHouse(rnd, street, h);
                break;
            case Street.QUAY:
                generateQuayHouse(rnd, street, h);
                break;
            default:
                throw new IllegalArgumentException("Unhandled street type '"+street.getType()+"'");
        }
        return h;
    }

    private static void generateStreetHouse(Random rnd, Street street, House h)
    {
        List<String> templates;
        City city = LocationLogic.getCity();
        if ((h.getHouseNumber() == 13) && (city.getNorthGuildStreet().equals(street.getID()) || city.getSouthGuildStreet().equals(street.getID())))
            templates = mGuildTemplates;
        else if (street.getType() == Street.STREET)
            templates = mStreetTemplates;
        else if (street.getType() == Street.DWARF)
            templates = mDwarfTemplates;
        else
            throw new IllegalStateException("Unknown street type: "+street.getType());
        int idx = (int)MathUtils.interpolate(h.getPosh(),  0, 1, 0, templates.size());
        int roll = idx + rnd.nextInt(5) - 2;
        if (roll < 0)
            roll = 0;
        else if (roll >= templates.size())
            roll = templates.size() - 1;
        String id = templates.get(roll);
        generateFromTemplate(street, h, id);
    }

    private static void generateQuayHouse(Random rnd, Street street, House h)
    {
        int roll = rnd.nextInt(mWarehouseTemplates.size());
        String id = mWarehouseTemplates.get(roll);
        generateFromTemplate(street, h, id);
    }

    public static void generateFromTemplate(Street street, House h, String id)
    {
        DebugUtils.trace("Generating from template "+id);
        int streetDir = street.getHighDir();
        int houseDir;
        if (h.getHouseNumber()%2 == 0)
            houseDir = ThievesConstLogic.right(streetDir);
        else
            houseDir = ThievesConstLogic.left(streetDir);
        if (houseDir%2 == 1)
            houseDir--;
        h.setHouseDir(houseDir);
        PTemplate template = mLibrary.getTemplates().get(id);
        if (template == null)
            throw new IllegalArgumentException("Unknown template '"+id+"'");
        h.setTemplate(template);
    }
    
    private static void readLocations()
    {
        try
        {
            mLibrary = LibraryLogic.read(LIB_FILE);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Cannot read "+LIB_FILE, e);
        }
        postProcessLibrary();
        Comparator<String> cmp = new Comparator<String>() {            
            @Override
            public int compare(String s1, String s2)
            {
                PTemplate o1 = mLibrary.getTemplates().get(s1);
                PTemplate o2 = mLibrary.getTemplates().get(s2);
                int a1 = o1.getNumSquares();
                int a2 = o2.getNumSquares();
                return a1 - a2;
            }
        };
        mWarehouseTemplates.sort(cmp);
        mStreetTemplates.sort(cmp);
        mDwarfTemplates.sort(cmp);
    }
    
    private static void postProcessLibrary()
    {
        for (PTemplate house : mLibrary.getTemplates().values())
        {
            int numSquares = 0;
            int numApatures = 0;
            for (PLocationRef l : house.getLocations().values())
            {
                int type = l.getType();
                if (type == PTemplate.SQUARE)
                    numSquares++;
                else
                {
                    numApatures++;
                    if (l.getID().equals("EXIT"))
                    {
                        PLocationRef e = null;
                        if (type == PTemplate.APATURE_HORZ)
                        {
                            e = house.getLocation(l, ThievesConstLogic.SOUTH);
                            if (e == null)
                                e = house.getLocation(l, ThievesConstLogic.NORTH);
                        }
                        else if (type == PTemplate.APATURE_VERT)
                        {
                            e = house.getLocation(l, ThievesConstLogic.EAST);
                            if (e == null)
                                e = house.getLocation(l, ThievesConstLogic.WEST);
                        }
                        else if (type == PTemplate.APATURE_TWEEN)
                        {
                            e = house.getLocation(l, ThievesConstLogic.UP);
                            if (e == null)
                                e = house.getLocation(l, ThievesConstLogic.DOWN);
                        }
                        if (e != null)
                            house.setEntry(e);
                    }
                }
            }
            house.setNumSquares(numSquares);
            house.setNumApatures(numApatures);
            String cat = house.getCategory();
            if (cat.equals(ThievesConstLogic.CAT_WAREHOUSE))
                mWarehouseTemplates.add(house.getID());
            else if (cat.equals(ThievesConstLogic.CAT_DWARF))
                mDwarfTemplates.add(house.getID());
            else if (cat.equals(ThievesConstLogic.CAT_GUILD))
                mGuildTemplates.add(house.getID());
            else
                mStreetTemplates.add(house.getID());
            if (house.getEntry() == null)
                throw new IllegalArgumentException("No entry for "+house.getID());
        }
    }
    
    public static PSquare getSquare(String id)
    {
        return mLibrary.getSquares().get(id);
    }
    
    public static PApature getApature(String id)
    {
        return mLibrary.getApatures().get(id);
    }
}
