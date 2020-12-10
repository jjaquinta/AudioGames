package jo.audio.thieves.tools.editor.logic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.logic.RuntimeLogic;

public class EditorSquareLogic
{
    public static List<PSquare> getSquares()
    {
        List<PSquare> names = new ArrayList<>();
        PLibrary location = EditorSettingsLogic.getInstance().getLibrary();
        if (location == null)
            return names;
        names.addAll(location.getSquares().values());
        Collections.sort(names);
        return names;
    }
    public static void select(PSquare tile)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.setSelectedSquare(tile);
        RuntimeLogic.status("Selected "+tile.toString());
    }
    private static void updateSquare(PSquare tile)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        Map<String,PSquare> squares = es.getLibrary().getSquares();
        squares.put(tile.getID(), tile);
        es.getLibrary().fireMonotonicPropertyChange("squares");
        EditorSettingsLogic.getInstance().fireMonotonicPropertyChange("location.square");
    }
    public static void updateColor(PSquare tile, Color c)
    {
        tile.setColor("#"+Integer.toHexString(c.getRGB()));
        updateSquare(tile);
    }
    public static void updateID(PSquare tile, String newID)
    {
        if (tile.getID().equals(newID))
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        Map<String,PSquare> squares = es.getLibrary().getSquares();
        squares.remove(tile.getID(), tile);
        tile.setID(newID);
        squares.put(tile.getID(), tile);
        es.getLibrary().fireMonotonicPropertyChange("squares");
        EditorSettingsLogic.getInstance().fireMonotonicPropertyChange("location.square");
    }
    public static void updateName(PSquare tile, String newVal)
    {
        tile.setName(newVal);
        updateSquare(tile);
    }
    public static void updateDesc(PSquare tile, String newVal)
    {
        if (tile.getDescription().equals(newVal))
            return;
        tile.setDescription(newVal);
        updateSquare(tile);
    }
    public static void updateClimbWalls(PSquare tile, int newVal)
    {
        if (tile.getClimbWallsMod() == newVal)
            return;
        tile.setClimbWallsMod(newVal);
        updateSquare(tile);
    }
    public static void updateFindTraps(PSquare tile, int newVal)
    {
        if (tile.getFindTrapsMod() == newVal)
            return;
        tile.setFindTrapsMod(newVal);
        updateSquare(tile);
    }
    public static void updateHideInShadows(PSquare tile, int newVal)
    {
        if (tile.getHideInShadowsMod() == newVal)
            return;
        tile.setHideInShadowsMod(newVal);
        updateSquare(tile);
    }
    public static void updateMoveSilently(PSquare tile, int newVal)
    {
        if (tile.getMoveSilentlyMod() == newVal)
            return;
        tile.setMoveSilentlyMod(newVal);
        updateSquare(tile);
    }
    public static void updateOpenLocks(PSquare tile, int newVal)
    {
        if (tile.getOpenLocksMod() == newVal)
            return;
        tile.setOpenLocksMod(newVal);
        updateSquare(tile);
    }
    public static void updateInside(PSquare tile, boolean newVal)
    {
        if (tile.getInside() == newVal)
            return;
        tile.setInside(newVal);
        updateSquare(tile);
    }
    public static void updateBedroom(PSquare tile, boolean newVal)
    {
        if (tile.getBedroom() == newVal)
            return;
        tile.setBedroom(newVal);
        updateSquare(tile);
    }
    public static PSquare newSquare(String newTileID)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLibrary lib = es.getLibrary();
        if (lib == null)
            return null;
        Map<String,PSquare> squares = lib.getSquares();
        PSquare tile = new PSquare();
        tile.setID(newTileID);
        tile.setName("New Tile");
        tile.setDescription("New Description");
        tile.setColor("#808080");
        squares.put(tile.getID(), tile);
        lib.fireMonotonicPropertyChange("squares");
        es.fireMonotonicPropertyChange("location.squares");
        return tile;
    }
    public static void deleteSquare(PSquare selected)
    {
        // TODO Auto-generated method stub
        
    }
}
