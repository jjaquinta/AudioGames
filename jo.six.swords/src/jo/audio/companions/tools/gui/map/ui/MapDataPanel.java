package jo.audio.companions.tools.gui.map.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.tools.gui.map.MapData;
import jo.audio.companions.tools.gui.map.logic.MapPrintLogic;
import jo.util.utils.BeanUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.IntegerUtils;

@SuppressWarnings("serial")
public class MapDataPanel extends JPanel
{
    private MapData mData;
    private PropertyChangeSupport mPCS;

    private JSpinner    mOX;
    private JSpinner    mOY;
    private JSpinner    mWidth;
    private JSpinner    mHeight;
    private JSpinner    mScale;
    private JCheckBox   mDrawRoads;
    private JCheckBox   mDrawRivers;
    private JCheckBox   mDrawBorders;
    private JCheckBox   mFeatureNames;
    private JCheckBox   mTownNames;
    private JCheckBox   mCountryNames;
    private JCheckBox   mTownBorders;
    private JCheckBox   mBorderNames;
    private JButton     mNorth;
    private JButton     mSouth;
    private JButton     mEast;
    private JButton     mWest;
    private JButton     mPrint;
    
    public MapDataPanel(MapData data)
    {
        mData = data;
        mPCS = new PropertyChangeSupport(mData);
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mOX = bindSpinner("x", 0, CompConstLogic.MAX_ENUMA_LOCATION_X, 32);
        mOY = bindSpinner("y", 0, CompConstLogic.MAX_ENUMA_LOCATION_Y, 32);
        mWidth = bindSpinner("width", 0, 2048, 32);
        mHeight = bindSpinner("height", 0, 2048, 32);
        mScale = bindSpinner("scale", -4, 64, 1);
        mDrawRoads = bindCheck("drawRoads", "Roads");
        mDrawRivers = bindCheck("drawRivers", "Rivers");
        mDrawBorders = bindCheck("drawBorders", "Countries");
        mTownBorders = bindCheck("drawTownBorders", "Towns");
        mNorth = bindIncrement("y", "^", new Supplier<Integer>() {            
            @Override
            public Integer get()
            {
                return - mData.getHeight()/4/mData.getPixelScale();
            }
        });
        mSouth = bindIncrement("y", "v", new Supplier<Integer>() {            
            @Override
            public Integer get()
            {
                return mData.getHeight()/4/mData.getPixelScale();
            }
        });
        mEast = bindIncrement("x", ">", new Supplier<Integer>() {            
            @Override
            public Integer get()
            {
                return mData.getWidth()/4/mData.getPixelScale();
            }
        });
        mWest = bindIncrement("x", "<", new Supplier<Integer>() {            
            @Override
            public Integer get()
            {
                return - mData.getWidth()/4/mData.getPixelScale();
            }
        });
        mTownNames = bindCheck("townNames", "Towns");
        mFeatureNames = bindCheck("featureNames", "Forts");
        mCountryNames = bindCheck("countryNames", "Countries");
        mBorderNames = bindCheck("borderNames", "Borders");
        mPrint = new JButton("Print");
    }
    
    private void initLayout()
    {
        JPanel row1 = new JPanel();
        row1.setLayout(new FlowLayout());
        row1.add(new JLabel("Center:"));
        row1.add(mOX);
        row1.add(mOY);
        row1.add(mWest);
        row1.add(mNorth);
        row1.add(mSouth);
        row1.add(mEast);
        row1.add(new JLabel("Size:"));
        row1.add(mWidth);
        row1.add(mHeight);
        row1.add(new JLabel("Scale:"));
        row1.add(mScale);
        row1.add(mPrint);
        JPanel row2 = new JPanel();
        row2.setLayout(new FlowLayout());
        row2.add(mDrawRoads);
        row2.add(mDrawRivers);
        row2.add(new JLabel("Borders:"));
        row2.add(mDrawBorders);
        row2.add(mTownBorders);
        row2.add(new JLabel("Names:"));
        row2.add(mTownNames);
        row2.add(mFeatureNames);
        row2.add(mCountryNames);
        row2.add(mBorderNames);
        setLayout(new GridLayout(2, 1));
        add(row1);
        add(row2);
    }
    
    private void initLink()
    {
        mData.addPropertyChangeListener(new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                switch (evt.getPropertyName())
                {
                    case "X":
                        mOX.setValue(mData.getX());
                        break;
                    case "Y":
                        mOY.setValue(mData.getY());
                        break;
                    case "scale":
                        mScale.setValue(mData.getScale());
                        break;
                }
            }
        });
        mPrint.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doPrint();
            }
        });
    }
    
    private void doPrint()
    {
        File dir = new File("c:\\temp");
        String prefix = "enuma";
        int tilesWide = 4;
        int tilesHigh = 4;
        int pixelsWide = 1024*4;
        int pixelsHigh = 768*4;
        MapPrintLogic.printTiles(mData, dir, prefix, tilesWide, tilesHigh, pixelsWide, pixelsHigh);
    }
    
    private JSpinner bindSpinner(final String dataName, int min, int max, int step)
    {
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(IntegerUtils.parseInt(BeanUtils.get(mData, dataName)), min, max, step));
        spinner.addChangeListener(new ChangeListener() {            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                BeanUtils.set(mData, dataName, spinner.getValue());
                mPCS.firePropertyChange(dataName, null, BeanUtils.get(mData, dataName));
            }
        });
        return spinner;
    }
    
    private JCheckBox bindCheck(final String dataName, String title)
    {
        final JCheckBox box = new JCheckBox(title, BooleanUtils.parseBoolean(BeanUtils.get(mData, dataName)));
        box.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                BeanUtils.set(mData, dataName, box.isSelected());
                mPCS.firePropertyChange(dataName, null, BeanUtils.get(mData, dataName));
            }
        });
        return box;
    }
    
    private JButton bindIncrement(final String dataName, String title, Supplier<Integer> incr)
    {
        JButton button = new JButton(title);
        button.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int v = IntegerUtils.parseInt(BeanUtils.get(mData, dataName));
                v += incr.get();
                BeanUtils.set(mData, dataName, v);
                mPCS.firePropertyChange(dataName, null, BeanUtils.get(mData, dataName));
            }
        });
        return button;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        mPCS.addPropertyChangeListener(listener);
    }
}
