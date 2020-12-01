package jo.audio.thieves.tools.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.data.TLocations;
import jo.audio.thieves.tools.editor.data.TTemplate;
import jo.audio.thieves.tools.editor.logic.EditorHouseLogic;
import jo.audio.thieves.tools.editor.logic.EditorLocationLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.audio.thieves.tools.logic.RuntimeLogic;

@SuppressWarnings("serial")
public class EditPanel extends JPanel
{
    private TilesPanel            mTiles;
    private ApaturesPanel         mApatures;
    private FloorPanel            mClient;
    private JComboBox<TLocations> mLocation;
    private JButton               mAddLocation;
    private JButton               mDelLocation;
    private JComboBox<TTemplate>  mHouse;
    private JButton               mAddHouse;
    private JButton               mDelHouse;
    private JButton               mSave;

    public EditPanel()
    {
        initInstantiate();
        initLayout();
        initLink();
        doNewDataLocations();
        doNewDataHouses();
    }

    private void initInstantiate()
    {
        mClient = new FloorPanel();
        mLocation = new JComboBox<>();
        mAddLocation = new JButton("+");
        mDelLocation = new JButton("-");
        mHouse = new JComboBox<>();
        mAddHouse = new JButton("+");
        mDelHouse = new JButton("-");
        mSave = new JButton("Save");
        mTiles = new TilesPanel();
        mApatures = new ApaturesPanel();
    }

    private void initLayout()
    {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout());
        toolbar.add(mLocation);
        toolbar.add(mAddLocation);
        toolbar.add(mDelLocation);
        toolbar.add(mHouse);
        toolbar.add(mAddHouse);
        toolbar.add(mDelHouse);
        toolbar.add(mSave);

        setLayout(new BorderLayout());
        add("North", toolbar);
        add("East", mTiles);
        add("West", mApatures);
        add("Center", mClient);
    }

    private void initLink()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.addPropertyChangeListener("locations", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doNewDataLocations();
            }
        });
        es.addPropertyChangeListener("houses", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doNewDataHouses();
            }
        });
        es.addPropertyChangeListener("selectedLocation",
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt)
                    {
                        doNewDataSelectedLocation();
                    }
                });
        es.addPropertyChangeListener("selectedHouse",
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt)
                    {
                        doNewDataSelectedHouse();
                    }
                });
        mLocation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doNewUISelectedLocation();
            }
        });
        mHouse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doNewUISelectedHouse();
            }
        });
        mSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    EditorSettingsLogic.save();
                }
                catch (IOException e1)
                {
                    RuntimeLogic.error(e1);
                }
            }
        });
        mAddLocation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doAddLocation();
            }
        });
        mDelLocation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doDelLocation();
            }
        });
        mAddHouse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doAddHouse();
            }
        });
        mDelHouse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doDelHouse();
            }
        });
    }

    private void doNewDataLocations()
    {
        DefaultComboBoxModel<TLocations> locationModel = new DefaultComboBoxModel<>(
                EditorLocationLogic.getLocations().toArray(new TLocations[0]));
        locationModel.setSelectedItem(
                EditorSettingsLogic.getInstance().getSelectedLocation());
        mLocation.setModel(locationModel);
    }

    private void doNewDataHouses()
    {
        DefaultComboBoxModel<TTemplate> houseModel = (DefaultComboBoxModel<TTemplate>)mHouse
                .getModel();
        houseModel.removeAllElements();
        for (TTemplate house : EditorHouseLogic.getHouses())
            houseModel.addElement(house);
        doNewDataSelectedHouse();
    }

    private void doNewDataSelectedLocation()
    {
        TLocations newLocation = EditorSettingsLogic.getInstance()
                .getSelectedLocation();
        TLocations oldLocation = (TLocations)mLocation.getSelectedItem();
        if (newLocation == oldLocation)
            return;
        if (newLocation == null)
            mLocation.setSelectedIndex(-1);
        else
            mLocation.setSelectedItem(newLocation);
    }

    private void doNewUISelectedLocation()
    {
        TLocations newLocation = (TLocations)mLocation.getSelectedItem();
        EditorLocationLogic.selectLocation(newLocation);
    }

    private void doNewDataSelectedHouse()
    {
        TTemplate newHouse = EditorSettingsLogic.getInstance()
                .getSelectedHouse();
        TTemplate oldHouse = (TTemplate)mHouse.getSelectedItem();
        if (newHouse == oldHouse)
            return;
        if (newHouse == null)
            mHouse.setSelectedIndex(-1);
        else
            mHouse.setSelectedItem(newHouse);
    }

    private void doNewUISelectedHouse()
    {
        TTemplate newHouse = (TTemplate)mHouse.getSelectedItem();
        EditorHouseLogic.selectHouse(newHouse);
    }

    private void doAddLocation()
    {
        EditorLocationLogic.addLocation();
    }

    private void doDelLocation()
    {
        TLocations loc = EditorSettingsLogic.getInstance()
                .getSelectedLocation();
        if (loc == null)
            return;
        int proceed = JOptionPane.showOptionDialog(this,
                "Delete " + loc.getPath(), "Delete Location",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                null, null);
        if (proceed != JOptionPane.YES_OPTION)
            return;
        EditorLocationLogic.deleteLocation();
    }

    private void doAddHouse()
    {
        String id = (String)JOptionPane.showInputDialog(this,
                "ID for new House", "Add New House",
                JOptionPane.QUESTION_MESSAGE, null, null, "NEW_HOSUE");
        if (id == null)
            return;
        EditorHouseLogic.addHouse(id);
    }

    private void doDelHouse()
    {
        TTemplate house = EditorSettingsLogic.getInstance().getSelectedHouse();
        if (house == null)
            return;
        int proceed = JOptionPane.showOptionDialog(this,
                "Delete " + house.getID(), "Delete House",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                null, null);
        if (proceed != JOptionPane.YES_OPTION)
            return;
        EditorHouseLogic.deleteHouse();
    }
}
