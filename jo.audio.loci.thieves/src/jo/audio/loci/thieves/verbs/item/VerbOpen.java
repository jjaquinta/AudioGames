package jo.audio.loci.thieves.verbs.item;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociContainer;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.verbs.VerbLookBase;

public class VerbOpen extends VerbLookBase
{
    public VerbOpen()
    {
        super("open,o", "$"+LociContainer.class, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociContainer container = (LociContainer)context.getMatchedDirectObject();
        if (container.getOpen())
        {
            player.addMessage("The "+container.getPrimaryName()+" is already open.");
            return;
        }
        else if (container.getLockable() && container.getLocked())
        {
            player.addMessage("The "+container.getPrimaryName()+" is locked.");
            return;
        }
        else
        {
            container.setOpen(true);
            player.addMessage("You open the "+container.getPrimaryName()+".");
            return;
        }
    }
}
