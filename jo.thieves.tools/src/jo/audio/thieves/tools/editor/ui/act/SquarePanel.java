package jo.audio.thieves.tools.editor.ui.act;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.audio.thieves.tools.editor.logic.EditorSquareLogic;
import jo.util.ui.swing.utils.ListenerUtils;

public class SquarePanel extends JPanel
{
    private SquarePicker mSquarePicker;
    private SquareViewer mSquarePanel;
    private JButton              mAddSquare;
    private JButton              mDelSquare;

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
        mAddSquare = new JButton("+");
        mDelSquare = new JButton("-");
    }

    private void initLayout()
    {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout());
        toolbar.add(mAddSquare);
        toolbar.add(mDelSquare);
        
        JPanel topbar = new JPanel();
        topbar.setLayout(new BorderLayout());
        topbar.add("Center", mSquarePicker);
        topbar.add("East", toolbar);

        setLayout(new BorderLayout());
        add("North", topbar);
        add("Center", mSquarePanel);
    }

    private void initLink()
    {
        ListenerUtils.listen(mAddSquare, (e) -> doAddSquare());
        ListenerUtils.listen(mDelSquare, (e) -> doDelSquare());
    }

    private void doAddSquare()
    {
        String id = (String)JOptionPane.showInputDialog(this,
                "ID for new Square", "Add New Square",
                JOptionPane.QUESTION_MESSAGE, null, null, "NEW_SQUARE");
        if (id == null)
            return;
        PSquare s = EditorSquareLogic.newSquare(id);
        EditorSettingsLogic.getInstance().setSelectedSquare(s);
    }

    private void doDelSquare()
    {
        PSquare selected = EditorSettingsLogic.getInstance().getSelectedSquare();
        if (selected == null)
            return;
        int proceed = JOptionPane.showOptionDialog(this,
                "Delete " + selected.getID(), "Delete Square",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                null, null);
        if (proceed != JOptionPane.YES_OPTION)
            return;
        EditorSquareLogic.deleteSquare(selected);
    }
}
