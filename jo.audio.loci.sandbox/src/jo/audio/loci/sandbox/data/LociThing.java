package jo.audio.loci.sandbox.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.utils.ResponseUtils;
import jo.audio.loci.sandbox.vprofile.VerbProfileThing;

public class LociThing extends LociObject
{
    public static final String PROFILE = "thing";
    
    public static final String ID_HELP_TEXT = "helpText";
    public static final String ID_OWNER = "owner";
    public static final String ID_OPENABLE = "openable";
    public static final String ID_OPEN = "open";

    public LociThing(String uri)
    {
        super(uri, PROFILE);
        setVerbProfile(VerbProfileThing.class);
    }
    
    public LociThing(String uri, String profile)
    {
        super(uri, profile);
        setVerbProfile(VerbProfileThing.class);
    }
    
    public LociThing(JSONObject json)
    {
        super(json);
        setVerbProfile(VerbProfileThing.class);
    }
    
    // utilities
    public String[] getExtendedDescription()
    {
        List<String> desc = new ArrayList<String>();
        desc.add(getName());
        desc.add(getDescription());
        if (getOpenable())
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
    
    public String getHelpText()
    {
        return getString(ID_HELP_TEXT);
    }
    
    public void setHelpText(String value)
    {
        setString(ID_HELP_TEXT, value);
    }
    
    public String getOwner()
    {
        return getString(ID_OWNER);
    }
    
    public void setOwner(String value)
    {
        setString(ID_OWNER, value);
    }
    
    public boolean getOpenable()
    {
        return getBoolean(ID_OPENABLE);
    }
    
    public void setOpenable(boolean value)
    {
        setBoolean(ID_OPENABLE, value);
    }
    
    public boolean getOpen()
    {
        return getBoolean(ID_OPEN);
    }
    
    public void setOpen(boolean value)
    {
        setBoolean(ID_OPEN, value);
    }
}
