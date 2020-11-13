package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataProfileLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.logic.InitializeLogic;

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
            LociPlayer p = (LociPlayer)DataProfileLogic.cast(obj);
            return userName.equalsIgnoreCase(p.getName());
            });
        if (player instanceof LociPlayer)
        {
            LociPlayer p = (LociPlayer)player;
            if (password.equals(p.getPassword()))
            {
                p.addMessage("Welcome back "+p.getName()+".");
                context.setInvoker(p);
                ContainmentLogic.remove((LociObject)DataStoreLogic.load(InitializeLogic.FOYER_URI), amadan);
                DataStoreLogic.delete(amadan);
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
