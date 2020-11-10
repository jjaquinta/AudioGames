package jo.audio.companions.logic.feature.dungeon;

import jo.audio.companions.data.CompContextBean;

public abstract class DungeonResult
{
    private int mChance;
    
    public DungeonResult(int chance)
    {
        mChance = chance;
    }
    
    public abstract void run(CompContextBean context);

    public int getChance()
    {
        return mChance;
    }

    public void setChance(int chance)
    {
        mChance = chance;
    }
    
}
