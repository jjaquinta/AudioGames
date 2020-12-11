package jo.audio.loci.thieves.verbs.item;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociContainer;
import jo.audio.loci.thieves.data.LociItem;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.util.utils.obj.StringUtils;

public class VerbPutIn extends Verb
{
    public VerbPutIn()
    {
        super("put", "$"+LociItem.class, "in,into", "this");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociItem item = (LociItem)context.getMatchedDirectObject();
        LociContainer container = (LociContainer)context.getMatchedIndirectObject();
        if (!item.isAccessible(player))
        {
            player.addMessage("You do not own the "+item.getPrimaryName()+".");
            return;
        }
//        if (!container.isAccessible(player))
//        {
//            player.addMessage("You do not own the "+container.getPrimaryName()+".");
//            return;
//        }
        if (!container.getOpen())
        {
            player.addMessage("The "+container.getPrimaryName()+" is not open.");
            return;
        }
        if (StringUtils.equals(item.getURI(), container.getURI()))
        {
            player.addMessage("You can't put something in itself.");
            return;
        }
        if (StringUtils.equals(item.getContainedBy(), container.getURI()))
        {
            player.addMessage("The "+item.getPrimaryName()+" is already in the "+container.getOwner()+".");
            return;
        }
        LociObject parent = (LociObject)DataStoreLogic.load(item.getContainedBy());
        ContainmentLogic.remove(parent, item);
        ContainmentLogic.add(container, item);
        player.addMessage("You put "+item.getPrimaryName()+" into "+container.getPrimaryName()+".");
    }
}
