package jo.audio.loci.thieves.verbs.room;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.thieves.data.LociApature;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.verbs.VerbLookBase;
import jo.audio.thieves.data.template.PApature;

public class VerbLookThrough extends VerbLookBase
{
    public VerbLookThrough()
    {
        super("look,l", null, "through,at", "$"+LociApature.class);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociObject thing = (LociObject)context.getMatchedIndirectObject();
        if (thing instanceof LociApature)
        {
            LociApature Apature = (LociApature)thing;
            if (Apature.getOpen())
            {
                player.addMessage("You look through the open "+Apature.getPrimaryName()+".");
                doLook(player, Apature.getDestinationObject());
                return;
            }
            PApature a = Apature.getApatureObject();
            if ((a != null) && a.getTransparent())
            {
                player.addMessage("You look through the "+Apature.getPrimaryName()+".");
                doLook(player, Apature.getDestinationObject());
                return;
            }
        }
        doLook(player, thing);
    }
}
