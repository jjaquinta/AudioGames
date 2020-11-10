package jo.audio.companions.logic;

import java.util.ArrayList;
import java.util.List;

import jo.audio.common.logic.io.DataCallback;
import jo.audio.common.logic.io.DriverLogic;
import jo.audio.companions.data.CompIdentBean;
import jo.audio.companions.data.CompLogBean;
import jo.audio.companions.data.CompMonsterInstanceBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.logic.io.CompIdentDriver;
import jo.audio.companions.logic.io.CompLogDriver;
import jo.audio.companions.logic.io.CompUserDriver;

public class CompIOLogic
{
    static
    {
        // core
        DriverLogic.addDriver(new CompIdentDriver());
        DriverLogic.addDriver(new CompUserDriver());
        DriverLogic.addDriver(new CompLogDriver());
    }
    
    public static void saveUser(CompUserBean user)
    {
        DriverLogic.save(user);
    }
    
    public static CompUserBean getUserFromURI(String id)
    {
        int o = id.indexOf('?');
        if (o > 0)
            id = id.substring(0, o);
        return (CompUserBean)DriverLogic.getFromURI(id, CompUserBean.class);
    }

    public static List<CompUserBean> getUsersFromSupportID(String supportID)
    {
        List<CompUserBean> users = new ArrayList<>();
        List<?> results = DriverLogic.getFromField(CompUserBean.class, 0, 0, "supportIdent", supportID);
        for (Object o : results)
            users.add((CompUserBean)o);
        return users;
    }

    public static CompUserBean getUserFromSupport(String supportID, String supportPass)
    {
        List<?> results = DriverLogic.getFromField(CompUserBean.class, 1, 30*1000L, "supportIdent", supportID, "supportPassword", supportPass);
        if (results.size() > 0)
            return (CompUserBean)results.get(0);
        return null;
    }
    
    public static List<CompUserBean> getAllUsers()
    {
        List<CompUserBean> users = new ArrayList<>();
        DriverLogic.getAll(CompUserBean.class, users);
        return users;
    }
    
    public static void getAllUsersIncremental(DataCallback cb)
    {
        DriverLogic.getAllIncremental(CompUserBean.class, cb);
    }

    public static void deleteUser(CompUserBean user)
    {
        DriverLogic.delete(user);        
    }
    
    // Ident IO
    
    public static void saveIdent(CompIdentBean user)
    {
        DriverLogic.save(user);
    }

    public static CompIdentBean getIdentFromURI(String id)
    {
        int o = id.indexOf('?');
        if (o > 0)
            id = id.substring(0, o);
        return (CompIdentBean)DriverLogic.getFromURI(id, CompIdentBean.class);
    }
    
    public static void getAllIdentsIncremental(DataCallback cb)
    {
        DriverLogic.getAllIncremental(CompIdentBean.class, cb);
    }

    // Log IO
    
    public static void log(CompUserBean user, String action, String object, String amount, String comments)
    {
        CompLogBean l = new CompLogBean();
        l.setURI("complog://"+user.getURI().substring(11));
        l.setTime(System.currentTimeMillis());
        l.setLocation(user.getLocation());
        l.setAction(action);
        l.setObject(object);
        l.setAmount(amount);
        l.setComments(comments);
        DriverLogic.save(l);
    }

    public static void logKill(CompUserBean user,
            CompMonsterInstanceBean target, String weapon, int xp)
    {
        log(user, CompLogBean.KILL, target.getType().getID(), String.valueOf(xp), weapon);
    }

    public static void logTreasure(CompUserBean user, int gold)
    {
        log(user, CompLogBean.TREASURE, null, String.valueOf(gold), null);
    }

    public static void logBuy(CompUserBean user, String itemID, int amount,
            float cost)
    {
        log(user, CompLogBean.BUY, itemID, String.valueOf(cost), String.valueOf(amount));
    }

    public static void logSell(CompUserBean user, String itemID, int amount,
            float cost)
    {
        log(user, CompLogBean.SELL, itemID, String.valueOf(cost), String.valueOf(amount));
    }

    public static void logUse(CompUserBean user, String itemID, int amount)
    {
        log(user, CompLogBean.SELL, itemID, "", String.valueOf(amount));
    }

    public static void logKillBoss(CompUserBean user)
    {
        log(user, CompLogBean.BOSS, null, null, null);
    }
}
