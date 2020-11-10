package jo.audio.companions.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompEffectInstanceBean;
import jo.audio.companions.data.CompEffectTypeBean;
import jo.audio.companions.data.CompEncounterBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompMonsterInstanceBean;
import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompTreasuresBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FightDetails;
import jo.audio.companions.data.GeoBean;
import jo.audio.companions.data.IAttack;
import jo.audio.companions.data.ICombatant;
import jo.audio.companions.data.ItemSelectBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

public class FightLogic
{
    public static void fight(CompContextBean context)
    {
        CompOperationLogic.fillContext(context);
        CompEncounterBean encounter = context.getUser().getEncounter();
        log("Fight, round #" + encounter.getRound());
        FightDetails fight = new FightDetails();
        fight.context = context;
        determineControl(fight);
        determineRanks(fight);
        log(fight.playerFirstRank.size() + " players in first rank");
        log(fight.playerSecondRank.size() + " players in second rank");
        log(fight.monsterFirstRank.size() + " monsters in first rank");
        log(fight.monsterSecondRank.size() + " monsters in second rank");
        for (ICombatant player : fight.playerFirstRank)
            playerAttack(fight, player, true);
        for (ICombatant player : fight.playerSecondRank)
            playerAttack(fight, player, false);
        for (ICombatant monst : fight.monsterFirstRank)
            monsterAttack(fight, monst, true);
        for (ICombatant monst : fight.monsterSecondRank)
            monsterAttack(fight, monst, false);
        monsterRegenerate(fight);

        FightReportLogic.reportOnFight(fight);
        
        CompUserBean user = context.getUser();
        if (user.getCompanions().size() == 0)
        {
            context.addMessage(
                    CompanionsModelConst.TEXT_YOU_HAVE_BEEN_DEFEATED);
            context.addMessage(
                    CompanionsModelConst.TEXT_YOU_DIED_SOUND);
            if (user.getGoldPieces() < 100000)
            {
                UserLogic.resetUser(context.getUser(), context.getLastOperation().getFlags());
                context.addMessage(CompanionsModelConst.TEXT_EVERYONE_DIED);
                clearEncounter(encounter);
            }
            else
            {
                context.addMessage(CompanionsModelConst.TEXT_AS_THE_LIFE_FORCE_FADES_IN_YOUR_LAST_CHARACTER);
                AudioMessageBean q = new AudioMessageBean(CompanionsModelConst.TEXT_WILL_YOU_PAY_100000_GOLD_TO_WISH_YOU_BACK_TO_FULL_HEALTH);
                JSONObject question = new JSONObject();
                question.put(QueryLogic.QUERY_TEXT, q.toJSON());
                question.put(QueryLogic.QUERY_ACTION, "death_resurrect");
                if (user.getMetadata() == null)
                    user.setMetadata(new JSONObject());
                user.getMetadata().put(CompUserBean.META_QUESTION, question);
                clearEncounter(encounter);
                context.setError(false);                
            }
        }
        else
        {
            int enemiesLeft = encounter.getMonsters().size();
            if (enemiesLeft == 0)
            {
                postCombat(context, encounter, fight);
            }
            else if (isMonsterRunAway(fight))
            {
                monsterRunAway(context, encounter, fight);
            }
            else
            {
                fight.context.getUser().getEncounter().setRound(
                        fight.context.getUser().getEncounter().getRound() + 1);
                if (enemiesLeft > 1)
                    fight.context.addMessage(
                            CompanionsModelConst.TEXT_YOU_HAVE_XXX_ENEMIES_LEFT,
                            enemiesLeft);
                else
                    fight.context.addMessage(
                            CompanionsModelConst.TEXT_YOU_HAVE_ONE_ENEMY_LEFT);
                if (fight.context.getUser().isNearlyDead())
                    fight.context.addMessage(
                            CompanionsModelConst.TEXT_NEARLY_DEAD);
            }
        }
        UserLogic.passTheTime(context, context.getUser(), CompConstLogic.TIME_COMBAT);
        CompIOLogic.saveUser(context.getUser());
    }

