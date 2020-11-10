package jo.audio.companions.tools.gui.edit;

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

import jo.audio.companions.tools.gui.edit.data.PModuleBean;
import jo.audio.companions.tools.gui.edit.data.RuntimeBean;

@SuppressWarnings("serial")
public class ModulePickerDlg extends JDialog
{
    private RuntimeBean     mRuntime;
    
    private JList<PModuleBean> mModules;
    private JCheckBox       mNewModuleCtrl;
    private JOptionPane     mClient;
    
    private PModuleBean     mModule;
    private boolean         mNewModule;

    public ModulePickerDlg(Frame f, RuntimeBean runtime)
    {
        super(f, "Pick Module", true);
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mModules = new JList<>(mRuntime.getDynamoModules().toArray(new PModuleBean[0]));
        mNewModuleCtrl = new JCheckBox("New Module");
        mClient = new JOptionPane(
                new Object[] { new JScrollPane(mModules), mNewModuleCtrl },
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
                mModules.requestFocusInWindow();
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
            mModule = mModules.getSelectedValue();
        }
        else
            mModule = null;
        setVisible(false);
        dispose();
    }
    
    public static PModuleBean pickModule(Component comp, RuntimeBean runtime, boolean[] opts)
    {
        ModulePickerDlg dlg = new ModulePickerDlg((Frame)SwingUtilities.getAncestorOfClass(Frame.class, comp), runtime);
        dlg.setVisible(true);
        PModuleBean module = dlg.getModule();
        if (opts != null)
        {
            if (opts.length > 0)
                opts[0] = dlg.isNewRoom();
        }
        return module;
    }

    public PModuleBean getModule()
    {
        return mModule;
    }

    public void setModule(PModuleBean module)
    {
        mModule = module;
    }

    public boolean isNewRoom()
    {
        return mNewModule;
    }

    public void setNewRoom(boolean newRoom)
    {
        mNewModule = newRoom;
    }
}
