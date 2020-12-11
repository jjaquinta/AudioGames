package jo.audio.loci.thieves.verbs.item;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.thieves.data.LociItemStackable;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociTreasure;
import jo.audio.loci.thieves.data.npc.LociBuyer;
import jo.audio.loci.thieves.logic.PopulateSquareLogic;

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
        int worth = buyer.calculateWorth(player, item);
        if (worth == 0)
        {
            player.addMessage("Your "+item.getPrimaryName()+" is not worth anything to "+buyer.getPrimaryName()+".");
            return;
        }
        ContainmentLogic.remove(player, item);
        LociItemStackable coins = PopulateSquareLogic.makeBagOfCoins(PopulateSquareLogic.COIN_GOLD, worth);
        ContainmentLogic.add(player, coins);
        player.addMessage("You sell "+item.getPrimaryName()+" to "+buyer.getPrimaryName()+" for "+worth+".");
        buyer.registerPurchase(player, item, worth);
    }
}
