package jo.audio.thieves.tools.editor.ui.act;

import java.awt.CardLayout;

import javax.swing.JPanel;

import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;

public class ActionPanel extends JPanel
{
    private SquarePanel mSquare;
    private ApaturePanel    mApature;
    private JPanel      mStuff;
    
    public ActionPanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mSquare = new SquarePanel();
        mApature = new ApaturePanel();
        mStuff = new JPanel();
    }

    private void initLayout()
    {
        setLayout(new CardLayout());
        add("square", mSquare);
        add("apature", mApature);
        add("stuff", mStuff);
    }

    private void initLink()
    {
        EditorSettingsLogic.getInstance().listen("actionMode", (ov,nv) -> doNewActionMode());
    }
    
    private void doNewActionMode()
    {
        switch (EditorSettingsLogic.getInstance().getActionMode())
        {
            case EditorSettings.ACTION_SQUARE:
                ((CardLayout)getLayout()).show(this, "square");
                break;
            case EditorSettings.ACTION_APATURE:
                ((CardLayout)getLayout()).show(this, "apature");
                break;
            case EditorSettings.ACTION_STUFF:
                ((CardLayout)getLayout()).show(this, "stuff");
                break;
        }
    }
}
