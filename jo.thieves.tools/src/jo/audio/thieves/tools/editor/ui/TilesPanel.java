package jo.audio.thieves.tools.editor.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.data.PLocation;
import jo.audio.thieves.tools.editor.data.PTile;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;

@SuppressWarnings("serial")
public class TilesPanel extends JComponent
{
    private int              mType;
    private JComboBox<PTile> mTiles;
    private TilePanel        mClient;

    public TilesPanel(int type)
    {
        mType = type;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mTiles = new JComboBox<>();
        mClient = new TilePanel();
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
                PTile tile = (PTile)mTiles.getSelectedItem();
                mClient.setTile(tile);
            }
        });
    }
    
    private void doNewLocation()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PLocation loc = es.getSelectedLocation();
        if (loc == null)
            setTiles(null);
        else if (mType == PTile.LOCATION)
            setTiles(loc.getLocations().values());
        else if (mType == PTile.APATURE)
            setTiles(loc.getApatures().values());
        else
        {
            List<PTile> tiles = new ArrayList<>();
            tiles.addAll(loc.getLocations().values());
            tiles.addAll(loc.getApatures().values());
            setTiles(tiles);
        }
    }
    
    private void doNewTile()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PTile tile = es.getSelectedTile();
        if (tile == null)
            return;
        if ((mType == 0) || (mType == tile.getType())) 
        {
            mTiles.setSelectedItem(tile);
        }
    }

    private void setTiles(Collection<PTile> tiles)
    {
        DefaultComboBoxModel<PTile> model = (DefaultComboBoxModel<PTile>)mTiles.getModel();
        model.removeAllElements();
        if (tiles != null)
        {
            for (PTile tile : tiles)
                model.addElement(tile);
            if (tiles.size() > 0)
                model.setSelectedItem(tiles.iterator().next());
        }
    }
}
