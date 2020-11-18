package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.sandbox.data.LociContainer;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.util.utils.obj.StringUtils;

public class VerbShut extends Verb
{
    public VerbShut()
    {
        super("close,shut", "this", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociContainer container = (LociContainer)context.getMatchedDirectObject();
        if (!StringUtils.equals(player.getURI(), container.getOwner()))
        {
            player.addMessage("You do not own the "+container.getOwner()+".");
            return;
        }
        if (!container.getOpen())
        {
            player.addMessage("The "+container.getName()+" is already closed.");
            return;
        }
        container.setOpen(false);
        player.addMessage("You close the "+container.getName()+".");
    }
}
