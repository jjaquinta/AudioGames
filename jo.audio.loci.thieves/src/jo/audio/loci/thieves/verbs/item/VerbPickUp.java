package jo.audio.loci.thieves.verbs.item;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociItem;
import jo.audio.loci.thieves.data.LociItemStackable;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.util.utils.obj.IntegerUtils;
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
        if ((item instanceof LociItemStackable) && (context.getDirectObjectMatcher() != null) && (context.getDirectObjectMatcher().groupCount() > 1))
        {
            LociItemStackable stack = (LociItemStackable)item; 
            int quantity = IntegerUtils.parseInt(context.getDirectObjectMatcher().group(2));
            if ((quantity > 0) && (quantity < stack.getCount()))
            {   // split
                String u = stack.getURI();
                int o = u.lastIndexOf('/');
                u = u.substring(0, o + 1) + System.currentTimeMillis();
                LociItemStackable split = new LociItemStackable(u);
                split.setName(stack.getName());
                split.setDescription(stack.getDescription());
                split.setClassification(stack.getClassification());
                split.setCount(quantity);
                split.setHelpText(stack.getHelpText());
                split.setOwner(stack.getOwner());
                split.setPublic(stack.getPublic());
                ContainmentLogic.add(player, split);
                stack.setCount(stack.getCount() - quantity);
                player.addMessage("You picked up "+split.getPrimaryName()+".");
                return;
            }
        }
        ContainmentLogic.remove(here, item);
        ContainmentLogic.add(player, item);
        player.addMessage("You picked up "+item.getPrimaryName()+".");
    }
}
