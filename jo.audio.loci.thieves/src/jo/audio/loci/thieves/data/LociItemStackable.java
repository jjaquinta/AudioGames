package jo.audio.loci.thieves.data;

import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.IContainerAlertAdded;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;

public class LociItemStackable extends LociItem implements IContainerAlertAdded
{
    public static final String ID_COUNT = "count";
    public static final String ID_CLASSIFICATION = "classification";

    public LociItemStackable(String uri)
    {
        super(uri);
    }
    
    public LociItemStackable(JSONObject json)
    {
        super(json);
    }
    
    // utilities

    @Override
    public String getName()
    {
        String name = super.getName();
        int o = name.indexOf(NAME_DELIM);
        String primaryName;
        if (o > 0)
            primaryName = name.substring(0, o);
        else
            primaryName = name;
        name += NAME_DELIM + getCount() + " " + primaryName;
        name += NAME_DELIM + "([0-9]*) " + primaryName;
        return name;
    }
    
    @Override
    public String getPrimaryName()
    {
        return getCount()+" "+super.getPrimaryName();
    }
    
    @Override
    public void itemAdded(LociBase parent, LociBase child)
    {
        if (child != this)
            return;
        LociThing container = (LociThing)parent;
        List<LociItemStackable> contents = container.getContainsStuff(LociItemStackable.class);
        for (LociItemStackable item : contents)
            if (!item.getURI().equals(child.getURI()) && item.getClassification().equals(getClassification()))
            {   // merge
                item.setCount(item.getCount() + getCount());
                ContainmentLogic.remove(container, this);
                DataStoreLogic.delete(this);
                break;
            }
    }
    
    // getters and setters
    
    public String getClassification()
    {
        return getString(ID_CLASSIFICATION);
    }
    
    public void setClassification(String value)
    {
        setString(ID_CLASSIFICATION, value);
    }
    
    public int getCount()
    {
        return getInt(ID_COUNT);
    }
    
    public void setCount(int value)
    {
        setInt(ID_COUNT, value);
        mNamePattern = null;
    }
}
