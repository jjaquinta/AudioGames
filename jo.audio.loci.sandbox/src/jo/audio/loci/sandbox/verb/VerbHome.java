package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociRoom;
import jo.audio.loci.sandbox.logic.InitializeLogic;

public class VerbHome extends Verb
{
    public VerbHome()
    {
        super("home", null, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociRoom oldRoom = (LociRoom)DataStoreLogic.load(player.getContainedBy());
        LociRoom newRoom = (LociRoom)DataStoreLogic.load(InitializeLogic.ENTRANCE_URI);
        if (oldRoom != null)
            ContainmentLogic.remove(oldRoom, player);
        ContainmentLogic.add(newRoom, player);
        player.addMessage("There's no place like home.");
        VerbLookBase.doLook(player, newRoom);
    }
}
