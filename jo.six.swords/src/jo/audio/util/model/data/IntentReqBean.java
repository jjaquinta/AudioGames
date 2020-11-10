package jo.audio.util.model.data;

import java.util.Properties;

public class IntentReqBean
{
    private String  mIntentID;
    private Properties mSlots = new Properties();
    private float mConfidence;
    
    // utilities
    public String getSlot(String id)
    {
        return mSlots.getProperty(id);
    }
    
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(mIntentID);
        if (mSlots.size() > 0)
        {
            sb.append(" [");
            for (Object key : mSlots.keySet())
                sb.append(key+"='"+mSlots.get(key)+"', ");
            sb.setLength(sb.length() - 2);
            sb.append("]");
        }
        return sb.toString();
    }
    
    // getters and setters
    public String getIntentID()
    {
        return mIntentID;
    }
    public void setIntentID(String intentID)
    {
        mIntentID = intentID;
    }
    public Properties getSlots()
    {
        return mSlots;
    }
    public void setSlots(Properties slots)
    {
        mSlots = slots;
    }
    public float getConfidence()
    {
        return mConfidence;
    }
    public void setConfidence(float confidence)
    {
        mConfidence = confidence;
    }
}
