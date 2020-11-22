package jo.audio.loci.sandbox.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.utils.ResponseUtils;

public class LociContainer extends LociItem
{
    public static final String PROFILE = "container";
    
    public static final String ID_OPEN = "open";

    public LociContainer(String uri)
    {
        super(uri, PROFILE);
        setVerbProfile("VerbProfileContainer");
    }
    
    public LociContainer(String uri, String profile)
    {
        super(uri, profile);
        setVerbProfile("VerbProfileContainer");
    }
    
    public LociContainer(JSONObject json)
    {
        super(json);
        setVerbProfile("VerbProfileContainer");
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
                itemNames.add(((LociObject)item).getName());
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
}
