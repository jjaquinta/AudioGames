package jo.audio.companions.web.logic;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.AbstractDocument.Content;

import org.omg.CORBA.Request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.logic.CompOperationLogic;
import jo.audio.companions.web.data.WebSession;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.obj.StringUtils;

public class WebSessionLogic
{
    private static Map<String, WebSession> mCache = new HashMap<String, WebSession>();
    
    public static WebSession getInstance(HttpServletRequest request)
    {
        Logger.getAnonymousLogger().log(Level.FINEST, "GETTING WEB SESSION");
        dumpRequest(request);
        WebSession ssn = null;
        String access_token = null;
        if (request.getParameter("access_token") != null)
        {
            Logger.getAnonymousLogger().log(Level.FINEST, "Found access token in request");
            access_token = (String)request.getParameter("access_token");
            if (mCache.containsKey(access_token))
            {
                ssn = mCache.get(access_token);
                request.getSession().setAttribute("access_token", access_token);
                Logger.getAnonymousLogger().log(Level.FINEST, "Found session object in cache");
            }
        }
        else if (request.getSession().getAttribute("ssn") != null)
        {
            Logger.getAnonymousLogger().log(Level.FINEST, "Found session object in session");
            ssn = (WebSession)request.getSession().getAttribute("ssn");
        }
        else if (request.getSession().getAttribute("access_token") != null)
        {
            Logger.getAnonymousLogger().log(Level.FINEST, "Found access token in session");
            access_token = (String)request.getSession().getAttribute("access_token");
            if (mCache.containsKey(access_token))
            {
                ssn = mCache.get(access_token);
                request.getSession().setAttribute("access_token", access_token);
                Logger.getAnonymousLogger().log(Level.FINEST, "Found session object in cache");
            }
        }
        if (ssn == null)
        {
            ssn = new WebSession();
            Logger.getAnonymousLogger().log(Level.FINEST, "Creating new session object");
        }
        request.getSession().setAttribute("ssn", ssn);
        ssn.setRequest(request);

        if (!ssn.isLoggedIn())
        {
            if (StringUtils.isTrivial(access_token))
            {
                Logger.getAnonymousLogger().log(Level.FINEST, "No access token");
                return ssn;
            }
            try
            {
                Logger.getAnonymousLogger().log(Level.FINEST, "Validating access token");
                Content c = Request.Get("https://api.amazon.com/user/profile")
                        .addHeader("Authorization", "bearer " + access_token)
                        .execute()
                        .returnContent();
                @SuppressWarnings("rawtypes")
                Map m = new ObjectMapper().readValue(c.toString(), new TypeReference<Map>(){});
                ssn.setLinkedID((String)m.get("user_id"));
                ssn.setLinkedName((String)m.get("name"));
                ssn.setLinkedEmail((String)m.get("email"));
                mCache.put(access_token, ssn);
                ssn.setLoggedIn(true);
                request.getSession().setAttribute("access_token", access_token);
                Logger.getAnonymousLogger().log(Level.FINEST, "Validated with user ID ="+ssn.getLinkedID());
            }
            catch (Exception e)
            {
                Logger.getAnonymousLogger().log(Level.FINEST, e);
                return ssn;
            }
        }
        queryNoCreate(ssn);
        ssn.setUserLinked(ssn.getContext().getUser() != null);
        if (ssn.getContext().getUser() != null)
            Logger.getAnonymousLogger().log(Level.FINEST, "Logged in as user "+ssn.getContext().getUser().getSupportIdent());
        else
            Logger.getAnonymousLogger().log(Level.FINEST, "Unable to log in as user "+ssn.getLinkedID());
        WebActionLogic.performAction(ssn);
        return ssn;
    }
    
