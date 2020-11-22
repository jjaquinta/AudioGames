package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociPlayerAdmin;
import jo.audio.loci.sandbox.data.LociThing;

public class VerbName extends Verb
{
    public VerbName()
    {
        super("name", "any", "as,with", ".*");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer amadan = (LociPlayer)context.getInvoker();
        LociThing obj = (LociThing)context.getMatchedDirectObject();
        String newDesc = context.getIndirectObjectText();
        if (!amadan.getURI().equals(obj.getOwner()) && !(amadan instanceof LociPlayerAdmin))
        {
            amadan.addMessage("Only the owner can change the name.");
            return;
        }
        obj.setName(newDesc);
        amadan.addMessage("Name changed.");
    }
}
