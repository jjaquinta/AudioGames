package jo.audio.thieves.tools.editor.ui;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.util.ui.swing.utils.ListenerUtils;

public class ApaturePicker extends JComponent
{
    private JComboBox<PApature> mApatures;

    public ApaturePicker()
    {
        initInstantiate();
        initLayout();
        initLink();
        doNewLibrary();
    }

    private void initInstantiate()
    {
        mApatures = new JComboBox<>();
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("North", mApatures);
    }

    private void initLink()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.listen("library", (ov,nv) -> doNewLibrary());
        es.listen("selectedApature", (ov,nv) -> doNewDataApature());
        ListenerUtils.listen(mApatures, (e) -> doNewUIApature());
    }
    
    private void doNewLibrary()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLibrary lib = es.getLibrary();
        if (lib == null)
            setTiles(null);
        else
            setTiles(lib.getApatures().values());
    }
    
    private void doNewUIApature()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PApature uitile = (PApature)mApatures.getSelectedItem();
        PApature datatile = es.getSelectedApature();
        if (!PApature.equals(uitile, datatile))
            es.setSelectedApature(uitile);
    }
    
    private void doNewDataApature()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PApature uitile = (PApature)mApatures.getSelectedItem();
        PApature datatile = es.getSelectedApature();
        if (!PApature.equals(uitile, datatile))
            mApatures.setSelectedItem(datatile);
    }

    private void setTiles(Collection<PApature> tiles)
    {
        DefaultComboBoxModel<PApature> model = (DefaultComboBoxModel<PApature>)mApatures.getModel();
        model.removeAllElements();
        if (tiles != null)
        {
            for (PApature tile : tiles)
                model.addElement(tile);
            if (tiles.size() > 0)
                model.setSelectedItem(tiles.iterator().next());
        }
    }
}
