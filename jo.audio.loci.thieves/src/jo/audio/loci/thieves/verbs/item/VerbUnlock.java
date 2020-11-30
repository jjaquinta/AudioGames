package jo.audio.loci.thieves.verbs.item;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociContainer;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.verbs.VerbLookBase;

public class VerbUnlock extends VerbLookBase
{
    public VerbUnlock()
    {
        super("unlock", "$"+LociContainer.class, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociContainer container = (LociContainer)context.getMatchedDirectObject();
        if (!container.getLockable())
        {
            player.addMessage("You can't unlock that.");
            return;
        }
        if (!container.getLocked())
        {
            player.addMessage("The "+container.getPrimaryName()+" is already unlocked.");
            return;
        }
        else
        {
            container.setLocked(false);
            player.addMessage("You unlock the "+container.getPrimaryName()+".");
            return;
        }
    }
}
