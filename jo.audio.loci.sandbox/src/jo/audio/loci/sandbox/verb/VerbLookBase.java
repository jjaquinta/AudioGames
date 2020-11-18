package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociThing;

public abstract class VerbLookBase extends Verb
{
    public VerbLookBase(String verbText, String directObjectText,
            String prepositionText, String indirectObjectText)
    {
        super(verbText, directObjectText, prepositionText, indirectObjectText);
    }

    public static void doLook(LociPlayer player, LociObject thing)
    {
        if (thing instanceof LociThing)
            player.addMessage(((LociThing)thing).getExtendedDescription());
        else
            player.addMessage(thing.getDescription());
    }
}
