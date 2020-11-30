package jo.audio.loci.thieves.verbs.room;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.verbs.VerbLookBase;
import jo.audio.thieves.data.gen.Apature;

public class VerbLock extends VerbLookBase
{
    public VerbLock()
    {
        super("lock", "$"+LociExit.class, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociExit exit = (LociExit)context.getMatchedDirectObject();
        Apature a = exit.getApatureObject();
        if ((a == null) || !a.isLockable())
        {
            player.addMessage("You can't lock that.");
            return;
        }
        if (exit.getLocked())
        {
            player.addMessage("The "+exit.getPrimaryName()+" is already locked.");
            return;
        }
        else
        {
            exit.setDoubleLocked(true);
            player.addMessage("You lock the "+exit.getPrimaryName()+".");
            return;
        }
    }
}
