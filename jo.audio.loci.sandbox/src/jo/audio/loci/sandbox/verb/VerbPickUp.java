package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.util.utils.obj.StringUtils;

public class VerbPickUp extends VerbLookBase
{
    public VerbPickUp()
    {
        super("pick up,get,take", "any", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociObject thing = (LociObject)context.getMatchedDirectObject();
        if (!StringUtils.equals(player.getContainedBy(), thing.getContainedBy()))
        {
            player.addMessage(thing.getName()+" is not in a position to be picked up.");
            return;
        }
        LociObject here = (LociObject)DataStoreLogic.load(player.getContainedBy());
        ContainmentLogic.remove(here, thing);
        ContainmentLogic.add(player, thing);
        player.addMessage("You are now carrying "+thing.getName()+".");
    }
}
