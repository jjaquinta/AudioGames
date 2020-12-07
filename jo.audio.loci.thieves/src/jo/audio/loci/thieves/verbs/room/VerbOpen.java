package jo.audio.loci.thieves.verbs.room;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociApature;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.verbs.VerbLookBase;
import jo.audio.thieves.data.template.PApature;

public class VerbOpen extends VerbLookBase
{
    public VerbOpen()
    {
        super("open,o", "$"+LociApature.class, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociApature Apature = (LociApature)context.getMatchedDirectObject();
        PApature a = Apature.getApatureObject();
        if ((a == null) || !a.getOpenable())
        {
            player.addMessage("You can't open that.");
            return;
        }
        if (Apature.getOpen())
        {
            player.addMessage("The "+Apature.getPrimaryName()+" is already open.");
            return;
        }
        else if (Apature.getLocked())
        {
            player.addMessage("The "+Apature.getPrimaryName()+" is locked.");
            return;
        }
        else
        {
            Apature.setDoubleOpen(true);
            player.addMessage("You open the "+Apature.getPrimaryName()+".");
            return;
        }
    }
}
