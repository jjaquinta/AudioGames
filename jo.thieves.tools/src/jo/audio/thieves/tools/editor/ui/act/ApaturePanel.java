package jo.audio.thieves.tools.editor.ui.act;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.tools.editor.logic.EditorApatureLogic;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.util.ui.swing.utils.ListenerUtils;

public class ApaturePanel extends JPanel
{
    private ApaturePicker mApaturePicker;
    private ApatureViewer mApaturePanel;
    private JButton              mAddApature;
    private JButton              mDelApature;

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
        mAddApature = new JButton("+");
        mDelApature = new JButton("-");
    }

    private void initLayout()
    {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout());
        toolbar.add(mAddApature);
        toolbar.add(mDelApature);
        
        JPanel topbar = new JPanel();
        topbar.setLayout(new BorderLayout());
        topbar.add("Center", mApaturePicker);
        topbar.add("East", toolbar);

        setLayout(new BorderLayout());
        add("North", topbar);
        add("Center", mApaturePanel);
    }

    private void initLink()
    {
        ListenerUtils.listen(mAddApature, (e) -> doAddApature());
        ListenerUtils.listen(mDelApature, (e) -> doDelApature());
    }

    private void doAddApature()
    {
        String id = (String)JOptionPane.showInputDialog(this,
                "ID for new Apature", "Add New Apature",
                JOptionPane.QUESTION_MESSAGE, null, null, "NEW_Apature");
        if (id == null)
            return;
        PApature a = EditorApatureLogic.newApature(id);
        EditorSettingsLogic.getInstance().setSelectedApature(a);
    }

    private void doDelApature()
    {
        PApature selected = EditorSettingsLogic.getInstance().getSelectedApature();
        if (selected == null)
            return;
        int proceed = JOptionPane.showOptionDialog(this,
                "Delete " + selected.getID(), "Delete Apature",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                null, null);
        if (proceed != JOptionPane.YES_OPTION)
            return;
        EditorApatureLogic.deleteApature(selected);
    }
}
