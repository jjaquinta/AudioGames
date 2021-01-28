package jo.audio.companions.tools.gui.map.logic;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.tools.gui.map.MapData;

public class ExportObjLogic
{
    public static void exportObj(MapData data, File objFile)
    {
        for (int x = 0; x < CompConstLogic.MAX_ENUMA_LOCATION_X; x += MapTileLogic.TILE_WIDTH)
            for (int y = 0; y < CompConstLogic.MAX_ENUMA_LOCATION_Y; y += MapTileLogic.TILE_HEIGHT)
            {
                BufferedImage tile = MapTileLogic.getFromCache(x, y, data);
            }

    }
}
