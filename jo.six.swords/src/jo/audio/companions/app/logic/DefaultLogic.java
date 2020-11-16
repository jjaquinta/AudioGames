package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import org.json.simple.JSONObject;

import jo.audio.companions.app.BaseStateHandler;
import jo.audio.companions.app.CombatStateHandler;
import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompEncounterBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.PhoneticMatchLogic;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class DefaultLogic
{
    public static void doDefault(CompState state, BaseStateHandler h)
    {
        if (doRawDefault(state, h))
            return;
        state.respond(CompanionsModelConst.TEXT_WTF);
    }

    private static boolean doRawDefault(CompState state, BaseStateHandler h)
    {
        String raw = state.getRequest().getRawText();
        // TODO: localize text
        if (StringUtils.isTrivial(raw))
            return false;
        raw = raw.toLowerCase();
        DebugUtils.trace("Attemtping to default, raw="+raw);
        List<FuzzyBaseMatcher> matchers = new ArrayList<>();
        addSingleInvocations(state, h, matchers);
        if (!StringUtils.isTrivial(state.getContext().getLocation().getRoomID()) && (state.getContext().getFeature() != null))
            addTacticalDefaults(state, h, matchers);
        else
            addStrategicDefaults(state, h, matchers);
        return evalFuzzies(matchers, raw, h, state);
    }

    public static void addStrategicDefaults(CompState state, BaseStateHandler h, List<FuzzyBaseMatcher> matchers)
    {
        if (state.getContext().getFeature() != null)
        {
            FeatureBean f = state.getContext().getFeature().getFeature();
            AudioMessageBean feature = f.getName();
            String featureName = state.resolve(feature);
            FuzzyBaseMatcher fm = new FuzzyBaseMatcher((s, hh) -> hh.doEnter(s), 
                    featureName, "into", "in to");
            matchers.add(fm);
        }
    }

    public static void addTacticalDefaults(CompState state, BaseStateHandler h,
            List<FuzzyBaseMatcher> matchers)
    {
        FeatureBean feature = state.getContext().getFeature().getFeature();
        CompRoomBean room = state.getContext().getRoom();
        for (int dir = 0; dir < 4; dir++)
        {
            final int thisDir = dir;
            if ("$exit".equals(room.getDirection(dir)))
            {
                FuzzyBaseMatcher fm = new FuzzyBaseMatcher((s, hh) -> hh.doMove(s, CompConstLogic.DIR_NAMES[thisDir]), 
                        "exit", "leave", "out");
                matchers.add(fm);
            }
            else
            {
                AudioMessageBean name = room.getDirectionDesc(dir);
                if (name == null)
                {
                    CompRoomBean north = FeatureLogic.findRoom(feature, room.getDirection(dir));
                    if (north != null)
                        name = north.getName();
                }
                if (name != null)
                {
                    String roomName = state.resolve(name);
                    FuzzyBaseMatcher fm = new FuzzyBaseMatcher((s, hh) -> hh.doMove(s, CompConstLogic.DIR_NAMES[thisDir]), 
                            roomName);
                    matchers.add(fm);
                }
            }
        }
    }
    
    private static List<FuzzyBaseMatcher> mBaseSingletons = new ArrayList<>();
    private static void addBaseSingleton(String names, BiConsumer<CompState, BaseStateHandler> action)
    {
        mBaseSingletons.add(new FuzzyBaseMatcher(action, names.split(",")));
    }
    static
    {
        addBaseSingleton("look,examine,read sign,read post", (state,h) -> h.doLook(state));
        addBaseSingleton("read deed", (state,h) -> h.doEquip(state, null, null));
        addBaseSingleton("north", (state,h) -> h.doNorth(state));
        addBaseSingleton("south,stuff,self", (state,h) -> h.doSouth(state));
        addBaseSingleton("east,beast", (state,h) -> h.doEast(state));
        addBaseSingleton("west", (state,h) -> h.doWest(state));
        addBaseSingleton("characters", (state,h) -> h.doWho(state));
        addBaseSingleton("time", (state,h) -> { state.respond(WhoLogic.getAboutTime(state));
            state.respond(WhoLogic.getAboutMoon(state));});
        addBaseSingleton("sun", (state,h) -> state.respond(WhoLogic.getAboutTime(state)));
        addBaseSingleton("moon", (state,h) -> state.respond(WhoLogic.getAboutMoon(state)));
        addBaseSingleton("straight,forward,continue", (state,h) -> {
                switch (state.getContext().getUser().getLastMoveDirection())
                {
                    case CompOperationBean.NORTH:
                        h.doMove(state, "north");
                        break;
                    case CompOperationBean.SOUTH:
                        h.doMove(state, "south");
                        break;
                    case CompOperationBean.EAST:
                        h.doMove(state, "east");
                        break;
                    case CompOperationBean.WEST:
                        h.doMove(state, "west");
                        break;
                }
            });
        addBaseSingleton("repeat,replay", (state,h) -> h.handleRepeat(state));
        addBaseSingleton("help", (state,h) -> h.doHelp(state));
        addBaseSingleton("follow,track", (state,h) -> {
                CompUserBean user = state.getUser();
                CompEncounterBean encounter = user.getEncounter();
                DebugUtils.trace("Attemtping to follow, spoorid="+encounter.getLastSpoorID()+", int="+user.getInteractions()+", spInt="+encounter.getLastSpoorInteraction());
                if (!StringUtils.isTrivial(encounter.getLastSpoorID()) && (user.getInteractions() <= encounter.getLastSpoorInteraction() + 1))
                    h.doAbout(state, "wilhemina");
                else
                    throw new IllegalStateException();
            });
        addBaseSingleton("turn off", (state,h) -> h.doStop(state));
        addBaseSingleton("bounty,bounties", (state,h) -> {
                if ("castleOffice".equals(state.getContext().getLocation().getRoomID()))
                {
                    CompRoomBean room = state.getContext().getRoom();
                    JSONObject params = room.getParams();
                    LookLogic.doMoreBounties(state, params);
                }
                else
                    throw new IllegalStateException();
            });
        // about aliases
        addBaseSingleton("level,gold", (state,h) -> h.doAbout(state, state.getRequest().getRawText()));
    }

    @SuppressWarnings("unchecked")
    private static <T,U> boolean evalFuzzies(List<? extends FuzzyStateMatcher<U>> matchers, String raw, U h, CompState state)
    {
        for (Iterator<?> i = matchers.iterator(); i.hasNext(); )
        {
            Object m = i.next();
            FuzzyStateMatcher<U> mm = (FuzzyStateMatcher<U>)m;
            mm.eval(raw);
            if (mm.count >= 100)
            {
                if (mm.exec(state, h))
                    return true;
                i.remove();
            }
            else if (mm.count == 0)
                i.remove();
        }
        Collections.sort(matchers, new Comparator<FuzzyStateMatcher<U>>() {
            @Override
            public int compare(FuzzyStateMatcher<U> o1, FuzzyStateMatcher<U> o2)
            {
                return o2.count - o1.count;
            }
        });
        for (FuzzyStateMatcher<U> m : matchers)
            if (m.count == 0)
                return false;
            else if (m.count < 100)
                if (m.exec(state, h))
                    return true;
        return false;

    }
    
    public static void addSingleInvocations(CompState state, BaseStateHandler h,
            List<FuzzyBaseMatcher> matchers)
    {
        addContextual(matchers, state);
        matchers.addAll(mBaseSingletons);
    }

    private static void addContextual(List<FuzzyBaseMatcher> matchers,
            CompState state)
    {
        for (CompCompanionBean comp : state.getUser().getCompanions())
        {
            final String name = state.resolve(comp.getName());
            matchers.add(new FuzzyBaseMatcher((s, h) -> h.doAbout(s, name), name));
        }
    }
    
    private static List<FuzzyCombatMatcher> mCombatSingletons = new ArrayList<>();
    private static void addCombatSingleton(String names, BiConsumer<CompState, CombatStateHandler> action)
    {
        mCombatSingletons.add(new FuzzyCombatMatcher(action, names.split(",")));
    }
    static
    {
        addCombatSingleton("runaway", (state,h) -> h.doMove(state));
        addCombatSingleton("shoot,kill,fart,bite", (state,h) -> h.doFight(state));
        addCombatSingleton("look,examine", (state,h) -> h.doLook(state));
        addCombatSingleton("repeat,replay", (state,h) -> h.handleRepeat(state));
        addCombatSingleton("help", (state,h) -> h.doHelp(state));
        addCombatSingleton("turn off", (state,h) -> h.doStop(state));
   }
    
    public static void doDefault(CompState state, CombatStateHandler h)
    {
        if (doRawDefault(state, h))
            return;
        state.respond(CompanionsModelConst.TEXT_WTF_COMBAT);
    }

    private static boolean doRawDefault(CompState state, CombatStateHandler h)
    {
        String raw = state.getRequest().getRawText();
        // TODO: localize text
        if (StringUtils.isTrivial(raw))
            return false;
        raw = raw.toLowerCase();
        List<FuzzyCombatMatcher> matchers = new ArrayList<>();
        matchers.addAll(mCombatSingletons);
        return evalFuzzies(matchers, raw, h, state);
    }
}

class FuzzyStateMatcher<T>
{
    public String[] text;
    public BiConsumer<CompState, T> action;
    public int count;
    
    public FuzzyStateMatcher(BiConsumer<CompState, T> action, String... text)
    {
        this.text = text;
        this.action = action;
    }
    
    public void eval(String raw)
    {
        this.count = -1;
        for (String t : text)
            this.count = Math.max(this.count, PhoneticMatchLogic.countWordMatches(raw, t));
    }
    
    public boolean exec(CompState state, T h)
    {
        try
        {
            action.accept(state, h);
            return true;
        }
        catch (IllegalStateException e)
        {                
        }
        return false;
    }
}

class FuzzyBaseMatcher extends FuzzyStateMatcher<BaseStateHandler>
{
    public FuzzyBaseMatcher(BiConsumer<CompState, BaseStateHandler> action, String... text)
    {
        super(action, text);
    }
}

class FuzzyCombatMatcher extends FuzzyStateMatcher<CombatStateHandler>
{
    public FuzzyCombatMatcher(BiConsumer<CompState, CombatStateHandler> action, String... text)
    {
        super(action, text);
    }
}
