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
import jo.util.utils.obj.StringUtils;

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
    public static void selectCategory(String newCategory)
    {
        if (StringUtils.isTrivial(newCategory))
            newCategory = null;
        EditorSettings es = EditorSettingsLogic.getInstance();
        if (StringUtils.equals(newCategory, es.getSelectedCategory()))
            return;
        es.setSelectedCategory(newCategory);
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
    
    private static final int[][] ORTH_DELTA = {
            { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }
    };

    public static void insertSquare(int f, int y, int x, PSquare square)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PTemplate house = es.getSelectedHouse();
        Map<String,PLocationRef> apatures = house.getApatures();
        Map<String,PLocationRef> squares = house.getSquares();
        Map<String,PSquare> squareIndex = es.getLibrary().getSquares();
        Map<String,PApature> apatureIndex = es.getLibrary().getApatures();
        setTile(f, y, x, square);
        for (int i = 0; i < ORTH_DELTA.length; i++)
        {
            // check apature
            String akey = (x+ORTH_DELTA[i][0])+","+(y+ORTH_DELTA[i][1])+","+f;
            if (apatures.containsKey(akey))
                continue; // already there
            // check square
            String skey = (x+ORTH_DELTA[i][0]*2)+","+(y+ORTH_DELTA[i][1]*2)+","+f;
            PLocationRef s2 = squares.get(skey);
            if (s2 == null)
                continue;
            PSquare square2 = squareIndex.get(s2.getID());
            if (square.getInside() == square2.getInside())
                setTile(x+ORTH_DELTA[i][0], y+ORTH_DELTA[i][1], f, apatureIndex.get("EMPTY"));
        }
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
    private static String DEFAULT_HOUSE = "{"
            + "  \"ID\":\"SHOTGUN_SINGLE_PEN\","
            + "  \"apatures\":{"
            + "      \"1,2,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":1,"
            + "          \"y\":2,"
            + "          \"z\":0"
            + "        },"
            + "      \"1,3,1\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":1,"
            + "          \"y\":3,"
            + "          \"z\":1"
            + "        },"
            + "      \"1,4,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":1,"
            + "          \"y\":4,"
            + "          \"z\":0"
            + "        },"
            + "      \"1,4,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":1,"
            + "          \"y\":4,"
            + "          \"z\":2"
            + "        },"
            + "      \"1,5,1\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":1,"
            + "          \"y\":5,"
            + "          \"z\":1"
            + "        },"
            + "      \"1,6,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":1,"
            + "          \"y\":6,"
            + "          \"z\":0"
            + "        },"
            + "      \"2,1,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":2,"
            + "          \"y\":1,"
            + "          \"z\":0"
            + "        },"
            + "      \"2,3,0\":{"
            + "          \"ID\":\"OUTER_DOOR\","
            + "          \"x\":2,"
            + "          \"y\":3,"
            + "          \"z\":0"
            + "        },"
            + "      \"2,3,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":2,"
            + "          \"y\":3,"
            + "          \"z\":2"
            + "        },"
            + "      \"2,5,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":2,"
            + "          \"y\":5,"
            + "          \"z\":2"
            + "        },"
            + "      \"2,7,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":2,"
            + "          \"y\":7,"
            + "          \"z\":0"
            + "        },"
            + "      \"3,1,1\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":3,"
            + "          \"y\":1,"
            + "          \"z\":1"
            + "        },"
            + "      \"3,2,0\":{"
            + "          \"ID\":\"WINDOW\","
            + "          \"x\":3,"
            + "          \"y\":2,"
            + "          \"z\":0"
            + "        },"
            + "      \"3,2,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":3,"
            + "          \"y\":2,"
            + "          \"z\":2"
            + "        },"
            + "      \"3,4,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":3,"
            + "          \"y\":4,"
            + "          \"z\":0"
            + "        },"
            + "      \"3,4,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":3,"
            + "          \"y\":4,"
            + "          \"z\":2"
            + "        },"
            + "      \"3,6,0\":{"
            + "          \"ID\":\"WINDOW\","
            + "          \"x\":3,"
            + "          \"y\":6,"
            + "          \"z\":0"
            + "        },"
            + "      \"3,6,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":3,"
            + "          \"y\":6,"
            + "          \"z\":2"
            + "        },"
            + "      \"3,7,1\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":3,"
            + "          \"y\":7,"
            + "          \"z\":1"
            + "        },"
            + "      \"4,1,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":4,"
            + "          \"y\":1,"
            + "          \"z\":0"
            + "        },"
            + "      \"4,1,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":4,"
            + "          \"y\":1,"
            + "          \"z\":2"
            + "        },"
            + "      \"4,3,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":4,"
            + "          \"y\":3,"
            + "          \"z\":0"
            + "        },"
            + "      \"4,3,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":4,"
            + "          \"y\":3,"
            + "          \"z\":2"
            + "        },"
            + "      \"4,5,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":4,"
            + "          \"y\":5,"
            + "          \"z\":0"
            + "        },"
            + "      \"4,5,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":4,"
            + "          \"y\":5,"
            + "          \"z\":2"
            + "        },"
            + "      \"4,7,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":4,"
            + "          \"y\":7,"
            + "          \"z\":0"
            + "        },"
            + "      \"4,7,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":4,"
            + "          \"y\":7,"
            + "          \"z\":2"
            + "        },"
            + "      \"5,1,1\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":5,"
            + "          \"y\":1,"
            + "          \"z\":1"
            + "        },"
            + "      \"5,2,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":5,"
            + "          \"y\":2,"
            + "          \"z\":2"
            + "        },"
            + "      \"5,4,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":5,"
            + "          \"y\":4,"
            + "          \"z\":0"
            + "        },"
            + "      \"5,4,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":5,"
            + "          \"y\":4,"
            + "          \"z\":2"
            + "        },"
            + "      \"5,6,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":5,"
            + "          \"y\":6,"
            + "          \"z\":2"
            + "        },"
            + "      \"5,7,1\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":5,"
            + "          \"y\":7,"
            + "          \"z\":1"
            + "        },"
            + "      \"6,1,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":6,"
            + "          \"y\":1,"
            + "          \"z\":0"
            + "        },"
            + "      \"6,3,0\":{"
            + "          \"ID\":\"WINDOW\","
            + "          \"x\":6,"
            + "          \"y\":3,"
            + "          \"z\":0"
            + "        },"
            + "      \"6,3,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":6,"
            + "          \"y\":3,"
            + "          \"z\":2"
            + "        },"
            + "      \"6,5,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":6,"
            + "          \"y\":5,"
            + "          \"z\":2"
            + "        },"
            + "      \"6,7,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":6,"
            + "          \"y\":7,"
            + "          \"z\":0"
            + "        },"
            + "      \"7,2,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":7,"
            + "          \"y\":2,"
            + "          \"z\":0"
            + "        },"
            + "      \"7,3,1\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":7,"
            + "          \"y\":3,"
            + "          \"z\":1"
            + "        },"
            + "      \"7,4,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":7,"
            + "          \"y\":4,"
            + "          \"z\":0"
            + "        },"
            + "      \"7,4,2\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":7,"
            + "          \"y\":4,"
            + "          \"z\":2"
            + "        },"
            + "      \"7,5,1\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":7,"
            + "          \"y\":5,"
            + "          \"z\":1"
            + "        },"
            + "      \"7,6,0\":{"
            + "          \"ID\":\"EMPTY\","
            + "          \"x\":7,"
            + "          \"y\":6,"
            + "          \"z\":0"
            + "        }"
            + "    },"
            + "  \"category\":\"shotgun\","
            + "  \"description\":\"A bright, shiny, new house.\","
            + "  \"name\":\"REPLACE_THIS\","
            + "  \"squares\":{"
            + "      \"1,1,0\":{"
            + "          \"ID\":\"FRONT_YARD\","
            + "          \"x\":1,"
            + "          \"y\":1,"
            + "          \"z\":0"
            + "        },"
            + "      \"1,3,0\":{"
            + "          \"ID\":\"FRONT_YARD\","
            + "          \"x\":1,"
            + "          \"y\":3,"
            + "          \"z\":0"
            + "        },"
            + "      \"1,3,2\":{"
            + "          \"ID\":\"ROOF_EDGE\","
            + "          \"x\":1,"
            + "          \"y\":3,"
            + "          \"z\":2"
            + "        },"
            + "      \"1,5,0\":{"
            + "          \"ID\":\"FRONT_YARD\","
            + "          \"x\":1,"
            + "          \"y\":5,"
            + "          \"z\":0"
            + "        },"
            + "      \"1,5,2\":{"
            + "          \"ID\":\"ROOF_EDGE\","
            + "          \"x\":1,"
            + "          \"y\":5,"
            + "          \"z\":2"
            + "        },"
            + "      \"1,7,0\":{"
            + "          \"ID\":\"FRONT_YARD\","
            + "          \"x\":1,"
            + "          \"y\":7,"
            + "          \"z\":0"
            + "        },"
            + "      \"3,1,0\":{"
            + "          \"ID\":\"SIDE_YARD\","
            + "          \"x\":3,"
            + "          \"y\":1,"
            + "          \"z\":0"
            + "        },"
            + "      \"3,1,2\":{"
            + "          \"ID\":\"ROOF_EDGE\","
            + "          \"x\":3,"
            + "          \"y\":1,"
            + "          \"z\":2"
            + "        },"
            + "      \"3,3,0\":{"
            + "          \"ID\":\"LIVING_ROOM\","
            + "          \"x\":3,"
            + "          \"y\":3,"
            + "          \"z\":0"
            + "        },"
            + "      \"3,3,2\":{"
            + "          \"ID\":\"ROOF\","
            + "          \"x\":3,"
            + "          \"y\":3,"
            + "          \"z\":2"
            + "        },"
            + "      \"3,5,0\":{"
            + "          \"ID\":\"BED_ROOM\","
            + "          \"x\":3,"
            + "          \"y\":5,"
            + "          \"z\":0"
            + "        },"
            + "      \"3,5,2\":{"
            + "          \"ID\":\"ROOF\","
            + "          \"x\":3,"
            + "          \"y\":5,"
            + "          \"z\":2"
            + "        },"
            + "      \"3,7,0\":{"
            + "          \"ID\":\"SIDE_YARD\","
            + "          \"x\":3,"
            + "          \"y\":7,"
            + "          \"z\":0"
            + "        },"
            + "      \"3,7,2\":{"
            + "          \"ID\":\"ROOF_EDGE\","
            + "          \"x\":3,"
            + "          \"y\":7,"
            + "          \"z\":2"
            + "        },"
            + "      \"5,1,0\":{"
            + "          \"ID\":\"SIDE_YARD\","
            + "          \"x\":5,"
            + "          \"y\":1,"
            + "          \"z\":0"
            + "        },"
            + "      \"5,1,2\":{"
            + "          \"ID\":\"ROOF_EDGE\","
            + "          \"x\":5,"
            + "          \"y\":1,"
            + "          \"z\":2"
            + "        },"
            + "      \"5,3,0\":{"
            + "          \"ID\":\"KITCHEN\","
            + "          \"x\":5,"
            + "          \"y\":3,"
            + "          \"z\":0"
            + "        },"
            + "      \"5,3,2\":{"
            + "          \"ID\":\"ROOF\","
            + "          \"x\":5,"
            + "          \"y\":3,"
            + "          \"z\":2"
            + "        },"
            + "      \"5,5,0\":{"
            + "          \"ID\":\"BATHROOM\","
            + "          \"x\":5,"
            + "          \"y\":5,"
            + "          \"z\":0"
            + "        },"
            + "      \"5,5,2\":{"
            + "          \"ID\":\"ROOF\","
            + "          \"x\":5,"
            + "          \"y\":5,"
            + "          \"z\":2"
            + "        },"
            + "      \"5,7,0\":{"
            + "          \"ID\":\"SIDE_YARD\","
            + "          \"x\":5,"
            + "          \"y\":7,"
            + "          \"z\":0"
            + "        },"
            + "      \"5,7,2\":{"
            + "          \"ID\":\"ROOF_EDGE\","
            + "          \"x\":5,"
            + "          \"y\":7,"
            + "          \"z\":2"
            + "        },"
            + "      \"7,1,0\":{"
            + "          \"ID\":\"BACK_YARD\","
            + "          \"x\":7,"
            + "          \"y\":1,"
            + "          \"z\":0"
            + "        },"
            + "      \"7,3,0\":{"
            + "          \"ID\":\"BACK_YARD\","
            + "          \"x\":7,"
            + "          \"y\":3,"
            + "          \"z\":0"
            + "        },"
            + "      \"7,3,2\":{"
            + "          \"ID\":\"ROOF_EDGE\","
            + "          \"x\":7,"
            + "          \"y\":3,"
            + "          \"z\":2"
            + "        },"
            + "      \"7,5,0\":{"
            + "          \"ID\":\"BACK_YARD\","
            + "          \"x\":7,"
            + "          \"y\":5,"
            + "          \"z\":0"
            + "        },"
            + "      \"7,5,2\":{"
            + "          \"ID\":\"ROOF_EDGE\","
            + "          \"x\":7,"
            + "          \"y\":5,"
            + "          \"z\":2"
            + "        },"
            + "      \"7,7,0\":{"
            + "          \"ID\":\"BACK_YARD\","
            + "          \"x\":7,"
            + "          \"y\":7,"
            + "          \"z\":0"
            + "        }"
            + "    }"
            + "}"
            ;
    
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
        es.setLibrary(location);
        es.setSelectedCategory(house.getCategory());
        selectHouse(house);
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
}
