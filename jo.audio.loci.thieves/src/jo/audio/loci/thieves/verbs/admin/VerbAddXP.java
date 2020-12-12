package jo.audio.loci.thieves.verbs.admin;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.thieves.data.LociItemStackable;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.logic.PlayerLogic;
import jo.audio.loci.thieves.logic.PopulateSquareLogic;
import jo.audio.loci.thieves.verbs.VerbLookBase;
import jo.util.utils.obj.IntegerUtils;

public class VerbAddXP extends VerbLookBase
{
    public VerbAddXP()
    {
        super("add", "[0-9]*", "xp,gold", null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        int amnt = IntegerUtils.parseInt(context.getDirectObjectText());
        if ("xp".equalsIgnoreCase(context.getPrepositionText()))
            PlayerLogic.addXP(player, amnt);
        else if ("gold".equalsIgnoreCase(context.getPrepositionText()))
        {
            LociItemStackable gold = PopulateSquareLogic.makeBagOfCoins(PopulateSquareLogic.COIN_GOLD, amnt);
            ContainmentLogic.add(player, gold);
        }
    }
}
