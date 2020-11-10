package jo.audio.companions.logic;

import java.text.SimpleDateFormat;
import java.util.Date;

import jo.audio.common.data.SLDataBean;
import jo.audio.common.data.StringStringMap;
import jo.audio.common.logic.CommonIOLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;

public class AccountingLogic
{
    private static SimpleDateFormat mDate = new SimpleDateFormat("yyyy-MM-dd");

    public static void credit(final String account, final String uri, final int amount)
    {
        DebugUtils.trace("Crediting acct="+account+" with "+amount);
        Thread t = new Thread("Accounting") { public void run() { doCredit(account, uri, amount); } };
        t.start();
    }

    public static void doCredit(String account, String uri, int amount)
    {
        SLDataBean data = CommonIOLogic.getDataFromURI("sixswords://accounting");
        if (data == null)
            data = new SLDataBean();
        if (data.getSecondaryValues() == null)
            data.setSecondaryValues(new StringStringMap());
        String key = account+"."+mDate.format(new Date());
        int views = IntegerUtils.parseInt(data.getSecondaryValues().get(key));
        views += amount;
        data.getSecondaryValues().put(key, String.valueOf(views));
        CommonIOLogic.setDataSecondaryValue("sixswords://accounting", key, String.valueOf(views));
        DebugUtils.trace("Credited sixswords://accounting/"+key+"/"+views);
    }
}