    private static void dumpRequest(HttpServletRequest request)
    {
        try
        {
            Logger.getAnonymousLogger().log(Level.FINEST, "AuthType: "+request.getAuthType());
            Logger.getAnonymousLogger().log(Level.FINEST, "CharacterEncoding: "+request.getCharacterEncoding());
            Logger.getAnonymousLogger().log(Level.FINEST, "ContentType: "+request.getContentType());
            Logger.getAnonymousLogger().log(Level.FINEST, "ContextPath: "+request.getContextPath());
            Logger.getAnonymousLogger().log(Level.FINEST, "LocalAddr: "+request.getLocalAddr());
            Logger.getAnonymousLogger().log(Level.FINEST, "LocalName: "+request.getLocalName());
            Logger.getAnonymousLogger().log(Level.FINEST, "Method: "+request.getMethod());
            Logger.getAnonymousLogger().log(Level.FINEST, "PathInfo: "+request.getPathInfo());
            Logger.getAnonymousLogger().log(Level.FINEST, "PathTranslated: "+request.getPathTranslated());
            Logger.getAnonymousLogger().log(Level.FINEST, "Protocol: "+request.getProtocol());
            Logger.getAnonymousLogger().log(Level.FINEST, "QueryString: "+request.getQueryString());
            Logger.getAnonymousLogger().log(Level.FINEST, "RemoteAddr: "+request.getRemoteAddr());
            Logger.getAnonymousLogger().log(Level.FINEST, "RemoteHost: "+request.getRemoteHost());
            Logger.getAnonymousLogger().log(Level.FINEST, "RemoteUser: "+request.getRemoteUser());
            Logger.getAnonymousLogger().log(Level.FINEST, "SessionId: "+request.getRequestedSessionId());
            Logger.getAnonymousLogger().log(Level.FINEST, "RequestURI: "+request.getRequestURI());
            Logger.getAnonymousLogger().log(Level.FINEST, "Scheme: "+request.getScheme());
            Logger.getAnonymousLogger().log(Level.FINEST, "ServerName: "+request.getServerName());
            Logger.getAnonymousLogger().log(Level.FINEST, "ServletPath: "+request.getServletPath());
            Logger.getAnonymousLogger().log(Level.FINEST, "ContentLength: "+request.getContentLength());
            Logger.getAnonymousLogger().log(Level.FINEST, "LocalPort: "+request.getLocalPort());
            Logger.getAnonymousLogger().log(Level.FINEST, "RemotePort: "+request.getRemotePort());
            Logger.getAnonymousLogger().log(Level.FINEST, "ServerPort: "+request.getServerPort());
            for (Enumeration<?> e = request.getAttributeNames(); e.hasMoreElements(); )
            {
                String name = (String)e.nextElement();
                String value = String.valueOf(request.getAttribute(name));
                Logger.getAnonymousLogger().log(Level.FINEST, "Attribute: "+name+"="+value);
            }
            for (Enumeration<?> e = request.getHeaderNames(); e.hasMoreElements(); )
            {
                String name = (String)e.nextElement();
                String value = String.valueOf(request.getHeader(name));
                Logger.getAnonymousLogger().log(Level.FINEST, "Header: "+name+"="+value);
            }
            for (Enumeration<?> e = request.getParameterNames(); e.hasMoreElements(); )
            {
                String name = (String)e.nextElement();
                String value = String.valueOf(request.getParameter(name));
                Logger.getAnonymousLogger().log(Level.FINEST, "Parameter: "+name+"="+value);
            }
        }
        catch (Exception e)
        {
            Logger.getAnonymousLogger().log(Level.FINEST, e);
        }
    }

    // operation functions
    private static CompContextBean queryNoCreate(WebSession ssn)
    {
        return doOperation(ssn, CompOperationBean.QUERY, null, ssn.getLinkedName(), ssn.getLinkedEmail(), null, 1, 0, 0, 0);
    }
    
    public static CompContextBean linkUser(WebSession ssn, String supportID, String supportPass)
    {
        return doOperation(ssn, CompOperationBean.LINK, supportID, supportPass, ssn.getLinkedName(), ssn.getLinkedEmail(), 1, 0, 0, 0);
    }
    
    private static CompContextBean doOperation(WebSession ssn, int id, String str1, String str2, String str3, String str4, long num1, long num2, long num3, long num4)
    {
        String identID = ssn.getLinkedID();
        CompOperationBean op = new CompOperationBean();
        op.setOperation(id);
        op.setIdentID(identID);
        op.setFlags(ssn.getRequest().getParameter("flags"));
        op.setStrParam1(str1);
        op.setStrParam2(str2);
        op.setStrParam3(str3);
        op.setStrParam4(str4);
        op.setNumParam1(num1);
        op.setNumParam2(num2);
        op.setNumParam3(num3);
        op.setNumParam4(num4);
        CompContextBean context = CompOperationLogic.operate(op);
        ssn.setContext(context);
        ssn.getMessages().clear();
        for (AudioMessageBean msg : context.getMessages())
            ssn.getMessages().add(TextAssets.expandInserts(msg));
        return context;
    }

}
