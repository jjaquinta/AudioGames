package jo.audio.util.model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntentDefBean
{
    private String          mIntent;
    private List<SlotBean>  mSlots;
    private Map<String,List<UtteranceBean>> mUtterances;
    private Map<String,List<String>> mExamples;
    private Map<String,List<String>> mWorkedExamples;
    private String mTarget;
    private String mTargetLang;
    private String mContext;

    public IntentDefBean()
    {
        mSlots = new ArrayList<SlotBean>();
        mUtterances = new HashMap<>();
        mExamples = new HashMap<>();
        mWorkedExamples = new HashMap<>();
    }
    
    // utilities
    
    @Override
    public String toString()
    {
        return mIntent;
    }
    
    public void addUtterance(String lang, UtteranceBean utterance)
    {
        List<UtteranceBean> utts = mUtterances.get(lang);
        if (utts == null)
        {
            utts = new ArrayList<>();
            mUtterances.put(lang, utts);
        }
        utts.add(utterance);
    }
    
    public void addExample(String lang, String example)
    {
        List<String> exs = mExamples.get(lang);
        if (exs == null)
        {
            exs = new ArrayList<>();
            mExamples.put(lang, exs);
        }
        exs.add(example);
    }

    public List<String> getExamples(String lang)
    {
        List<String> examples = null;
        examples = mExamples.get(lang);
        if (examples == null)
        {
            examples = mExamples.get(lang.substring(0, 2));
            if (examples == null)
                examples = mExamples.get("en");
        }
        return examples;
    }

    public List<UtteranceBean> getUtterances(String lang)
    {
        List<UtteranceBean> utterances = null;
        utterances = mUtterances.get(lang);
        if (utterances == null)
        {
            utterances = mUtterances.get(lang.substring(0, 2));
            if (utterances == null)
                utterances = mUtterances.get("en");
        }
        return utterances;
    }
    
    public boolean isTargettedAt(int originator)
    {
        if (mTarget == null)
            return true;
        switch (originator)
        {
            case AudioRequestBean.ALEXA:
                return mTarget.toLowerCase().indexOf("alexa") >= 0;
            case AudioRequestBean.GOOGLE:
                return mTarget.toLowerCase().indexOf("google") >= 0;
            case AudioRequestBean.APIAI:
                return mTarget.toLowerCase().indexOf("apiai") >= 0;
        }
        return false;
    }
    
    public boolean isTargettedAtLang(String lang)
    {
        if (mTargetLang == null)
            return true;
        return mTargetLang.toLowerCase().indexOf(lang.toLowerCase()) >= 0;
    }
    
    // getters and setters
    
    public String getIntent()
    {
        return mIntent;
    }
    public void setIntent(String intent)
    {
        mIntent = intent;
    }
    public List<SlotBean> getSlots()
    {
        return mSlots;
    }
    public void setSlots(List<SlotBean> slots)
    {
        mSlots = slots;
    }

    public Map<String,List<UtteranceBean>> getUtterances()
    {
        return mUtterances;
    }

    public void setUtterances(Map<String,List<UtteranceBean>> utterances)
    {
        mUtterances = utterances;
    }

    public Map<String,List<String>> getExamples()
    {
        return mExamples;
    }

    public void setExamples(Map<String,List<String>> examples)
    {
        mExamples = examples;
    }

    public Map<String, List<String>> getWorkedExamples()
    {
        return mWorkedExamples;
    }

    public void setWorkedExamples(Map<String, List<String>> workedExamples)
    {
        mWorkedExamples = workedExamples;
    }

    public String getTarget()
    {
        return mTarget;
    }

    public void setTarget(String target)
    {
        mTarget = target;
    }

    public String getTargetLang()
    {
        return mTargetLang;
    }

    public void setTargetLang(String targetLang)
    {
        mTargetLang = targetLang;
    }

    public String getContext()
    {
        return mContext;
    }

    public void setContext(String context)
    {
        mContext = context;
    }
}
