package jo.audio.util.state.logic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jo.audio.util.BaseUserState;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioRequestBean;
import jo.audio.util.model.data.AudioResponseBean;
import jo.audio.util.model.data.IntentDefBean;
import jo.audio.util.model.data.InteractionModelBean;
import jo.audio.util.model.data.SlotBean;
import jo.audio.util.model.logic.ModelToExamples;
import jo.audio.util.model.logic.ParseModelLogic;
import jo.util.utils.ArrayUtils;
import jo.util.utils.BeanUtils;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.ResourceUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.StringUtils;

public class ApplicationHandler
{
    public static final String PERM_NAME = "NAME";
    public static final String PERM_COARSE_LOCATION = "DEVICE_COARSE_LOCATION";
    public static final String PERM_FINE_LOCATION = "DEVICE_FINE_LOCATION";
    public static final String PERM_EMAIL = "EMAIL";

    protected Map<String, BaseUserState> mCACHE = new HashMap<>();
    protected Map<String, BaseUserState> mSessionCACHE = new HashMap<>();
    protected Class<? extends BaseUserState> mStateClass;
    protected String mBasePackageName;
    protected String mTitle;
    protected Map<Integer, StateHandler>  mStates = new HashMap<>();
    protected InteractionModelBean mModel;
    protected boolean mEnforceLinking = false;
    protected long mQueryISPTimeout = 0; // 0 = never
    protected Random mRND = new Random();
    protected Set<String> mRequiredPermissions = new HashSet<>();
    
    private static final Map<Thread, BaseUserState> mThreadState = new HashMap<>();

    private BaseUserState mLastState = null; // testing purposes only
    
    public ApplicationHandler(Class<? extends BaseUserState> stateClass)
    {
        mStateClass = stateClass;
        bootstrap();
    }

    public AudioResponseBean interact(AudioRequestBean request)
    {
        //DebugUtils.trace("ApplicationHandler.interact 1");
        boolean isTest = BooleanUtils.parseBoolean(request.getTransactionState().get("test"));
        //DebugUtils.trace("ApplicationHandler.interact 2");
        try
        {
            BaseUserState state = getFromSession(request);
            mThreadState.put(Thread.currentThread(), state);
            mLastState = state;
            //DebugUtils.trace("ApplicationHandler.interact 3");
            if (isLinkingNeeded(state))
            {
                return handleLinking();                
            }
            else if (request.getIntent().getIntentID().equalsIgnoreCase("LAUNCHAPP"))
            {
                //DebugUtils.trace("ApplicationHandler.interact 4");
                handleLaunch(state, request);
            }
            else if (request.getIntent().getIntentID().equalsIgnoreCase("RETURNTOAPP"))
            {
                //DebugUtils.trace("ApplicationHandler.interact 4.5");
                handleReturn(state, request);
            }
            else if (request.getIntent().getIntentID().equalsIgnoreCase("OPTION"))
            {
                //DebugUtils.trace("ApplicationHandler.interact 5");
                handleOption(state, request);
            }
            else if (request.getIntent().getIntentID().equalsIgnoreCase("NavigateHomeIntent"))
            {
                state.endSession();
            }
            else if (request.getIntent().getIntentID().equalsIgnoreCase("System.ExceptionEncountered"))
            {
                log("System Exception");
                log(request.getRawRequest().toJSONString());
                state.endSession();
            }
            else
            {
                //DebugUtils.trace("ApplicationHandler.interact 6");
                handleIntent(state, request);
            }
            getStateHandler(state).addExpectedIntents(state);
            if (StringUtils.isTrivial(state.getResponse().getCardTitle()))
                state.getResponse().setCardTitle(mTitle);
            if (StringUtils.isTrivial(state.getResponse().getRepromptText()))
                addReprompts(state);
            setToSession(state, request);
            return state.getResponse();
        }
        catch (Throwable e)
        {
            log(e);
            return ResponseUtils.buildSpeechletResponse(e);
        }
        finally {
            if (!isTest)
                mThreadState.remove(Thread.currentThread());
        }
    }
    
    public static BaseUserState getCurrentState()
    {
        return mThreadState.get(Thread.currentThread());
    }

    public BaseUserState getLastState() // testing purposes only
    {
        return mLastState;
    }

    protected String getModelName()
    {
        String appName = mStateClass.getSimpleName();
        if (appName.endsWith("State"))
            appName = appName.substring(0, appName.length() - 5);
        return makeModelName(appName, null, ".model");
    }
    
    protected AudioResponseBean handleLinking()
    {
        return ResponseUtils.buildLinkRequestSpeechletResponse();
    }

