package jo.audio.thieves.tools.editor.ui.act;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class ApaturePanel extends JPanel
{
    private ApaturePicker mApaturePicker;
    private ApatureViewer mApaturePanel;

    public ApaturePanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mApaturePicker = new ApaturePicker();
        mApaturePanel = new ApatureViewer();
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("North", mApaturePicker);
        add("Center", mApaturePanel);
    }

    private void initLink()
    {
    }
}
