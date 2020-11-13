package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociRoom;

public abstract class VerbHelpBase extends Verb
{
    public VerbHelpBase(String verbText, String directObjectText, String prepositionText, String indirectObjectText)
    {
        super(verbText, directObjectText, prepositionText, indirectObjectText);
    }

    protected void doHelp(LociPlayer player, LociObject thing)
    {
        String msg = ((LociRoom)thing).getHelpText();
        if (msg == null)
            msg = "Try 'help commands' for a list of commands.";
        player.addMessage(msg);
    }
}
