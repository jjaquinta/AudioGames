package jo.audio.thieves.tools.editor.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;

@SuppressWarnings("serial")
public class ApaturesPanel extends JComponent
{
    private JComboBox<PApature> mTiles;
    private ApaturePanel        mClient;

    public ApaturesPanel()
    {
        initInstantiate();
        initLayout();
        initLink();
        doNewLibrary();
    }

    private void initInstantiate()
    {
        mTiles = new JComboBox<>();
        mClient = new ApaturePanel();
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("North", mTiles);
        add("Center", mClient);
    }

    private void initLink()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.addPropertyChangeListener("library", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doNewLibrary();
            }
        });
        es.addPropertyChangeListener("selectedApature", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doNewTile();
            }
        });
        mTiles.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PApature tile = (PApature)mTiles.getSelectedItem();
                mClient.setTile(tile);
            }
        });
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
    
    private void doNewTile()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PApature tile = es.getSelectedApature();
        if (tile == null)
            return;
        mTiles.setSelectedItem(tile);
    }

    private void setTiles(Collection<PApature> tiles)
    {
        DefaultComboBoxModel<PApature> model = (DefaultComboBoxModel<PApature>)mTiles.getModel();
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
