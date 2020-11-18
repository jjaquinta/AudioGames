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

public class VerbTakeOut extends Verb
{
    public VerbTakeOut()
    {
        super("take,remove", ".*", "out,out of,from", "this");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociContainer container = (LociContainer)context.getMatchedIndirectObject();
        String thingName = context.getDirectObjectText();
        LociItem item = null;
        for (String containedURI : container.getContains())
        {
            LociItem i = (LociItem)DataStoreLogic.load(containedURI);
            if (i.getName().equalsIgnoreCase(thingName))
            {
                item = i;
                break;
            }
        }
        if (item == null)
        {
            player.addMessage("There is no "+thingName+" in the "+container.getName()+".");
            return;
        }
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
        if (!StringUtils.equals(item.getContainedBy(), container.getURI()))
        {
            player.addMessage("The "+item.getName()+" is not in the "+container.getOwner()+".");
            return;
        }
        LociObject parent = (LociObject)DataStoreLogic.load(container.getContainedBy());
        ContainmentLogic.remove(container, item);
        ContainmentLogic.add(parent, item);
        player.addMessage("You take "+item.getName()+" out of "+container.getName()+".");
    }
}
