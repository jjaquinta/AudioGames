package jo.audio.companions.tools.gui.edit.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.function.Consumer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import jo.util.beans.PCSBean;

@SuppressWarnings("serial")
public class PCSComboBox<E> extends JComboBox<E>
{
    private boolean mChanging = false;
    
    @SuppressWarnings("unchecked")
    public PCSComboBox(PCSBean itemsRoot, String itemsProps, PCSBean selectedRoot, String selectedProps, Consumer<E> onSet)
    {
        setModel(new DefaultComboBoxModel<>());
        new ComplexPropertyChangeListener(itemsRoot, itemsProps) {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                mChanging = true;
                Collection<E> items = (Collection<E>)evt.getNewValue();
                DefaultComboBoxModel<E> model = (DefaultComboBoxModel<E>)getModel();
                //Object oldSelection = model.getSelectedItem();
                model.removeAllElements();
                for (E item : items)
                    model.addElement(item);
                //model.setSelectedItem(oldSelection);
                mChanging = false;
            }
        };
        new ComplexPropertyChangeListener(selectedRoot, selectedProps) {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                Object item = evt.getNewValue();
                DefaultComboBoxModel<E> model = (DefaultComboBoxModel<E>)getModel();
                model.setSelectedItem(item);
            }
        };
        if (onSet != null)
            addActionListener(new ActionListener() {                
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (mChanging)
                        return;
                    onSet.accept((E)getSelectedItem());
                }
            });
    }
}
