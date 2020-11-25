package jo.audio.thieves.logic.gen;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.thieves.data.gen.Apature;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Location;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.audio.thieves.slu.ThievesModelConst;
import jo.util.utils.DebugUtils;
import jo.util.utils.MathUtils;
import jo.util.utils.io.ResourceUtils;
import jo.util.utils.obj.IntegerUtils;

public class HouseLogic
{
    private static final Map<String, JSONObject> mLocationTypeIndex = new HashMap<>();
    private static final Map<String, JSONObject> mApatureTypeIndex = new HashMap<>();
    private static final Map<String, JSONObject> mTemplatesIndex = new HashMap<>();
    private static final List<String> mStreetTemplates = new ArrayList<>();
    private static final List<String> mWarehouseTemplates = new ArrayList<>();
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
        int idx = (int)MathUtils.interpolate(h.getPosh(),  0, 1, 0, mStreetTemplates.size());
        int roll = idx + rnd.nextInt(5) - 2;
        if (roll < 0)
            roll = 0;
        else if (roll >= mStreetTemplates.size())
            roll = mStreetTemplates.size() - 1;
        String id = mStreetTemplates.get(roll);
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
        HouseLogic.populateFromTemplate(h, id, houseDir);
    }
    
    private static void readLocations()
    {
        readLocationsFrom("locationTypes.json");
        Comparator<String> cmp = new Comparator<String>() {            
            @Override
            public int compare(String s1, String s2)
            {
                JSONObject o1 = mTemplatesIndex.get(s1);
                JSONObject o2 = mTemplatesIndex.get(s2);
                int a1 = IntegerUtils.parseInt(o1.get("area"));
                int a2 = IntegerUtils.parseInt(o2.get("area"));
                return a1 - a2;
            }
        };
        mWarehouseTemplates.sort(cmp);
        mStreetTemplates.sort(cmp);
    }
    
    private static void readLocationsFrom(String source)
    {
        DebugUtils.trace("Reading locations from "+source);
        try
        {
            InputStream is = ResourceUtils.loadSystemResourceStream(source, ThievesModelConst.class);
            JSONObject json = (JSONObject)JSONUtils.PARSER.parse(new InputStreamReader(is, "utf-8"));
            is.close();
            String PREFIX = makePrefix(json, source);
            JSONArray items = JSONUtils.getArray(json, "locations");
            if (items != null)
                for (int i = 0; i < items.size(); i++)
                {
                    JSONObject item = (JSONObject)items.get(i);
                    mLocationTypeIndex.put(item.getString("ID"), item);
                }
            items = JSONUtils.getArray(json, "apatures");
            if (items != null)
                for (int i = 0; i < items.size(); i++)
                {
                    JSONObject item = (JSONObject)items.get(i);
                    mApatureTypeIndex.put((String)item.getString("ID"), item);
                }
            JSONObject idMap = JSONUtils.getObject(json, "idMap");
            items = JSONUtils.getArray(json, "templates");
            if (items != null)
                for (int i = 0; i < items.size(); i++)
                {
                    JSONObject item = (JSONObject)items.get(i);
                    if (!item.containsKey("prefix"))
                        item.put("prefix", PREFIX);
                    if (!item.containsKey("idMap"))
                        item.put("idMap", idMap);
                    JSONArray floors = JSONUtils.getArray(item, "floors");
                    JSONArray floor = (JSONArray)floors.get(0);
                    String row = (String)floor.get(0);
                    int area = floors.size()*floor.size()*row.length();
                    item.put("area", area);
                    String id = item.getString("ID");
                    mTemplatesIndex.put(id, item);
                    if (id.toLowerCase().indexOf("ware") >= 0)
                        mWarehouseTemplates.add(id);
                    else
                        mStreetTemplates.add(id);
                }
            JSONArray include = JSONUtils.getArray(json, "include");
            if (include != null)
                for (int i = 0; i < include.size(); i++)
                    readLocationsFrom((String)include.get(i));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private static String makePrefix(JSONObject json, String source)
    {
        String PREFIX = json.getString("prefix");
        if (PREFIX == null)
        {
            PREFIX = source;
            int o = PREFIX.lastIndexOf('/');
            if (o >= 0)
                PREFIX = PREFIX.substring(o + 1);
            o = PREFIX.lastIndexOf('.');
            if (o >= 0)
                PREFIX = PREFIX.substring(0, o);
        }
        if ((PREFIX.length() > 0) && Character.isLetterOrDigit(PREFIX.charAt(PREFIX.length()-1)))
            PREFIX += "$";
        return PREFIX;
    }

    private static Location makeLocation(JSONObject json, House h)
    {
        Location l = new Location();
        l.fromJSON(json);
        l.setID(l.getID()+"$"+h.getLocations().size());
        h.getLocations().put(l.getID(), l);
        return l;
    }

    private static Apature makeApature(JSONObject json, House h)
    {
        Apature a = new Apature();
        a.fromJSON(json);
        a.setID(a.getID()+"$"+h.getApatures().size());
        h.getApatures().put(a.getID(), a);
        return a;
    }
    
    public static void populateFromTemplate(House h, String id, int east)
    {
        JSONObject template = mTemplatesIndex.get(id);
        if (template == null)
            throw new IllegalArgumentException("Unknown template '"+id+"'");
        h.setRaw(template);
        String PREFIX = template.getString("prefix");
        JSONObject idMap = JSONUtils.getObject(template, "idMap");
        JSONArray tFloors = JSONUtils.getArray(template, "floors");
        Location[][][] floors = populateFloors(h, tFloors, PREFIX,
                idMap);
        populateApatures(h, tFloors, floors, PREFIX, idMap, east);
        populateExits(h, east, idMap, tFloors, floors);
    }

    public static void populateExits(House h, int east, JSONObject idMap,
            JSONArray tFloors, Location[][][] floors)
    {
        int west = ThievesConstLogic.opposite(east);
        int north = ThievesConstLogic.left(east);
        int south = ThievesConstLogic.right(east);
        for (int f = 0; f < tFloors.size(); f += 2)
        {
            JSONArray tFloor = (JSONArray)tFloors.get(f);
            for (int r = 0; r < tFloor.size(); r++)
            {
                String tRow = (String)tFloor.get(r);
                if ((r == 0) || (r == tFloor.size() - 1))
                {   // north/south side
                    for (int c = 1; c < tRow.length() - 1; c += 2)
                    {
                        String tApature = tRow.substring(c, c+1);
                        if (idMap.containsKey(tApature))
                            tApature = idMap.getString(tApature);
                        if ("EXIT".equals(tApature))
                            floors[f][r+((r == 0) ? 1 : -1)][c].setApature((r == 0) ? north : south, "$exit");
                    }
                }
                else if (r%2 == 1)
                {   // east/west side
                    String tApature = tRow.substring(0, 1);
                    if (idMap.containsKey(tApature))
                        tApature = idMap.getString(tApature);
                    if ("EXIT".equals(tApature))
                    {
                        floors[f][r][1].setApature(west, "$exit");
                        h.setEntry(floors[f][r][1].getID());
                    }
                    tApature = tRow.substring(tRow.length() - 1);
                    if (idMap.containsKey(tApature))
                        tApature = idMap.getString(tApature);
                    if ("EXIT".equals(tApature))
                        floors[f][r][tRow.length() - 2].setApature(east, "$exit");
                }
            }
        }
    }

    public static void populateApatures(House h, JSONArray tFloors,
            Location[][][] floors, String PREFIX, JSONObject idMap, int east)
    {
        int west = ThievesConstLogic.opposite(east);
        int north = ThievesConstLogic.left(east);
        int south = ThievesConstLogic.right(east);
        for (int f = 0; f < tFloors.size(); f++)
        {
            JSONArray tFloor = (JSONArray)tFloors.get(f);
            if (f%2 == 0)
            {   // horizontal apatures
                for (int r = 1; r < tFloor.size(); r++)
                {
                    String tRow = (String)tFloor.get(r);
                    if (r%2 == 0)
                    {   // north/south apatures
                        for (int c = 1; c < tRow.length() - 1; c += 2)
                            connectApature(idMap, PREFIX, tRow, f, r, c, h, floors, 0, 1, 0,
                                    north, south);
                    }
                    else
                    {   // east/west apatures
                        for (int c = 2; c < tRow.length() - 2; c += 2)
                            connectApature(idMap, PREFIX, tRow, f, r, c, h, floors, 0, 0, 1,
                                    west, east);
                    }
                }
            }
            else
            {   // vertical apatures
                for (int r = 1; r < tFloor.size(); r += 2)
                {
                    String tRow = (String)tFloor.get(r);
                    for (int c = 1; c < tRow.length(); c += 2)
                        connectApature(idMap, PREFIX, tRow, f, r, c, h, floors, 1, 0, 0,
                                ThievesConstLogic.DOWN, ThievesConstLogic.UP);
                }
            }
        }
    }
    
    private static void connectApature(JSONObject idMap, String PREFIX, String tRow, int f, int r, int c, House h,
            Location[][][] floors, int df, int dr, int dc, int minusDir, int plusDir)
    {
        JSONObject typeApature = getApatureType(idMap, PREFIX, tRow, c);
        if (typeApature == null)
            return;
        Apature a = makeApature(typeApature, h);
        Location d = floors[f-df][r-dr][c-dc];
        if (d == null)
        {
            DebugUtils.trace(JSONUtils.toFormattedString(h.getRaw()));
            throw new IllegalArgumentException("Apature at "+f+","+r+","+c+" has no location at "+(f-df)+","+(r-dr)+","+(c-dc));
        }
        Location u = floors[f+df][r+dr][c+dc];
        if (u == null)
        {
            DebugUtils.trace(JSONUtils.toFormattedString(h.getRaw()));
            throw new IllegalArgumentException("Apature at "+f+","+r+","+c+" has no location at "+(f+df)+","+(r+dr)+","+(c+dc));
        }
        a.setLocation(minusDir, d.getID());
        a.setLocation(plusDir, u.getID());
        d.setApature(plusDir, a.getID());
        u.setApature(minusDir, a.getID());
    }

    private static JSONObject getApatureType(JSONObject idMap, String PREFIX, String tRow, int c)
    {
        String tApature = tRow.substring(c, c+1);
        if (idMap.containsKey(tApature))
            tApature = idMap.getString(tApature);
        if (".".equals(tApature))
            return null;
        JSONObject typeApature = mApatureTypeIndex.get(tApature);
        if (typeApature == null)
        {
            typeApature = mLocationTypeIndex.get(PREFIX+tApature);
            if (typeApature == null)
                throw new IllegalArgumentException("Unknown location ID '"+tApature+"' or '"+(PREFIX+tApature)+"'");
        }
        return typeApature;
    }
    
    private static Location[][][] populateFloors(House h,
            JSONArray tFloors, String PREFIX, JSONObject idMap)
    {
        Location[][][] floors = new Location[tFloors.size()][][];
        for (int f = 0; f < tFloors.size(); f += 2)
        {
            JSONArray tFloor = (JSONArray)tFloors.get(f);
            floors[f] = new Location[tFloor.size()][];
            for (int r = 1; r < tFloor.size(); r += 2)
            {
                String tRow = (String)tFloor.get(r);
                floors[f][r] = new Location[tRow.length()];
                for (int c = 1; c < tRow.length(); c += 2)
                {
                    String tLocation = tRow.substring(c, c+1);
                    if (".".equals(tLocation) || " ".equals(tLocation))
                        continue;
                    String idLocation = idMap.getString(tLocation);
                    if (".".equals(idLocation) || " ".equals(idLocation))
                        continue;
                    JSONObject typeLocation = mLocationTypeIndex.get(idLocation);
                    if (typeLocation == null)
                    {
                        typeLocation = mLocationTypeIndex.get(PREFIX+idLocation);
                        if (typeLocation == null)
                            throw new IllegalArgumentException("Unknown location ID '"+tLocation+"' -> '"+idLocation+"' or '"+(PREFIX+idLocation)+"'");
                    }
                    Location l = makeLocation(typeLocation, h);
                    floors[f][r][c] = l;
                }
            }
        }
        return floors;
    }
}
