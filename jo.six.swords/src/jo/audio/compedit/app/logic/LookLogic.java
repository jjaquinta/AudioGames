package jo.audio.compedit.app.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.logic.MonsterLogic;
import jo.audio.companions.tools.gui.edit.data.PFeatureBean;
import jo.audio.companions.tools.gui.edit.data.PRoomBean;
import jo.audio.compedit.data.CompEditState;
import jo.audio.compedit.slu.CompEditModelConst;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.obj.StringUtils;

public class LookLogic
{
    
    private static final String[] DIR_EXIT = {
        CompEditModelConst.TEXT_NORTH_LEADS_OUT_OF_HERE,
        CompEditModelConst.TEXT_SOUTH_LEADS_OUT_OF_HERE,
        CompEditModelConst.TEXT_EAST_LEADS_OUT_OF_HERE,
        CompEditModelConst.TEXT_WEST_LEADS_OUT_OF_HERE,
    };

    public static void doLook(CompEditState state)
    {
        PFeatureBean feature = state.getContext().getFeature();
        PRoomBean room = state.getContext().getRoom();
        if ((feature == null) || (room == null))
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LIST_FEATURES_TO_FIND_OUT_WHAT_FEATURES_YOU_HAVE);
            return;
        }
        state.respond(room.getDescription());
        state.respond(CompEditModelConst.TEXT_EOLN);
        if ((room.getParams() != null) && room.getParams().containsKey(CompRoomBean.MD_ENCOUNTER_ID))
        {
            String monstID = room.getParams().getString(CompRoomBean.MD_ENCOUNTER_ID);
            CompMonsterTypeBean monst = MonsterLogic.getMonsterType(monstID);
            if (monst != null)
            {
                state.respond(CompEditModelConst.TEXT_THE_MONSTER_FOR_THIS_ROOM_IS_SET_TO_XXX, monst.getName());
                state.respond(CompEditModelConst.TEXT_EOLN);
                if (room.getParams().containsKey(CompRoomBean.MD_ENCOUNTER_CHALLENGE))
                {
                    Integer chMod = JSONUtils.getInt(room.getParams(), CompRoomBean.MD_ENCOUNTER_CHALLENGE);
                    if (chMod != null)
                    {
                        if (chMod < -1)
                            state.respond(CompEditModelConst.TEXT_THE_CHALLENGE_RATING_FOR_THIS_ROOM_IS_XXX_LEVELS_EASIER, -chMod);
                        else if (chMod < 0)
                            state.respond(CompEditModelConst.TEXT_THE_CHALLENGE_RATING_FOR_THIS_ROOM_IS_XXX_LEVEL_EASIER, -chMod);
                        else if (chMod > 1)
                            state.respond(CompEditModelConst.TEXT_THE_CHALLENGE_RATING_FOR_THIS_ROOM_IS_XXX_LEVELS_HARDER, chMod);
                        else if (chMod > 0)
                            state.respond(CompEditModelConst.TEXT_THE_CHALLENGE_RATING_FOR_THIS_ROOM_IS_XXX_LEVEL_HARDER, chMod);
                    }
                }
            }
        }
        AudioMessageBean ourName = new AudioMessageBean(AudioMessageBean.RAW, room.getName());
        AudioMessageBean[] names = new AudioMessageBean[4];
        for (int dir = 0; dir < PRoomBean.DIRS.length; dir++)
        {
            if ("$exit".equals(room.getDir(PRoomBean.DIRS[dir])))
                state.respond(DIR_EXIT[dir]);
            else
            {
                PRoomBean north = findRoom(feature, room.getDir(PRoomBean.DIRS[dir]));
                if (north != null)
                    names[dir] = new AudioMessageBean(AudioMessageBean.RAW, north.getName());
            }
        }
        respondWithDirections(state, names, ourName);
