package jo.audio.loci.sandbox.test;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.logic.InitializeLogic;
import jo.audio.loci.sandbox.logic.InteractLogic;

class TestBase
{
    protected static String mUserName;
    protected static String mPassword;
    protected static String mToken;
    
    @BeforeEach
    protected void init()
    {
        SetupLogic.cleanup();
        InitializeLogic.initialize();
    }

    protected ExecuteContext talk(String command, String... validate)
    {
        System.out.println(">"+command);
        ExecuteContext context = InteractLogic.interact(mUserName, mPassword, mToken, command);
        LociPlayer player = (LociPlayer)context.getInvoker();
        boolean[] validated = new boolean[validate.length];
        for (String reply : player.getAndClearMessages())
        {
            System.out.println(reply);
            for (int i = 0; i < validate.length; i++)
                if (!validated[i])
                    if (reply.toLowerCase().indexOf(validate[i].toLowerCase()) >= 0)
                        validated[i] = true;
        }
        for (int i = 0; i < validated.length; i++)
            Assert.assertTrue("Could not find "+validate[i]+" in output", validated[i]);
        mToken = context.getInvoker().getURI();
        return context;
    }
}
