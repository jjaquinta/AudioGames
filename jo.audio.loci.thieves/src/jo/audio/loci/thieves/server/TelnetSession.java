package jo.audio.loci.thieves.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.TypeAheadContext;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.ExecuteLogic;
import jo.audio.loci.core.utils.BaseTelnet;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociPlayerGhost;
import jo.audio.loci.thieves.logic.InteractLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.FileUtils;

public class TelnetSession
{
    private BaseTelnet.RedirectorInputStream mStdIn;
    private PrintStream                      mStdOut;
    private File                             mTranscript;

    private ExecuteContext                   mResponse;
    private TypeAheadContext                 mTypeAhead;
    private LociPlayer                       mPlayer;
    private String                           mUsername;
    private String                           mPassword;
    private String                           mToken;
    private OutputTimer                      mPrompter = null;

    public TelnetSession(BaseTelnet.RedirectorInputStream stdIn,
            PrintStream stdOut)
    {
        mStdIn = stdIn;
        mStdOut = stdOut;
    }

    public void run()
    {
        DebugUtils.debug("Connection initiated");
        try
        {
            mResponse = InteractLogic.interact(mUsername, mPassword, mToken,
                    "look");
            mTypeAhead = ExecuteLogic.typeAhead(mResponse.getInvoker());
            while (true)
            {
                handleResponse();
                if (!mPlayer.getOnline())
                {
                    printMessages();
                    DebugUtils.debug(mPlayer.getPrimaryName() + " is offline");
                    break;
                }
                // print(">");
                String inbuf = null;
                try
                {
                    inbuf = readLine();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    break;
                }
                DebugUtils.debug(inbuf);
                if (mTranscript != null)
                    try
                    {
                        FileUtils.appendFile(">"+inbuf+System.getProperty("line.separator"), mTranscript);
                    }
                    catch (IOException e)
                    {
                    }
                mResponse = InteractLogic.interact(mUsername, mPassword, mToken,
                        inbuf);
                mTypeAhead = ExecuteLogic.typeAhead(mResponse.getInvoker());
            }
            mPrompter.cancel();
            mPlayer.setOnline(false);
            if (mPlayer instanceof LociPlayerGhost)
            {
                LociBase parent = DataStoreLogic.load(mPlayer.getContainedBy());
                ContainmentLogic.remove((LociObject)parent, mPlayer);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DebugUtils.debug("Connection terminated");
        }
    }

    private String readLine() throws IOException
    {
        StringBuffer inbuf = new StringBuffer();
        for (;;)
        {
            int ch = mStdIn.readChar(true);
            if (ch == -1)
                throw new IOException("End of input");
            if (ch == '\b')
            {
                if (inbuf.length() > 0)
                {
                    inbuf.setLength(inbuf.length() - 1);
                    backspace();
                }
            }
            else if ((ch == '\r') || (ch == '\n'))
            {
                if (inbuf.length() > 0)
                {
                    newLine();
                    break;
                }
            }
            else if (ch == '?')
            {
                promptTypeAhead(inbuf.toString());
            }
            else if (ch == '\t')
            {
                performTypeAhead(inbuf);
            }
            else if (ch == 21)
            {
                for (int i = 0; i < inbuf.length(); i++)
                    backspace();
                inbuf.setLength(0);
            }
            else
            {
                mStdOut.write(ch);
                inbuf.append((char)ch);
            }
        }
        return inbuf.toString();
    }

    protected void backspace()
    {
        mStdOut.write('\b');
        mStdOut.write(' ');
        mStdOut.write('\b');
    }

    private void promptTypeAhead(String sofar) throws IOException
    {
        sofar = sofar.toLowerCase();
        List<String> candidates = new ArrayList<>();
        for (String cmd : mTypeAhead.getCommands())
            if (cmd.toLowerCase().startsWith(sofar))
                candidates.add(cmd);
        if (candidates.size() == 0)
        {
            mStdOut.write(7); // bell
            return;
        }
        newLine();
        for (String cmd : candidates)
            println(cmd);
        mStdOut.write(sofar.getBytes());
    }

    protected void newLine()
    {
        mStdOut.write('\r');
        mStdOut.write('\n');
    }

    private void performTypeAhead(StringBuffer inbuf) throws IOException
    {
        String sofar = inbuf.toString().toLowerCase();
        List<String> candidates = new ArrayList<>();
        for (String cmd : mTypeAhead.getCommands())
            if (cmd.toLowerCase().startsWith(sofar))
                candidates.add(cmd.substring(sofar.length()));
        if (candidates.size() == 0)
        {
            mStdOut.write(7); // bell
            return;
        }
        String best = candidates.get(0);
        for (int i = 1; i < candidates.size(); i++)
            best = common(best, candidates.get(i));
        if (best.length() == 0)
        {
            mStdOut.write(7); // bell
            return;
        }
        inbuf.append(best);
        mStdOut.write(best.getBytes("utf-8"));
    }
    
    private String common(String s1, String s2)
    {
        int l = Math.min(s1.length(), s2.length());
        for (int i = 0; i < l; i++)
        {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);
            if (c1 != c2)
                break;
        }
        return s1.substring(0, l);
    }

    private void handleResponse()
    {
        mPlayer = (LociPlayer)mResponse.getInvoker();
        mToken = mPlayer.getURI();
        if (mPrompter == null)
            mPrompter = new OutputTimer();
        else
            mPrompter.interrupt();
        mTranscript = new File("transcripts"+System.getProperty("file.separator")+mPlayer.getPrimaryName()+".txt");
    }

    private synchronized void printMessages()
    {
        String[] text = mPlayer.getAndClearMessages();
        for (String txt : text)
            println(txt);
    }

    protected void println(String msg)
    {
        mStdOut.println(msg);
        mStdOut.flush();
        if (mTranscript != null)
            try
            {
                FileUtils.appendFile("<"+msg+System.getProperty("line.separator"), mTranscript);
            }
            catch (IOException e)
            {
            }
    }

    class OutputTimer implements Runnable
    {
        private static final long TIMEOUT = 1 * 1000L;

        private boolean           mCancel;
        private Thread            mThread;

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
                    // debug(hashCode()+" Reprompter sleeping");
                    Thread.sleep(TIMEOUT);
                    // debug(hashCode()+" Reprompter done sleeping");
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
}
