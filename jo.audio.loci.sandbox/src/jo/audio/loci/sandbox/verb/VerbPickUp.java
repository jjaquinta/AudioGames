package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.sandbox.data.LociItem;
import jo.audio.loci.sandbox.data.LociPlayer;
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
        if (!StringUtils.equals(player.getURI(), item.getOwner()))
        {
            player.addMessage("You do not own the "+item.getName()+".");
            return;
        }
        if (!StringUtils.equals(player.getContainedBy(), item.getContainedBy()))
        {
            player.addMessage(item.getName()+" is not in a position to be picked up.");
            return;
        }
        LociObject here = (LociObject)DataStoreLogic.load(player.getContainedBy());
        ContainmentLogic.remove(here, item);
        ContainmentLogic.add(player, item);
        player.addMessage("You are now carrying "+item.getName()+".");
    }
}
