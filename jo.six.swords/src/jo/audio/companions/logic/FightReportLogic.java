package jo.audio.companions.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompEncounterBean;
import jo.audio.companions.data.FightDetails;
import jo.audio.companions.data.FightDetails.Attack;
import jo.audio.companions.data.ICombatant;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;

public class FightReportLogic
{
    public static void reportOnFight(FightDetails fight)
    {
        log("Reporting on fight: "+fight.responses.size()+" entries");
        reportConsecutiveHits(fight);
        reportConsecutiveKills(fight);
        reportSequentialKills(fight);
        reportConsecutiveMisses(fight);
        reportTrailingMisses(fight);
        dumpResponses(fight);
        reportConsecutiveMonsters(fight);
        dumpResponses(fight);
        reportConsecutiveAny(fight);
        reportOnRemainder(fight);
        log("Condenses to: "+fight.responses.size()+" entries");
        for (Object resp : fight.responses)
            if (resp instanceof AudioMessageBean)
                fight.context.addMessage((AudioMessageBean)resp);
            else
                log("Unknown response: "+resp);
    }

    private static void dumpResponses(FightDetails fight)
    {
        log("Responses:");
        for (int i = 0; i < fight.responses.size(); i++)
            log(i+": "+fight.responses.get(i));
    }
    
    private static Function<Object,Boolean> firstConsecutive = (resp) -> { 
        if (resp instanceof AudioMessageBean)
            return false;
        FightDetails.Attack att = (FightDetails.Attack)resp;
        if (!att.playerAttack)
            return false;
        if (att.hit)
            return false;
        return true;
    };
    private static BiFunction<Object,Object,Boolean> secondConsecutiveSame = (first, resp) -> { 
        if (resp instanceof AudioMessageBean)
            return false;
        FightDetails.Attack att = (FightDetails.Attack)resp;
        if (!att.playerAttack)
            return false;
        if (att.hit)
            return false;
        FightDetails.Attack firstAtt = (FightDetails.Attack)first;
        if (!firstAtt.attacker.getLogName().equals(att.attacker.getLogName()))
            return false;
        if (!firstAtt.target.getLogName().equals(att.target.getLogName()))
            return false;
        return true;
    }; 
    private static BiFunction<Object,Object,Boolean> secondConsecutiveDifferent = (first, resp) -> { 
        if (resp instanceof AudioMessageBean)
            return false;
        FightDetails.Attack att = (FightDetails.Attack)resp;
        if (!att.playerAttack)
            return false;
        if (att.hit)
            return false;
        FightDetails.Attack firstAtt = (FightDetails.Attack)first;
        if (!firstAtt.target.getLogName().equals(att.target.getLogName()))
            return false;
        return true;
    }; 
    
    private static void reportConsecutiveMisses(FightDetails fight)
    {
        List<int[]> sequences = findConsecutive(fight, firstConsecutive, secondConsecutiveSame);
        for (int i = sequences.size() - 1; i >= 0; i--)
        {
            int[] sequence = sequences.get(i);
            FightDetails.Attack att = (FightDetails.Attack)fight.responses.get(sequence[0]);
            for (int j = sequence[1] - 1; j >= sequence[0]; j--)
                fight.responses.remove(j);
            int times = sequence[1] - sequence[0];
            if (times == 2)
                fight.responses.add(sequence[0], new AudioMessageBean(CompanionsModelConst.TEXT_XXX_MISSES_YYY_TWICE, 
                        att.attacker.getLogName(), att.target.getLogName()));
            else
                fight.responses.add(sequence[0], new AudioMessageBean(CompanionsModelConst.TEXT_XXX_MISSES_YYY_ZZZ_TIMES, 
                        att.attacker.getLogName(), att.target.getLogName(), times));
        }
        sequences = findConsecutive(fight, firstConsecutive, secondConsecutiveDifferent);
        for (int i = sequences.size() - 1; i >= 0; i--)
        {
            int[] sequence = sequences.get(i);
            List<String> attackerNames = new ArrayList<>();
            String defenderName = null;
            for (int j = sequence[1] - 1; j >= sequence[0]; j--)
            {
                FightDetails.Attack att = (FightDetails.Attack)fight.responses.get(j);
                fight.responses.remove(j);
                attackerNames.add(0, att.attacker.getLogName());
                defenderName = att.target.getLogName();
            }
            if (attackerNames.size() == 2)
                fight.responses.add(sequence[0], new AudioMessageBean(CompanionsModelConst.TEXT_XXX_BOTH_MISS_YYY, 
                        AudioMessageBean.and(attackerNames), defenderName));
            else
                fight.responses.add(sequence[0], new AudioMessageBean(CompanionsModelConst.TEXT_XXX_ALL_MISS_YYY, 
                    AudioMessageBean.and(attackerNames), defenderName));
        }
    }
    
