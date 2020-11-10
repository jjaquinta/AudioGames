package jo.audio.companions.app.logic;

import jo.audio.companions.app.BaseStateHandler;
import jo.audio.companions.app.CombatStateHandler;
import jo.audio.companions.data.CompEncounterBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.FeatureBean;
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
        FeatureBean f = state.getContext().getFeature().getFeature();
        CompRoomBean room = state.getContext().getRoom();
        if ((raw.indexOf("exit") >= 0) || (raw.indexOf("leave") >= 0) || (raw.indexOf("out") >= 0))
        {
            if ("$exit".equals(room.getNorth()))
            {
                h.doNorth(state);
                return true;
            }
            if ("$exit".equals(room.getSouth()))
            {
                h.doSouth(state);
                return true;
            }
            if ("$exit".equals(room.getEast()))
            {
                h.doEast(state);
                return true;
            }
            if ("$exit".equals(room.getWest()))
            {
                h.doWest(state);
                return true;
            }
        }
        String bestDir = null;
        int bestCount = 0;
        CompRoomBean rn = FeatureLogic.findRoom(f, room.getNorth());
        if (rn != null)
        {
            int count = PhoneticMatchLogic.countWordMatches(raw, state.resolve(rn.getName()));
            if (count > bestCount)
            {
                bestDir = "north";
                bestCount = count;
            }
        }
        CompRoomBean rs = FeatureLogic.findRoom(f, room.getSouth());
        if (rs != null)
        {
            int count = PhoneticMatchLogic.countWordMatches(raw, state.resolve(rs.getName()));
            if (count > bestCount)
            {
                bestDir = "south";
                bestCount = count;
            }
        }
        CompRoomBean re = FeatureLogic.findRoom(f, room.getEast());
        if (re != null)
        {
            int count = PhoneticMatchLogic.countWordMatches(raw, state.resolve(re.getName()));
            if (count > bestCount)
            {
                bestDir = "east";
                bestCount = count;
            }
        }
        CompRoomBean rw = FeatureLogic.findRoom(f, room.getWest());
        if (rw != null)
        {
            int count = PhoneticMatchLogic.countWordMatches(raw, state.resolve(rw.getName()));
            if (count > bestCount)
            {
                bestDir = "west";
                bestCount = count;
            }
        }
        if (bestDir != null)
        {
            h.doMove(state, bestDir);
            return true;
        }
        return false;
    }

    public static boolean doSingleInvocations(CompState state, BaseStateHandler h,
            String raw)
    {
        DebugUtils.trace("Attemtping to default, raw="+raw);
        if (raw.indexOf("look") >= 0)
        {
            h.doLook(state);
            return true;
        }
        if (raw.indexOf("examine") >= 0)
        {
            h.doLook(state);
            return true;
        }
        if (raw.indexOf("read") >= 0)
        {
            if ((raw.indexOf("sign") >= 0) || (raw.indexOf("post") >= 0))
            {
                h.doLook(state);
                return true;
            }
            else if ((raw.indexOf("deed") >= 0))
            {
                h.doEquip(state, null, null);
                return true;
            }
        }
        if (raw.indexOf("north") >= 0)
        {
            h.doNorth(state);
            return true;
        }
        if ((raw.indexOf("south") >= 0) || (raw.indexOf("stuff") >= 0) || (raw.indexOf("self") >= 0))
        {
            h.doSouth(state);
            return true;
        }
        if ((raw.indexOf("east") >= 0) || (raw.indexOf("beast") >= 0))
        {
            h.doEast(state);
            return true;
        }
        if (raw.indexOf("west") >= 0)
        {
            h.doWest(state);
            return true;
        }
        if (raw.indexOf("characters") >= 0)
        {
            h.doWho(state);
            return true;
        }
        if (raw.indexOf("time") >= 0)
        {
            state.respond(WhoLogic.getAboutTime(state));
            state.respond(WhoLogic.getAboutMoon(state));
            return true;
        }
        if (raw.indexOf("sun") >= 0)
        {
            state.respond(WhoLogic.getAboutTime(state));
            return true;
        }
        if (raw.indexOf("moon") >= 0)
        {
            state.respond(WhoLogic.getAboutMoon(state));
            return true;
        }
        if ((raw.indexOf("straight") >= 0) || (raw.indexOf("forward") >= 0) || (raw.indexOf("continue") >= 0))
        {
            switch (state.getContext().getUser().getLastMoveDirection())
            {
                case CompOperationBean.NORTH:
                    h.doMove(state, "north");
                    return true;
                case CompOperationBean.SOUTH:
                    h.doMove(state, "south");
                    return true;
                case CompOperationBean.EAST:
                    h.doMove(state, "east");
                    return true;
                case CompOperationBean.WEST:
                    h.doMove(state, "west");
                    return true;
            }
        }
        if ((raw.indexOf("repeat") >= 0) || (raw.indexOf("replay") >= 0))
        {
            h.handleRepeat(state);
            return true;
        }
        if ((raw.indexOf("follow") >= 0) || (raw.indexOf("track") >= 0))
        {
            CompUserBean user = state.getUser();
            CompEncounterBean encounter = user.getEncounter();
            DebugUtils.trace("Attemtping to follow, spoorid="+encounter.getLastSpoorID()+", int="+user.getInteractions()+", spInt="+encounter.getLastSpoorInteraction());
            if (!StringUtils.isTrivial(encounter.getLastSpoorID()) && (user.getInteractions() <= encounter.getLastSpoorInteraction() + 1))
            {
                h.doAbout(state, "wilhemina");
                return true;
            }
        }
        if (raw.indexOf("help") >= 0)
        {
            h.doHelp(state);
            return true;
        }
        if (raw.indexOf("turn off") >= 0)
        {
            h.doStop(state);
            return true;
        }
        return false;
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
        if (raw.indexOf("runaway") >= 0)
        {
            h.doMove(state);
            return true;
        }
        if (raw.indexOf("shoot") >= 0)
        {
            h.doFight(state);
            return true;
        }
        if (raw.indexOf("kill") >= 0)
        {
            h.doFight(state);
            return true;
        }
        if (raw.indexOf("look") >= 0)
        {
            h.doLook(state);
            return true;
        }
        if ((raw.indexOf("repeat") >= 0) || (raw.indexOf("replay") >= 0))
        {
            h.handleRepeat(state);
            return true;
        }
        if (raw.indexOf("help") >= 0)
        {
            h.doHelp(state);
            return true;
        }
        if (raw.indexOf("turn off") >= 0)
        {
            h.doStop(state);
            return true;
        }
        if ((raw.indexOf("fart") >= 0) || (raw.indexOf("bite") >= 0))
        {
            h.doFight(state);
            return true;
        }
        return false;
    }
}
