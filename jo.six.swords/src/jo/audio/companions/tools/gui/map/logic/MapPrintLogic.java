package jo.audio.companions.tools.gui.map.logic;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import jo.audio.companions.tools.gui.map.MapData;

public class MapPrintLogic
{
    public static void printCountry(MapData data, String name, int[] bounds)
    {
        int sw = bounds[2] - bounds[0] + 1;
        int sh = bounds[3] - bounds[1] + 1;
        int pw = sw*data.getPixelScale();
        int ph = sh*data.getPixelScale();
        BufferedImage img = new BufferedImage(pw, ph, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)img.getGraphics();
        int ox = data.getX();
        int oy = data.getY();
        data.setSuspendNotifications(true);
        data.setX(bounds[0] + sw/2);
        data.setY(bounds[1] + sh/2);
        MapDrawLogic.doPaint(g, data, new Dimension(pw, ph), 0, 0);
        data.setX(ox);
        data.setY(oy);
        data.setSuspendNotifications(false);
        g.dispose();
        File f = new File("c:\\temp", name+".png");
        try
        {
            ImageIO.write(img, "PNG", f);
        }
        catch (IOException e)
        {
        }
        System.out.println("Written country map "+pw+"x"+ph+" to "+f);
    }
    
    public static void printTiles(MapData data, File dir, String prefix, int tilesWide, int tilesHigh, 
            int startX, int startY, int scale, int pixelsWide, int pixelsHigh)
    {
        int oX = startX;
        int oY = startY;
        int squaresPerTileWide = pixelsWide/scale;
        int squaresPerTileHigh = pixelsHigh/scale;
        int zX = oX - (int)(squaresPerTileWide/2.0*tilesWide); 
        int zY = oY - (int)(squaresPerTileHigh/2.0*tilesHigh);
        data.setSuspendNotifications(true);
        int oldScale = data.getScale();
        data.setScale(scale);
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
                    System.out.println("Printing "+f.getName());
                    ImageIO.write(tile, "PNG", f);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        data.setX(oX);
        data.setY(oY);
        data.setScale(oldScale);
        data.setSuspendNotifications(false);
        System.out.println("Done.");
    }
}
