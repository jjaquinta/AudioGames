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

import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorHouseLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.audio.thieves.tools.logic.RuntimeLogic;

@SuppressWarnings("serial")
public class EditPanel extends JPanel
{
    private SquaresPanel            mTiles;
    private ApaturesPanel         mApatures;
    private FloorPanel            mClient;
    private JComboBox<PTemplate>  mHouse;
    private JButton               mAddHouse;
    private JButton               mDelHouse;
    private JButton               mSave;

    public EditPanel()
    {
        initInstantiate();
        initLayout();
        initLink();
        doNewDataLibrary();
    }

    private void initInstantiate()
    {
        mClient = new FloorPanel();
        mHouse = new JComboBox<>();
        mAddHouse = new JButton("+");
        mDelHouse = new JButton("-");
        mSave = new JButton("Save");
        mTiles = new SquaresPanel();
        mApatures = new ApaturesPanel();
    }

    private void initLayout()
    {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout());
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
        es.addPropertyChangeListener("library", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doNewDataLibrary();
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

    private void doNewDataLibrary()
    {
        DefaultComboBoxModel<PTemplate> houseModel = (DefaultComboBoxModel<PTemplate>)mHouse
                .getModel();
        houseModel.removeAllElements();
        for (PTemplate house : EditorHouseLogic.getHouses())
            houseModel.addElement(house);
        doNewDataSelectedHouse();
    }

    private void doNewDataSelectedHouse()
    {
        PTemplate newHouse = EditorSettingsLogic.getInstance()
                .getSelectedHouse();
        PTemplate oldHouse = (PTemplate)mHouse.getSelectedItem();
        if (newHouse == oldHouse)
            return;
        if (newHouse == null)
            mHouse.setSelectedIndex(-1);
        else
            mHouse.setSelectedItem(newHouse);
    }

    private void doNewUISelectedHouse()
    {
        PTemplate newHouse = (PTemplate)mHouse.getSelectedItem();
        EditorHouseLogic.selectHouse(newHouse);
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
        PTemplate house = EditorSettingsLogic.getInstance().getSelectedHouse();
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
