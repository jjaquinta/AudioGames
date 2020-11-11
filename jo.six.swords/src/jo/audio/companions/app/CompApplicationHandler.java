package jo.audio.companions.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.app.logic.OperationLogic;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.logic.CompOperationLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.data.AudioRequestBean;
import jo.audio.util.model.data.AudioResponseBean;
import jo.audio.util.state.logic.ApplicationHandler;
import jo.util.utils.obj.StringUtils;

public class CompApplicationHandler extends ApplicationHandler
{
    private static CompApplicationHandler mInstance = null;
    public static Long mRestart = null;
    private static final long ACTIVE_CUTOFF = 3*60*1000L;
    private static Map<String,Long> mActiveUsers = new HashMap<>();
    protected static Map<String,String> mActiveUsersLocation = new HashMap<>();
    public static final String mMoreSound = "dot dot dot";
    private static final long TEXT_UPDATE_TIMEOUT = 60*60*1000L;
    private long mLastTextUpdate = 0L;
    
    public static final String MAP_URL = "https://api.111george.com/companions/road/map/v1";
    public static final String MONST_URL = "https://api.111george.com/companions/road/monst/v1";

    public static CompApplicationHandler getInstance()
    {
        if (mInstance == null)
            mInstance = new CompApplicationHandler();
        return mInstance;
    }
    
    public CompApplicationHandler()
    {
        super(CompState.class);
        //mEnforceLinking = true;
        if (mRestart == null)
            mRestart = System.currentTimeMillis();
        getRequiredPermissions().add(PERM_EMAIL);
        getRequiredPermissions().add(PERM_NAME);
        mQueryISPTimeout = 15*60*1000L;
        updateText();
    }
    
    @Override
    public AudioResponseBean interact(AudioRequestBean request)
    {
        AudioResponseBean response = super.interact(request);
        //CompState state = (CompState)getLastState();
        String txt = response.getOutputSpeechText();
        int o = txt.indexOf(mMoreSound);
        if ((o >= 0) && (o < txt.length() - mMoreSound.length()))
        {
            String prefix = txt.substring(0, o);
            String suffix = txt.substring(o + mMoreSound.length());
            txt = prefix + " " + suffix + " " + mMoreSound;
            response.setOutputSpeechText(txt);
        }
        if (System.currentTimeMillis() - mLastTextUpdate > TEXT_UPDATE_TIMEOUT)
        {
            Thread t = new Thread("text update") { public void run() { updateText(); } };
            t.start();
        }
        String rawssml = response.getOutputSpeechText();
        response.getTransactionState().put("rawssml", rawssml);
        response.setOutputSpeechText(stripProps(rawssml));
        response.setCardContent(stripProps(response.getCardContent()));
        return response;
    }
    
    private static String stripProps(String output)
    {
        for (;;)
        {
            int s = output.indexOf("<span");
            if (s < 0)
                break;
            int e = output.indexOf(">", s);
            if (e < 0)
                break;
            output = output.substring(0, s) + output.substring(e + 1);
        }
        for (;;)
        {
            int e = output.indexOf("</span>");
            if (e < 0)
                break;
            output = output.substring(0, e) + output.substring(e + 7);
        }
        return parseProps(output, null);
    }
    
    private static String parseProps(String output, Properties props)
    {
        for (;;)
        {
            int start = output.indexOf("[[");
            if (start < 0)
                break;
            int stop = output.indexOf("]]", start);
            if (stop < 0)
                break;
            String kv = output.substring(start + 2, stop);
            output = output.substring(0, start) + output.substring(stop + 2);
            int o = kv.indexOf('=');
            if ((o >= 0) && (props != null))
                props.put(kv.substring(0, o).trim(), kv.substring(o + 1).trim());
        }
        return output;
    }

    @Override
    protected String getModelName()
    {
        return makeModelName("Companions", null, ".model");
    }

    @Override
    protected AudioResponseBean handleLinking()
    {
        return ResponseUtils.buildLinkRequestSpeechletResponse();
    }

//    @Override
//    protected boolean isLinkingNeeded(BaseUserState baseState)
//    {
//        CompState state = (CompState)baseState;
//        if (state.getUser() != null)
//            return false;
//        return true;
//    }

    @Override
    protected BaseUserState getFromSession(AudioRequestBean session)
    {
        CompState state = (CompState)super.getFromSession(session);
        OperationLogic.query(state, state.getLinkedName(), state.getLinkedEmail());        
        if (!StringUtils.isTrivial(state.getContext().getUser().getSupportIdent()))
            log("SUPPORT IDENT - "+state.getContext().getUser().getSupportIdent());
        log("canPremium="+state.getContext().isCanPremium()+", isPremium="+state.getContext().isPremium());
        if (!CompanionsModelConst.INTENT_MORE.equalsIgnoreCase(session.getIntent().getIntentID()))
        {
            state.setMore((List<List<AudioMessageBean>>)null);
            state.setMoreIntent(null);
            state.setMoreDepth(0);
        }
        return state;
    }
    
    @Override
    protected void setToSession(BaseUserState state, AudioRequestBean session)
    {
        super.setToSession(state, session);
        CompState s = (CompState)state;
        s.setLastMessages(s.getContext().getMessages());
        mActiveUsers.put(s.getContext().getUser().getURI(), System.currentTimeMillis());
        incrementAccount(s.getContext().getUser(), s.getIntent());
   }
    
    protected void incrementAccount(CompUserBean ac, String intent)
    {
        ac.setInteractions(ac.getInteractions() + 1);
        ac.setLastInteraction(System.currentTimeMillis());
    }

    public String getAppID()
    {
        String id = mStateClass.getSimpleName();
        id = id.toLowerCase();
        if (id.endsWith("state"))
            id = id.substring(0, id.length() - 5);
        return id;
    }
    public long getUptime()
    {
        return System.currentTimeMillis() - mRestart;
    }

    public static List<String> getActiveUsers()
    {
        long cutoff = System.currentTimeMillis() - ACTIVE_CUTOFF;
        List<String> activeUsers = new ArrayList<>();
        for (String user : mActiveUsers.keySet().toArray(new String[0]))
        {
            Long tick = mActiveUsers.get(user);
            if (tick < cutoff)
            {
                mActiveUsers.remove(user);
                mActiveUsersLocation.remove(user);
            }
            else
                activeUsers.add(user);
        }
        return activeUsers;
    }
    
    public static List<String> getActiveUsersAt(String location)
    {
        List<String> users = getActiveUsers();
        for (Iterator<String> i = users.iterator(); i.hasNext(); )
            if (!location.equals(mActiveUsersLocation.get(i.next())))
                i.remove();
        return users;
    }

    public void updateText()
    {
        //log("Updating text");
        CompOperationBean op = new CompOperationBean();
        op.setOperation(CompOperationBean.TEXT);
        CompContextBean context = CompOperationLogic.operate(op);
        for (String lang : context.getTextModel().keySet())
        {
            //log("Updating text lang="+lang);
            JSONObject dict = JSONUtils.getObject(context.getTextModel(), lang);
            for (String key : dict.keySet())
            {
                List<String> vals = new ArrayList<>();
                for (String v : JSONUtils.toStringArray(JSONUtils.getArray(dict, key)))
                    vals.add(v);
                mModel.replaceText(lang, key, vals);
                //log("Updating text replace "+key+" with "+vals);
            }
        }
        mLastTextUpdate = System.currentTimeMillis();
    }
}
