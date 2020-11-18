package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.sandbox.data.LociItem;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociThing;
import jo.util.utils.obj.StringUtils;

public class VerbPickUp extends Verb
{
    public VerbPickUp()
    {
        super("pick up,get,take", "any", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociObject obj = (LociObject)context.getMatchedDirectObject();
        if (!(obj instanceof LociItem))
        {
            player.addMessage("You cannot pick up "+obj.getName()+".");
            return;
        }
        LociThing thing = (LociThing)obj;
        if (!StringUtils.equals(player.getURI(), thing.getOwner()))
        {
            player.addMessage("You do not own the "+thing.getName()+".");
            return;
        }
        if (!StringUtils.equals(player.getContainedBy(), obj.getContainedBy()))
        {
            player.addMessage(obj.getName()+" is not in a position to be picked up.");
            return;
        }
        LociObject here = (LociObject)DataStoreLogic.load(player.getContainedBy());
        ContainmentLogic.remove(here, obj);
        ContainmentLogic.add(player, obj);
        player.addMessage("You are now carrying "+obj.getName()+".");
    }
}
