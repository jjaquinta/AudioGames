package jo.audio.companions.tools.gui.edit.text;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.edit.logic.RuntimeLogic;

public class TextEditorPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
    
    private JTextArea   mClient;
    
    public TextEditorPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mClient = new JTextArea();
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
        mRuntime.addPropertyChangeListener("locationText", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doNewText();
            }
        });
    }
    
    public void beingShown()
    {
        System.out.println("TextEditor being shown");
    }
    
    public void beingHidden()
    {
        System.out.println("TextEditor being hidden");
        String txt = mClient.getText();
        RuntimeLogic.updateLocationText(mRuntime, txt);
    }
    
    private void doNewText()
    {
        mClient.setText(mRuntime.getLocationText());
    }
}
