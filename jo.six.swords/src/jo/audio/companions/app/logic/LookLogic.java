package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.data.SquareBean.SignPost;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.ItemLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.EnumerationUtils;
import jo.audio.util.PhoneticMatchLogic;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.data.AudioRequestBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.StringUtils;

public class LookLogic
{
    public static void doLook(CompState state)
    {
        doLook(state, null, null, false);
    }
    public static void doLook(CompState state, SquareBean oldSquare, Integer oldTime, boolean extended)
    {
        if (!StringUtils.isTrivial(state.getContext().getLocation().getRoomID()) && (state.getContext().getFeature() != null))
            doLookTactical(state);
        else
            doLookStrategic(state, oldSquare, oldTime, extended);
    }
    
    private static void doLookStrategic(CompState state, SquareBean oldSquare, Integer oldTime, boolean extended)
    {
        if (extended && doExtendedLook(state))
            return;
        // RegionBean r = state.getContext().getRegion();
        SquareBean s = state.getContext().getSquare();
        // state.respond(CompanionsModelConst.TEXT_YOU_ARE_IN_XXX,
        // r.getTitle());
        String terrain = state
                .resolve(CompConstLogic.TERRAIN_NAMES[s.getTerrain()]+"_"+s.getTerrainDepth());
        if ((oldSquare != null) && (oldSquare.getTerrain() == s.getTerrain()) && (oldSquare.getTerrainDepth() == s.getTerrainDepth()))
            state.respond(CompanionsModelConst.TEXT_AROUND_YOU_CONTINUES_XXX, terrain);
        else
            state.respond(CompanionsModelConst.TEXT_AROUND_YOU_IS_XXX, terrain);
        addRivers(state, s);
        addRoads(state, s);
        if (s.getDescription() != null)
            state.respond(s.getDescription());
        if (state.getContext().getFeature() != null)
        {
            FeatureBean f = state.getContext().getFeature().getFeature();
            AudioMessageBean feature = f.getName();
            if (state.getResponse().getOutputSpeechText().indexOf(state.resolve(feature)) < 0)
                state.respond(CompanionsModelConst.TEXT_THERE_IS_A_XXX_HERE,
                        feature);
        }
        addSignposts(state, s);
        state.setMore(getMoreLookStrategic(state).toArray(new AudioMessageBean[0]));
        if (oldTime != null)
        {
            notifyTime(state, oldTime);
            notifyMoon(state, oldTime);
        }
    }

    public static void notifyTime(CompState state, int oldTime)
    {
        int oldHour = (oldTime/60)%24;
        int newTime = state.getUser().getTotalTime();
        int newHour = (newTime/60)%24;
        DebugUtils.trace("OldHour="+oldHour+", newHour="+newHour+", OldTime="+oldTime+", newTime="+newTime);
        if (oldHour != newHour)
        {
            switch (newHour)
            {
                case 0:
                    state.respond(CompanionsModelConst.TEXT_MIDNIGHT);
                    if (state.getRequest().getOriginator() != AudioRequestBean.ALEXA)
                        state.prompt("about time", CompanionsModelConst.TEXT_PROMPT_TIME);
                    break;
                case 5:
                    state.respond(CompanionsModelConst.TEXT_PRE_DAWN);
                    break;
                case 6:
                    state.respond(CompanionsModelConst.TEXT_DAWN);
                    break;
                case 12:
                    state.respond(CompanionsModelConst.TEXT_NOON);
                    if (state.getRequest().getOriginator() != AudioRequestBean.ALEXA)
                        state.prompt("about time", CompanionsModelConst.TEXT_PROMPT_TIME);
                    break;
                case 17:
                    state.respond(CompanionsModelConst.TEXT_PRE_DUSK);
                    break;
                case 18:
                    state.respond(CompanionsModelConst.TEXT_DUSK);
                    state.prompt(CompanionsModelConst.INTENT_SLEEP, CompanionsModelConst.TEXT_PROMPT_SLEEP);
                    break;
            }
        }
    }

    public static void notifyMoon(CompState state, int oldTime)
    {
        int oldHour = CompConstLogic.getHourOfMoon(oldTime);
        int newHour = CompConstLogic.getHourOfMoon(state.getUser().getTotalTime());
        if (oldHour != newHour)
        {
            int phase = CompConstLogic.getMoonPhase(state.getUser().getTotalTime());
            String tphase = "{{MOON_PHASE_"+phase+"}}";
            switch (newHour)
            {
                case 6:
                    state.respond(CompanionsModelConst.TEXT_MOON_IS_RISING_OVER_THE_HORIZON, tphase);
                    break;
                case 12:
                    state.respond(CompanionsModelConst.TEXT_MOON_IS_OVERHEAD, tphase);
                    break;
                case 18:
                    state.respond(CompanionsModelConst.TEXT_MOON_IS_SETTING, tphase);
                    break;
            }
        }
    }

