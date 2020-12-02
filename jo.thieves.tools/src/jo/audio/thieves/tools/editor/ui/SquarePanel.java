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

import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.tools.editor.logic.EditorSquareLogic;
import jo.util.ui.swing.TableLayout;

@SuppressWarnings("serial")
public class SquarePanel extends JComponent
{
    private PSquare       mTile;

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

    public SquarePanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mID = new JTextField();
        mName = new JTextField(24);
        mDescription = new JTextArea(4,24);
        mDescription.setLineWrap(true);
        mDescription.setWrapStyleWord(true);
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
        mID.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                EditorSquareLogic.updateID(mTile, mID.getText());
            }
        });
        mName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                EditorSquareLogic.updateName(mTile, mName.getText());
            }
        });
        mDescription.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                EditorSquareLogic.updateDesc(mTile, mDescription.getText());
            }
        });
        mClimbWallsMod.addChangeListener(new ChangeListener() {            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                EditorSquareLogic.updateClimbWalls(mTile, (Integer)mClimbWallsMod.getValue());
            }
        });
        mHideInShadowsMod.addChangeListener(new ChangeListener() {            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                EditorSquareLogic.updateHideInShadows(mTile, (Integer)mHideInShadowsMod.getValue());
            }
        });
        mFindTrapsMod.addChangeListener(new ChangeListener() {            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                EditorSquareLogic.updateFindTraps(mTile, (Integer)mFindTrapsMod.getValue());
            }
        });
        mOpenLocksMod.addChangeListener(new ChangeListener() {            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                EditorSquareLogic.updateOpenLocks(mTile, (Integer)mOpenLocksMod.getValue());
            }
        });
        mMoveSilentlyMod.addChangeListener(new ChangeListener() {            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                EditorSquareLogic.updateMoveSilently(mTile, (Integer)mMoveSilentlyMod.getValue());
            }
        });
        mInside.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                EditorSquareLogic.updateInside(mTile, mInside.isSelected());
            }
        });
        mBedroom.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                EditorSquareLogic.updateBedroom(mTile, mBedroom.isSelected());
            }
        });
    }
    
    private void doColor()
    {
        Color newColor = JColorChooser.showDialog(
                this,
                "Choose Color",
                mTile.getColorObject());
        if (newColor != null)
        {
            EditorSquareLogic.updateColor(mTile, newColor);
            updateColorIcon();
        }
    }

    public PSquare getTile()
    {
        return mTile;
    }

    public void setTile(PSquare tile)
    {
        mTile = tile;
        if (mTile == null)
        {
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
            mID.setText(mTile.getID());
            mName.setText(mTile.getName());
            mDescription.setText(mTile.getDescription());
            updateColorIcon();
            mColorLabel.setVisible(true);
            mClimbWallsMod.setValue(mTile.getClimbWallsMod());
            mFindTrapsMod.setValue(mTile.getFindTrapsMod());
            mOpenLocksMod.setValue(mTile.getOpenLocksMod());
            mMoveSilentlyMod.setValue(mTile.getMoveSilentlyMod());
            mHideInShadowsMod.setValue(mTile.getHideInShadowsMod());
            mInside.setSelected(mTile.getInside());
            mBedroom.setSelected(mTile.getBedroom());
        }
    }

    public void updateColorIcon()
    {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_3BYTE_BGR);
        int rgb = Integer.parseInt(mTile.getColor().substring(1), 16);
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++)
                img.setRGB(x, y, rgb);
        ImageIcon icon = new ImageIcon(img);
        mColorLabel.setIcon(icon);
    }
}