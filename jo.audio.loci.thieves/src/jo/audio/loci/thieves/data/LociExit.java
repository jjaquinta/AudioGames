package jo.audio.loci.thieves.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.stores.ExitStore;
import jo.audio.loci.thieves.stores.HouseStore;
import jo.audio.thieves.data.gen.Apature;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.logic.LocationLogic;
import jo.util.utils.obj.StringUtils;

public class LociExit extends LociThing
{
    public static final String PROFILE = LociExit.class.getSimpleName();
 
    public static final String ID_SOURCE = "source";
    public static final String ID_DESTINATION = "destination";
    public static final String ID_DIRECTION = "direction";
    public static final String ID_ELEVATION = "elevation";
    public static final String ID_APATURE = "apature";
    public static final String ID_OPEN = "open";
    public static final String ID_LOCKED = "locked";
    
    public LociExit(String uri)
    {
        super(uri, PROFILE);        
        init();
    }
    
    public LociExit(JSONObject json)
    {
        super(json);
        init();
    }

    private void init()
    {
        setVerbProfile("VerbProfileExit");
        Apature a = getApatureObject();
        if (a != null)
        {
            if (!mProperties.containsKey(ID_OPEN))
                setOpen(!a.isOpenable());
            if (!mProperties.containsKey(ID_LOCKED))
                setLocked(a.isLockable());
        }
        else
        {
            setOpen(true);
            setLocked(false);
        }
    }
    
    public Apature getApatureObject()
    {
        String from = getSource();
        if (StringUtils.isTrivial(from))
            return null;
        if (!from.startsWith(HouseStore.PREFIX))
            return null;
        String houseID = StringUtils.stripAfterLast(StringUtils.stripBeforeFirst(from, "://"), "/");
        House h = LocationLogic.getHouse(houseID, null);
        Apature a = h.getApatures().get(getApature());
        return a;
    }
    
    public LociLocality getSourceObject()
    {
        return (LociLocality)DataStoreLogic.load(getSource());
    }
    
    public LociLocality getDestinationObject()
    {
        return (LociLocality)DataStoreLogic.load(getDestination());
    }
    
    @Override
    public String[] getExtendedDescription(LociPlayer wrt)
    {
        List<String> desc = new ArrayList<String>();
        desc.add(getPrimaryName()+".");
        String description = getDescription();
        if (!StringUtils.isTrivial(description))
            desc.add(description);
        Apature a = getApatureObject();
        if (a != null)
        {
            if (a.isOpenable())
                if (a.isLockable())
                    desc.add("It is "+getOpenClosed()+" and "+getLockedUnlocked()+".");
                else
                    desc.add("It is "+getOpenClosed()+".");
        }
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
    
    public LociExit getOpposite()
    {
        String opURI = ExitStore.flipURI(getURI());
        LociExit op = (LociExit)DataStoreLogic.load(opURI);
        return op;
    }
    
    public void setDoubleOpen(boolean value)
    {
        setOpen(value);
        LociExit op = getOpposite();
        if (op != null)
            op.setOpen(value);
    }
    
    public void setDoubleLocked(boolean value)
    {
        setLocked(value);
        LociExit op = getOpposite();
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
    
    public int getElevation()
    {
        return getInt(ID_ELEVATION);
    }
    
    public void setElevation(int value)
    {
        setInt(ID_ELEVATION, value);
    }
    
    public String getApature()
    {
        return getString(ID_APATURE);
    }
    
    public void setApature(String value)
    {
        setString(ID_APATURE, value);
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
