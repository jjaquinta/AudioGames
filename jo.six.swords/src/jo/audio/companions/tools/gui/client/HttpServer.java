package jo.audio.companions.tools.gui.client;

import java.io.IOException;

import jo.util.utils.obj.IntegerUtils;

public class HttpServer extends HttpClient
{    
    public HttpServer(int port)
    {
        super(port);
    }

    public static void main(String[] args) throws IOException
    {
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
