package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.sandbox.data.LociExit;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociRoom;

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
        LociRoom oldRoom = (LociRoom)DataStoreLogic.load(player.getContainedBy());
        LociRoom newRoom = (LociRoom)DataStoreLogic.load(exit.getDestination());
        if (!exit.isAccessible(player))
        {
            player.addMessage("You cannot go that way.");
            return;
        }
        ContainmentLogic.remove(oldRoom, player);
        ContainmentLogic.add(newRoom, player);
        player.addMessage("You go "+exit.getName());
        VerbLookBase.doLook(player, newRoom);
    }
}
