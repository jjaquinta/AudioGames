package jo.audio.loci.thieves.verbs.move;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.loci.thieves.data.LociLocality;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociThing;
import jo.audio.loci.thieves.verbs.VerbLookBase;

public class VerbGoImplicit extends Verb
{
    public VerbGoImplicit()
    {
        super("this", null, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociExit exit = (LociExit)context.getMatchedVerbHost();
        LociLocality oldRoom = (LociLocality)DataStoreLogic.load(player.getContainedBy());
        LociLocality newRoom = (LociLocality)DataStoreLogic.load(exit.getDestination());
        VerbGoImplicit.transition(player, exit, oldRoom, newRoom);
    }

    public static void transition(LociPlayer player, LociThing exit, LociLocality oldRoom, LociLocality newRoom)
    {
        if ((exit != null) && !exit.isAccessible(player))
        {
            player.addMessage("You cannot go that way.");
            return;
        }
        oldRoom.say(player.getPrimaryName()+" leaves.", player.getURI(), null);
        ContainmentLogic.remove(oldRoom, player);
        ContainmentLogic.add(newRoom, player);
        newRoom.say(player.getPrimaryName()+" enters from "+oldRoom.getPrimaryName()+".", 
                player.getURI(), "You go to "+newRoom.getPrimaryName()+".");
        VerbLookBase.doLook(player, newRoom);
    }
}
