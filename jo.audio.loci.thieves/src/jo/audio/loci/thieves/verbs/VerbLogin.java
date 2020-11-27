package jo.audio.loci.thieves.verbs;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociPlayer;

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
        LociBase player = DataStoreLogic.findFirst(LociPlayer.PROFILE, (obj) -> {
            LociPlayer p = (LociPlayer)obj;
            return userName.equalsIgnoreCase(p.getPrimaryName());
            });
        if (player instanceof LociPlayer)
        {
            LociPlayer p = (LociPlayer)player;
            if (password.equals(p.getPassword()))
            {
                p.addMessage("Welcome back "+p.getPrimaryName()+".");
                VerbRegister.enter(context, amadan, p);
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
