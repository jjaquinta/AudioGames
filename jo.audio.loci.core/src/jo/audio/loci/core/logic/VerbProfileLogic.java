package jo.audio.loci.core.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.data.VerbProfile;
import jo.audio.loci.core.logic.vprofile.VerbProfileObject;
import jo.util.utils.obj.StringUtils;

public class VerbProfileLogic
{
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
}
