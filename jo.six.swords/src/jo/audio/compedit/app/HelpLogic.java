package jo.audio.compedit.app;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jo.audio.compedit.data.CompEditState;
import jo.audio.compedit.slu.CompEditModelConst;
import jo.audio.util.model.data.IntentDefBean;
import jo.audio.util.model.data.PhraseSegmentBean;
import jo.audio.util.model.data.TextSegmentBean;
import jo.audio.util.model.data.UtteranceBean;
import jo.ipa.logic.FuzzyMatchLogic;
import jo.ipa.logic.IPAComp;
import jo.ipa.logic.IPADictionary;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class HelpLogic
{
    private static IPADictionary mMiscTopicDictionary = null;
    
    public static void doHelp(CompEditState state, String topic)
    {
        if (StringUtils.isTrivial(topic))
        {
            baseHelp(state);
            return;
        }
        setupDictionaries(state);
        List<IPAComp> matches = FuzzyMatchLogic.findMatches(mMiscTopicDictionary, topic, 5);
        if (matches.size() > 0)
        {
            IPAComp match = matches.get(0);
            DebugUtils.trace("Help for topic '"+topic+"' matches '"+match.toString()+"'");
            for (int i = 1; i < matches.size(); i++)
                DebugUtils.trace("  also "+matches.get(i));
            String id = (String)match.getWord2().getMetadata();
            if (id != null)
            {
                state.respond(id);
                return;
            }
        }
        baseHelp(state);
    }

    private static void setupDictionaries(CompEditState state)
    {
        if (mMiscTopicDictionary != null)
            return;
        mMiscTopicDictionary = new IPADictionary();
        addIntent(state, CompEditModelConst.INTENT_SETFEATURENAME,
                CompEditModelConst.TEXT_HELP_FEATURE);
        addIntent(state, CompEditModelConst.INTENT_SETLOCATION,
                CompEditModelConst.TEXT_HELP_LOCATION);
        addIntent(state, CompEditModelConst.INTENT_SETDIMENSION,
                CompEditModelConst.TEXT_HELP_DIMENSION);
        addIntent(state, CompEditModelConst.INTENT_LOAD,
                CompEditModelConst.TEXT_HELP_LOAD);
        addIntent(state, CompEditModelConst.INTENT_NEW,
                CompEditModelConst.TEXT_HELP_NEW);
        addIntent(state, CompEditModelConst.INTENT_SETNAME,
                CompEditModelConst.TEXT_HELP_NAME);
        addIntent(state, CompEditModelConst.INTENT_SETDESC,
                CompEditModelConst.TEXT_HELP_DESCRIPTION);
        addIntent(state, CompEditModelConst.INTENT_SETTYPE,
                CompEditModelConst.TEXT_HELP_TYPE);
        addIntent(state, CompEditModelConst.INTENT_SETMONSTER,
                CompEditModelConst.TEXT_HELP_MONSTER);
        addIntent(state, CompEditModelConst.INTENT_DIG,
                CompEditModelConst.TEXT_HELP_DIG);
        addIntent(state, CompEditModelConst.INTENT_LINK,
                CompEditModelConst.TEXT_HELP_LINK);
        addIntent(state, CompEditModelConst.INTENT_LIST,
                CompEditModelConst.TEXT_HELP_LIST);
        addIntent(state, CompEditModelConst.INTENT_CHALLENGEUP,
                CompEditModelConst.TEXT_HELP_INCREASE_CHALLENGE);
        addIntent(state, CompEditModelConst.INTENT_CHALLENGEDOWN,
                CompEditModelConst.TEXT_HELP_DECREASE_CHALLENGE);
        addIntent(state, CompEditModelConst.INTENT_NORTH,
                CompEditModelConst.TEXT_HELP_DIR);
        addIntent(state, CompEditModelConst.INTENT_SOUTH,
                CompEditModelConst.TEXT_HELP_DIR);
        addIntent(state, CompEditModelConst.INTENT_EAST,
                CompEditModelConst.TEXT_HELP_DIR);
        addIntent(state, CompEditModelConst.INTENT_WEST,
                CompEditModelConst.TEXT_HELP_DIR);
        addIntent(state, CompEditModelConst.INTENT_LOOK,
                CompEditModelConst.TEXT_HELP_LOOK);
        addIntent(state, CompEditModelConst.INTENT_HELP,
                CompEditModelConst.TEXT_HELP_HELP);
    }
    
    private static void addIntent(CompEditState state, String intentID, String textID)
    {
        IntentDefBean intent = CompEditApplicationHandler.getInstance().getModel().getIntent(intentID);
        Set<String> aliases = new HashSet<>();
        for (UtteranceBean u : intent.getUtterances().get(state.getRequest().getLanguage()))
        {
            PhraseSegmentBean seg = u.getPhrase().get(0);
            if (seg instanceof TextSegmentBean)
            {
                TextSegmentBean text = (TextSegmentBean)seg;
                String txt = text.getText();
                if (!StringUtils.isTrivial(txt))
                    aliases.add(txt);
            }
        }
        FuzzyMatchLogic.addToDictionary(mMiscTopicDictionary, textID, aliases.toArray(new String[0]));
    }
    
    private static final String[] BASE_HELP_IDS = {
            CompEditModelConst.TEXT_HERE_ARE_THE_COMMANDS_YOU_CAN_TRY,
            CompEditModelConst.TEXT_CMD_NEW,
            CompEditModelConst.TEXT_CMD_LOAD,
            CompEditModelConst.TEXT_CMD_LIST,
            CompEditModelConst.TEXT_CMD_FEATURE,
            CompEditModelConst.TEXT_CMD_LOCATION,
            CompEditModelConst.TEXT_CMD_DIMENSION,
            CompEditModelConst.TEXT_CMD_NAME,
            CompEditModelConst.TEXT_CMD_DESCRIPTION,
            CompEditModelConst.TEXT_CMD_INCREASE_CHALLENGE,
            CompEditModelConst.TEXT_CMD_DECREASE_CHALLENGE,
            CompEditModelConst.TEXT_CMD_TYPE,
            CompEditModelConst.TEXT_CMD_MONSTER,
            CompEditModelConst.TEXT_CMD_LOOK,
            CompEditModelConst.TEXT_CMD_DIG,
            CompEditModelConst.TEXT_CMD_DIR,
            CompEditModelConst.TEXT_CMD_LINK,
            CompEditModelConst.TEXT_CMD_HELP,
    };
    
    private static void baseHelp(CompEditState state)
    {
        for (int i = 0; i < BASE_HELP_IDS.length; i++)
        {
            if (i > 0)
                state.respondRaw("\n");
            state.respond(BASE_HELP_IDS[i]);
        }
    }
}
