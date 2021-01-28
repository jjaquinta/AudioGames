package jo.audio.companions.tools.gui.edit.rich;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jo.audio.companions.data.build.PRoomBean;
import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.edit.logic.SelectionLogic;
import jo.util.utils.obj.StringUtils;

public class DirectionsPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean         mRuntime;
    
    private DirectionCtrl mNorth;
    private DirectionCtrl mSouth;
    private DirectionCtrl mEast;
    private DirectionCtrl mWest;
    private JLabel        mCenter;
    
    public DirectionsPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mNorth = new DirectionCtrl(mRuntime, PRoomBean.NORTH, "\u2191");
        mSouth = new DirectionCtrl(mRuntime, PRoomBean.SOUTH, "\u2193");
        mEast = new DirectionCtrl(mRuntime, PRoomBean.EAST, "\u2192");
        mWest = new DirectionCtrl(mRuntime, PRoomBean.WEST, "\u2190");
        mCenter = new JLabel("*", JLabel.CENTER);
    }

    private void initLayout()
    {
        JPanel navigator = this;
        navigator.setLayout(new GridLayout(3, 3));
        navigator.add(new JLabel("\\", JLabel.CENTER));
        navigator.add(mNorth);
        navigator.add(new JLabel("/", JLabel.CENTER));
        navigator.add(mWest);
        navigator.add(mCenter);
        navigator.add(mEast);
        navigator.add(new JLabel("/", JLabel.CENTER));
        navigator.add(mSouth);
        navigator.add(new JLabel("\\", JLabel.CENTER));
    }

    private void initLink()
    {
        mRuntime.addPropertyChangeListener("selectedRoom", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateCenter();
            }
        });
    }
    
    private void updateCenter()
    {
        if (mRuntime.getSelectedRoom() == null)
            mCenter.setText("*");
        else
        {
            String name = mRuntime.getSelectedRoom().getName();
            mCenter.setToolTipText(name);
            if (name.length() > 12)
                name = name.substring(0, 9)+"...";
            mCenter.setText(name);
        }
    }
    
    @SuppressWarnings("serial")
    class DirectionCtrl extends JPanel
    {
        private RuntimeBean         mRuntime;
        private String              mDir;
        private String              mDefText;

        private JButton             mMove;
        private JButton             mIcon1;
        
        public DirectionCtrl(RuntimeBean runtime, String dir, String defText)
        {
            mRuntime = runtime;
            mDir = dir;
            mDefText = defText;
            initInstantiate();
            initLayout();
            initLink();
        }

        private void initInstantiate()
        {
            mMove = new JButton(mDefText);
            mIcon1 = new JButton("");
        }

        private void initLayout()
        {
            setLayout(new BorderLayout());
            add("Center", mMove);
            add("East", mIcon1);
        }

        private void initLink()
        {
            mMove.addActionListener(new ActionListener() {            
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    doDirection();
                }
            });
            mIcon1.addActionListener(new ActionListener() {                
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    doNewDetatch();
                }
            });
            mRuntime.addPropertyChangeListener("selectedRoom", new PropertyChangeListener() {            
                @Override
                public void propertyChange(PropertyChangeEvent evt)
                {
                    updateDirection();
                }
            });
        }
        
        private void doDirection()
        {
            PRoomBean selectedRoom = mRuntime.getSelectedRoom();
            if (selectedRoom == null)
                return;
            String id = selectedRoom.getDir(mDir);
            if (!StringUtils.isTrivial(id))
                SelectionLogic.selectRoom(mRuntime, id);
        }
        
        private void doNewDetatch()
        {
            PRoomBean selectedRoom = mRuntime.getSelectedRoom();
            if (selectedRoom == null)
                return;
            String id = selectedRoom.getDir(mDir);
            if (!StringUtils.isTrivial(id))
            {   // detatch
                selectedRoom.setDir(mDir, "");
            }
            else
            {   // new
                boolean[] opts = new boolean[2];
                PRoomBean room = RoomPickerDlg.pickRoom(this, mRuntime.getSelectedFeature(), opts);
                if (opts[0])
                    room = FeatureLogic.newRoom(mRuntime.getLocationRich(), mRuntime.getSelectedFeature());
                if (room != null)
                {
                    selectedRoom.setDir(mDir, room.getID());
                    if (opts[1])
                    {
                        room.setDir(PRoomBean.opposite(mDir), selectedRoom.getID());
                    }
                }
            }
            updateDirection();
        }
        
        private void updateDirection()
        {
            if (mRuntime.getSelectedRoom() == null)
            {
                mMove.setText(mDefText);
                mMove.setEnabled(false);
                mMove.setToolTipText(null);
                mIcon1.setText("");
                return;
            }
            String id = mRuntime.getSelectedRoom().getDir(mDir);
            if (!StringUtils.isTrivial(id))
            {
                mMove.setEnabled(true);
                PRoomBean room = FeatureLogic.findRoom(mRuntime.getSelectedFeature(), id);
                if (room == null)
                {
                    mMove.setText("{"+id+"}");
                    mMove.setToolTipText(id);
                    mIcon1.setText("\u2718"); // delete
                    mIcon1.setToolTipText("Detatch connection");
                }
                else
                {
                    String name = room.getName();
                    mMove.setToolTipText(name);
                    if (name.length() > 12)
                        name = name.substring(0, 9)+"...";
                    mMove.setText(name);
                    mIcon1.setText("\u2718"); // delete
                    mIcon1.setToolTipText("Detatch connection");
                }
            }
            else
            {
                mMove.setText(mDefText);
                mMove.setEnabled(false);
                mMove.setToolTipText(null);
                mIcon1.setText("\u2727"); // new
                mIcon1.setToolTipText("Create new room");
            }
        }
    }
}
