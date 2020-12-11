package jo.audio.loci.thieves.verbs.item;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociTreasure;
import jo.audio.loci.thieves.data.npc.LociBuyer;

public class VerbSell extends Verb
{
    public VerbSell()
    {
        super("sell,fence,give", "$"+LociTreasure.class, "to", "this");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociTreasure item = (LociTreasure)context.getMatchedDirectObject();
        LociBuyer buyer = (LociBuyer)context.getMatchedIndirectObject();
        if (!item.getContainedBy().equals(player.getURI()))
        {
            player.addMessage("You are not carrying the "+item.getPrimaryName()+".");
            return;
        }
        if (!buyer.getType().equals(item.getType()))
        {
            player.addMessage(buyer.getPrimaryName()+" is not interested in "+item.getPrimaryName()+".");
            return;
        }
        ContainmentLogic.remove(player, item);
        player.addMessage("You sell "+item.getPrimaryName()+" to "+buyer.getPrimaryName()+".");
    }
}
