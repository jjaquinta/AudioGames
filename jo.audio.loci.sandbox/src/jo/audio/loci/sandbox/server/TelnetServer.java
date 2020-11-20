package jo.audio.loci.sandbox.server;
//Copyright 2015 Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland

import java.io.IOException;

import jo.audio.loci.core.utils.BaseTelnet;
import jo.audio.loci.sandbox.logic.InitializeLogic;
import jo.util.utils.DebugUtils;

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
public class TelnetServer extends BaseTelnet
{
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
    public TelnetServer(int tcpPort)
    {
        super(tcpPort, true, "utf-8");
        DebugUtils.debug("Starting Telnet Server on port "+tcpPort);
    }

    /**
     * This method is called after a Telnet client has connected. It is intended
     * to be overridden by the application. The default implementation provides
     * a simple line-mode input prompt.
     */
    public void processSessionDialog() throws Exception
    {
        TelnetSession ssn = new TelnetSession(getStdIn(), getStdOut());
        try
        {
            ssn.run();
        }
        catch (Exception e)
        {
            DebugUtils.trace("Uncaught exception", e);
            throw new IllegalStateException("Unknown exception during connection", e);
        }
    }
    
    public static void main(String[] args) throws IOException
    {
        DebugUtils.debug = true;
        DebugUtils.mDebugLevel = DebugUtils.TRACE;
        InitializeLogic.initialize();
        TelnetServer app = new TelnetServer(23);
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
