package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.sandbox.data.LociExit;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociPlayerAdmin;
import jo.audio.loci.sandbox.data.LociRoom;
import jo.audio.loci.sandbox.data.LociThing;
import jo.audio.loci.sandbox.logic.InitializeLogic;

public class VerbDelete extends Verb
{
    public VerbDelete()
    {
        super("delete,del,yeet", "this", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociThing item = (LociThing)context.getMatchedDirectObject();
        if (!item.isAccessible(player))
        {
            player.addMessage("You cannot delete "+item.getName()+".");
            return;
        }
        if (item instanceof LociPlayer)
            if (!(player instanceof LociPlayerAdmin))
            {
                player.addMessage("You cannot delete another player.");
                return;
            }
        if (item instanceof LociRoom)
            if (!item.getOwner().equals(player.getURI()) && !(player instanceof LociPlayerAdmin))
            {
                player.addMessage("You cannot delete a room that isn't yours.");
                return;
            }
        if (item instanceof LociExit)
            if (!item.getOwner().equals(player.getURI()) && !(player instanceof LociPlayerAdmin))
            {
                player.addMessage("You cannot delete an exit that isn't yours.");
                return;
            }
        LociObject oldContainer = (LociObject)DataStoreLogic.load(item.getContainedBy());
        LociObject newContainer = (LociObject)DataStoreLogic.load(player.getContainedBy());
        if (newContainer.getURI().equals(item.getURI()))
            newContainer = (LociObject)DataStoreLogic.load(InitializeLogic.ENTRANCE_URI);
        // move contents
        String[] contains = item.getContains();
        if (contains != null)
            for (String containedURI : item.getContains())
            {
                LociObject containedItem = (LociObject)DataStoreLogic.load(containedURI);
                ContainmentLogic.remove(item, containedItem);
                ContainmentLogic.add(newContainer, containedItem);
            }
        // remove item
        if (oldContainer != null)
            ContainmentLogic.remove(oldContainer, item);
        player.addMessage("You deleted the "+item.getName()+".");
    }
}
