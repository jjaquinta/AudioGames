package jo.audio.util;

import java.util.List;
import java.util.Random;

import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.data.AudioRequestBean;
import jo.audio.util.model.data.AudioResponseBean;
import jo.audio.util.model.logic.ModelResolveLogic;
import jo.audio.util.state.logic.ApplicationHandler;

public class BaseUserState
{
    private static final String SHORT_PAUSE = "<break time=\".5s\"/>";

    private static final String MEDIUM_PAUSE = "<break time=\"1s\"/>";

    private static final String LONG_PAUSE = "<break time=\"2s\"/>";

    private static final String PAUSE = "<break time=\"1s\"/>";

    public static final Random RND = new Random();
    
    private boolean mDebug = false;
    
    private ApplicationHandler  mApplication;
    private int             mState;
    private String          mLastIntent;
    private String          mLastIntentParam;
    private long            mLastResponseTime;
    private String          mIntent;
    private String          mIntentParam;

    private AudioRequestBean    mRequest;
    private AudioResponseBean   mResponse;
    private AudioResponseBean   mLastResponse;
    private String          mUserID;
    private String          mLinkedID;
    private String          mLinkedName;
    private String          mLinkedEmail;
    
    public BaseUserState()
    {
        mRequest = new AudioRequestBean();
        mResponse = new AudioResponseBean();
    }
    
    // utility functions
    public String resolve(String key, Object... args)
    {
        if (mDebug)
        {
            getApplication().log("BaseUserState.resolve: Resolving '"+key+"'");
            for (int i = 0; i < args.length; i++)
                getApplication().log(" arg"+(i+1)+"='"+args[i]+"', class="+args[i].getClass().getSimpleName());
        }
        for (int i = 0; i < args.length; i++)
            if (args[i] instanceof AudioMessageBean)
            {
                if (mDebug) getApplication().log("BaseUserState.resolve: Expanding arg#"+i+"...");
                args[i] = resolve((AudioMessageBean)args[i]);
                if (mDebug) getApplication().log("BaseUserState.resolve: Expanded arg#"+i+" to '"+args[i]+"'");
            }
        if (AudioMessageBean.AND.equals(key))
            return ResponseUtils.wordList(args, -1);
        else if (AudioMessageBean.OR.equals(key))
            return ResponseUtils.wordListOR(args);
        else if (AudioMessageBean.RAW.equals(key))
        {
            StringBuffer ret = new StringBuffer();
            for (int i = 0; i < args.length; i++)
                ret.append(args[i]);
            return ret.toString();
        }
        else if (AudioMessageBean.GROUP.equals(key))
        {
            StringBuffer ret = new StringBuffer();
            for (int i = 0; i < args.length; i++)
            {
                if (i > 0)
                    ret.append(" ");
                if (args[i] == null)
                    throw new IllegalArgumentException("Argument #"+i+" for '"+key+"' unexpectedly null ("+args+")");
                ret.append(expandInserts(args[i].toString()));
            }
            return ret.toString();
        }
        if (key.indexOf("{{") >= 0)
        {
            if (mDebug) getApplication().log("BaseUserState.resolve: Expanding...");
            String expansion = expandInserts(key); // expand, not resolve
            if (mDebug) getApplication().log("BaseUserState.resolve: Expanded to '"+expansion+"'");
            return expansion; // expand, not resolve
        }
        if (mDebug) getApplication().log("BaseUserState.resolve: Resolving...");
        String resolved = ModelResolveLogic.resolve(mApplication.getModel(), mRequest.getLanguage(), RND, this, key, args);
        if (mDebug) getApplication().log("BaseUserState.resolve: Resolved '"+resolved+"'");
        return resolved;
    }
    
    public String expandInserts(String txt)
    {
        return ModelResolveLogic.expandInserts(mApplication.getModel(), mRequest.getLanguage(), RND, this, txt);
    }
    
    public String resolve(AudioMessageBean msg)
    {
        if (msg.getArgs() != null)
            return resolve(msg.getIdent(), msg.getArgs());
        else
            return resolve(msg.getIdent());
    }
    
    public void respond(List<AudioMessageBean> msgs)
    {
        if (mDebug) getApplication().log("BaseUserState.respond(msg list): Responding with "+msgs.size()+" messages");
        for (AudioMessageBean msg : msgs)
            respond(msg);
        if (mDebug) getApplication().log("BaseUserState.respond(msg list): Done responding with "+msgs.size()+" messages");
    }
    
    public void respondRaw(String txt)
    {
        respondRaw(0, txt);
    }
    
    public void respondRaw(int priority, String txt)
    {
        String spoken = parseInserts(txt, false);
        mResponse.getOutputSpeech().append(priority, spoken);
        String written = parseInserts(txt, true);
        mResponse.getCardSpeech().append(priority, written);
    }
    
