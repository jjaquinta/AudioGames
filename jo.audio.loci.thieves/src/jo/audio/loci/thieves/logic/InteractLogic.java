package jo.audio.loci.thieves.logic;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataProfileLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.ExecuteLogic;
import jo.audio.loci.core.logic.stores.MemoryStore;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociPlayerGhost;
import jo.util.utils.obj.StringUtils;

public class InteractLogic
{
    public static final String TEMP_PLAYER_URI_PREFIX = MemoryStore.PREFIX+"tempplayer/";
    
    public static ExecuteContext interact(String userName, String password, String token, String command)
    {
        LociPlayer player = getPlayer(userName, password, token);
        player.setOnline(true);
        player.setLastActive(System.currentTimeMillis());
        ExecuteContext context = ExecuteLogic.execute(player, command);
        player = (LociPlayer)context.getInvoker();
        player.setLastActive(System.currentTimeMillis());
        if (!context.isSuccess())
            player.addMessage("I am unable to execute '"+command+"'");
        return context;
    }

    private static LociPlayer getPlayer(String userName, String password, String token)
    {
        if (!StringUtils.isTrivial(token))
        {
            LociBase player = DataStoreLogic.load(token);
            if (player instanceof LociPlayer)
                return (LociPlayer)player;
        }
        if (!StringUtils.isTrivial(userName) && !StringUtils.isTrivial(password))
        {
            LociBase player = DataStoreLogic.findFirst(LociPlayer.PROFILE, (obj) -> {
                LociPlayer p = (LociPlayer)DataProfileLogic.cast(obj);
                return (userName.equalsIgnoreCase(p.getPrimaryName()) && password.equals(p.getPassword()));
                });
            if (player instanceof LociPlayer)
                return (LociPlayer)player;
        }
        // create a temporary player
        LociPlayerGhost player = new LociPlayerGhost(TEMP_PLAYER_URI_PREFIX+System.currentTimeMillis());
        player.setName("Amadan");
        player.setDescription("A transparent entity, waiting to be made coporeal.");
        player.setPassword("");
        player.setOnline(true);
        player.setLastActive(System.currentTimeMillis());
        DataStoreLogic.save(player);
        ContainmentLogic.add(InitializeLogic.getFoyeur(), player);
        return player;
    }
}