    protected boolean isLinkingNeeded(BaseUserState state)
    {
        return mEnforceLinking && StringUtils.isTrivial(state.getLinkedID());
    }
    
    protected void handleLaunch(BaseUserState state,
            AudioRequestBean request)
    {
        StateHandler stateHandler = getStateHandler(state);
        log("Handling LAUNCHAPP with "+stateHandler.getClass().getSimpleName());
        stateHandler.handleWelcome(state);
    }
    
    protected void handleReturn(BaseUserState state,
            AudioRequestBean request)
    {
        StateHandler stateHandler = getStateHandler(state);
        log("Handling RETURNTOAPP with "+stateHandler.getClass().getSimpleName());
        stateHandler.handleReturn(state);
    }
    
    protected void handleOption(BaseUserState state,
            AudioRequestBean request)
    {
        StateHandler stateHandler = getStateHandler(state);
        log("Handling OPTION with "+stateHandler.getClass().getSimpleName());
        stateHandler.handleOption(state);
    }

    protected void handleIntent(BaseUserState state,
            AudioRequestBean request)
    {
        // alias slot values
        IntentDefBean intentDef = mModel.getIntent(request.getIntent().getIntentID());
        if ((intentDef != null) && (intentDef.getSlots() != null))
            for (SlotBean slot : intentDef.getSlots())
            {
                if (!StringUtils.isTrivial(slot.getDictionary()))
                    continue;
                if (!request.getIntent().getSlots().contains(slot.getName()))
                    continue;
                Map<String,List<String>> dict = mModel.getDictionary(slot.getDictionary(), request.getLanguage());
                String value = (String)request.getIntent().getSlots().get(slot.getName());
                if (dict.containsKey(value))
                    continue;
                // look for alias
                for (String key : dict.keySet())
                    if (dict.get(key).contains(value))
                    {
                        log("Aliasing "+value+" to "+key);
                        request.getIntent().getSlots().put(slot.getName(), key);
                        break;
                    }
            }
        StateHandler stateHandler = getStateHandler(state);
        log("Handling "+request.getIntent().toString()
                +(StringUtils.isTrivial(request.getRawText()) ? "" : "[raw="+request.getRawText()+"]")
                +" with "+stateHandler.getClass().getSimpleName());
        state.setIntent(request.getIntent().getIntentID());
        stateHandler.handle(state, request);
    }

    protected void setToSession(BaseUserState state, AudioRequestBean session)
    {
        session.getTransactionState().put("state", state.getState());
        if (state.getIntent() != null)
            session.getTransactionState().put("lastIntent", state.getIntent());
        if (state.getIntentParam() != null)
            session.getTransactionState().put("lastIntentParam", state.getIntentParam());
        state.setLastResponse(state.getResponse());
        state.setLastResponseTime(System.currentTimeMillis());
    }
    
    protected BaseUserState getFromSession(AudioRequestBean session)
    {
        //DebugUtils.trace("ApplicationHandler.getFromSession 1");
        BaseUserState userState = mCACHE.get(session.getUserID());
        BaseUserState sessionState = mSessionCACHE.get(session.getSessionID());
        BaseUserState state = null;
        if (userState == null) 
            if (sessionState == null)
            {   // user=null, session=null
                //DebugUtils.trace("No cached user or session");
                try
                {
                    state = (BaseUserState)mStateClass.newInstance();
                }
                catch (Exception e)
                {
                    throw new IllegalStateException("Can't instantiate state bean "+mStateClass.getName(), e);
                }
                if (!StringUtils.isTrivial(session.getUserID()))
                {
                    state.setUserID(session.getUserID());
                    mCACHE.put(session.getUserID(), state);
                }
                else
                {
                    state.setUserID(session.getSessionID());
                    mSessionCACHE.put(session.getSessionID(), state);
                }
            }
            else
            {   // user=null, session<>null
                //DebugUtils.trace("No cached user, cached session");
                state = sessionState;
                if (!StringUtils.isTrivial(session.getUserID()))
                {   // convert to user based
                    state.setUserID(session.getUserID());
                    mCACHE.put(session.getUserID(), state);
                    mSessionCACHE.remove(session.getSessionID());
                }
            }
        else
            if (sessionState == null)
            {   // user<>null, session=null
                //DebugUtils.trace("Cached user, no cached session");
                state = userState;
            }
            else
            {   // user<>null, session<>null
                //DebugUtils.trace("Cached user and session");
                userState.setState(sessionState.getState()); // update state
                mSessionCACHE.remove(session.getSessionID()); // remove session based one
            }
        log("User: "+state.getUserID());
        state.setApplication(this);
        //DebugUtils.trace("ApplicationHandler.getFromSession 2");
        if (!StringUtils.isTrivial(session.getAccessToken()))
        {
            log("Access Token: "+session.getAccessToken());
            getProfile(session, state);
        }
        else
        {
            state.setLinkedName(session.getUserName());
            state.setLinkedEmail(session.getUserEmail());
        }
        //DebugUtils.trace("ApplicationHandler.getFromSession 3");
        if (session.getTransactionState().containsKey("state"))
            state.setState(((Number)session.getTransactionState().get("state")).intValue());
        if (session.getTransactionState().containsKey("lastIntent"))
            state.setLastIntent((String)session.getTransactionState().get("lastIntent"));
        if (session.getTransactionState().containsKey("lastIntentParam"))
            state.setLastIntentParam((String)session.getTransactionState().get("lastIntentParam"));
        //DebugUtils.trace("getFromState, transactionState="+session.getTransactionState()+", lastIntent="+state.getLastIntent());
        //DebugUtils.trace("ApplicationHandler.getFromSession 4");
        state.setIntent(null);
        state.setIntentParam(null);
        state.setRequest(session);
        state.setResponse(new AudioResponseBean());
        //DebugUtils.trace("ApplicationHandler.getFromSession 5");
        state.getResponse().setTransactionState(state.getRequest().getTransactionState());
        //DebugUtils.trace("ApplicationHandler.getFromSession 6");
        return state;
    }
    
