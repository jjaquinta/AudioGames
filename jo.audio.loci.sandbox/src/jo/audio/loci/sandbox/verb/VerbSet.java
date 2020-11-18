package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociThing;
import jo.util.utils.obj.StringUtils;

public class VerbSet extends Verb
{
    public VerbSet()
    {
        super("set", "this", "to", "public,private");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociThing thing = (LociThing)context.getMatchedDirectObject();
        String op = context.getIndirectObjectText();
        if (!StringUtils.equals(player.getURI(), thing.getOwner()))
        {
            player.addMessage("You do not own the "+thing.getName()+".");
            return;
        }
        switch (op.toLowerCase())
        {
            case "public":
                if (thing.getPublic())
                    player.addMessage(thing.getName()+" is already set to public.");
                else
                {
                    thing.setPublic(true);
                    player.addMessage(thing.getName()+" is now set to public.");
                }
                break;
            case "private":
                if (!thing.getPublic())
                    player.addMessage(thing.getName()+" is already set to private.");
                else
                {
                    thing.setPublic(false);
                    player.addMessage(thing.getName()+" is now set to private.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported set: "+op);
        }
    }
}
