package jo.audio.companions.tools.gui.client;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import org.json.simple.JSONObject;

import jo.audio.util.ToJSONLogic;
import jo.audio.util.model.data.AudioResponseBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;
import jo.util.utils.xml.EntityUtils;

public class HttpClient
{
    private Thread              mServerThread;
    private boolean             mPleaseStop;
    private int                 mPort;

    public HttpClient(int tcpPort)
    {
        mPort = tcpPort;
        DebugUtils.debug("Starting Telnet Server on port "+tcpPort);
        start();
    }
    
    private void start()
    {
        mServerThread = new Thread("HTTP Server") { public void run() { doServer(); } };
        mPleaseStop = false;
        mServerThread.start();
    }
    
    @SuppressWarnings("unused")
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
        }
        soc.close();
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

    private void handleRequest(Socket soc, Properties headers, String protocol, Reader rdr) throws Exception
    {
        StringTokenizer st = new StringTokenizer(protocol, " ");
        /*String method =*/ st.nextToken();
        String path = st.nextToken();
        String language = headers.getProperty("Content-Language", "en_US");
        int o = path.indexOf("text=");
        AudioResponseBean response;
        if (o < 0)
            response = RequestLogic.performLaunchRequest(language);
        else
            response = RequestLogic.performIntentRequest(URLDecoder.decode(path.substring(o + 5), "utf-8"), language);
        printResponse(soc, headers, response);
    }
    
    public void printResponse(Socket soc, Properties headers, AudioResponseBean response) throws IOException
    {
        String accept = headers.getProperty("Accept", "text/html");
        byte[] data;
        if ("application/json".equals(accept))
        {
            JSONObject json = ToJSONLogic.toJSONFromBean(response);
            String jsonText = json.toJSONString();
            data = jsonText.getBytes("utf-8");
            accept = "application/json; charset=UTF-8";
        }
        else
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
            String html = toHTML(outputSpeechText);
            data = html.getBytes("utf-8");
            accept = "text/html; charset=UTF-8";
        }
        OutputStream os = soc.getOutputStream();
        Writer wtr = new OutputStreamWriter(os);
        returnHeaders(wtr, "200");
        wtr.write("Content-Type: "+accept+"\n");
        wtr.write("Content-Length: "+data.length+"\n");
        wtr.write("\n");
        wtr.flush();
        os.write(data);
        os.close();
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
    
    private static String toHTML(String outputText)
    {
        StringBuffer html = new StringBuffer();
        html.append("<html>");
        html.append("<header>");
        html.append("<title>Six Swords</title>");
        html.append("</header>");
        html.append("<body>");
        html.append("<p>");
        html.append(EntityUtils.insertEntities(outputText, true));
        html.append("</p>");
        html.append("<form action=\"/\">");
        html.append("<input type=\"text\" id=\"text\" name=\"text\" size=\"40\"></input>");
        html.append("<input type=\"submit\"></input>");
        html.append("</form>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
    
    public static void main(String[] args) throws IOException
    {
        HttpClient app = new HttpClient(80);
        try
        {
            Desktop.getDesktop().browse(new URI("http://localhost:80"));
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
