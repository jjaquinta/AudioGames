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

import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.data.TApature;
import jo.audio.thieves.tools.editor.data.TLocations;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;

@SuppressWarnings("serial")
public class ApaturesPanel extends JComponent
{
    private JComboBox<TApature> mTiles;
    private ApaturePanel        mClient;

    public ApaturesPanel()
    {
        initInstantiate();
        initLayout();
        initLink();
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
        es.addPropertyChangeListener("selectedLocation", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doNewLocation();
            }
        });
        es.addPropertyChangeListener("selectedTile", new PropertyChangeListener() {            
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
                TApature tile = (TApature)mTiles.getSelectedItem();
                mClient.setTile(tile);
            }
        });
    }
    
    private void doNewLocation()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        TLocations loc = es.getSelectedLocation();
        if (loc == null)
            setTiles(null);
        else
            setTiles(loc.getApatures());
    }
    
    private void doNewTile()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        TApature tile = es.getSelectedApature();
        if (tile == null)
            return;
        mTiles.setSelectedItem(tile);
    }

    private void setTiles(Collection<TApature> tiles)
    {
        DefaultComboBoxModel<TApature> model = (DefaultComboBoxModel<TApature>)mTiles.getModel();
        model.removeAllElements();
        if (tiles != null)
        {
            for (TApature tile : tiles)
                model.addElement(tile);
            if (tiles.size() > 0)
                model.setSelectedItem(tiles.iterator().next());
        }
    }
}
