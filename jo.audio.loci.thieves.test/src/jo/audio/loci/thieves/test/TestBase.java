package jo.audio.loci.thieves.test;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.logic.InitializeLogic;
import jo.audio.loci.thieves.logic.InteractLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;

public class TestBase
{
    protected static Random mRandom;
    protected static String mUserName;
    protected static String mPassword;
    protected static String mToken;
    protected static ExecuteContext mLastContext;
    protected static String mLastReply;
    
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
        mLastReply = output.toString();
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

    protected void demoteFromAdmin()
    {
        LociPlayer player = (LociPlayer)mLastContext.getInvoker();
        player.demoteFromAdmin();
    }
    
    protected void traverse(String... nodes)
    {
        for (int i = 0; i < nodes.length; i++)
            talk(nodes[i], (i>0)?nodes[i-1]:"", (i<nodes.length-1)?nodes[i+1]:"");
    }
    
    protected void unlock(String item)
    {
        for (int i = 0; i < 10; i++)
        {
            talk("unlock "+item);
            if (mLastReply.toLowerCase().indexOf("you unlock") >= 0)
                return;
            if (mLastReply.toLowerCase().indexOf("unable to execute") >= 0)
                Assert.fail("Could not unlock '"+item+"': "+mLastReply);
        }
        Assert.fail("Too many tries to unlock");
    }
    
    protected void lock(String item)
    {
        for (int i = 0; i < 10; i++)
        {
            talk("lock "+item);
            if (mLastReply.toLowerCase().indexOf("you lock") >= 0)
                return;
            if (mLastReply.toLowerCase().indexOf("unable to execute") >= 0)
                Assert.fail("Could not lock '"+item+"': "+mLastReply);
        }
        Assert.fail("Too many tries to lock");
    }
    
    protected void climb(String dir, String expected)
    {
        for (int i = 0; i < 10; i++)
        {
            talk(dir);
            if (mLastReply.toLowerCase().indexOf("you fail your climb check") < 0)
            {
                if (mLastReply.toLowerCase().indexOf(expected.toLowerCase()) < 0)
                    Assert.fail("Could not find '"+expected+"': "+mLastReply);
                return;
            }
        }
        Assert.fail("Too many tries to climb");
    }
    
    protected void up(String expected)
    {
        climb("up", expected);
    }
    
    protected void down(String expected)
    {
        climb("down", expected);
    }
    
    private static Pattern mGoldPattern = Pattern.compile("([0-9]+) gold", Pattern.CASE_INSENSITIVE);
    
    protected int getDynamicGold()
    {
        Matcher m = mGoldPattern.matcher(mLastReply);
        if (!m.find())
            Assert.fail("Cannot find '### gold' in '"+mLastReply+"'");
        String goldTxt = m.group(1);
        int gold = IntegerUtils.parseInt(goldTxt);
        return gold;
    }
}
