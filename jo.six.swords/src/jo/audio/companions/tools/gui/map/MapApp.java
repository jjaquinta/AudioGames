package jo.audio.companions.tools.gui.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.GenerationLogic;

public class MapApp
{
    private String[]    mArgs;
    
    private MapAssets mAssets = new MapAssets();
    
    private MapData mData = new MapData();

    private JFrame     mFrame; 
    private JComponent mCanvas;
    private JCheckBox  mChallenge;
    private JButton    mDimensionUp;
    private JButton    mDimensionDown;
    private JButton    mHome;
    private JButton    mGoto;
    private JTextField mStatusLeft;
    private JTextField mStatusMiddle;
    private JTextField mStatusRight;
    
    public MapApp(String[] args)
    {
        mArgs = args;
    }
    
    @SuppressWarnings("serial")
    public void run()
    {
        parseArgs();
        mFrame = new JFrame("Six Swords Map");
        mCanvas = new JComponent() {
            @Override
            public void paint(Graphics g)
            {
                doPaint(g, getSize());
            }
        };
        MapMouseAdapter listener = new MapMouseAdapter(this, mAssets, mData);
        mCanvas.addMouseListener(listener);
        mCanvas.addMouseMotionListener(listener);
        mCanvas.addMouseWheelListener(listener);
        mChallenge = new JCheckBox("Show Challenge");
        mDimensionUp = new JButton("+");
        mDimensionDown = new JButton("-");
        mHome = new JButton("Home");
        mGoto = new JButton("Goto");
        JPanel toolBar = new JPanel();
        toolBar.setLayout(new GridLayout(1, 5));
        toolBar.add(mChallenge);
        toolBar.add(mDimensionUp);
        toolBar.add(mDimensionDown);
        toolBar.add(mHome);
        toolBar.add(mGoto);
        mStatusLeft = new JTextField();
        mStatusMiddle = new JTextField();
        mStatusRight = new JTextField();
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new GridLayout(1, 3));
        statusBar.add(mStatusLeft);
        statusBar.add(mStatusMiddle);
        statusBar.add(mStatusRight);
        mFrame.getContentPane().setLayout(new BorderLayout());
        mFrame.getContentPane().add("North", toolBar);
        mFrame.getContentPane().add("Center", mCanvas);
        mFrame.getContentPane().add("South", statusBar);
        mFrame.setSize(1024, 768);
        mFrame.setVisible(true);
        mFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
        mChallenge.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mCanvas.repaint();
            }
        });
        mDimensionDown.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (mData.getZ() > 0)
                {
                    mData.setZ(mData.getZ() - 1);
                    mCanvas.repaint();
                }
            }
        });
        mDimensionUp.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (mData.getZ() + 1 < CompConstLogic.INITIAL_DIM_LOCATION_X.length)
                {
                    mData.setZ(mData.getZ() + 1);
                    mCanvas.repaint();
                }
            }
        });
        mHome.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int z = mData.getZ();
                mData.setX(CompConstLogic.INITIAL_DIM_LOCATION_X[z]);
                mData.setY(CompConstLogic.INITIAL_DIM_LOCATION_Y[z]);
                mCanvas.repaint();
            }
        });
        mGoto.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                CoordBean c = new CoordBean();
                c.setX(mData.getX());
                c.setY(mData.getY());
                c.setZ(mData.getZ());
                CoordDlg dlg = new CoordDlg(mFrame, c);
                dlg.setVisible(true);
                mData.setX(c.getX());
                mData.setY(c.getY());
                mData.setZ(c.getZ());
                mCanvas.repaint();
            }
        });
    }
    
    private void parseArgs()
    {
        for (int i = 0; i < mArgs.length; i++)
            if ("-x".equals(mArgs[i]))
                mData.setX(Integer.parseInt(mArgs[++i]));
            else if ("-y".equals(mArgs[i]))
                mData.setY(Integer.parseInt(mArgs[++i]));
    }
    
    private void doPaint(Graphics g, Dimension size)
    {
        mData.setSize(size);
        int ox = ((int)size.getWidth())/2 + mData.getDX();
        int oy = ((int)size.getHeight())/2 + mData.getDY();
        int xRad = ((int)size.getWidth())/mData.getScale()/2 + 1;
        int yRad = ((int)size.getHeight())/mData.getScale()/2 + 1;
        int roadThick = Math.max(1,  mData.getScale()/6);
        int roadOff = roadThick/2;
        for (int x = -xRad; x <= xRad; x++)
            for (int y = -yRad; y <= yRad; y++)
            {
                SquareBean sq = getSquare(mData.getX() + x, mData.getY() + y);
                int px = x*mData.getScale() + ox;
                int py = y*mData.getScale() + oy;
                Color c = mAssets.getColor(sq, mChallenge.isSelected());
                g.setColor(c);
                g.fillRect(px, py, mData.getScale(), mData.getScale());
                paintRivers(g, roadThick, roadOff, sq, px, py);
                paintRoads(g, roadThick, roadOff, sq, px, py);
                if (mData.getScale() >= 24)
                {
                    BufferedImage img = mAssets.getImage(sq);
                    g.drawImage(img, px, py, px+mData.getScale(), py+mData.getScale(), 0, 0, 48, 48, null, null);
                }
//                if (mChallenge.isSelected())
//                {
//                    c = mAssets.getChallengeColor(sq);
//                    if (c != null)
//                    {
//                        g.setColor(c);
//                        for (int i = 0; i < roadOff; i++)
//                            g.drawRect(px+i, py+i, mData.getScale()-2*i, mData.getScale()-2*i);
//                    }
//                }
            }
    }

    public void paintRoads(Graphics g, int roadThick, int roadOff,
            SquareBean sq, int px, int py)
    {
        if (sq.isAnyRoads())
        {
            g.setColor(MapAssets.ROAD1);
            g.fillOval(px+mData.getScale()/2-roadOff, py+mData.getScale()/2-roadOff, roadThick, roadThick);
        }
        if (sq.isRoadNorth())
        {
            setRoadColor(g, sq.getRoadNorth());
            g.fillRect(px+mData.getScale()/2-roadOff, py+0, roadThick, mData.getScale()/2);
        }
        if (sq.isRoadSouth())
        {
            setRoadColor(g, sq.getRoadSouth());
            g.fillRect(px+mData.getScale()/2-roadOff, py+mData.getScale()/2, roadThick, mData.getScale()/2);
        }
        if (sq.isRoadEast())
        {
            setRoadColor(g, sq.getRoadEast());
            g.fillRect(px+mData.getScale()/2, py+mData.getScale()/2-roadOff, mData.getScale()/2, roadThick);
        }
        if (sq.isRoadWest())
        {
            setRoadColor(g, sq.getRoadWest());
            g.fillRect(px+0, py+mData.getScale()/2-roadOff, mData.getScale()/2, roadThick);
        }
    }

    public void paintRivers(Graphics g, int riverThick, int riverOff,
            SquareBean sq, int px, int py)
    {
        if (sq.isAnyRivers())
        {
            g.setColor(MapAssets.ROAD1);
            g.fillOval(px+mData.getScale()/2-riverOff, py+mData.getScale()/2-riverOff, riverThick, riverThick);
        }
        if (sq.isRiverNorth())
        {
            setRiverColor(g, sq.getRiverNorth());
            g.fillRect(px+mData.getScale()/2-riverOff, py+0, riverThick, mData.getScale()/2);
        }
        if (sq.isRiverSouth())
        {
            setRiverColor(g, sq.getRiverSouth());
            g.fillRect(px+mData.getScale()/2-riverOff, py+mData.getScale()/2, riverThick, mData.getScale()/2);
        }
        if (sq.isRiverEast())
        {
            setRiverColor(g, sq.getRiverEast());
            g.fillRect(px+mData.getScale()/2, py+mData.getScale()/2-riverOff, mData.getScale()/2, riverThick);
        }
        if (sq.isRiverWest())
        {
            setRiverColor(g, sq.getRiverWest());
            g.fillRect(px+0, py+mData.getScale()/2-riverOff, mData.getScale()/2, riverThick);
        }
    }

    private void setRoadColor(Graphics g, int road)
    {
        if (road == SquareBean.T_TRACK)
            g.setColor(MapAssets.ROAD1);
        else if (road == SquareBean.T_ROAD)
            g.setColor(MapAssets.ROAD2);
        else if (road == SquareBean.T_HIGHWAY)
            g.setColor(MapAssets.ROAD3);
    }

    private void setRiverColor(Graphics g, int river)
    {
        if (river == SquareBean.R_BROOK)
            g.setColor(MapAssets.RIVER1);
        else if (river == SquareBean.R_STREAM)
            g.setColor(MapAssets.RIVER2);
        else if (river == SquareBean.R_RIVER)
            g.setColor(MapAssets.RIVER3);
    }

    public void repaint()
    {
        mCanvas.repaint();
    }

    private Map<Long, SquareBean> mCache = new HashMap<>();
    
    private SquareBean getSquare(int x, int y)
    {
        long hash = (x + 32768)*65536L + (y + 32768);
        if (mCache.containsKey(hash))
            return mCache.get(hash);
        SquareBean sq = GenerationLogic.getSquare(new CoordBean(x, y, mData.getZ()));
        if (FeatureLogic.isStaticFeature(sq.getOrds(), null))
            sq.setFeature(CompConstLogic.FEATURE_STATIC);
        return sq;
    }
    
    public void updateStatus(CoordBean ord)
    {
        SquareBean sq = GenerationLogic.getSquare(ord);
        StringBuffer left = new StringBuffer();
        StringBuffer leftHover = new StringBuffer();
        StringBuffer middle = new StringBuffer();
        StringBuffer right = new StringBuffer();
        left.append(ord.toString());
        left.append(" ");
        left.append(CompConstLogic.TERRAIN_NAMES[sq.getTerrain()]);
        if (sq.getTerrainDepth() > 0)
            left.append(":"+sq.getTerrainDepth());
        left.append(" Challenge:"+sq.getChallenge());
        if (sq.getSignposts().size() > 0)
            left.append(" "+sq.getSignposts().size()+" signs");
        toLonLat(leftHover, ord);
        RegionBean region = GenerationLogic.getRegion(ord);
        if (sq.getFeature() != CompConstLogic.FEATURE_NONE)
        {
            middle.append(CompConstLogic.FEATURE_NAMES[sq.getFeature()]);
            FeatureBean feature = FeatureLogic.getFeature(region, sq, null);
            middle.append(" ");
            String name = mAssets.expandInserts(feature.getName());
            middle.append(name);
        }
        for (DemenseBean d = sq.getDemense(); d != null; d = d.getLiege())
        {
            right.append(mAssets.expandInserts(d.getName()));
            if (d.getLiege() != null)
                right.append(" - ");
        }
        String msg = mAssets.expandInserts(left.toString());
        mStatusLeft.setText(msg);
        mStatusLeft.setToolTipText(leftHover.toString());
        msg = mAssets.expandInserts(middle.toString());
        mStatusMiddle.setText(msg);
        msg = mAssets.expandInserts(right.toString());
        mStatusRight.setText(msg);
    }
    
    public static void toLonLat(StringBuffer txt, CoordBean loc)
    {
        int lon = loc.getX() - CompConstLogic.INITIAL_DIM_LOCATION_X[loc.getZ()];
        int lat = loc.getY() - CompConstLogic.INITIAL_DIM_LOCATION_Y[loc.getZ()];
        txt.append(Math.abs(lat)/60);
        txt.append("\u00B0");
        txt.append(Math.abs(lat)%60);
        txt.append("\u2032");
        txt.append((lat < 0) ? "N" : "S");
        txt.append(" ");
        txt.append(Math.abs(lon)/60);
        txt.append("\u00B0");
        txt.append(Math.abs(lon)%60);
        txt.append("\u2032");
        txt.append((lon < 0) ? "W" : "E");
    }

    public static void main(String[] args)
    {
        MapApp app = new MapApp(args);
        app.run();
    }
}