    public void respond(String format, Object... args)
    {
        respond(0, format, args);
    }
    
    public void respond(int priority, String format, Object... args)
    {
        if (mDebug) getApplication().log("BaseUserState.respond(fmt, args): Responding with '"+format+"'");
        String txt = resolve(format, args);
        if (mDebug) getApplication().log("BaseUserState.respond(fmt, args): -> '"+txt+"'");
        respondRaw(priority, txt);
        if (mDebug) getApplication().log("BaseUserState.respond(fmt, args): Done");
    }
    
    public void respond(AudioMessageBean msg)
    {
        if (mDebug) getApplication().log("BaseUserState.respond(msg): Responding with '"+msg+"'");
        if (msg.getArgs() != null)
            respond(msg.getPriority(), msg.getIdent(), msg.getArgs());
        else
            respond(msg.getPriority(), msg.getIdent());
        if (mDebug) getApplication().log("BaseUserState.respond(msg): Done responding with '"+msg+"'");
    }
    
    public void reprompt(String format, Object... args)
    {
        reprompt(0, format, args);
    }
    
    public void reprompt(int priority, String format, Object... args)
    {
        String text = resolve(format, args);
        text = parseInserts(text, false);
        mResponse.getRepromptSpeech().append(priority, text);
    }
    
    public void reprompt(AudioMessageBean msg)
    {
        if (msg.getArgs() != null)
            reprompt(msg.getPriority(), msg.getIdent(), msg.getArgs());
        else
            reprompt(msg.getPriority(), msg.getIdent());
    }
    
    public void repromptRaw(String text)
    {
        repromptRaw(0, text);
    }
    
    public void repromptRaw(int priority, String text)
    {
        text = parseInserts(text, false);
        mResponse.getRepromptSpeech().append(priority, text);
    }
    
    public void addLargeImage(String image)
    {
        mResponse.setCardImageLarge(image);        
    }
    
    public void addSmallImage(String image)
    {
        mResponse.setCardImageSmall(image);        
    }
    
    public void addImage(String image)
    {
        int o = image.lastIndexOf('.');
        if (o < 0)
        {
            addLargeImage(image);
            return;
        }
        String suffix = image.substring(o);
        String prefix = image.substring(0, o);
        if (prefix.endsWith("_lg") || prefix.endsWith("_sm"))
            prefix = prefix.substring(0, prefix.length() - 3);
        addLargeImage(prefix+"_lg"+suffix);
        addSmallImage(prefix+"_sm"+suffix);
    }
    
    public void card(String format, Object... args)
    {
        card(0, format, args);
    }
    
    public void card(int priority, String format, Object... args)
    {
        String text = resolve(format, args);
        text = parseInserts(text, true);
        mResponse.getCardSpeech().append(priority, text);
    }
    
    public void card(AudioMessageBean msg)
    {
        if (msg.getArgs() != null)
            card(msg.getPriority(), msg.getIdent(), msg.getArgs());
        else
            card(msg.getPriority(), msg.getIdent());
    }
    
    public void speak(String format, Object... args)
    {
        speak(0, format, args);
    }
    
    public void speak(int priority, String format, Object... args)
    {
        String text = resolve(format, args);
        text = parseInserts(text, false);
        mResponse.getOutputSpeech().append(priority, text);
    }
    
    public void speak(AudioMessageBean msg)
    {
        if (msg.getArgs() != null)
            speak(msg.getPriority(), msg.getIdent(), msg.getArgs());
        else
            speak(msg.getPriority(), msg.getIdent());
    }

    public void endSession()
    {
        mResponse.setShouldEndSession(true);
    }
    
    private String parseInsert(String cmd, String arg, boolean forCard)
    {
        switch (cmd)
        {
            case "forCard":
            case "card":
                if (forCard)
                    return arg;
                else
                    return "";
            case "speech":
            case "sp":
            case "forSpeech":
                if (!forCard)
                    return arg;
                else
                    return "";
            case "pause":
                if (forCard)
                    return "\n";
                else if ("long".equals(arg))
                    return LONG_PAUSE;
                else if ("medium".equals(arg))
                    return MEDIUM_PAUSE;
                else if ("short".equals(arg))
                    return SHORT_PAUSE;
                else
                    return PAUSE;
            case "ph":
            case "phoneme":
            {
                String[] args = arg.split(":");
                if (args.length != 2)
                    throw new IllegalArgumentException("Expected two arguments for '"+cmd+"', not '"+args+"'");
                if (forCard)
                    return args[0];
                else
                    return "<phoneme alphabet=\"ipa\" ph=\""+args[1]+"\">"+args[0]+"</phoneme>";
            }
            case "ref":
            {
                String[] args = arg.split(":");
                String txt;
                if (args.length == 1)
                    txt = resolve(args[0]);
                else
                {
                    Object[] a = new String[args.length - 1];
                    System.arraycopy(args, 1, a, 0, a.length);
                    txt = resolve(args[0], a);
                }
                return parseInserts(txt, forCard);
            }
            default:
                throw new IllegalArgumentException("Unknown parse command '"+cmd+"("+arg+")");
        }
    }
    
