package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.sandbox.data.LociPlayer;

public class VerbHelpDO extends VerbHelpBase
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
        doHelp(player, thing);
    }
}
