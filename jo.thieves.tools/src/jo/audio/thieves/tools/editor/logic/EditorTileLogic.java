package jo.audio.thieves.tools.editor.logic;

import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;

import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.data.TApature;
import jo.audio.thieves.tools.editor.data.TLocation;
import jo.audio.thieves.tools.editor.data.TLocations;
import jo.util.utils.obj.StringUtils;

public class EditorTileLogic
{
    public static void select(TLocation tile)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.setSelectedTile(tile);
    }
    public static void select(TApature tile)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.setSelectedApature(tile);
    }
    public static void updateColor(TLocation tile, Color c)
    {
        String ch = LocationsLogic.getChar(tile);
        EditorSettingsLogic.getInstance().getSelectedLocation().getColorMap().put(ch, c);
        EditorSettingsLogic.getInstance().fireMonotonicPropertyChange("location.tile");
    }
    public static void updateChar(TLocation tile, String newVal)
    {
        String oldVal = LocationsLogic.getChar(tile);
        if (StringUtils.equals(oldVal, newVal))
            return;
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.getSelectedLocation().getIDMap().remove(oldVal);
        es.getSelectedLocation().getIDMap().put(newVal, tile);
        es.fireMonotonicPropertyChange("location.tile");
    }
    private static void  updateTile(TLocation tile, Consumer<TLocation> action)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        List<TLocation> tiles = es.getSelectedLocation().getLocations();
        for (TLocation t : tiles)
            if (t.getID().equals(tile.getID()))
            {
                action.accept(tile);
                break;
            }
        es.getSelectedLocation().setLocations(tiles);
        es.fireMonotonicPropertyChange("location.tile");
    }
    public static void updateID(TLocation tile, String newID)
    {
        if (tile.getID().equals(newID))
            return;
        updateTile(tile, (t) -> t.setID(newID));
    }
    public static void updateName(TLocation tile, String newVal)
    {
        if (tile.getName().equals(newVal))
            return;
        updateTile(tile, (t) -> t.setName(newVal));
    }
    public static void updateDesc(TLocation tile, String newVal)
    {
        if (tile.getDescription().equals(newVal))
            return;
        updateTile(tile, (t) -> t.setDescription(newVal));
    }
    public static void updateClimbWalls(TLocation tile, int newVal)
    {
        if (tile.getClimbWallsMod() == newVal)
            return;
        updateTile(tile, (t) -> t.setClimbWallsMod(newVal));
    }
    public static void updateFindTraps(TLocation tile, int newVal)
    {
        if (tile.getFindTrapsMod() == newVal)
            return;
        updateTile(tile, (t) -> t.setFindTrapsMod(newVal));
    }
    public static void updateHideInShadows(TLocation tile, int newVal)
    {
        if (tile.getHideInShadowsMod() == newVal)
            return;
        updateTile(tile, (t) -> t.setHideInShadowsMod(newVal));
    }
    public static void updateMoveSilently(TLocation tile, int newVal)
    {
        if (tile.getMoveSilentlyMod() == newVal)
            return;
        updateTile(tile, (t) -> t.setMoveSilentlyMod(newVal));
    }
    public static void updateOpenLocks(TLocation tile, int newVal)
    {
        if (tile.getOpenLocksMod() == newVal)
            return;
        updateTile(tile, (t) -> t.setOpenLocksMod(newVal));
    }
    public static void updateInside(TLocation tile, boolean newVal)
    {
        if (tile.getInside() == newVal)
            return;
        updateTile(tile, (t) -> t.setInside(newVal));
    }
    public static void updateBedroom(TLocation tile, boolean newVal)
    {
        if (tile.getBedroom() == newVal)
            return;
        updateTile(tile, (t) -> t.setBedroom(newVal));
    }
    public static TLocation newTile(String newTileID)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        TLocations loc = es.getSelectedLocation();
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
        TLocation tile = new TLocation();
        tile.setID(newTileID);
        tile.setName("New Tile");
        tile.setDescription("New Description");
        loc.getLocations().add(tile);
        loc.getIDMap().put(ch, tile);
        es.fireMonotonicPropertyChange("location.tile");
        return tile;
    }
    public static TApature newApature(String newTileID)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        TLocations loc = es.getSelectedLocation();
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
        TApature tile = new TApature();
        tile.setID(newTileID);
        tile.setName("New Tile");
        tile.setDescription("New Description");
        loc.getApatures().add(tile);
        loc.getIDMap().put(ch, tile);
        es.fireMonotonicPropertyChange("location.tile");
        return tile;
    }
}
