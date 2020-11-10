package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.IntentDefBean;
import jo.audio.util.model.data.UtteranceBean;
import jo.util.utils.obj.StringUtils;

public class IntentLogic
{
    private static final String[] COMMON_INTENTS = {
            CompanionsModelConst.INTENT_ENTER,
            CompanionsModelConst.INTENT_NORTH,
            CompanionsModelConst.INTENT_SOUTH,
            CompanionsModelConst.INTENT_EAST,
            CompanionsModelConst.INTENT_WEST,
            CompanionsModelConst.INTENT_FIGHT,
            CompanionsModelConst.INTENT_MOVE,
            CompanionsModelConst.INTENT_INVENTORY,
            CompanionsModelConst.INTENT_WHO,            
            CompanionsModelConst.INTENT_LOOK,            
    };
    
    public static List<String> getSuggestions(CompState state)
    {
        List<String> intents = new ArrayList<>();
        for (String intent : COMMON_INTENTS)
        {
            //state.getApplication().log("Is "+intent+" a good suggestion.");
            IntentDefBean i = state.getApplication().getModel().getIntent(intent);
            if (!i.isTargettedAt(state.getRequest().getOriginator()))
            {
                //state.getApplication().log("  Not a target.");
                continue;
            }
            if (!isValidIntent(state, intent))
            {
                //state.getApplication().log("  Not valid.");
                continue;
            }
            List<UtteranceBean> utts = i.getUtterances(state.getRequest().getLanguage());
            if ((utts == null) || (utts.size() == 0))
            {
                //state.getApplication().log("  No utterances.");
                continue;
            }
            String suggestion = null;
            for (UtteranceBean u : utts)
            {
                String s = u.getRawUtterance();
                if (s.indexOf('{') >= 0)
                    continue;
                //state.getApplication().log("  Considering "+s+".");
                if ((suggestion == null) || (suggestion.length() > s.length()))
                    suggestion = s;
            }
            //state.getApplication().log("  Adding "+suggestion+".");
            if (suggestion != null)
                intents.add(suggestion);
        }
        //state.getApplication().log("Calculated "+intents.size()+" suggestions.");
        return intents;
    }
    
    public static Set<String> getExampledIntents(CompState state)
    {
        Set<String> intents = new HashSet<>();
        for (IntentDefBean intent : state.getApplication().getModel().getIntents().values())
        {
            if (!intent.isTargettedAt(state.getRequest().getOriginator()))
                continue;
            List<String> ex = intent.getExamples(state.getRequest().getLanguage());
            if ((ex == null) || (ex.size() == 0))
                continue;
            if (isValidIntent(state, intent.getIntent()))
                intents.add(intent.getIntent());
        }
        return intents;
    }
    
    public static Set<String> getValidIntents(CompState state)
    {
        Set<String> intents = new HashSet<>();
        for (IntentDefBean intent : state.getApplication().getModel().getIntents().values())
            if (intent.isTargettedAt(state.getRequest().getOriginator()))
                if (isValidIntent(state, intent.getIntent()))
                    intents.add(intent.getIntent());
        return intents;
    }
    
    public static boolean isValidIntent(CompState state, String intent)
    {
        switch (intent.toLowerCase())
        {
            case CompanionsModelConst.INTENT_ABOUT:
                return true;
            case CompanionsModelConst.INTENT_ACTIVATE:
                return isActivateValid(state);
            case CompanionsModelConst.INTENT_BUY:
                return isBuyValid(state);
            case CompanionsModelConst.INTENT_CANCEL:
                return true;
            case CompanionsModelConst.INTENT_EAST:
                return isEastValid(state);
            case CompanionsModelConst.INTENT_ENTER:
                return isEnterValid(state);
            case CompanionsModelConst.INTENT_EQUIP:
                return isEquipValid(state);
            case CompanionsModelConst.INTENT_FIGHT:
                return isFightValid(state);
            case CompanionsModelConst.INTENT_FIRE:
                return isFireValid(state);
            case CompanionsModelConst.INTENT_HELP:
                return true;
            case CompanionsModelConst.INTENT_HIRE:
                return isHireValid(state);
            case CompanionsModelConst.INTENT_INVENTORY:
                return true;
            case CompanionsModelConst.INTENT_LOOK:
                return true;
            case CompanionsModelConst.INTENT_MORE:
                return true;
            case CompanionsModelConst.INTENT_MOVE:
                return isMoveValid(state);
            case CompanionsModelConst.INTENT_NO:
                return isNoValid(state);
            case CompanionsModelConst.INTENT_NORTH:
                return isNorthValid(state);
            case CompanionsModelConst.INTENT_REPEAT:
                return true;
            case CompanionsModelConst.INTENT_SELL:
                return isSellValid(state);
            case CompanionsModelConst.INTENT_SOUTH:
                return isSouthValid(state);
            case CompanionsModelConst.INTENT_STOP:
                return true;
            case CompanionsModelConst.INTENT_UNEQUIP:
                return isUnequipValid(state);
            case CompanionsModelConst.INTENT_WEST:
                return isWestValid(state);
            case CompanionsModelConst.INTENT_WHO:
                return true;
            case CompanionsModelConst.INTENT_YES:
                return isYesValid(state);
        }
        return true;
    }

