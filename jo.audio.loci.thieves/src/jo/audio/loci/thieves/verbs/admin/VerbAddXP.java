package jo.audio.loci.thieves.verbs.admin;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.logic.PlayerLogic;
import jo.audio.loci.thieves.verbs.VerbLookBase;
import jo.util.utils.obj.IntegerUtils;

public class VerbAddXP extends VerbLookBase
{
    public VerbAddXP()
    {
        super("add xp,xp", "[0-9]*", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        int xp = IntegerUtils.parseInt(context.getDirectObjectText());
        PlayerLogic.addXP(player, xp);
    }
}
