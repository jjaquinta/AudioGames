package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.sandbox.data.LociPlayer;

public class VerbRegister2 extends Verb
{
    public VerbRegister2()
    {
        super("register", ".*", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer amadan = (LociPlayer)context.getInvoker();
        String userName = context.getDirectObjectText();
        int o = userName.indexOf(' ');
        if (o < 0)
        {
            amadan.addMessage("You need to give both a username and a password to register.");
            return;
        }
        String password = userName.substring(o).trim();
        userName = userName.substring(0, o).trim();
        VerbRegister.doRegister(context, userName, password);
    }
}
