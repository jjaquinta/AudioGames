package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompEncounterBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompMonsterInstanceBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.EnumerationUtils;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class WhoLogic
{

    public static void reportWho(CompState state, boolean detail)
    {
        CompCompanionBean companion = state.getContext().getCompanion();
        if (companion != null)
        {
            state.respond(
                    CompanionsModelConst.TEXT_YOUR_ACTIVE_COMPANION_IS_XXX,
                    companion.getName());
        }
        else
            state.respond(
                    CompanionsModelConst.TEXT_YOU_HAVE_NO_ACTIVE_COMPANION);
        List<String> compNames = ListLogic.companions(
                state.getContext().getUser().getCompanions(),
                state.getContext().getUser().getActiveCompanion());
        if (compNames.size() == 1)
        {
            state.respond(CompanionsModelConst.TEXT_YOUR_OTHER_COMPANION_IS_XXX,
                    compNames.get(0));
        }
        else if (compNames.size() > 1)
        {
            state.respond(CompanionsModelConst.TEXT_YOUR_OTHER_COMPANIONS_ARE_XXX, 
                    ResponseUtils.wordList(compNames));
        }
        List<String> unconciousNames = ListLogic.companions(
                state.getContext().getUser().getDeadCompanions(),
                null);
        if (unconciousNames.size() == 1)
            state.respond(CompanionsModelConst.TEXT_YOU_ARE_CARRYING_THE_UNCONSCIOUS_BODY_OF_XXX, 
                    unconciousNames.get(0));
        else if (unconciousNames.size() > 1)
            state.respond(CompanionsModelConst.TEXT_YOU_ARE_CARRYING_THE_UNCONSCIOUS_BODIES_OF_XXX, 
                    ResponseUtils.wordList(unconciousNames));
        state.respond(CompanionsModelConst.TEXT_TOGETHER_YOU_FORM_XXX, state.getContext().getUser().getSupportIdent());
            
        if (!state.prompt(CompanionsModelConst.INTENT_INVENTORY, CompanionsModelConst.TEXT_YOU_CAN_SAY_INVENTORY_TO_FIND_OUT_WHAT_YOU_ARE_CARRYING))
            state.prompt(CompanionsModelConst.INTENT_ABOUT, CompanionsModelConst.TEXT_YOU_CAN_SAY_ABOUT_FOR_MORE_INFORMATION_ABOUT_YOUR_PARTY);

        state.setMore(getMoreWho(state).toArray(new AudioMessageBean[0]));
    }

    private static List<AudioMessageBean> getMoreWho(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        msgs.add(getWhoHitPoints(state));
        msgs.add(getWhoEquipped(state));
        msgs.add(getWhoLevel(state));
        msgs.add(getWhoPhysicalStats(state));
        msgs.add(getWhoMentalStats(state));
        msgs.add(getWhoXP(state));
        return msgs;
    }

    public static AudioMessageBean getWhoHitPoints(CompState state)
    {
        List<AudioMessageBean> reply = new ArrayList<>();
        for (CompCompanionBean companion : state.getContext().getUser().getCompanions())
        {
            if (reply.size() > 0)
            {
                if (companion.getCurrentHitPoints() == companion.getEffectiveHitPoints())
                    reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_YYY, companion.getName(), companion.getCurrentHitPoints()));
                else
                    reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_YYY_OUT_OF_ZZZ, 
                            companion.getName(), companion.getCurrentHitPoints(), companion.getEffectiveHitPoints()));
            }
            else
            {
                if (companion.getCurrentHitPoints() == companion.getEffectiveHitPoints())
                    reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_YYY_HIT_POINTS, companion.getName(), companion.getCurrentHitPoints()));
                else
                    reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_YYY_HIT_POINTS_OUT_OF_ZZZ, 
                            companion.getName(), companion.getCurrentHitPoints(), companion.getEffectiveHitPoints()));
            }
        }
        return new AudioMessageBean(CompanionsModelConst.TEXT_SENTANCE, AudioMessageBean.and(reply));
    }

    public static AudioMessageBean getWhoEquipped(CompState state)
    {
        List<AudioMessageBean> reply = new ArrayList<>();
        for (CompCompanionBean companion : state.getContext().getUser().getCompanions())
        {
            List<String> ready = ListLogic.itemInstances(companion.getItems());
            if (ready.size() == 0)
                continue;
            if (reply.size() > 0)
                reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_WITH_YYY, companion.getName(), ResponseUtils.wordList(ready)));
            else
                reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_EQUIPPED_WITH_YYY, companion.getName(), ResponseUtils.wordList(ready)));
        }
        return new AudioMessageBean(CompanionsModelConst.TEXT_SENTANCE, AudioMessageBean.and(reply));
    }

    public static AudioMessageBean getWhoLevel(CompState state)
    {
        List<AudioMessageBean> reply = new ArrayList<>();
        int level = -1;
        for (CompCompanionBean companion : state.getContext().getUser().getCompanions())
        {
            if (reply.size() > 0)
            {
                if (companion.getLevel() == companion.getEffectiveLevel())
                    reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_YYY, companion.getName(), EnumerationUtils.toOrdinal(companion.getLevel())));
                else
                    reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_USUALLY_YYY_BUT_IS_CURRENTLY_ZZZ, 
                            companion.getName(), EnumerationUtils.toOrdinal(companion.getLevel()), EnumerationUtils.toOrdinal(companion.getEffectiveLevel())));
                if (level != companion.getLevel())
                    level = -2;
            }
            else
            {
                if (companion.getLevel() == companion.getEffectiveLevel())
                    reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_YYY_LEVEL, companion.getName(), EnumerationUtils.toOrdinal(companion.getLevel())));
                else
                    reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_USUALLY_YYY_LEVEL_BUT_IS_CURRENTLY_ZZZ_LEVEL, 
                            companion.getName(), EnumerationUtils.toOrdinal(companion.getLevel()), EnumerationUtils.toOrdinal(companion.getEffectiveLevel())));
                level = companion.getLevel();
            }
        }
        if (level > 0)
            return new AudioMessageBean(CompanionsModelConst.TEXT_EVERYONE_IS_XXX_LEVEL, EnumerationUtils.toOrdinal(level));
        else
            return new AudioMessageBean(CompanionsModelConst.TEXT_SENTANCE, AudioMessageBean.and(reply));
    }

    public static AudioMessageBean getWhoPhysicalStats(CompState state)
    {
        List<AudioMessageBean> reply = new ArrayList<>();
        for (CompCompanionBean companion : state.getContext().getUser().getCompanions())
        {
            if (reply.size() > 0)
                reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_YYY__ZZZ__AND_WWW, 
                        companion.getName(), companion.getSTRModified(), companion.getDEXModified(), companion.getCONModified()));
            else
                reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_A_STRENGTH_OF_YYY__A_DEXTERITY_OF_ZZZ__AND_A_CONSTITUTION_OF_WWW, 
                        companion.getName(), companion.getSTRModified(), companion.getDEXModified(), companion.getCONModified()));
        }
        return new AudioMessageBean(CompanionsModelConst.TEXT_SENTANCE, AudioMessageBean.and(reply));
    }

    public static AudioMessageBean getWhoMentalStats(CompState state)
    {
        List<AudioMessageBean> reply = new ArrayList<>();
        for (CompCompanionBean companion : state.getContext().getUser().getCompanions())
        {
            if (reply.size() > 0)
                reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_YYY__ZZZ__AND_WWW, 
                        companion.getName(), companion.getINTModified(), companion.getWISModified(), companion.getCHAModified()));
            else
                reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_A_INTELLIGENCE_OF_YYY__A_WISDOM_OF_ZZZ__AND_A_CHARISMA_OF_WWW, 
                        companion.getName(), companion.getINTModified(), companion.getWISModified(), companion.getCHAModified()));
        }
        return new AudioMessageBean(CompanionsModelConst.TEXT_SENTANCE, AudioMessageBean.and(reply));
    }

    public static AudioMessageBean getWhoXP(CompState state)
    {
        List<AudioMessageBean> reply = new ArrayList<>();
        for (CompCompanionBean companion : state.getContext().getUser().getCompanions())
        {
            if (reply.size() > 0)
                reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_YYY, 
                        companion.getName(), companion.getExperiencePoints()));
            else
                reply.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_YYY_EXPERIENCE_POINTS, 
                        companion.getName(), companion.getExperiencePoints()));
        }
        return new AudioMessageBean(CompanionsModelConst.TEXT_SENTANCE, AudioMessageBean.and(reply));
    }

    public static AudioMessageBean getWhoArmor(CompState state)
    {
        List<AudioMessageBean> companions = new ArrayList<>();
        for (CompCompanionBean comp : state.getUser().getCompanions())
        {
            List<String> items = new ArrayList<>();
            for (CompItemInstanceBean item : comp.getItems())
                if (CompItemTypeBean.TYPE_ARMOR == item.getType().getType())
                    items.add(item.getFullName());
            if (items.size() > 0)
                companions.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_USING_YYY, comp.getName(), AudioMessageBean.and(items)));
        }
        return new AudioMessageBean(CompanionsModelConst.TEXT_SENTANCE, AudioMessageBean.and(companions));        
    }

    public static AudioMessageBean getWhoWeapons(CompState state)
    {
        List<AudioMessageBean> companions = new ArrayList<>();
        for (CompCompanionBean comp : state.getUser().getCompanions())
        {
            List<String> items = new ArrayList<>();
            for (CompItemInstanceBean item : comp.getItems())
                if ((CompItemTypeBean.TYPE_HAND == item.getType().getType()) || (CompItemTypeBean.TYPE_HURLED == item.getType().getType())
                        || (CompItemTypeBean.TYPE_LAUNCHER == item.getType().getType()))
                    items.add(item.getFullName());
            if (items.size() > 0)
                companions.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_USING_YYY, comp.getName(), AudioMessageBean.and(items)));
        }
        return new AudioMessageBean(CompanionsModelConst.TEXT_SENTANCE, AudioMessageBean.and(companions));
    }

    public static void doMoreActivate(CompState state)
    {
        CompCompanionBean companion = state.getContext().getCompanion();
        if (state.getMoreDepth() == 1)
        {
            List<String> ready = ListLogic
                    .itemInstances(companion.getItems());
            if (companion.isMale())
            {
                state.respond(
                        CompanionsModelConst.TEXT_HE_CURRENTLY_HAS_XXX_HIT_POINTS_OUT_OF_YYY,
                        companion.getCurrentHitPoints(),
                        companion.getEffectiveHitPoints());
                if (ready.size() == 0)
                    state.resolve(
                            CompanionsModelConst.TEXT_HE_DOESN_T_HAVE_ANYTHING_EQUIPPED);
                else
                    state.resolve(
                            CompanionsModelConst.TEXT_HE_HAS_READY_XXX,
                            ResponseUtils.wordList(ready));
            }
            else
            {
                state.respond(
                        CompanionsModelConst.TEXT_SHE_CURRENTLY_HAS_XXX_HIT_POINTS_OUT_OF_YYY,
                        companion.getCurrentHitPoints(),
                        companion.getEffectiveHitPoints());
                if (ready.size() == 0)
                    state.resolve(
                            CompanionsModelConst.TEXT_SHE_DOESN_T_HAVE_ANYTHING_EQUIPPED);
                else
                    state.resolve(
                            CompanionsModelConst.TEXT_SHE_HAS_READY_XXX,
                            ResponseUtils.wordList(ready));
            }

            if (companion.getLevel() == companion.getEffectiveLevel())
                state.respond(
                        CompanionsModelConst.TEXT_XXX_IS_A_YYY_WITH_A_STRENGTH_OF_ZZZ,
                        companion.getName(),
                        EnumerationUtils.toOrdinal(companion.getLevel()),
                        CompConstLogic.RACE_NAMES[companion.getRace()],
                        companion.getSTRModified(), companion.getDEXModified(), companion.getCONModified(),
                        companion.getINTModified(), companion.getWISModified(), companion.getCHAModified());
            else
                state.respond(
                        CompanionsModelConst.TEXT_XXX_IS_A_YYY_LEVEL_ZZZ_BUT_IS_CURRENTLY_LEVEL_WWW,
                        companion.getName(),
                        EnumerationUtils.toOrdinal(companion.getLevel()),
                        CompConstLogic.RACE_NAMES[companion.getRace()],
                        EnumerationUtils.toOrdinal(companion.getEffectiveLevel()),
                        companion.getSTRModified(), companion.getDEXModified(), companion.getCONModified(),
                        companion.getINTModified(), companion.getWISModified(), companion.getCHAModified());

            state.respond(CompanionsModelConst.TEXT_MORE_SOUND);
        }
        else if (state.getMoreDepth() == 2)
        {
            int freeProficiencies = companion.getWeaponProficiences()
                    - companion.getProficiencies().size();
            List<String> profs = ListLogic
                    .itemIDs(companion.getProficiencies());
            state.respond(
                    CompanionsModelConst.TEXT_XXX_HAS_YYY_EXPERIENCE_POINTS_AND_ZZZ_WEAPON_PROFICIENCIES,
                    companion.getName(), companion.getExperiencePoints(),
                    freeProficiencies);
            if (profs.size() > 0)
                state.respond(
                        CompanionsModelConst.TEXT_XXX_IN_PROFICIENT_IN_YYY,
                        companion.getName(), ResponseUtils.wordList(profs));
            state.respond(CompanionsModelConst.TEXT_MORE_SOUND);
        }
        else if (state.getMoreDepth() == 3)
        {
            List<String> equipped = ListLogic
                    .itemInstances(companion.getItems());
            if (equipped.size() == 0)
                state.respond(
                        CompanionsModelConst.TEXT_XXX_IS_NOT_EQUIPPED_WITH_ANYTHING);
            else
                state.respond(
                        CompanionsModelConst.TEXT_XXX_IS_EQUIPPED_WITH_YYY,
                        companion.getName(), ResponseUtils.wordList(equipped));
            state.respond(CompanionsModelConst.TEXT_MORE_SOUND);
        }
        else if (state.getMoreDepth() == 4)
        {
            int challengeDelta = companion.getLevel()
                    - state.getContext().getSquare().getChallenge();
            if (challengeDelta > 2)
                state.respond(CompanionsModelConst.TEXT_XXX_OVER_CLASSED,
                        companion.getName());
            else if (challengeDelta < -2)
                state.respond(CompanionsModelConst.TEXT_XXX_OUT_CLASSED,
                        companion.getName());
            else
                state.respond(CompanionsModelConst.TEXT_XXX_ON_PAR,
                        companion.getName());
        }
        else if (state.getMoreDepth() >= 5)
        {
            state.respond(CompanionsModelConst.TEXT_XXX_IS_BORED,
                    companion.getName());
        }
    }

    public static void doAbout(CompState state, String whom)
    {
        if ("wilhemina".equalsIgnoreCase(whom))
        {
            OperationLogic.debug(state, "encounter");
            CombatLogic.checkCombat(state);
            return;
        }
        else if ("adolph".equalsIgnoreCase(whom))
        {
            OperationLogic.debug(state, "cure");
            CombatLogic.checkCombat(state);
            return;
        }
        else if ("hermione".equalsIgnoreCase(whom))
        {
            OperationLogic.debug(state, "decimate");
            CombatLogic.checkCombat(state);
            return;
        }        
        else if ("andromeda".equalsIgnoreCase(whom))
        {
            OperationLogic.debug(state, "wizard");
            return;
        }        
        CompCompanionBean comp = MatchLogic
                .resolveCompanion(state, state.getContext().getUser(), whom, true);
        if (comp == null)
            doAboutGame(state, whom);
        else
            doAboutCompanion(state, comp);
    }

    private static void doAboutGame(CompState state, String raw)
    {
        if (doAboutGameRaw(state, raw))
            return;
        List<AudioMessageBean> msgs;
        if (state.getState() == CompState.STATE_COMBAT)
            msgs = getAboutCombat(state);
        else
            msgs = getAboutGame(state);
        state.respond(msgs.get(0));
        msgs.remove(0);
        state.setMore(msgs.toArray(new AudioMessageBean[0]));
    }

    private static boolean doAboutGameRaw(CompState state, String raw)
    {
        if (StringUtils.isTrivial(raw))
            raw = state.getRequest().getRawText();
        if (StringUtils.isTrivial(raw))
            return false;
        raw = raw.toLowerCase();
        int o = raw.indexOf(' ');
        if (o < 0)
            return false; // assume one word intent
        raw = raw.substring(o).trim(); // remove intent word
        DebugUtils.trace("WhoLogic.doAboutGameRaw('"+raw+"')");
        /*
        CompUserBean user = state.getUser();
        if ((raw.indexOf("travel") >= 0) || (raw.indexOf("miles") >= 0))
        {
            state.respond(CompanionsModelConst.TEXT_YOU_HAVE_TRAVELED_A_TOTAL_OF_XXX_MILES_AND_RANGED_UP_TO_YYY_MILES_FROM_YOUR_STARTING_POINT,
                    (int)user.getTotalDistance(), (int)user.getMaxDistance());
            return true;
        }
        if ((raw.indexOf("fought") >= 0) || (raw.indexOf("fight") >= 0) || (raw.indexOf("battle") >= 0) || (raw.indexOf("win") >= 0) || (raw.indexOf("won") >= 0))
        {
            state.respond(CompanionsModelConst.TEXT_YOU_HAVE_FOUGHT_XXX_BATTLES_AND_WON_YYY_OF_THEM,
                    user.getTotalFights(), user.getTotalWins());
            return true;
        }
        if ((raw.indexOf("kill") >= 0) || (raw.indexOf("monster") >= 0) || (raw.indexOf("bounty") >= 0) || (raw.indexOf("bounties") >= 0))
        {
            state.respond(CompanionsModelConst.TEXT_YOU_HAVE_KILLED_XXX_MONSTERS_OF_YYY_DIFFERENT_TYPES,
                    user.getTotalKills(), ResponseUtils.countList(user.getKillList()));
            state.respond(CompanionsModelConst.TEXT_YOU_HAVE_KILLED_XXX_BOSS_MONSTERS_AND_COLLECTED_YYY_BOUNTIES,
                    ResponseUtils.countList(user.getBossKillList()), ResponseUtils.countList(user.getBountyList()));
            return true;
        }
        if ((raw.indexOf("hit") >= 0) || (raw.indexOf("points") >= 0) || (raw.indexOf("health") >= 0))
        {
            state.respond(getWhoHitPoints(state));
            return true;
        }
        if ((raw.indexOf("equipment") >= 0) || (raw.indexOf("items") >= 0))
        {
            state.respond(getWhoEquipped(state));
            return true;
        }
        if ((raw.indexOf("experience") >= 0))
        {
            state.respond(getWhoXP(state));
            return true;
        }
        if ((raw.indexOf("level") >= 0))
        {
            state.respond(getWhoLevel(state));
            return true;
        }
        if ((raw.indexOf("physical") >= 0) || (raw.indexOf("strength") >= 0) || (raw.indexOf("constitution") >= 0) || (raw.indexOf("dexterity") >= 0))
        {
            state.respond(getWhoPhysicalStats(state));
            return true;
        }
        if ((raw.indexOf("mental") >= 0) || (raw.indexOf("intelligence") >= 0) || (raw.indexOf("wisdom") >= 0) || (raw.indexOf("charisma") >= 0))
        {
            state.respond(getWhoMentalStats(state));
            return true;
        }
        if ((raw.indexOf("armor") >= 0) || (raw.indexOf("shield") >= 0) || (raw.indexOf("defense") >= 0))
        {
            state.respond(getWhoArmor(state));
            return true;
        }
        if ((raw.indexOf("weapon") >= 0) || (raw.indexOf("attack") >= 0) || (raw.indexOf("offence") >= 0) || (raw.indexOf("offense") >= 0))
        {
            state.respond(getWhoWeapons(state));
            return true;
        }
        if ((raw.indexOf("visited") >= 0) || (raw.indexOf("location") >= 0) || (raw.indexOf("carried") >= 0))
        {
            state.respond(CompanionsModelConst.TEXT_YOU_HAVE_VISITED_XXX_LOCATIONS_AND_CARRIED_A_MAXIMUM_OF_YYY_GOLD_PIECES, 
                    ResponseUtils.countList(user.getVisitList()), (int)user.getMaxGoldPieces());
            return true;
        }
        if ((raw.indexOf("bless") >= 0) || (raw.indexOf("god") >= 0) || (raw.indexOf("diety") >= 0))
        {
            int blessingsNum = JSONUtils.getInt(user.getMetadata(), "BLESSINGS_NUM");
            int who = ResponseUtils.countList(JSONUtils.getString(user.getMetadata(), "BLESSINGS_WHO"));
            state.respond(CompanionsModelConst.TEXT_YOU_HAVE_BEEN_BLESSED_XXX_TIMES_BY_YYY_DEITIES,
                    blessingsNum, who);
            return true;
        }
        if ((raw.indexOf("achievement") >= 0) || (raw.indexOf("bronze") >= 0) || (raw.indexOf("silver") >= 0) || (raw.indexOf("platinum") >= 0))
        {
            DebugUtils.trace("WhoLogic.doAboutGameRaw, doing achievements");
            List<String> badges = new ArrayList<>();
            for (StringTokenizer st = new StringTokenizer(user.getTags(), " "); st.hasMoreElements(); )
            {
                String tag = st.nextToken();
                if (CompUserBean.USER_TAG_KEYS.containsKey(tag))
                    badges.add("{{"+CompUserBean.USER_TAG_KEYS.get(tag)+"}}");
            }
            DebugUtils.trace("WhoLogic.doAboutGameRaw, badges="+badges);
            List<AudioMessageBean> msgs = new ArrayList<>();
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_VISIT_HELP));
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_TRAVELLER_HELP));
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_BANKER_HELP));
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_RANGER_HELP));
            if (badges.size() > 0)
            {
                AudioMessageBean list = new AudioMessageBean(AudioMessageBean.AND, badges.toArray());
                state.respond(CompanionsModelConst.TEXT_YOU_ACHIEVEMENTS_ARE_XXX, user.getSupportIdent(), list);
                state.setMore(msgs.toArray(new AudioMessageBean[0]));
                return true;
            }
            state.resolve(msgs.get(0));
            msgs.remove(0);
            state.setMore(msgs.toArray(new AudioMessageBean[0]));
            return true;
        }
        if (state.getContext().getRanks() == null)
            OperationLogic.queryRanks(state, state.getLinkedName(), state.getLinkedEmail());        
        if ((raw.indexOf("rank") >= 0) && (state.getContext().getRanks().size() > 0))
        {
            List<AudioMessageBean> ranks = new ArrayList<>();
            for (String rankID : state.getContext().getRanks().keySet())
            {
                int rankValue = state.getContext().getRanks().get(rankID);
                AudioMessageBean msg = new AudioMessageBean(CompanionsModelConst.TEXT_XXX_AT_YYY, 
                        EnumerationUtils.toOrdinal(rankValue),
                        "{{RANK_"+rankID+"}}");
                ranks.add(msg);
            }
            AudioMessageBean list = new AudioMessageBean(AudioMessageBean.AND, ranks.toArray());
            state.respond(CompanionsModelConst.TEXT_YOU_ARE_RANKED_XXX, list);
            return true;
        }
        if ((raw.indexOf("date") >= 0))
        {
            state.respond(getAboutTime(state));
            state.respond(getAboutMoon(state));
            return true;
        }
        if ((raw.indexOf("time") >= 0) || (raw.indexOf("now") >= 0) || (raw.indexOf("sun") >= 0))
        {
            state.respond(getAboutTime(state));
            return true;
        }
        if ((raw.indexOf("moon") >= 0))
        {
            state.respond(getAboutMoon(state));
            return true;
        }
        */
        List<AudioMessageBean> msgs = WhatLogic.resolveWhat(state, raw);
        if (msgs.size() > 0)
        {
            state.respond(msgs.get(0));
            msgs.remove(0);
            state.setMore(msgs.toArray(new AudioMessageBean[0]));
            return true;
        }

        return false;
    }
    
    public static AudioMessageBean getAboutTime(CompState state)
    {
        int hour = (state.getUser().getTotalTime()%(24*60))/60;
        int minute = (state.getUser().getTotalTime()%60);
        if (hour == 0)
        {
            if (minute == 0)
                return new AudioMessageBean(CompanionsModelConst.TEXT_MIDNIGHT);
            else
                return new AudioMessageBean(CompanionsModelConst.TEXT_TIME_NIGHT, 12, minute);
        }
        else if (hour == 12)
        {
            if (minute == 0)
                return new AudioMessageBean(CompanionsModelConst.TEXT_NOON);
            else
                return new AudioMessageBean(CompanionsModelConst.TEXT_TIME_AFTERNOON, 12, minute);
        }
        else if (hour < 6)
            return new AudioMessageBean(CompanionsModelConst.TEXT_TIME_NIGHT, hour, minute);
        else if (hour < 12)
            return new AudioMessageBean(CompanionsModelConst.TEXT_TIME_MORNING, hour, minute);
        else if (hour < 18)
            return new AudioMessageBean(CompanionsModelConst.TEXT_TIME_AFTERNOON, hour - 12, minute);
        else
            return new AudioMessageBean(CompanionsModelConst.TEXT_TIME_NIGHT, hour - 12, minute);
    }
    
    public static AudioMessageBean getAboutMoon(CompState state)
    {
        int hour = CompConstLogic.getHourOfMoon(state.getUser().getTotalTime());
        int phase = CompConstLogic.getMoonPhase(state.getUser().getTotalTime());
        String tphase = "{{MOON_PHASE_"+phase+"}}";
        if (hour < 6)
            return new AudioMessageBean(CompanionsModelConst.TEXT_MOON_IS_BELOW_THE_HORIZON, tphase);
        else if (hour == 6)
            return new AudioMessageBean(CompanionsModelConst.TEXT_MOON_IS_RISING_OVER_THE_HORIZON, tphase);
        else if (hour < 12)
            return new AudioMessageBean(CompanionsModelConst.TEXT_MOON_IS_RISING, tphase);
        else if (hour == 12)
            return new AudioMessageBean(CompanionsModelConst.TEXT_MOON_IS_OVERHEAD, tphase);
        else if (hour < 18)
            return new AudioMessageBean(CompanionsModelConst.TEXT_MOON_IS_DECLINING, tphase);
        else if (hour == 18)
            return new AudioMessageBean(CompanionsModelConst.TEXT_MOON_IS_SETTING, tphase);
        else
            return new AudioMessageBean(CompanionsModelConst.TEXT_MOON_IS_BELOW_THE_HORIZON, tphase);
    }

    private static List<AudioMessageBean> getAboutGame(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        CompUserBean user = state.getUser();
        int blessingsNum = JSONUtils.getInt(user.getMetadata(), "BLESSINGS_NUM");
        AudioMessageBean msg1 = new AudioMessageBean(AudioMessageBean.GROUP);
        msg1.setArgs(new Object[3]);
        msg1.getArgs()[0] = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_TRAVELED_A_TOTAL_OF_XXX_MILES_AND_RANGED_UP_TO_YYY_MILES_FROM_YOUR_STARTING_POINT,
                user.getSupportIdent(), (int)user.getTotalDistance(), (int)user.getMaxDistance());
        msg1.getArgs()[1] = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_FOUGHT_XXX_BATTLES_AND_WON_YYY_OF_THEM,
                user.getTotalFights(), user.getTotalWins());
        msg1.getArgs()[2] = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_KILLED_XXX_MONSTERS_OF_YYY_DIFFERENT_TYPES,
                user.getTotalKills(), ResponseUtils.countList(user.getKillList()));
        msgs.add(msg1);
        AudioMessageBean msg2 = new AudioMessageBean(AudioMessageBean.GROUP);
        msg2.setArgs(new Object[(blessingsNum > 0) ? 3 : 2]);
        msg2.getArgs()[0] = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_VISITED_XXX_LOCATIONS_AND_CARRIED_A_MAXIMUM_OF_YYY_GOLD_PIECES, 
                ResponseUtils.countList(user.getVisitList()), (int)user.getMaxGoldPieces());
        msg2.getArgs()[1] = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_KILLED_XXX_BOSS_MONSTERS_AND_COLLECTED_YYY_BOUNTIES,
                ResponseUtils.countList(user.getBossKillList()), ResponseUtils.countList(user.getBountyList()));
        if (blessingsNum > 0)
        {
            int who = ResponseUtils.countList(JSONUtils.getString(user.getMetadata(), "BLESSINGS_WHO"));
            msg2.getArgs()[2] = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_BEEN_BLESSED_XXX_TIMES_BY_YYY_DEITIES,
                blessingsNum, who);
        }
        msgs.add(msg2);
        if (!StringUtils.isTrivial(user.getTags()))
        {
            List<String> badges = new ArrayList<>();
            for (StringTokenizer st = new StringTokenizer(user.getTags(), " "); st.hasMoreElements(); )
            {
                String tag = st.nextToken();
                if (CompUserBean.USER_TAG_KEYS.containsKey(tag))
                    badges.add("{{"+CompUserBean.USER_TAG_KEYS.get(tag)+"}}");
            }
            if (badges.size() > 0)
            {
                AudioMessageBean list = new AudioMessageBean(AudioMessageBean.AND, badges.toArray());
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_ACHIEVEMENTS_ARE_XXX, user.getSupportIdent(), list));
            }
        }
        if (state.getContext().getRanks() == null)
            OperationLogic.queryRanks(state, state.getLinkedName(), state.getLinkedEmail());        
        if (state.getContext().getRanks().size() > 0)
        {
            List<AudioMessageBean> ranks = new ArrayList<>();
            for (String rankID : state.getContext().getRanks().keySet())
            {
                int rankValue = state.getContext().getRanks().get(rankID);
                AudioMessageBean msg = new AudioMessageBean(CompanionsModelConst.TEXT_XXX_AT_YYY, 
                        EnumerationUtils.toOrdinal(rankValue),
                        "{{RANK_"+rankID+"}}");
                ranks.add(msg);
            }
            AudioMessageBean list = new AudioMessageBean(AudioMessageBean.AND, ranks.toArray());
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_ARE_RANKED_XXX, user.getSupportIdent(), list));
        }
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_YOUR_SUPPORT_ID_IS_XXX, 
                state.getContext().getUser().getSupportIdent(), 
                state.getContext().getUser().getSupportPassword()));
        return msgs;
    }

    private static List<AudioMessageBean> getAboutCombat(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        CompUserBean user = state.getUser();
        CompEncounterBean enc = user.getEncounter();
        int playersNum = user.getCompanions().size();
        int allies = enc.getAllies().size();
        int enemies = enc.getMonsters().size();
        AudioMessageBean msg1 = new AudioMessageBean(AudioMessageBean.GROUP);
        if (allies == 0)
            msg1.addToGroup(new AudioMessageBean(CompanionsModelConst.TEXT_THE_XXX_OF_YOU_ARE_FIGHTING_YYY_ENEMIES, playersNum, enemies));
        else
            msg1.addToGroup(new AudioMessageBean(CompanionsModelConst.TEXT_THE_XXX_OF_YOU_ARE_FIGHTING_YYY_ENEMIES_WITH_ZZZ_ALLIES, 
                    playersNum, enemies, allies));
        int full = 0;
        int current = 0;
        for (CompMonsterInstanceBean monster : enc.getMonsters())
        {
            current += monster.getCurrentHitPoints();
            full += monster.getFullHitPoints();
        }
        int pc = current*100/full;
        if (pc < 25)
            msg1.addToGroup(new AudioMessageBean(CompanionsModelConst.TEXT_FIGHT_UNDER_25));
        else if (pc < 50)
            msg1.addToGroup(new AudioMessageBean(CompanionsModelConst.TEXT_FIGHT_UNDER_50));
        else if (pc < 75)
            msg1.addToGroup(new AudioMessageBean(CompanionsModelConst.TEXT_FIGHT_UNDER_75));
        else if (pc < 100)
            msg1.addToGroup(new AudioMessageBean(CompanionsModelConst.TEXT_FIGHT_UNDER_100));
        else
            msg1.addToGroup(new AudioMessageBean(CompanionsModelConst.TEXT_FIGHT_AT_100));
        msgs.add(msg1);
        return msgs;
    }

    private static void doAboutCompanion(CompState state,
            CompCompanionBean companion)
    {
        if (companion.getEffectiveHitPoints() == companion.getCurrentHitPoints())
            state.respond(CompanionsModelConst.TEXT_XXX_HAS_YYY_HIT_POINTS, companion.getName(), companion.getCurrentHitPoints());
        else
            state.respond(CompanionsModelConst.TEXT_XXX_HAS_YYY_HIT_POINTS_OUT_OF_ZZZ, companion.getName(), companion.getCurrentHitPoints(), companion.getEffectiveHitPoints());
        state.respond(".");
        List<String> ready = ListLogic.itemInstances(companion.getItems());
        if (ready.size() > 0)
        {
            state.respond(CompanionsModelConst.TEXT_XXX_IS_EQUIPPED_WITH_YYY, companion.getName(), ResponseUtils.wordList(ready));
            state.respond(".");
        }
        List<AudioMessageBean> msgs = new ArrayList<>();
        if (companion.getLevel() == companion.getEffectiveLevel())
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_A_YYY_WITH_A_STRENGTH_OF_ZZZ,
                    companion.getName(),
                    EnumerationUtils.toOrdinal(companion.getLevel()),
                    CompConstLogic.RACE_NAMES[companion.getRace()],
                    companion.getSTRModified(), companion.getDEXModified(), companion.getCONModified(),
                    companion.getINTModified(), companion.getWISModified(), companion.getCHAModified()));
        else
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_A_YYY_LEVEL_ZZZ_BUT_IS_CURRENTLY_LEVEL_WWW,
                    companion.getName(),
                    EnumerationUtils.toOrdinal(companion.getLevel()),
                    CompConstLogic.RACE_NAMES[companion.getRace()],
                    EnumerationUtils.toOrdinal(companion.getEffectiveLevel()),
                    companion.getSTRModified(), companion.getDEXModified(), companion.getCONModified(),
                    companion.getINTModified(), companion.getWISModified(), companion.getCHAModified()));
        List<String> profs = ListLogic.itemIDs(companion.getProficiencies());
        if (profs.size() > 0)
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IN_PROFICIENT_IN_YYY,
                    companion.getName(), ResponseUtils.wordList(profs)));
        int needed;
        int top = CompConstLogic.TABLE_XP_FOR_CLASS[companion.getClazz()].length;
        if (companion.getLevel() < top)
        {
            needed = CompConstLogic.TABLE_XP_FOR_CLASS[companion.getClazz()][companion.getLevel()];
            needed -= companion.getExperiencePoints();
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_YYY_EXPERIENCE_POINTS_AND_NEEDS_ZZZ_FOR_THE_NEXT_LEVEL_, 
                    companion.getName(), companion.getExperiencePoints(),
                    needed));
        }
        else
        {
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_ATTAINED_MAXIMUM_LEVEL, 
                    companion.getName()));
        }
        
        state.setMore(msgs.toArray(new AudioMessageBean[0]));
    }
}
