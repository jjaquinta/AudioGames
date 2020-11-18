package jo.audio.loci.sandbox.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.DataStoreLogic;

public class LociThing extends LociObject
{
    public static final String PROFILE = "thing";
    
    public static final String ID_HELP_TEXT = "helpText";
    public static final String ID_OWNER = "owner";
    public static final String ID_PUBLIC = "public";

    public LociThing(String uri)
    {
        super(uri, PROFILE);
        setVerbProfile("VerbProfileThing");
    }
    
    public LociThing(String uri, String profile)
    {
        super(uri, profile);
        setVerbProfile("VerbProfileThing");
    }
    
    public LociThing(JSONObject json)
    {
        super(json);
        setVerbProfile("VerbProfileThing");
    }
    
    // utilities
    public String[] getExtendedDescription()
    {
        List<String> desc = new ArrayList<String>();
        desc.add(getName());
        desc.add(getDescription());
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
    
    public boolean getPublic()
    {
        return getBoolean(ID_PUBLIC);
    }
    
    public void setPublic(boolean value)
    {
        setBoolean(ID_PUBLIC, value);
    }
}
