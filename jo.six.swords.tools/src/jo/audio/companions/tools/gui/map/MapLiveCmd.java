package jo.audio.companions.tools.gui.map;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import jo.audio.companions.tools.gui.map.ui.MapFrame;
import jo.util.utils.BeanUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.IntegerUtils;

public class MapLiveCmd
{
    private String[]    mArgs;    
    private MapData mData = new MapData();
    
    public MapLiveCmd(String[] args)
    {
        mArgs = args;
    }
    
    public void run()
    {
        parseArgs();
        loadData();
        MapFrame frame = new MapFrame(mData);
        frame.setSize(1024, 768);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosed(e);
                saveData();
                System.exit(0);
            }
        });
    }
    
    private File PROP_FILE = new File(System.getProperty("user.home"), "jo_dnd\\enuma_map.properties");
    private String[] INT_PROPS = new String[] { "x", "y", "width", "height", "scale" };
    private String[] BOOLEAN_PROPS = new String[] { "drawBorders", "drawRivers", "drawRoads", "featureNames", "countryNames",
            "townBorders", "borderNames", "townNames" };
    
    private void loadData()
    {
        if (!PROP_FILE.exists())
            return;
        Properties p = new Properties();
        try
        {
            FileReader rdr = new FileReader(PROP_FILE);
            p.load(rdr);
            rdr.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        for (String key : INT_PROPS)
            if (p.containsKey(key))
                BeanUtils.set(mData, key, IntegerUtils.parseInt(p.get(key)));
        for (String key : BOOLEAN_PROPS)
            if (p.containsKey(key))
                BeanUtils.set(mData, key, BooleanUtils.parseBoolean(p.get(key)));
    }
    
    private void saveData()
    {
        Properties p = new Properties();
        for (String key : INT_PROPS)
            p.put(key, String.valueOf(BeanUtils.get(mData, key)));
        for (String key : BOOLEAN_PROPS)
            p.put(key, String.valueOf(BeanUtils.get(mData, key)));
        try
        {
            FileWriter wtr = new FileWriter(PROP_FILE);
            p.store(wtr, "Enuma Elish Map Settings");
            wtr.close();
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
            else if ("--noTerrainIcons".equalsIgnoreCase(mArgs[i]))
                mData.setNoTerrainIcons(true);
            else if ("--noRuinIcons".equalsIgnoreCase(mArgs[i]))
                mData.setNoRuinIcons(true);
    }

    public static void main(String[] args)
    {
        MapLiveCmd app = new MapLiveCmd(args);
        app.run();
    }
}
