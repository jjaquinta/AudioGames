package jo.audio.companions.tools.gui.edit.rich;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.edit.ui.PCSTextField;
import jo.util.ui.swing.TableLayout;

public class ModuleDetailPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
    
    private JTextField  mID;
    private JTextField  mName;
    private JTextField  mAuthor;
    private JTextField  mAccount;
    private JTextField  mEnabledBy;
    
    public ModuleDetailPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mID = new PCSTextField(mRuntime, "locationRich.id", (txt) -> {FeatureLogic.setModuleID(mRuntime, txt);});
        mName = new PCSTextField(mRuntime, "locationRich.name", (txt) -> {FeatureLogic.setModuleName(mRuntime, txt);});
        mAuthor = new PCSTextField(mRuntime, "locationRich.author", (txt) -> {FeatureLogic.setModuleAuthor(mRuntime, txt);});
        mAccount = new PCSTextField(mRuntime, "locationRich.account", (txt) -> {FeatureLogic.setModuleAuthor(mRuntime, txt);});
        mEnabledBy = new PCSTextField(mRuntime, "locationRich.enabledBy", (txt) -> {FeatureLogic.setModuleAuthor(mRuntime, txt);});
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,1", new JLabel("ID:"));
        add("+,.,1,1,3 fill=h", mID);
        add("1,+", new JLabel("Name:"));
        add("+,.,1,1,3 fill=h", mName);
        add("1,+", new JLabel("Author:"));
        add("+,.,1,1,3 fill=h", mAuthor);
        add("1,+", new JLabel("Account:"));
        add("+,.,1,1,3 fill=h", mAccount);
        add("1,+", new JLabel("Enabled By:"));
        add("+,.,1,1,3 fill=h", mEnabledBy);
    }

    private void initLink()
    {
    }
}
