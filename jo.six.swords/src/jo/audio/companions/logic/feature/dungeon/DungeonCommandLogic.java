package jo.audio.companions.logic.feature.dungeon;

import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.logic.CompIOLogic;
import jo.audio.companions.logic.QueryLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;

public class DungeonCommandLogic
{
    public static final String LIST_POOL = "dungeonPoolList";
    public static final String LIST_CHEST = "dungeonChestList";
    public static final String LIST_THRONE = "dungeonThroneList";
    private static final String CMD_CHEST = "chest";
    private static final String CMD_POOL = "pool";
    private static final String CMD_THRONE = "throne";

    public static void postEnter(CompContextBean context, List<String> args)
    {
        if (args.size() < 2)
            return;
        String cmd = args.get(1);
        switch (cmd)
        {
            case CMD_THRONE:
                doThrone(context);
                break;
            case CMD_POOL:
                doPool(context);
                break;
            case CMD_CHEST:
                doChest(context);
                break;
        }
    }

    private static void askQuestion(CompContextBean context, String qmsg, String action, String id,
            String nlist, String nmsg)
    {
        CompUserBean user = context.getUser();
        if ((user.getMetadata() != null) && user.getMetadata().containsKey(nlist))
        {
            if (ResponseUtils.isInList(user.getMetadata().getString(nlist), user.getLocation()))
            {
                context.addMessage(nmsg);
                return;
            }
        }        
        AudioMessageBean q = new AudioMessageBean(qmsg);
        JSONObject question = new JSONObject();
        question.put(QueryLogic.QUERY_TEXT, q.toJSON());
        question.put(QueryLogic.QUERY_ACTION, action);
        question.put(QueryLogic.QUERY_ID, id);
        if (user.getMetadata() == null)
            user.setMetadata(new JSONObject());
        user.getMetadata().put(CompUserBean.META_QUESTION, question);
        CompIOLogic.saveUser(user);
        context.setError(false);        
    }

    private static void doThrone(CompContextBean context)
    {
        askQuestion(context, CompanionsModelConst.TEXT_DO_YOU_WANT_TO_SIT_IN_THE_THRONE, "dungeon", CMD_THRONE,
                LIST_THRONE, CompanionsModelConst.TEXT_THE_THRONE_IS_CRACKED);
    }

    private static void doChest(CompContextBean context)
    {
        askQuestion(context, CompanionsModelConst.TEXT_DO_YOU_WANT_TO_OPEN_THE_CHEST, "dungeon", CMD_CHEST,
                LIST_CHEST, CompanionsModelConst.TEXT_THE_CHEST_IS_EMPTY);
    }

    private static void doPool(CompContextBean context)
    {
        askQuestion(context, CompanionsModelConst.TEXT_DO_YOU_WANT_TO_DRINK_FROM_THE_POOL, "dungeon", CMD_POOL,
                LIST_POOL, CompanionsModelConst.TEXT_THE_POOL_IS_EMPTY);
    }
    
    public static void doYes(CompContextBean context, String cmd)
    {
        switch (cmd)
        {
            case CMD_THRONE:
                DungeonThroneLogic.doYesThrone(context);
                break;
            case CMD_POOL:
                DungeonPoolLogic.doYesPool(context);
                break;
            case CMD_CHEST:
                DungeonChestLogic.doYesChest(context);
                break;
        }
        CompUserBean user = context.getUser();
        user.getMetadata().remove(CompUserBean.META_QUESTION);
        CompIOLogic.saveUser(user);
        context.setError(false);
    }
    
    
    
    public static void doNo(CompContextBean context, String cmd)
    {
        CompUserBean user = context.getUser();
        user.getMetadata().remove(CompUserBean.META_QUESTION);
        CompIOLogic.saveUser(user);
        context.setError(false);
    }
}
