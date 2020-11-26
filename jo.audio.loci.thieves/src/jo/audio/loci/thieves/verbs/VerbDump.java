package jo.audio.loci.thieves.verbs;

import org.json.simple.JSONUtils;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.thieves.data.LociPlayer;

public class VerbDump extends VerbLookBase
{
    public VerbDump()
    {
        super("dump,debug", "any", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociBase thing = context.getMatchedDirectObject();
        String json = JSONUtils.toFormattedString(thing.toJSON());
        player.addMessage(json.split("\n"));
    }
}
