package jo.audio.loci.thieves.verbs;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.logic.PlayerLogic;

public class VerbLogin extends Verb
{
    public VerbLogin()
    {
        super("login,log in", ".*", "with", ".*");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer amadan = (LociPlayer)context.getInvoker();
        String userName = context.getDirectObjectText();
        String password = context.getIndirectObjectText();
        LociPlayer player = PlayerLogic.getPlayer(userName);
        if (player != null)
        {
            if (password.equals(player.getPassword()))
            {
                player.addMessage("Welcome back "+player.getPrimaryName()+".");
                VerbRegister.enter(context, amadan, player);
            }
            else
                amadan.addMessage("That is a incorrect username or password.");
        }
        else
        {
            amadan.addMessage("That is a incorrect username or password.");
        }
    }
}
