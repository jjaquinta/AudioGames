package jo.audio.companions.tools.gui.map.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.tools.gui.map.MapData;
import jo.audio.companions.tools.gui.map.logic.BorderDrawLogic;
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
    private JLabel      mStatus;
    
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
        mStatus = new JLabel();
    }

    private void initLayout()
    {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("North", mToolbar);
        //getContentPane().add("Center", mScroller);
        getContentPane().add("Center", mClient);
        getContentPane().add("South", mStatus);
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
        mData.addPropertyChangeListener("hover", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateStatus();
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
    
    private void updateStatus()
    {
        SquareBean sq = mData.getHover();
        if (sq == null)
        {
            mStatus.setText("");
            return;
        }
        String txt = sq.getOrds().toString();
        txt += " " + (int)sq.getAltitude();
        txt += " " + CompConstLogic.TERRAIN_NAMES[sq.getTerrain()];
        if (sq.getFeature() > 0)
            txt += " " + CompConstLogic.FEATURE_NAMES[sq.getFeature()];
        for (DemenseBean d = sq.getDemense(); d != null; d = d.getLiege())
            txt += " / "+BorderDrawLogic.findName(d);
        if (sq.isAnyRivers())
        {
            txt += " R";
            if (sq.isRiverNorth())
                txt += "\u2191";
            if (sq.isRiverSouth())
                txt += "\u2193";
            if (sq.isRiverWest())
                txt += "\u2190";
            if (sq.isRiverEast())
                txt += "\u2192";
        }
        mStatus.setText(txt);
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
