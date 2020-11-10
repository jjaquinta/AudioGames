package jo.audio.companions.tools.gui.edit.rich;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jo.audio.companions.tools.gui.edit.data.PFeatureBean;
import jo.audio.companions.tools.gui.edit.data.PRoomBean;
import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.edit.ui.PCSTextField;
import jo.util.ui.swing.TableLayout;

public class FeatureDetailPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;

    private RuntimeBean mRuntime;
    private PRoomBean   mEntranceRoom;
    
    private PCSTextField  mName;
    private PCSTextField  mLocation;
    private JButton       mEntrance;
    private PCSTextField  mEnabledBy;
    
    public FeatureDetailPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
        initFeature(mRuntime.getSelectedFeature());
    }

    private void initInstantiate()
    {
        mLocation = new PCSTextField(mRuntime, "selectedFeature.location",
                (txt) -> FeatureLogic.setFeatureLocation(mRuntime, 0, txt));
        mName = new PCSTextField(mRuntime, "selectedFeature.name",
                (txt) -> FeatureLogic.setFeatureName(mRuntime, 0, txt));
        mEntrance = new JButton();
        mEnabledBy = new PCSTextField(mRuntime, "selectedFeature.enabledBy",
                (txt) -> FeatureLogic.setFeatureEnabledBy(mRuntime, 0, txt));
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,1", new JLabel("Name:"));
        add("+,.,1,1,3 fill=h", mName);
        add("1,+", new JLabel("ID:"));
        add("+,.,1,1,3 fill=h", mLocation);
        add("1,+", new JLabel("Entrance:"));
        add("+,.,1,1,3 fill=h", mEntrance);
        add("1,+", new JLabel("Enabled By:"));
        add("+,.,1,1,3 fill=h", mEnabledBy);
    }

    private void initLink()
    {
        mRuntime.addPropertyChangeListener("selectedFeature", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                initFeature(mRuntime.getSelectedFeature());
            }
        });
        mEntrance.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doNewEntrance();
            }
        });
    }
    
    private void doNewEntrance()
    {
        // TODO:
    }

    private void initFeature(PFeatureBean feature)
    {
        if (feature == null)
            return;
        String entranceID = feature.getEntranceID();
        if (entranceID == null)
            mEntranceRoom = null;
        else
            for (PRoomBean room : feature.getRooms())
            {
                if (room.getID().equals(entranceID))
                {
                    mEntranceRoom = room;
                    break;
                }
            }
        if (mEntranceRoom == null)
            mEntrance.setText("<pick>");
        else
            mEntrance.setText(mEntranceRoom.getName());
    }
}
