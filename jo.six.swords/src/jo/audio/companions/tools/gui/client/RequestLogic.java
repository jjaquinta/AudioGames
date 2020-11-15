package jo.audio.companions.tools.gui.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import jo.audio.companions.app.CompApplicationHandler;
import jo.audio.companions.logic.CompOperationLogic;
import jo.audio.util.model.data.AudioRequestBean;
import jo.audio.util.model.data.AudioResponseBean;
import jo.audio.util.model.data.IntentDefBean;
import jo.audio.util.model.data.IntentReqBean;
import jo.audio.util.model.data.InteractionModelBean;
import jo.audio.util.model.data.PhraseSegmentBean;
import jo.audio.util.model.data.SlotSegmentBean;
import jo.audio.util.model.data.UtteranceBean;
import jo.audio.util.model.logic.ModelToRegex;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class RequestLogic
{
    private static InteractionModelBean                mModel;
    private static CompApplicationHandler              mHandler;
    private static String                              mLanguage = "en_US";
    private static Map<String, List<UtteranceMatcher>> mParsers;
    private static Map<String, JSONObject>             mTransactionStates = new HashMap<>();

    public static void setModel(InteractionModelBean model)
    {
        if (mModel == model)
            return;
        mModel = model;
        updateParsers();
    }

    public static InteractionModelBean getModel()
    {
        return mModel;
    }
    
    public static void setLanguage(String language)
    {
        if (language.equals(mLanguage))
            return;
        mLanguage = language;
        updateParsers();
    }
    
    public static String getLanguage()
    {
        return mLanguage;
    }

    public static void setHandler(CompApplicationHandler handler)
    {
        if (mHandler == handler)
            return;
        mHandler = handler;
        setModel(mHandler.getModel());
    }

    public static AudioResponseBean performIntentRequest(String txt,
            String username, String password, String language) throws IOException
    {
        setHandler(CompApplicationHandler.getInstance());
        setLanguage(language);
        CompOperationLogic.startBackgroundDaemons();
        AudioRequestBean request = makeRequest(username, password);
        request.setRawText(txt);
        IntentReqBean intent = match(txt);
        request.getIntents().add(intent);
        AudioResponseBean response = mHandler.interact(request);
        JSONObject transactionState = response.getTransactionState();
        mTransactionStates.put(username, transactionState);
        return response;
    }

    public static void performSessionEndedRequest() throws IOException
    {
        // NOP
    }

    public static AudioResponseBean performLaunchRequest(String username, String password, String language) throws IOException
    {
        setHandler(CompApplicationHandler.getInstance());
        setLanguage(language);
        CompOperationLogic.startBackgroundDaemons();
        AudioRequestBean request = makeRequest(username, password);
        IntentReqBean intent = new IntentReqBean();
        intent.setIntentID("LAUNCHAPP");
        request.getIntents().add(intent);
        AudioResponseBean response = mHandler.interact(request);
        JSONObject transactionState = response.getTransactionState();
        mTransactionStates.put(username, transactionState);
        return response;
    }

    private static AudioRequestBean makeRequest(String username, String password)
    {
        JSONObject transactionState = mTransactionStates.get(username);
        if (transactionState == null)
        {
            transactionState = new JSONObject();
            mTransactionStates.put(username, transactionState);
        }
        AudioRequestBean request = new AudioRequestBean();
        request.setSessionID(username);
        request.setUserID(username);
        request.setOriginator(AudioRequestBean.TELNET);
        request.setLanguage(mLanguage);
        request.setTransactionState(transactionState);
        return request;
    }

    private static void updateParsers()
    {
        Map<String, List<UtteranceMatcher>> parsers = new HashMap<>();
        for (String intentID : mModel.getIntents().keySet())
        {
            IntentDefBean intent = mModel.getIntent(intentID);
            if (!StringUtils.isTrivial(intent.getTargetLang())
                    && !mLanguage.equals(intent.getTargetLang()))
            {
                // mConnection.debug("skipping intent id="+intentID+",
                // locale="+intent.getTargetLang());
                continue;
            }
            if (!StringUtils.isTrivial(intent.getTarget())
                    && !"telnet".equalsIgnoreCase(intent.getTarget()))
            {
                // mConnection.debug("skipping intent id="+intentID+",
                // target="+intent.getTarget());
                continue;
            }
            // mConnection.debug("intent id="+intentID);
            List<UtteranceMatcher> patterns = new ArrayList<>();
            parsers.put(intentID, patterns);
            for (UtteranceBean utterance : intent.getUtterances()
                    .get(mLanguage))
            {
                if (utterance.getTags().containsKey("only")
                        && !"telnet".equalsIgnoreCase(
                                utterance.getTags().getProperty("only")))
                    continue;
                UtteranceMatcher um = new UtteranceMatcher(utterance);
                patterns.add(um);
            }
        }
        // mConnection.debug("Done updating parsers");
        mParsers = parsers;
    }

    private static IntentReqBean match(String inbuf)
    {
        IntentReqBean intent = null;
        inbuf = inbuf.trim();
        for (String intentID : mParsers.keySet())
        {
            for (UtteranceMatcher um : mParsers.get(intentID))
            {
                intent = um.match(inbuf);
                if (intent != null)
                    break;
            }
            if (intent != null)
                break;
        }
        if (intent == null)
        {
            intent = new IntentReqBean();
            intent.setIntentID("input.unknown");
        }
        DebugUtils.debug("Matched intent=" + intent.getIntentID());
        for (Object key : intent.getSlots().keySet())
            DebugUtils.debug("  slot " + key + "=" + intent.getSlots().get(key));
        return intent;
    }

}

class UtteranceMatcher
{
    private String        mRegex;
    private Pattern       mPattern;
    private UtteranceBean mUtterance;
    private List<String>  mSlots;

    public UtteranceMatcher(UtteranceBean utterance)
    {
        mUtterance = utterance;
        mRegex = ModelToRegex.createRegex(RequestLogic.getModel(), RequestLogic.getLanguage(),
                mUtterance);
        System.out.println("Utterance: "+utterance.getRawUtterance()+" -> "+mRegex);
        mPattern = Pattern.compile(mRegex, Pattern.CASE_INSENSITIVE);
        mSlots = new ArrayList<>();
        for (PhraseSegmentBean p : mUtterance.getPhrase())
            if (p instanceof SlotSegmentBean)
                mSlots.add(((SlotSegmentBean)p).getSlot().getName());
    }

    public IntentReqBean match(String inbuf)
    {
        Matcher m = mPattern.matcher(inbuf);
        if (!m.matches())
            return null;
        // mConnection.debug("Matched with pattern="+mPattern.pattern()+",
        // groups="+m.groupCount());
        IntentReqBean intent = new IntentReqBean();
        intent.setIntentID(mUtterance.getIntent().getIntent());
        for (int i = 0; i < mSlots.size(); i++)
            intent.getSlots().put(mSlots.get(i), m.group(i + 1));
        return intent;
    }
}
