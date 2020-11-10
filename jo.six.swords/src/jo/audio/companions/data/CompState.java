package jo.audio.companions.data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jo.audio.companions.logic.CompConstLogic;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.LongUtils;

public class CompState extends BaseUserState
{
    public static final int STATE_BASE = 0;
    public static final int STATE_COMBAT = 1;
    public static final int STATE_QUESTION = 2;
    public static final int STATE_DIRECTION = 3;

    private int         mTutorial;
    private List<AudioMessageBean> mLastMessages;
    private CompContextBean mContext;
    private String      mMoreIntent;
    private int         mMoreDepth;
    private List<List<AudioMessageBean>>    mMore;
    private long        mLastDangerReport;
    private long        mLastTheyAttackSound;

    // utility functions
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

    public boolean isFlag(String fname)
    {
//        String flags = null;
//        if (getRequest().getHttpRequest() != null)
//            flags = getRequest().getHttpRequest().getParameter("flags");
//        if (flags == null)
            return false;
//        return flags.toLowerCase().indexOf(fname.toLowerCase()) >= 0;
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
    
    @SuppressWarnings("unchecked")
    public void setMore(List<AudioMessageBean>... lists)
    {
        mMore = new ArrayList<>();
        for (List<AudioMessageBean> list : lists)
            mMore.add(list);
    }
    
    public void setMore(AudioMessageBean... msgs)
    {
        mMore = new ArrayList<>();
        for (AudioMessageBean msg : msgs)
        {
            List<AudioMessageBean> list = new ArrayList<>();
            list.add(msg);
            mMore.add(list);
        }
    }
    
    public void setMore(String... msgs)
    {
        mMore = new ArrayList<>();
        for (String msg : msgs)
        {
            List<AudioMessageBean> list = new ArrayList<>();
            list.add(new AudioMessageBean(msg));
            mMore.add(list);
        }
    }
    
    public void addMore(AudioMessageBean msg)
    {
        if (mMore == null)
            mMore = new ArrayList<>();
        List<AudioMessageBean> list = new ArrayList<>();
        list.add(msg);
        mMore.add(list);
    }
    
    public void addMore(List<AudioMessageBean> list)
    {
        if (mMore == null)
            mMore = new ArrayList<>();
        mMore.add(list);
    }
    
    public boolean isSubscriber()
    {
        String flags = getContext().getLastOperation().getFlags();
        DebugUtils.trace("Flags='"+flags+"'");
        return (flags.indexOf(CompConstLogic.PREMIUM_SUBSCRIPTION_ID1) >= 0)
                || (flags.indexOf(CompConstLogic.PREMIUM_SUBSCRIPTION_ID2) >= 0);
    }
    
    public boolean isCirrane()
    {
        String flags = getContext().getLastOperation().getFlags();
        return flags.toLowerCase().indexOf("cirrane") >= 0;
    }
    
    public boolean isIreland()
    {
        return isFlag("irl");
    }
    
    public boolean isIceland()
    {
        return isFlag("ice");
    }
    
    // setters and getters
    public CompUserBean getUser()
    {
        if (mContext == null)
            return null;
        return mContext.getUser();
    }

    public CompContextBean getContext()
    {
        return mContext;
    }

    public void setContext(CompContextBean coreContext)
    {
        mContext = coreContext;
    }

    public int getTutorial()
    {
        return mTutorial;
    }

    public void setTutorial(int tutorial)
    {
        mTutorial = tutorial;
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

    public long getLastDangerReport()
    {
        return mLastDangerReport;
    }

    public void setLastDangerReport(long lastDangerReport)
    {
        mLastDangerReport = lastDangerReport;
    }

    public long getLastTheyAttackSound()
    {
        return mLastTheyAttackSound;
    }

    public void setLastTheyAttackSound(long lastTheyAttackSound)
    {
        mLastTheyAttackSound = lastTheyAttackSound;
    }

    public List<List<AudioMessageBean>> getMore()
    {
        return mMore;
    }

    public void setMore(List<List<AudioMessageBean>> more)
    {
        mMore = more;
    }

}
