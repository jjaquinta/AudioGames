package jo.audio.loci.sandbox.logic;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataProfileLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.ExecuteLogic;
import jo.audio.loci.core.logic.stores.MemoryStore;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociRoom;
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
                return (userName.equalsIgnoreCase(p.getName()) && password.equals(p.getPassword()));
                });
            if (player instanceof LociPlayer)
                return (LociPlayer)player;
        }
        // create a temporary player
        LociPlayer player = new LociPlayer(TEMP_PLAYER_URI_PREFIX+System.currentTimeMillis());
        player.setName("Amadan");
        player.setDescription("A transparent entity, waiting to be made coporeal.");
        player.setPassword("");
        DataStoreLogic.save(player);
        ContainmentLogic.add(getFoyeur(), player);
        return player;
    }
    
    private static LociRoom getFoyeur()
    {
        LociRoom foyeur = (LociRoom)DataStoreLogic.load(InitializeLogic.FOYER_URI);
        return foyeur;
    }
}
