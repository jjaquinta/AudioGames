package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.sandbox.data.LociContainer;
import jo.audio.loci.sandbox.data.LociItem;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.util.utils.obj.StringUtils;

public class VerbPutIn extends Verb
{
    public VerbPutIn()
    {
        super("put", "any", "in,into", "this");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociObject thing = (LociObject)context.getMatchedDirectObject();
        LociContainer container = (LociContainer)context.getMatchedIndirectObject();
        if (!(thing instanceof LociItem))
        {
            player.addMessage("That is not something you can move.");
            return;
        }
        LociItem item = (LociItem)thing;
        if (!StringUtils.equals(player.getURI(), item.getOwner()))
        {
            player.addMessage("You do not own the "+item.getName()+".");
            return;
        }
        if (!StringUtils.equals(player.getURI(), container.getOwner()))
        {
            player.addMessage("You do not own the "+container.getOwner()+".");
            return;
        }
        if (!container.getOpen())
        {
            player.addMessage("The "+container.getName()+" is not open.");
            return;
        }
        if (StringUtils.equals(thing.getURI(), container.getURI()))
        {
            player.addMessage("You can't put something in itself.");
            return;
        }
        if (StringUtils.equals(thing.getContainedBy(), container.getURI()))
        {
            player.addMessage("The "+thing.getName()+" is already in the "+container.getOwner()+".");
            return;
        }
        LociObject parent = (LociObject)DataStoreLogic.load(item.getContainedBy());
        ContainmentLogic.remove(parent, item);
        ContainmentLogic.add(container, item);
        player.addMessage("You put "+item.getName()+" into "+container.getName()+".");
    }
}
