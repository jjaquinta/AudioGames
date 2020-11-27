package jo.audio.loci.core.logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;

public class DataProfileLogic
{
    private static final Map<String, Class<? extends LociBase>> mDataProfiles = new HashMap<>();
    static
    {
        registerDataProfile(LociObject.class);
    }
    
    public static void registerDataProfile(Class<? extends LociBase> archetype)
    {
        try
        {
            String name = archetype.getSimpleName();
            mDataProfiles.put(name, archetype);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static LociBase instantiate(JSONObject json)
    {
        String profileName = json.getString(LociBase.ID_DATA_PROFILE);
        Class<? extends LociBase> archetype = mDataProfiles.get(profileName);
        if (archetype == null)
            throw new IllegalArgumentException("Illegal data profile name '"+profileName+"'");
        LociBase copy = null;
        try
        {
            Constructor<? extends LociBase> constructor = archetype.getConstructor(JSONObject.class);
            if (constructor == null)
                throw new IllegalStateException("Cannot construct "+archetype);
            copy = (LociBase)constructor.newInstance(json);
        }
        catch (InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e)
        {
            throw new IllegalStateException(e);
        }
        return copy;
    }

    public static Class<? extends LociBase> getArchetype(String name)
    {
        return mDataProfiles.get(name);
    }
}
