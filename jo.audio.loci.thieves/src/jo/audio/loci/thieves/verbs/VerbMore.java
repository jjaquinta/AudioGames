package jo.audio.loci.thieves.verbs;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.thieves.data.LociPlayer;

public class VerbMore extends VerbLookBase
{
    public VerbMore()
    {
        super("more", null, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        String[] more = player.getMore();
        if ((more != null) && (more.length > 0))
            player.addMore();
        else
            player.addMessage("I have nothing further to add.");
    }
}
