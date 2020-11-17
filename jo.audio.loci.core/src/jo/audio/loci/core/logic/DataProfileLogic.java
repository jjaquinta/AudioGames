package jo.audio.loci.core.logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.stores.NullStore;

public class DataProfileLogic
{
    private static final Map<String, Class<? extends LociBase>> mDataProfiles = new HashMap<>();
    static
    {
        registerDataProfile("object", LociObject.class);
    }
    
    public static void registerDataProfile(String name, Class<? extends LociBase> archetype)
    {
        mDataProfiles.put(name, archetype);
    }
    
    public static void registerDataProfile(Class<? extends LociBase> archetype)
    {
        try
        {
            LociBase base = archetype.getConstructor(String.class).newInstance(NullStore.PREFIX);
            mDataProfiles.put(base.getDataProfile(), archetype);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static LociBase cast(LociBase ori)
    {
        String profileName = ori.getDataProfile();
        Class<? extends LociBase> archetype = mDataProfiles.get(profileName);
        if (archetype == null)
            throw new IllegalArgumentException("Illegal data profile name '"+profileName+"'");
        LociBase copy = null;
        try
        {
            Constructor<? extends LociBase> constructor = archetype.getConstructor(JSONObject.class);
            if (constructor == null)
                throw new IllegalStateException("Cannot construct "+archetype);
            copy = (LociBase)constructor.newInstance(ori.toJSON());
        }
        catch (InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e)
        {
            throw new IllegalStateException(e);
        }
        copy.fromJSON(ori.toJSON());
        return copy;
    }
}
