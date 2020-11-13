package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.sandbox.data.LociPlayer;

public class VerbLookIO extends VerbLookBase
{
    public VerbLookIO()
    {
        super("look", null, "at", "this");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociObject thing = (LociObject)context.getMatchedIndirectObject();
        doLook(player, thing);
    }
}
