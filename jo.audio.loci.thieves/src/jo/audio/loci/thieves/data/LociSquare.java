package jo.audio.loci.thieves.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.utils.ResponseUtils;
import jo.audio.loci.thieves.stores.ApatureStore;
import jo.audio.loci.thieves.stores.ExitStore;
import jo.audio.loci.thieves.stores.SquareStore;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.audio.thieves.slu.ThievesModelConst;
import jo.util.utils.obj.StringUtils;

public class LociSquare extends LociLocality
{
    private SquareStore.SquareURI mURI;
    
    public LociSquare(String uri)
    {
        super(uri);        
    }
    
    public LociSquare(JSONObject json)
    {
        super(json);
        init();
    }
    
    private void init()
    {
        String u = getURI();
        mURI = ((SquareStore)DataStoreLogic.getStore(SquareStore.PREFIX)).new SquareURI(u);
        PSquare sq = mURI.getThis();
        mProperties.put(ID_NAME, sq.getName());
        if (StringUtils.isTrivial(sq.getDescription()))
            mProperties.put(ID_DECRIPTION, "");
        else
            mProperties.put(ID_DECRIPTION, sq.getDescription());
        Set<String> contains = new HashSet<>();
        String[] cs = getContains();
        if (cs != null)
            for (String c : cs)
                if (!c.startsWith(ExitStore.PREFIX) && !c.startsWith(ApatureStore.PREFIX))
                    contains.add(c);
        for (int dir : ThievesConstLogic.ORTHOGONAL_DIRS)
        {
            PLocationRef exit = mURI.getApatureRef(dir);
            if (exit != null)
            {
                if ("EXIT".equals(exit.getID()))
                {
                    String streetID = StringUtils.stripAfterLast(u.substring(SquareStore.PREFIX.length()), ":");
                    String sURI = ExitStore.PREFIX+streetID+"/"+streetID+"/"+dir;
                    contains.add(sURI);
                }
                else
                {    
                    ApatureStore.ApatureURI aURI = ((ApatureStore)DataStoreLogic.getStore(ApatureStore.PREFIX)).new ApatureURI(
                            mURI.mStreet, mURI.mHouseNum, exit, dir);
                    contains.add(aURI.toURI());
                }
            }
        }
        setContains(contains.toArray(new String[0]));
    }
    
    @Override
    public String[] getExtendedDescription(LociPlayer wrt)
    {
        List<String> desc = new ArrayList<String>();
        desc.add(getPrimaryName()+".");
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
            else if (o instanceof LociApature)
            {
                LociApature exit = (LociApature)o;
                String name = ThievesModelConst.expand(exit.getPrimaryName());
                String dirName = ThievesModelConst.expand("{{DIRECTION_NAME#"+exit.getDirection()+"}}");
                if (dirName.equals(name))
                    name = exit.getDestinationObject().getName();
                if (StringUtils.isTrivial(name) || dirName.equals(name))
                    exitNames.add(dirName);
                else
                    exitNames.add(dirName+" to "+name);
            }
            else if (o instanceof LociExit)
            {
                LociExit exit = (LociExit)o;
                int dir = exit.getDirection();
                exitNames.add("{{DIRECTION_NAME#"+dir+"}} to "+o.getPrimaryName());
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

    // getters and setters
    
}
