package jo.audio.loci.thieves.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.logic.InitializeLogic;
import jo.audio.loci.thieves.logic.InteractLogic;
import jo.util.html.URIBuilder;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.ResourceUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.xml.EntityUtils;

public class HttpServer
{
    private Thread              mServerThread;
    private boolean             mPleaseStop;
    private int                 mPort;

    public HttpServer(int tcpPort)
    {
        mPort = tcpPort;
        DebugUtils.debug("Starting HTTP Server on port "+tcpPort);
        start();
    }
    
    private void start()
    {
        mServerThread = new Thread("HTTP Server") { public void run() { doServer(); } };
        mPleaseStop = false;
        mServerThread.start();
    }
    
    private void stop()
    {
        mPleaseStop = true;
        if (mServerThread != null)
            mServerThread.interrupt();
    }
    
    public void waitForTermination() throws InterruptedException
    {
        if (mServerThread != null)
            mServerThread.join();
    }
    
    private void doServer()
    {
        while (!mPleaseStop)
        {
            try
            {
                ServerSocket server = new ServerSocket(mPort);
                while (!mPleaseStop)
                {
                    try
                    {
                        Socket sock = server.accept();
                        service(sock);
                    }
                    catch (IOException e)
                    {
                        
                    }
                    if (server.isClosed())
                        break;
                }
                server.close();
            }
            catch (IOException e)
            {
                
            }
        }
    }
    
    private void service(Socket soc) throws IOException
    {
        DebugUtils.debug("Connection");
        InputStream is = soc.getInputStream();
        try
        {
            Reader rdr = new InputStreamReader(is, "utf-8");
            Properties headers = new Properties();
            String protocol = readHeaders(rdr, headers);
            if (protocol.startsWith("OPTIONS"))
                handlePreflight(soc, headers);
            else
                handleRequest(soc, headers, protocol, rdr);
            rdr.close();
        }
        catch (Exception e)
        {
            DebugUtils.debug("Error in handle request");
            DebugUtils.debug(e.toString());
            e.printStackTrace();
        }
        soc.close();
        DebugUtils.debug("Connection closed");
    }
    
    private String readHeaders(Reader rdr, Properties headers) throws IOException
    {
        String protocol = readLine(rdr);
        DebugUtils.trace(protocol);
        for (;;)
        {
            String line = readLine(rdr);
            if (line.length() == 0)
                break;
            int o = line.indexOf(":");
            if (o < 0)
                break;
            headers.put(line.substring(0, o).trim(), line.substring(o + 1).trim());
        }
        return protocol;
    }
    
    private String readLine(Reader is) throws IOException
    {
        StringBuffer sb = new StringBuffer();
        for (;;)
        {
            int ch = is.read();
            if (ch == -1)
                break;
            if (ch == '\n')
                break;
            sb.append((char)ch);
        }
        return sb.toString();
    }
    
    private void handlePreflight(Socket soc, Properties headers) throws IOException
    {
        DebugUtils.info("Responding to pre-flight");
        OutputStream os = soc.getOutputStream();
        Writer wtr = new OutputStreamWriter(os);
        returnHeaders(wtr, "200");
        wtr.write("Content-Length: 0\n");
        wtr.write("Content-Type: text/html; charset=UTF-8\n");
        wtr.write("\n");
        wtr.write("\n");
        wtr.close();
    }
    
    // GET / - returns game page
    // GET /api - just calls API

    private void handleRequest(Socket soc, Properties headers, String protocol, Reader rdr) throws Exception
    {
        StringTokenizer st = new StringTokenizer(protocol, " ");
        if (st.countTokens() != 3)
            return;
        String method = st.nextToken();
        String path = st.nextToken();
        System.out.println("Handle Request "+method+" - "+path);
        if (method.equalsIgnoreCase("GET") && path.startsWith("/api"))
            handleAPIRequest(soc, headers, path);
        else if (path.equals("/"))
            handleGameRequest(soc);
        else if (path.equals("/terminate"))
            stop();
        else
            returnData(soc, "text/plain", "WTF?".getBytes());
    }
    
    private void handleAPIRequest(Socket soc, Properties headers, String path) throws IOException
    {
        Properties query = new Properties();
        URIBuilder.parseQuery(path, query);
        String text = query.getProperty("text");
        String username = query.getProperty("username");
        String password = query.getProperty("password");
        String token = query.getProperty("token");
        ExecuteContext response = InteractLogic.interact(username, password, token, text);
        printResponse(soc, headers, response);
    }
    