    private static boolean doExtendedLook(CompState state)
    {
        SquareBean s = state.getContext().getSquare();
        String raw = state.getRequest().getRawText();
        if (StringUtils.isTrivial(raw))
            return false;
        raw = raw.toLowerCase();
        // TODO: Need to localize strings
        if ((raw.indexOf("sign") >= 0) || (raw.indexOf("post") >= 0))
            if (s.getSignposts().size() > 0)
            {
                List<SignPost> signposts = state.getContext().getSquare().getSignposts();
                AudioMessageBean posts = new AudioMessageBean(AudioMessageBean.AND, new Object[signposts.size()]);
                for (int i = 0; i < signposts.size(); i++)
                {
                    AudioMessageBean post = getSign(state, i);
                    posts.getArgs()[i] = post;
                }
                AudioMessageBean signs = new AudioMessageBean(CompanionsModelConst.TEXT_THE_SIGN_POST_POINTS_XXX, posts);
                state.respond(signs);
                return true;
            }
        if ((raw.indexOf("north") >= 0))
        {
            state.respond(makeNorthMessage(state));
            return true;
        }
        if ((raw.indexOf("south") >= 0))
        {
            state.respond(makeSouthMessage(state));
            return true;
        }
        if ((raw.indexOf("east") >= 0))
        {
            state.respond(makeEastMessage(state));
            return true;
        }
        if ((raw.indexOf("west") >= 0))
        {
            state.respond(makeWestMessage(state));
            return true;
        }
        if (state.getContext().getFeature() != null)
        {
            FeatureBean f = state.getContext().getFeature().getFeature();
            AudioMessageBean feature = f.getName();
            if (PhoneticMatchLogic.isAnyWordMatch(raw, state.resolve(feature)))
            {
                String roomID = FeatureLogic.getEntrance(f, -1);
                CompRoomBean room = FeatureLogic.findRoom(f, roomID);
                if (room != null)
                {
                    state.respond(room.getDescription());
                    return true;
                }
            }   
        }
        return false;
    }
    
    public static void addSignposts(CompState state, SquareBean s)
    {
        List<SignPost> signposts = s.getSignposts();
        if (signposts.size() == 1)
            state.respond(new AudioMessageBean(CompanionsModelConst.TEXT_A_SIGN_POST_POINTS_XXX, getSign(state, 0)));
        else if (signposts.size() > 1)
        {
            state.respond(CompanionsModelConst.TEXT_THERE_IS_A_SIGN_POST_HERE);
            if (state.getRequest().getOriginator() == AudioRequestBean.ALEXA)
                state.prompt("LOOK_AT_SIGNPOST",
                        CompanionsModelConst.TEXT_READ_SIGNPOST_ALEXA);
//            else
//                state.prompt("LOOK_AT_SIGNPOST",
//                        CompanionsModelConst.TEXT_READ_SIGNPOST_GOOGLE);
        }
    }

    public static void addRoads(CompState state, SquareBean s)
    {
        addRoads(state, s, SquareBean.T_TRACK,
                CompanionsModelConst.TEXT_THE_TRACK_GOES_XXX,
                CompanionsModelConst.TEXT_STRAIGHT_THE_TRACK_GOES_XXX_YYY,
                CompanionsModelConst.TEXT_CURVED_THE_TRACK_GOES_XXX_YYY,
                CompanionsModelConst.TEXT_THE_TRACK_GOES_XXX_YYY_ZZZ,
                CompanionsModelConst.TEXT_THE_TRACK_GOES_XXX_YYY_ZZZ_WWW,
                CompanionsModelConst.TEXT_FOLLOW_STRAIGHT_THE_TRACK_GOES_XXX_YYY,
                CompanionsModelConst.TEXT_FOLLOW_CURVED_THE_TRACK_GOES_XXX_YYY
                );
        addRoads(state, s, SquareBean.T_ROAD,
                CompanionsModelConst.TEXT_THE_ROAD_GOES_XXX,
                CompanionsModelConst.TEXT_STRAIGHT_THE_ROAD_GOES_XXX_YYY,
                CompanionsModelConst.TEXT_CURVED_THE_ROAD_GOES_XXX_YYY,
                CompanionsModelConst.TEXT_THE_ROAD_GOES_XXX_YYY_ZZZ,
                CompanionsModelConst.TEXT_THE_ROAD_GOES_XXX_YYY_ZZZ_WWW,
                CompanionsModelConst.TEXT_FOLLOW_STRAIGHT_THE_ROAD_GOES_XXX_YYY,
                CompanionsModelConst.TEXT_FOLLOW_CURVED_THE_ROAD_GOES_XXX_YYY
                );
        addRoads(state, s, SquareBean.T_HIGHWAY,
                CompanionsModelConst.TEXT_THE_PAVED_ROAD_GOES_XXX,
                CompanionsModelConst.TEXT_STRAIGHT_THE_PAVED_ROAD_GOES_XXX_YYY,
                CompanionsModelConst.TEXT_CURVED_THE_PAVED_ROAD_GOES_XXX_YYY,
                CompanionsModelConst.TEXT_THE_PAVED_ROAD_GOES_XXX_YYY_ZZZ,
                CompanionsModelConst.TEXT_THE_PAVED_ROAD_GOES_XXX_YYY_ZZZ_WWW,
                CompanionsModelConst.TEXT_FOLLOW_STRAIGHT_THE_PAVED_ROAD_GOES_XXX_YYY,
                CompanionsModelConst.TEXT_FOLLOW_CURVED_THE_PAVED_ROAD_GOES_XXX_YYY
                );
        addRoads(state, s, SquareBean.T_BRIDGE,
                CompanionsModelConst.TEXT_THE_BRIDGE_GOES_XXX,
                CompanionsModelConst.TEXT_STRAIGHT_THE_BRIDGE_GOES_XXX_YYY,
                CompanionsModelConst.TEXT_CURVED_THE_BRIDGE_GOES_XXX_YYY,
                CompanionsModelConst.TEXT_THE_BRIDGE_GOES_XXX_YYY_ZZZ,
                CompanionsModelConst.TEXT_THE_BRIDGE_GOES_XXX_YYY_ZZZ_WWW,
                null,                
                null
                );
    }

