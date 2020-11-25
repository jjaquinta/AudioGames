package jo.audio.loci.thieves.verbs;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.thieves.data.LociPlayer;

public class VerbLogout extends Verb
{
    public VerbLogout()
    {
        super("logout,log out,quit", null, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer amadan = (LociPlayer)context.getInvoker();
        amadan.setOnline(false);
        amadan.addMessage("Thank you for playing.");
    }
}