    private static boolean isActivateValid(CompState state)
    {
        return state.getUser().getCompanions().size() > 0;
    }

    private static boolean isBuyValid(CompState state)
    {
        if (state.getState() == CompState.STATE_COMBAT)
            return false;
        if (state.getContext().getRoom() == null)
            return false;
        return state.getContext().getRoom().getType().equals(CompRoomBean.TYPE_ITEM_SHOP);
    }

    private static boolean isSellValid(CompState state)
    {
        if (state.getState() == CompState.STATE_COMBAT)
            return false;
        if (state.getContext().getRoom() == null)
            return false;
        return state.getContext().getRoom().getType().equals(CompRoomBean.TYPE_ITEM_SHOP);
    }

    private static boolean isEastValid(CompState state)
    {
        if (state.getState() == CompState.STATE_COMBAT)
            return false;
        if (state.getContext().getRoom() == null)
            return true;
        return !StringUtils.isTrivial(state.getContext().getRoom().getEast());
    }

    private static boolean isEnterValid(CompState state)
    {
        if (state.getState() == CompState.STATE_COMBAT)
            return false;
        if (state.getContext().getRoom() != null)
            return false;
        return state.getContext().getSquare().getFeature() != CompConstLogic.FEATURE_NONE;
    }

    private static boolean isEquipValid(CompState state)
    {
        return state.getUser().getCompanions().size() > 0;
    }

    private static boolean isFightValid(CompState state)
    {
        if (state.getState() != CompState.STATE_COMBAT)
            return false;
        return true;
    }

    private static boolean isFireValid(CompState state)
    {
        if (state.getState() == CompState.STATE_COMBAT)
            return false;
        return state.getUser().getCompanions().size() > 0;
    }

    private static boolean isHireValid(CompState state)
    {
        if (state.getState() == CompState.STATE_COMBAT)
            return false;
        if (state.getContext().getRoom() == null)
            return false;
        return state.getContext().getRoom().getType().equals(CompRoomBean.TYPE_FIGHTERS_GUILD);
    }

    private static boolean isMoveValid(CompState state)
    {   // actually run away
        if (state.getState() != CompState.STATE_COMBAT)
            return false;
        return true;
    }

    private static boolean isNoValid(CompState state)
    {
        return false;
    }

    private static boolean isNorthValid(CompState state)
    {
        if (state.getState() == CompState.STATE_COMBAT)
            return false;
        if (state.getContext().getRoom() == null)
            return true;
        return !StringUtils.isTrivial(state.getContext().getRoom().getNorth());
    }

    private static boolean isSouthValid(CompState state)
    {
        if (state.getState() == CompState.STATE_COMBAT)
            return false;
        if (state.getContext().getRoom() == null)
            return true;
        return !StringUtils.isTrivial(state.getContext().getRoom().getSouth());
    }

    private static boolean isUnequipValid(CompState state)
    {
        return state.getUser().getCompanions().size() > 0;
    }

    private static boolean isWestValid(CompState state)
    {
        if (state.getState() == CompState.STATE_COMBAT)
            return false;
        if (state.getContext().getRoom() == null)
            return true;
        return !StringUtils.isTrivial(state.getContext().getRoom().getWest());
    }

    private static boolean isYesValid(CompState state)
    {
        return false;
    }
}
