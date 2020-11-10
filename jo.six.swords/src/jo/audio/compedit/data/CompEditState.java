package jo.audio.compedit.data;

import java.util.List;
import java.util.StringTokenizer;

import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.data.IntentDefBean;
import jo.util.utils.obj.LongUtils;

public class CompEditState extends BaseUserState
{
    public static final int STATE_BASE = 0;

    private int         mInteractions;
    private List<AudioMessageBean> mLastMessages;
    private CompEditContextBean mContext;
    private String      mMoreIntent;
    private int         mMoreDepth;

    // utility functions
    public void prompts(String... intents)
    {
        for (String intent : intents)
        {
            IntentDefBean def = getApplication().getModel().getIntent(intent);
            if (def == null)
                continue;
            String example = getApplication().pickExample(getRequest().getLanguage(), intent);
            if (prompt(intent, example))
                break;
        }
    }
    public boolean prompt(String intent, String key, Object... args)
    {
        int freq = 0;
        long now = System.currentTimeMillis();
        for (StringTokenizer st = new StringTokenizer(intent, ","); st.hasMoreTokens(); )
        {
            String i = st.nextToken();
            long elapsed = now - LongUtils.parseLong(mContext.getUser().getIntentTimestamp().get(i));
            if (elapsed < 5*60*1000L)
                return false;
            freq += incrementFrequency(i);
        }
        int roll = BaseUserState.RND.nextInt(freq + 1);
        if (roll == 0)
        {
            respond(key, args);
            return true;
        }
        return false;
    }

    private int incrementFrequency(String intent)
    {
        if (mContext == null)
            return 0;
        if (mContext.getUser() == null)
            return 0;
        int val = 0;
        if (mContext.getUser().getIntentFrequency().containsKey(intent))
            val = mContext.getUser().getIntentFrequency().get(intent).intValue();
        mContext.getUser().getIntentFrequency().put(intent, val + 1L);
        mContext.getUser().getIntentTimestamp().put(intent, System.currentTimeMillis());
        return val;
    }
    
    // setters and getters
    public int getInteractions()
    {
        return mInteractions;
    }

    public void setInteractions(int interactions)
    {
        mInteractions = interactions;
    }

    public CompEditUserBean getUser()
    {
        if (mContext == null)
            return null;
        return mContext.getUser();
    }

    public CompEditContextBean getContext()
    {
        return mContext;
    }

    public void setContext(CompEditContextBean coreContext)
    {
        mContext = coreContext;
    }

    public List<AudioMessageBean> getLastMessages()
    {
        return mLastMessages;
    }

    public void setLastMessages(List<AudioMessageBean> lastMessages)
    {
        mLastMessages = lastMessages;
    }

    public String getMoreIntent()
    {
        return mMoreIntent;
    }

    public void setMoreIntent(String moreIntent)
    {
        mMoreIntent = moreIntent;
    }

    public int getMoreDepth()
    {
        return mMoreDepth;
    }

    public void setMoreDepth(int moreDepth)
    {
        mMoreDepth = moreDepth;
    }
}
