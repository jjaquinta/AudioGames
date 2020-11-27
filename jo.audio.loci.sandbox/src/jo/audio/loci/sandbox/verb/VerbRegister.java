package jo.audio.loci.sandbox.verb;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.logic.InitializeLogic;
import jo.util.utils.obj.StringUtils;

public class VerbRegister extends Verb
{
    public VerbRegister()
    {
        super("register", ".*", "with", ".*");
    }

    @Override
    public void execute(ExecuteContext context)
    {
        String userName = context.getDirectObjectText();
        String password = context.getIndirectObjectText();
        doRegister(context, userName, password);
    }
    
    public static void doRegister(ExecuteContext context, String userName, String password)
    {
        LociPlayer amadan = (LociPlayer)context.getInvoker();
        LociBase player = DataStoreLogic.findFirst(LociPlayer.class.getSimpleName(), (obj) -> {
            LociPlayer p = (LociPlayer)obj;
            return userName.equalsIgnoreCase(p.getPrimaryName());
            });
        if (player instanceof LociPlayer)
        {
            LociPlayer p = (LociPlayer)player;
            if (password.equals(p.getPassword()))
            {
                p.addMessage("Welcome back "+p.getPrimaryName()+".");
                enter(context, amadan, p);
            }
            else
                amadan.addMessage("The name '"+userName+"' is already taken.");
        }
        else
        {
            String uri = DiskStore.PREFIX+"player/"+System.currentTimeMillis();
            LociPlayer p = new LociPlayer(uri);
            p.setName(userName);
            p.setDescription("A non-descript player.");
            p.setPassword(password);
            p.setOwner(p.getURI());
            p.setLastActive(System.currentTimeMillis());
            p.addMessage("Welcome "+p.getPrimaryName()+".");
            enter(context, amadan, p);
        }
    }

    public static void enter(ExecuteContext context, LociPlayer amadan, LociPlayer p)
    {
        context.setInvoker(p);
        p.setOnline(true);
        p.setLastActive(System.currentTimeMillis());
        LociObject foyeur = (LociObject)DataStoreLogic.load(amadan.getContainedBy());
        ContainmentLogic.remove(foyeur, amadan);
        LociObject entrance;
        if (!StringUtils.isTrivial(p.getContainedBy()))
        {
            entrance = (LociObject)DataStoreLogic.load(p.getContainedBy());
        }
        else
        {
            entrance = InitializeLogic.geEntrance();
            ContainmentLogic.add(entrance, p);
        }
        VerbLookBase.doLook(p, entrance);
    }
}