//        JSONObject params = room.getParams();
//        if (room.getType().equals(CompRoomBean.TYPE_ITEM_SHOP))
//            more = doLookShop(state, params);
//        else if (room.getType().equals(CompRoomBean.TYPE_FIGHTERS_GUILD))
//            more = doLookHires(state, params);
//        if (params != null)
//        {
//            if (params.containsKey("bounties"))
//                more |= doLookBounties(state, params);
//            else if (params.containsKey("moreText"))
//                more |= true;
//            if (params.containsKey("prompt"))
//            {
//                JSONObject prompt = JSONUtils.getObject(params, "prompt");
//                String intent = JSONUtils.getString(prompt, "intent");
//                String key = JSONUtils.getString(prompt, "key");
//                JSONArray jargs = JSONUtils.getArray(prompt, "args");
//                Object[] args;
//                if (jargs == null)
//                    args = new Object[0];
//                else
//                    args = jargs.toArray();
//                state.prompt(intent, key, args);
//            }
//        }
//        if (more)
//        {
//            state.respond(CompanionsModelConst.TEXT_MORE_SOUND);
//            state.setMoreIntent(CompanionsModelConst.INTENT_LOOK);
//            state.setMoreDepth(0);
//        }
    }

    private static final String[] DIR_NAMES = {
        CompEditModelConst.TEXT_NORTH,
        CompEditModelConst.TEXT_SOUTH,
        CompEditModelConst.TEXT_EAST,
        CompEditModelConst.TEXT_WEST,
    };

    private static void respondWithDirections(CompEditState state, AudioMessageBean[] names, AudioMessageBean ourName)
    {
        Map<AudioMessageBean,List<String>> destinations = new HashMap<AudioMessageBean, List<String>>();
        for (int dir = 0; dir < names.length; dir++)
        {
            AudioMessageBean name = names[dir];
            if (name == null)
                continue;
            List<String> nameDests = destinations.get(name);
            if (nameDests == null)
            {
                nameDests = new ArrayList<>();
                destinations.put(name, nameDests);
            }
            nameDests.add("{{"+DIR_NAMES[dir]+"}}");
        }
        for (AudioMessageBean name : destinations.keySet())
        {
            List<String> directions = destinations.get(name);
            if (name.equals(ourName))
                state.respond(CompEditModelConst.TEXT_XXX_CONTINUES_YYY, 
                        name, ResponseUtils.wordList(directions));
            else
                state.respond(CompEditModelConst.TEXT_XXX_LEADS_TO_YYY, 
                    ResponseUtils.wordList(directions), name);
        }
    }

    private static PRoomBean findRoom(PFeatureBean feature, String dir)
    {
        for (PRoomBean room : feature.getRooms())
            if (room.getID().equals(dir))
                return room;
        return null;
    }

    public static void move(CompEditState state, String direction)
    {
        int dir = FeatureLogic.resolveDirection(state, direction);
        if (dir < 0)
        {
            state.respond(CompEditModelConst.TEXT_I_DON_T_KNOW_WHAT_DIRECTION_XXX_IS, direction);
            return;
        }
        move(state, dir);
    }
    
    public static void move(CompEditState state, int dir)
    {
        PRoomBean room = state.getContext().getRoom();
        if (room == null)
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LOAD_FEATURE_FOLLOWED_BY_THE_NAME_OF_THE_FEATURE_YOU_WANT_TO_LOAD);
            return;
        }
        if (StringUtils.isTrivial(room.getDir(PRoomBean.DIRS[dir - 1])))
        {
            state.respond(CompEditModelConst.TEXT_THERE_IS_NO_EXIT_IN_DIRECTION_XXX, "{{"+PRoomBean.DIRS[dir - 1]+"}}");
            return;
        }
        OperationLogic.move(state, dir);
        if (state.getContext().isError())
            state.respond(CompEditModelConst.TEXT_I_WAS_UNABLE_TO_MOVE_XXX, "{{"+PRoomBean.DIRS[dir - 1]+"}}");
        else
            LookLogic.doLook(state);
    }

}