    private String parseInserts(String Inbuf, boolean forCard)
    {
        StringBuffer outbuf = new StringBuffer(Inbuf);
        for (;;)
        {
            int o = outbuf.lastIndexOf("<<");
            if (o < 0)
                break;
            int c = outbuf.indexOf(":", o);
            if (c < 0)
                throw new IllegalStateException("Expected ':', got '"+outbuf.substring(o)+"'");
            String cmd = outbuf.substring(o+2, c);
            int e = outbuf.indexOf(">>", c);
            if (e < 0)
                throw new IllegalStateException("Expected '>>', got '"+outbuf.substring(o)+"'");
            String arg = outbuf.substring(c+1, e);
            String val = parseInsert(cmd, arg, forCard);
            outbuf.replace(o, e + 2, val);
        }
        return outbuf.toString();
    }

    /**
     * Adds the sound.
     * Wraps a URL to a MP3 in SSML.
     *
     * @param mp3 the mp3 url
     */
    public void addSound(String mp3)
    {
        mResponse.getOutputSpeech().append("<audio src=\""+mp3+"\"/>");
    }
    
    /**
     * Adds the pause.
     * Adds a SSML pause directive to the output.
     */
    public void addPause()
    {
        mResponse.getOutputSpeech().append(PAUSE);
        card("");
    }
    
    /**
     * Adds the pause.
     * Adds a SSML pause directive to the output.
     */
    public void addLongPause()
    {
        speak(LONG_PAUSE);
        card("");
    }
    
    /**
     * Adds the pause.
     * Adds a SSML pause directive to the output.
     */
    public void addMediumPause()
    {
        speak(MEDIUM_PAUSE);
        card("");
    }
    
    /**
     * Adds the pause.
     * Adds a SSML pause directive to the output.
     */
    public void addShortPause()
    {
        speak(SHORT_PAUSE);
        card("");
    }
    
    // getters and setters

    public String getUserID()
    {
        return mUserID;
    }

    public void setUserID(String userID)
    {
        mUserID = userID;
    }
    public int getState()
    {
        return mState;
    }

    public void setState(int state)
    {
        mState = state;
    }

    public String getLastIntent()
    {
        return mLastIntent;
    }

    public void setLastIntent(String lastIntent)
    {
        mLastIntent = lastIntent;
    }

    public String getLastIntentParam()
    {
        return mLastIntentParam;
    }

    public void setLastIntentParam(String lastIntentParam)
    {
        mLastIntentParam = lastIntentParam;
    }

    public String getIntentParam()
    {
        return mIntentParam;
    }

    public void setIntentParam(String intentParam)
    {
        mIntentParam = intentParam;
    }

    public String getIntent()
    {
        return mIntent;
    }

    public void setIntent(String intent)
    {
        mIntent = intent;
    }

    public void setLastResponseTime(long lastResponseTime)
    {
        mLastResponseTime = lastResponseTime;
    }

    public String getLinkedID()
    {
        return mLinkedID;
    }

    public void setLinkedID(String linkedID)
    {
        mLinkedID = linkedID;
    }

    public String getLinkedName()
    {
        return mLinkedName;
    }

    public void setLinkedName(String linkedName)
    {
        mLinkedName = linkedName;
    }

    public String getLinkedEmail()
    {
        return mLinkedEmail;
    }

    public void setLinkedEmail(String linkedEmail)
    {
        mLinkedEmail = linkedEmail;
    }

    public ApplicationHandler getApplication()
    {
        return mApplication;
    }

    public void setApplication(ApplicationHandler application)
    {
        mApplication = application;
    }

    public AudioRequestBean getRequest()
    {
        return mRequest;
    }

    public void setRequest(AudioRequestBean request)
    {
        mRequest = request;
    }

    public AudioResponseBean getResponse()
    {
        return mResponse;
    }

    public void setResponse(AudioResponseBean response)
    {
        mResponse = response;
    }

    public void setLastResponse(AudioResponseBean lastResponse)
    {
        mLastResponse = lastResponse;
    }

    public long getLastResponseTime()
    {
        return mLastResponseTime;
    }

    public AudioResponseBean getLastResponse()
    {
        return mLastResponse;
    }

    public boolean isDebug()
    {
        return mDebug;
    }

    public void setDebug(boolean debug)
    {
        mDebug = debug;
    }
}
