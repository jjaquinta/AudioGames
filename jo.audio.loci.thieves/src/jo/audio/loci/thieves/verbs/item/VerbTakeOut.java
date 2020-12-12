package jo.audio.loci.thieves.verbs.item;

import java.util.regex.Matcher;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociContainer;
import jo.audio.loci.thieves.data.LociItem;
import jo.audio.loci.thieves.data.LociItemStackable;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.util.utils.obj.IntegerUtils;

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
        if (!container.getOpen())
        {
            player.addMessage("The "+container.getPrimaryName()+" is not open.");
            return;
        }
        LociItem item = null;
        Matcher itemMatcher = null;
        for (String containedURI : container.getContains())
        {
            LociItem i = (LociItem)DataStoreLogic.load(containedURI);
            itemMatcher = i.getNamePattern().matcher(thingName);
            if (itemMatcher.matches())
            {
                item = i;
                break;
            }
        }
        if (item == null)
        {
            player.addMessage("There is no "+thingName+" in the "+container.getPrimaryName()+".");
            return;
        }
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
        if ((item instanceof LociItemStackable) && (itemMatcher != null) && (itemMatcher.groupCount() > 1))
        {
            LociItemStackable stack = (LociItemStackable)item; 
            int quantity = IntegerUtils.parseInt(itemMatcher.group(2));
            if (quantity > stack.getCount())
            {
                player.addMessage("You cannot take "+quantity+" of "+stack.getCount()+" items out of that.");
                return;
            }
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
                player.addMessage("You take "+split.getPrimaryName()+" out of "+container.getPrimaryName()+".");
                return;
            }
        }
        ContainmentLogic.remove(container, item);
        ContainmentLogic.add(player, item);
        player.addMessage("You take "+item.getPrimaryName()+" out of "+container.getPrimaryName()+".");
    }
}
