package jo.audio.loci.thieves.verbs;

import java.util.List;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.utils.ResponseUtils;
import jo.audio.loci.thieves.data.LociPlayer;

public class VerbInventory extends Verb
{
    public VerbInventory()
    {
        super("inventory,i", "none", "none", "none");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer amadan = (LociPlayer)context.getInvoker();
        List<String> itemNames = amadan.getContainsNames();
        if (itemNames.size() == 0)
            amadan.addMessage("You don't carry anything.");
        else if (itemNames.size() == 1)
            amadan.addMessage("You are carrying a "+itemNames.get(0)+".");
        else
            amadan.addMessage("You are carring "+ResponseUtils.wordList(itemNames.toArray(), -1, "and a ")+".");
    }
}