    private static void reportConsecutiveAny(FightDetails fight)
    {
        //log("reportConsecutiveAny");
        List<FightDetails.Attack> stream = new ArrayList<FightDetails.Attack>();
        for (int i  = 0; i < fight.responses.size() - 1; i++)
        {
            Object resp = fight.responses.get(i);
            if (resp instanceof AudioMessageBean)
                continue;
            FightDetails.Attack att = (FightDetails.Attack)resp;
            if (!att.playerAttack)
                break;
            if (!(fight.responses.get(i + 1) instanceof FightDetails.Attack))
                continue;
            FightDetails.Attack next = (FightDetails.Attack)fight.responses.get(i + 1);
            if (att.attacker != next.attacker)
                continue;
            if (!next.playerAttack)
                continue;
            //log(i+" and "+(i+1)+" for "+att.companion.getName());
            stream.clear();
            stream.add(att);
            stream.add(next);
            int end = i + 2;
            while (end < fight.responses.size() - 1)
            {
                if (!(fight.responses.get(end) instanceof FightDetails.Attack))
                    break;
                next = (FightDetails.Attack)fight.responses.get(end);
                if (att.attacker != next.attacker)
                    break;
                if (!next.playerAttack)
                    break;
                //log(" adding in "+end);
                stream.add(next);
                end++;
            }
            //log("Removing "+stream.size()+" messages from the responses");
            for (int j = 0; j < stream.size(); j++)
                fight.responses.remove(i);
            //log("Adding in primary at "+i+".");
            if (!att.hit)
                fight.responses.add(i, constructPlayerMiss(fight.context.getUser().getEncounter(), att));
            else
                if (!att.knockout)
                    fight.responses.add(i, constructPlayerHit(fight.context.getUser().getEncounter(), null, att));
                else
                    fight.responses.add(i, constructPlayerKill(att));
            for (int j = 1; j < stream.size(); j++)
            {
                next = stream.get(j);
                //log("Adding in secondary at "+(i+1)+".");
                if (j == stream.size() - 1)
                {
                    if (!next.hit)
                        fight.responses.add(++i, new AudioMessageBean(CompanionsModelConst.TEXT_TERMINAL_MISSES));
                    else
                        if (next.knockout)
                            fight.responses.add(++i, new AudioMessageBean(CompanionsModelConst.TEXT_TERMINAL_KILLS));
                        else
                            fight.responses.add(++i, new AudioMessageBean(CompanionsModelConst.TEXT_TERMINAL_HITS_FOR_XXX_DAMAGE, next.damage));
                }
                else
                {
                    if (!next.hit)
                        fight.responses.add(++i, new AudioMessageBean(CompanionsModelConst.TEXT_SEQUENTIAL_MISSES));
                    else
                        if (next.knockout)
                            fight.responses.add(++i, new AudioMessageBean(CompanionsModelConst.TEXT_SEQUENTIAL_KILLS));
                        else
                            fight.responses.add(++i, new AudioMessageBean(CompanionsModelConst.TEXT_SEQUENTIAL_HITS_FOR_XXX_DAMAGE, next.damage));
                }
            }
            //log("Moving on to next.");
        }
    }
    
