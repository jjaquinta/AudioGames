package jo.audio.loci.thieves.verbs;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.logic.PlayerLogic;
import jo.audio.loci.thieves.verbs.misc.VerbMoon;
import jo.audio.loci.thieves.verbs.misc.VerbTime;

public class VerbHelp extends VerbLookBase
{
    public VerbHelp()
    {
        super("help,what is my,about", ".*", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        String txt = context.getDirectObjectText().toLowerCase();
        switch (txt)
        {
            case "level":
                player.addMessage("You are "+player.getLevel()+" level.");
                break;
            case "standing":
            case "rank":
                player.addMessage("You have a standing of "+PlayerLogic.getGuildStandingDescription(player)+" in the guild.");
                break;
            case "time":
                VerbTime.doTime(player);
                break;
            case "moon":
                VerbMoon.doMoon(player);
                break;
            default:
                player.addMessage("I'm not sure how to help with '"+txt+"'.");
                break;
        }
    }
}
