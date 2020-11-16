package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
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
import jo.util.utils.MathUtils;
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
        if (doSingleInvocations(state, h, raw))
            return true;
        if (!StringUtils.isTrivial(state.getContext().getLocation().getRoomID()) && (state.getContext().getFeature() != null))
        {
            if (doTacticalDefaults(state, h, raw))
                return true;
        }
        else
        {   // strategic
            if (doStrategicDefaults(state, h, raw))
                return true;
        }
        return false;
    }

    public static boolean doStrategicDefaults(CompState state, BaseStateHandler h, String raw)
    {
        if (state.getContext().getFeature() != null)
        {
            FeatureBean f = state.getContext().getFeature().getFeature();
            AudioMessageBean feature = f.getName();
            if (PhoneticMatchLogic.isAnyWordMatch(raw, state.resolve(feature)))
            {
                h.doEnter(state);
                return true;
            }   
            if ((raw.indexOf("into") >= 0) || (raw.indexOf("in to") >= 0))
            {
                h.doEnter(state);
                return true;
            }
        }
        return false;
    }

    public static boolean doTacticalDefaults(CompState state, BaseStateHandler h,
            String raw)
    {
        FeatureBean feature = state.getContext().getFeature().getFeature();
        CompRoomBean room = state.getContext().getRoom();
        int bestDir = -1;
        int bestCount = -1;
        for (int dir = 0; dir < 4; dir++)
        {
            if ("$exit".equals(room.getDirection(dir)))
            {
                int count = MathUtils.max(PhoneticMatchLogic.countWordMatches(raw, "exit"),
                        PhoneticMatchLogic.countWordMatches(raw, "leave"),
                        PhoneticMatchLogic.countWordMatches(raw, "out"));
                if (count > bestCount)
                {
                    bestDir = dir;
                    bestCount = count;
                }
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
                    int count = PhoneticMatchLogic.countWordMatches(raw, roomName);
                    if (count > bestCount)
                    {
                        bestDir = dir;
                        bestCount = count;
                    }
                }
            }
        }
        if (bestDir >= 0)
        {
            h.doMove(state, CompConstLogic.DIR_NAMES[bestDir]);
            return true;
        }
        return false;
    }
    
    private static List<FuzzyBaseMatcher> mBaseSingletons = new ArrayList<>();
    private static void addBaseSingleton(String names, BiConsumer<CompState, BaseStateHandler> action)
    {
        for (StringTokenizer st = new StringTokenizer(names, ","); st.hasMoreTokens(); )
            mBaseSingletons.add(new FuzzyBaseMatcher(st.nextToken(), action));
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
        for (Object m : matchers)
        {
            FuzzyStateMatcher<U> mm = (FuzzyStateMatcher<U>)m;
            if (mm.eval(raw))
                if (mm.exec(state, h))
                    return true;
        }
        Collections.sort(matchers, new Comparator<FuzzyStateMatcher<U>>() {
            @Override
            public int compare(FuzzyStateMatcher<U> o1, FuzzyStateMatcher<U> o2)
            {
                return o2.count - o1.count;
            }
        });
        for (FuzzyStateMatcher<U> m : matchers)
            if (m.count < 100)
                if (m.exec(state, h))
                    return true;
        return false;

    }
    
    public static boolean doSingleInvocations(CompState state, BaseStateHandler h,
            String raw)
    {
        DebugUtils.trace("Attemtping to default, raw="+raw);
        List<FuzzyBaseMatcher> matchers = new ArrayList<>();
        addContextual(matchers, state);
        matchers.addAll(mBaseSingletons);
        return evalFuzzies(matchers, raw, h, state);
    }

    private static void addContextual(List<FuzzyBaseMatcher> matchers,
            CompState state)
    {
        for (CompCompanionBean comp : state.getUser().getCompanions())
        {
            final String name = state.resolve(comp.getName());
            matchers.add(new FuzzyBaseMatcher(name, (s, h) -> h.doAbout(s, name)));
        }
    }
    
    private static List<FuzzyCombatMatcher> mCombatSingletons = new ArrayList<>();
    private static void addCombatSingleton(String names, BiConsumer<CompState, CombatStateHandler> action)
    {
        for (StringTokenizer st = new StringTokenizer(names, ","); st.hasMoreTokens(); )
            mCombatSingletons.add(new FuzzyCombatMatcher(st.nextToken(), action));
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
    public String text;
    public BiConsumer<CompState, T> action;
    public int count;
    
    public FuzzyStateMatcher(String text, BiConsumer<CompState, T> action)
    {
        this.text = text;
        this.action = action;
    }
    
    public boolean eval(String raw)
    {
        this.count = MathUtils.max(PhoneticMatchLogic.countWordMatches(raw, text));
        return this.count == 100;
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
    public FuzzyBaseMatcher(String text, BiConsumer<CompState, BaseStateHandler> action)
    {
        super(text, action);
    }
}

class FuzzyCombatMatcher extends FuzzyStateMatcher<CombatStateHandler>
{
    public FuzzyCombatMatcher(String text, BiConsumer<CompState, CombatStateHandler> action)
    {
        super(text, action);
    }
}
