package jo.audio.thieves.tools.editor.ui;

import java.awt.Color;
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

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorApatureLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.util.ui.swing.TableLayout;
import jo.util.ui.swing.utils.FocusUtils;
import jo.util.ui.swing.utils.ListenerUtils;

@SuppressWarnings("serial")
public class ApaturePanel extends JComponent
{
    private PApature         mApature;

    private JTextField    mID;
    private JTextField    mName;
    private JTextArea     mDescription;
    private JTextArea     mTransition;
    private JLabel        mColorLabel;
    private JButton       mColor;
    private JSpinner      mClimbWallsMod;
    private JSpinner      mFindTrapsMod;
    private JSpinner      mOpenLocksMod;
    private JSpinner      mMoveSilentlyMod;
    private JSpinner      mHideInShadowsMod;
    private JCheckBox     mLockable;
    private JCheckBox     mOpenable;
    private JCheckBox     mTransparent;

    public ApaturePanel()
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
        mTransition = new JTextArea(4,24);
        mTransition.setLineWrap(true);
        mTransition.setWrapStyleWord(true);
        mColorLabel = new JLabel("\u2588\u2588\u2588\u2588");
        mColor = new JButton("Color");
        mClimbWallsMod = new JSpinner(new SpinnerNumberModel(0, -100, 100, 5));
        mFindTrapsMod = new JSpinner(new SpinnerNumberModel(0, -100, 100, 5));
        mOpenLocksMod = new JSpinner(new SpinnerNumberModel(0, -100, 100, 5));
        mMoveSilentlyMod = new JSpinner(new SpinnerNumberModel(0, -100, 100, 5));
        mHideInShadowsMod = new JSpinner(new SpinnerNumberModel(0, -100, 100, 5));
        mLockable = new JCheckBox("Lockable");
        mOpenable = new JCheckBox("Openable");
        mTransparent = new JCheckBox("Transparent");
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
        add("1,+ 2x1 fill=h", new JLabel("Transition:"));
        add("1,+ 2x1 fill=h", mTransition);
        add("1,+", mColorLabel);
        add("+,. fill=h", mColor);
        add("1,+", new JLabel(""));
        add("+,. fill=h", mOpenable);
        add("1,+", new JLabel(""));
        add("+,. fill=h", mLockable);
        add("1,+", new JLabel(""));
        add("+,. fill=h", mTransparent);
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
        ListenerUtils.listen(mColor, (e) -> doColor());
        FocusUtils.focusLost(mID, (e) -> EditorApatureLogic.updateID(mApature, mID.getText()));
        FocusUtils.focusLost(mName, (e) -> EditorApatureLogic.updateName(mApature, mName.getText()));
        FocusUtils.focusLost(mDescription, (e) -> EditorApatureLogic.updateDesc(mApature, mDescription.getText()));
        ListenerUtils.listen(mClimbWallsMod, (e) -> EditorApatureLogic.updateClimbWalls(mApature, (Integer)mClimbWallsMod.getValue()));
        ListenerUtils.listen(mHideInShadowsMod, (e) -> EditorApatureLogic.updateHideInShadows(mApature, (Integer)mHideInShadowsMod.getValue()));
        ListenerUtils.listen(mFindTrapsMod, (e) -> EditorApatureLogic.updateFindTraps(mApature, (Integer)mFindTrapsMod.getValue()));
        ListenerUtils.listen(mOpenLocksMod, (e) -> EditorApatureLogic.updateOpenLocks(mApature, (Integer)mOpenLocksMod.getValue()));
        ListenerUtils.listen(mMoveSilentlyMod, (e) -> EditorApatureLogic.updateMoveSilently(mApature, (Integer)mMoveSilentlyMod.getValue()));
        ListenerUtils.listen(mLockable, (e) -> EditorApatureLogic.updateLockable(mApature, mLockable.isSelected()));
        ListenerUtils.listen(mOpenable, (e) -> EditorApatureLogic.updateOpenable(mApature, mOpenable.isSelected()));
        ListenerUtils.listen(mTransparent, (e) -> EditorApatureLogic.updateTransparent(mApature, mTransparent.isSelected()));
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.listen("selectedApature", (ov,nv) -> setTile(EditorSettingsLogic.getInstance().getSelectedApature()));
    }
    
    private void doColor()
    {
        Color newColor = JColorChooser.showDialog(
                this,
                "Choose Color",
                mApature.getColorObject());
        if (newColor != null)
        {
            EditorApatureLogic.updateColor(mApature, newColor);
            updateColorIcon();
        }
    }

    public PApature getTile()
    {
        return mApature;
    }

    public void setTile(PApature tile)
    {
        mApature = tile;
        if (mApature == null)
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
            mOpenable.setSelected(false);
            mLockable.setSelected(false);
            mTransparent.setSelected(false);
        }
        else
        {
            mID.setText(mApature.getID());
            mName.setText(mApature.getName());
            mDescription.setText(mApature.getDescription());
            mTransition.setText(mApature.getTransition());
            updateColorIcon();
            mColorLabel.setVisible(true);
            mClimbWallsMod.setValue(mApature.getClimbWallsMod());
            mFindTrapsMod.setValue(mApature.getFindTrapsMod());
            mOpenLocksMod.setValue(mApature.getOpenLocksMod());
            mMoveSilentlyMod.setValue(mApature.getMoveSilentlyMod());
            mHideInShadowsMod.setValue(mApature.getHideInShadowsMod());
            mOpenable.setSelected(mApature.getOpenable());
            mLockable.setSelected(mApature.getLockable());
            mTransparent.setSelected(mApature.getTransparent());
        }
    }

    public void updateColorIcon()
    {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_3BYTE_BGR);
        int rgb = Integer.parseInt(mApature.getColor().substring(1), 16);
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 16; y++)
                img.setRGB(x, y, rgb);
        ImageIcon icon = new ImageIcon(img);
        mColorLabel.setIcon(icon);
    }
}
