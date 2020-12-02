package jo.audio.thieves.tools.editor.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.audio.thieves.tools.editor.logic.EditorSquareLogic;

@SuppressWarnings("serial")
public class SquaresPanel extends JComponent
{
    private JComboBox<PSquare> mTiles;
    private SquarePanel        mClient;

    public SquaresPanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mTiles = new JComboBox<>();
        mClient = new SquarePanel();
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
        es.addPropertyChangeListener("selectedSquare", new PropertyChangeListener() {            
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
                PSquare tile = (PSquare)mTiles.getSelectedItem();
                mClient.setTile(tile);
            }
        });
    }
    
    private void doNewTile()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PSquare tile = es.getSelectedSquare();
        if (tile == null)
            return;
        mTiles.setSelectedItem(tile);
    }

    private void doNewLibrary()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        DefaultComboBoxModel<PSquare> model = (DefaultComboBoxModel<PSquare>)mTiles.getModel();
        model.removeAllElements();
        if (es != null)
        {
            List<PSquare> squares = EditorSquareLogic.getSquares();
            for (PSquare tile : squares)
                model.addElement(tile);
            if (squares.size() > 0)
                model.setSelectedItem(squares.iterator().next());
        }
    }
}
