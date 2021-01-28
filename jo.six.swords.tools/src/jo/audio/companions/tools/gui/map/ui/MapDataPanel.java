package jo.audio.companions.tools.gui.map.ui;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.tools.gui.map.MapData;
import jo.audio.companions.tools.gui.map.logic.MapDrawLogic;
import jo.audio.companions.tools.gui.map.logic.MapPrintLogic;
import jo.util.ui.swing.ClipboardLogic;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.utils.BeanUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.IntegerUtils;

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
    private JCheckBox   mColorBacking;
    private JButton     mNorth;
    private JButton     mSouth;
    private JButton     mEast;
    private JButton     mWest;
    private JButton     mPrint;
    private JButton     mCountry;
    private JButton     mCopy;
    private JButton     mPaste;
    
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
        mColorBacking = bindCheck("colorBacking", "Color");
        mNorth = bindIncrement("y", "^", () -> -mData.getHeight()/4/mData.getPixelScale());            
        mSouth = bindIncrement("y", "v", () -> mData.getHeight()/4/mData.getPixelScale());
        mEast = bindIncrement("x", ">", () -> mData.getWidth()/4/mData.getPixelScale());
        mWest = bindIncrement("x", "<", () -> - mData.getWidth()/4/mData.getPixelScale());
        mTownNames = bindCheck("townNames", "Towns");
        mFeatureNames = bindCheck("featureNames", "Forts");
        mCountryNames = bindCheck("countryNames", "Countries");
        mBorderNames = bindCheck("borderNames", "Borders");
        mPrint = new JButton("Print");
        mCountry = new JButton("Country");
        mCopy = new JButton("C");
        mPaste = new JButton("P");
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
        row1.add(mCountry);
        row1.add(mCopy);
        row1.add(mPaste);
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
        row2.add(mColorBacking);
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
        ListenerUtils.listen(mPrint, (e) -> doPrint());
        ListenerUtils.listen(mCountry, (e) -> doCountry());
        ListenerUtils.listen(mCopy, (e) -> doCopy());
        ListenerUtils.listen(mPaste, (e) -> doPaste());
    }
    
    private void doCountry()
    {
        List<String> countries = MapDrawLogic.getVassals("");
        Collections.sort(countries);
        String choice = (String)JOptionPane.showInputDialog(this, "Choose a country", "Print Country", JOptionPane.INFORMATION_MESSAGE, null, 
                countries.toArray(new String[0]), countries.get(0));
        if (choice == null)
            return;
        int[] bounds = MapDrawLogic.getBounds(choice);
        MapPrintLogic.printCountry(mData, choice, bounds);
    }
    
    private void doPrint()
    {
        MapPrintDlg dlg = new MapPrintDlg((Frame)SwingUtilities.getRoot(this), mData);
        dlg.setVisible(true);
        /*
        File dir = new File("c:\\temp");
        String prefix = "enuma";
        if (mData.getHover() == null)
        {
            int tilesWide = 4;
            int tilesHigh = 4;
            int pixelsWide = 1024*4;
            int pixelsHigh = 768*4;
            MapPrintLogic.printTiles(mData, dir, prefix, tilesWide, tilesHigh, pixelsWide, pixelsHigh);
        }
        else
        {
            mData.setX(mData.getDrawMinX());
            mData.setY(mData.getDrawMinY());
            double dx = mData.getDrawMaxX() - mData.getDrawMinX();
            double dy = mData.getDrawMaxY() - mData.getDrawMinY();
            int tilesWide = (int)Math.ceil(dx/MapTileLogic.TILE_WIDTH);
            int tilesHigh = (int)Math.ceil(dy/MapTileLogic.TILE_HEIGHT);
            int pixelsWide = 1024*4;
            int pixelsHigh = 768*4;
            MapPrintLogic.printTiles(mData, dir, prefix, tilesWide, tilesHigh, pixelsWide, pixelsHigh);
        }
        */
    }
    
    private void doCopy()
    {
        int ox = IntegerUtils.parseInt(mOX.getValue());
        int oy = IntegerUtils.parseInt(mOY.getValue());
        int sx = IntegerUtils.parseInt(mWidth.getValue());
        int sy = IntegerUtils.parseInt(mHeight.getValue());
        int scale = IntegerUtils.parseInt(mScale.getValue());
        String txt = ox+","+oy+","+sx+","+sy+","+scale;
        ClipboardLogic.copyText(txt);
    }
    
    private void doPaste()
    {
        StringTokenizer txt = new StringTokenizer(ClipboardLogic.paste(), ",");
        mOX.setValue(IntegerUtils.parseInt(txt.nextToken()));
        mOY.setValue(IntegerUtils.parseInt(txt.nextToken()));
        mWidth.setValue(IntegerUtils.parseInt(txt.nextToken()));
        mHeight.setValue(IntegerUtils.parseInt(txt.nextToken()));
        mScale.setValue(IntegerUtils.parseInt(txt.nextToken()));
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
