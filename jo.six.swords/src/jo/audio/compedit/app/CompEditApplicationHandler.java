package jo.audio.compedit.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jo.audio.compedit.app.logic.OperationLogic;
import jo.audio.compedit.data.CompEditState;
import jo.audio.compedit.data.CompEditUserBean;
import jo.audio.compedit.slu.CompEditModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioRequestBean;
import jo.audio.util.model.data.AudioResponseBean;
import jo.audio.util.state.logic.ApplicationHandler;

public class CompEditApplicationHandler extends ApplicationHandler
{
    private static CompEditApplicationHandler mInstance = null;
    public static Long mRestart = null;
    private static final long ACTIVE_CUTOFF = 3*60*1000L;
    private static Map<String,Long> mActiveUsers = new HashMap<>();
    protected static Map<String,String> mActiveUsersLocation = new HashMap<>();
    public static final String mMoreSound = "<audio src=\"https://s3.amazonaws.com/tsatsatzu-alexa/sound/beeps/DEEK.mp3\"/>";

    public static CompEditApplicationHandler getInstance()
    {
        if (mInstance == null)
            mInstance = new CompEditApplicationHandler();
        return mInstance;
    }
    
    public CompEditApplicationHandler()
    {
        super(CompEditState.class);
        //mEnforceLinking = true;
        if (mRestart == null)
            mRestart = System.currentTimeMillis();
    }
    
    @Override
    public AudioResponseBean interact(AudioRequestBean request)
    {
        AudioResponseBean response = super.interact(request);
        String txt = response.getOutputSpeechText();
        int o = txt.indexOf(mMoreSound);
        if ((o >= 0) && (o < txt.length() - mMoreSound.length()))
        {
            String prefix = txt.substring(0, o);
            String suffix = txt.substring(o + mMoreSound.length());
            txt = prefix + " " + suffix + " " + mMoreSound;
            response.setOutputSpeechText(txt);
        }
        return response;
    }

    @Override
    protected BaseUserState getFromSession(AudioRequestBean session)
    {
        CompEditState state = (CompEditState)super.getFromSession(session);
        OperationLogic.query(state, state.getLinkedName(), state.getLinkedEmail());        
        if (!CompEditModelConst.INTENT_MORE.equalsIgnoreCase(session.getIntent().getIntentID()))
        {
            state.setMoreIntent(null);
            state.setMoreDepth(0);
        }
        return state;
    }
    
    @Override
    protected void setToSession(BaseUserState state, AudioRequestBean session)
    {
        super.setToSession(state, session);
        CompEditState s = (CompEditState)state;
        s.setLastMessages(s.getContext().getMessages());
        mActiveUsers.put(s.getContext().getUser().getURI(), System.currentTimeMillis());
        incrementAccount(s.getContext().getUser(), s.getIntent());
   }
    
    protected void incrementAccount(CompEditUserBean ac, String intent)
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
}
