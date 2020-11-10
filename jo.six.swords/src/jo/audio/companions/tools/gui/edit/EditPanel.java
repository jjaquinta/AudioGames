package jo.audio.companions.tools.gui.edit;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import jo.audio.companions.tools.gui.edit.data.RuntimeBean;

public class EditPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = -810592860706850444L;
    
    private static final String CARD_DISCLAIMER = "disclaimer";
    private static final String CARD_MAIN = "main";
    
    private RuntimeBean mRuntime;
    
    private JPanel          mClient;
    private LoadPanel mDisclaimer;
    private MainPanel       mMain;

    public EditPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
        doUpdateDisplay();
    }

    private void initInstantiate()
    {
        mClient = new JPanel();
        mDisclaimer = new LoadPanel(mRuntime);
        mMain = new MainPanel(mRuntime);
    }

    private void initLayout()
    {
        mClient.setLayout(new CardLayout());
        mClient.add(mDisclaimer, CARD_DISCLAIMER);
        mClient.add(mMain, CARD_MAIN);
        setLayout(new BorderLayout());
        add("Center", mClient);
    }

    private void initLink()
    {
        mRuntime.addPropertyChangeListener("loaded", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doUpdateDisplay();
            }
        });
    }

    private void doUpdateDisplay()
    {
        if (mRuntime.isLoaded())
            ((CardLayout)mClient.getLayout()).show(mClient, CARD_MAIN);
        else
            ((CardLayout)mClient.getLayout()).show(mClient, CARD_DISCLAIMER);
    }
}
