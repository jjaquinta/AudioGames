package jo.audio.util.model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

public class AudioRequestBean
{
    public static final int TEST = 0;
    public static final int ALEXA = 1;
    public static final int GOOGLE = 2;
    public static final int APIAI = 3;
    public static final int TELNET = 4;
    public static final int APIAI2 = 5;
    
    public static final int SUB_API_AI_ASSISTANT = 1;
    public static final int SUB_API_AI_WEB = 2;
    public static final int SUB_API_AI_SLACK = 3;
    
    private int                 mOriginator;
    private int                 mSubOriginator;
    private String              mEndpoint;
    private JSONObject          mRawRequest;
    private String              mVersion;
    private JSONObject          mTransactionState = new JSONObject();
    private String              mSessionID;
    private String              mApplicationID;
    private String              mUserID;
    private String              mUserName;
    private String              mUserFirstName;
    private String              mUserLastName;
    private String              mUserEmail;
    private Map<String,Object>  mAttributes = new HashMap<>();
    private String              mRawText;
    private String              mLanguage;
    private String              mAccessToken;
    private List<IntentReqBean> mIntents = new ArrayList<>();
    
    // utilities
    public IntentReqBean getIntent()
    {
        if (mIntents.size() > 0)
            return mIntents.get(0);
        else
            return null;
    }
    
    public Object getAttribute(String key)
    {
        if (mAttributes != null)
            return mAttributes.get(key);
        else
            return null;
    }
    
    public void setAttribute(String key, Object value)
    {
        if (mAttributes == null)
            mAttributes = new HashMap<>();
        mAttributes.put(key, value);
    }
    
    // getters and setters
    
    public String getEndpoint()
    {
        return mEndpoint;
    }
    public void setEndpoint(String endpoint)
    {
        mEndpoint = endpoint;
    }
    public String getSessionID()
    {
        return mSessionID;
    }
    public void setSessionID(String sessionID)
    {
        mSessionID = sessionID;
    }
    public String getApplicationID()
    {
        return mApplicationID;
    }
    public void setApplicationID(String applicationID)
    {
        mApplicationID = applicationID;
    }
    public String getUserID()
    {
        return mUserID;
    }
    public void setUserID(String userID)
    {
        mUserID = userID;
    }
    public Map<String, Object> getAttributes()
    {
        return mAttributes;
    }
    public void setAttributes(Map<String, Object> attributes)
    {
        mAttributes = attributes;
    }
    public JSONObject getTransactionState()
    {
        return mTransactionState;
    }
    public void setTransactionState(JSONObject transactionState)
    {
        mTransactionState = transactionState;
    }
    public JSONObject getRawRequest()
    {
        return mRawRequest;
    }
    public void setRawRequest(JSONObject rawRequest)
    {
        mRawRequest = rawRequest;
    }
    public String getUserName()
    {
        return mUserName;
    }
    public void setUserName(String userName)
    {
        mUserName = userName;
    }
    public String getUserFirstName()
    {
        return mUserFirstName;
    }
    public void setUserFirstName(String userFirstName)
    {
        mUserFirstName = userFirstName;
    }
    public String getUserLastName()
    {
        return mUserLastName;
    }
    public void setUserLastName(String userLastName)
    {
        mUserLastName = userLastName;
    }
    public String getUserEmail()
    {
        return mUserEmail;
    }
    public void setUserEmail(String userEmail)
    {
        mUserEmail = userEmail;
    }
    public String getRawText()
    {
        return mRawText;
    }
    public void setRawText(String rawText)
    {
        mRawText = rawText;
    }

    public List<IntentReqBean> getIntents()
    {
        return mIntents;
    }

    public void setIntents(List<IntentReqBean> intents)
    {
        mIntents = intents;
    }

    public String getLanguage()
    {
        return mLanguage;
    }

    public void setLanguage(String language)
    {
        mLanguage = language;
    }

    public int getOriginator()
    {
        return mOriginator;
    }

    public void setOriginator(int originator)
    {
        mOriginator = originator;
    }

    public String getVersion()
    {
        return mVersion;
    }

    public void setVersion(String version)
    {
        mVersion = version;
    }

    public String getAccessToken()
    {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken)
    {
        mAccessToken = accessToken;
    }

    public int getSubOriginator()
    {
        return mSubOriginator;
    }

    public void setSubOriginator(int subOriginator)
    {
        mSubOriginator = subOriginator;
    }

}
