package jo.six.swords.test;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;

import jo.audio.companions.app.CompApplicationHandler;
import jo.audio.companions.data.CompIdentBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.logic.CompIOLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.companions.tools.gui.client.RequestLogic;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioResponseBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class Base
{
    protected String  mUsername;
    protected String  mPassword;
    protected String  mLanguage;
    
    protected AudioResponseBean mResponse;
    
    @BeforeEach
    protected void setup()
    {
        mUsername = "amadan";
        mPassword = "lollipop";
        mLanguage = "en_US";
        String userURI = "compuser:///"+mUsername;
        CompUserBean user = CompIOLogic.getUserFromURI(userURI);
        if (user != null)
            CompIOLogic.deleteUser(user);
        String identURI = "scid://"+mUsername;
        CompIdentBean ident = CompIOLogic.getIdentFromURI(identURI);
        if (ident != null)
            CompIOLogic.deleteIdent(ident);
        BaseUserState.RND.setSeed(0L);
        GenerationLogic.clearCache();
        CompApplicationHandler.getInstance().testClearFromCache(null);
    }
    
    protected CompUserBean getUser()
    {
        CompUserBean user = CompIOLogic.getUserFromURI("compuser:///"+mUsername);
        return user;
    }
    
    protected void transact(String toSay, String... toHear) throws IOException
    {
        if (StringUtils.isTrivial(toSay))
            mResponse = RequestLogic.performLaunchRequest(mUsername, mPassword, mLanguage);
        else
        {
            System.out.println(">"+toSay);
            mResponse = RequestLogic.performIntentRequest(toSay, mUsername, mPassword, mLanguage);
        }
        System.out.println("<"+mResponse.getCardContent());
        assertHeard(toHear);
    }
    
    protected boolean isHeard(String... toHear)
    {
        String heard = mResponse.getCardContent();
        for (String expected : toHear)
        {
            if (expected.startsWith("$"))
            {
                if (!isContainsModelText(heard, expected.substring(1)))
                    return false;
            }
            else
            {
                if (heard.toLowerCase().indexOf(expected.toLowerCase()) < 0)
                    return false;
            }
        }
        return true;
    }
    
    protected void assertHeard(String... toHear)
    {
        String heard = mResponse.getCardContent();
        for (String expected : toHear)
        {
            if (expected.startsWith("$"))
                assertContainsModelText(heard, expected.substring(1));
            else
                Assert.assertTrue("Expected '"+expected+"' in '"+heard+"'", heard.toLowerCase().indexOf(expected.toLowerCase()) >= 0);
        }
    }
    
    protected boolean isContainsModelText(String heard, String id)
    {
        heard = heard.toLowerCase();
        List<String> texts = RequestLogic.getModel().getText(mLanguage, id);
        for (String text : texts)
            if (containsModelText(heard, text))
                return true;
        return false;
    }
    
    protected void assertContainsModelText(String heard, String id)
    {
        Assert.assertTrue("Expected $"+id+" in '"+heard+"'", isContainsModelText(heard, id));
    }
    
    private boolean containsModelText(String heard, String text)
    {
        String[] texts = text.split("%.");
        for (String t : texts)
        {
            int o = heard.indexOf(t.toLowerCase());
            if (o < 0)
                return false;
            heard = heard.substring(o+t.length());
        }
        return true;
    }
    
    protected void north(String... toHear) throws IOException
    {
        transact("north", "$"+CompanionsModelConst.TEXT_YOU_TRAVEL_NORTH);
        assertHeard(toHear);
    }
    
    protected void south(String... toHear) throws IOException
    {
        transact("south", "$"+CompanionsModelConst.TEXT_YOU_TRAVEL_SOUTH);
        assertHeard(toHear);
    }
    
    protected void east(String... toHear) throws IOException
    {
        transact("east", "$"+CompanionsModelConst.TEXT_YOU_TRAVEL_EAST);
        assertHeard(toHear);
    }
    
    protected void west(String... toHear) throws IOException
    {
        transact("west", "$"+CompanionsModelConst.TEXT_YOU_TRAVEL_WEST);
        assertHeard(toHear);
    }
    
    protected void debugOn()
    {
        DebugUtils.mDebugLevel = DebugUtils.TRACE;
    }
    
    protected void debugOff()
    {
        DebugUtils.mDebugLevel = DebugUtils.INFO;
    }
}
