package jo.audio.companions.logic;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.logic.build.CastleLogic;
import jo.audio.companions.logic.build.PermitLogic;
import jo.audio.companions.logic.feature.dungeon.DungeonCommandLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.util.utils.DebugUtils;

public class QueryLogic
{
    public static final String QUERY_TEXT = "text";
    public static final String QUERY_TYPE = "type";
    public static final String QUERY_TYPE_YESNO = "yesno";
    public static final String QUERY_TYPE_NSEW = "nsew";
    public static final String QUERY_ACTION = "action";
    public static final String QUERY_ID = "id";
    
    public static void doCancel(CompContextBean context)
    {
        JSONObject q = (JSONObject)context.getUser().getMetadata().get(CompUserBean.META_QUESTION);
        if (q != null)
        {
            String action = JSONUtils.getString(q, QUERY_ACTION);
            if (action != null)
            {
                switch (action)
                {
                    case "death_resurrect":
                        FightLogic.doResurrectNo(context);
                        break;
                }
            }
        }

        context.getUser().getMetadata().remove(CompUserBean.META_QUESTION);
        CompIOLogic.saveUser(context.getUser());
    }
    
    public static void doYes(CompContextBean context)
    {
        JSONObject q = (JSONObject)context.getUser().getMetadata().get(CompUserBean.META_QUESTION);
        if (q == null)
        {
            DebugUtils.trace("No question!");
            doCancel(context);
            return;
        }
        String action = JSONUtils.getString(q, QUERY_ACTION);
        if (action == null)
        {
            DebugUtils.trace("Null action for question: "+q.toJSONString());
        }
        switch (action)
        {
            case "dungeon":
                DungeonCommandLogic.doYes(context, JSONUtils.getString(q, QUERY_ID));
                break;
            case "fire":
                UserLogic.doYesFire(context, JSONUtils.getString(q, QUERY_ID));
                break;
            case "getMessage":
                MessageLogic.doYesGetMessage(context, JSONUtils.getString(q, QUERY_ID));
                break;
            case "createCastle":
                CastleLogic.doYesCreateCastle(context, JSONUtils.getString(q, QUERY_ID));
                break;
            case "death_resurrect":
                FightLogic.doResurrectYes(context);
                break;
        }
    }

    public static void doNo(CompContextBean context)
    {
        JSONObject q = (JSONObject)context.getUser().getMetadata().get(CompUserBean.META_QUESTION);
        String action = JSONUtils.getString(q, QUERY_ACTION );
        switch (action)
        {
            case "dungeon":
                DungeonCommandLogic.doNo(context, JSONUtils.getString(q, QUERY_ID));
                break;
            case "fire":
                UserLogic.doNoFire(context, JSONUtils.getString(q, QUERY_ID));
                break;
            case "getMessage":
                MessageLogic.doNoGetMessage(context, JSONUtils.getString(q, QUERY_ID));
                break;
            case "createCastle":
                CastleLogic.doNoCreateCastle(context, JSONUtils.getString(q, QUERY_ID));
                break;
            case "death_resurrect":
                FightLogic.doResurrectNo(context);
                break;
            case "wizard":
                CompUserBean user = context.getUser();
                user.getMetadata().remove(CompUserBean.META_QUESTION);
                context.addMessage(CompanionsModelConst.TEXT_WIZARD_DECLINE);
                CompIOLogic.saveUser(context.getUser());
                break;
        }
    }

    public static void doDirection(CompContextBean context, int dir)
    {
        JSONObject q = (JSONObject)context.getUser().getMetadata().get(CompUserBean.META_QUESTION);
        String action = JSONUtils.getString(q, QUERY_ACTION );
        switch (action)
        {
            case "createRoom":
                CastleLogic.doDirCreateRoom(context, JSONUtils.getString(q, QUERY_ID), dir);
                break;
            case "createPermit":
                PermitLogic.doDirPermit(context, JSONUtils.getString(q, QUERY_ID), dir);
                break;
        }
    }
}
