package jo.audio.loci.thieves.verbs.item;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociContainer;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.logic.TimeLogic;
import jo.audio.loci.thieves.verbs.VerbLookBase;

public class VerbClose extends VerbLookBase
{
    public VerbClose()
    {
        super("close,c", "$"+LociContainer.class, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociContainer container = (LociContainer)context.getMatchedDirectObject();
        if (!container.getOpen())
        {
            player.addMessage("The "+container.getPrimaryName()+" is already closed.");
            return;
        }
        else
        {
            container.setOpen(false);
            player.addMessage("You close the "+container.getPrimaryName()+".");
            if (container.getLockable() && container.getLocked())
                player.addMessage("The lock clicks shut.");
            TimeLogic.updateSilent(player, null);
            return;
        }
    }
}
