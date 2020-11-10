package jo.audio.companions.tools.gui.map.logic;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.tools.gui.map.MapData;

public class MapTileLogic
{
    public static final int TILE_WIDTH = 64;
    public static final int TILE_HEIGHT = 48;
    
    private static Map<String, BufferedImage> mMapTiles = new HashMap<String, BufferedImage>();
    
    public static void clearCache()
    {
        mMapTiles.clear();
    }
    
    private static String makeKey(int x, int y)
    {
        return x+","+y;
    }
    
    private static void putInCache(int x, int y, BufferedImage tile)
    {
        mMapTiles.put(makeKey(x, y), tile);
    }
    
    public static BufferedImage getFromCache(int x, int y, MapData data)
    {
        String key = makeKey(x, y);
        if (!mMapTiles.containsKey(key))
        {
            BufferedImage tile = makeTile(x, y, data);
            putInCache(x, y, tile);
        }
        return mMapTiles.get(key);
    }
    
    private static BufferedImage makeTile(int x, int y, MapData data)
    {
        BufferedImage tile = new BufferedImage(TILE_WIDTH*data.getPixelScale(), TILE_HEIGHT*data.getPixelScale(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)tile.getGraphics();
        //data.setBroadcast(false);
        int oldX = data.getX();
        int oldY = data.getY();
        data.setX(x);
        data.setY(y);
        MapDrawLogic.doPaint(g, data, new Dimension(TILE_WIDTH*data.getPixelScale(), TILE_HEIGHT*data.getPixelScale()), 0, 0);
        g.dispose();
        data.setX(oldX);
        data.setY(oldY);
        //data.setBroadcast(true);
        return tile;
    }
    
    public static void paintTiles(Graphics2D g, Rectangle screen, MapData data)
    {
        int scale = data.getPixelScale();
        Rectangle tileBase  = new Rectangle(0, 0, scale*MapTileLogic.TILE_WIDTH, scale*MapTileLogic.TILE_HEIGHT);
        for (int x = 0; x < CompConstLogic.MAX_ENUMA_LOCATION_X; x += MapTileLogic.TILE_WIDTH)
            for (int y = 0; y < CompConstLogic.MAX_ENUMA_LOCATION_Y; y += MapTileLogic.TILE_HEIGHT)
            {
                Rectangle r  = new Rectangle(tileBase);
                r.translate(x*scale, y*scale);
                r.translate(-data.getX()*scale, -data.getY()*scale);
                if (!r.intersects(screen))
                    continue;
                BufferedImage tile = MapTileLogic.getFromCache(x, y, data);
                g.drawImage(tile, r.x, r.y, null);
//                g.setColor(Color.RED);
//                g.draw(r);
//                g.drawLine(r.x, r.y, r.x+r.width, r.y+r.height);
//                g.drawLine(r.x, r.y+r.height, r.x+r.width, r.y);
            }

    }
}
