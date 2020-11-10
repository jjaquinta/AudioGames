package jo.audio.compedit.logic;

import java.util.ArrayList;
import java.util.List;

import jo.audio.common.logic.io.DriverLogic;
import jo.audio.compedit.data.CompEditIdentBean;
import jo.audio.compedit.data.CompEditModuleBean;
import jo.audio.compedit.data.CompEditUserBean;
import jo.audio.compedit.logic.io.CompEditIdentDriver;
import jo.audio.compedit.logic.io.CompEditModuleDriver;
import jo.audio.compedit.logic.io.CompEditUserDriver;
import jo.util.utils.DebugUtils;

public class CompEditIOLogic
{
    static
    {
        // core
        DriverLogic.addDriver(new CompEditIdentDriver());
        DriverLogic.addDriver(new CompEditUserDriver());
        DriverLogic.addDriver(new CompEditModuleDriver());
    }

    // user
    
    public static void saveUser(CompEditUserBean user)
    {
        DebugUtils.trace("Saving user "+user.getURI());
        DriverLogic.save(user);
    }
    
    public static CompEditUserBean getUserFromURI(String id)
    {
        int o = id.indexOf('?');
        if (o > 0)
            id = id.substring(0, o);
        return (CompEditUserBean)DriverLogic.getFromURI(id, CompEditUserBean.class);
    }
    
    public static List<CompEditUserBean> getAllUsers()
    {
        List<CompEditUserBean> users = new ArrayList<>();
        DriverLogic.getAll(CompEditUserBean.class, users);
        return users;
    }

    // Ident IO
    
    public static void saveIdent(CompEditIdentBean user)
    {
        DriverLogic.save(user);
    }

    public static CompEditIdentBean getIdentFromURI(String id)
    {
        int o = id.indexOf('?');
        if (o > 0)
            id = id.substring(0, o);
        return (CompEditIdentBean)DriverLogic.getFromURI(id, CompEditIdentBean.class);
    }

    // modules
    
    public static void saveModule(CompEditModuleBean mod)
    {
        DriverLogic.save(mod);
    }
    
    public static CompEditModuleBean getModuleFromURI(String id)
    {
        int o = id.indexOf('?');
        if (o > 0)
            id = id.substring(0, o);
        return (CompEditModuleBean)DriverLogic.getFromURI(id, CompEditModuleBean.class);
    }
    
    public static List<CompEditModuleBean> getAllModules()
    {
        List<CompEditModuleBean> modules = new ArrayList<>();
        if (System.getProperty("companions.offline") == null)
            DriverLogic.getAll(CompEditModuleBean.class, modules);
        return modules;
    }
}
