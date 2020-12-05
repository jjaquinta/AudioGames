package jo.audio.thieves.tools.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorHouseLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.audio.thieves.tools.editor.ui.bp.BluePrintPanel;
import jo.audio.thieves.tools.logic.RuntimeLogic;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.utils.obj.StringUtils;

public class EditPanel extends JPanel
{
    private SquarePicker         mSquarePicker;
    private ApaturePicker        mApaturePicker;
    private SquarePanel          mSquarePanel;
    private ApaturePanel         mApaturePanel;
    private FloorPanel           mClient;
    private FloorViewer          mViewer;
    private BluePrintPanel       mBluePrint;
    private JComboBox<String>    mCategory;
    private JComboBox<PTemplate> mHouse;
    private HousePanel           mHouseEdit;
    private JButton              mAddHouse;
    private JButton              mDelHouse;
    private JButton              mSave;

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
        mViewer = new FloorViewer();
        mHouse = new JComboBox<>();
        mCategory = new JComboBox<>();
        mAddHouse = new JButton("+");
        mDelHouse = new JButton("-");
        mSave = new JButton("Save");
        mSquarePicker = new SquarePicker();
        mApaturePicker = new ApaturePicker();
        mSquarePanel = new SquarePanel();
        mApaturePanel = new ApaturePanel();
        mHouseEdit = new HousePanel();
        mBluePrint = new BluePrintPanel();
    }

    private void initLayout()
    {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout());
        toolbar.add(mCategory);
        toolbar.add(mHouse);
        toolbar.add(mAddHouse);
        toolbar.add(mDelHouse);
        toolbar.add(mSave);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Blueprint", mBluePrint);
        tabs.addTab("Isometric", mViewer);
        tabs.addTab("Plan", mClient);
        JPanel client = new JPanel();
        client.setLayout(new BorderLayout());
        client.add("North", mHouseEdit);
        client.add("Center", tabs);
        
        JPanel left = new JPanel();
        left.setLayout(new BorderLayout());
        left.add("North", mApaturePicker);
        left.add("Center", mApaturePanel);

        JPanel right = new JPanel();
        right.setLayout(new BorderLayout());
        right.add("North", mSquarePicker);
        right.add("Center", mSquarePanel);

        setLayout(new BorderLayout());
        add("North", toolbar);
        add("East", right);
        add("West", left);
        add("Center", client);
    }

    private void initLink()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.listen("library", (ov,nv) -> doNewDataLibrary());
        es.listen("selectedCategory", (ov,nv) -> doNewDataSelectedCategory());
        es.listen("selectedHouse", (ov,nv) -> doNewDataSelectedHouse());
        ListenerUtils.listen(mCategory, (e) -> doNewUISelectedCategory());
        ListenerUtils.listen(mHouse, (e) -> doNewUISelectedHouse());
        ListenerUtils.listen(mSave, (e) -> {
            try
            {
                EditorSettingsLogic.save();
            }
            catch (IOException e1)
            {
                RuntimeLogic.error(e1);
            }
        });
        ListenerUtils.listen(mAddHouse, (e) -> doAddHouse());
        ListenerUtils.listen(mDelHouse, (e) -> doDelHouse());
    }

    private void doNewDataLibrary()
    {
        String oldCategory = (String)mCategory.getSelectedItem();
        DefaultComboBoxModel<String> categoryModel = new DefaultComboBoxModel<>();
        categoryModel.removeAllElements();
        Set<String> categories = new HashSet<>();
        for (PTemplate house : EditorHouseLogic.getHouses())
        {
            if (!categories.contains(house.getCategory()))
            {
                categories.add(house.getCategory());
                categoryModel.addElement(house.getCategory());
            }
        }
        categoryModel.setSelectedItem(oldCategory);
        mCategory.setModel(categoryModel);
        doNewDataSelectedCategory();
    }

    private void doNewDataSelectedCategory()
    {
        String category = EditorSettingsLogic.getInstance().getSelectedCategory();
        //System.out.println("doNewDataSelectedCategory - "+category);
        if (!StringUtils.equals((String)mCategory.getSelectedItem(), category))
            mCategory.setSelectedItem(category);
        DefaultComboBoxModel<PTemplate> houseModel = new DefaultComboBoxModel<PTemplate>();
        houseModel.removeAllElements();
        for (PTemplate house : EditorHouseLogic.getHouses())
        {
            if (StringUtils.isTrivial(category) || category.equals(house.getCategory()))
            {
                houseModel.addElement(house);
                //System.out.println("  adding "+house);
            }
        }
        houseModel.setSelectedItem(EditorSettingsLogic.getInstance().getSelectedHouse());
        mHouse.setModel(houseModel);
        doNewDataSelectedHouse();
    }

    private void doNewDataSelectedHouse()
    {
        PTemplate newHouse = EditorSettingsLogic.getInstance().getSelectedHouse();
        //System.out.println("doNewDataSelectedHouse - "+newHouse);
        PTemplate oldHouse = (PTemplate)mHouse.getSelectedItem();
        if ((newHouse == null) && (oldHouse == null))
            return;
        mHouseEdit.setHouse(newHouse);
        if (newHouse == null)
            mHouse.setSelectedIndex(-1);
        else
            for (int i = 0; i < mHouse.getItemCount(); i++)
                if (((PTemplate)mHouse.getItemAt(i)).getID().equals(newHouse.getID()))
                {
                    mHouse.setSelectedIndex(i);
                    break;
                }
    }

    private void doNewUISelectedCategory()
    {
        String newCategory = (String)mCategory.getSelectedItem();
        EditorHouseLogic.selectCategory(newCategory);
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
                JOptionPane.QUESTION_MESSAGE, null, null, "NEW_HOUSE");
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
