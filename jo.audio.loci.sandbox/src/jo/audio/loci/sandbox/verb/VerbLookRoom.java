package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.sandbox.data.LociPlayer;

public class VerbLookRoom extends VerbLookBase
{
    public VerbLookRoom()
    {
        super("look", null, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociObject thing = (LociObject)context.getMatchedVerbHost();
        doLook(player, thing);
    }
}
