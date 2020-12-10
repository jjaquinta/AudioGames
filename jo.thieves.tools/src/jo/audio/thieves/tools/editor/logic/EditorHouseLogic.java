package jo.audio.thieves.tools.editor.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;
import org.json.simple.parser.ParseException;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.data.template.PLocation;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.audio.thieves.logic.template.LibraryLogic;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.logic.RuntimeLogic;
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
        String k = x+","+y+","+f;
        removeTile(k);
    }
    public static void removeTile(String k)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PTemplate house = es.getSelectedHouse();
        Map<String,PLocationRef> locations = house.getLocations();
        if (locations.containsKey(k))
        {
            locations.remove(k);
            house.fireMonotonicPropertyChange("locations");
        }
        es.fireMonotonicPropertyChange("location.floor");
        RuntimeLogic.status("Clearing floor "+k);
    }
    public static void toggleTileTag(String k, String tag)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PTemplate house = es.getSelectedHouse();
        PLocationRef ref = house.getLocations().get(k);
        if (ref == null)
            throw new IllegalStateException();
        if (ref.getTags().contains(tag))
            ref.getTags().remove(tag);
        else
            ref.getTags().add(tag);
        house.fireMonotonicPropertyChange("locations");
        es.fireMonotonicPropertyChange("location.floor");
        RuntimeLogic.status("Clearing floor "+k);
    }
    
    private static final int[][] ORTH_DELTA = {
            { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }
    };

    public static void insertSquare(int f, int y, int x, PSquare square)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PTemplate house = es.getSelectedHouse();
        Map<String,PLocationRef> locations = house.getLocations();
        Map<String,PSquare> squareIndex = es.getLibrary().getSquares();
        Map<String,PApature> apatureIndex = es.getLibrary().getApatures();
        setTile(f, y, x, square);
        for (int i = 0; i < ORTH_DELTA.length; i++)
        {
            // check apature
            String akey = (x+ORTH_DELTA[i][0])+","+(y+ORTH_DELTA[i][1])+","+f;
            if (locations.containsKey(akey))
                continue; // already there
            // check square
            String skey = (x+ORTH_DELTA[i][0]*2)+","+(y+ORTH_DELTA[i][1]*2)+","+f;
            PLocationRef s2 = locations.get(skey);
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
        PLocationRef ref = new PLocationRef();
        ref.setID(tile.getID());
        ref.setX(x);
        ref.setY(y);
        ref.setZ(f);
        Map<String,PLocationRef> locations = house.getLocations();
        locations.put(x+","+y+","+f, ref);
        house.fireMonotonicPropertyChange("locations");
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
        es.getLibrary().fireMonotonicPropertyChange("templates");
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
            + "  \"category\":\"shotgun\","
            + "  \"description\":\"A bright, shiny, new house.\","
            + "  \"name\":\"REPLACE_THIS\","
            + "  \"locations\":{"
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
    
    private static void newHouse(String id, JSONObject clone)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLibrary location = es.getLibrary();
        if (location == null)
            return;
        PTemplate house = new PTemplate();
        house.fromJSON(clone);
        Map<String,PTemplate> templates = location.getTemplates();
        house.setID(id);
        templates.put(id, house);
        location.fireMonotonicPropertyChange("templates");
        es.setLibrary(location);
        es.setSelectedCategory(house.getCategory());
        selectHouse(house);
    }
    
    public static void addHouse(String id)
    {
        try
        {
            JSONObject clone = (JSONObject)JSONUtils.PARSER.parse(DEFAULT_HOUSE);
            newHouse(id, clone);
        }
        catch (ParseException e)
        {
            RuntimeLogic.error(e);
        }
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
        location.fireMonotonicPropertyChange("templates");
        es.fireMonotonicPropertyChange("houses");
    }
    public static int[][] getBoundary()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        return LibraryLogic.getBoundary(es.getSelectedHouse());
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
        Map<String, PLocationRef> squares = house.getLocations();
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
    public static void cleanupHouse()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PTemplate house = es.getSelectedHouse();
        cleanNothings(house);
        cleanBadRoof(es, house);
        cleanSquares(house);
        cleanGoodRoof(es, house);
        cleanGoodEdge(es, house);
        cleanApatures(es, house);
        es.fireMonotonicPropertyChange("location.floor");
    }
    private static void cleanApatures(EditorSettings es, PTemplate house)
    {
        for (String key : house.getLocations().keySet().toArray(new String[0]))
        {
            PLocationRef loc = house.getLocations().get(key);
            if (loc.isApature())
            {
                PLocationRef[] neighbors = house.getNeighbors(loc);
                if (loc.getType() == PTemplate.APATURE_TWEEN)
                {
                    if ((neighbors[0] == null) || (neighbors[1] == null))
                        house.getLocations().remove(key);
                }
                else
                {
                    if ((neighbors[0] == null) && (neighbors[1] == null))
                        house.getLocations().remove(key); // borders on nothing
                    if ("EMPTY".equals(loc.getID()) && (neighbors[0] != null) && (neighbors[1] != null))
                    {
                        PSquare n1 = es.getLibrary().getSquares().get(neighbors[0].getID());
                        PSquare n2 = es.getLibrary().getSquares().get(neighbors[1].getID());
                        if (n1.getInside() != n2.getInside())
                            house.getLocations().remove(key); // need wall between inside and outside
                    }
                }
            }
            else if (isRoof(loc))
            {
                for (int dir = 0; dir < 8; dir += 2)
                {
                    int rx = loc.getX() + ThievesConstLogic.ORTHOGONAL_DELTAS[dir][0]*2;
                    int ry = loc.getY() + ThievesConstLogic.ORTHOGONAL_DELTAS[dir][1]*2;
                    int rz = loc.getZ() + ThievesConstLogic.ORTHOGONAL_DELTAS[dir][2]*2;
                    PLocationRef neigh = house.getLocation(rx, ry, rz);
                    if (isRoof(neigh))
                    {
                        int tx = loc.getX() + ThievesConstLogic.ORTHOGONAL_DELTAS[dir][0]*1;
                        int ty = loc.getY() + ThievesConstLogic.ORTHOGONAL_DELTAS[dir][1]*1;
                        int tz = loc.getZ() + ThievesConstLogic.ORTHOGONAL_DELTAS[dir][2]*1;
                        PLocationRef tween = house.getLocation(tx, ty, tz);
                        if (tween == null)
                            house.putLocation(new PLocationRef("EMPTY", tx, ty, tz));
                    }
                }
            }
        }
    }
    private static boolean isRoof(PLocationRef loc)
    {
        if (loc == null)
            return false;
        return ROOF.equals(loc.getID()) || ROOF_EDGE.equals(loc.getID());
    }
    private static void cleanSquares(PTemplate house)
    {
        for (String key : house.getLocations().keySet().toArray(new String[0]))
        {
            PLocationRef loc = house.getLocations().get(key);
            if (loc.isSquare())
            {
                if ("EMPTY".equals(loc.getID()))
                    house.getLocations().remove(key);
            }
        }
    }
    private static void cleanBadRoof(EditorSettings es, PTemplate house)
    {
        for (String key : house.getLocations().keySet().toArray(new String[0]))
        {
            PLocationRef loc = house.getLocations().get(key);
            if (loc.isSquare())
            {
                if (ROOF.equals(loc.getID()))
                {
                    PLocationRef under = house.getLocation(loc, ThievesConstLogic.DOWN, 2);
                    if (under == null) // roof over nothing
                        house.getLocations().remove(key);
                    else
                    {
                        PSquare s = es.getLibrary().getSquares().get(under.getID());
                        if (!s.getInside())
                            house.getLocations().remove(key); // roof over outside
                    }
                }
            }
        }
    }
    private static void cleanGoodRoof(EditorSettings es, PTemplate house)
    {
        for (String key : house.getLocations().keySet().toArray(new String[0]))
        {
            PLocationRef loc = house.getLocations().get(key);
            if (loc.isSquare())
            {
                PSquare s = es.getLibrary().getSquares().get(loc.getID());
                if (s.getInside())
                {
                    PLocationRef over = house.getLocation(loc, ThievesConstLogic.UP, 2);
                    if (over == null)
                    {
                        PLocationRef roof = new PLocationRef(ROOF, loc.getX(), loc.getY(), loc.getZ() + 2);
                        house.putLocation(roof);
                    }
                }
            }
        }
    }
    private static void cleanGoodEdge(EditorSettings es, PTemplate house)
    {
        System.out.println("Locations: "+house.getLocations().toString());
        for (String key : house.getLocations().keySet().toArray(new String[0]))
        {
            PLocationRef loc = house.getLocations().get(key);
            if (!key.equals(loc.toKey()))
                System.out.println("!!! "+key+" <> "+loc);
        }
        for (PLocationRef loc : house.getLocations().values().toArray(new PLocationRef[0]))
        {
            if (loc.isSquare() && ROOF.equals(loc.getID()))
                for (int dir = 0; dir < 8; dir += 2)
                {
                    int ex = loc.getX() + ThievesConstLogic.ORTHOGONAL_DELTAS[dir][0]*2; 
                    int ey = loc.getY() + ThievesConstLogic.ORTHOGONAL_DELTAS[dir][1]*2; 
                    int ez = loc.getZ() + ThievesConstLogic.ORTHOGONAL_DELTAS[dir][2]*2;
                    PLocationRef next = house.getLocation(ex, ey, ez);
                    if (next == null) // needs roof edge
                    {
                        PLocationRef edge = new PLocationRef(ROOF_EDGE, ex, ey, ez);
                        house.putLocation(edge);
                    }
                }
        }
    }
    private static void cleanNothings(PTemplate house)
    {
        for (String key : house.getLocations().keySet().toArray(new String[0]))
        {
            PLocationRef loc = house.getLocations().get(key);
            if (PTemplate.getType(loc) == PTemplate.NOTHING)
                house.getLocations().remove(key);
        }
    }
    public static void duplicateHouse(String id)
    {
        PTemplate clone = EditorSettingsLogic.getInstance().getSelectedHouse();
        if (clone != null)
            newHouse(id, clone.toJSON());
    }
}
