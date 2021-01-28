package jo.audio.compedit.app.logic;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.data.build.PFeatureBean;
import jo.audio.companions.data.build.PRoomBean;
import jo.audio.compedit.data.CompEditModuleBean;
import jo.audio.compedit.data.CompEditState;
import jo.audio.compedit.slu.CompEditModelConst;
import jo.audio.util.PhoneticMatchLogic;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.obj.StringUtils;

public class FeatureLogic
{
    public static void listFeatures(CompEditState state)
    {
        CompEditModuleBean module = state.getContext().getModule();
        if ((module == null) || (module.getFeatures().size() == 0))
        {
            state.respond(CompEditModelConst.TEXT_YOU_HAVE_NOT_YET_CREATED_ANY_FEATURES);
            state.respond(CompEditModelConst.TEXT_SAY_NEW_FEATURE_TO_CREATE_A_FEATURE);
            return;
        }
        List<String> featureNames = getFeatureNames(module);
        state.respond(CompEditModelConst.TEXT_YOU_HAVE_CREATED_THE_FOLLOWING_FEATURES_XXX,
                new AudioMessageBean(AudioMessageBean.AND, featureNames.toArray()));
    }

    public static List<String> getFeatureNames(CompEditModuleBean module)
    {
        List<String> featureNames = new ArrayList<>();
        for (PFeatureBean feature : module.getFeatures())
            featureNames.add(feature.getName());
        return featureNames;
    }

    public static void loadFeature(CompEditState state, String featureName)
    {
        CompEditModuleBean module = state.getContext().getModule();
        if ((module == null) || (module.getFeatures().size() == 0))
        {
            state.respond(CompEditModelConst.TEXT_YOU_HAVE_NO_FEATURES_TO_LOAD);
            state.respond(CompEditModelConst.TEXT_SAY_NEW_FEATURE_TO_CREATE_A_FEATURE);
            return;
        }
        if (StringUtils.isTrivial(featureName))
        {
            state.respond(CompEditModelConst.TEXT_SAY_LOAD_FEATURE_FOLLOWED_BY_THE_NAME_OF_THE_FEATURE_YOU_WANT_TO_LOAD);
            state.respond(CompEditModelConst.TEXT_OR_SAY_NEW_FEATURE_TO_CREATE_A_NEW_ONE);
            return;
        }
        List<String> featureNames = getFeatureNames(module);
        int idx = PhoneticMatchLogic.findMatchIdx(featureName, featureNames);
        if (idx < 0)
        {
            state.respond(CompEditModelConst.TEXT_I_COULDNT_FIND_FEATURE_XXX, featureName);
            state.respond(CompEditModelConst.TEXT_SAY_LIST_FEATURES_TO_FIND_OUT_WHAT_FEATURES_YOU_HAVE);
            state.respond(CompEditModelConst.TEXT_IF_IM_HAVING_TROUBLE_RECOGNIZING_YOUR_FEATURE);
            state.respond(CompEditModelConst.TEXT_OR_SAY_NEW_FEATURE_TO_CREATE_A_NEW_ONE);
            return;
        }
        PFeatureBean feature = module.getFeatures().get(idx);
        OperationLogic.selectFeature(state, feature);
        LookLogic.doLook(state);
    }

    public static void newFeature(CompEditState state, String featureName)
    {
        if (StringUtils.isTrivial(featureName))
            featureName = state.resolve(CompEditModelConst.TEXT_NEW_FEATURE_NAME);
        OperationLogic.newFeature(state, featureName);
        if (!state.getContext().isError())
        {
            state.respond(CompEditModelConst.TEXT_CREATED_FEATURE_CALLED_XXX, state.getContext().getFeature().getName());
            LookLogic.doLook(state);
        }
        else
            state.respond(CompEditModelConst.TEXT_COULD_NOT_CREATE_FEATURE_CALLED_XXX, state.getContext().getFeature().getName());
    }

