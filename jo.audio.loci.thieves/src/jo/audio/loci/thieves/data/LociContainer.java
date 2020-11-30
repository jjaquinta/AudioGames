package jo.audio.loci.thieves.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.utils.ResponseUtils;

public class LociContainer extends LociItem
{
    public static final String ID_OPEN = "open";
    public static final String ID_LOCKED = "locked";
    public static final String ID_LOCKABLE = "lockable";

    public LociContainer(String uri)
    {
        super(uri);
    }
    
    public LociContainer(JSONObject json)
    {
        super(json);
    }
    
    // utilities
    @Override
    public String[] getExtendedDescription(LociPlayer wrt)
    {
        List<String> desc = new ArrayList<String>();
        for (String d : super.getExtendedDescription(wrt))
            desc.add(d);
        if (getOpen())
        {
            List<String> itemNames = getContainsNames();
            if (itemNames.size() == 0)
                desc.add("It is empty.");
            else if (itemNames.size() == 1)
                desc.add("Inside is "+itemNames.get(0)+".");
            else
                desc.add("Inside are "+ResponseUtils.wordList(itemNames)+".");
        }
        else
            desc.add("It is closed");
        return desc.toArray(new String[0]);
    }

    public List<String> getContainsNames()
    {
        List<String> itemNames = new ArrayList<>();
        String[] contents = getContains();
        if (contents == null)
            return itemNames;
        for (String itemURI : contents)
        {
            LociBase item = DataStoreLogic.load(itemURI);
            if (item instanceof LociObject)
                itemNames.add(((LociObject)item).getPrimaryName());
        }
        return itemNames;
    }
    
    // getters and setters
    
    public boolean getOpen()
    {
        return getBoolean(ID_OPEN);
    }
    
    public void setOpen(boolean value)
    {
        setBoolean(ID_OPEN, value);
    }
    
    public boolean getLockable()
    {
        return getBoolean(ID_LOCKABLE);
    }
    
    public void setLockable(boolean value)
    {
        setBoolean(ID_LOCKABLE, value);
    }
    
    public boolean getLocked()
    {
        return getBoolean(ID_LOCKED);
    }
    
    public void setLocked(boolean value)
    {
        setBoolean(ID_LOCKED, value);
    }
}
