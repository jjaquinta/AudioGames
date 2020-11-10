package jo.audio.companions.logic.feature.dungeon;

import jo.audio.companions.data.CompContextBean;
import jo.audio.util.BaseUserState;

public class DungeonResults
{
    private int mTotal;
    private DungeonResult[] mResults;
    
    public DungeonResults(DungeonResult[] results)
    {
        mResults = results;
        mTotal = 0;
        for (DungeonResult result : mResults)
            mTotal += result.getChance();
    }
    
    public void action(CompContextBean context)
    {
        int roll = BaseUserState.RND.nextInt(mTotal);
        for (DungeonResult r : mResults)
        {
            roll -= r.getChance();
            if (roll < 0)
            {
                r.run(context);
                break;
            }
        }
    }
}
