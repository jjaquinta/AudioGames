package jo.audio.loci.thieves.verbs.room;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.verbs.VerbLookBase;
import jo.audio.thieves.data.gen.Apature;

public class VerbLookThrough extends VerbLookBase
{
    public VerbLookThrough()
    {
        super("look,l", null, "through,at", "$"+LociExit.class);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociObject thing = (LociObject)context.getMatchedIndirectObject();
        if (thing instanceof LociExit)
        {
            LociExit exit = (LociExit)thing;
            if (exit.getOpen())
            {
                player.addMessage("You look through the open "+exit.getPrimaryName()+".");
                doLook(player, exit.getDestinationObject());
                return;
            }
            Apature a = exit.getApatureObject();
            if ((a != null) && a.isTransparent())
            {
                player.addMessage("You look through the "+exit.getPrimaryName()+".");
                doLook(player, exit.getDestinationObject());
                return;
            }
        }
        doLook(player, thing);
    }
}
