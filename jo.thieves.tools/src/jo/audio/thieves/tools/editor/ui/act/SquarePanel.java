package jo.audio.thieves.tools.editor.ui.act;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class SquarePanel extends JPanel
{
    private SquarePicker mSquarePicker;
    private SquareViewer mSquarePanel;

    public SquarePanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mSquarePicker = new SquarePicker();
        mSquarePanel = new SquareViewer();
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("North", mSquarePicker);
        add("Center", mSquarePanel);
    }

    private void initLink()
    {
    }
}
