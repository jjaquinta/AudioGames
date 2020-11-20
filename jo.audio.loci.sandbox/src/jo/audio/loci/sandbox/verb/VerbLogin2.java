package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.DataProfileLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.sandbox.data.LociPlayer;

public class VerbLogin2 extends Verb
{
    public VerbLogin2()
    {
        super("login,log in", ".*", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer amadan = (LociPlayer)context.getInvoker();
        String username = "";
        String password = context.getDirectObjectText();
        for (;;)
        {
            int o = password.indexOf(' ');
            if (o < 0)
                break;
            username = (username + " " + password.substring(0, o)).trim();
            password = password.substring(o).trim();
            final String userName = username;
            LociBase player = DataStoreLogic.findFirst(LociPlayer.PROFILE, (obj) -> {
                LociPlayer p = (LociPlayer)DataProfileLogic.cast(obj);
                return userName.equalsIgnoreCase(p.getName());
                });
            if (player instanceof LociPlayer)
            {
                LociPlayer p = (LociPlayer)player;
                if (password.equals(p.getPassword()))
                {
                    p.addMessage("Welcome back "+p.getName()+".");
                    VerbRegister.enter(context, amadan, p);
                    return;
                }
            }
        }
        amadan.addMessage("That is a incorrect username or password.");
    }
}
