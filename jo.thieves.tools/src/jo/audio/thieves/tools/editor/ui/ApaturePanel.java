package jo.audio.thieves.tools.editor.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jo.audio.thieves.tools.editor.data.TApature;
import jo.audio.thieves.tools.editor.logic.LocationsLogic;
import jo.util.ui.swing.TableLayout;

@SuppressWarnings("serial")
public class ApaturePanel extends JComponent
{
    private TApature         mApature;

    private JTextField    mChar;
    private JTextField    mID;
    private JTextField    mName;
    private JTextArea     mDescription;
    private JLabel        mColorLabel;
    private JButton       mColor;
    private JSpinner      mClimbWallsMod;
    private JSpinner      mFindTrapsMod;
    private JSpinner      mOpenLocksMod;
    private JSpinner      mMoveSilentlyMod;
    private JSpinner      mHideInShadowsMod;
    private JCheckBox     mInside;
    private JCheckBox     mBedroom;

    public ApaturePanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mChar = new JTextField();
        mID = new JTextField();
        mName = new JTextField(24);
        mDescription = new JTextArea(4,24);
        mColorLabel = new JLabel("\u2588\u2588\u2588\u2588");
        mColor = new JButton("Color");
        mClimbWallsMod = new JSpinner(new SpinnerNumberModel(0, -100, 100, 5));
        mFindTrapsMod = new JSpinner(new SpinnerNumberModel(0, -100, 100, 5));
        mOpenLocksMod = new JSpinner(new SpinnerNumberModel(0, -100, 100, 5));
        mMoveSilentlyMod = new JSpinner(new SpinnerNumberModel(0, -100, 100, 5));
        mHideInShadowsMod = new JSpinner(new SpinnerNumberModel(0, -100, 100, 5));
        mInside = new JCheckBox("Interior");
        mBedroom = new JCheckBox("Bedroom");
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,+", new JLabel("Char:"));
        add("+,. fill=h", mChar);
        add("1,+", new JLabel("ID:"));
        add("+,. fill=h", mID);
        add("1,+", new JLabel("Name:"));
        add("+,. fill=h", mName);
        add("1,+ 2x1 fill=h", new JLabel("Desc:"));
        add("1,+ 2x1 fill=h", mDescription);
        add("1,+", mColorLabel);
        add("+,. fill=h", mColor);
        add("1,+", new JLabel(""));
        add("+,. fill=h", mInside);
        add("1,+", new JLabel(""));
        add("+,. fill=h", mBedroom);
        add("1,+", new JLabel("CW:"));
        add("+,. fill=h", mClimbWallsMod);
        add("1,+", new JLabel("FT:"));
        add("+,. fill=h", mFindTrapsMod);
        add("1,+", new JLabel("OL:"));
        add("+,. fill=h", mOpenLocksMod);
        add("1,+", new JLabel("MS:"));
        add("+,. fill=h", mMoveSilentlyMod);
        add("1,+", new JLabel("HS:"));
        add("+,. fill=h", mHideInShadowsMod);
    }

    private void initLink()
    {
        mColor.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doColor();
            }
        });
        mChar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                //EditorTileLogic.updateChar(mApature, mChar.getText());
            }
        });
        mID.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                //EditorTileLogic.updateID(mApature, mID.getText());
            }
        });
        mName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                //EditorTileLogic.updateName(mApature, mName.getText());
            }
        });
        mDescription.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                //EditorTileLogic.updateDesc(mApature, mDescription.getText());
            }
        });
        mClimbWallsMod.addChangeListener(new ChangeListener() {            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                //EditorTileLogic.updateClimbWalls(mApature, (Integer)mClimbWallsMod.getValue());
            }
        });
        mHideInShadowsMod.addChangeListener(new ChangeListener() {            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                //EditorTileLogic.updateHideInShadows(mApature, (Integer)mHideInShadowsMod.getValue());
            }
        });
        mFindTrapsMod.addChangeListener(new ChangeListener() {            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                //EditorTileLogic.updateFindTraps(mApature, (Integer)mFindTrapsMod.getValue());
            }
        });
        mOpenLocksMod.addChangeListener(new ChangeListener() {            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                //EditorTileLogic.updateOpenLocks(mApature, (Integer)mOpenLocksMod.getValue());
            }
        });
        mMoveSilentlyMod.addChangeListener(new ChangeListener() {            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                //EditorTileLogic.updateMoveSilently(mApature, (Integer)mMoveSilentlyMod.getValue());
            }
        });
        mInside.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //EditorTileLogic.updateInside(mApature, mInside.isSelected());
            }
        });
        mBedroom.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //EditorTileLogic.updateBedroom(mApature, mBedroom.isSelected());
            }
        });
    }
    
    private void doColor()
    {
        Color newColor = JColorChooser.showDialog(
                this,
                "Choose Color",
                LocationsLogic.getColor(mApature));
        if (newColor != null)
        {
            //EditorTileLogic.updateColor(mApature, newColor);
            updateColorIcon();
        }
    }

    public TApature getTile()
    {
        return mApature;
    }

    public void setTile(TApature tile)
    {
        mApature = tile;
        if (mApature == null)
        {
            mChar.setText("");
            mID.setText("");
            mName.setText("");
            mDescription.setText("");
            mColorLabel.setVisible(false);
            mClimbWallsMod.setValue(0);
            mFindTrapsMod.setValue(0);
            mOpenLocksMod.setValue(0);
            mMoveSilentlyMod.setValue(0);
            mHideInShadowsMod.setValue(0);
            mInside.setSelected(false);
            mBedroom.setSelected(false);
        }
        else
        {
            mChar.setText(LocationsLogic.getChar(mApature));
            mID.setText(mApature.getID());
            mName.setText(mApature.getName());
            mDescription.setText(mApature.getDescription());
            updateColorIcon();
            mColorLabel.setVisible(true);
            //mClimbWallsMod.setValue(mApature.getClimbWallsMod());
            //mFindTrapsMod.setValue(mApature.getFindTrapsMod());
            //mOpenLocksMod.setValue(mApature.getOpenLocksMod());
            //mMoveSilentlyMod.setValue(mApature.getMoveSilentlyMod());
            //mHideInShadowsMod.setValue(mApature.getHideInShadowsMod());
            //mInside.setSelected(mApature.getInside());
            //mBedroom.setSelected(mApature.getBedroom());
        }
    }

    public void updateColorIcon()
    {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_3BYTE_BGR);
        int rgb = LocationsLogic.getColor(mApature).getRGB();
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++)
                img.setRGB(x, y, rgb);
        ImageIcon icon = new ImageIcon(img);
        mColorLabel.setIcon(icon);
    }
}
