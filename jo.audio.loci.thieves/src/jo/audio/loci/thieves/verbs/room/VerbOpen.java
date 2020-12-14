package jo.audio.loci.thieves.verbs.room;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociApature;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.logic.TimeLogic;
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
        LociApature apature = (LociApature)context.getMatchedDirectObject();
        PApature a = apature.getApatureObject();
        if ((a == null) || !a.getOpenable())
        {
            player.addMessage("You can't open that.");
            return;
        }
        if (apature.getOpen())
        {
            player.addMessage("The "+apature.getPrimaryName()+" is already open.");
            return;
        }
        else if (apature.getLocked())
        {
            player.addMessage("The "+apature.getPrimaryName()+" is locked.");
            return;
        }
        else
        {
            apature.setDoubleOpen(true);
            player.addMessage("You open the "+apature.getPrimaryName()+".");
            TimeLogic.updateSilent(player, null);
            return;
        }
    }
}
