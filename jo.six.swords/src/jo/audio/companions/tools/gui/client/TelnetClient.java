package jo.audio.companions.tools.gui.client;
//Copyright 2015 Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland

import java.awt.Desktop;

//www.source-code.biz, www.inventec.ch/chdh
//
//This module is multi-licensed and may be used under the terms of any of the following licenses:
//
//LGPL, GNU Lesser General Public License, V2.1 or later, http://www.gnu.org/licenses/lgpl.html
//EPL, Eclipse Public License, V1.0 or later, http://www.eclipse.org/legal
//
//Please contact the author if you need another license.
//This module is provided "as is", without warranties of any kind.
//
//Home page: http://www.source-code.biz/snippets/java/CompTelnet

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.ClosedChannelException;
import java.util.Properties;

import jo.audio.util.model.data.AudioResponseBean;
import jo.audio.util.telnet.BaseTelnet;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

/**
 * This class implements a minimalist embedded Telnet server that redirects the
 * standard input/output streams of the Java runtime library (System.in, out and
 * err) to a Telnet client.
 * 
 * To open a port to an Elastic Beanstalk AWS server:
 * Go the App's dashboard page.
 * Choose Configuration
 * Select "Modify" on Load Balancer
 * Click "Add Listener"
 * Fill in port, set protocol to TCP
 * Save
 * Apply
 * 
 * Go to EC2 service
 * Select "Security Groups"
 * 
 */
public class TelnetClient extends BaseTelnet
{
    private String              mLocale = "en_US";
    private RepromptTimer       mReprompter = null;

    /**
     * Creates and starts a redirector.
     *
     * <p>
     * The redirector starts the Telnet server and waits for a client to
     * connect. Only one client can be connected at a time.
     *
     * @param tcpPort
     *            The TCP port number for the Telnet server.
     * @param blockingMode
     *            If true, the TCP channel is used in blocking mode and only
     *            blocking input is possible. If false, a non-blocking channel
     *            is used and non-blocking input is possible, but there is a bit
     *            more overhead for reading and writing.
     * @param charsetEncoding
     *            Character set encoding for the Telnet console data, e.g.
     *            "UTF-8".
     */
    public TelnetClient(int tcpPort)
    {
        super(tcpPort, true, "utf-8");
        debug("Starting Telnet Server on port "+tcpPort);
    }

    /**
     * This method is called after a Telnet client has connected. It is intended
     * to be overridden by the application. The default implementation provides
     * a simple line-mode input prompt.
     */
    public void processSessionDialog() throws Exception
    {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(getStdIn()));
        debug("Connection initiated");
        AudioResponseBean response = RequestLogic.performLaunchRequest(mLocale);
        printResponse(response);
        while (true)
        {
            print(">");
            String inbuf = null;
            try
            {
                inbuf = reader.readLine();
            }
            catch (ClosedChannelException e)
            {
                break;
            }
            debug(inbuf);
            response = RequestLogic.performIntentRequest(inbuf, mLocale);
            if (response.isShouldEndSession())
                break;
            printResponse(response);
        }
    }
    
    public void printResponse(AudioResponseBean response)
    {
        String outputSpeechText;
        if (!StringUtils.isTrivial(response.getCardContent()) && (response.getCardContent().trim().length() > 0))
        {
            outputSpeechText = toPlainText(response.getCardContent());
        }
        else
        {
            outputSpeechText = response.getOutputSpeechText();
            Properties props = new Properties();
            outputSpeechText = parseProps(outputSpeechText, props);
        }
        println(outputSpeechText);
        setupReprompt(response.getRepromptText());
    }

    private String parseProps(String output, Properties props)
    {
        for (;;)
        {
            int start = output.indexOf("[[");
            if (start < 0)
                break;
            int stop = output.indexOf("]]", start);
            if (stop < 0)
                break;
            String kv = output.substring(start + 2, stop);
            output = output.substring(0, start) + output.substring(stop + 2);
            int o = kv.indexOf('=');
            if (o >= 0)
                props.put(kv.substring(0, o).trim(), kv.substring(o + 1).trim());
        }
        return output;
    }

    private String toPlainText(String txt)
    {
        // treat empty <phoneme>s separately
        for (;;)
        {
            int o = txt.indexOf("<phoneme>");
            if (o < 0)
                break;
            int e = txt.indexOf("</phoneme>", o);
            if (e < 0)
                break;
            txt = txt.substring(0, o) + txt.substring(e + 10);
        }
        txt = StringUtils.exciseAllTags(txt);
        return txt;
    }
    
    private void setupReprompt(String text)
    {
        cancelReprompt();
        //debug("setupReprompt("+text+")");
        if (StringUtils.isTrivial(text))
            return;
        synchronized (TelnetClient.this)
        {
            mReprompter = new RepromptTimer(text);
        }
    }
    
    private void cancelReprompt()
    {
        //debug("cancelReprompt()");
        synchronized (TelnetClient.this)
        {
            if (mReprompter != null)
            {
                mReprompter.mCancel = true;
                //debug("Canceling reprompter "+mReprompter.hashCode());
                mReprompter = null;
            }
        }
    }
    
    class RepromptTimer implements Runnable
    {
        private static final long TIMEOUT = 15000L;
        
        String  mText;
        boolean mCancel;
        PrintStream mStdOut;
        
        public RepromptTimer(String txt)
        {
            mText = txt;
            mCancel = false;
            mStdOut = getStdOut();
            Thread t = new Thread(this, "Reprompt timer");
            t.start();
        }

        @Override
        public void run()
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
            if (!mCancel)
            {
                //debug(hashCode()+" Reprompting with "+mText);
                mStdOut.println("\b\b"+mText);
                mStdOut.print("> ");
            }
            else
                //debug(hashCode()+" Reprompter was canceled");
            synchronized (TelnetClient.this)
            {
                if (mReprompter == this)
                    mReprompter = null;
            }
            //debug(hashCode()+" Reprompter done");
        }
    }

    /**
     * This method is called when a fatal error occurs on the internal thread.
     * It is intended to be overridden by the application. The default
     * implementation writes an error message to System.err. When this method is
     * called, System.in/out/err are no longer redirected.
     */
    public void processFatalError(Exception e)
    {
        println();
        println("Fatal error in AudioTelnet: " + e);
    }

    protected void debug(String msg)
    {
        DebugUtils.debug(msg);
    }

    protected void debug(Throwable e)
    {
        DebugUtils.debug(e.toString());
        e.printStackTrace();
    }
    
    public static void main(String[] args) throws IOException
    {
        TelnetClient app = new TelnetClient(23);
        try
        {
            Desktop.getDesktop().browse(new URI("telnet://localhost:23"));
        }
        catch (URISyntaxException e1)
        {
            e1.printStackTrace();
        }
        try
        {
            app.waitForTermination();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
