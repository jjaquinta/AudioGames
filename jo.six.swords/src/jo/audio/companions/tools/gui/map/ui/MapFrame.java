package jo.audio.companions.tools.gui.map.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import jo.audio.companions.tools.gui.map.MapData;
import jo.audio.companions.tools.gui.map.logic.MapTileLogic;

@SuppressWarnings("serial")
public class MapFrame extends JFrame
{
    private MapData mData;
    //private BufferedImage mImage;

    private MapDataPanel mToolbar;
    //private JLabel  mClient;
    //private JScrollPane mScroller;
    private MapCanvas   mClient;
    
    public MapFrame(MapData data)
    {
        super("Enuma Elish Live Mapper");
        mData = data;
        initInstantiate();
        initLayout();
        initLink();
        updateImage();
    }

    private void initInstantiate()
    {
        //mClient = new JLabel();
        mClient = new MapCanvas(mData);
        //mScroller = new JScrollPane(mClient);
        
        mToolbar = new MapDataPanel(mData);
    }

    private void initLayout()
    {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("North", mToolbar);
        //getContentPane().add("Center", mScroller);
        getContentPane().add("Center", mClient);
    }

    private void initLink()
    {
        mToolbar.addPropertyChangeListener(new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateImage();
            }
        });
    }
    
    private void updateImage()
    {
        /*
        Color bg = mClient.getBackground();
        mClient.setBackground(Color.ORANGE);
        mImage = new BufferedImage(mData.getSize().width, mData.getSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)mImage.getGraphics();
        MapDrawLogic.doPaint(g, mData);
        g.dispose();
        mClient.setIcon(new ImageIcon(mImage));
        mClient.setBackground(bg);
        */
        MapTileLogic.clearCache();
        mClient.repaint();
    }
    
    /*
    private void doSave()
    {
        String mOut = "c:\\temp\\romitu_map.png";
        if (!mOut.endsWith(".png"))
            mOut += ".png";
        try
        {
            ImageIO.write(mImage, "PNG", new File(mOut));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    */
}
