package jo.audio.compedit.service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import jo.audio.compedit.app.CompEditApplicationHandler;
import jo.audio.compedit.data.CompEditIdentBean;
import jo.audio.compedit.logic.IdentLogic;
import jo.audio.util.BaseServlet;
import jo.audio.util.telnet.SingleAppTelnet;

public class CompEditServlet extends BaseServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 6998188851979224629L;

    private static CompEditServlet mInstance = null;
    
    public CompEditServlet()
    {
        mInstance = this;
//        DebugUtils.mLoggers.add(new LogEngine() {
//            @Override
//            public void log(int severity, String msg, Throwable exception)
//            {
//                if (exception != null)
//                    Logger.getAnonymousLogger().log(Level.FINEST, exception);
//                if (msg != null)
//                    Logger.getAnonymousLogger().log(Level.FINEST, msg);
//            }
//        });
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        this.setHandler(CompEditApplicationHandler.getInstance());
    }
    
    @Override
    protected void launchTelnet()
    {
        new SingleAppTelnet(mTelnetPort, this)
        {
            protected String doLogin(String username, String password)
            {
                CompEditIdentBean id = IdentLogic.getIdent(username);
                if (id != null)
                {
                    if (id.getPassword().equals(password))
                    {
                        return id.getUserID();
                    }
                }            
                return null;
            }
            
            protected String doRegister(String username, String password)
            {
                CompEditIdentBean id = IdentLogic.getIdent(username);
                if (id != null)
                {
                    if (id.getPassword().equals(password))
                        return id.getUserID();
                    else
                        return null;
                }
                id = IdentLogic.newInstance(username, password);
                return id.getUserID();
            }
        };
    }

    public static void debug(String string)
    {
        if (mInstance == null)
            System.out.println(string);
        else
            mInstance.log(string);
    }       
}
