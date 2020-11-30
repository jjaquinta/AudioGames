package jo.audio.loci.thieves.verbs.item;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociItem;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.util.utils.obj.StringUtils;

public class VerbDrop extends Verb
{
    public VerbDrop()
    {
        super("put down,drop", "this", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociItem thing = (LociItem)context.getMatchedDirectObject();
        if (!StringUtils.equals(player.getURI(), thing.getContainedBy()))
        {
            player.addMessage("You are not carrying "+thing.getPrimaryName()+".");
            return;
        }
        LociObject here = (LociObject)DataStoreLogic.load(player.getContainedBy());
        ContainmentLogic.remove(player, thing);
        ContainmentLogic.add(here, thing);
        player.addMessage("You dropped "+thing.getPrimaryName()+".");
    }
}
