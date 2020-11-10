package jo.audio.companions.web.logic;

import java.util.logging.Level;
import java.util.logging.Logger;

import jo.audio.companions.web.data.WebSession;
import jo.util.utils.obj.StringUtils;

public class WebActionLogic
{

    public static void performAction(WebSession ssn)
    {
        String action = ssn.getRequest().getParameter("action");
        if (StringUtils.isTrivial(action))
        {
            Logger.getAnonymousLogger().log(Level.FINEST, "No action to perform.");
            return;
        }
        Logger.getAnonymousLogger().log(Level.FINEST, "Performing action "+action);
        if (action.startsWith("navTop"))
            doNavTop(ssn, action);
        else if (action.startsWith("navMid"))
            doNavMiddle(ssn, action);
        else if (action.startsWith("navLow"))
            doNavLow(ssn, action);
        else
            switch (action)
            {
                case "userLink":
                    doLinkUser(ssn);
                    break;
            }
    }
    
    private static void doNavTop(WebSession ssn, String action)
    {
        ssn.setTopNav(action.substring(6).toLowerCase());
        ssn.setMiddleNav(null);
        ssn.setLowNav(null);
        Logger.getAnonymousLogger().log(Level.FINEST, "Navigation set to "+ssn.getTopNav());
    }
    
    private static void doNavMiddle(WebSession ssn, String action)
    {
        ssn.setMiddleNav(action.substring(6).toLowerCase());
        ssn.setLowNav(null);
        Logger.getAnonymousLogger().log(Level.FINEST, "Navigation set to "+ssn.getTopNav()+"/"+ssn.getMiddleNav());
    }
    
    private static void doNavLow(WebSession ssn, String action)
    {
        ssn.setLowNav(action.substring(6).toLowerCase());
        Logger.getAnonymousLogger().log(Level.FINEST, "Navigation set to "+ssn.getTopNav()+"/"+ssn.getMiddleNav()+"/"+ssn.getLowNav());
    }
    
    private static void doLinkUser(WebSession ssn)
    {
        String supportID = ssn.getRequest().getParameter("supportID");
        String supportPass = ssn.getRequest().getParameter("supportPassword");
        WebSessionLogic.linkUser(ssn, supportID, supportPass);
    }

}
