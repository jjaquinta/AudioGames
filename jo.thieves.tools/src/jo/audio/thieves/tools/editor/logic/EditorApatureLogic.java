package jo.audio.thieves.tools.editor.logic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.tools.editor.data.EditorSettings;

public class EditorApatureLogic
{
    public static List<PApature> getApatures()
    {
        List<PApature> names = new ArrayList<>();
        PLibrary location = EditorSettingsLogic.getInstance().getLibrary();
        if (location == null)
            return names;
        names.addAll(location.getApatures().values());
        Collections.sort(names);
        return names;
    }
    public static void select(PApature tile)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.setSelectedApature(tile);
    }
    private static void updateApature(PApature tile)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        Map<String,PApature> squares = es.getLibrary().getApatures();
        squares.put(tile.getID(), tile);
        es.getLibrary().fireMonotonicPropertyChange("squares");
        EditorSettingsLogic.getInstance().fireMonotonicPropertyChange("location.apature");
    }
    public static void updateColor(PApature tile, Color c)
    {
        tile.setColor("#"+Integer.toHexString(c.getRGB()));
        updateApature(tile);
    }
    public static void updateID(PApature tile, String newID)
    {
        if (tile.getID().equals(newID))
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        Map<String,PApature> squares = es.getLibrary().getApatures();
        squares.remove(tile.getID(), tile);
        tile.setID(newID);
        squares.put(tile.getID(), tile);
        es.getLibrary().fireMonotonicPropertyChange("squares");
        EditorSettingsLogic.getInstance().fireMonotonicPropertyChange("location.apature");
    }
    public static void updateName(PApature tile, String newVal)
    {
        tile.setName(newVal);
        updateApature(tile);
    }
    public static void updateDesc(PApature tile, String newVal)
    {
        if (tile.getDescription().equals(newVal))
            return;
        tile.setDescription(newVal);
        updateApature(tile);
    }
    public static void updateTransition(PApature tile, String newVal)
    {
        if (tile.getTransition().equals(newVal))
            return;
        tile.setTransition(newVal);
        updateApature(tile);
    }
    public static void updateClimbWalls(PApature tile, int newVal)
    {
        if (tile.getClimbWallsMod() == newVal)
            return;
        tile.setClimbWallsMod(newVal);
        updateApature(tile);
    }
    public static void updateFindTraps(PApature tile, int newVal)
    {
        if (tile.getFindTrapsMod() == newVal)
            return;
        tile.setFindTrapsMod(newVal);
        updateApature(tile);
    }
    public static void updateHideInShadows(PApature tile, int newVal)
    {
        if (tile.getHideInShadowsMod() == newVal)
            return;
        tile.setHideInShadowsMod(newVal);
        updateApature(tile);
    }
    public static void updateMoveSilently(PApature tile, int newVal)
    {
        if (tile.getMoveSilentlyMod() == newVal)
            return;
        tile.setMoveSilentlyMod(newVal);
        updateApature(tile);
    }
    public static void updateOpenLocks(PApature tile, int newVal)
    {
        if (tile.getOpenLocksMod() == newVal)
            return;
        tile.setOpenLocksMod(newVal);
        updateApature(tile);
    }
    public static void updateLockable(PApature tile, boolean newVal)
    {
        if (tile.getLockable() == newVal)
            return;
        tile.setLockable(newVal);
        updateApature(tile);
    }
    public static void updateOpenable(PApature tile, boolean newVal)
    {
        if (tile.getOpenable() == newVal)
            return;
        tile.setOpenable(newVal);
        updateApature(tile);
    }
    public static void updateTransparent(PApature tile, boolean newVal)
    {
        if (tile.getTransparent() == newVal)
            return;
        tile.setTransparent(newVal);
        updateApature(tile);
    }
    public static PApature newApature(String newTileID)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLibrary lib = es.getLibrary();
        if (lib == null)
            return null;
        Map<String,PApature> apatures = lib.getApatures();
        PApature tile = new PApature();
        tile.setID(newTileID);
        tile.setName("New Apature");
        tile.setDescription("New Description");
        tile.setColor("#808080");
        apatures.put(tile.getID(), tile);
        lib.fireMonotonicPropertyChange("apatures");
        es.fireMonotonicPropertyChange("location.apature");
        return tile;
    }
    public static void deleteApature(PApature selected)
    {
        // TODO Auto-generated method stub
        
    }
}
