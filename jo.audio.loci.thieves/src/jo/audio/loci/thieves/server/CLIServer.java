package jo.audio.loci.thieves.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociPlayerGhost;
import jo.audio.loci.thieves.logic.InitializeLogic;
import jo.audio.loci.thieves.logic.InteractLogic;
import jo.util.utils.DebugUtils;

public class CLIServer
{
    private ExecuteContext mResponse;
    private LociPlayer     mPlayer;
    private String         mUsername;
    private String         mPassword;
    private String         mToken;
    private OutputTimer    mPrompter = null;

    public void run()
    {
        DebugUtils.debug("Session started");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        mResponse = InteractLogic.interact(mUsername, mPassword,
                mToken, "look");
        while (true)
        {
            handleResponse();
            if (!mPlayer.getOnline())
            {
                printMessages();
                DebugUtils.debug(mPlayer.getPrimaryName()+" is offline");
                break;
            }
            // print(">");
            String inbuf = null;
            try
            {
                inbuf = reader.readLine();
            }
            catch (IOException e)
            {
                break;
            }
            DebugUtils.debug(inbuf);
            mResponse = InteractLogic.interact(mUsername, mPassword, mToken,
                    inbuf);
        }
        mPrompter.cancel();
        mPlayer.setOnline(false);
        if (mPlayer instanceof LociPlayerGhost)
        {
            LociBase parent = DataStoreLogic.load(mPlayer.getContainedBy());
            ContainmentLogic.remove((LociObject)parent, mPlayer);
        }
        DebugUtils.debug("Session terminated");
    }

    private void handleResponse()
    {
        mPlayer = (LociPlayer)mResponse.getInvoker();
        mToken = mPlayer.getURI();
        if (mPrompter == null)
            mPrompter = new OutputTimer();
        else
            mPrompter.interrupt();
    }

    private synchronized void printMessages()
    {
        String[] text = mPlayer.getAndClearMessages();
        for (String txt : text)
            println(txt);
    }

    protected void println(String msg)
    {
        System.out.println(msg);
    }
    
    class OutputTimer implements Runnable
    {
        private static final long TIMEOUT = 1*1000L;
        
        private boolean mCancel;
        private Thread  mThread;
        
        public OutputTimer()
        {
            mCancel = false;
            mThread = new Thread(this, "Prompt timer");
            mThread.start();
        }

        public void interrupt()
        {
            mThread.interrupt();
        }
        
        public void cancel()
        {
            mCancel = true;
            mThread.interrupt();
        }

        @Override
        public void run()
        {
            for (;;)
            {
                try
                {
                    //debug(hashCode()+" Reprompter sleeping");
                    Thread.sleep(TIMEOUT);
                    //debug(hashCode()+" Reprompter done sleeping");
                }
                catch (InterruptedException e)
                {
                }
                if (mCancel)
                    break;
                printMessages();
            }
        }
    }

    public static void main(String[] args)
    {
        DebugUtils.debug = true;
        DebugUtils.mDebugLevel = DebugUtils.TRACE;
        InitializeLogic.initialize();
        CLIServer app = new CLIServer();
        try
        {
            app.run();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
