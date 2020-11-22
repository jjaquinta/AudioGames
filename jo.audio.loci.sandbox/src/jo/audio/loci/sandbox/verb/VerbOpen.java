package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.sandbox.data.LociContainer;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.util.utils.obj.StringUtils;

public class VerbOpen extends Verb
{
    public VerbOpen()
    {
        super("open", "this", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociContainer container = (LociContainer)context.getMatchedDirectObject();
        if (!StringUtils.equals(player.getURI(), container.getOwner()))
        {
            player.addMessage("You do not own the "+container.getPrimaryName()+".");
            return;
        }
        if (container.getOpen())
        {
            player.addMessage("The "+container.getPrimaryName()+" is already open.");
            return;
        }
        container.setOpen(true);
        player.addMessage("You open the "+container.getPrimaryName()+".");
    }
}