    private static void reportConsecutiveMonsters(FightDetails fight)
    {
        log("reportConsecutiveMonsters");
        for (int i  = 0; i < fight.responses.size() - 1; i++)
        {
            Object resp = fight.responses.get(i);
            if (resp instanceof AudioMessageBean)
                continue;
            FightDetails.Attack att = (FightDetails.Attack)resp;
            if (!att.isMonsterHitNotKill())
                continue;
            if (fight.responses.get(i + 1) instanceof AudioMessageBean)
                continue;
            FightDetails.Attack next = (FightDetails.Attack)fight.responses.get(i + 1);
            if (!next.isMonsterHitNotKill())
            {
                log((i+1)+" is not monster hit not kill");
                continue;
            }
            if (!att.attacker.getLogName().equals(next.attacker.getLogName()))
            {
                log((i+1)+" not same monster");
                continue;
            }
            log(i+" and "+(i+1)+" for "+att.target.getLogName());
            int start = i;
            Map<ICombatant,Integer> cumulativeHits = new HashMap<>();
            Map<ICombatant,Integer> cumulativeStrikes = new HashMap<>();
            att.incrementTargetDamage(cumulativeHits);
            att.incrementTargetStrikes(cumulativeStrikes);
            next.incrementTargetDamage(cumulativeHits);
            next.incrementTargetStrikes(cumulativeStrikes);
            int end = i + 2;
            while (end < fight.responses.size() - 1)
            {
                if (!(fight.responses.get(end) instanceof FightDetails.Attack))
                    break;
                next = (FightDetails.Attack)fight.responses.get(end);
                if (!next.isMonsterHitNotKill() || !att.attacker.getLogName().equals(next.attacker.getLogName()))
                    break;
                log(" adding in "+end);
                next.incrementTargetDamage(cumulativeHits);
                next.incrementTargetStrikes(cumulativeStrikes);
                end++;
            }
            log("Removing "+(end - start)+" messages from the responses");
            List<Attack> attacks = extractResponses(fight, i, end - start);
            log("Adding in primary at "+i+".");
            fight.responses.add(i, constructMonsterHits(att.attacker, cumulativeHits, cumulativeStrikes, attacks));
            log("Moving on to next.");
        }
    }

    private static void reportConsecutiveKills(FightDetails fight)
    {
        for (int i  = 0; i < fight.responses.size() - 1; i++)
        {
            Object resp = fight.responses.get(i);
            if (resp instanceof AudioMessageBean)
                continue;
            FightDetails.Attack att = (FightDetails.Attack)resp;
            if (!att.isPlayerKill())
                continue;
            //log("One kill at @"+i);
            if (!(fight.responses.get(i + 1) instanceof FightDetails.Attack))
            {
                //log("Next is not an attack");
                continue;
            }
            FightDetails.Attack next = (FightDetails.Attack)fight.responses.get(i + 1);
            if (!next.isPlayerKill())
            {
                //log("Next is not a kill");
                continue;
            }
            if (att.attacker != next.attacker)
            {
                //log("Next different companion");
                continue;
            }
            if (!next.playerAttack)
                continue;
            if (!att.target.getLogName().equals(next.target.getLogName()))
            {
                //log("Next is different monster ("+att.monster.getID()+" vs "+next.monster.getID()+")");
                continue;
            }
            //log("Two kill at @"+(i+1));
            int kills = 2;
            int end = i + 2;
            while (end < fight.responses.size() - 1)
            {
                if (!(fight.responses.get(end) instanceof FightDetails.Attack))
                    break;
                next = (FightDetails.Attack)fight.responses.get(end);
                //log("Next: kill="+next.kill+", same comp="+(att.companion != next.companion)+", playerAttack="+next.playerAttack+", monsterID="+next.monster.getID());
                if (!next.knockout)
                    break;
                if (att.attacker != next.attacker)
                    break;
                if (!next.playerAttack)
                    break;
                if (!att.target.getLogName().equals(next.target.getLogName()))
                    break;
                //log("Another kill at @"+(i+1));
                end++;
                kills++;
            }
            //log("Removing "+kills+" kills starting at "+i);
            for (int j = 0; j < kills; j++)
                fight.responses.remove(i);
            //log("Inserting multiple kill phrase at "+i);
            fight.responses.add(i, new AudioMessageBean(CompanionsModelConst.TEXT_XXX_KILLS_YYY_ZZZ, 
                    att.attacker.getLogName(), kills, att.target.getLogName()));
        }
    }

