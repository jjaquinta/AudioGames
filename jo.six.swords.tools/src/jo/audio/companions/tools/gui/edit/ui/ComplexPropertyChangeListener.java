package jo.audio.companions.tools.gui.edit.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.StringTokenizer;

import jo.util.beans.PCSBean;
import jo.util.utils.BeanUtils;

public abstract class ComplexPropertyChangeListener implements PropertyChangeListener
{
    private PCSBean[]                mBeans;
    private String[]                 mProperties;
    private PropertyChangeListener   mListener;
    
    public ComplexPropertyChangeListener(PCSBean root, String props)
    {
        StringTokenizer st = new StringTokenizer(props, ".");
        mBeans = new PCSBean[st.countTokens()];
        mProperties = new String[mBeans.length];
        for (int i = 0; i < mProperties.length; i++)
            mProperties[i] = st.nextToken();
        mListener = new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {                
                change((PCSBean)evt.getSource(), evt.getOldValue(), evt.getNewValue());
            }
        };
        mBeans[0] = root;
        mBeans[0].addPropertyChangeListener(mProperties[0], mListener);
        change(root, null, BeanUtils.get(root, mProperties[0]));
    }
    
    private void change(PCSBean bean, Object oldValue, Object newValue)
    {
        int idx = 0;
        while (idx < mBeans.length)
            if (mBeans[idx] == bean)
                break;
            else
                idx++;
        if (idx == mBeans.length)
            throw new IllegalStateException();
        if (idx == mBeans.length - 1)
        {   // data value
            if (oldValue != newValue)
                propertyChange(new PropertyChangeEvent(mBeans[idx], mProperties[idx], oldValue, newValue));
        }
        else
        {   // up the chain
            Object ov = null;
            if (mBeans[idx+1] != null)
            {
                mBeans[idx+1].removePropertyChangeListener(mListener);
                ov = BeanUtils.get(mBeans[idx+1], mProperties[idx+1]);
            }
            mBeans[idx+1] = (PCSBean)newValue;
            if (mBeans[idx+1] != null)
            {
                mBeans[idx+1].addPropertyChangeListener(mProperties[idx+1], mListener);
                change(mBeans[idx+1], ov, BeanUtils.get(mBeans[idx+1], mProperties[idx+1]));
            }
        }
    }
}
