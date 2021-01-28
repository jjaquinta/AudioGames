package jo.audio.companions.tools.gui.edit.rich;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import jo.audio.companions.data.build.PRoomBean;
import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.edit.logic.SelectionLogic;
import jo.audio.companions.tools.gui.edit.ui.PCSList;
import jo.audio.companions.tools.gui.edit.ui.PCSTextField;

public class FeatureEditPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean         mRuntime;
    
    private JButton             mFeatureInfo;
    private PCSTextField        mFeatureName;
    private PCSList<PRoomBean>  mRooms;
    private JButton             mAddRoom;
    private JButton             mDelRoom;
    private MapPanel            mNavigator;
    private RoomDetailPanel     mRoomDetails;
    
    public FeatureEditPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mFeatureInfo = new JButton("(i)");
        mFeatureName = new PCSTextField(mRuntime, "selectedFeature.name", null);
        mFeatureName.setEditable(false);
        Font oldFont = mFeatureName.getFont();
        mFeatureName.setFont(new Font(oldFont.getName(), oldFont.getStyle(), oldFont.getSize() + 3));
        mRooms = new PCSList<PRoomBean>(mRuntime, "selectedFeature.rooms", mRuntime, "selectedRoom",
                (room) -> SelectionLogic.selectRoom(mRuntime, (room == null) ? null : room.getID()));

        mAddRoom = new JButton("+");
        mDelRoom = new JButton("-");
        mNavigator = new MapPanel(mRuntime);
        mRoomDetails = new RoomDetailPanel(mRuntime);
    }

    private void initLayout()
    {
        JPanel roomTools = new JPanel();
        roomTools.setLayout(new GridLayout(1, 2));
        roomTools.add(mAddRoom);
        roomTools.add(mDelRoom);
        
        JPanel roomTitle = new JPanel();
        roomTitle.setLayout(new BorderLayout());
        roomTitle.add("Center", new JLabel("Rooms:"));
        roomTitle.add("East", roomTools);
        
        JPanel roomSelect = new JPanel();
        roomSelect.setLayout(new BorderLayout());
        roomSelect.add("North", roomTitle);
        roomSelect.add("Center", new JScrollPane(mRooms,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
        
        JPanel featureLeft = new JPanel();
        featureLeft.setLayout(new GridLayout(2, 1));
        featureLeft.add(roomSelect);
        featureLeft.add(mNavigator);
        
        JPanel featureTitle = new JPanel();
        featureTitle.setLayout(new BorderLayout());
        featureTitle.add("West", mFeatureInfo);
        featureTitle.add("Center", mFeatureName);
        
        setLayout(new BorderLayout());
        add("North", featureTitle);
        add("West", featureLeft);
        add("Center", mRoomDetails);
    }

    private void initLink()
    {
        mFeatureInfo.addActionListener(new ActionListener() {           
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doFeatureInfo();
            }
        });
        mDelRoom.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doDelRoom();
            }
        });
        mAddRoom.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doAddRoom();
            }
        });
    }
    
    private void doAddRoom()
    {
        PRoomBean newRoom = FeatureLogic.newRoom(mRuntime.getLocationRich(), mRuntime.getSelectedFeature());
        SelectionLogic.selectRoom(mRuntime, newRoom.getID());
    }
    
    private void doDelRoom()
    {
        PRoomBean room = mRuntime.getSelectedRoom();
        if (room == null)
            return;
        FeatureLogic.removeRoom(mRuntime, mRuntime.getSelectedFeature().getOID(), room.getID());
    }
    
    private void doFeatureInfo()
    {
        FeatureDetailPanel client = new FeatureDetailPanel(mRuntime);
        JOptionPane.showConfirmDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                client, "Feature Details", JOptionPane.OK_CANCEL_OPTION);
    }
}