    private static void addRoads(CompState state, SquareBean s, int type,
            String oneRoad, String twoStraight, String twoCurved,
            String threeRoads, String fourRoads, 
            String followTwoStraight, String followTwoCurved)
    {
        List<String> roads = new ArrayList<>();
        if (s.getRoadNorth() == type)
            roads.add(state.resolve(CompanionsModelConst.TEXT_NORTH));
        if (s.getRoadSouth() == type)
            roads.add(state.resolve(CompanionsModelConst.TEXT_SOUTH));
        if (s.getRoadEast() == type)
            roads.add(state.resolve(CompanionsModelConst.TEXT_EAST));
        if (s.getRoadWest() == type)
            roads.add(state.resolve(CompanionsModelConst.TEXT_WEST));
        if (roads.size() == 1)
            state.respond(oneRoad,
                    roads.get(0));
        else if (roads.size() == 2)
        {
            boolean straight = (
                    (roads.get(0).equals(CompanionsModelConst.TEXT_NORTH) && roads.get(1).equals(CompanionsModelConst.TEXT_SOUTH))
                    ||
                    (roads.get(0).equals(CompanionsModelConst.TEXT_EAST) && roads.get(1).equals(CompanionsModelConst.TEXT_WEST))
                    );
            boolean follow = false;
            if (followTwoStraight != null)
                switch (state.getUser().getLastMoveDirection())
                {
                    case CompOperationBean.NORTH:
                        if (s.getRoadSouth() == type)
                        {
                            roads.remove(state.resolve(CompanionsModelConst.TEXT_SOUTH));
                            follow = true;
                        }
                        break;
                    case CompOperationBean.SOUTH:
                        if (s.getRoadNorth() == type)
                        {
                            roads.remove(state.resolve(CompanionsModelConst.TEXT_NORTH));
                            follow = true;
                        }
                        break;
                    case CompOperationBean.EAST:
                        if (s.getRoadWest() == type)
                        {
                            roads.remove(state.resolve(CompanionsModelConst.TEXT_WEST));
                            follow = true;
                        }
                        break;
                    case CompOperationBean.WEST:
                        if (s.getRoadEast() == type)
                        {
                            roads.remove(state.resolve(CompanionsModelConst.TEXT_EAST));
                            follow = true;
                        }
                        break;
                }
            if (straight)
                if (follow)
                    state.respond(followTwoStraight, roads.get(0));
                else
                    state.respond(twoStraight, roads.get(0), roads.get(1));
            else
                if (follow)
                    state.respond(followTwoCurved, roads.get(0));
                else
                    state.respond(twoCurved, roads.get(0), roads.get(1));
        }
        else if (roads.size() == 3)
            state.respond(threeRoads, roads.get(0), roads.get(1), roads.get(2));
        else if (roads.size() == 4)
            state.respond(fourRoads, roads.get(0), roads.get(1), roads.get(2), roads.get(3));
    }

    private static final String[] RIVER_TYPE = {
            null, 
            "{{"+CompanionsModelConst.TEXT_BROOK+"}}", 
            "{{"+CompanionsModelConst.TEXT_STREAM+"}}", 
            "{{"+CompanionsModelConst.TEXT_RIVER+"}}", 
    };
    
