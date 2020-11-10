package jo.audio.util.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UtteranceBean implements Comparable<UtteranceBean>
{
    private IntentDefBean              mIntent;
    private List<PhraseSegmentBean> mPhrase;
    private String mRawUtterance;
    private Properties mTags = new Properties();
    
    public UtteranceBean()
    {
        mPhrase = new ArrayList<PhraseSegmentBean>();
    }
    
    public int compareTo(UtteranceBean o2) 
    {
        String i1 = getIntent().getIntent();
        String i2 = o2.getIntent().getIntent();
        int cmp = i1.compareTo(i2);
        if (cmp != 0)
            return cmp;
        int ph1 = getPhrase().size();
        int ph2 = o2.getPhrase().size();
        if (ph1 != ph2)
            return ph1 - ph2;
        PhraseSegmentBean seg1 = getPhrase().get(0);
        PhraseSegmentBean seg2 = o2.getPhrase().get(0);
        return seg1.compareTo(seg2);
    }
    
    @Override
    public String toString()
    {
        StringBuffer txt = new StringBuffer(mIntent.getIntent());
        txt.append(":");
        for (PhraseSegmentBean p : mPhrase)
        {
            txt.append(" ");
            txt.append(p.toString());
        }
        return txt.toString();
    }
    
    public IntentDefBean getIntent()
    {
        return mIntent;
    }
    public void setIntent(IntentDefBean intent)
    {
        mIntent = intent;
    }
    public List<PhraseSegmentBean> getPhrase()
    {
        return mPhrase;
    }
    public void setPhrase(List<PhraseSegmentBean> phrase)
    {
        mPhrase = phrase;
    }

    public String getRawUtterance()
    {
        return mRawUtterance;
    }

    public void setRawUtterance(String rawUtterance)
    {
        mRawUtterance = rawUtterance;
    }

    public Properties getTags()
    {
        return mTags;
    }

    public void setTags(Properties tags)
    {
        mTags = tags;
    }
}
