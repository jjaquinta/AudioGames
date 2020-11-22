package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociRoom;

public class VerbSay extends Verb
{
    public VerbSay()
    {
        super("say,'", ".*", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        String text = context.getDirectObjectText();
        LociRoom room = (LociRoom)DataStoreLogic.load(player.getContainedBy());
        if (room == null)
        {
            player.addMessage("You say \""+text+"\" to the empty void.");
            return;
        }
        for (String uri : room.getContains())
        {
            LociBase thing = DataStoreLogic.load(uri);
            if (thing instanceof LociPlayer)
            {
                LociPlayer p2 = (LociPlayer)thing;
                if (!p2.getOnline())
                    continue;
                if (p2.getURI().equals(player.getURI()))
                    p2.addMessage("You say \""+text+"\".");
                else
                    p2.addMessage(player.getPrimaryName()+" says \""+text+"\".");
            }
        }
    }
}