    // look for kills of the same number by different people
    private static void reportSequentialKills(FightDetails fight)
    {
        for (int i  = 0; i < fight.responses.size() - 1; i++)
        {
            Object resp = fight.responses.get(i);
            if (resp instanceof AudioMessageBean)
                continue;
            FightDetails.Attack att = (FightDetails.Attack)resp;
            if (!att.isPlayerKill())
                continue;
            //log("One kill at @"+i);
            if (!(fight.responses.get(i + 1) instanceof FightDetails.Attack))
            {
                //log("Next is not an attack");
                continue;
            }
            FightDetails.Attack next = (FightDetails.Attack)fight.responses.get(i + 1);
            if (!next.isPlayerKill())
            {
                //log("Next is not a kill");
                continue;
            }
            if (!att.target.getLogName().equals(next.target.getLogName()))
            {
                //log("Next is different monster ("+att.monster.getID()+" vs "+next.monster.getID()+")");
                continue;
            }
            //log("Two kill at @"+(i+1));
            List<String> kills = new ArrayList<>();
            kills.add(att.attacker.getLogName());
            kills.add(next.attacker.getLogName());
            int end = i + 2;
            while (end < fight.responses.size() - 1)
            {
                if (!(fight.responses.get(end) instanceof FightDetails.Attack))
                    break;
                next = (FightDetails.Attack)fight.responses.get(end);
                //log("Next: kill="+next.kill+", same comp="+(att.companion != next.companion)+", playerAttack="+next.playerAttack+", monsterID="+next.monster.getID());
                if (!next.isPlayerKill())
                    break;
                if (!att.target.getLogName().equals(next.target.getLogName()))
                    break;
                //log("Another kill at @"+(i+1));
                end++;
                kills.add(next.attacker.getLogName());
            }
            //log("Removing "+kills+" kills starting at "+i);
            for (int j = 0; j < kills.size(); j++)
                fight.responses.remove(i);
            //log("Inserting multiple kill phrase at "+i);
            AudioMessageBean killers = AudioMessageBean.and(kills);
            fight.responses.add(i, new AudioMessageBean(CompanionsModelConst.TEXT_XXX_KILLS_YYY_ZZZ, 
                    killers, 1, att.target.getLogName()));
        }
    }
    
