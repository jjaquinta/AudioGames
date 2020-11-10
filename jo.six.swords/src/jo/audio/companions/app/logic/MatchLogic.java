package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.app.data.AboutInfoStatic;
import jo.audio.companions.app.logic.match.AchievementAboutInfo;
import jo.audio.companions.app.logic.match.ArmorAboutInfo;
import jo.audio.companions.app.logic.match.BlessAboutInfo;
import jo.audio.companions.app.logic.match.DateAboutInfo;
import jo.audio.companions.app.logic.match.EquipmentAboutInfo;
import jo.audio.companions.app.logic.match.FoughtAboutInfo;
import jo.audio.companions.app.logic.match.HitAboutInfo;
import jo.audio.companions.app.logic.match.KillAboutInfo;
import jo.audio.companions.app.logic.match.LevelAboutInfo;
import jo.audio.companions.app.logic.match.LocationAboutInfo;
import jo.audio.companions.app.logic.match.MentalAboutInfo;
import jo.audio.companions.app.logic.match.MissionAboutInfo;
import jo.audio.companions.app.logic.match.MoonAboutInfo;
import jo.audio.companions.app.logic.match.NearbyAboutInfo;
import jo.audio.companions.app.logic.match.PhysicalAboutInfo;
import jo.audio.companions.app.logic.match.RanksAboutInfo;
import jo.audio.companions.app.logic.match.TimeAboutInfo;
import jo.audio.companions.app.logic.match.TravelAboutInfo;
import jo.audio.companions.app.logic.match.VisitedAboutInfo;
import jo.audio.companions.app.logic.match.WeaponAboutInfo;
import jo.audio.companions.app.logic.match.XPAboutInfo;
import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.logic.ItemLogic;
import jo.audio.companions.logic.MonsterLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.PhoneticMatchLogic;
import jo.ipa.logic.FuzzyMatchLogic;
import jo.ipa.logic.IPAComp;
import jo.ipa.logic.IPADictionary;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class MatchLogic
{
    public static final int STATIC_MONSTERS = 1;
    public static final int STATIC_ITEMS = 2;
    public static final int CURRENT_ITEMS = 3;
    public static final int COMPANION_ITEMS = 4;
    public static final int LIVE_COMPANIONS = 5;
    public static final int DEAD_COMPANIONS = 6;
    public static final int MISC = 7;
    
    private static IPADictionary mStaticMonsterDictionary = null;
    private static IPADictionary mStaticItemDictionary = null;
    private static IPADictionary mMiscTopicDictionary = null;
    
    private static void initDictionaries()
    {
        if (mStaticMonsterDictionary != null)
            return;
        // index Monsters
        mStaticMonsterDictionary = new IPADictionary();
        List<CompMonsterTypeBean> monsters = MonsterLogic.getAllTypes();
        Set<String> done = new HashSet<>();
        for (CompMonsterTypeBean m : monsters)
        {
            String name = m.getName();
            if (done.contains(name))
                continue;
            done.add(name); // avoid multiplexed monsters
            FuzzyMatchLogic.addToDictionary(mStaticMonsterDictionary, m, name);
        }
        // index Items
        mStaticItemDictionary = new IPADictionary();
        List<CompItemTypeBean> items = ItemLogic.getAllItemTypes(-1);
        for (CompItemTypeBean i : items)
        {
            String name = i.getName();
            FuzzyMatchLogic.addToDictionary(mStaticItemDictionary, i, name);
        }
        // index Items
        mMiscTopicDictionary = new IPADictionary();
        AboutInfo travel = new TravelAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, travel, "travel", "miles");
        AboutInfo fought = new FoughtAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, fought, "fought", "fight", "battle", "win", "won");
        AboutInfo kill = new KillAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, kill, "kill", "monster", "bounty", "bounties");
        AboutInfo hit = new HitAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, hit, "hit", "points", "health");
        AboutInfo equipment = new EquipmentAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, equipment, "equipment", "items");
        AboutInfo xp = new XPAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, xp, "experience");
        AboutInfo level = new LevelAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, level, "level");
        AboutInfo physical = new PhysicalAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, physical, "physical", "strength", "constitution", "dexterity");
        AboutInfo mental = new MentalAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, mental, "mental", "intelligence", "wisdom", "charisma");
        AboutInfo armor = new ArmorAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, armor, "armor", "shield", "defense");
        AboutInfo weapon = new WeaponAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, weapon, "weapon", "attack", "offence", "offense");
        AboutInfo visited = new VisitedAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, visited, "visited", "carried");
        AboutInfo location = new LocationAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, location, "longitude", "location", "latitude");
        AboutInfo bless = new BlessAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, bless, "bless", "god", "diety");
        AboutInfo achievement = new AchievementAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, achievement, "achievement,badge,bronze,silver,platinum");
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, new AboutInfoStatic(CompanionsModelConst.TEXT_VISIT_HELP), "visitor badge");
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, new AboutInfoStatic(CompanionsModelConst.TEXT_TRAVELLER_HELP), "traveller badge");
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, new AboutInfoStatic(CompanionsModelConst.TEXT_BANKER_HELP), "banker badge");
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, new AboutInfoStatic(CompanionsModelConst.TEXT_RANGER_HELP), "ranger badge");
        AboutInfo ranks = new RanksAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, ranks, "rank");
        AboutInfo date = new DateAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, date, "date");
        AboutInfo time = new TimeAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, time, "time", "now", "sun");
        AboutInfo moon = new MoonAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, moon, "moon");
        AboutInfo nearby = new NearbyAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, nearby, "nearby", "around here", "this area", "the vacinity", "the nearest");
        AboutInfo mission = new MissionAboutInfo();
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, mission, "mission", "quest");
    }

    public static List<IPAComp> findMatches(CompState state, String text, int... flags)
    {
        initDictionaries();
        IPADictionary dict = new IPADictionary();
        for (int flag : flags)
            switch (flag)
            {
                case STATIC_MONSTERS:
                    dict.getIncludes().add(mStaticMonsterDictionary);
                    break;
                case STATIC_ITEMS:
                    dict.getIncludes().add(mStaticItemDictionary);
                    break;
                case MISC:
                    dict.getIncludes().add(mMiscTopicDictionary);
                    break;
                case CURRENT_ITEMS:
                    dict.getIncludes().add(makeCurrentItemDictionary(state));
                    break;
                case COMPANION_ITEMS:
                    dict.getIncludes().add(makeCompanionItemDictionary(state));
                    break;
                case LIVE_COMPANIONS:
                    dict.getIncludes().add(makeLiveCompanionDictionary(state));
                    break;
                case DEAD_COMPANIONS:
                    dict.getIncludes().add(makeDeadCompanionDictionary(state));
                    break;
            }
        List<IPAComp> matches = FuzzyMatchLogic.findMatches(dict, text, 5);
        return matches;
    }

    private static IPADictionary makeCurrentItemDictionary(CompState state)
    {
        IPADictionary dict = new IPADictionary();
        for (CompItemInstanceBean item : state.getUser().getItems())
        {
            String name = item.getFullName();
            FuzzyMatchLogic.addToDictionary(dict, item, name);            
        }
        return dict;
    }

    private static IPADictionary makeCompanionItemDictionary(CompState state)
    {
        IPADictionary dict = new IPADictionary();
        for (CompCompanionBean comp : state.getUser().getCompanions())
            for (CompItemInstanceBean item : comp.getItems())
            {
                String name = item.getFullName();
                FuzzyMatchLogic.addToDictionary(dict, item, name);            
            }
        return dict;
    }

    private static IPADictionary makeLiveCompanionDictionary(CompState state)
    {
        IPADictionary dict = new IPADictionary();
        for (CompCompanionBean comp : state.getUser().getCompanions())
        {
            String name = state.resolve(comp.getID());
            FuzzyMatchLogic.addToDictionary(dict, comp, name);            
        }
        return dict;
    }

    private static IPADictionary makeDeadCompanionDictionary(CompState state)
    {
        IPADictionary dict = new IPADictionary();
        for (CompCompanionBean comp : state.getUser().getDeadCompanions())
        {
            String name = state.resolve(comp.getID());
            FuzzyMatchLogic.addToDictionary(dict, comp, name);            
        }
        return dict;
    }
    
    public static CompCompanionBean resolveCompanion(CompState state, CompUserBean user, String whom, boolean dead)
    {
        DebugUtils.trace("resolving companion '"+whom+"'");
        if (StringUtils.isTrivial(whom))
        {
            DebugUtils.trace("  trivial, no resolution");
            return null;
        }
        List<String> compNames = new ArrayList<>();
        List<CompCompanionBean> comps = new ArrayList<>();
        comps.addAll(user.getCompanions());
        if (dead)
            comps.addAll(user.getDeadCompanions());
        for (CompCompanionBean comp : comps)
        {
            String name = comp.getName();
            DebugUtils.trace("  raw value '"+name+"'");
            name = state.expandInserts(name);
            DebugUtils.trace("  resolved value '"+name+"'");
            compNames.add(name.toLowerCase());
        }
        for (StringTokenizer st = new StringTokenizer(whom, " "); st.hasMoreTokens(); )
        {
            CompCompanionBean comp = doResolveCompanion(state, compNames, comps, st.nextToken().toLowerCase(), dead);
            if (comp != null)
                return comp;
        }
        if (!StringUtils.isTrivial(state.getRequest().getRawText()))
            for (StringTokenizer st = new StringTokenizer(state.getRequest().getRawText(), " "); st.hasMoreTokens(); )
            {
                CompCompanionBean comp = doResolveCompanion(state, compNames, comps, st.nextToken().toLowerCase(), dead);
                if (comp != null)
                    return comp;
            }
        return null;
    }
    private static CompCompanionBean doResolveCompanion(CompState state, List<String> compNames, List<CompCompanionBean> comps, String whom, boolean dead)
    {
        for (int idx = 0; idx < compNames.size(); idx++)
        {
            String name = compNames.get(idx);
            if (whom.equals(name))
            {
                DebugUtils.trace("  Yes!");
                return comps.get(idx);
            }
        }
        DebugUtils.trace("  Performing phonetic match on list.");
        int idx = PhoneticMatchLogic.findMatchIdx(whom, compNames);
        DebugUtils.trace("  Phonetic match returns "+idx+".");
        if (idx >= 0)
        {
            CompCompanionBean comp = comps.get(idx);
            return comp;
        }
        if (!StringUtils.isTrivial(state.getRequest().getRawText()))
        {
            String raw = state.getRequest().getRawText().toLowerCase();
            DebugUtils.trace("  Performing phonetic match on raw text '"+raw+"'.");
            for (idx = 0; idx < compNames.size(); idx++)
                if (raw.indexOf(compNames.get(idx)) >= 0)
                {
                    CompCompanionBean comp = comps.get(idx);
                    DebugUtils.trace("  matched '"+comp.getName()+"'/'"+compNames.get(idx)+"'.");
                    return comp;
                }
        }
        return null;
    }
    public static CompItemInstanceBean resolveItem(List<CompItemInstanceBean> items, String thing)
    {
        if (StringUtils.isTrivial(thing))
            return null;
        List<String> itemNames = new ArrayList<>();
        List<CompItemInstanceBean> itemIndex = new ArrayList<>();
        for (CompItemInstanceBean item : items)
        {
            itemNames.add(item.getType().getName());
            itemIndex.add(item);
            if (!StringUtils.isTrivial(item.getName()))
            {
                itemNames.add(item.getName());
                itemIndex.add(item);
            }
        }
        int idx = PhoneticMatchLogic.findMatchIdx(thing, itemNames);
        if (idx < 0)
            return null;
        CompItemInstanceBean item = itemIndex.get(idx);
        return item;
    }
    public static CompItemInstanceBean resolveItem(CompUserBean user, String thing)
    {
        return resolveItem(user.getItems(), thing);
    }
    public static CompItemInstanceBean resolveItem(CompCompanionBean comp, String thing)
    {
        return resolveItem(comp.getItems(), thing);
    }
}
