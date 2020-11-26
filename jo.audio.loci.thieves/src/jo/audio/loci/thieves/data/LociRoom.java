package jo.audio.loci.thieves.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.utils.ResponseUtils;
import jo.audio.loci.thieves.stores.ExitStore;
import jo.audio.loci.thieves.stores.HouseStore;
import jo.audio.thieves.data.gen.Location;
import jo.audio.thieves.logic.LocationLogic;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.util.utils.obj.StringUtils;

public class LociRoom extends LociLocality
{
    public static final String PROFILE = "room";
    
    private Location mLocation;
    
    public LociRoom(String uri)
    {
        super(uri, PROFILE);        
    }
    
    public LociRoom(JSONObject json)
    {
        super(json);
        mLocation = LocationLogic.getLocation(getURI().substring(HouseStore.PREFIX.length()));
        init();
    }
    
    public LociRoom(JSONObject json, Location location)
    {
        super(json);
        mLocation = location;
        init();
        Set<String> contains = new HashSet<>();
        String[] cs = getContains();
        if (cs != null)
            for (String c : cs)
                if (!c.startsWith(ExitStore.PREFIX))
                    contains.add(c);
        for (int dir : ThievesConstLogic.ORTHOGONAL_DIRS)
        {
            String exit = mLocation.getApature(dir);
            if (!StringUtils.isTrivial(exit))
            {
                String euri;
                euri = ExitStore.toURI(getURI(), dir, exit);
                contains.add(euri);
            }
        }
        setContains(contains.toArray(new String[0]));
    }

    private void init()
    {
        setVerbProfile("VerbProfileRoom");
        mProperties.put(ID_NAME, mLocation.getName());
        if (StringUtils.isTrivial(mLocation.getDescription()))
            mProperties.put(ID_DECRIPTION, "");
        else
            mProperties.put(ID_DECRIPTION, mLocation.getDescription());
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
            {
                LociExit exit = (LociExit)o;
                if (exit.getDirection() >= 0)
                    exitNames.add("{{DIRECTION_NAME#"+exit.getDirection()+"}} to "+o.getPrimaryName());
                else
                    exitNames.add(o.getPrimaryName());
            }
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
            desc.add("You can go "+ResponseUtils.wordList(exitNames.toArray(), -1, "or ")+".");
        return desc.toArray(new String[0]);
    }

    public Location getLocation()
    {
        return mLocation;
    }

    public void setLocation(Location location)
    {
        mLocation = location;
    }

    // getters and setters
    
}
