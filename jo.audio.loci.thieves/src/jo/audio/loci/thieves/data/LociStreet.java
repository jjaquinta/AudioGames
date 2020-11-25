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
import jo.audio.thieves.logic.ThievesConstLogic;

public class LociStreet extends LociLocality
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
    
    public LociStreet(JSONObject json, Street street)
    {
        super(json);
        mStreet = street;
        init();
        String[] contains = new String[2];
        contains[0] = ExitStore.toURI(mStreet.getID(), mStreet.getHighIntersection().getID());
        contains[1] = ExitStore.toURI(mStreet.getID(), mStreet.getLowIntersection().getID());
        setContains(contains);
    }

    private void init()
    {
        setVerbProfile("VerbProfileStreet");
        mProperties.put(ID_NAME, mStreet.getName());
        if (mStreet.getType() == Street.STREET)
            mProperties.put(ID_DECRIPTION, mStreet.getHouses()+" houses line this street from "
                    +ThievesConstLogic.dirToName(mStreet.getHighDir())+" to "
                    +ThievesConstLogic.dirToName(mStreet.getLowDir())+".");
        else if (mStreet.getType() == Street.QUAY)
            mProperties.put(ID_DECRIPTION, mStreet.getHouses()+" warehouses stand next to the river from "
                    +ThievesConstLogic.dirToName(mStreet.getHighDir())+" to "
                    +ThievesConstLogic.dirToName(mStreet.getLowDir())+".");
        else if (mStreet.getType() == Street.BRIDGE)
            mProperties.put(ID_DECRIPTION, "This bridge crosses the river "
                    +LocationLogic.getCity().getRiverName()+" from "
                    +ThievesConstLogic.dirToName(mStreet.getHighDir())+" to "
                    +ThievesConstLogic.dirToName(mStreet.getLowDir())+".");
        else
            throw new IllegalArgumentException("Unknown street type: "+mStreet.getType());
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

    public Street getStreet()
    {
        return mStreet;
    }

    public void setStreet(Street street)
    {
        mStreet = street;
    }
    
}
