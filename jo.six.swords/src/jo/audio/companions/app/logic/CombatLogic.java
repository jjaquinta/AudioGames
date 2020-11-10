package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompEncounterBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompMonsterInstanceBean;
import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.logic.MonsterLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.EnumerationUtils;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.obj.StringUtils;

public class CombatLogic
{
    private static final long DANGER_REPORT_TIMEOUT = 30*1000L; // don't report more than once every 30 seconds
    private static final long TIMEOUT_THEY_ATTACK_SOUND = 30*60*1000L; // don't report they attack sound more than every 30 minutes
    
    public static boolean isInCombat(CompState state)
    {
        CompEncounterBean encounter = state.getContext().getUser().getEncounter();
        if (encounter.getRound() <= 0)
            return false;
        if (encounter.getMonsters().size() == 0)
            return false;
        return true;
    }

    public static void checkCombat(CompState state)
    {
        CompEncounterBean encounter = state.getContext().getUser().getEncounter();
        long now = System.currentTimeMillis();
        if (!isInCombat(state))
        {
            if (state.getContext().getSpoor() != null)
            {
                state.respond(CompanionsModelConst.TEXT_THERE_ARE_SIGNS_OF_XXX_HERE, state.getContext().getSpoor().getName());
            }
            else if (!(state.getContext().getSquare().isTown() || state.getContext().getSquare().isCastle()) 
                    && (now > state.getLastDangerReport() + DANGER_REPORT_TIMEOUT) && BaseUserState.RND.nextInt(6) == 1)
            {   // report danger level
                state.prompt("DANGER_REPORT", CompanionsModelConst.TEXT_OCCASIONALLY_I_WILL_TELL_YOU_ABOUT_HOW_DANGEROUS_AN_AREA_IS);
                int cr = state.getContext().getSquare().getChallenge2();
                if (cr <= 0)
                    state.respond(CompanionsModelConst.TEXT_CHALLENGE_N_ONE);
                else if (cr == 1)
                    state.respond(CompanionsModelConst.TEXT_CHALLENGE_EVEN);
                else if (cr == 2)
                    state.respond(CompanionsModelConst.TEXT_CHALLENGE_P_ONE);
                else if (cr == 3)
                    state.respond(CompanionsModelConst.TEXT_CHALLENGE_P_TWO);
                else if (cr == 4)
                    state.respond(CompanionsModelConst.TEXT_CHALLENGE_P_THREE);
                else if (cr >= 5)
                    state.respond(CompanionsModelConst.TEXT_CHALLENGE_P_FOUR);
                state.setLastDangerReport(now);
            }                
            return;
        }
        List<String> combatants = listCombatants(encounter.getMonsters());
        if (!StringUtils.isTrivial(encounter.getAnnounce()))
        {
            AudioMessageBean announce = new AudioMessageBean(encounter.getAnnounce(),new Object[] { ResponseUtils.wordList(combatants)});
            state.respond(announce);
            encounter.setAnnounce(null);
        }
        else
        {
            state.respond(CompanionsModelConst.TEXT_YOU_COME_UPON_XXX, ResponseUtils.wordList(combatants));
            if (encounter.getMonsters().size() > 1)
                state.respond(CompanionsModelConst.TEXT_THEY_ATTACK);
            else
                state.respond(CompanionsModelConst.TEXT_IT_ATTACKS);
        }
        if (state.getContext().isRoomParam("boss"))
            state.respond(CompanionsModelConst.TEXT_MEET_BOSS);
        else
        {
            long elapsed = now - state.getLastTheyAttackSound();
            if (elapsed > TIMEOUT_THEY_ATTACK_SOUND)
            {
                state.respond(CompanionsModelConst.TEXT_FIGHT_START_SOUND);
                state.setLastTheyAttackSound(now);
            }
        }
        state.setState(CompState.STATE_COMBAT);
        state.prompt(CompanionsModelConst.INTENT_FIGHT, CompanionsModelConst.TEXT_SAY_FIGHT_TO_FIGHT_THE_NEXT_ROUND_OF_COMBAT_OR_RUN_AWAY_TO_FLEE);
    }

    public static List<String> listCombatants(
            List<CompMonsterInstanceBean> monsters)
    {
        Map<String, Integer> quantities = new HashMap<>();
        for (CompMonsterInstanceBean monster : monsters)
        {
            int cnt = quantities.containsKey(monster.getID()) ? quantities.get(monster.getID()) : 0;
            cnt++;
            quantities.put(monster.getID(), cnt);
        }
        List<String> combatants = new ArrayList<>();
        for (String id : quantities.keySet())
        {
            CompMonsterTypeBean type = MonsterLogic.getMonsterType(id);
            if (type != null)
                combatants.add(EnumerationUtils.toCardinal(quantities.get(id))+" "+type.getName());
            else
                combatants.add(EnumerationUtils.toCardinal(quantities.get(id))+" "+id);
        }
        return combatants;
    }

    public static List<String> listFighterHits(CompState state)
    {
        List<CompCompanionBean> companions = state.getContext().getUser().getCompanions();
        List<String> combatants = new ArrayList<>();
        for (CompCompanionBean companion : companions)
        {
            if (combatants.size() == 0)
                combatants.add(state.resolve(CompanionsModelConst.TEXT_XXX_HAS_YYY_HIT_POINTS,
                    companion.getName(), companion.getCurrentHitPoints()));
            else
                combatants.add(state.resolve(CompanionsModelConst.TEXT_XXX_HAS_YYY,
                        companion.getName(), companion.getCurrentHitPoints()));
        }
        return combatants;
    }

    public static List<String> listFighterArms(CompState state)
    {
        List<CompCompanionBean> companions = state.getContext().getUser().getCompanions();
        List<String> combatants = new ArrayList<>();
        for (CompCompanionBean companion : companions)
        {
            List<String> weapons = new ArrayList<>();
            for (CompItemInstanceBean weapon : companion.getItems())
            {
                int type = weapon.getType().getType();
                if ((CompItemTypeBean.TYPE_HAND == type) || (CompItemTypeBean.TYPE_LAUNCHER == type) || (CompItemTypeBean.TYPE_HURLED == type))
                    weapons.add(weapon.getFullName());
            }
            if (weapons.size() == 0)
                weapons.add(state.resolve(CompanionsModelConst.TEXT_NOTHING));
            combatants.add(state.resolve(CompanionsModelConst.TEXT_XXX_IS_ARMED_WITH_YYY,
                    companion.getName(), ResponseUtils.wordList(weapons)));
        }
        return combatants;
    }
}
