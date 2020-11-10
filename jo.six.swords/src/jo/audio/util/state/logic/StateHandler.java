package jo.audio.util.state.logic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioRequestBean;
import jo.audio.util.model.data.IntentDefBean;
import jo.audio.util.model.data.IntentReqBean;
import jo.audio.util.model.data.UtteranceBean;
import jo.audio.util.model.logic.ModelToExamples;
import jo.util.utils.ArrayUtils;
import jo.util.utils.BeanUtils;
import jo.util.utils.DebugUtils;

public abstract class StateHandler
{
    protected ApplicationHandler mApp;
    protected Map<String, ActionHandler>  mActions = new HashMap<>();
    protected ActionHandler mDefaultAction;
    protected List<String> mHandledActions = new ArrayList<>();
    protected Map<String,List<String>> mExampledActions = new HashMap<>();
    
    public StateHandler()
    {
    }
    
    public void log(String msg)
    {
        mApp.log(msg);
    }
    
    void bootstrap()
    {
        Method defaultIntentHandlerMethod = BeanUtils.getMethod(this.getClass(), 
                "doDefault",
                "handleDefault");
        if (defaultIntentHandlerMethod != null)
            mDefaultAction = new MethodActionHandler(this, defaultIntentHandlerMethod, null);
        for (IntentDefBean intent : mApp.getModel().getIntents().values())
        {
            String intentName = intent.getIntent();
            if (intentName.startsWith("AMAZON."))
                intentName = intentName.substring(7);
            if (intentName.startsWith("AudioPlayer."))
                intentName = intentName.substring(12);
            if (intentName.toLowerCase().endsWith("intent"))
                intentName = intentName.substring(0, intentName.length() - 6);
            Method intentHandlerMethod = BeanUtils.getMethod(this.getClass(), 
                    intentName,
                    "do"+intentName,
                    "handle"+intentName);
            if (intentHandlerMethod == null)
                if (defaultIntentHandlerMethod != null)
                    intentHandlerMethod = defaultIntentHandlerMethod;
                else
                {
                    System.err.println("Can't bootstrap intent handler for "+intentName+" on "+getClass().getName());
                    continue;
                }
            else
            {
                mHandledActions.add(intentName);
                if (intent.getExamples() != null)
                    for (String lang : intent.getExamples().keySet())
                    {
                        List<String> actions = mExampledActions.get(lang);
                        if (actions == null)
                        {
                            actions = new ArrayList<>();
                            mExampledActions.put(lang, actions);
                        }
                        actions.add(intent.getIntent());
                    }
            }
            mActions.put(toIntentKey(intent.getIntent()), new MethodActionHandler(this, intentHandlerMethod, intent));
        }
    }
    
    public void handle(BaseUserState state, AudioRequestBean request)
    {
        IntentReqBean inputSymbol = request.getIntent();
        String isName = inputSymbol.getIntentID();
        if (isName.equalsIgnoreCase("input.unknown"))
        {
            if (mDefaultAction != null)
                mDefaultAction.handle(state, inputSymbol);
            else
            {
                state.respond("NOT_SURE_WHAT_YOU_MEAN_HERE");
            }
            return;
        }
        ActionHandler ah = mActions.get(toIntentKey(isName)); 
        if (ah == null)
        {
            DebugUtils.trace("Input symbol: "+isName+"/"+toIntentKey(isName));
            DebugUtils.trace("Handled symbols:");
            for (String key : mActions.keySet())
                DebugUtils.trace("    "+key);
            throw new IllegalStateException(state.getClass().getName()+", state="+state.getState()+", inputSymbol="+isName+" not handled");
        }
        ah.handle(state, inputSymbol);        
    }
    
    private String toIntentKey(String name)
    {
        name = name.toLowerCase();
        if (name.startsWith("amazon."))
            name = name.substring(7);
        if (name.endsWith("intent"))
            name = name.substring(0, name.length() - 6);
        return name;
    }
    
    protected IntentReqBean selectIntent(AudioRequestBean request)
    {
        return request.getIntent(); // can be overridden to select between multiple intents
    }
    
    public abstract void handleWelcome(BaseUserState state);
    
    public void handleReturn(BaseUserState state)
    {
        throw new IllegalStateException("Return not handled");
    }

    public void handleOption(BaseUserState state)
    {
        throw new IllegalStateException("Option not handled");
    }
    
    // override if there are specifics
    public void addReprompts(BaseUserState state)
    {
        String lang = state.getRequest().getLanguage();
        List<String> actions = mExampledActions.get(lang);
        if ((actions == null) || (actions.size() == 0))
            return;
        String intentID = ArrayUtils.getRandom(actions, BaseUserState.RND);
        IntentDefBean intent  = state.getApplication().getModel().getIntent(intentID);
        List<String> examples = intent.getWorkedExamples().get(lang);
        if (examples == null)
        {
            examples = ModelToExamples.createExamples(getApp().getModel(), intent, lang);
            intent.getWorkedExamples().put(lang, examples);
        }
        if (examples.size() == 0)
            return;
        state.reprompt("TRY_SAYING_XXX", examples.get(BaseUserState.RND.nextInt(examples.size())));
    }

    public void handleRepeat(BaseUserState state)
    {
        if (state.getLastResponse() == null)
            handleWelcome(state);
        else
            state.setResponse(state.getLastResponse());
    }

    public ApplicationHandler getApp()
    {
        return mApp;
    }

    public void setApp(ApplicationHandler app)
    {
        mApp = app;
    }

    public void addExpectedIntents(BaseUserState state)
    {
        //DebugUtils.trace("StateHandler.addExpectedIntents state="+state);
        AudioRequestBean request = state.getRequest();
        if (request == null)
            return; // why is this ever null?
        //DebugUtils.trace("StateHandler.addExpectedIntents request="+request);
        for (String intentID : mHandledActions)
        {
            //DebugUtils.trace("StateHandler.addExpectedIntents intentID="+intentID);
            IntentDefBean intent = state.getApplication().getModel().getIntent(intentID);
            //DebugUtils.trace("StateHandler.addExpectedIntents intent="+intent);
            if (intent != null)
            {
                //DebugUtils.trace("StateHandler.addExpectedIntents originator="+request.getOriginator());
                //DebugUtils.trace("StateHandler.addExpectedIntents language="+request.getLanguage());
                if (intent.isTargettedAt(request.getOriginator()) && intent.isTargettedAtLang(request.getLanguage()))
                {
                    //DebugUtils.trace("StateHandler.addExpectedIntents adding expected intent");
                    state.getResponse().getExpectedContexts().add(intent.getContext());
                }
            }
        }
    }
    
    protected void addSuggestion(BaseUserState state, String intent)
    {
        IntentDefBean i = getApp().getModel().getIntent(intent);
        if (i == null)
            return;
        List<UtteranceBean> u = i.getUtterances(state.getRequest().getLanguage());
        if (u == null)
            return;
        if (u.size() == 0)
            return;
        state.getResponse().getSuggestions().add(u.get(0).getRawUtterance());
    }
}
