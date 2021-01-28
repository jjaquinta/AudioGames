package jo.audio.companions.tools.gui.map;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import jo.audio.companions.tools.gui.map.logic.MapDrawLogic;

public class MapCmd
{
    private String[]    mArgs;    
    private MapData mData = new MapData();
    private String mOut = "c:\\temp\\romitu_map.png";
    
    public MapCmd(String[] args)
    {
        mArgs = args;
    }
    
    public void run()
    {
        parseArgs();
        BufferedImage img = new BufferedImage(mData.getSize().width, mData.getSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)img.getGraphics();
        MapDrawLogic.doPaint(g, mData);
        g.dispose();
        if (!mOut.endsWith(".png"))
            mOut += ".png";
        try
        {
            ImageIO.write(img, "PNG", new File(mOut));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void parseArgs()
    {
        mData.setSize(new Dimension(1024, 768));
        for (int i = 0; i < mArgs.length; i++)
            if ("-x".equals(mArgs[i]))
                mData.setX(Integer.parseInt(mArgs[++i]));
            else if ("-y".equals(mArgs[i]))
                mData.setY(Integer.parseInt(mArgs[++i]));
            else if ("-z".equals(mArgs[i]))
                mData.setZ(Integer.parseInt(mArgs[++i]));
            else if ("-s".equals(mArgs[i]))
                mData.setScale(Integer.parseInt(mArgs[++i]));
            else if ("-w".equals(mArgs[i]))
                mData.setSize(new Dimension(Integer.parseInt(mArgs[++i]), mData.getSize().height));
            else if ("-h".equals(mArgs[i]))
                mData.setSize(new Dimension(mData.getSize().width, Integer.parseInt(mArgs[++i])));
            else if ("-o".equals(mArgs[i]))
                mOut = mArgs[++i];
            else if ("--noTerrainIcons".equalsIgnoreCase(mArgs[i]))
                mData.setNoTerrainIcons(true);
            else if ("--noRuinIcons".equalsIgnoreCase(mArgs[i]))
                mData.setNoRuinIcons(true);
    }

    public static void main(String[] args)
    {
        MapCmd app = new MapCmd(args);
        app.run();
    }
}
