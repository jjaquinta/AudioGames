package jo.audio.thieves.tools.editor.logic;

import java.awt.Color;
import java.util.Map;

import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.data.PLocation;
import jo.audio.thieves.tools.editor.data.PTile;
import jo.util.utils.obj.StringUtils;

public class EditorTileLogic
{
    public static void select(PTile tile)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.setSelectedTile(tile);
    }
    public static void updateColor(PTile tile, Color c)
    {
        tile.setColor(c);
        EditorSettingsLogic.getInstance().fireMonotonicPropertyChange("location.tile");
    }
    public static void updateChar(PTile tile, String newVal)
    {
        String oldVal = tile.getChar();
        if (StringUtils.equals(oldVal, newVal))
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        tile.setChar(newVal);
        Map<String, PTile> idMap = es.getSelectedLocation().getIDMap();
        for (String key : idMap.keySet().toArray(new String[0]))
            if (idMap.get(key) == tile)
                idMap.remove(key);
        idMap.put(newVal, tile);
        es.fireMonotonicPropertyChange("location.tile");
    }
    public static void updateID(PTile tile, String newID)
    {
        String oldID = tile.getID();
        if (oldID.equals(newID))
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        tile.setID(newID);
        if (es.getSelectedLocation().getLocations().containsKey(oldID))
        {
            es.getSelectedLocation().getLocations().remove(oldID);
            es.getSelectedLocation().getLocations().put(newID, tile);
        }
        if (es.getSelectedLocation().getApatures().containsKey(oldID))
        {
            es.getSelectedLocation().getApatures().remove(oldID);
            es.getSelectedLocation().getApatures().put(newID, tile);
        }
        es.fireMonotonicPropertyChange("location.tile");
    }
    public static void updateName(PTile tile, String newVal)
    {
        String oldVal = tile.getName();
        if (oldVal.equals(newVal))
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        tile.setName(newVal);
        es.fireMonotonicPropertyChange("location.tile");
    }
    public static void updateDesc(PTile tile, String newVal)
    {
        String oldVal = tile.getDescription();
        if (oldVal.equals(newVal))
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        tile.setDescription(newVal);
        es.fireMonotonicPropertyChange("location.tile");
    }
    public static void updateClimbWalls(PTile tile, int newVal)
    {
        int oldVal = tile.getClimbWallsMod();
        if (oldVal == newVal)
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        tile.setClimbWallsMod(newVal);
        es.fireMonotonicPropertyChange("location.tile");
    }
    public static void updateFindTraps(PTile tile, int newVal)
    {
        int oldVal = tile.getFindTrapsMod();
        if (oldVal == newVal)
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        tile.setFindTrapsMod(newVal);
        es.fireMonotonicPropertyChange("location.tile");
    }
    public static void updateHideInShadows(PTile tile, int newVal)
    {
        int oldVal = tile.getHideInShadowsMod();
        if (oldVal == newVal)
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        tile.setHideInShadowsMod(newVal);
        es.fireMonotonicPropertyChange("location.tile");
    }
    public static void updateMoveSilently(PTile tile, int newVal)
    {
        int oldVal = tile.getMoveSilentlyMod();
        if (oldVal == newVal)
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        tile.setMoveSilentlyMod(newVal);
        es.fireMonotonicPropertyChange("location.tile");
    }
    public static void updateOpenLocks(PTile tile, int newVal)
    {
        int oldVal = tile.getOpenLocksMod();
        if (oldVal == newVal)
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        tile.setOpenLocksMod(newVal);
        es.fireMonotonicPropertyChange("location.tile");
    }
    public static void updateInside(PTile tile, boolean newVal)
    {
        boolean oldVal = tile.isInside();
        if (oldVal == newVal)
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        tile.setInside(newVal);
        es.fireMonotonicPropertyChange("location.tile");
    }
    public static void updateBedroom(PTile tile, boolean newVal)
    {
        boolean oldVal = tile.isBedroom();
        if (oldVal == newVal)
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        tile.setBedroom(newVal);
        es.fireMonotonicPropertyChange("location.tile");
    }
    public static PTile newTile(String newTileID, int type)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLocation loc = es.getSelectedLocation();
        if (loc == null)
            return null;
        String ch = newTileID.substring(0, 1);
        if (loc.getIDMap().containsKey(ch))
        {
            for (char c = ' '; c <= '~'; c++)
                if (loc.getIDMap().containsKey(String.valueOf(c)))
                {
                    ch = String.valueOf(c);
                    break;
                }
        }
        PTile tile = new PTile();
        tile.setID(newTileID);
        tile.setChar(ch);
        tile.setName("New Tile");
        tile.setDescription("New Description");
        tile.setType(type);
        if (type == PTile.LOCATION)
            loc.getLocations().put(tile.getID(), tile);
        else
            loc.getApatures().put(tile.getID(), tile);
        loc.getIDMap().put(tile.getChar(), tile);
        es.fireMonotonicPropertyChange("location.tile");
        return tile;
    }
}
