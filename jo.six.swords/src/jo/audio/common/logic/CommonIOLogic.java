package jo.audio.common.logic;

import jo.audio.common.data.SLDataBean;
import jo.audio.common.logic.io.DriverLogic;
import jo.audio.common.logic.io.SLDataDriver;

public class CommonIOLogic
{
    static
    {
        // common
        DriverLogic.addDriver(new SLDataDriver());
    }

    // Data IO
    
    public static void saveData(SLDataBean data)
    {
        DriverLogic.save(data);
    }

    public static SLDataBean getDataFromURI(String id)
    {
        int o = id.indexOf('?');
        if (o > 0)
            id = id.substring(0, o);
        return (SLDataBean)DriverLogic.getFromURI(id, SLDataBean.class);
    }

    public static String getDataPrimaryValue(String id)
    {
        SLDataBean data = CommonIOLogic.getDataFromURI(id);
        if (data == null)
            return null;
        return data.getPrimaryValue();
    }

    public static String getDataSecondaryValue(String id, String key)
    {
        SLDataBean data = CommonIOLogic.getDataFromURI(id);
        if (data == null)
            return null;
        return data.getSecondaryValues().get(key);
    }

    public static void setDataSecondaryValue(String id, String key, String value)
    {
        SLDataBean data = CommonIOLogic.getDataFromURI(id);
        if (data == null)
        {
            data = new SLDataBean();
            data.setKey(id);
        }
        data.getSecondaryValues().put(key, value);
        saveData(data);
    }
}
