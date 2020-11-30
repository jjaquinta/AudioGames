package jo.audio.loci.thieves.verbs.item;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociItem;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.util.utils.obj.StringUtils;

public class VerbPickUp extends Verb
{
    public VerbPickUp()
    {
        super("pick up,get,take", "this", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociItem item = (LociItem)context.getMatchedDirectObject();
        if (!item.isAccessible(player))
        {
            player.addMessage("You do not own the "+item.getPrimaryName()+".");
            return;
        }
        if (!StringUtils.equals(player.getContainedBy(), item.getContainedBy()))
        {
            player.addMessage(item.getPrimaryName()+" is not in a position to be picked up.");
            return;
        }
        LociObject here = (LociObject)DataStoreLogic.load(player.getContainedBy());
        ContainmentLogic.remove(here, item);
        ContainmentLogic.add(player, item);
        player.addMessage("You are now carrying "+item.getPrimaryName()+".");
    }
}
