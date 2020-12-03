package jo.audio.thieves.tools.editor.logic;

import java.util.ArrayList;
import java.util.Collection;
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
import jo.audio.thieves.data.template.PLocationRef;
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
        Map<String,PLocationRef> squares = house.getSquares();
        if (squares.containsKey(k))
        {
            squares.remove(k);
            house.setSquares(squares);
        }
        Map<String,PLocationRef> apatures = house.getApatures();
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
        PLocationRef ref = new PLocationRef();
        ref.setID(tile.getID());
        ref.setX(x);
        ref.setY(y);
        ref.setZ(f);
        if (tile instanceof PSquare)
        {
            Map<String,PLocationRef> squares = house.getSquares();
            squares.put(k, ref);
            house.setSquares(squares);
        }
        if (tile instanceof PApature)
        {
            Map<String,PLocationRef> apatures = house.getApatures();
            apatures.put(k, ref);
            house.setApatures(apatures);
        }
        es.fireMonotonicPropertyChange("location.floor");
        RuntimeLogic.status("Setting floor "+f+", "+x+","+y+" to "+tile.getID());
    }
    private static void updateInfo(PTemplate newHouse, String oldHouse)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        Map<String,PTemplate> templates = es.getLibrary().getTemplates();
        if (oldHouse != null)
            templates.remove(oldHouse);
        templates.put(newHouse.getID(), newHouse);
        es.getLibrary().setTemplates(templates);
        es.fireMonotonicPropertyChange("location.info");
    }
        
    public static void updateName(PTemplate house, String newValue)
    {
        house.setName(newValue);
        updateInfo(house, null);
    }
    public static void updateCategory(PTemplate house, String newValue)
    {
        house.setCategory(newValue);
        updateInfo(house, null);
    }
    public static void updateDescription(PTemplate house, String newValue)
    {
        house.setDescription(newValue);
        updateInfo(house, null);
    }
    public static void updateID(PTemplate house, String newValue)
    {
        String oldValue = house.getID();
        house.setID(newValue);
        updateInfo(house, oldValue);
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
    public static int[][] getBoundary(Collection<String> keys)
    {
        int[][] ret = null;
        for (String key : keys)
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
        Map<String, PLocationRef> squares = house.getSquares();
        for (int z = bounds[0][2]; z <= bounds[1][2]; z += 2)
            for (int x = bounds[0][0] + 1; x < bounds[1][0]; x += 2)
                for (int y = bounds[0][1] + 1; y < bounds[1][1]; y += 2)
                {
                    String k = x+","+y+","+z;
                    PLocationRef thisTile = squares.get(k);
                    if ((thisTile != null) && !thisTile.getID().equals(ROOF) && !thisTile.getID().equals(ROOF_EDGE))
                    {
                        String k1 = x+","+y+","+(z+1);
                        if (!squares.containsKey(k1))
                        {
                            PLocationRef roof = new PLocationRef();
                            roof.setID(ROOF);
                            squares.put(k1, roof);
                        }
                    }
                }
        es.fireMonotonicPropertyChange("location.floor");
    }
    
    public static final int NOTHING = 0;
    public static final int SQUARE = 1;
    public static final int APATURE_HORZ = 2;
    public static final int APATURE_VERT = 3;
    public static final int APATURE_TWEEN = 4;
    
    public static int getType(int x, int y, int z)
    {
        if ((z%2) == 1)
        {   // tween
            if ((x%2 == 1) && (y%2 == 1))
                return APATURE_TWEEN;
            else
                return NOTHING;
        }
        if ((x%2 == 1) && (y%2 == 0))
            return APATURE_HORZ;
        else if ((x%2 == 0) && (y%2 == 1))
            return APATURE_VERT;
        else if ((x%2 == 1) && (y%2 == 1))
            return SQUARE;
        else //if ((x%2 == 0) && (y%2 == 0))
            return NOTHING;
    }
}
