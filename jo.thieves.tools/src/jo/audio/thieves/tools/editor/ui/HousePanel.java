package jo.audio.thieves.tools.editor.ui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.tools.editor.logic.EditorHouseLogic;
import jo.util.ui.swing.TableLayout;
import jo.util.ui.swing.utils.FocusUtils;

@SuppressWarnings("serial")
public class HousePanel extends JComponent
{
    private PTemplate mHouse;

    private JTextField    mID;
    private JTextField    mName;
    private JTextField    mCategory;
    private JTextArea     mDescription;

    public HousePanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mID = new JTextField();
        mName = new JTextField(24);
        mCategory = new JTextField(24);
        mDescription = new JTextArea(4,24);
        mDescription.setLineWrap(true);
        mDescription.setWrapStyleWord(true);
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,+", new JLabel("ID:"));
        add("+,. fill=h", mID);
        add("1,+", new JLabel("Name:"));
        add("+,. fill=h", mName);
        add("1,+", new JLabel("Category:"));
        add("+,. fill=h", mCategory);
        add("1,+ 2x1 fill=h", new JLabel("Desc:"));
        add("1,+ 2x1 fill=h", mDescription);
    }

    private void initLink()
    {
        FocusUtils.focusLost(mID, (e) -> EditorHouseLogic.updateID(mHouse, mID.getText()));
        FocusUtils.focusLost(mName, (e) -> EditorHouseLogic.updateName(mHouse, mName.getText()));
        FocusUtils.focusLost(mCategory, (e) -> EditorHouseLogic.updateCategory(mHouse, mCategory.getText()));
        FocusUtils.focusLost(mDescription, (e) -> EditorHouseLogic.updateDescription(mHouse, mDescription.getText()));
    }
    
    public PTemplate getHouse()
    {
        return mHouse;
    }

    public void setHouse(PTemplate tile)
    {
        mHouse = tile;
        if (mHouse == null)
        {
            mID.setText("");
            mName.setText("");
            mCategory.setText("");
            mDescription.setText("");
        }
        else
        {
            mID.setText(mHouse.getID());
            mName.setText(mHouse.getName());
            mCategory.setText(mHouse.getCategory());
            mDescription.setText(mHouse.getDescription());
        }
    }
}
