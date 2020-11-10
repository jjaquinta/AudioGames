package jo.audio.companions.web.data;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import jo.audio.companions.data.CompContextBean;

public class WebSession
{
    private HttpServletRequest  mRequest;
    private boolean mLoggedIn;
    private boolean mUserLinked;
    private String mLinkedID;
    private String mLinkedName;
    private String mLinkedEmail;
    private CompContextBean mContext;
    private List<String> mMessages = new ArrayList<>();
    private String  mTopNav;
    private String  mMiddleNav;
    private String  mLowNav;
    
    public HttpServletRequest getRequest()
    {
        return mRequest;
    }
    public void setRequest(HttpServletRequest request)
    {
        mRequest = request;
    }
    public boolean isLoggedIn()
    {
        return mLoggedIn;
    }
    public void setLoggedIn(boolean loggedIn)
    {
        mLoggedIn = loggedIn;
    }
    public boolean isUserLinked()
    {
        return mUserLinked;
    }
    public void setUserLinked(boolean userLinked)
    {
        mUserLinked = userLinked;
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
    public CompContextBean getContext()
    {
        return mContext;
    }
    public void setContext(CompContextBean context)
    {
        mContext = context;
    }
    public List<String> getMessages()
    {
        return mMessages;
    }
    public void setMessages(List<String> messages)
    {
        mMessages = messages;
    }
    public String getTopNav()
    {
        return mTopNav;
    }
    public void setTopNav(String topNav)
    {
        mTopNav = topNav;
    }
    public String getMiddleNav()
    {
        return mMiddleNav;
    }
    public void setMiddleNav(String middleNav)
    {
        mMiddleNav = middleNav;
    }
    public String getLowNav()
    {
        return mLowNav;
    }
    public void setLowNav(String lowNav)
    {
        mLowNav = lowNav;
    }
}
