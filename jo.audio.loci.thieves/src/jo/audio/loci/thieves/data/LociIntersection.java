package jo.audio.loci.thieves.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.utils.ResponseUtils;
import jo.audio.loci.thieves.stores.ExitStore;
import jo.audio.loci.thieves.stores.SquareStore;
import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.logic.LocationLogic;
import jo.util.utils.obj.StringUtils;

public class LociIntersection extends LociLocality
{
    private Intersection    mIntersection;
    private List<String>    mStreetNames;
    
    public LociIntersection(String uri)
    {
        super(uri);
    }
    
    public LociIntersection(JSONObject json)
    {
        super(json);
        mIntersection = LocationLogic.getIntersection(getURI().substring(SquareStore.PREFIX.length()));
        init();
    }
    
    public LociIntersection(JSONObject json, Intersection intersection)
    {
        super(json);
        mIntersection = intersection;
        init();
        String[] contains = new String[mIntersection.getStreets().size()];
        for (int i = 0; i < contains.length; i++)
            contains[i] = ExitStore.toURI(mIntersection, mIntersection.getStreets().get(i));
        setContains(contains);
    }

    private void init()
    {
        mProperties.put(ID_NAME, mIntersection.getName());
        if (StringUtils.isTrivial(mIntersection.getDescription()))
            mProperties.put(ID_DECRIPTION, "");
        else
            mProperties.put(ID_DECRIPTION, mIntersection.getDescription());
    }
    
    @Override
    public String[] getExtendedDescription(LociPlayer wrt)
    {
        List<String> desc = new ArrayList<String>();
        desc.add(getPrimaryName()+".");
        String description = getDescription();
        if (!StringUtils.isTrivial(description))
            desc.add(description);
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
                desc.add(exitNames.get(0)+" leads away from here.");
            else
                desc.add(ResponseUtils.wordList(exitNames)+" come together here.");
        return desc.toArray(new String[0]);
    }
    
    public List<String> getStreetNames()
    {
        return mStreetNames;
    }

    // getters and setters

    public Intersection getIntersection()
    {
        return mIntersection;
    }

    public void setIntersection(Intersection intersection)
    {
        mIntersection = intersection;
    }
    
}
