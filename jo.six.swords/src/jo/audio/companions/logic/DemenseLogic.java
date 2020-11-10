package jo.audio.companions.logic;

import java.util.HashMap;
import java.util.Map;

import jo.audio.companions.data.DemenseBean;
import jo.util.utils.obj.StringUtils;

public class DemenseLogic
{
    private static Map<String, DemenseBean> DEMENSES = new HashMap<>();
    
    public static void register(DemenseBean d)
    {
        DEMENSES.put(d.getID(), d);
        if (!StringUtils.isTrivial(d.getLiegeID()))
        {
            if (d.getLiegeID().equals(d.getID()))
                System.err.println("Circular demense reference: "+d.getID()+"/"+d.getName());
            d.setLiege(DEMENSES.get(d.getLiegeID()));
            d.getLiege().getVassals().add(d);
        }
    }
    
    public static DemenseBean get(String id)
    {
        if (id == null)
            return null;
        return DEMENSES.get(id);
    }

    public static String getLanguage(DemenseBean d)
    {
        while (d != null)
        {
            if (!StringUtils.isTrivial(d.getLanguage()))
                return d.getLanguage();
            d = d.getLiege();
        }
        return "IT";
    }
}
