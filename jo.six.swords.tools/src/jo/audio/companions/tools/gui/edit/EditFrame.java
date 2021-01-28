package jo.audio.companions.tools.gui.edit;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.edit.logic.RuntimeLogic;

public class EditFrame extends JFrame
{
    /**
     * 
     */
    private static final long serialVersionUID = 4297397520186885053L;
    
    private RuntimeBean mRuntime;
    
    private EditPanel    mClient;

    public EditFrame()
    {
        super("Tsatsatzu - 6 Swords Location Editor");
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mRuntime = RuntimeLogic.newInstance();
        mClient = new EditPanel(mRuntime);
    }

    private void initLayout()
    {
        getContentPane().add("Center", mClient);
        
    }

    private void initLink()
    {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosed(e);
                RuntimeLogic.shutdown(mRuntime);
                System.exit(0);
            }
        });
    }
}
