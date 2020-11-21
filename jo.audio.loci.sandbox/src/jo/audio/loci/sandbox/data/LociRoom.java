package jo.audio.loci.sandbox.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.utils.ResponseUtils;

public class LociRoom extends LociThing
{
    public static final String PROFILE = "room";
    
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
        setVerbProfile("VerbProfileRoom");
    }
    
    @Override
    public String[] getExtendedDescription()
    {
        List<String> desc = new ArrayList<String>();
        desc.add(getName());
        desc.add(getDescription());
        List<String> playerNames = new ArrayList<>();
        List<String> exitNames = new ArrayList<>();
        List<String> itemNames = new ArrayList<>();
        for (LociObject o : getContainsObjects())
            if (o instanceof LociPlayer)
            {
                LociPlayer p = (LociPlayer)o;
                if (p.getOnline())
                {
                    String name = o.getName();
                    long lastActiveElapsed = System.currentTimeMillis() - p.getLastActive();
                    if (lastActiveElapsed > 60*1000L)
                        name += " (AFK "+lastActiveElapsed+")";
                    playerNames.add(name);
                }
            }
            else if (o instanceof LociExit)
                exitNames.add(o.getName());
            else
                itemNames.add(o.getName());
        if (itemNames.size() > 0)
            if (itemNames.size() == 1)
                desc.add("There is "+itemNames.get(0)+" here.");
            else
                desc.add("There are "+ResponseUtils.wordList(itemNames)+" here.");
        if (playerNames.size() > 0)
            if (playerNames.size() == 1)
                desc.add(playerNames.get(0)+" is here.");
            else
                desc.add(ResponseUtils.wordList(playerNames)+" are here.");
        if (exitNames.size() > 0)
            if (exitNames.size() == 1)
                desc.add("There is an exit to the "+exitNames.get(0)+".");
            else
                desc.add("There are exits to the "+ResponseUtils.wordList(exitNames)+".");
        return desc.toArray(new String[0]);
    }

    // getters and setters
    
}