    public static void reportFeatureName(CompEditState state)
    {
        PFeatureBean feature = state.getContext().getFeature();
        if (feature == null)
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LOAD_FEATURE_FOLLOWED_BY_THE_NAME_OF_THE_FEATURE_YOU_WANT_TO_LOAD);
            return;
        }
        state.respond(CompEditModelConst.TEXT_THIS_FEATURE_IS_CALLED_XXX, feature.getName());
        state.respond(CompEditModelConst.TEXT_TO_CHANGE_THE_FEATURE_NAME__SAY_SET_FEATURE_NAME_FOLLOWED_BY_THE_NEW_NAME);
    }

    public static void setFeatureName(CompEditState state, String featureName)
    {
        PFeatureBean feature = state.getContext().getFeature();
        if (feature == null)
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LOAD_FEATURE_FOLLOWED_BY_THE_NAME_OF_THE_FEATURE_YOU_WANT_TO_LOAD);
            return;
        }
        OperationLogic.setFeatureName(state, featureName);
        if (!state.getContext().isError())
        {
            state.respond(CompEditModelConst.TEXT_THIS_FEATURE_IS_CALLED_XXX, feature.getName());
            LookLogic.doLook(state);
        }
        else
            state.respond(CompEditModelConst.TEXT_COULD_NOT_NAME_FEATURE_XXX, featureName);
    }

    public static void setRoomName(CompEditState state, String text)
    {
        PRoomBean room = state.getContext().getRoom();
        if (room == null)
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LOAD_FEATURE_FOLLOWED_BY_THE_NAME_OF_THE_FEATURE_YOU_WANT_TO_LOAD);
            return;
        }
        OperationLogic.setRoomName(state, text);
        if (!state.getContext().isError())
        {
            state.respond(CompEditModelConst.TEXT_THIS_ROOM_IS_CALLED_XXX, room.getName());
            LookLogic.doLook(state);
        }
        else
            state.respond(CompEditModelConst.TEXT_COULD_NOT_NAME_ROOM_XXX, text);
    }

    public static void reportRoomName(CompEditState state)
    {
        PRoomBean room = state.getContext().getRoom();
        if (room == null)
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LOAD_FEATURE_FOLLOWED_BY_THE_NAME_OF_THE_FEATURE_YOU_WANT_TO_LOAD);
            return;
        }
        state.respond(CompEditModelConst.TEXT_THIS_ROOM_IS_CALLED_XXX, room.getName());
        state.respond(CompEditModelConst.TEXT_TO_CHANGE_THE_ROOM_NAME__SAY_SET_ROOM_NAME_FOLLOWED_BY_THE_NEW_NAME);
    }

    public static void setRoomDescription(CompEditState state, String text)
    {
        PRoomBean room = state.getContext().getRoom();
        if (room == null)
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LOAD_FEATURE_FOLLOWED_BY_THE_NAME_OF_THE_FEATURE_YOU_WANT_TO_LOAD);
            return;
        }
        text = Character.toUpperCase(text.charAt(0)) + text.substring(1) + ".";
        OperationLogic.setRoomDescription(state, text);
        if (!state.getContext().isError())
        {
            LookLogic.doLook(state);
        }
        else
            state.respond(CompEditModelConst.TEXT_COULD_NOT_DESCRIBE_ROOM_XXX, text);
    }

    public static void reportRoomDescription(CompEditState state)
    {
        PRoomBean room = state.getContext().getRoom();
        if (room == null)
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LOAD_FEATURE_FOLLOWED_BY_THE_NAME_OF_THE_FEATURE_YOU_WANT_TO_LOAD);
            return;
        }
        LookLogic.doLook(state);
        state.respond(CompEditModelConst.TEXT_TO_CHANGE_THE_ROOM_DESCRIPTION__SAY_SET_ROOM_DESCRIPTION_FOLLOWED_BY_THE_NEW_DESCRIPTION);
    }

    public static Boolean setRoomMetadata(CompEditState state, String key, Object value)
    {
        PRoomBean room = state.getContext().getRoom();
        if (room == null)
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LOAD_FEATURE_FOLLOWED_BY_THE_NAME_OF_THE_FEATURE_YOU_WANT_TO_LOAD);
            return null;
        }
        OperationLogic.setRoomMetadata(state, key, value);
        return !state.getContext().isError(); 
    }
    
    static int resolveDirection(CompEditState state, String direction)
    {
        List<String> vocab = new ArrayList<>();
        vocab.add(state.resolve(CompEditModelConst.TEXT_NORTH));
        vocab.add(state.resolve(CompEditModelConst.TEXT_SOUTH));
        vocab.add(state.resolve(CompEditModelConst.TEXT_EAST));
        vocab.add(state.resolve(CompEditModelConst.TEXT_WEST));
        int idx = PhoneticMatchLogic.findMatchIdx(direction, vocab);
        if (idx < 0)
            return -1;
        return idx + 1;
    }

    public static void digRoom(CompEditState state, String direction)
    {
        PRoomBean room = state.getContext().getRoom();
        if (room == null)
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LOAD_FEATURE_FOLLOWED_BY_THE_NAME_OF_THE_FEATURE_YOU_WANT_TO_LOAD);
            return;
        }
        int dir = resolveDirection(state, direction);
        if (dir < 0)
        {
            state.respond(CompEditModelConst.TEXT_I_DON_T_KNOW_WHAT_DIRECTION_XXX_IS, direction);
            return;
        }
        if (!StringUtils.isTrivial(room.getDir(PRoomBean.DIRS[dir - 1])))
        {
            state.respond(CompEditModelConst.TEXT_YOU_ALREADY_HAVE_AN_EXIT_IN_DIRECTION_XXX, direction);
            return;
        }
        OperationLogic.digRoom(state, dir);
        if (state.getContext().isError())
            state.respond(CompEditModelConst.TEXT_I_WAS_UNABLE_TO_DIG_XXX, direction);
        else
            LookLogic.doLook(state);
    }

    public static void linkRoom(CompEditState state, String direction, String roomName)
    {
        PRoomBean room = state.getContext().getRoom();
        if (room == null)
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LOAD_FEATURE_FOLLOWED_BY_THE_NAME_OF_THE_FEATURE_YOU_WANT_TO_LOAD);
            return;
        }
        int dir = resolveDirection(state, direction);
        if (dir < 0)
        {
            state.respond(CompEditModelConst.TEXT_I_DON_T_KNOW_WHAT_DIRECTION_XXX_IS, direction);
            return;
        }
        if (!StringUtils.isTrivial(room.getDir(PRoomBean.DIRS[dir - 1])))
        {
            state.respond(CompEditModelConst.TEXT_YOU_ALREADY_HAVE_AN_EXIT_IN_DIRECTION_XXX, direction);
            return;
        }
        String roomID;
        if (roomName.equalsIgnoreCase("$exit") || roomName.equalsIgnoreCase("exit"))
            roomID = "$exit";
        else
        {
            PRoomBean room2 = resolveRoom(state, roomName);
            if (room2 == null)
            {
                state.respond(CompEditModelConst.TEXT_I_CAN_T_FIND_A_ROOM_BY_THE_NAME_OF_XXX);
                return;
            }
            roomID = room2.getID();
        }
        OperationLogic.linkRoom(state, dir, roomID);
        if (state.getContext().isError())
            state.respond(CompEditModelConst.TEXT_I_WAS_UNABLE_TO_LINK_XXX_TO_YYY, direction, roomName);
        else
            LookLogic.doLook(state);
    }
    
    static PRoomBean resolveRoom(CompEditState state, String name)
    {
        List<String> vocab = new ArrayList<>();
        for (PRoomBean room : state.getContext().getFeature().getRooms())
            vocab.add(room.getName());
        int idx = PhoneticMatchLogic.findMatchIdx(name, vocab);
        if (idx < 0)
            return null;
        return state.getContext().getFeature().getRooms().get(idx);
    }

    public static void unlinkRoom(CompEditState state, String direction)
    {
        PRoomBean room = state.getContext().getRoom();
        if (room == null)
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LOAD_FEATURE_FOLLOWED_BY_THE_NAME_OF_THE_FEATURE_YOU_WANT_TO_LOAD);
            return;
        }
        int dir = resolveDirection(state, direction);
        if (dir < 0)
        {
            state.respond(CompEditModelConst.TEXT_I_DON_T_KNOW_WHAT_DIRECTION_XXX_IS, direction);
            return;
        }
        if (StringUtils.isTrivial(room.getDir(PRoomBean.DIRS[dir - 1])))
        {
            state.respond(CompEditModelConst.TEXT_YOU_DON_T_HAVE_AN_EXIT_IN_DIRECTION_XXX, direction);
            return;
        }
        OperationLogic.unlinkRoom(state, dir);
        if (state.getContext().isError())
            state.respond(CompEditModelConst.TEXT_I_WAS_UNABLE_TO_UNLINK_XXX, direction);
        else
            LookLogic.doLook(state);
    }
}
