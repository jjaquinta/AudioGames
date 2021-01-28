package jo.audio.companions.tools.gui.edit.rich;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.build.PRoomBean;
import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.util.ui.swing.TableLayout;

public class RoomDetailPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean         mRuntime;
    
    private JTextField          mID;
    private JTextField          mName;
    private JTextArea           mDescription;
    private JComboBox<String>   mType;
    private JTable              mParams;
    
    private JTextField          mEncounterID;
    private JButton             mEncounterIDSet;
    private JTextField          mEncounterNumber;
    private JTextField          mEncounterChallenge;
    private JTextField          mWaitTime;
    
    private DirectionsPanel     mDirections;
    
    // items - for a shop
    // itemType
    
    // hires - for a guild hall
    
    // messageSource
    
    public RoomDetailPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mDescription = new JTextArea();
        mName = new JTextField();
        mID = new JTextField();
        mID.setEditable(false);
        mType = new JComboBox<>(new String[] { "scenic", "encounter" });
        mParams = new JTable();
        
        mEncounterID = new JTextField();
        mEncounterID.setEditable(false);
        mEncounterIDSet = new JButton("*");
        mEncounterChallenge = new JTextField();
        mEncounterChallenge.setToolTipText("If encounterID not set, this is a +/- modifier to current danger level for monster type to generate");
        mEncounterNumber = new JTextField();
        mEncounterNumber.setToolTipText("Dice to roll for number encountered");
        mWaitTime = new JTextField();
        
        mDirections = new DirectionsPanel(mRuntime);
    }

    private void initLayout()
    {
        JPanel data = new JPanel();
        data.setLayout(new TableLayout());
        data.add("1,1", new JLabel("Name:"));
        data.add("+,.,1,1,3 fill=h", mName);
        data.add("1,+", new JLabel("ID:"));
        data.add("+,.,1,1,3 fill=h", mID);
        data.add("1,+", new JLabel("Description:"));
        data.add("+,.,1,1,3,3 fill=h", mDescription);
        data.add("1,+", new JLabel("Type:"));
        data.add("+,.,1,1,3 fill=h", mType);
        data.add("1,+,2", new JLabel("ENCOUNTER"));
        data.add("1,+", new JLabel("ID:"));
        JPanel encounterID = new JPanel();
        encounterID.setLayout(new BorderLayout());
        encounterID.add("Center", mEncounterID);
        encounterID.add("East", mEncounterIDSet);
        data.add("+,.,1,1,3 fill=h", encounterID);
        data.add("1,+", new JLabel("Number:"));
        data.add("+,.,1,1,3 fill=h", mEncounterNumber);
        data.add("1,+", new JLabel("Challenge:"));
        data.add("+,.,1,1,3 fill=h", mEncounterChallenge);
        data.add("1,+", new JLabel("Wait:"));
        data.add("+,.,1,1,3 fill=h", mWaitTime);
        
        setLayout(new BorderLayout());
        add("North", data);
        add("Center", mParams);
        add("South", mDirections);
    }

    private void initLink()
    {
        mEncounterIDSet.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doEncounterIDSet();
            }
        });
        mRuntime.addPropertyChangeListener("selectedRoom", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                initRoom();
            }
        });
        mName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                saveName();
            }
        });
        mDescription.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                saveDescription();
            }
        });
        mType.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                saveType();
            }
        });
        mEncounterNumber.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                saveEncounterNumber();
            }
        });
        mEncounterChallenge.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                saveEncounterChallenge();
            }
        });
        mWaitTime.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                saveWaitTime();
            }
        });
    }
    
    private void doEncounterIDSet()
    {
        CompMonsterTypeBean monst = MonsterPickerDlg.pickMonster(this);
        if (monst != null)
            mEncounterID.setText(monst.getID());
        else
            mEncounterID.setText("");
        saveEncounterID();
    }

    private void saveName()
    {
        PRoomBean room = mRuntime.getSelectedRoom();
        if (room == null)
            return;
        room.setName(mName.getText());
    }

    private void saveDescription()
    {
        PRoomBean room = mRuntime.getSelectedRoom();
        if (room == null)
            return;
        room.setDescription(mDescription.getText());
    }

    private void saveType()
    {
        PRoomBean room = mRuntime.getSelectedRoom();
        if (room == null)
            return;
        room.setType((String)mType.getSelectedItem());
    }

    private void saveEncounterID()
    {
        PRoomBean room = mRuntime.getSelectedRoom();
        if (room == null)
            return;
        FeatureLogic.setParam(room, CompRoomBean.MD_ENCOUNTER_ID, mEncounterID.getText());
    }

    private void saveEncounterNumber()
    {
        PRoomBean room = mRuntime.getSelectedRoom();
        if (room == null)
            return;
        FeatureLogic.setParam(room, CompRoomBean.MD_ENCOUNTER_NUMBER, mEncounterNumber.getText());
    }

    private void saveEncounterChallenge()
    {
        PRoomBean room = mRuntime.getSelectedRoom();
        if (room == null)
            return;
        FeatureLogic.setParam(room, CompRoomBean.MD_ENCOUNTER_CHALLENGE, mEncounterChallenge.getText());
    }

    private void saveWaitTime()
    {
        PRoomBean room = mRuntime.getSelectedRoom();
        if (room == null)
            return;
        FeatureLogic.setParamDuration(room, CompRoomBean.MD_WAIT_TIME, mWaitTime.getText());
    }

    private void initRoom()
    {
        PRoomBean room = mRuntime.getSelectedRoom();
        if (room == null)
            return;
        mID.setText(room.getID());
        mName.setText(room.getName());
        mDescription.setText(room.getDescription());
        mType.setSelectedItem(room.getType());
        
        mEncounterID.setText(FeatureLogic.getParam(room, CompRoomBean.MD_ENCOUNTER_ID));
        mEncounterNumber.setText(FeatureLogic.getParam(room, CompRoomBean.MD_ENCOUNTER_NUMBER));
        mEncounterChallenge.setText(FeatureLogic.getParam(room, CompRoomBean.MD_ENCOUNTER_CHALLENGE));
        mWaitTime.setText(FeatureLogic.getParamDuration(room, CompRoomBean.MD_WAIT_TIME));
        // TODO: params
    }
}
