package jo.audio.loci.thieves.verbs;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.logic.PlayerLogic;

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
            LociPlayer player = PlayerLogic.getPlayer(username);
            if (player != null)
            {
                LociPlayer p = (LociPlayer)player;
                if (password.equals(p.getPassword()))
                {
                    p.addMessage("Welcome back "+p.getPrimaryName()+".");
                    VerbRegister.enter(context, amadan, p);
                    return;
                }
            }
        }
        amadan.addMessage("That is a incorrect username or password.");
    }
}
