package jo.audio.loci.thieves.verbs.room;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociApature;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.logic.SkillLogic;
import jo.audio.loci.thieves.verbs.VerbLookBase;
import jo.audio.thieves.data.template.PApature;

public class VerbUnlock extends VerbLookBase
{
    public VerbUnlock()
    {
        super("unlock", "$"+LociApature.class, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociApature Apature = (LociApature)context.getMatchedDirectObject();
        PApature a = Apature.getApatureObject();
        if ((a == null) || !a.getLockable())
        {
            player.addMessage("You can't unlock that.");
            return;
        }
        if (!Apature.getLocked())
        {
            player.addMessage("The "+Apature.getPrimaryName()+" is already unlocked.");
            return;
        }
        else
        {
            if (SkillLogic.rollOpenLocks(player, a.getOpenLocksMod()))
            {
                Apature.setDoubleLocked(false);
                player.addMessage("You unlock the "+Apature.getPrimaryName()+".");
            }
            else
                player.addMessage("You fail to unlock the "+Apature.getPrimaryName()+".");
            return;
        }
    }
}
