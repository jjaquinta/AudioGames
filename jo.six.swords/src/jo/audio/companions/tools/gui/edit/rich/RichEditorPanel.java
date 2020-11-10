package jo.audio.companions.tools.gui.edit.rich;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.edit.logic.RuntimeLogic;

public class RichEditorPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
    
    private RichClientPanel mClient;
    
    public RichEditorPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mClient = new RichClientPanel(mRuntime);
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", new JScrollPane(mClient,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
    }

    private void initLink()
    {
    }
    
    public void beingShown()
    {
        System.out.println("TextEditor being shown");
    }
    
    public void beingHidden()
    {
        System.out.println("TextEditor being hidden");
        RuntimeLogic.updateLocationRich(mRuntime, mRuntime.getLocationRich());
    }
}
