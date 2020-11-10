package jo.audio.companions.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.audio.companions.logic.CompIOLogic;
import jo.audio.companions.logic.CompanionLogic;
import jo.audio.companions.logic.ExperienceLogic;
import jo.audio.companions.logic.UserLogic;
import jo.audio.util.ResponseUtils;

public class FightDetails
{
    public CompContextBean                                 context;
    public List<ICombatant>                         playerFirstRank   = new ArrayList<>();
    public List<ICombatant>                         playerSecondRank  = new ArrayList<>();
    public List<ICombatant>                   monsterFirstRank  = new ArrayList<>();
    public List<ICombatant>                   monsterSecondRank = new ArrayList<>();
    public Map<ICombatant, ICombatant> monsterTargets    = new HashMap<>();
    public List<Object>                                    responses = new ArrayList<>();

    
    public void miss(ICombatant attacker, ICombatant target, IAttack attack)
    {
        Attack att = new Attack();
        att.playerAttack = (attacker instanceof CompCompanionBean);
        att.attacker = attacker;
        att.target = target;
        att.weapon = attack;
        att.hit = false;
        responses.add(att);
    }

    public void kill(ICombatant attacker, ICombatant target, IAttack attack, int damage)
    {
        Attack att = new Attack();
        att.playerAttack = (attacker instanceof CompCompanionBean);
        att.attacker = attacker;
        att.target = target;
        att.weapon = attack;
        att.hit = true;
        att.damage = damage;
        att.knockout = true;
        responses.add(att);
        if ((attacker instanceof CompCompanionBean) && (target instanceof CompMonsterInstanceBean))
        {
            int xp = ExperienceLogic.addXP(this.responses, (CompCompanionBean)attacker, (CompMonsterInstanceBean)target, damage + target.getCurrentHitPoints());
            this.context.getUser().getEncounter().setXP(xp + this.context.getUser().getEncounter().getXP());
            this.context.getUser().setKillList(ResponseUtils.addToList(this.context.getUser().getKillList(), ((CompMonsterInstanceBean)target).getType().getName()));
            this.context.getUser().setTotalKills(this.context.getUser().getTotalKills() + 1);
            CompIOLogic.logKill(this.context.getUser(), (CompMonsterInstanceBean)target, attack.getName(), xp);
        }
        if (target instanceof CompCompanionBean)
            playerKilled((CompCompanionBean)target);
    }
    public void hit(ICombatant attacker, ICombatant target, IAttack attack, int damage)
    {
        Attack att = new Attack();
        att.playerAttack = (attacker instanceof CompCompanionBean);
        att.attacker = attacker;
        att.target = target;
        att.weapon = attack;
        att.hit = true;
        att.damage = damage;
        responses.add(att);
        if ((attacker instanceof CompCompanionBean) && (target instanceof CompMonsterInstanceBean))
        {
            int xp = ExperienceLogic.addXP(this.responses, (CompCompanionBean)attacker, (CompMonsterInstanceBean)target, damage);
            this.context.getUser().getEncounter().setXP(xp + this.context.getUser().getEncounter().getXP());
        }
    }
    public void knockedOut(ICombatant attacker, ICombatant target, IAttack attack, int damage)
    {
        Attack att = new Attack();
        att.playerAttack = (attacker instanceof CompCompanionBean);
        att.attacker = attacker;
        att.target = target;
        att.weapon = attack;
        att.hit = true;
        att.damage = damage;
        att.knockout = true;
        responses.add(att);
        if (target instanceof CompCompanionBean)
            playerKnockedOut((CompCompanionBean)target);
    }

    public void allyKilled(ICombatant target)
    {
        if ((target instanceof CompCompanionBean))
            playerKilled((CompCompanionBean)target);
        this.playerFirstRank.remove(target);
        this.playerSecondRank.remove(target);
    }

    public static void doPlayerKilled(CompContextBean context, CompCompanionBean target)
    {
        context.getUser().getCompanions().remove(target);
        context.getUser().getReallyDeadCompanions().add(0, target);
        for (CompItemInstanceBean item : target.getItems()
                .toArray(new CompItemInstanceBean[0]))
        {
            String id = item.getID();
            int q = item.getQuantity();
            CompanionLogic.doRemoveItem(target, id, q);
            UserLogic.doAddItem(context.getUser(), id, q);
        }
        if (context.getUser().getActiveCompanion()
                .equals(target.getID()))
            if (context.getUser().getCompanions().size() > 0)
                context.getUser().setActiveCompanion(context
                        .getUser().getCompanions().get(0).getID());
            else
                context.getUser().setActiveCompanion(null);
    }

    private void playerKilled(CompCompanionBean target)
    {
        doPlayerKilled(this.context, target);
    }
    
    public static void doPlayerKnockedOut(CompContextBean context, CompCompanionBean target)
    {
        context.getUser().getCompanions().remove(target);
        context.getUser().getDeadCompanions().add(target);
//        for (CompItemInstanceBean item : target.getItems()
//                .toArray(new CompItemInstanceBean[0]))
//        {
//            String id = item.getID();
//            int q = item.getQuantity();
//            CompanionLogic.doRemoveItem(target, id, q);
//            UserLogic.doAddItem(this.context.getUser(), id, q);
//        }
        if (target.getID().equals(context.getUser().getActiveCompanion()
                ))
            if (context.getUser().getCompanions().size() > 0)
                context.getUser().setActiveCompanion(context
                        .getUser().getCompanions().get(0).getID());
            else
                context.getUser().setActiveCompanion(null);
    }
    
    private void playerKnockedOut(CompCompanionBean target)
    {
        doPlayerKnockedOut(this.context, target);
        this.playerFirstRank.remove(target);
        this.playerSecondRank.remove(target);
    }

    public class Attack
    {
        public boolean                 playerAttack;
        public ICombatant              attacker;
        public ICombatant              target;
        public boolean                 hit;
        public IAttack                 weapon;
        public int                     damage;
        public boolean                 knockout;
        public boolean                 kill;
        
        public boolean isMonsterHitNotKill()
        {
            return (!playerAttack) && hit && (!knockout && !kill);
        }
        
        public boolean isPlayerKill()
        {
            return playerAttack && (knockout || kill);
        }
        
        @Override
        public String toString()
        {
            return "[playerAttack="+playerAttack+", attacker="+attacker.getLogName()+", target="+target.getLogName()+", hit="+hit+", weapon="+weapon+", damage="+damage+", kill="+knockout+"]";
        }

        public void incrementTargetStrikes(
                Map<ICombatant, Integer> cumulativeHits)
        {
            Integer hits = cumulativeHits.get(target);
            if (hits == null)
                hits = 1;
            else
                hits = hits + 1;
            cumulativeHits.put(target, hits);
        }

        public void incrementTargetDamage(
                Map<ICombatant, Integer> cumulativeHits)
        {
            Integer hits = cumulativeHits.get(target);
            if (hits == null)
                hits = 1;
            else
                hits += 1;
            cumulativeHits.put(target, hits);
        }
    }
}

