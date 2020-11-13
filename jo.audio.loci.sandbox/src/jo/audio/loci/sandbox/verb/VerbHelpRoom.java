package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.sandbox.data.LociPlayer;

public class VerbHelpRoom extends VerbHelpBase
{
    public VerbHelpRoom()
    {
        super("help,\\?", null, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociObject thing = (LociObject)context.getMatchedVerbHost();
        doHelp(player, thing);
    }
}
