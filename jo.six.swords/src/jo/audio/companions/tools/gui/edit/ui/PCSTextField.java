package jo.audio.companions.tools.gui.edit.ui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.util.function.Consumer;

import javax.swing.JTextField;

import jo.util.beans.PCSBean;

@SuppressWarnings("serial")
public class PCSTextField extends JTextField
{
    public PCSTextField(PCSBean root, String props, Consumer<String> focusLost)
    {
        if (focusLost != null)
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e)
                {                    
                    focusLost.accept(getText());
                }
            });
        new ComplexPropertyChangeListener(root, props) {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                String txt = (String)evt.getNewValue();
                setText(txt);
            }
        };
    }
}
