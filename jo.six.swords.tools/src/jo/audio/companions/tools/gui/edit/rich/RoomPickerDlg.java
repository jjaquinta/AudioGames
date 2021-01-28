package jo.audio.companions.tools.gui.edit.rich;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import jo.audio.companions.data.build.PFeatureBean;
import jo.audio.companions.data.build.PRoomBean;

@SuppressWarnings("serial")
public class RoomPickerDlg extends JDialog
{
    private JList<PRoomBean>mRooms;
    private JCheckBox       mNewRoomCtrl;
    private JCheckBox       mBacklinkCtrl;
    private JOptionPane     mClient;
    
    private PFeatureBean    mFeature;
    private PRoomBean       mRoom;
    private boolean         mNewRoom;
    private boolean         mBackLink;

    public RoomPickerDlg(Frame f, PFeatureBean feature)
    {
        super(f, "Pick Room", true);
        mFeature = feature;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mRooms = new JList<>(mFeature.getRooms().toArray(new PRoomBean[0]));
        mNewRoomCtrl = new JCheckBox("New Room");
        mBacklinkCtrl = new JCheckBox("Back Link");
        mClient = new JOptionPane(
                new Object[] { new JScrollPane(mRooms), mNewRoomCtrl, mBacklinkCtrl },
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                null,
                null
                );
    }

    private void initLayout()
    {
        setContentPane(mClient);
        pack();
    }

    private void initLink()
    {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e)
            {
                mRooms.requestFocusInWindow();
            }
        });
        mClient.addPropertyChangeListener("value", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doClick((Integer)evt.getNewValue());
            }
        });
    }
    
    private void doClick(int value)
    {
        if (value == 0)
        {   // OK
            mRoom = mRooms.getSelectedValue();
            mNewRoom = mNewRoomCtrl.isSelected();
            mBackLink = mBacklinkCtrl.isSelected();
        }
        else
            mRoom = null;
        setVisible(false);
        dispose();
    }
    
    public static PRoomBean pickRoom(Component comp, PFeatureBean feature, boolean[] opts)
    {
        RoomPickerDlg dlg = new RoomPickerDlg((Frame)SwingUtilities.getAncestorOfClass(Frame.class, comp), feature);
        dlg.setVisible(true);
        PRoomBean room = dlg.getRoom();
        if (opts != null)
        {
            if (opts.length > 0)
                opts[0] = dlg.isNewRoom();
            if (opts.length > 1)
                opts[1] = dlg.isBackLink();
        }
        return room;
    }

    public PRoomBean getRoom()
    {
        return mRoom;
    }

    public void setRoom(PRoomBean room)
    {
        mRoom = room;
    }

    public boolean isNewRoom()
    {
        return mNewRoom;
    }

    public void setNewRoom(boolean newRoom)
    {
        mNewRoom = newRoom;
    }

    public boolean isBackLink()
    {
        return mBackLink;
    }

    public void setBackLink(boolean backLink)
    {
        mBackLink = backLink;
    }
}
