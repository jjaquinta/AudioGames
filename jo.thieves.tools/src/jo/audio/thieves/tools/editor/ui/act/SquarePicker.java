package jo.audio.thieves.tools.editor.ui.act;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.audio.thieves.tools.editor.logic.EditorSquareLogic;
import jo.util.ui.swing.utils.ListenerUtils;

public class SquarePicker extends JComponent
{
    private JComboBox<PSquare> mSquares;

    public SquarePicker()
    {
        initInstantiate();
        initLayout();
        initLink();
        doNewLibrary();
    }

    private void initInstantiate()
    {
        mSquares = new JComboBox<>();
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", mSquares);
    }

    private void initLink()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        es.listen("library", (ov,nv) -> doNewLibrary());
        es.listen("selectedSquare", (ov,nv) -> doNewDataSquare());
        ListenerUtils.listen(mSquares, (e) -> doNewUISquare());
    }
    
    private void doNewUISquare()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PSquare uitile = (PSquare)mSquares.getSelectedItem();
        PSquare datatile = es.getSelectedSquare();
        if (!PSquare.equals(uitile, datatile))
            es.setSelectedSquare(uitile);
    }
    
    private void doNewDataSquare()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        PSquare uitile = (PSquare)mSquares.getSelectedItem();
        PSquare datatile = es.getSelectedSquare();
        if (!PSquare.equals(uitile, datatile))
            mSquares.setSelectedItem(datatile);
    }

    private void doNewLibrary()
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        DefaultComboBoxModel<PSquare> model = (DefaultComboBoxModel<PSquare>)mSquares.getModel();
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
