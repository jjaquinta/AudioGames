package jo.audio.loci.core.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.Verb;

public class VerbLogic
{
    private static final Map<String, List<Verb>> mVerbIndex = new HashMap<>();
    
    public static void registerVerbs(Class<? extends LociBase> dataProfile, Verb... verbs)
    {
        DataProfileLogic.registerDataProfile(dataProfile); // in case it hasn't
        String key = dataProfile.getSimpleName();
        List<Verb> vs = mVerbIndex.get(key);
        if (vs == null)
        {
            vs = new ArrayList<>();
            mVerbIndex.put(key, vs);
        }
        for (Verb v : verbs)
            vs.add(v);
    }
    
    public static List<Verb> getVerbs(Class<? extends LociBase> dataProfile)
    {
        if (dataProfile.equals(LociBase.class))
            return new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Verb> verbs = getVerbs((Class<? extends LociBase>)dataProfile.getSuperclass());
        String key = dataProfile.getSimpleName();
        List<Verb> vs = mVerbIndex.get(key);
        if (vs != null)
            verbs.addAll(vs);
        return verbs;
    }
    
    /*
    private static final Map<String, VerbProfile> mVerbProfiles = new HashMap<>();
    static
    {
        VerbProfileLogic.registerVerbProfile(new VerbProfileObject());
    }
    public static void registerVerbProfile(VerbProfile profile)
    {
        mVerbProfiles.put(profile.getName(), profile);
    }
    
    public static List<Verb> getVerbs(String profileName)
    {
        VerbProfile profile = mVerbProfiles.get(profileName);
        if (profile == null)
            throw new IllegalArgumentException("Illegal verb profile name '"+profileName+"'");
        List<Verb> verbs;
        if (!StringUtils.isTrivial(profile.getExtends()))
            verbs = getVerbs(profile.getExtends());
        else
            verbs = new ArrayList<Verb>();
        for (String verbName : profile.getVerbs())
            VerbLogic.addVerb(verbs, verbName);
        return verbs;
    }
    */
}