    private static void reportConsecutiveHits(FightDetails fight)
    {
        for (int i  = 0; i < fight.responses.size() - 1; i++)
        {
            Object resp = fight.responses.get(i);
            if (resp instanceof AudioMessageBean)
                continue;
            FightDetails.Attack att = (FightDetails.Attack)resp;
            if (!att.playerAttack)
                break;
            if (!att.hit)
                continue;
            if (att.knockout)
                continue;
            //log("One hit at @"+i+" for "+att.damage+" damage");
            if (!(fight.responses.get(i + 1) instanceof FightDetails.Attack))
            {
                //log("Next is not an attack");
                continue;
            }
            FightDetails.Attack next = (FightDetails.Attack)fight.responses.get(i + 1);
            if (!next.hit)
            {
                //log("Next is not a hit");
                continue;
            }
            if (next.knockout)
            {
                //log("Next is a kill");
                continue;
            }
            if (!next.playerAttack)
                continue;
            if (att.attacker != next.attacker)
            {
                //log("Next different companion");
                continue;
            }
            if (!att.target.getLogName().equals(next.target.getLogName()))
            {
                //log("Next is different monster ("+att.monster.getID()+" vs "+next.monster.getID()+")");
                continue;
            }
            //log("Two hit at @"+(i+1)+" for "+next.damage+" damage");
            int hits = 2;
            int end = i + 2;
            int damage = att.damage + next.damage;
            while (end < fight.responses.size() - 1)
            {
                if (!(fight.responses.get(end) instanceof FightDetails.Attack))
                    break;
                next = (FightDetails.Attack)fight.responses.get(end);
                if (!next.hit)
                    break;
                if (next.knockout)
                    break;
                if (!next.playerAttack)
                    break;
                if (att.attacker != next.attacker)
                    break;
                if (!att.target.getLogName().equals(next.target.getLogName()))
                    break;
                //log("Another hit at @"+(end)+" for "+next.damage+" damage");
                damage += next.damage;
                end++;
                hits++;
            }
            //log("Removing "+hits+" hits starting at "+i);
            List<Attack> attacks = extractResponses(fight, i, hits);
            //log("Inserting multiple hits phrase at "+i);
            String verb = extractVerb(attacks);
            if (verb == null)
                fight.responses.add(i, new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HITS_YYY_ZZZ_TIMES_FOR_WWW_DAMAGE, 
                    att.attacker.getLogName(), att.target.getLogName(), hits, damage));
            else
                fight.responses.add(i, new AudioMessageBean(CompanionsModelConst.TEXT_XXX_VERBS_YYY_ZZZ_TIMES_FOR_WWW_DAMAGE, 
                    att.attacker.getLogName(), verb, att.target.getLogName(), hits, damage));
        }
    }
    
    private static void reportTrailingMisses(FightDetails fight)
    {
        // bubble sort misses to the bottom
        int top = findFirstMonsterAttack(fight.responses);
        //log("Bubble sorting monster responses starting at "+top);
        boolean anySwaps;
        do
        {
            anySwaps = false;
            for (int i = top; i < fight.responses.size() - 1; i++)
            {
                Object r1 = fight.responses.get(i);
                Object r2 = fight.responses.get(i+1);
                if (!(r1 instanceof FightDetails.Attack) || !(r2 instanceof FightDetails.Attack))
                    continue;
                FightDetails.Attack a1 = (FightDetails.Attack)r1;
                FightDetails.Attack a2 = (FightDetails.Attack)r2;
                if (!a1.hit && a2.hit)
                {   // swap
                    fight.responses.remove(i); // a1
                    fight.responses.add(i + 1, a1);
                    //log("Swaping "+i+" and "+(i+1));
                    anySwaps = true;
                }
            }
        } while (anySwaps);
        //log("Looking for last non-miss");
        int i = fight.responses.size() - 1;
        while (i >= 0)
        {
            Object resp = fight.responses.get(i);
            if (!(resp instanceof FightDetails.Attack))
                break;
            FightDetails.Attack att = (FightDetails.Attack)resp;
            if (att.hit)
                break;
            i--;
        }
        i++;
        //log("Found it at "+i);
        if (i >= fight.responses.size() - 1)
            return; // zero or one miss
        //log("Removing misses");
        while (fight.responses.size() > i)
            fight.responses.remove(i);
        //log("down to "+fight.responses.size());
        //log("adding miss response");
        if (i == 0)
            fight.responses.add(new AudioMessageBean(CompanionsModelConst.TEXT_EVERYONE_MISSES));
        else
            fight.responses.add(new AudioMessageBean(CompanionsModelConst.TEXT_EVERYONE_ELSE_MISSES));
        //log("Up to "+fight.responses.size());
    }
    
    private static int findFirstMonsterAttack(List<Object> responses)
    {
        for (int i = 0; i < responses.size(); i++)
        {
            Object o = responses.get(i);
            if (o instanceof FightDetails.Attack)
                if (!((FightDetails.Attack)o).playerAttack)
                    return i;
        }
        return responses.size();
    }

    private static void reportOnRemainder(FightDetails fight)
    {
        ICombatant lastPlayer = null;
        ICombatant lastTarget = null;
        for (int i = 0; i < fight.responses.size(); i++)
        {
            Object resp = fight.responses.get(i);
            if (!(resp instanceof FightDetails.Attack))
                continue;
            fight.responses.remove(i);
            FightDetails.Attack att = (FightDetails.Attack)resp;
            if (att.playerAttack)
            {
                if (att.attacker != lastPlayer)
                {
                    lastPlayer = att.attacker;
                    lastTarget = null;
                }
                if (!att.hit)
                {
                    fight.responses.add(i, constructPlayerMiss(fight.context.getUser().getEncounter(), att));
                    lastTarget = att.target;
                }
                else
                {
                    if (!att.knockout)
                        fight.responses.add(i, constructPlayerHit(fight.context.getUser().getEncounter(), lastTarget, att));
                    else
                        fight.responses.add(i, constructPlayerKill(att));
                    lastTarget = att.target;
                }
            }
            else
            {   // monster attack
                if (!att.hit)
                    fight.responses.add(i, constructMonsterMiss(att));
                else
                {
                    if (att.knockout)
                        fight.responses.add(i, constructMonsterKnockout(att));
                    else if (att.kill)
                        fight.responses.add(i, constructMonsterKill(att));
                    fight.responses.add(i, constructMonsterHit(att));
                }
            }
        }
    }

    public static AudioMessageBean constructMonsterKill(FightDetails.Attack att)
    {
        return new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_KILLED,
                att.target.getLogName());
    }

    public static AudioMessageBean constructMonsterKnockout(FightDetails.Attack att)
    {
        return new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_KNOCKED_OUT,
                att.target.getLogName());
    }

    public static AudioMessageBean constructMonsterHit(FightDetails.Attack att)
    {
        String verb = extractVerb(att);
        if (verb == null)
            return new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HITS_YYY_FOR_WWW_DAMAGE,
                att.attacker.getLogName(), att.target.getLogName(), att.damage);
        else
            return new AudioMessageBean(CompanionsModelConst.TEXT_XXX_VERBS_YYY_FOR_WWW_DAMAGE,
                att.attacker.getLogName(), verb, att.target.getLogName(), att.damage);
    }

    public static AudioMessageBean constructMonsterMiss(FightDetails.Attack att)
    {
        AudioMessageBean msg;
        msg = new AudioMessageBean(CompanionsModelConst.TEXT_XXX_MISSES_YYY,
                att.attacker.getLogName(), att.target.getLogName());
        return msg;
    }

    public static AudioMessageBean constructPlayerKill(FightDetails.Attack att)
    {
        AudioMessageBean msg;
        msg = new AudioMessageBean(
                CompanionsModelConst.TEXT_XXX_KILLS_YYY,
                att.attacker.getLogName(), att.target.getLogName());
        return msg;
    }

    public static AudioMessageBean constructPlayerHit(CompEncounterBean enc,
            ICombatant lastTarget, FightDetails.Attack att)
    {
        CompCompanionBean companion = (CompCompanionBean)att.attacker;
        String verb = extractVerb(att);
        AudioMessageBean msg;
        if (verb == null)
            if (companion.isMale())
            {
                if (!haveWeAnnounced(enc, companion.getName(), att.weapon.getName()))
                    msg = new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_HITS_YYY_WITH_HIS_ZZZ_FOR_WWW_DAMAGE,
                        companion.getName(), att.target.getLogName(),
                        att.weapon.getName(), att.damage);
                else if (att.target != lastTarget)
                    msg = new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_HITS_YYY_FOR_WWW_DAMAGE,
                        companion.getName(), att.target.getLogName(),
                        att.damage);
                else
                    msg = new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_HITS_FOR_WWW_DAMAGE,
                        companion.getName(), att.damage);
            }
            else
            {
                if (!haveWeAnnounced(enc, companion.getName(), att.weapon.getName()))
                    msg = new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_HITS_YYY_WITH_HER_ZZZ_FOR_WWW_DAMAGE,
                        companion.getName(), att.target.getLogName(),
                        att.weapon.getName(), att.damage);
                else if (att.target != lastTarget)
                    msg = new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_HITS_YYY_FOR_WWW_DAMAGE,
                        companion.getName(), att.target.getLogName(),
                        att.damage);
                else
                    msg = new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_HITS_FOR_WWW_DAMAGE,
                        companion.getName(), att.damage);
            }
        else
            if (companion.isMale())
            {
                if (!haveWeAnnounced(enc, companion.getName(), att.weapon.getName()))
                    msg = new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_VERBS_YYY_WITH_HIS_ZZZ_FOR_WWW_DAMAGE,
                        companion.getName(), verb, att.target.getLogName(),
                        att.weapon.getName(), att.damage);
                else if (att.target != lastTarget)
                    msg = new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_VERBS_YYY_FOR_WWW_DAMAGE,
                        companion.getName(), verb, att.target.getLogName(),
                        att.damage);
                else
                    msg = new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_VERBS_FOR_WWW_DAMAGE,
                        companion.getName(), verb, att.damage);
            }
            else
            {
                if (!haveWeAnnounced(enc, companion.getName(), att.weapon.getName()))
                    msg = new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_VERBS_YYY_WITH_HER_ZZZ_FOR_WWW_DAMAGE,
                        companion.getName(), verb, att.target.getLogName(),
                        att.weapon.getName(), att.damage);
                else if (att.target != lastTarget)
                    msg = new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_VERBS_YYY_FOR_WWW_DAMAGE,
                        companion.getName(), verb, att.target.getLogName(),
                        att.damage);
                else
                    msg = new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_VERBS_FOR_WWW_DAMAGE,
                        companion.getName(), verb, att.damage);
            }
        return msg;
    }

    public static AudioMessageBean constructPlayerMiss(CompEncounterBean e, FightDetails.Attack att)
    {
        CompCompanionBean companion = (CompCompanionBean)att.attacker;
        AudioMessageBean msg;
        if (!haveWeAnnounced(e, companion.getName(), att.weapon.getName()))
        {
            if (companion.isMale())
                msg = new AudioMessageBean(
                    CompanionsModelConst.TEXT_XXX_MISSES_YYY_WITH_HIS_ZZZ,
                    companion.getName(), att.target.getLogName(),
                    att.weapon.getName());
            else
                msg = new AudioMessageBean(
                    CompanionsModelConst.TEXT_XXX_MISSES_YYY_WITH_HER_ZZZ,
                    companion.getName(), att.target.getLogName(),
                    att.weapon.getName());
        }
        else
            msg = new AudioMessageBean(
                CompanionsModelConst.TEXT_XXX_MISSES_YYY,
                companion.getName(), att.target.getLogName());
        return msg;
    }
    
    private static AudioMessageBean constructMonsterHits(ICombatant monster,
            Map<ICombatant, Integer> cumulativeHits,
            Map<ICombatant, Integer> cumulativeStrikes,
            List<Attack> attacks)
    {
        String verb = extractVerb(attacks);
        List<AudioMessageBean> args = new ArrayList<>();
        for (ICombatant comp : cumulativeHits.keySet())
        {
            int damage = cumulativeHits.get(comp);
            int whacks = cumulativeStrikes.get(comp);
            if (whacks == 1)
            {
                if (verb == null)
                    args.add(new AudioMessageBean(CompanionsModelConst.TEXT_HITS_XXX_FOR_YYY,
                        comp.getLogName(), damage));
                else
                    args.add(new AudioMessageBean(CompanionsModelConst.TEXT_VERBS_XXX_FOR_YYY,
                        verb, comp.getLogName(), damage));
            }
            else
            {
                if (verb == null)
                    args.add(new AudioMessageBean(CompanionsModelConst.TEXT_HITS_XXX_YYY_TIMES_FOR_YYY,
                        comp.getLogName(), whacks, damage));
                else
                    args.add(new AudioMessageBean(CompanionsModelConst.TEXT_VERBS_XXX_YYY_TIMES_FOR_YYY,
                        verb, comp.getLogName(), whacks, damage));
            }
        }
        AudioMessageBean payload = new AudioMessageBean(AudioMessageBean.AND, args.toArray());
        return new AudioMessageBean(CompanionsModelConst.TEXT_MONSTER_ACTIONS, monster.getLogName(), payload); 
    }
    
    private static List<Attack> extractResponses(FightDetails fight, int start, int len)
    {
        List<Attack> attacks = new ArrayList<>();
        for (int j = 0; j < len; j++)
        {
            attacks.add((Attack)fight.responses.get(start));
            fight.responses.remove(start);
        }
        return attacks;
    }
    
    private static String extractVerb(List<Attack> attacks)
    {
        String verb = null;
        log("extracting verb from "+attacks.size()+" attacks");
        for (Attack a : attacks)
            if ((a.weapon != null) && a.weapon.hasParam("verb"))
            {
                if (verb == null)
                {
                    verb = a.weapon.getParam("verb");
                    log("first verb="+verb+".");
                }
                else if (!verb.equals(a.weapon.getParam("verb")))
                {
                    log("verb="+verb+" conflicts with "+a.weapon.getParam("verb")+".");
                    verb = null;
                    break;
                }
            }
            else if (a.weapon == null)
                log("no verb (roll)");
            else
                log("no verb (param) "+a.weapon);
        log("return verb="+verb+".");
        if (verb != null)
            verb = "{{"+verb+"}}";
        return verb;
    }
    
    private static String extractVerb(Attack a)
    {
        log("extracting verb from singleton attacks");
        if ((a.weapon != null) && a.weapon.hasParam("verb"))
        {
            String verb = a.weapon.getParam("verb");
            log("verb="+verb);
            return "{{"+verb+"}}";
        }
        if (a.weapon == null)
            log("no verb (roll)");
        else
            log("no verb (param) "+a.weapon);
        return null;
    }

    private static boolean haveWeAnnounced(CompEncounterBean e, String user, String weapon)
    {
        String key = user+"="+weapon;
        if ((e.getAnnouncedWeapons() != null) && (e.getAnnouncedWeapons().indexOf(key) >= 0))
            return true;
        e.setAnnouncedWeapons(e.getAnnouncedWeapons()+"$"+key);
        return false;
    }

    /*
    private List<int[]> findConsecutive(FightDetails fight, boolean playerAttack, boolean hit, boolean knockout, boolean sameAttacker,
            boolean sameMonster)
    {
        List<int[]> matches = new ArrayList<>();
        for (int i  = 0; i < fight.responses.size() - 1; i++)
        {
            Object resp = fight.responses.get(i);
            if (resp instanceof AudioMessageBean)
                continue;
            FightDetails.Attack att = (FightDetails.Attack)resp;
            if (att.playerAttack != playerAttack)
                continue;
            if (att.hit != hit)
                continue;
            if (att.knockout != knockout)
                continue;
            int start = i;
            if (!(fight.responses.get(i + 1) instanceof FightDetails.Attack))
                continue;
            FightDetails.Attack next = (FightDetails.Attack)fight.responses.get(i + 1);
            if (next.playerAttack != playerAttack)
                continue;
            if (next.hit != hit)
                continue;
            if (next.knockout != knockout)
                continue;
            if (sameAttacker && (att.attacker != next.attacker))
                continue;
            if (sameMonster && !att.target.getLogName().equals(next.target.getLogName()))
                continue;
            int end = i + 2;
            while (end < fight.responses.size() - 1)
            {
                if (!(fight.responses.get(end) instanceof FightDetails.Attack))
                    break;
                next = (FightDetails.Attack)fight.responses.get(end);
                if (next.playerAttack != playerAttack)
                    break;
                if (next.hit != hit)
                    break;
                if (next.knockout != knockout)
                    break;
                if (sameAttacker && (att.attacker != next.attacker))
                    break;
                if (sameMonster && !att.target.getLogName().equals(next.target.getLogName()))
                    break;
                end++;
            }
            matches.add(new int[] { start, end });
        }
       return matches;
    }
    */
    private static List<int[]> findConsecutive(FightDetails fight, 
            Function<Object, Boolean> isFirstCandidate, BiFunction<Object, Object, Boolean> isSubsequentCandidate)
    {
        List<int[]> matches = new ArrayList<>();
        for (int i  = 0; i < fight.responses.size() - 1; i++)
        {
            Object first = fight.responses.get(i);
            if (!isFirstCandidate.apply(first))
                continue;
            int start = i;
            while (++i < fight.responses.size())
            {
                Object second = fight.responses.get(i);
                if (!isSubsequentCandidate.apply(first, second))
                    break;
            }
            int end = i;
            if (end - start > 1)
                matches.add(new int[] { start, end });
            i--;
        }
        return matches;
    }
    
    private static void log(String msg)
    {
        //DebugUtils.trace(msg);
    }
}