    public static void decimate(CompContextBean context)
    {
        CompOperationLogic.fillContext(context);
        CompEncounterBean encounter = context.getUser().getEncounter();
        if (encounter == null)
            return;
        while (encounter.getMonsters().size() > 1)
            encounter.getMonsters().remove(1);
        if (encounter.getMonsters().size() > 0)
            encounter.getMonsters().get(0).setHitPoints(1);
        context.addMessage(
            CompanionsModelConst.TEXT_YOU_HAVE_ONE_ENEMY_LEFT);
        CompIOLogic.saveUser(context.getUser());
    }

    public static void monsterRunAway(CompContextBean context,
            CompEncounterBean encounter, FightDetails fight)
    {
        if (encounter.getMonsters().size() == 1)
            fight.context.addMessage(CompanionsModelConst.TEXT_THE_XXX_RUNS_AWAY, encounter.getMonsters().get(0).getType().getName());
        else
            fight.context.addMessage(CompanionsModelConst.TEXT_YOUR_ENEMY_RUNS_AWAY);
        int xp = 0;
        for (CompMonsterInstanceBean target : encounter.getMonsters())
            xp += target.getExperience();
        xp /= 2;
        encounter.setXP(encounter.getXP() + xp);
        ExperienceLogic.addXP(context, xp);
        postCombat(context, encounter, fight);
    }

    private static boolean isMonsterRunAway(FightDetails fight)
    {
        CompEncounterBean encounter = fight.context.getUser().getEncounter();
        for (CompMonsterInstanceBean monst : encounter.getMonsters())
            if (monst.getType().isSpecial("unfazed"))
                return false;
        float monstStrength = (float)encounter.getCurrentHits()/(float)encounter.getInitialHits();
        monstStrength += encounter.getMorale(); 
        float userStrength = fight.context.getUser().getPCHitPoints();
        return monstStrength < userStrength/2;
    }
    
    public static void postCombat(CompContextBean context,
            CompEncounterBean encounter, FightDetails fight)
    {
        context.addMessage(CompanionsModelConst.TEXT_THE_FIGHT_IS_OVER);
        RoomLogic.roomUsed(context.getUser(), context.getLocation());
        boolean isBoss = context.isRoomParam("boss");
        if (isBoss)
        {
            fight.context.getUser().setBossKillList(ResponseUtils.addToList(fight.context.getUser().getBossKillList(), 
                    context.getSquare().getOrds().toString()));
            context.addMessage(CompanionsModelConst.TEXT_KILL_BOSS);
            CompIOLogic.logKillBoss(context.getUser());
        }
        addEncounterTreasure(context, encounter, fight);
        addMonsterTreasure(context, encounter, fight);
        if (encounter.getXP() > 0)
            context.addMessage(CompanionsModelConst.TEXT_YOU_GAIN_XXX_EXPERIENCE_POINTS, encounter.getXP());
        context.getUser().setTotalWins(context.getUser().getTotalWins() + 1);
        if (context.isRoomParam("postCombat"))
            MacroLogic.executeSimple(context, context.getRoomParam("postCombat"));
        if ((context.getRoom() != null) && context.getRoom().getType().equals(CompRoomBean.TYPE_ENCOUNTER))
            context.getFeature().getEncountersUnfinished().put(context.getRoom().getID(), null);
        BadgeLogic.updateBadges(context, context.getUser());
        clearEncounter(encounter);
    }