    private static void addRivers(CompState state, SquareBean s)
    {
        if (!s.isAnyRivers())
            return;
        List<String> inflowTypes = new ArrayList<>();
        List<String> inflowDirs = new ArrayList<>();
        List<String> outflowTypes = new ArrayList<>();
        List<String> outflowDirs = new ArrayList<>();
        addFlows(s.getRiverNorth(), CompanionsModelConst.TEXT_NORTH, s.getAltitude(), state.getContext().getSquareNorth().getAltitude(),
                inflowTypes, inflowDirs, outflowTypes, outflowDirs);
        addFlows(s.getRiverSouth(), CompanionsModelConst.TEXT_SOUTH, s.getAltitude(), state.getContext().getSquareSouth().getAltitude(),
                inflowTypes, inflowDirs, outflowTypes, outflowDirs);
        addFlows(s.getRiverEast(), CompanionsModelConst.TEXT_EAST, s.getAltitude(), state.getContext().getSquareEast().getAltitude(),
                inflowTypes, inflowDirs, outflowTypes, outflowDirs);
        addFlows(s.getRiverWest(), CompanionsModelConst.TEXT_WEST, s.getAltitude(), state.getContext().getSquareWest().getAltitude(),
                inflowTypes, inflowDirs, outflowTypes, outflowDirs);
        if ((inflowTypes.size() > 0) && (outflowTypes.size() == 0))
        {
            state.respond(CompanionsModelConst.TEXT_A_XXX_FLOWS_IN_FROM_THE_YYY_AND_DRAINS_INTO_THE_GROUND_HERE,
                    AudioMessageBean.and(inflowTypes), AudioMessageBean.and(inflowDirs));
        }
        else if ((inflowTypes.size() == 0) && (outflowTypes.size() > 0))
        {
            state.respond(CompanionsModelConst.TEXT_A_XXX_FLOWS_OUT_TO_THE_YYY_FROM_HERE,
                    AudioMessageBean.and(outflowTypes), AudioMessageBean.and(outflowDirs));
        }
        else if ((inflowTypes.size() == 1) && (outflowTypes.size() == 1))
        {
            state.respond(CompanionsModelConst.TEXT_A_XXX_FLOWS_FROM_YYY_TO_ZZZ,
                    inflowTypes.get(0), inflowDirs.get(0), outflowDirs.get(0));
        }
        else if ((inflowTypes.size() == 1) && (outflowTypes.size() > 1))
        {
            state.respond(CompanionsModelConst.TEXT_A_XXX_FLOWS_FROM_YYY_TO_ZZZ,
                    inflowTypes, inflowDirs, AudioMessageBean.and(outflowDirs.get(0)));
        }
        else if ((inflowTypes.size() > 1) && (outflowTypes.size() == 1))
        {
            state.respond(CompanionsModelConst.TEXT_A_XXX_FLOWS_FROM_YYY_TO_ZZZ,
                    AudioMessageBean.and(inflowTypes), AudioMessageBean.and(inflowDirs), outflowDirs.get(0));
        }
        else
        {
            state.respond(CompanionsModelConst.TEXT_A_XXX_FLOWS_FROM_YYY_TO_ZZZ,
                    AudioMessageBean.and(inflowTypes), AudioMessageBean.and(inflowDirs), AudioMessageBean.and(outflowDirs.get(0)));
        }
    }

    private static void addFlows(int riverType, String dirName, float ourAltitude, float theirAltitude,
            List<String> inflowTypes, List<String> inflowDirs, List<String> outflowTypes, List<String> outflowDirs)
    {
        if (riverType == SquareBean.R_NONE)
            return;
        if (theirAltitude > ourAltitude)
        {
            inflowTypes.add(RIVER_TYPE[riverType]);
            inflowDirs.add(dirName);
        }
        else
        {
            outflowTypes.add(RIVER_TYPE[riverType]);
            outflowDirs.add(dirName);
        }
    }
    
    private static final String[] DIR_EXIT = {
        CompanionsModelConst.TEXT_NORTH_LEADS_OUT_OF_HERE,
        CompanionsModelConst.TEXT_SOUTH_LEADS_OUT_OF_HERE,
        CompanionsModelConst.TEXT_EAST_LEADS_OUT_OF_HERE,
        CompanionsModelConst.TEXT_WEST_LEADS_OUT_OF_HERE,
    };
    
//    private static final String[] DIR_ENTER = {
//        CompanionsModelConst.TEXT_NORTH_LEADS_TO_XXX,
//        CompanionsModelConst.TEXT_SOUTH_LEADS_TO_XXX,
//        CompanionsModelConst.TEXT_EAST_LEADS_TO_XXX,
//        CompanionsModelConst.TEXT_WEST_LEADS_TO_XXX,
//    };
    
    private static void doLookTactical(CompState state)
    {
        FeatureBean feature = state.getContext().getFeature().getFeature();
        CompRoomBean room = state.getContext().getRoom();
        state.respond(room.getDescription());
        boolean more = false;
        AudioMessageBean ourName = room.getName();
        AudioMessageBean[] names = new AudioMessageBean[4];
        for (int dir = 0; dir < 4; dir++)
        {
            if ("$exit".equals(room.getDirection(dir)))
                state.respond(DIR_EXIT[dir]);
            else
            {
                names[dir] = room.getDirectionDesc(dir);
                if (names[dir] == null)
                {
                    CompRoomBean north = FeatureLogic.findRoom(feature, room.getDirection(dir));
                    if (north != null)
                        names[dir] = north.getName();
                }
            }
        }
        JSONObject params = room.getParams();
        if ((params != null) && BooleanUtils.parseBoolean(params.get("ordinalDirections")))
            respondWithOrdinalDirections(state, names, ourName);
        else
            respondWithDirections(state, names, ourName);
        if (room.getType().equals(CompRoomBean.TYPE_ITEM_SHOP))
            more = doLookShop(state, params);
        else if (room.getType().equals(CompRoomBean.TYPE_FIGHTERS_GUILD))
            more = doLookHires(state, params);
        if (params != null)
        {
            if (params.containsKey("bounties"))
                more |= doLookBounties(state, params);
            else if (params.containsKey("moreText"))
                more |= true;
            if (params.containsKey("prompt"))
            {
                JSONObject prompt = JSONUtils.getObject(params, "prompt");
                String intent = JSONUtils.getString(prompt, "intent");
                String key = JSONUtils.getString(prompt, "key");
                JSONArray jargs = JSONUtils.getArray(prompt, "args");
                Object[] args;
                if (jargs == null)
                    args = new Object[0];
                else
                    args = jargs.toArray();
                state.prompt(intent, key, args);
            }
        }
        if (more)
        {
            state.respond(CompanionsModelConst.TEXT_MORE_SOUND);
            state.setMoreIntent(CompanionsModelConst.INTENT_LOOK);
            state.setMoreDepth(0);
        }
    }

