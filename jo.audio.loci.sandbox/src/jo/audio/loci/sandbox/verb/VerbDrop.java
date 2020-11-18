package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.sandbox.data.LociItem;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.util.utils.obj.StringUtils;

public class VerbDrop extends VerbLookBase
{
    public VerbDrop()
    {
        super("put down,drop", "any", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociObject thing = (LociObject)context.getMatchedDirectObject();
        if (!(thing instanceof LociItem))
        {
            player.addMessage("You cannot drop "+thing.getName()+".");
        }
        if (!StringUtils.equals(player.getURI(), thing.getContainedBy()))
        {
            player.addMessage("You are not carrying "+thing.getName()+".");
            return;
        }
        LociObject here = (LociObject)DataStoreLogic.load(player.getContainedBy());
        ContainmentLogic.remove(player, thing);
        ContainmentLogic.add(here, thing);
        player.addMessage("You dropped "+thing.getName()+".");
    }
}
