package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.sandbox.data.LociContainer;
import jo.audio.loci.sandbox.data.LociPlayer;

public class VerbCreateContainer extends Verb
{
    public VerbCreateContainer()
    {
        super("create,make", "container", "named,called", ".*");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer amadan = (LociPlayer)context.getInvoker();
        String name = context.getIndirectObjectText();
        String uri = DiskStore.PREFIX+"container/"+System.currentTimeMillis();
        LociContainer p = new LociContainer(uri);
        p.setName(name);
        p.setDescription("A small, blue box.");
        p.setOwner(amadan.getURI());
        ContainmentLogic.add(amadan, p);
        amadan.addMessage("You have created a "+p.getPrimaryName()+".");
    }
}
