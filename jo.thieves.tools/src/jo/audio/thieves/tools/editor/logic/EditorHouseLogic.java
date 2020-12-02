package jo.audio.thieves.tools.editor.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;
import org.json.simple.parser.ParseException;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.data.template.PLocation;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.logic.RuntimeLogic;
import jo.util.utils.obj.IntegerUtils;

public class EditorHouseLogic
{
    public static PTemplate getHouse(String id)
    {
        PLibrary lib = EditorSettingsLogic.getInstance().getLibrary();
        if (lib == null)
            return null;
        if (lib.getTemplates().size() == 0)
            return null;
        PTemplate template = lib.getTemplates().get(id);
        return template;
    }
    public static boolean isHouse(String name)
    {
        return getHouse(name) != null;
    }
    public static List<PTemplate> getHouses()
    {
        List<PTemplate> names = new ArrayList<>();
        PLibrary location = EditorSettingsLogic.getInstance().getLibrary();
        if (location == null)
            return names;
        names.addAll(location.getTemplates().values());
        Collections.sort(names);
        return names;
    }
    public static void selectHouse(PTemplate newHouse)
    {
        if ((newHouse != null) && !isHouse(newHouse.getID()))
            newHouse = null;
        EditorSettings es = EditorSettingsLogic.getInstance();
        if (newHouse == es.getSelectedHouse())
            return;
        es.setSelectedHouse(newHouse);
    }
    public static void removeTile(int f, int y, int x)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PTemplate house = es.getSelectedHouse();
        String k = x+","+y+","+f;
        Map<String,PSquare> squares = house.getSquares();
        if (squares.containsKey(k))
        {
            squares.remove(k);
            house.setSquares(squares);
        }
        Map<String,PApature> apatures = house.getApatures();
        if (apatures.containsKey(k))
        {
            apatures.remove(k);
            house.setApatures(apatures);
        }
        es.fireMonotonicPropertyChange("location.floor");
        RuntimeLogic.status("Clearing floor "+f+", "+x+","+y);
    }
    public static void setTile(int f, int y, int x, PLocation tile)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PTemplate house = es.getSelectedHouse();
        String k = x+","+y+","+f;
        if (tile instanceof PSquare)
        {
            Map<String,PSquare> squares = house.getSquares();
            squares.put(k, (PSquare)tile);
            house.setSquares(squares);
        }
        if (tile instanceof PApature)
        {
            Map<String,PApature> apatures = house.getApatures();
            apatures.put(k, (PApature)tile);
            house.setApatures(apatures);
        }
        es.fireMonotonicPropertyChange("location.floor");
        RuntimeLogic.status("Setting floor "+f+", "+x+","+y+" to "+tile.getID());
    }
    
    // TODO update default:
    private static String DEFAULT_HOUSE = "{"+
        "\"ID\":\"NEW_HOUSE\","+
        "\"floors\":["+
           "["+
              "\" _ _ \","+
              "\"|   |\","+
              "\"|   |\","+
              "\"$   |\","+
              "\" _ _ \""+
           "]"+
        "]"+
     "}";
    
    public static void addHouse(String id)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLibrary location = es.getLibrary();
        if (location == null)
            return;
        PTemplate house = new PTemplate();
        try
        {
            house.fromJSON((JSONObject)JSONUtils.PARSER.parse(DEFAULT_HOUSE));
        }
        catch (ParseException e)
        {
            RuntimeLogic.error(e);
        }
        Map<String,PTemplate> templates = location.getTemplates();
        house.setID(id);
        templates.put(id, house);
        location.setTemplates(templates);
        es.fireMonotonicPropertyChange("houses");
    }
    
    public static void deleteHouse()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLibrary location = es.getLibrary();
        if (location == null)
            return;
        PTemplate house = es.getSelectedHouse();
        if (house == null)
            return;
        Map<String,PTemplate> templates = location.getTemplates();
        templates.remove(house.getID());
        location.setTemplates(templates);
        es.fireMonotonicPropertyChange("houses");
    }
    public static int[][] getBoundary()
    {
        int[][] ret = null;
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLibrary location = es.getLibrary();
        if (location == null)
            return ret;
        PTemplate house = es.getSelectedHouse();
        if (house == null)
            return ret;
        for (String key : house.getApatures().keySet())
            ret = extendBoundary(key, ret);
        for (String key : house.getSquares().keySet())
            ret = extendBoundary(key, ret);
        return ret;
    }
    private static int[][] extendBoundary(String key, int[][] edges)
    {
        StringTokenizer st = new StringTokenizer(key, ",");
        int x = IntegerUtils.parseInt(st.nextToken());
        int y = IntegerUtils.parseInt(st.nextToken());
        int z = IntegerUtils.parseInt(st.nextToken());
        if (edges == null)
            return new int[][] { { x, y, z }, { x, y, z } };
        edges[0][0] = Math.min(edges[0][0], x);
        edges[0][1] = Math.min(edges[0][1], y);
        edges[0][2] = Math.min(edges[0][2], z);
        edges[1][0] = Math.max(edges[1][0], x);
        edges[1][1] = Math.max(edges[1][1], y);
        edges[1][2] = Math.max(edges[1][2], z);
        return edges;
    }

    private static final String ROOF = "ROOF";
    private static final String ROOF_EDGE = "ROOF_EDGE";
    
    public static void addRoofEdge(int f)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLibrary loc = es.getLibrary();
        if (loc == null)
            return;
        PTemplate house = es.getSelectedHouse();
        if (house == null)
            return;
        int[][] bounds = getBoundary();
        Map<String, PSquare> squares = house.getSquares();
        for (int z = bounds[0][2]; z <= bounds[1][2]; z += 2)
            for (int x = bounds[0][0] + 1; x < bounds[1][0]; x += 2)
                for (int y = bounds[0][1] + 1; y < bounds[1][1]; y += 2)
                {
                    String k = x+","+y+","+z;
                    PSquare thisTile = squares.get(k);
                    if ((thisTile != null) && !thisTile.getID().equals(ROOF) && !thisTile.getID().equals(ROOF_EDGE))
                    {
                        String k1 = x+","+y+","+(z+1);
                        if (!squares.containsKey(k1))
                        {
                            PSquare roof = new PSquare();
                            roof.setID(ROOF);
                            squares.put(k1, roof);
                        }
                    }
                }
        es.fireMonotonicPropertyChange("location.floor");
    }
}
