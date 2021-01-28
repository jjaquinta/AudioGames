package jo.audio.companions.tools.gui.explorer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class ExplorerFrame extends JFrame
{
    /**
     * 
     */
    private static final long serialVersionUID = 4297397520186885053L;
    
    private ExplorerBean mRuntime;
    
    private ExplorerPanel    mClient;

    public ExplorerFrame()
    {
        super("Tsatsatzu - 6 Swords Location Editor");
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mRuntime = ExplorerLogic.newInstance();
        mClient = new ExplorerPanel(mRuntime);
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
                ExplorerLogic.shutdown(mRuntime);
                System.exit(0);
            }
        });
    }
}
