package jo.audio.loci.thieves.test;

import java.util.Random;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.logic.InitializeLogic;
import jo.audio.loci.thieves.logic.InteractLogic;
import jo.util.utils.DebugUtils;

public class TestBase
{
    protected static Random mRandom;
    protected static String mUserName;
    protected static String mPassword;
    protected static String mToken;
    protected static ExecuteContext mLastContext;
    
    @BeforeEach
    protected void init()
    {
        SetupLogic.cleanup();
        InitializeLogic.initialize();
        DebugUtils.debug = false;
        mToken = null;
        mUserName = null;
        mPassword = null;
        mRandom = new Random(0);
    }

    protected ExecuteContext talk(String command, String... validate)
    {
        System.out.println(">"+command);
        mLastContext = InteractLogic.interact(mUserName, mPassword, mToken, command);
        LociPlayer player = (LociPlayer)mLastContext.getInvoker();
        boolean[] validated = new boolean[validate.length];
        StringBuffer output = new StringBuffer();
        for (String reply : player.getAndClearMessages())
        {
            if (output.length() > 0)
                output.append(", ");
            output.append("\""+reply+"\"");
            System.out.println(reply);
            for (int i = 0; i < validate.length; i++)
                if (!validated[i])
                    if (validate[i].startsWith("!"))
                    {
                        if (reply.toLowerCase().indexOf(validate[i].substring(1).toLowerCase()) >= 0)
                            validated[i] = true;
                    }
                    else
                    {
                        if (reply.toLowerCase().indexOf(validate[i].toLowerCase()) >= 0)
                            validated[i] = true;
                    }
        }
        for (int i = 0; i < validated.length; i++)
            if (validate[i].startsWith("!"))
                Assert.assertFalse("Found "+validate[i]+" in output '"+output+"'", validated[i]);
            else
                Assert.assertTrue("Could not find "+validate[i]+" in output '"+output+"'", validated[i]);
        mToken = mLastContext.getInvoker().getURI();
        return mLastContext;
    }
    
    public void trace()
    {
        DebugUtils.mDebugLevel = DebugUtils.TRACE;
    }
    
    protected String randomWord()
    {
        int len = randomNumber();
        char[] word = new char[len];
        for (int i = 0; i < word.length; i++)
            word[i] = (char)('a' + mRandom.nextInt(26));
        return new String(word);
    }

    protected int randomNumber()
    {
        return mRandom.nextInt(6)+mRandom.nextInt(6)+mRandom.nextInt(6);
    }

    protected void promoteToAdmin()
    {
        LociPlayer player = (LociPlayer)mLastContext.getInvoker();
        player.promnoteToAdmin();
    }
}
