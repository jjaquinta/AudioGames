package jo.audio.compedit.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.build.PFeatureBean;
import jo.audio.companions.data.build.PRoomBean;
import jo.audio.compedit.app.logic.FeatureLogic;
import jo.audio.compedit.app.logic.LookLogic;
import jo.audio.compedit.data.CompEditState;
import jo.audio.compedit.slu.CompEditModelConst;
import jo.audio.util.model.data.AudioMessageBean;
import jo.ipa.logic.FuzzyMatchLogic;
import jo.ipa.logic.IPAComp;
import jo.ipa.logic.IPADictionary;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class MonsterLogic
{
    private static IPADictionary mMonsterDictionary = null;
    
    public static void doMonster(CompEditState state, String topic)
    {
        if (StringUtils.isTrivial(topic))
        {
            listMonsterSetting(state);
            return;
        }
        setupDictionaries(state);
        List<IPAComp> matches = FuzzyMatchLogic.findMatches(mMonsterDictionary, topic, 5);
        DebugUtils.trace("Ranked matches:");
        for (int i = 0; i < matches.size(); i++)
            DebugUtils.trace("  "+matches.get(i)+", adjDist="+matches.get(i).getAdjustedDistance());
        if (matches.size() == 0)
        {
            listMonsterSetting(state);
            return;
        }
        IPAComp match = FuzzyMatchLogic.resolveMatches(matches);
        if (match != null)
        {
            Object md = match.getWord2().getMetadata();
            if ("$delete".equals(md))
            {
                Boolean didIt = FeatureLogic.setRoomMetadata(state, CompRoomBean.MD_ENCOUNTER_ID, null);
                if (didIt != null) 
                    if (didIt)
                    {
                        state.respond(CompEditModelConst.TEXT_THE_MONSTER_HAS_BEEN_CLEARED_FROM_THIS_ROOM);
                        LookLogic.doLook(state);
                    }
                    else
                        state.respond(CompEditModelConst.TEXT_CANNOT_CLEAR_THE_MONSTER_FROM_THIS_ROOM);
                return;
            }
            if (md != null)
            {
                @SuppressWarnings("unchecked")
                List<String> ids = (List<String>)md;
                CompMonsterTypeBean monst = jo.audio.companions.logic.MonsterLogic.getMonsterType(ids.get(0));
                Boolean didIt = FeatureLogic.setRoomMetadata(state, CompRoomBean.MD_ENCOUNTER_ID, monst.getID());
                if (didIt != null) 
                    if (didIt)
                    {
                        state.respond(CompEditModelConst.TEXT_THE_MONSTER_FOR_THIS_ROOM_IS_SET_TO_XXX, monst.getName());
                        LookLogic.doLook(state);
                    }
                    else
                        state.respond(CompEditModelConst.TEXT_CANNOT_SET_THE_MONSTER_FOR_THIS_ROOM_TO_XXX, monst.getName());
                return;
            }
        }
        else if (matches.size() > 0)
        {
            state.respond(CompEditModelConst.TEXT_IM_NOT_SURE_WHAT_XXX_IS, topic);
            List<String> names = new ArrayList<>();
            for (IPAComp m : matches)
                names.add(m.getWord2().getWord());
            state.respond(CompEditModelConst.TEXT_DID_YOU_MEAN_XXX, AudioMessageBean.or(names));
            return;
        }
        listMonsterSetting(state);
    }

    private static void setupDictionaries(CompEditState state)
    {
        if (mMonsterDictionary != null)
            return;
        mMonsterDictionary = new IPADictionary();
        Map<String, List<String>> nameToID = new HashMap<String, List<String>>();
        for (CompMonsterTypeBean monster : jo.audio.companions.logic.MonsterLogic.getIndexedTypes())
        {
            String name = monster.getName();
            String id = monster.getID();
            List<String> ids = nameToID.get(name);
            if (ids == null)
            {
                ids = new ArrayList<String>();
                nameToID.put(name, ids);
            }
            ids.add(id);
        }
        for (String name : nameToID.keySet())
            FuzzyMatchLogic.addToDictionary(mMonsterDictionary, nameToID.get(name), name);
        FuzzyMatchLogic.addToDictionary(mMonsterDictionary, "$delete", "nothing");
        FuzzyMatchLogic.addToDictionary(mMonsterDictionary, "$delete", "nobody");
        FuzzyMatchLogic.addToDictionary(mMonsterDictionary, "$delete", "empty");
    }
    
    private static void listMonsterSetting(CompEditState state)
    {
        PFeatureBean feature = state.getContext().getFeature();
        PRoomBean room = state.getContext().getRoom();
        if ((feature == null) || (room == null))
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LIST_FEATURES_TO_FIND_OUT_WHAT_FEATURES_YOU_HAVE);
            return;
        }
        if ((room.getParams() != null) && room.getParams().containsKey(CompRoomBean.MD_ENCOUNTER_ID))
        {
            String monstID = room.getParams().getString(CompRoomBean.MD_ENCOUNTER_ID);
            CompMonsterTypeBean monst = jo.audio.companions.logic.MonsterLogic.getMonsterType(monstID);
            if (monst != null)
            {
                state.respond(CompEditModelConst.TEXT_THE_MONSTER_FOR_THIS_ROOM_IS_SET_TO_XXX, monst.getName());
                return;
            }
        }
        state.respond(CompEditModelConst.TEXT_THERE_IS_NO_MONSTER_SET_ON_THIS_ROOM);
    }

    public static void doChallengeDelta(CompEditState state, int delta)
    {
        PFeatureBean feature = state.getContext().getFeature();
        PRoomBean room = state.getContext().getRoom();
        if ((feature == null) || (room == null))
        {
            state.respond(CompEditModelConst.TEXT_YOU_DONT_CURRENTLY_HAVE_A_FEATURE_LOADED);
            state.respond(CompEditModelConst.TEXT_SAY_LIST_FEATURES_TO_FIND_OUT_WHAT_FEATURES_YOU_HAVE);
            return;
        }
        int current = 0;
        if ((room.getParams() != null) && room.getParams().containsKey(CompRoomBean.MD_ENCOUNTER_CHALLENGE))
            current = JSONUtils.getInt(room.getParams(), CompRoomBean.MD_ENCOUNTER_CHALLENGE);
        current += delta;
        Boolean didIt;
        if (current != 0)
            didIt = FeatureLogic.setRoomMetadata(state, CompRoomBean.MD_ENCOUNTER_CHALLENGE, current);
        else
            didIt = FeatureLogic.setRoomMetadata(state, CompRoomBean.MD_ENCOUNTER_CHALLENGE, null);
        if (didIt != null) 
            if (didIt)
            {
                LookLogic.doLook(state);
            }
            else
                state.respond(CompEditModelConst.TEXT_CANNOT_SET_THE_CHALLENGE_RATING_FOR_THIS_ROOM_TO_XXX, current);
    }
}
