package jo.audio.companions.tools.gui.map.logic;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import jo.audio.companions.tools.gui.map.MapData;

public class MapPrintLogic
{
    public static void printTiles(MapData data, File dir, String prefix, int tilesWide, int tilesHigh, int pixelsWide, int pixelsHigh)
    {
        int oX = data.getX();
        int oY = data.getY();
        int squaresPerTileWide = pixelsWide/data.getScale();
        int squaresPerTileHigh = pixelsHigh/data.getScale();
        int zX = oX - (int)(squaresPerTileWide/2.0*tilesWide); 
        int zY = oY - (int)(squaresPerTileHigh/2.0*tilesHigh);
        for (int tx = 0; tx < tilesWide; tx++)
            for (int ty = 0; ty < tilesHigh; ty++)
            {
                data.setX(zX + tx*squaresPerTileWide + squaresPerTileWide/2);
                data.setY(zY + ty*squaresPerTileHigh + squaresPerTileHigh/2);
                BufferedImage tile = new BufferedImage(pixelsWide, pixelsHigh, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D g = (Graphics2D)tile.getGraphics();
                MapTileLogic.paintTiles(g, new Rectangle(0, 0, pixelsWide, pixelsHigh), data);
                File f = new File(dir, prefix+"_"+tx+"_"+ty+".png");
                try
                {
                    ImageIO.write(tile, "PNG", f);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        data.setX(oX);
        data.setY(oY);
    }
}
