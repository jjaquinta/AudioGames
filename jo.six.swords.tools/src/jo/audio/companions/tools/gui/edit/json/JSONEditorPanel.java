package jo.audio.companions.tools.gui.edit.json;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.json.JSONEditPanel;

public class JSONEditorPanel extends JPanel implements PropertyChangeListener
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
    
    private JSONEditPanel   mClient;
    
    public JSONEditorPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mClient = new JSONEditPanel();
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
        mRuntime.addPropertyChangeListener(this);
    }
    
    public void beingShown()
    {
        System.out.println("JSONEditor being shown");
    }
    
    public void beingHidden()
    {
        System.out.println("JSONEditor being hidden");
        //JSONObject json = mClient.getJsonObject();
        //RuntimeLogic.updateLocationJSON(mRuntime, json);
    }

    private void doNewJSON()
    {
        mClient.setJsonObject(mRuntime.getLocationJSON(), JSONEditPanel.UpdateType.REPLACE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if ("locationJSON".equals(evt.getPropertyName()))
            doNewJSON();
    }
}
