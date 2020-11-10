package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.app.CompApplicationHandler;
import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.logic.CompOperationLogic;
import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.data.AudioRequestBean;
import jo.util.utils.obj.StringUtils;

public class OperationLogic
{
    // wrapper functions
    public static CompContextBean query(CompState state, String linkedName, String linkedEmail)
    {
        return doOperation(state, CompOperationBean.QUERY, null, linkedName, linkedEmail, null, 0, 0, 0, 0);
    }
    
    public static CompContextBean queryRanks(CompState state, String linkedName, String linkedEmail)
    {
        return doOperation(state, CompOperationBean.QUERY, CompOperationBean.RANKS, linkedName, linkedEmail, null, 0, 0, 0, 0);
    }
    
    public static CompContextBean queryNearby(CompState state)
    {
        return doOperation(state, CompOperationBean.QUERY, CompOperationBean.NEARBY, null, null, null, 8, 0, 0, 0);
    }
    
    public static CompContextBean move(CompState state, int dir)
    {
        return doOperation(state, CompOperationBean.MOVE, null, null, dir, 0);
    }
    
    public static CompContextBean activate(CompState state, CompCompanionBean companion)
    {
        return doOperation(state, CompOperationBean.ACTIVATE, companion.getID(), null, 0, 0);
    }
    
    public static CompContextBean equip(CompState state, CompCompanionBean companion, CompItemInstanceBean item, int amnt)
    {
        return doOperation(state, CompOperationBean.EQUIP, companion.getID(), item.getID(), amnt, 0);
    }
    
    public static CompContextBean unequip(CompState state, CompCompanionBean companion, CompItemInstanceBean item, int amnt)
    {
        return doOperation(state, CompOperationBean.UNEQUIP, companion.getID(), item.getID(), amnt, 0);
    }

    public static CompContextBean fight(CompState state)
    {
        return doOperation(state, CompOperationBean.FIGHT, null, null, 0, 0);
    }

    public static CompContextBean abandonCombat(CompState state)
    {
        return doOperation(state, CompOperationBean.ABANDON_COMBAT, null, null, 0, 0);
    }

    public static CompContextBean enter(CompState state)
    {
        return doOperation(state, CompOperationBean.ENTER, null, null, 0, 0);
    }
    
    public static CompContextBean buy(CompState state, CompItemTypeBean item, int amnt)
    {
        return doOperation(state, CompOperationBean.BUY, item.getID(), null, amnt, 0);
    }
    
    public static CompContextBean sell(CompState state, CompItemInstanceBean item, int amnt)
    {
        return doOperation(state, CompOperationBean.SELL, item.getID(), null, amnt, 0);
    }
    
    public static CompContextBean hire(CompState state, String id)
    {
        return doOperation(state, CompOperationBean.HIRE, id, null, 0, 0);
    }
    
    public static CompContextBean fire(CompState state, CompCompanionBean comp)
    {
        return doOperation(state, CompOperationBean.FIRE, comp.getID(), null, 0, 0);
    }
    
    public static CompContextBean debug(CompState state, String command)
    {
        return doOperation(state, CompOperationBean.DEBUG, command, null, 0, 0);
    }
    
    public static CompContextBean yes(CompState state)
    {
        return doOperation(state, CompOperationBean.ANSWER, null, null, CompOperationBean.YES, 0);
    }
    
    public static CompContextBean no(CompState state)
    {
        return doOperation(state, CompOperationBean.ANSWER, null, null, CompOperationBean.NO, 0);
    }
    
    public static CompContextBean cancel(CompState state)
    {
        return doOperation(state, CompOperationBean.ANSWER, null, null, CompOperationBean.CANCEL, CompOperationBean.CANCEL);
    }
    
    public static CompContextBean direction(CompState state, int dir)
    {
        return doOperation(state, CompOperationBean.ANSWER, null, null, 0, dir);
    }

    public static CompContextBean consumeNews(CompState state, int msg)
    {
        return doOperation(state, CompOperationBean.CONSUMENEWS, null, null, msg, 0);
    }
    
    public static CompContextBean sleep(CompState state)
    {
        return doOperation(state, CompOperationBean.SLEEP, null, null, 0, 0);
    }
    
    private static CompContextBean doOperation(CompState state, int id, String str1, String str2, long num1, long num2)
    {
        return doOperation(state, id, str1, str2, null, null, num1, num2, 0, 0);
    }
    private static CompContextBean doOperation(CompState state, int id, String str1, String str2, String str3, String str4, long num1, long num2, long num3, long num4)
    {
        String identID = state.getUserID();
        CompOperationBean op = new CompOperationBean();
        op.setOperation(id);
        op.setIdentID(identID);
        op.setFlags(makeFlags(state));
        op.setStrParam1(str1);
        op.setStrParam2(str2);
        op.setStrParam3(str3);
        op.setStrParam4(str4);
        op.setNumParam1(num1);
        op.setNumParam2(num2);
        op.setNumParam3(num3);
        op.setNumParam4(num4);
        CompContextBean context = CompOperationLogic.operate(op);
        if (context.isNewText())
            ((CompApplicationHandler)state.getApplication()).updateText();
        state.setContext(context);
        for (AudioMessageBean resp : context.getMessages())
            state.getApplication().log("OperationLogic - reply = "+resp);
        state.respond(context.getMessages());
        return context;
    }

    public static String makeFlags(CompState state)
    {
        List<String> flags;
        flags = new ArrayList<String>();
        if ((state.getRequest().getOriginator() == AudioRequestBean.ALEXA))
            flags.add("ALEXA");
        if ((state.getRequest().getOriginator() == AudioRequestBean.APIAI))
            flags.add("GOOGLE");
        if ((state.getRequest().getOriginator() == AudioRequestBean.TELNET))
            flags.add("TELNET");
        flags.add("lang="+state.getRequest().getLanguage());
        return StringUtils.listize(flags, ",");
    }
}
