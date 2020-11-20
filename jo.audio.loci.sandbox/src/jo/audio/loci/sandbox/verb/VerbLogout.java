package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.sandbox.data.LociPlayer;

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