    private static Map<String, String> mLinkedID = new HashMap<>();
    private static Map<String, String> mLinkedName = new HashMap<>();
    private static Map<String, String> mLinkedEmail = new HashMap<>();
    
    private void getProfile(AudioRequestBean session, BaseUserState state)
    {   // https://developer.amazon.com/public/apis/engage/login-with-amazon/content/java_sample.html
        String access_token = session.getAccessToken();
        if (!mLinkedID.containsKey(access_token))
        {
            if ("test".equals(access_token))
                getProfileTest(access_token);
            else
                getProfileFromWeb(access_token);
        }
        state.setLinkedID(mLinkedID.get(access_token));
        state.setLinkedName(mLinkedName.get(access_token));
        state.setLinkedEmail(mLinkedEmail.get(access_token));
    }
    
    private String getProfileValue(String access_token, Map<String,String> map)
    {
        if (access_token == null)
            return null;
        if (!map.containsKey(access_token))
            getProfileFromWeb(access_token);
        return map.get(access_token);
    }
    
    public String getProfileID(String access_token)
    {
        return getProfileValue(access_token, mLinkedID);
    }
    
    public String getProfileName(String access_token)
    {
        return getProfileValue(access_token, mLinkedName);
    }
    
    public String getProfileEmail(String access_token)
    {
        return getProfileValue(access_token, mLinkedEmail);
    }
    
    private void getProfileTest(String access_token)
    {
        mLinkedID.put(access_token, "TESTING");
        mLinkedName.put(access_token, "Amadan Jaquinta");
        mLinkedEmail.put(access_token, "amadan@111george.com");
    }
    
    private void getProfileFromWeb(String access_token)
    {
        // TODO: get it from... ?
        mLinkedID.put(access_token, "TESTING");
        mLinkedName.put(access_token, "Amadan Jaquinta");
        mLinkedEmail.put(access_token, "amadan@111george.com");
    }

    protected void addReprompts(BaseUserState state)
    {
        StateHandler stateHandler = getStateHandler(state);
        stateHandler.addReprompts(state);
    }

    public StateHandler getStateHandler(BaseUserState state)
    {
        return getStateHandler(state.getState());
    }

    public StateHandler getStateHandler(int state)
    {
        StateHandler stateHandler = mStates.get(state);
        if (stateHandler == null)
        {
            log("States:");
            for (Integer s : mStates.keySet())
                log("State "+s+"="+mStates.get(s).getClass().getName());
            throw new IllegalStateException("No state handler for "+state);
        }
        return stateHandler;
    }

    private void bootstrap()
    {
        mBasePackageName = mStateClass.getPackage().getName();
        if (mBasePackageName.endsWith(".data"))
            mBasePackageName = mBasePackageName.substring(0, mBasePackageName.length() - 5);
        bootstrapIntents();
        bootstrapStates();
    }

    private void bootstrapIntents()
    {
        String modelName = getModelName();
        try
        {
            mModel = ParseModelLogic.parse("resource://"+modelName);
        }
        catch (Exception e1)
        {
            log("Cannot load '"+modelName+"'");
            log(e1);
            mModel = null;
        }
        if (mModel == null)
        {
            log("Cannot bootstrap model");
        }
        else
        {
            System.out.println("Loaded model");
            for (IntentDefBean intent : mModel.getIntents().values())
            {
                if (intent.getExamples().size() > 0)
                    mModel.getIntentsWithExamples().add(intent);
            }
        }
    }
    