    public static boolean doLookBounties(CompState state, JSONObject params)
    {
        List<JSONObject> unfilled = getUnfilledBounties(state, params);
        if (unfilled.size() == 0)
            return false;
        state.respond(CompanionsModelConst.TEXT_THERES_A_LIST_OF_BOUNTIES_HERE);
        return true;
    }

    public static List<JSONObject> getUnfilledBounties(CompState state,
            JSONObject params)
    {
        JSONArray bounties = (JSONArray)params.get("bounties");
        List<JSONObject> unfilled = new ArrayList<>();
        for (int i = 0; i < bounties.size(); i++)
        {
            JSONObject bounty = (JSONObject)bounties.get(i);
            String ords = JSONUtils.getString(bounty, "ords");
            if ((state.getUser().getBountyList() == null) || (state.getUser().getBountyList().indexOf("{"+ords+"}") < 0))
                unfilled.add(bounty);
        }
        return unfilled;
    }
    
    public static boolean doLookHires(CompState state, JSONObject params)
    {
        boolean more;
        JSONArray hires = (JSONArray)params.get("hires");
        List<String> vocab = new ArrayList<>();
        for (int i = 0; i < hires.size(); i++)
        {
            JSONObject json = (JSONObject)hires.get(i);
            CompCompanionBean hire = new CompCompanionBean();
            hire.fromJSON(json);
            if (state.getUser().getCompanion(hire.getID()) != null)
                continue;
            vocab.add(state.resolve(CompanionsModelConst.TEXT_XXX_WITH_YYY_HIT_POINTS, hire.getName(), hire.getHitPoints()));
        }
        if (vocab.size() == 0)
            state.respond(CompanionsModelConst.TEXT_NO_ONE_IS_AVAILABLE_FOR_HIRE);
        else if (vocab.size() == 1)
            state.respond(CompanionsModelConst.TEXT_XXX_IS_AVAILABLE_FOR_HIRE, vocab.get(0));
        else
            state.respond(CompanionsModelConst.TEXT_XXX_ARE_AVAILABLE_FOR_HIRE, ResponseUtils.wordList(vocab));
        more = true;
        return more;
    }

    public static boolean doLookShop(CompState state, JSONObject params)
    {
        List<AudioMessageBean> msgs = getMoreItemShop(state, params);
        state.respond(msgs.get(0));
        msgs.remove(0);
        state.setMore(msgs.toArray(new AudioMessageBean[0]));
        return false;
    }

    private static final String[] DIR_NAMES = {
        CompanionsModelConst.TEXT_NORTH,
        CompanionsModelConst.TEXT_SOUTH,
        CompanionsModelConst.TEXT_EAST,
        CompanionsModelConst.TEXT_WEST,
    };
    
    private static void respondWithOrdinalDirections(CompState state, AudioMessageBean[] names, AudioMessageBean ourName)
    {
        for (int dir = 0; dir < names.length; dir++)
        {
            AudioMessageBean name = names[dir];
            if (name == null)
                continue;
            state.respond(CompanionsModelConst.TEXT_XXX_LEADS_TO_YYY, 
                    "{{"+DIR_NAMES[dir]+"}}", name);
        }        
    }
    
    private static void respondWithDirections(CompState state, AudioMessageBean[] names, AudioMessageBean ourName)
    {
        Map<String,List<String>> destinations = new HashMap<String, List<String>>();
        Map<String,AudioMessageBean> destMsg = new HashMap<String, AudioMessageBean>();
        for (int dir = 0; dir < names.length; dir++)
        {
            AudioMessageBean name = names[dir];
            if (name == null)
                continue;
            destMsg.put(name.toString(), name);
            List<String> nameDests = destinations.get(name.toString());
            if (nameDests == null)
            {
                nameDests = new ArrayList<>();
                destinations.put(name.toString(), nameDests);
            }
            nameDests.add("{{"+DIR_NAMES[dir]+"}}");
        }
        for (String ident : destinations.keySet())
        {
            AudioMessageBean name = destMsg.get(ident);
            List<String> directions = destinations.get(ident);
            if (ident.equals(ourName.getIdent()))
                state.respond(CompanionsModelConst.TEXT_XXX_CONTINUES_YYY, 
                        name, ResponseUtils.wordList(directions));
            else
                state.respond(CompanionsModelConst.TEXT_XXX_LEADS_TO_YYY, 
                    ResponseUtils.wordList(directions), name);
        }
    }

    
    public static void doMoreLook(CompState state)
    {
        doMoreLookTactical(state);
    }

