package jo.audio.thieves.tools;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import jo.audio.thieves.tools.data.RuntimeBean;
import jo.audio.thieves.tools.editor.ui.EditPanel;
import jo.audio.thieves.tools.logic.RuntimeLogic;

public class ToolsFrame extends JFrame
{
    /**
     * 
     */
    private static final long serialVersionUID = 4297397520186885053L;
    
    private RuntimeBean mRuntime;
    
    private EditPanel    mClient;
    private JTextField   mStatus;

    public ToolsFrame()
    {
        super("Tsatsatzu - 6 Thieves Tools");
        initInstantiate();
        initLayout();
        initLink();
        doUpdateStatus();
    }

    private void initInstantiate()
    {
        mRuntime = RuntimeLogic.getInstance();
        mClient = new EditPanel();
        mStatus = new JTextField();
        mStatus.setEditable(false);
    }

    private void initLayout()
    {
        getContentPane().add("Center", mClient);
        getContentPane().add("South", mStatus);
    }

    private void initLink()
    {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (mRuntime.isAnyChange())
                    if (JOptionPane.showConfirmDialog(ToolsFrame.this, "You have unsaved changes. Do you want to save first?") == JOptionPane.YES_OPTION)
                        RuntimeLogic.save();
                super.windowClosed(e);
                RuntimeLogic.shutdown();
                System.exit(0);
            }
        });
        RuntimeLogic.getInstance().addPropertyChangeListener("status", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doUpdateStatus();
            }
        });
    }
    
    private void doUpdateStatus()
    {
        mStatus.setText(RuntimeLogic.getInstance().getStatus());
    }
}
