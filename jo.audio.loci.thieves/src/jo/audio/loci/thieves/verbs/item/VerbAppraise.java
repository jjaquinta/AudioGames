package jo.audio.loci.thieves.verbs.item;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociTreasure;
import jo.audio.loci.thieves.data.npc.LociBuyer;

public class VerbAppraise extends Verb
{
    public VerbAppraise()
    {
        super("ask", "this", "to appraise,about", "$"+LociTreasure.class);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociTreasure item = (LociTreasure)context.getMatchedIndirectObject();
        LociBuyer buyer = (LociBuyer)context.getMatchedDirectObject();
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
        int worth = buyer.calculateWorth(player, item);
        player.addMessage(buyer.getPrimaryName()+" would give you "+worth+" gold for "+item.getPrimaryName()+".");
    }
}
