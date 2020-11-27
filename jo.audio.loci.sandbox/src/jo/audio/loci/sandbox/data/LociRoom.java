package jo.audio.loci.sandbox.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.utils.ResponseUtils;
import jo.audio.loci.sandbox.logic.InitializeLogic;

public class LociRoom extends LociThing
{
    public static final String PROFILE = LociRoom.class.getSimpleName();
    
    public LociRoom(String uri)
    {
        super(uri, PROFILE);        
        init();
    }
    
    public LociRoom(String uri, String profile)
    {
        super(uri, profile);        
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
    public String[] getExtendedDescription(LociPlayer wrt)
    {
        List<String> desc = new ArrayList<String>();
        desc.add(getPrimaryName());
        desc.add(getDescription());
        if (!InitializeLogic.FOYER_URI.equals(getURI()))
        {
            List<String> playerNames = new ArrayList<>();
            List<String> exitNames = new ArrayList<>();
            List<String> itemNames = new ArrayList<>();
            for (LociObject o : getContainsObjects())
                if (o instanceof LociPlayer)
                {
                    LociPlayer p = (LociPlayer)o;
                    if (p.getURI().equals(wrt.getURI()))
                        continue;
                    if (p.getOnline())
                    {
                        String name = o.getPrimaryName();
                        long lastActiveElapsed = System.currentTimeMillis() - p.getLastActive();
                        if (lastActiveElapsed > 60*1000L)
                            name += " (AFK)";
                        playerNames.add(name);
                    }
                }
                else if (o instanceof LociExit)
                    exitNames.add(o.getPrimaryName());
                else
                    itemNames.add(o.getPrimaryName());
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
        }
        return desc.toArray(new String[0]);
    }

    // getters and setters
    
}
