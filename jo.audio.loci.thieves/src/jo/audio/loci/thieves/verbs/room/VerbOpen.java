package jo.audio.loci.thieves.verbs.room;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.verbs.VerbLookBase;
import jo.audio.thieves.data.gen.Apature;

public class VerbOpen extends VerbLookBase
{
    public VerbOpen()
    {
        super("open,o", "$"+LociExit.class, null, null);
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
            if ((a == null) || !a.isOpenable())
            {
                player.addMessage("You can't open that.");
                return;
            }
            if (exit.getOpen())
            {
                player.addMessage("The "+exit.getPrimaryName()+" is already open.");
                return;
            }
            else if (exit.getLocked())
            {
                player.addMessage("The "+exit.getPrimaryName()+" is locked.");
                return;
            }
            else
            {
                exit.setDoubleOpen(true);
                player.addMessage("You open the "+exit.getPrimaryName()+".");
                return;
            }
        }
        else
            player.addMessage("You can't open that.");
    }
}
