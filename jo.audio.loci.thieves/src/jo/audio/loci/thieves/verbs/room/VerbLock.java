package jo.audio.loci.thieves.verbs.room;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociApature;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.verbs.VerbLookBase;
import jo.audio.thieves.data.template.PApature;

public class VerbLock extends VerbLookBase
{
    public VerbLock()
    {
        super("lock", "$"+LociApature.class, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociApature Apature = (LociApature)context.getMatchedDirectObject();
        PApature a = Apature.getApatureObject();
        if ((a == null) || !a.getLockable())
        {
            player.addMessage("You can't lock that.");
            return;
        }
        if (Apature.getLocked())
        {
            player.addMessage("The "+Apature.getPrimaryName()+" is already locked.");
            return;
        }
        else
        {
            Apature.setDoubleLocked(true);
            player.addMessage("You lock the "+Apature.getPrimaryName()+".");
            return;
        }
    }
}
