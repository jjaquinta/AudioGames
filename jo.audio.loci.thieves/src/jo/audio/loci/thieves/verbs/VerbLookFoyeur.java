package jo.audio.loci.thieves.verbs;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.thieves.data.LociPlayer;

public class VerbLookFoyeur extends VerbLookBase
{
    public VerbLookFoyeur()
    {
        super("look,l", null, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociObject thing = (LociObject)context.getMatchedVerbHost();
        doLook(player, thing);
    }
}
