package jo.audio.loci.thieves.verbs.room;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.verbs.VerbLookBase;
import jo.audio.thieves.data.gen.Apature;

public class VerbUnlock extends VerbLookBase
{
    public VerbUnlock()
    {
        super("unlock", "$"+LociExit.class, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociObject thing = (LociObject)context.getMatchedDirectObject();
        if (thing instanceof LociExit)
        {
            LociExit exit = (LociExit)thing;
            Apature a = exit.getApatureObject();
            if ((a == null) || !a.isLockable())
            {
                player.addMessage("You can't unlock that.");
                return;
            }
            if (!exit.getLocked())
            {
                player.addMessage("The "+exit.getPrimaryName()+" is already unlocked.");
                return;
            }
            else
            {
                exit.setDoubleLocked(false);
                player.addMessage("You unlock the "+exit.getPrimaryName()+".");
                return;
            }
        }
        else
            player.addMessage("You can't unlock that.");
    }
}
