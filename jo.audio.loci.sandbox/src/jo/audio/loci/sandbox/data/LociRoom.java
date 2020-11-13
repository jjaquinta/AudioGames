package jo.audio.loci.sandbox.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.utils.ResponseUtils;
import jo.audio.loci.sandbox.vprofile.VerbProfileRoom;

public class LociRoom extends LociThing
{
    public static final String PROFILE = "room";
    
    public static final String ID_EXITS = "exits";
       
    public LociRoom(String uri)
    {
        super(uri, PROFILE);        
        init();
    }
    
    public LociRoom(JSONObject json)
    {
        super(json);
        init();
    }

    private void init()
    {
        setVerbProfile(VerbProfileRoom.class);
    }
    
    @Override
    public String[] getExtendedDescription()
    {
        List<String> desc = new ArrayList<String>();
        desc.add(getName());
        desc.add(getDescription());
        List<String> itemNames = getContainsNames();
        if (itemNames.size() == 0)
            ;
        else if (itemNames.size() == 1)
            desc.add(itemNames.get(0)+" is here.");
        else
            desc.add(ResponseUtils.wordList(itemNames)+" are here.");
        return desc.toArray(new String[0]);
    }

    // getters and setters
    
    public String[] getExits()
    {
        return getStringArray(ID_EXITS);
    }
    
    public void setExits(String[] value)
    {
        setStringArray(ID_EXITS, value);
    }
}
