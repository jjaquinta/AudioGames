package jo.audio.companions.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import jo.audio.common.data.SLDataBean;
import jo.audio.common.logic.CommonIOLogic;
import jo.audio.companions.data.CompUserBean;

public class RanksLogic
{
    public static final long RANKS_TIMEOUT = 1*60*60*1000L; 
    private static SLDataBean mRanks = null;
    private static long mLastLookup = 0L;
    
    public static Map<String,Integer> getRanks(CompUserBean user)
    {
        long now = System.currentTimeMillis();
        long elapsed = now - mLastLookup;
        if ((elapsed > RANKS_TIMEOUT) || (mRanks == null))
        {
            mRanks = CommonIOLogic.getDataFromURI("sixswords://rankings");
            if (mRanks == null)
            {
                mRanks = new SLDataBean();
                mRanks.setKey("sixswords://rankings");
                CommonIOLogic.saveData(mRanks);
            }
            mLastLookup = now;
        }
        Map<String, Integer> ranks = new HashMap<>();
        for (String key : mRanks.getSecondaryValues().keySet())
        {
            String val  = mRanks.getSecondaryValues().get(key);
            if (val.indexOf(user.getURI()) < 0)                
                continue;
            int rank = 1;
            for (StringTokenizer st = new StringTokenizer(val, ";"); st.hasMoreTokens(); )
                if (st.nextToken().equals(user.getURI()))
                {
                    ranks.put(key, rank);
                    break;
                }
                else
                    rank++;
        }
        return ranks;
    }
}
