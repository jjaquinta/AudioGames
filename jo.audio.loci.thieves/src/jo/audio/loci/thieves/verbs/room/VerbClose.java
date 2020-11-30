package jo.audio.loci.thieves.verbs.room;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.verbs.VerbLookBase;
import jo.audio.thieves.data.gen.Apature;

public class VerbClose extends VerbLookBase
{
    public VerbClose()
    {
        super("close,c", "$"+LociExit.class, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociExit exit = (LociExit)context.getMatchedDirectObject();
        Apature a = exit.getApatureObject();
        if ((a == null) || !a.isOpenable())
        {
            player.addMessage("You can't close "+exit.getPrimaryName()+".");
            return;
        }
        if (!exit.getOpen())
        {
            player.addMessage("The "+exit.getPrimaryName()+" is already closed.");
            return;
        }
        else
        {
            exit.setDoubleOpen(false);
            player.addMessage("You close the "+exit.getPrimaryName()+".");
            if (exit.getLocked())
                player.addMessage("The lock clicks shut.");
            return;
        }
    }
}
