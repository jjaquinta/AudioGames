package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociThing;
import jo.util.utils.obj.StringUtils;

public class VerbSetHelp extends Verb
{
    public VerbSetHelp()
    {
        super("set help on,set help", "this", "to", ".*");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociThing thing = (LociThing)context.getMatchedDirectObject();
        String help = context.getIndirectObjectText();
        if (!StringUtils.equals(player.getURI(), thing.getOwner()))
        {
            player.addMessage("You do not own the "+thing.getName()+".");
            return;
        }
        thing.setHelpText(help);
        player.addMessage("Help is now set to '"+thing.getHelpText()+"'.");
    }
}
