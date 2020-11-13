package jo.audio.loci.core.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.audio.loci.core.data.Verb;

public class VerbLogic
{
    private static final Map<String, Verb> mVerbs = new HashMap<>();
    static
    {
    }
    
    public static void registerVerbs(Verb... verbs)
    {
        for (Verb verb : verbs)
            mVerbs.put(verb.getID(), verb);
    }
    
    public static Verb get(String name)
    {
        Verb verb = mVerbs.get(name);
        if (verb == null)
            throw new IllegalArgumentException("Illegal verb name '"+name+"'");
        return verb;
    }
    
    public static void addVerb(List<Verb> verbs, String name)
    {
        Verb verb = get(name);
        if (!verbs.contains(verb))
            verbs.add(verb);
    }
}
