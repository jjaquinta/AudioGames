package jo.audio.companions.data;

import java.util.Random;

public interface IAttack
{
    public boolean hasParam(String param);
    public String getParam(String param);
    public int rollDamage(CompContextBean context, Random rnd, ICombatant target);
    public int bonusToHit(ICombatant target);
    public int getMagic();
    public String getName();
    public void effect(FightDetails fight, ICombatant target);
}
