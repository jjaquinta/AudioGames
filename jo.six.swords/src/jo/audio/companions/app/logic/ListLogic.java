package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.logic.ItemLogic;
import jo.util.utils.DebugUtils;

public class ListLogic
{
    public static List<String> itemIDs(List<String> itemIDs)
    {
        List<String> list = new ArrayList<>();
        for (String itemID : itemIDs)
        {
            CompItemTypeBean item = ItemLogic.getItemType(itemID);
            list.add(item.getName());
        }
        return list;
    }
    public static List<String> itemInstances(List<CompItemInstanceBean> items)
    {
        return itemInstances(items, -1);
    }
    public static List<String> itemInstances(List<CompItemInstanceBean> items, int filter)
    {
        List<String> list = new ArrayList<>();
        for (CompItemInstanceBean item : items)
        {
            if ((filter >= 0) && (filter != item.getType().getType()))
                continue;
            try
            {
                if (item.getQuantity() > 1)
                    list.add(item.getQuantity()+" "+item.getFullName());
                else
                    list.add(item.getFullName());
            }
            catch (IllegalArgumentException e)
            {
                DebugUtils.trace("Hmm. We seem to have an item that doesn't exist: "+item.getID());
            }
        }
        return list;
    }
    public static List<String> companions(List<CompCompanionBean> companions, String excludeID)
    {
        List<String> compNames = new ArrayList<>();
        for (CompCompanionBean comp : companions)
        {
            if (comp.getID().equals(excludeID))
                continue;
            compNames.add(comp.getName());
        }
        return compNames;
    }
}