    private void handleGameRequest(Socket soc) throws IOException
    {
        byte[] data = ResourceUtils.loadSystemResourceBinary("index.html", HttpServer.class);
        returnData(soc, "text/html;charset=utf-8", data);
    }
    
    @SuppressWarnings("unchecked")
    public void printResponse(Socket soc, Properties headers, ExecuteContext response) throws IOException
    {
        LociPlayer player = (LociPlayer)response.getInvoker();
        String accept = headers.getProperty("Accept", "text/html");
        byte[] data;
        if ("application/json".equals(accept))
        {
            JSONObject json = new JSONObject();
            json.put("uri", player.getURI());
            JSONArray messages = new JSONArray();
            json.put("messages", messages);
            for (String message : player.getAndClearMessages())
                messages.add(message);
            json.put("online", player.getOnline());
            String jsonText = json.toJSONString();
            data = jsonText.getBytes("utf-8");
            accept = "application/json; charset=UTF-8";
        }
        else if ("text/plain".equals(accept))
        {
            StringBuffer outputSpeechText = new StringBuffer();
            for (String txt : player.getAndClearMessages())
                outputSpeechText.append(txt+"\n");
            data = outputSpeechText.toString().getBytes("utf-8");
            accept = "text/plain; charset=UTF-8";
        }
        else
        {
            String html = toHTML(response);
            data = html.getBytes("utf-8");
            accept = "text/html; charset=UTF-8";
        }
        returnData(soc, accept, data);
    }
    
    private void returnData(Socket soc, String contentType, byte[] data) throws IOException
    {
        OutputStream os = soc.getOutputStream();
        Writer wtr = new OutputStreamWriter(os, "utf-8");
        returnHeaders(wtr, "200");
        wtr.write("Content-Type: "+contentType+"\n");
        wtr.write("Content-Length: "+data.length+"\n");
        wtr.write("\n");
        //wtr.flush();
        //os.write(data);
        //os.close();
        wtr.write(new String(data, "utf-8"));
        wtr.flush();
        wtr.close();
    }
    
    private static void returnHeaders(Writer wtr, String status) throws IOException
    {
        wtr.write("HTTP/1.1 "+status+" OK\n");
        wtr.write("Date: "+(new Date())+"\n");
        wtr.write("Server: Apache/2.4.7 (Ubuntu)\n");
        wtr.write("access-control-expose-headers: content-type\n");
        wtr.write("access-control-allow-headers: content-type\n");
        wtr.write("access-control-allow-methods: GET, POST, OPTIONS, HEAD, PUT, DELETE, PATCH\n");
        wtr.write("access-control-allow-origin: *\n");
        wtr.write("Connection: Keep-Alive\n");
        wtr.write("Keep-Alive: timeout=5, max=98\n");
    }
    
    private static String toHTML(ExecuteContext response) throws IOException
    {
        /*
        String html = ResourceUtils.loadSystemResourceString("index.html", HttpServer.class);
        html = html.replace("<!-- display -->", EntityUtils.insertEntities(outputText, true));
        return html;
        */
        StringBuffer html = new StringBuffer();
        html.append("<html>");
        html.append("<header>");
        html.append("<title>Sandbox</title>");
        html.append("</header>");
        html.append("<body>");
        html.append("<p>");
        LociPlayer player = (LociPlayer)response.getInvoker();
        for (String txt : player.getAndClearMessages())
            html.append(EntityUtils.insertEntities(txt, true)+"<br/>");
        html.append("</p>");
        html.append("<form action=\"/api\">");
        if (player.getOnline())
            html.append("<input type=\"hidden\" id=\"token\" name=\"token\" value=\""+player.getURI()+"\"></input>");
        else
            html.append("<input type=\"hidden\" id=\"token\" name=\"token\" value=\"\"></input>");
        html.append("<input type=\"text\" id=\"text\" name=\"text\" size=\"40\"></input>");
        html.append("<input type=\"submit\"></input>");
        html.append("</form>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
    
    public static void main(String[] args) throws IOException
    {
        DebugUtils.debug = true;
        DebugUtils.mDebugLevel = DebugUtils.TRACE;
        InitializeLogic.initialize();
        int port = 8080;
        if (args.length > 0)
            port = IntegerUtils.parseInt(args[0]);
        if (port == 0)
            port = 8080;
        HttpServer app = new HttpServer(port);
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
