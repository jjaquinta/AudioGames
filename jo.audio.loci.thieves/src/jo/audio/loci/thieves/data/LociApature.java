package jo.audio.loci.thieves.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.stores.ApatureStore;
import jo.audio.thieves.data.template.PApature;
import jo.util.utils.obj.StringUtils;

public class LociApature extends LociThing
{
    public static final String ID_SOURCE = "source";
    public static final String ID_DESTINATION = "destination";
    public static final String ID_DIRECTION = "direction";
    public static final String ID_OPEN = "open";
    public static final String ID_LOCKED = "locked";
    
    private ApatureStore.ApatureURI    mURI;
    
    public LociApature(String uri)
    {
        super(uri);        
        init();
    }
    
    public LociApature(JSONObject json)
    {
        super(json);
        init();
    }

    private void init()
    {
        String u = getURI();
        mURI = ((ApatureStore)DataStoreLogic.getStore(u)).new ApatureURI(u);
        PApature a = mURI.getThis();
        if (!mProperties.containsKey(ID_OPEN))
            setOpen(!a.getOpenable());
        if (!mProperties.containsKey(ID_LOCKED))
            setLocked(a.getLockable());
    }
    
    public PApature getApatureObject()
    {
        return mURI.getThis();
    }
    
    public LociSquare getSourceObject()
    {
        return (LociSquare)DataStoreLogic.load(getSource());
    }
    
    public LociSquare getDestinationObject()
    {
        return (LociSquare)DataStoreLogic.load(getDestination());
    }
    
    @Override
    public String[] getExtendedDescription(LociPlayer wrt)
    {
        List<String> desc = new ArrayList<String>();
        desc.add(getPrimaryName()+".");
        String description = getDescription();
        if (!StringUtils.isTrivial(description))
            desc.add(description);
        PApature a = getApatureObject();
        if (a.getOpenable())
            if (a.getLockable())
                desc.add("It is "+getOpenClosed()+" and "+getLockedUnlocked()+".");
            else
                desc.add("It is "+getOpenClosed()+".");
        return desc.toArray(new String[0]);
    }
    
    public String getOpenClosed()
    {
        if (getOpen())
            return "open";
        else
            return "closed";
    }
    
    public String getLockedUnlocked()
    {
        if (getLocked())
            return "locked";
        else
            return "unlocked";
    }
    
    public LociApature getOpposite()
    {
        String opURI = ApatureStore.flipURI(getURI());
        LociApature op = (LociApature)DataStoreLogic.load(opURI);
        return op;
    }
    
    public void setDoubleOpen(boolean value)
    {
        setOpen(value);
        LociApature op = getOpposite();
        if (op != null)
            op.setOpen(value);
    }
    
    public void setDoubleLocked(boolean value)
    {
        setLocked(value);
        LociApature op = getOpposite();
        if (op != null)
            op.setLocked(value);
    }

    // getters and setters
    
    public String getDestination()
    {
        return getString(ID_DESTINATION);
    }
    
    public void setDestination(String value)
    {
        setString(ID_DESTINATION, value);
    }
    
    public String getSource()
    {
        return getString(ID_SOURCE);
    }
    
    public void setSource(String value)
    {
        setString(ID_SOURCE, value);
    }
    
    public int getDirection()
    {
        return getInt(ID_DIRECTION);
    }
    
    public void setDirection(int value)
    {
        setInt(ID_DIRECTION, value);
    }
    
    public boolean getOpen()
    {
        return getBoolean(ID_OPEN);
    }
    
    public void setLocked(boolean value)
    {
        setBoolean(ID_LOCKED, value);
    }
    
    public boolean getLocked()
    {
        return getBoolean(ID_LOCKED);
    }
    
    public void setOpen(boolean value)
    {
        setBoolean(ID_OPEN, value);
    }
}
