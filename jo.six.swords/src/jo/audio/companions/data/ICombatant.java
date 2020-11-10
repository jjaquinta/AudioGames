package jo.audio.companions.data;

import java.util.List;

public interface ICombatant
{
    public String getLogName();
    public boolean isEffect(String type);
    public int getCurrentHitPoints();
    public void setCurrentHitPoints(int hitPoints);
    public IAttack[] getAttacks(CompContextBean context, boolean firstRank, List<ICombatant> targets);
    public int getLimitedAttackUse();
    public void setLimitedAttackUse(int limitedAttackUse);
    public int getAC();
    public int getTHAC(int ac);
    public int getEffectiveLevel();
    public float getWidth();
    public boolean isLessThanOneHD();
    public String getPhylum();
    public String getSize();
}

