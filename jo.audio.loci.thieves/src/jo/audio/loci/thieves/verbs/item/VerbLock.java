package jo.audio.loci.thieves.verbs.item;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociContainer;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.logic.SkillLogic;
import jo.audio.loci.thieves.verbs.VerbLookBase;

public class VerbLock extends VerbLookBase
{
    public VerbLock()
    {
        super("lock", "$"+LociContainer.class, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociContainer container = (LociContainer)context.getMatchedDirectObject();
        if (!container.getLockable())
        {
            player.addMessage("You can't lock that.");
            return;
        }
        if (container.getLocked())
        {
            player.addMessage("The "+container.getPrimaryName()+" is already locked.");
            return;
        }
        else
        {
            if (SkillLogic.rollOpenLocks(player, container.getOpenLocksMod()))
            {
                container.setLocked(true);
                player.addMessage("You lock the "+container.getPrimaryName()+".");
            }
            else
                player.addMessage("You fail to lock the "+container.getPrimaryName()+".");
            return;
        }
    }
}
