package jo.audio.companions.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import jo.audio.companions.app.CompApplicationHandler;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.logic.CompOperationLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.util.dynamo.DriverLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.LogEngine;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.LongUtils;
import jo.util.utils.obj.StringUtils;

public class CompAPIServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 6998188851979224629L;
    
    private CompApplicationHandler mApp;
    
    public CompAPIServlet()
    {
        mApp = CompApplicationHandler.getInstance();
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.doOptions() CORS request");
        //The following are CORS headers. Max age informs the 
        //browser to keep the results of this call for 1 day.
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Origin, Accept");
        response.setHeader("Access-Control-Max-Age", "86400");
        //Tell the browser what requests we allow.
        response.setHeader("Allow", "GET, HEAD, POST, TRACE, OPTIONS");
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        doGet(req, resp);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        //The following are CORS headers. Max age informs the 
        //browser to keep the results of this call for 1 day.
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS, PATCH");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Origin, Accept");
        resp.setHeader("Access-Control-Max-Age", "86400");
        //Tell the browser what requests we allow.
        resp.setHeader("Allow", "GET, HEAD, POST, TRACE, OPTIONS");
        String type = req.getParameter("type");
        Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.doGet() type="+type);
        try
        {
            JSONObject jresponse = new JSONObject();
            if ("region".equalsIgnoreCase(type))
            {
                int x = IntegerUtils.parseInt(req.getParameter("x"));
                int y = IntegerUtils.parseInt(req.getParameter("y"));
                int z = IntegerUtils.parseInt(req.getParameter("z"));
                CoordBean oord = new CoordBean(x, y, z);
                GenerationLogic.getSquare(oord); // make sure square generated
                RegionBean region = GenerationLogic.getRegion(oord);
                jresponse = region.toJSON();
            }
            else if ("op".equalsIgnoreCase(type))
            {
                String accessToken = req.getParameter("access-token");
                String userID = null;
                if (!StringUtils.isTrivial(accessToken))
                    userID = mApp.getProfileID(accessToken);            
                CompOperationBean op = new CompOperationBean();
                op.setOperation(IntegerUtils.parseInt(req.getParameter("op")));
                Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.doGet() op="+op.getOperation());
                op.setIdentID(userID);
                op.setStrParam1(req.getParameter("str1"));
                if (op.getOperation() == CompOperationBean.QUERY)
                {
                    op.setStrParam2(mApp.getProfileName(accessToken));
                    Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.doGet() autoset str2="+op.getStrParam2());
                }
                else
                    op.setStrParam2(req.getParameter("str2"));
                if (op.getOperation() == CompOperationBean.QUERY)
                {
                    op.setStrParam3(mApp.getProfileEmail(accessToken));
                    Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.doGet() autoset str3="+op.getStrParam3());
                }
                else
                    op.setStrParam3(req.getParameter("str3"));
                op.setStrParam4(req.getParameter("str4"));
                op.setNumParam1(LongUtils.parseLong(req.getParameter("num1")));
                op.setNumParam2(LongUtils.parseLong(req.getParameter("num2")));
                op.setNumParam3(LongUtils.parseLong(req.getParameter("num3")));
                op.setNumParam4(LongUtils.parseLong(req.getParameter("num4")));
                CompContextBean context = CompOperationLogic.operate(op);
                jresponse = context.toJSON();
                Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.doGet() response=");
                Logger.getAnonymousLogger().log(Level.FINEST, jresponse.toJSONString());
            }
            else if ("io".equalsIgnoreCase(type))
            {
                String action = req.getParameter("action");
                switch (action)
                {
                    case "clear":
                        Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.doGet() clearing cache");
                        DriverLogic.clearCaches();
                        break;
                    case "single":
                        Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.doGet() setting single threaded");
                        DriverLogic.setSingleThreaded(true);
                        break;
                    case "multi":
                        Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.doGet() setting multi threaded");
                        DriverLogic.setSingleThreaded(false);
                        break;
                    case "trace":
                        Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.doGet() tracing io");
                        DebugUtils.mLoggers.add(new LogEngine() {            
                            @Override
                            public void log(int severity, String msg, Throwable exception)
                            {
                                if (exception != null)
                                    Logger.getAnonymousLogger().log(Level.FINEST, exception);
                                if (msg != null)
                                    Logger.getAnonymousLogger().log(Level.FINEST, msg);
                            }
                        });
                        break;
                    case "untrace":
                        Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.doGet() untracing io");
                        DebugUtils.mLoggers.remove(DebugUtils.mLoggers.size() - 1);
                        break;
                }
            }
            byte[] bresponse = jresponse.toJSONString().getBytes("utf-8");
            resp.setContentType("application/json");
            resp.setContentLength(bresponse.length);
            Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.doGet() content length="+bresponse.length);
            resp.getOutputStream().write(bresponse);
        }
        catch (Exception e)
        {
            resp.setContentType("text/plain");
            PrintWriter ps = new PrintWriter(resp.getWriter());
            e.printStackTrace(ps);
            Logger.getAnonymousLogger().log(Level.FINEST, "CompAPIServlet.error:");
            Logger.getAnonymousLogger().log(Level.FINEST, e);
        }
    }
}
