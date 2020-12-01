package jo.audio.thieves.tools.editor.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;
import org.json.simple.parser.ParseException;

import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.data.TLocations;
import jo.audio.thieves.tools.editor.data.TTemplate;
import jo.audio.thieves.tools.logic.RuntimeLogic;
import jo.util.utils.MapUtils;

public class EditorHouseLogic
{
    public static TTemplate getHouse(String name)
    {
        TLocations location = EditorSettingsLogic.getInstance().getSelectedLocation();
        if (location == null)
            return null;
        if (location.getTemplates().size() == 0)
            return null;
        for (TTemplate template : location.getTemplates())
        {
            if (name.equals(template.getID()))
                return template;
        }
        return null;
    }
    public static boolean isHouse(String name)
    {
        return getHouse(name) != null;
    }
    public static List<TTemplate> getHouses()
    {
        List<TTemplate> names = new ArrayList<>();
        TLocations location = EditorSettingsLogic.getInstance().getSelectedLocation();
        if (location == null)
            return names;
        names.addAll(location.getTemplates());
        Collections.sort(names);
        return names;
    }
    public static void selectHouse(TTemplate newHouse)
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
        TTemplate house = es.getSelectedHouse();
        house.getFloors()[f][y][x] = '.';
        es.fireMonotonicPropertyChange("location.floor");
        RuntimeLogic.status("Clearing floor "+f+", "+x+","+y);
    }
    public static void setTile(int f, int y, int x, char ch)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        TTemplate house = es.getSelectedHouse();
        if (house.getFloors()[f][y][x] == ch)
            return;
        house.getFloors()[f][y][x] = ch;
        es.fireMonotonicPropertyChange("location.floor");
        RuntimeLogic.status("Setting floor "+f+", "+x+","+y+" to "+ch);
    }
    
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
        TLocations location = es.getSelectedLocation();
        if (location == null)
            return;
        TTemplate house = new TTemplate();
        try
        {
            house.fromJSON((JSONObject)JSONUtils.PARSER.parse(DEFAULT_HOUSE));
        }
        catch (ParseException e)
        {
            RuntimeLogic.error(e);
        }
        house.setID(id);
        location.getTemplates().add(house);
        es.fireMonotonicPropertyChange("houses");
    }
    
    public static void deleteHouse()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        TLocations location = es.getSelectedLocation();
        if (location == null)
            return;
        TTemplate house = es.getSelectedHouse();
        if (house == null)
            return;
        location.getTemplates().remove(house);
        es.fireMonotonicPropertyChange("houses");
    }
    public static void setWidth(int newWidthInSquares)
    {
        if (newWidthInSquares <= 0)
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        TTemplate house = es.getSelectedHouse();
        if (house == null)
            return;
        int newWidth = newWidthInSquares*2 + 1;
        int oldWidth = house.getFloors()[0][0].length;
        for (int f = 0; f < house.getFloors().length; f++)
        {
            char[][] floor = house.getFloors()[f];
            for (int y = 0; y < floor.length; y++)
            {
                char[] oldRow = floor[y];
                char[] newRow = new char[newWidth];
                System.arraycopy(oldRow, 0, newRow, 0, Math.min(oldWidth, newWidth));
                for (int x = Math.min(oldWidth, newWidth); x < newWidth; x++)
                    newRow[x] = ' ';
                floor[y] = newRow;
            }
        }
        es.fireMonotonicPropertyChange("location.floor");
    }
    public static void setHeight(int newHeightInSquares)
    {
        if (newHeightInSquares <= 0)
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        TTemplate house = es.getSelectedHouse();
        if (house == null)
            return;
        int newHeight = newHeightInSquares*2 + 1;
        int oldHeight = house.getFloors()[0].length;
        int oldWidth = house.getFloors()[0][0].length;
        for (int f = 0; f < house.getFloors().length; f++)
        {
            char[][] oldFloor = house.getFloors()[f];
            char[][] newFloor = new char[newHeight][];
            System.arraycopy(oldFloor, 0, newFloor, 0, Math.min(oldHeight, newHeight));
            for (int y = Math.min(oldHeight, newHeight); y < newHeight; y++)
            {
                newFloor[y] = new char[oldWidth];
                for (int x = 0; x < oldWidth; x++)
                    newFloor[y][x] = ' ';
            }
            house.getFloors()[f] = newFloor;
        }
        es.fireMonotonicPropertyChange("location.floor");
    }
    
    public static void removeFloor()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        TTemplate house = es.getSelectedHouse();
        if (house == null)
            return;
        char[][][] oldFloors = house.getFloors();
        if (oldFloors.length == 1)
            return;
        char[][][] newFloors = new char[oldFloors.length - 2][][];
        System.arraycopy(oldFloors, 0, newFloors, 0, newFloors.length);
        house.setFloors(newFloors);
        es.fireMonotonicPropertyChange("location.floor");
    }
    
    public static void addFloor()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        TTemplate house = es.getSelectedHouse();
        if (house == null)
            return;
        char[][][] oldFloors = house.getFloors();
        int height = oldFloors[0].length;
        int width = oldFloors[0][0].length;
        char[][][] newFloors = new char[oldFloors.length + 2][][];
        System.arraycopy(oldFloors, 0, newFloors, 0, oldFloors.length);
        for (int f = oldFloors.length; f < newFloors.length; f++)
        {
            newFloors[f] = new char[height][];
            for (int y = 0; y < height; y++)
            {
                newFloors[f][y] = new char[width];
                for (int x = 0; x < width; x++)
                    newFloors[f][y][x] = ' ';
            }
        }
        house.setFloors(newFloors);
        es.fireMonotonicPropertyChange("location.floor");
        
    }
    public static void addBorder(int f)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        TTemplate house = es.getSelectedHouse();
        if (house == null)
            return;
        char[][] floor = house.getFloors()[f];
        int w = floor[0].length;
        int h = floor.length;
        for (int x = 1; x < w; x += 2)
        {
            if (floor[0][x] != '$')
                floor[0][x] = '_';
            if (floor[h-1][x] != '$')
                floor[h-1][x] = '_';
        }
        for (int y = 1; y < h; y += 2)
        {
            if (floor[y][0] != '$')
                floor[y][0] = '_';
            if (floor[y][w-1] != '$')
                floor[y][w-1] = '_';
        }
        es.fireMonotonicPropertyChange("location.floor");
    }
    public static void addRoofEdge(int f)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        TLocations loc = es.getSelectedLocation();
        if (loc == null)
            return;
        TTemplate house = es.getSelectedHouse();
        if (house == null)
            return;
        char[][][] floors = house.getFloors();
        char[][] floor = floors[f];
        int w = floor[0].length;
        int h = floor.length;
        char roof = ((String)MapUtils.getKey(loc.getIDMap(), "ROOF")).charAt(0);
        char roofEdge = ((String)MapUtils.getKey(loc.getIDMap(), "ROOF_EDGE")).charAt(0);
        char empty = ' ';
        char solid = '_';
        for (int x = 1; x < w; x += 2)
            for (int y = 1; y < h; y += 2)
            {
                if (isTile(floors, f, y, x, roof))
                {
                    setIfTile(floors, f-1, y, x, empty, solid);
                    if (setIfTile(floors, f, y, x - 2, empty, roofEdge))
                    {
                        setIfTile(floors, f, y, x - 3, empty, solid);
                    }
                    if (setIfTile(floors, f, y, x + 2, empty, roofEdge))
                    {
                        setIfTile(floors, f, y, x + 3, empty, solid);
                    }
                    if (setIfTile(floors, f, y - 2, x, empty, roofEdge))
                    {
                        setIfTile(floors, f, y - 3, x, empty, solid);
                    }
                    if (setIfTile(floors, f, y + 2, x, empty, roofEdge))
                    {
                        setIfTile(floors, f, y + 3, x, empty, solid);
                    }
                }
            }
        es.fireMonotonicPropertyChange("location.floor");
    }
    public static void cleanup(int f)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        TLocations loc = es.getSelectedLocation();
        if (loc == null)
            return;
        TTemplate house = es.getSelectedHouse();
        if (house == null)
            return;
        char[][][] floors = house.getFloors();
        char[][] floor = floors[f];
        int w = floor[0].length;
        int h = floor.length;
        char empty = ' ';
        char solid = '_';
        for (int x = 1; x < w; x += 2)
            for (int y = 1; y < h; y += 2)
            {
                if (isTile(floors, f, y, x, empty))
                {
                    setIfTile(floors, f, y, x - 1, empty, solid);
                    setIfTile(floors, f, y, x + 1, empty, solid);
                    setIfTile(floors, f, y - 1, x, empty, solid);
                    setIfTile(floors, f, y + 1, x, empty, solid);
                    setIfTile(floors, f - 1, y, x, empty, solid);
                    setIfTile(floors, f + 1, y, x, empty, solid);
                }
            }
        es.fireMonotonicPropertyChange("location.floor");
    }
    
    private static boolean isTile(char[][][] floors, int f, int y, int x, char testTile)
    {
        try
        {
            return floors[f][y][x] == testTile;
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return false;
        }
    }
    
    private static boolean setIfTile(char[][][] floors, int f, int y, int x, char testTile, char setTile)
    {
        try
        {
            if (floors[f][y][x] == testTile)
            {
                floors[f][y][x] = setTile;
                return true;
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
        }
        return false;
    }
}