    private static List<AudioMessageBean> getMoreLookStrategic(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        // signpost
        List<SignPost> signposts = state.getContext().getSquare().getSignposts();
        if (signposts.size() > 1)
        {
            AudioMessageBean posts = new AudioMessageBean(AudioMessageBean.AND, new Object[signposts.size()]);
            for (int i = 0; i < signposts.size(); i++)
            {
                AudioMessageBean post = getSign(state, i);
                posts.getArgs()[i] = post;
            }
            AudioMessageBean signs = new AudioMessageBean(CompanionsModelConst.TEXT_THE_SIGN_POST_POINTS_XXX, posts);
            msgs.add(signs);
        }
        // region
        DemenseBean d = state.getContext().getSquare().getDemense();
        if (d != null)
        {
            List<AudioMessageBean> area = new ArrayList<>();
            area.add(new AudioMessageBean(CompanionsModelConst.TEXT_AROUND_YOU_IS_XXX,
                        d.getFullName()));
            for (DemenseBean r = d.getLiege(); r != null; r = r.getLiege())
                area.add(new AudioMessageBean(CompanionsModelConst.TEXT_WHICH_IS_IN_XXX,
                        r.getFullName()));
            area.add(makeLonLatMessage(state));
            msgs.add(new AudioMessageBean(AudioMessageBean.GROUP, area.toArray()));
        }
        else
            msgs.add(makeLonLatMessage(state));
        // surrounding terrain
        AudioMessageBean surrounds = new AudioMessageBean(AudioMessageBean.GROUP, new Object[4]);
        surrounds.getArgs()[0] = makeNorthMessage(state);
        surrounds.getArgs()[1] = makeSouthMessage(state);
        surrounds.getArgs()[2] = makeEastMessage(state);
        surrounds.getArgs()[3] = makeWestMessage(state);
        msgs.add(surrounds);
        // misc
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_THE_WEATHER_IS_PLEASANT));
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_CAN_SEE_A_CLOUD_SHAPED));
        return msgs;
    }

    public static AudioMessageBean makeLonLatMessage(CompState state)
    {
        Object[] args = new Object[6];
        int lat = state.getContext().getLocation().getLattitude();
        int lon = state.getContext().getLocation().getLongitude();
        args[0] = Math.abs(lat)/60;
        args[1] = Math.abs(lat)%60;
        args[2] = (lat < 0) ? "{{North}}" : "{{South}}";
        args[3] = Math.abs(lon)/60;
        args[4] = Math.abs(lon)%60;
        args[5] = (lon < 0) ? "{{West}}" : "{{East}}";
        return new AudioMessageBean(CompanionsModelConst.TEXT_YOU_ARE_AT_AAA_DEGREES_BBB_MINUTES_CCC_LATITUDE_AND_DDD_DEGREES_EEE_MINUTES_FFF_LONGITUDE, args);
    }
    
    public static AudioMessageBean makeWestMessage(CompState state)
    {
        return new AudioMessageBean(
                CompanionsModelConst.TEXT_OFF_TO_THE_WEST_YOU_CAN_SEE_XXX,
                CompConstLogic.TERRAIN_NAMES[state.getContext()
                        .getSquareWest().getTerrain()]);
    }

    public static AudioMessageBean makeEastMessage(CompState state)
    {
        return new AudioMessageBean(
                CompanionsModelConst.TEXT_OFF_TO_THE_EAST_YOU_CAN_SEE_XXX,
                CompConstLogic.TERRAIN_NAMES[state.getContext()
                        .getSquareEast().getTerrain()]);
    }

    public static AudioMessageBean makeSouthMessage(CompState state)
    {
        return new AudioMessageBean(
                CompanionsModelConst.TEXT_OFF_TO_THE_SOUTH_YOU_CAN_SEE_XXX,
                CompConstLogic.TERRAIN_NAMES[state.getContext()
                        .getSquareSouth().getTerrain()]);
    }

    public static AudioMessageBean makeNorthMessage(CompState state)
    {
        return new AudioMessageBean(
                CompanionsModelConst.TEXT_OFF_TO_THE_NORTH_YOU_CAN_SEE_XXX,
                CompConstLogic.TERRAIN_NAMES[state.getContext()
                        .getSquareNorth().getTerrain()]);
    }

    private static AudioMessageBean getSign(CompState state, int idx)
    {
        List<SignPost> signposts = state.getContext().getSquare().getSignposts();
        AudioMessageBean dir = null;
        switch (signposts.get(idx).getDirection())
        {
            case CompOperationBean.NORTH:
                dir = new AudioMessageBean(CompanionsModelConst.TEXT_NORTH);
                break;
            case CompOperationBean.SOUTH:
                dir = new AudioMessageBean(CompanionsModelConst.TEXT_SOUTH);
                break;
            case CompOperationBean.EAST:
                dir = new AudioMessageBean(CompanionsModelConst.TEXT_EAST);
                break;
            case CompOperationBean.WEST:
                dir = new AudioMessageBean(CompanionsModelConst.TEXT_WEST);
                break;
        }
        AudioMessageBean post = new AudioMessageBean(CompanionsModelConst.TEXT_XXX_TO_YYY,
                dir, state.getContext().getSignpostNames().get(idx));
        return post;
    }
    
    public static void doMoreLookTactical(CompState state)
    {
        CompRoomBean room = state.getContext().getRoom();
        if (room.getType().equals(CompRoomBean.TYPE_FIGHTERS_GUILD))
        {
            doMoreFightersGuild(state, room);
            return;
        }
        JSONObject params = room.getParams();
        if (params != null) 
        {
            if (params.containsKey("bounties"))
                doMoreBounties(state, params);
            else if (params.containsKey("moreText"))
                doMoreText(state, params);
        }
        else
            state.respond(CompanionsModelConst.TEXT_THERE_IS_NO_MORE_TO_SEE_HERE);
    }

    public static void doMoreText(CompState state, JSONObject params)
    {
        JSONArray text = JSONUtils.getArray(params, "moreText");
        int idx = (state.getMoreDepth() - 1)%text.size();
        String txt = (String)text.get(idx);
        state.respond(txt);
        if (idx < text.size() - 1)
            state.respond(CompanionsModelConst.TEXT_MORE_SOUND);
    }

    public static void doMoreBounties(CompState state, JSONObject params)
    {
        List<JSONObject> bounties = getUnfilledBounties(state, params);
        int idx = (state.getMoreDepth() - 1)%bounties.size();
        JSONObject bounty = bounties.get(idx);
        CoordBean ords = new CoordBean(JSONUtils.getString(bounty, "ords"));
        int reward = JSONUtils.getInt(bounty, "reward");
        List<String> dirs = new ArrayList<>();
        int dx = ords.getX() - state.getContext().getLocation().getX();
        int dy = ords.getY() - state.getContext().getLocation().getY();
        if (dx < -1)
            dirs.add(state.resolve(CompanionsModelConst.TEXT_XXX_MILES_WEST, -dx));
        else if (dx == -1)
            dirs.add(state.resolve(CompanionsModelConst.TEXT_ONE_MILE_WEST));
        else if (dx > 1)
            dirs.add(state.resolve(CompanionsModelConst.TEXT_XXX_MILES_EAST, dx));
        else if (dx == 1)
            dirs.add(state.resolve(CompanionsModelConst.TEXT_ONE_MILE_EAST, dx));
        if (dy < -1)
            dirs.add(state.resolve(CompanionsModelConst.TEXT_XXX_MILES_NORTH, -dy));
        if (dy == -1)
            dirs.add(state.resolve(CompanionsModelConst.TEXT_ONE_MILE_NORTH, -dy));
        else if (dy > 1)
            dirs.add(state.resolve(CompanionsModelConst.TEXT_XXX_MILES_SOUTH, dy));
        else if (dy == 1)
            dirs.add(state.resolve(CompanionsModelConst.TEXT_ONE_MILE_SOUTH, dy));
        state.respond(CompanionsModelConst.TEXT_CLEARING_THE_MONSTERS_OUT_XXX_OF_HERE_WILL_GAIN_YOU_YYY_GOLD_PIECES,
                ResponseUtils.wordList(dirs), reward);
        if (idx < bounties.size() - 1)
            state.respond(CompanionsModelConst.TEXT_MORE_SOUND);
    }

    @SuppressWarnings("unchecked")
    private static List<AudioMessageBean> getMoreItemShop(CompState state, JSONObject params)
    {
        String shopType = params.getString("shopType");
        JSONArray items;
        final JSONObject itemQuan = new JSONObject();
        if ("storage".equals(shopType))
        {
            String loc = state.getUser().getLocation();
            JSONObject iq = JSONUtils.getObject(state.getUser().getMetadata(), "storage."+loc);
            if (iq != null)
            {
                for (String key : iq.keySet())
                    itemQuan.put(key, iq.get(key));
            }
            items = new JSONArray();
            items.addAll(itemQuan.keySet());
        }
        else
            items = (JSONArray)params.get("items");
        List<AudioMessageBean> msgs = new ArrayList<>();
        if (items.size() == 0)
        {
            if ("storage".equals(shopType))
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_THERE_IS_NOTHING_STORED_IN_THIS_ROOM));
            else
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_THERE_IS_NOTHING_FOR_SALE_IN_THIS_SHOP));
            return msgs;
        }
        List<List<CompItemTypeBean>> goods = new ArrayList<>();
        List<CompItemTypeBean> acc = new ArrayList<>();
        int itemType = 0;
        for (int i = 0; i < items.size(); i++)
        {
            String id = (String)items.get(i);
            CompItemTypeBean type = ItemLogic.getItemType(id);
            acc.add(type);
            if (i == 0)
                itemType = type.getType();
            else if (itemType != type.getType())
                itemType = -1;
            if (acc.size() == 3)
            {
                goods.add(acc);
                acc = new ArrayList<>();
            }
        }
        if (acc.size() > 0)
            goods.add(acc);
        // detail 0
        if ("storage".equals(shopType))
            msgs.addAll(composeItemMore(goods, CompanionsModelConst.TEXT_THIS_ROOM_CONTAINS_XXX, CompanionsModelConst.TEXT_XXXSTRING_YYYSTRING, null, 
                (type) -> new Object[] { String.valueOf(itemQuan.get(type.getID())), type.getName() }));
        else
            msgs.addAll(composeItemMore(goods, CompanionsModelConst.TEXT_THIS_SHOP_SELLS_XXX, CompanionsModelConst.TEXT_XXX_FOR_YYY, null, 
                    (type) -> new Object[] { type.getName(), (int)type.getCost() }));
        switch (itemType)
        {
            case CompItemTypeBean.TYPE_AMMO:
                // detail 1
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_DOES_ABOUT_YYY_DAMAGE, null, 
                        (type) -> new Object[] { type.getName(), (int)type.getDamageSMRoll().average() }));
                // detail == 2)
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_IS_SOLD_IN_BUNDLES_OF_YYY, CompanionsModelConst.TEXT_XXX_IN_BUNDLES_OF_YYY, 
                        (type) -> new Object[] { type.getName(), type.getCount() }));
                // detail == 3
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, null, 
                        (type) -> new Object[] { type.getName(), (int)type.getEncumbrance() }));
                break;
            case CompItemTypeBean.TYPE_ARMOR:
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_IMPROVES_YOUR_ARMOR_BY_YYY, CompanionsModelConst.TEXT_XXX_BY_YYY, 
                        (type) -> new Object[] { type.getName(), type.getACMod() }));
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, null, 
                        (type) -> new Object[] { type.getName(), (int)type.getEncumbrance() }));
                break;
            case CompItemTypeBean.TYPE_HAND:
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_DOES_ABOUT_YYY_DAMAGE, null, 
                        (type) -> new Object[] { type.getName(), (int)type.getDamageSMRoll().average() }));
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_REQUIRES_YYY_HANDS_TO_WIELD, null, 
                        (type) -> new Object[] { type.getName(), type.getHandsNeeded() }));
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, null, 
                        (type) -> new Object[] { type.getName(), (int)type.getEncumbrance() }));
                break;
            case CompItemTypeBean.TYPE_HURLED:
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_DOES_ABOUT_YYY_DAMAGE, null, 
                        (type) -> new Object[] { type.getName(), (int)type.getDamageSMRoll().average() }));
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_IS_SOLD_IN_BUNDLES_OF_YYY, CompanionsModelConst.TEXT_XXX_IN_BUNDLES_OF_YYY, 
                        (type) -> new Object[] { type.getName(), type.getCount() }));
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, null, 
                        (type) -> new Object[] { type.getName(), (int)type.getEncumbrance() }));
                break;
            case CompItemTypeBean.TYPE_LAUNCHER:
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_DOES_ABOUT_YYY_DAMAGE, null, 
                        (type) -> new Object[] { type.getName(), (int)type.getDamageSMRoll().average() }));
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_REQUIRES_YYY_HANDS_TO_WIELD, null, 
                        (type) -> new Object[] { type.getName(), type.getHandsNeeded() }));
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, null, 
                        (type) -> new Object[] { type.getName(), (int)type.getEncumbrance() }));
                break;
            case CompItemTypeBean.TYPE_SHIELD:
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_IMPROVES_YOUR_ARMOR_BY_YYY, CompanionsModelConst.TEXT_XXX_BY_YYY, 
                        (type) -> new Object[] { type.getName(), type.getACMod() }));
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, null, 
                        (type) -> new Object[] { type.getName(), (int)type.getEncumbrance() }));
                break;
            case -1: // mixed, i.e. gypsy
                msgs.addAll(composeItemMore(goods, null,CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, null, 
                        (type) -> new Object[] { type.getName(), (int)type.getEncumbrance() }));
                break;
        }
        return msgs;
    }

    private static List<AudioMessageBean> composeItemMore(List<List<CompItemTypeBean>> goods,
            String sentance, String initialKey, String subsequentKey,
            Function<CompItemTypeBean, Object[]> args)
    {
        if (sentance == null)
            sentance = CompanionsModelConst.TEXT_SENTANCE;
        if (subsequentKey == null)
            subsequentKey = initialKey;
        List<AudioMessageBean> msgs = new ArrayList<>();
        for (List<CompItemTypeBean> types : goods)
        {
            List<AudioMessageBean> vocab = new ArrayList<>();
            for (CompItemTypeBean type : types)
            {
                if (vocab.size() == 0)
                    vocab.add(new AudioMessageBean(initialKey, args.apply(type)));
                else
                    vocab.add(new AudioMessageBean(subsequentKey, args.apply(type)));
            }
            AudioMessageBean and = new AudioMessageBean(AudioMessageBean.AND, vocab.toArray());
            AudioMessageBean s = new AudioMessageBean(sentance, and);
            DebugUtils.trace("LookLogic.composeItemMore, msg="+s.toString());
            msgs.add(s);
        }
        return msgs;
    }
    
    public static void doMoreFightersGuild(CompState state, CompRoomBean room)
    {
        if (state.getMoreDepth() >= 3)
        {
            state.respond(CompanionsModelConst.TEXT_THERE_IS_NO_MORE_TO_SEE_HERE);
            return;
        }
        JSONArray hires = (JSONArray)room.getParams().get("hires");
        for (int i = 0; i < hires.size(); i++)
        {
            JSONObject json = (JSONObject)hires.get(i);
            CompCompanionBean hire = new CompCompanionBean();
            hire.fromJSON(json);
            if (state.getUser().getCompanion(hire.getID()) != null)
                continue;
            switch (state.getMoreDepth())
            {
                case 1:
                    state.respond(
                            CompanionsModelConst.TEXT_XXX_IS_A_YYY_WITH_A_STRENGTH_OF_ZZZ,
                            hire.getName(),
                            EnumerationUtils.ORDINAL[hire.getLevel() - 1],
                            CompConstLogic.RACE_NAMES[hire.getRace()],
                            hire.getSTRModified(), hire.getDEXModified(), hire.getCONModified(),
                            hire.getINTModified(), hire.getWISModified(), hire.getCHAModified());
                    break;
                case 2:
                    List<String> profs = ListLogic.itemIDs(hire.getProficiencies());
                    if (profs.size() > 0)
                        state.respond(
                                CompanionsModelConst.TEXT_XXX_IN_PROFICIENT_IN_YYY,
                                hire.getName(), ResponseUtils.wordList(profs));
                    break;
            }
        }
        if (state.getMoreDepth() < 2)
            state.respond(CompanionsModelConst.TEXT_MORE_SOUND);
    }
}