    protected String loadModel(String appName, String prefix, String suffix)
    {
        String modelName = makeModelName(appName, prefix,
                suffix);
        try
        {
            return ResourceUtils.loadSystemResourceString(modelName);
        }
        catch (Exception e)
        {
            log(e);
        }
        log("Cannot load '"+modelName+"'.");
        return null;
    }

    protected String makeModelName(String appName,
            String prefix, String suffix)
    {
        String modelName = mBasePackageName.replace('.', '/') + "/slu/";
        if (prefix != null)
            modelName += prefix;
        modelName += appName + suffix;
        return modelName;
    }
    
    @SuppressWarnings("unchecked")
    private void bootstrapStates()
    {
        for (Field f : mStateClass.getFields())
            if (f.getName().startsWith("STATE_"))
            {
                String name = f.getName().substring(6).toLowerCase();
                name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                for (;;)
                {
                    int o = name.indexOf('_');
                    if (o < 0)
                        break;
                    name = name.substring(0,  o) + Character.toUpperCase(name.charAt(o+1)) + name.substring(o + 2);
                }
                List<String> names = new ArrayList<>();
                names.add(mBasePackageName + "." + name);
                names.add(mBasePackageName + ".logic." + name);
                names.add(mBasePackageName + ".app." + name);
                names.add(mBasePackageName + "." + name + "Handler");
                names.add(mBasePackageName + ".logic." + name + "Handler");
                names.add(mBasePackageName + ".app." + name + "Handler");
                names.add(mBasePackageName + "." + name + "StateHandler");
                names.add(mBasePackageName + ".logic." + name + "StateHandler");
                names.add(mBasePackageName + ".app." + name + "StateHandler");
                Class<? extends StateHandler> stateHandlerClass = (Class<? extends StateHandler>)BeanUtils.getClass(names.toArray(new String[0]));
                if (stateHandlerClass == null)
                {
                    log("Cannot bootstrap state handler for "+name+", tried:");
                    for (String n : names)
                        log(n);
                    continue;
                }
                try
                {
                    int stateValue = f.getInt(null);
                    StateHandler stateHandler = (StateHandler)stateHandlerClass.newInstance();
                    stateHandler.setApp(this);
                    stateHandler.bootstrap();
                    mStates.put(stateValue, stateHandler);
                    log("Loaded handler for "+name+"="+stateValue+", "+stateHandler.getClass().getName());
                }
                catch (Exception e)
                {
                    log("Cannot bootstrap handler "+stateHandlerClass.getName()+" for state "+name);
                    log(e);
                    continue;
                }
            }
    }

    public String pickExample(String lang, String... intents)
    {
        IntentDefBean i = null;
        if (intents.length == 0)
            i = ArrayUtils.getRandom(mModel.getIntentsWithExamples(), mRND);
        else
            i = mModel.getIntent(intents[mRND.nextInt(intents.length)]);
        List<String> list = i.getExamples().get(lang);
        if ((list == null) || (list.size() == 0))
        {
            log("Could not find example for "+intents);
            return null;
        }
        String ex = ArrayUtils.getRandom(list, mRND);
        if (ex == null)
            ex = "Try saying \"<>\". ";
        int o = ex.indexOf("<>");
        if (o >= 0)
        {
            String sampleT = makeSample(lang, i);
            ex = ex.substring(0, o) + sampleT + ex.substring(o + 2);
        }
        return ex;
    }
    
    private String makeSample(String lang, IntentDefBean intent)
    {
        List<String> exs = ModelToExamples.createExamples(mModel, intent, lang);
        return ArrayUtils.getRandom(exs, mRND);
    }
    
    public void log(Throwable t)
    {
        Logger.getAnonymousLogger().log(Level.FINE, "", t);
    }

    public void log(String msg)
    {
        DebugUtils.trace(msg);
    }
    
    public InteractionModelBean getModel()
    {
        return mModel;
    }

    public void setModel(InteractionModelBean model)
    {
        mModel = model;
    }
    
    public void testClearFromCache(String userID)
    {
        if (userID == null)
            mCACHE.clear();
        else
            mCACHE.remove(userID);
    }

    public Set<String> getRequiredPermissions()
    {
        return mRequiredPermissions;
    }

    public void setRequiredPermissions(Set<String> requiredPermissions)
    {
        mRequiredPermissions = requiredPermissions;
    }
}
