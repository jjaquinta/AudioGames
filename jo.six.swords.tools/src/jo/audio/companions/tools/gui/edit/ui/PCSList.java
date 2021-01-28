package jo.audio.companions.tools.gui.edit.ui;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.function.Consumer;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jo.util.beans.PCSBean;

@SuppressWarnings("serial")
public class PCSList<E> extends JList<E>
{
    @SuppressWarnings("unchecked")
    public PCSList(PCSBean itemsRoot, String itemsProps, PCSBean selectedRoot, String selectedProps, Consumer<E> onSet)
    {
        setModel(new DefaultListModel<>());
        if (onSet != null)
            addListSelectionListener(new ListSelectionListener() {                
                @Override
                public void valueChanged(ListSelectionEvent e)
                {
                    onSet.accept(getSelectedValue());
                }
            });
        new ComplexPropertyChangeListener(itemsRoot, itemsProps) {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                Collection<E> items = (Collection<E>)evt.getNewValue();
                DefaultListModel<E> model = (DefaultListModel<E>)getModel();
                Object oldSelection = getSelectedValue();
                model.removeAllElements();
                for (E item : items)
                    model.addElement(item);
                setSelectedValue(oldSelection, true);
            }
        };
        new ComplexPropertyChangeListener(selectedRoot, selectedProps) {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                Object item = evt.getNewValue();
                setSelectedValue(item, true);
            }
        };
    }
}
