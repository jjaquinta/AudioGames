package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociThing;

public class VerbHelpDO extends Verb
{
    public VerbHelpDO()
    {
        super("help,\\?", "this", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociObject thing = (LociObject)context.getMatchedDirectObject();
        String msg = ((LociThing)thing).getHelpText();
        if (msg == null)
            msg = "Try 'help commands' for a list of commands.";
        player.addMessage(msg);
    }
}
