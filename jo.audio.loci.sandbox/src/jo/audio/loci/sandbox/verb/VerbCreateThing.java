package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociThing;

public class VerbCreateThing extends Verb
{
    public VerbCreateThing()
    {
        super("create", "thing", "named", ".*");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer amadan = (LociPlayer)context.getInvoker();
        String name = context.getIndirectObjectText();
        String uri = DiskStore.PREFIX+"thing/"+System.currentTimeMillis();
        LociThing p = new LociThing(uri);
        p.setName(name);
        p.setDescription("A small, blue thing.");
        p.setOwner(amadan.getURI());
        ContainmentLogic.add(amadan, p);
        amadan.addMessage("You have created a "+p.getName()+".");
    }
}