    public static void addEncounterTreasure(CompContextBean context,
            CompEncounterBean encounter, FightDetails fight)
    {
        List<CompItemInstanceBean> treasureItems = new ArrayList<>();
        int gold = 0;
        for (DiceRollBean treasure : encounter.getTreasures())
            gold += treasure.roll(BaseUserState.RND);
        for (ItemSelectBean item : encounter.getTreasureItems())
        {
            CompItemInstanceBean treasureItem = ItemLogic.selectItem(item);
            if (treasureItem != null)
            {
                treasureItems.add(treasureItem);
                fight.context.getUser().getItems().add(treasureItem);
            }
        }
        for (CompItemInstanceBean item : encounter.getDiscards())
            ItemLogic.addItem(fight.context.getUser().getItems(), item.getType().getID(), item.getQuantity());
        if (gold > 0)
        {
            ExperienceLogic.addGold(context.getUser(), gold);
            ExperienceLogic.addXP(fight.context, gold);
            context.addMessage(
                    CompanionsModelConst.TEXT_YOU_GAIN_XXX_GOLD_PIECES_,
                    gold);
            CompIOLogic.logTreasure(fight.context.getUser(), gold);
        }
        if (treasureItems.size() > 0)
        {
            List<String> names = new ArrayList<>();
            for (CompItemInstanceBean i : treasureItems)
                names.add(i.getFullName());
            context.addMessage(CompanionsModelConst.TEXT_YOU_FIND_XXX_, AudioMessageBean.and(names));
        }
    }

    public static void addMonsterTreasure(CompContextBean context,
            CompEncounterBean encounter, FightDetails fight)
    {
        log("Adding monster treasure for "+encounter.getKills().size()+" kills");
        if (encounter.getKills().size() == 0)
            return;
        Map<CompMonsterTypeBean,Integer> count = new HashMap<>();
        CompMonsterTypeBean majority = null;
        for (CompMonsterInstanceBean monst : encounter.getKills())
        {
            if (!count.containsKey(monst.getType()))
                count.put(monst.getType(), 1);
            else
                count.put(monst.getType(), count.get(monst.getType()) + 1);
            if ((majority == null) || (count.get(majority) < count.get(monst.getType())))
                majority = monst.getType();
        }
        log("majority ="+majority.getName()+" x "+count.get(majority));
        // individual
        CompTreasuresBean loot = new CompTreasuresBean();
        for (CompMonsterTypeBean type : count.keySet())
            TreasureLogic.rollIndividualTreasures(type, count.get(type), BaseUserState.RND, loot);
        log("Individual treasure totals "+loot.getTotalValue());
        if (StringUtils.isTrivial(context.getLocation().getRoomID()))
        {   // strategic
            log("Strategic combat.");
            searchBodies(context, majority, loot);
            int lair = IntegerUtils.parseInt(StringUtils.digitize(majority.getLairProbability()));
            int roll = BaseUserState.RND.nextInt(100);
            log("Lair chance="+lair+", rolled="+roll+".");
            if (roll < lair)
            {
                log("In lair.");
                loot = new CompTreasuresBean();
                TreasureLogic.rollLairTreasure(majority, BaseUserState.RND, loot);
                TreasureLogic.addMessages(loot);
                if (loot.getMessage() != null)
                    context.addMessage(new AudioMessageBean(CompanionsModelConst.TEXT_FOLLOWING_TRACKS_YOU_FIND_A_LAIR_WHICH_CONTAINS_XXX,
                        loot.getMessage()));
                TreasureLogic.addTreasure(context, loot);
            }
        }
        else
        {   // tactical
            log("Tactical combat.");
            boolean isBoss = context.isRoomParam("boss");
            if (isBoss)
            {
                log("Boss room, adding lair.");
                TreasureLogic.rollLairTreasure(majority, BaseUserState.RND, loot);
            }
            searchBodies(context, majority, loot);
        }
    }

