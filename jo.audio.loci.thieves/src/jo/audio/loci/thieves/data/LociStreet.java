package jo.audio.loci.thieves.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.utils.ResponseUtils;
import jo.audio.loci.thieves.stores.ExitStore;
import jo.audio.loci.thieves.stores.StreetStore;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.logic.LocationLogic;
import jo.audio.thieves.slu.ThievesModelConst;

public class LociStreet extends LociThing
{
    public static final String PROFILE = "street";
    
    private Street    mStreet;
    
    public LociStreet(String uri)
    {
        super(uri, PROFILE);
    }
    
    public LociStreet(JSONObject json)
    {
        super(json);
        mStreet = LocationLogic.getStreet(getURI().substring(StreetStore.PREFIX.length()));
        init();
    }
    
    public LociStreet(JSONObject json, Street intersection)
    {
        super(json);
        mStreet = intersection;
        init();
        String[] contains = new String[2];
        contains[0] = ExitStore.toURI(mStreet.getID(), mStreet.getHighIntersection().getID());
        contains[1] = ExitStore.toURI(mStreet.getID(), mStreet.getLowIntersection().getID());
        setContains(contains);
    }

    private void init()
    {
        setVerbProfile("VerbProfileStreet");
        mProperties.put(ID_NAME, ThievesModelConst.expand(mStreet.getName()));
        mProperties.put(ID_DECRIPTION, "");
    }
    
    @Override
    public String[] getExtendedDescription(LociPlayer wrt)
    {
        List<String> desc = new ArrayList<String>();
        desc.add(getPrimaryName());
        desc.add(getDescription());
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
        return desc.toArray(new String[0]);
    }

    // getters and setters
    
}
