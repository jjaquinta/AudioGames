package jo.audio.companions.tools.gui.map.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;

import jo.audio.companions.tools.gui.map.MapData;
import jo.audio.companions.tools.gui.map.logic.MapPrintLogic;
import jo.audio.companions.tools.gui.map.logic.MapTileLogic;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.utils.obj.IntegerUtils;

public class MapPrintDlg extends JDialog
{
    private MapData           mData;
    
    private JTextField        mPrefix;
    private JSpinner          mWidth;
    private JSpinner          mHeight;
    private JSpinner          mScale;

    private int[]             mOrigin = new int[2];
    private BufferedImage[][] mIcons;
    private JToggleButton[][] mSelections;

    private JButton           mOK;
    private JButton           mCancel;

    public MapPrintDlg(Frame f, MapData data)
    {
        super(f, "Print Map", true);
        mData = data;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mIcons = MapTileLogic.getTiles(mOrigin);
        mPrefix = new JTextField("enuma");
        mWidth = new JSpinner(
                new SpinnerNumberModel(1024 * 4, 0, 1024 * 4, 32));
        mHeight = new JSpinner(new SpinnerNumberModel(768 * 4, 0, 768 * 4, 32));
        mScale = new JSpinner(new SpinnerNumberModel(8, -4, 64, 1));
        mOK = new JButton("OK");
        mCancel = new JButton("Cancel");
        mSelections = new JToggleButton[mIcons.length][mIcons[0].length];
        for (int y = 0; y < mIcons.length; y++)
            for (int x = 0; x < mIcons[y].length; x++)
            {
                mSelections[y][x] = new JToggleButton();
                if (mIcons[y][x] != null)
                {
                    Image img = mIcons[y][x].getScaledInstance(64, 48,
                            BufferedImage.SCALE_DEFAULT);
                    mSelections[y][x].setIcon(new ImageIcon(img));
                    mSelections[y][x].setEnabled(true);
                }
                else
                    mSelections[y][x].setEnabled(false);
            }
    }

    private void initLayout()
    {
        JPanel north = new JPanel();
        north.setLayout(new FlowLayout());
        north.add(new JLabel("Prefix:"));
        north.add(mPrefix);
        north.add(new JLabel("Width:"));
        north.add(mWidth);
        north.add(new JLabel("Height:"));
        north.add(mHeight);
        north.add(new JLabel("Scale:"));
        north.add(mScale);
        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        south.add(mOK);
        south.add(mCancel);
        JPanel center = new JPanel();
        center.setLayout(
                new GridLayout(mSelections.length, mSelections[0].length));
        for (int y = 0; y < mSelections.length; y++)
            for (int x = 0; x < mSelections[0].length; x++)
                center.add(mSelections[y][x]);
        JPanel client = new JPanel();
        client.setLayout(new BorderLayout());
        client.add("North", north);
        client.add("Center", new JScrollPane(center));
        client.add("South", south);
        setContentPane(client);
        pack();
    }

    private void initLink()
    {
        Consumer<ActionEvent> al = (e) -> doSelection(e);
        for (int y = 0; y < mSelections.length; y++)
            for (int x = 0; x < mSelections[y].length; x++)
                ListenerUtils.listen(mSelections[y][x], al);
        ListenerUtils.listen(mCancel, (e) -> setVisible(false));
        ListenerUtils.listen(mOK, (e) -> doPrint());
        
    }

    private void doSelection(ActionEvent e)
    {
        JToggleButton obj = (JToggleButton)e.getSource();
        if (obj.isSelected())
        {
            int[] bounds = getSpan();
            for (int y = bounds[1]; y <= bounds[3]; y++)
                for (int x = bounds[0]; x <= bounds[2]; x++)
                    mSelections[y][x].setSelected(true);
        }
        else
        {
            for (int y = 0; y < mSelections.length; y++)
                for (int x = 0; x < mSelections[y].length; x++)
                    mSelections[y][x].setSelected(false);
        }
    }

    private int[] getSpan()
    {
        int lowx = -1;
        int lowy = -1;
        int highx = -1;
        int highy = -1;
        for (int y = 0; y < mSelections.length; y++)
            for (int x = 0; x < mSelections[y].length; x++)
                if (mSelections[y][x].isSelected())
                    if (lowx < 0)
                    {
                        lowx = x;
                        highx = x;
                        lowy = y;
                        highy = y;
                    }
                    else
                    {
                        lowx = Math.min(lowx, x);
                        highx = Math.max(highx, x);
                        lowy = Math.min(lowy, y);
                        highy = Math.max(highy, y);
                    }
        return new int[] { lowx, lowy, highx, highy };
    }
    
    private void doPrint()
    {
        int[] span = getSpan();
        int w = span[2] - span[0];
        int h = span[3] - span[1];
        MapPrintLogic.printTiles(mData, new File("c:\\temp"), mPrefix.getText(), 
                w, h, 
                mOrigin[0], mOrigin[1],
                IntegerUtils.parseInt(mScale.getValue()), 
                IntegerUtils.parseInt(mWidth.getValue()), IntegerUtils.parseInt(mHeight.getValue()));
        setVisible(false);
    }
}