    public static void searchBodies(CompContextBean context,
            CompMonsterTypeBean majority, CompTreasuresBean loot)
    {
        TreasureLogic.addMessages(loot);
        if (loot.getMessage() != null)
            if (context.getUser().getEncounter().getKills().size() > 1)
                context.addMessage(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_SEARCH_THE_BODIES_AND_FIND_XXX,
                    loot.getMessage()));
            else
                context.addMessage(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_SEARCH_THE_BODY_AND_FIND_XXX,
                        loot.getMessage()));
        TreasureLogic.addTreasure(context, loot);
    }
    
    private static void clearEncounter(CompEncounterBean encounter)
    {
        encounter.setRound(0);
        encounter.setAnnounce("");
        encounter.setAnnouncedWeapons("");
        encounter.setXP(0);
        encounter.getTreasures().clear();
        encounter.getTreasureItems().clear();
        encounter.getDiscards().clear();
        encounter.getAllies().clear();
        encounter.getMonsters().clear();
        encounter.getKills().clear();
    }

    private static void monsterAttack(FightDetails fight,
            ICombatant monster, boolean firstRank)
    {
        log(monster.getLogName() + "'s turn");
        ICombatant target = null;
        if (firstRank)
        {
            target = fight.monsterTargets.get(monster);
            if (!fight.context.getUser().getCompanions().contains(target))
                target = null;
        }
        if ((target == null)
                && (fight.context.getUser().getCompanions().size() > 0))
            target = fight.context.getUser().getCompanions()
                    .get(BaseUserState.RND.nextInt(
                            fight.context.getUser().getCompanions().size()));
        if ((target != null) && target.isEffect(CompEffectTypeBean.INVISIBILITY))
        {
            log("Tried to target " + target.getLogName() + " but was invisible.");
            target = null;
        }
        if (target == null)
        {
            log("no target");
            return;
        }
        log("targeting " + target.getLogName() + " " + target.getCurrentHitPoints()
                + "hp");
        List<ICombatant> targets = new ArrayList<>();
        targets.add(target);
        // hit the player
        for (IAttack attack : monster.getAttacks(fight.context, firstRank, targets))
        {
            if (attack.hasParam("chanceUse"))
            {
                int chance = IntegerUtils.parseInt(attack.getParam("chanceUse"));
                if (BaseUserState.RND.nextInt(100) >= chance)
                    continue;
            }
            if (attack.hasParam("maxTimes"))
            {
                int maxTimes = IntegerUtils.parseInt(attack.getParam("maxTimes"));
                if (monster.getLimitedAttackUse() >= maxTimes)
                    continue;
                monster.setLimitedAttackUse(monster.getLimitedAttackUse()+1);
            }
            int ac = target.getAC();
            int th = monster.getTHAC(ac);
            int roll = BaseUserState.RND.nextInt(20) + 1;
            roll += attack.bonusToHit(target);
            if (th > 20)
                th = 20; // 20 always hits
            log("ac=" + ac + ", to-hit=" + th + ", roll=" + roll);
            if ((roll < th) || (roll == 1))
            {
                log("miss");
                fight.miss(monster, target, attack);
                continue;
            }
            int damage = attack.rollDamage(fight.context, BaseUserState.RND, target);
            log("hit for (" + attack.toString() + ") " + damage + " damage");
            target.setCurrentHitPoints(target.getCurrentHitPoints() - damage);
            log("Left with " + target.getCurrentHitPoints()+ " hit points");
            attack.effect(fight, target);
            if (target.getCurrentHitPoints() <= -10)
            {
                log("kills");
                fight.kill(monster, target, attack, damage);
                break;
            }
            else if (target.getCurrentHitPoints() <= 0)
            {
                log("knockedOut");
                fight.knockedOut(monster, target, attack, damage);
                break;
            }
            else
            {
                fight.hit(monster, target, attack, damage);
                if (target.getEffectiveLevel() <= 0)
                {
                    log("drains completely");
                    fight.responses.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_COMPLETELY_DRAINED_THEY_RISE_AS_A_YYY,
                            target.getLogName(), monster.getLogName()));
                    fight.allyKilled(target);
                    if (monster instanceof CompMonsterInstanceBean)
                        fight.context.getUser().getEncounter().getMonsters().add(MonsterLogic.createInstance(
                                fight.context.getUser(),
                                ((CompMonsterInstanceBean)monster).getID()));
                    break;
                }
            }
        }
    }

    private static void monsterRegenerate(FightDetails fight)
    {
        Map<String, Integer> who = new HashMap<>();
        for (CompMonsterInstanceBean monst : fight.context.getUser().getEncounter().getMonsters())
        {
            if (!monst.getType().isSpecial("regen"))
                continue;
            if (monst.getHitPoints() == monst.getFullHitPoints())
                continue;
            int amnt = 1;
            if (monst.getType().isSpecial("regen-2"))
                amnt = 2;
            else if (monst.getType().isSpecial("regen-3"))
                amnt = 3;
            monst.setHitPoints(monst.getHitPoints() + amnt);
            if (monst.getHitPoints() > monst.getFullHitPoints())
                monst.setHitPoints(monst.getFullHitPoints());
            String name = monst.getType().getName();
            if (who.containsKey(name))
                who.put(name, who.get(name) + 1);
            else
                who.put(name, 1);
        }
        if (who.size() == 0)
            return;
        List<AudioMessageBean> args = new ArrayList<>();
        for (String key : who.keySet())
            args.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_YYY, who.get(key), key));
        fight.responses.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_REGENERATES, AudioMessageBean.and(args)));
    }

    private static void playerAttack(FightDetails fight,
            ICombatant player, boolean firstRank)
    {
        log(player.getLogName() + "'s turn");
        // find target
        List<ICombatant> targets = new ArrayList<>();
        float width = 0;
        if (fight.monsterFirstRank.size() > 0)
        {
            log("Checking first rank");
            for (ICombatant monster : fight.monsterFirstRank)
            {
                log("Considering " + monster.getLogName() + ", width="
                        + monster.getWidth());
                width += monster.getWidth();
                if (width <= 1.0)
                {
                    targets.add(monster);
                    fight.monsterTargets.put(monster, player);
                    log("Taking this one");
                }
                else
                {
                    log("Too wide");
                    break;
                }
                if (!monster.isLessThanOneHD())
                {
                    log("Not < 1HD, done selection");
                    break;
                }
                if (targets.size() >= player.getEffectiveLevel())
                {
                    log("One attack/level on < 1HD, done selection");
                    break;
                }
            }
        }
        else if (!firstRank && (fight.monsterSecondRank.size() > 0))
        {
            log("Checking second rank");
            for (ICombatant monster : fight.monsterSecondRank)
            {
                width += monster.getWidth();
                if (width <= 1.0)
                    targets.add(monster);
                else
                    break;
                if (!monster.isLessThanOneHD())
                    break;
            }
        }
        if (targets.size() == 0)
        {
            log("no target");
//            fight.context.addMessage(
//                    CompanionsModelConst.TEXT_XXX_HAS_NO_TARGET,
//                    player.getName());
            return;
        }
        IAttack[] attacks = player.getAttacks(fight.context, firstRank, targets);
        if (attacks.length == 0)
        {
            log("no weapon");
            fight.context.addMessage(
                    CompanionsModelConst.TEXT_XXX_HAS_NO_WEAPON,
                    player.getLogName());
            return;
        }
        log("targeting " + targets.get(0).getLogName() + " "
                + targets.get(0).getCurrentHitPoints() + "hp out of "
                + targets.size()
                +" with "+attacks.length+" attacks.");
        // hit the creatures
        for (IAttack attack : attacks)
        {
            ICombatant target = null;
            while (targets.size() > 0)
            {
                target = targets.get(0);
                if (target.getCurrentHitPoints() <= 0)
                {
                    targets.remove(0);
                    continue; // already dead
                }
                break;
            }
            if (target == null)
                break;
            int ac = target.getAC();
            int th = player.getTHAC(ac);
            int roll = BaseUserState.RND.nextInt(20) + 1;
            if (roll > 1) // 1 always misses
                roll += attack.bonusToHit(target);
            if (th > 20)
                th = 20; // 20 always hits
            log("ac=" + ac + ", to-hit=" + th + ", roll=" + roll);
            if ((roll < th) || (roll == 1))
            {
                log("miss");
                fight.miss(player, target, attack);
                continue;
            }
            if (hitBy(target) > attack.getMagic())
            {
                fight.responses.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_S_YYY_DOESN_T_SEEM_TO_DAMAGE_ZZZ,
                        player.getLogName(), attack.getName(), target.getLogName()));
                break;
            }
            int damage = attack.rollDamage(fight.context, BaseUserState.RND, target);
            target.setCurrentHitPoints(target.getCurrentHitPoints() - damage);
            attack.effect(fight, target);
            if (target.getCurrentHitPoints() > 0)
            {
                fight.hit(player, target, attack, damage);
                log("still has " + target.getCurrentHitPoints() + "hp left");
            }
            else
            {
                log("kills");
                fight.kill(player, target, attack, damage);
                fight.context.getUser().getEncounter().getMonsters()
                        .remove(target);
                fight.context.getUser().getEncounter().getKills()
                    .add((CompMonsterInstanceBean)target);
                fight.monsterFirstRank.remove(target);
                fight.monsterSecondRank.remove(target);
                ExperienceLogic.registerKill(fight.context, target);
                targets.remove(0);
            }
        }
    }
    
    private static int hitBy(ICombatant monst)
    {
        if (!monst.isEffect("hitby"))
            return 0;
        if (monst.isEffect("hitby-1"))
            return 1;
        if (monst.isEffect("hitby-2"))
            return 2;
        if (monst.isEffect("hitby-3"))
            return 3;
        if (monst.isEffect("hitby-4"))
            return 4;
        if (monst.isEffect("hitby-5"))
            return 5;
        return 0;
    }

    private static void determineRanks(FightDetails fight)
    {
        for (CompCompanionBean player : fight.context.getUser().getCompanions())
            if (player.getMissile(fight.context.getUser()) != null)
                fight.playerSecondRank.add(player);
            else if (player.getHand() != null)
                fight.playerFirstRank.add(player);
            else
                fight.playerSecondRank.add(player);
        // add allies
        for (CompMonsterInstanceBean ally : fight.context.getUser()
                .getEncounter().getAllies())
        {
            fight.playerFirstRank.add(ally); // no missile weapon monsters
        }
        // readjust player's first rank as monsters envelop
        float width = 0;
        for (CompMonsterInstanceBean monst : fight.context.getUser()
                .getEncounter().getMonsters())
        {
            fight.monsterFirstRank.add(monst); // no missile weapon monsters
            width += monst.getType().getWidth();
        }
        while ((width > fight.playerFirstRank.size())
                && (fight.playerSecondRank.size() > 0))
        {
            fight.playerFirstRank.add(fight.playerSecondRank.get(0));
            fight.playerSecondRank.remove(0);
        }
        // assign monsters to players
        width = 0;
        for (CompMonsterInstanceBean monst : fight.context.getUser()
                .getEncounter().getMonsters())
        {
            int idx = (int)width;
            if (idx < fight.playerFirstRank.size())
                fight.monsterTargets.put(monst, fight.playerFirstRank.get(idx));
            width += monst.getType().getWidth();
        }
    }

    private static void log(String msg)
    {
        DebugUtils.trace(msg);
    }

    public static void abandon(CompContextBean context)
    {
        CompOperationLogic.fillContext(context);
        if ((context.getRoom() != null) && context.getRoom().getType().equals(CompRoomBean.TYPE_ENCOUNTER))
            context.getFeature().getEncountersUnfinished().put(context.getRoom().getID(), 
                    context.getUser().getEncounter().getMonsters().toArray(new CompMonsterInstanceBean[0]));
        context.getUser().getEncounter().setRound(0);
        context.getUser().getEncounter().getAllies().clear();
        context.getUser().getEncounter().getMonsters().clear();
        context.getUser().getEncounter().getKills().clear();
        if (!StringUtils.isTrivial(context.getUser().getOldLocation()))
            context.getUser().setLocation(context.getUser().getOldLocation());
        CompIOLogic.saveUser(context.getUser());
    }
    
    private static void determineControl(FightDetails fight)
    {
        Map<String, CompCompanionBean> controls = new HashMap<>();
        for (CompCompanionBean p : fight.context.getUser().getCompanions())
            if (p.getCurrentHitPoints() > 0)
                for (CompEffectInstanceBean e : p.getEffects())
                    if (CompEffectTypeBean.CONTROL.equals(e.getID()))
                        controls.put(e.getSubType().toLowerCase(), p);
        Map<String,List<CompMonsterInstanceBean>> affected = new HashMap<>();
        for (CompMonsterInstanceBean m : fight.context.getUser().getEncounter().getMonsters())
        {
            String type = m.getType().getType().toLowerCase();
            if (controls.containsKey(type))
            {
                if (!affected.containsKey(type))
                    affected.put(type, new ArrayList<>());
                affected.get(type).add(m);
            }
        }
        if (affected.size() == 0)
            return;
        for (String type : affected.keySet())
        {
            CompCompanionBean p = controls.get(type);
            List<CompMonsterInstanceBean> ms = affected.get(type);
            int num = DiceRollBean.roll(BaseUserState.RND, 1, 12, 0);
            if (num > ms.size())
                num = ms.size();
            fight.responses.add(new AudioMessageBean(
                    CompanionsModelConst.TEXT_XXX_COMPELS_YYY_ZZZ_TO_RUN_AWAY,
                    p.getName(),
                    num,
                    "{{MONSTER_TYPE" + ((num > 1) ? "_PLURAL" : "") + "_" + type.toUpperCase()+"}}"
                    ));
            while (num-- > 0)
            {
                int idx = BaseUserState.RND.nextInt(ms.size());
                fight.context.getUser().getEncounter().getMonsters().remove(ms.get(idx));
                ms.remove(idx);
            }
        }
    }
    
    public static void doResurrectYes(CompContextBean context)
    {
        CompUserBean user = context.getUser();
        user.getMetadata().remove(CompUserBean.META_QUESTION);
        context.addMessage(CompanionsModelConst.TEXT_CASH_REGISTER);
        ExperienceLogic.addGold(user, -100000);
        if ((user.getDeadCompanions().size() == 0) && (user.getReallyDeadCompanions().size() > 0))
        {
            CompCompanionBean dead = user.getReallyDeadCompanions().get(0);
            dead.setCurrentHitPoints(dead.getHitPoints());
            user.getCompanions().add(0, dead);
            user.getReallyDeadCompanions().remove(0);
        }
        UserLogic.fullyHealCompanions(context, user);
        UserLogic.fullyCureCompanions(context, user);
        user.setActiveCompanion(user.getCompanions().get(0).getID());
        GeoBean loc = new GeoBean(user.getLocation());
        loc.setRoomID(null);
        user.setLocation(loc.toString());
        CompIOLogic.saveUser(context.getUser());
    }
    
    public static void doResurrectNo(CompContextBean context)
    {
        CompUserBean user = context.getUser();
        user.getMetadata().remove(CompUserBean.META_QUESTION);
        UserLogic.resetUser(context.getUser(), context.getLastOperation().getFlags());
        context.addMessage(CompanionsModelConst.TEXT_EVERYONE_DIED);
        CompIOLogic.saveUser(context.getUser());
    }
}
