package jo.audio.companions.logic;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.FeatureInstanceBean;
import jo.audio.companions.data.LocationBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;

public class MessageLogic
{

    private static final String USER_MSG_CURRENT = "currentMessages";
    private static final String USER_MSG_DECLINED = "lastDeclinedMessage";
    private static final String MSG_ID = "id";
    private static final String MSG_DEST = "destination";
    private static final String MSG_REWARD = "reward";

    public static void addMessageInstance(FeatureInstanceBean inst,
            RegionBean region, SquareBean sq, CompRoomBean room)
    {
        SquareBean s = FeatureLogic.findTownOrCastle(region, sq);
        if (s != null)
        {
            int dist = s.getOrds().dist(sq.getOrds());
            int reward = dist*(BaseUserState.RND.nextInt(6) + 1);
            JSONObject msg = new JSONObject();
            msg.put(MSG_ID, String.valueOf(System.currentTimeMillis()));
            msg.put(MSG_DEST, s.getOrds().toString());
            msg.put(MSG_REWARD, reward);
            inst.getRoomToMessage().put(room.getID(), msg);
        }
    }
    
    public static void checkMessagesForDelivery(CompContextBean context)
    {
        JSONObject currentMessages = JSONUtils.getObject(context.getUser().getMetadata(), USER_MSG_CURRENT);
        if (currentMessages == null)
            return;
        LocationBean loc = new LocationBean(context.getUser().getLocation());
        for (String key : currentMessages.keySet().toArray(new String[0]))
        {
            JSONObject msg = JSONUtils.getObject(currentMessages, key);
            if (msg == null)
                continue;
            String destination = msg.getString(MSG_DEST);
            CoordBean dest = new CoordBean(destination);
            int reward = JSONUtils.getInt(msg, MSG_REWARD);
            if (dest.equals(loc))
            {
                DebugUtils.trace("MessageLogic - checkMessage, adding p8 message");
                context.addMessage(8, CompanionsModelConst.TEXT_HERE_S_YOUR_XXX_FOR_DELIVERING_THAT_MESSAGE, reward);
                ExperienceLogic.addGold(context.getUser(), reward);
                currentMessages.remove(key);
                CompIOLogic.saveUser(context.getUser());
            }
            RegionBean region = GenerationLogic.getRegion(dest);
            SquareBean square = GenerationLogic.getSquare(dest);
            FeatureBean feature = FeatureLogic.getFeature(region, square, context);
            AudioMessageBean m = new AudioMessageBean(CompanionsModelConst.TEXT_XXX_FOR_YYY, feature.getName(), reward);
            context.getMessageDestinations().add(m);
            //DebugUtils.trace("MessageLogic - checkMessagesForDelivery, msg: "+m.toJSON());
        }
    }

    public static void checkMessagesForPickup(CompContextBean context)
    {
        if (context.getRoomParam("messageSource") != Boolean.TRUE)
            return;
        JSONObject msg = context.getFeature().getRoomToMessage().get(context.getRoom().getID());
        String msgID = msg.getString(MSG_ID);
        CompUserBean user = context.getUser();
        if (user.getMetadata() == null)
            user.setMetadata(new JSONObject());
        JSONObject currentMessages = JSONUtils.getObject(user.getMetadata(), USER_MSG_CURRENT);
        if (currentMessages != null)
        {
            if (currentMessages.get(msgID) != null)
                return;
        }
        String lastDeclinedMessage = user.getMetadata().getString(USER_MSG_DECLINED);
        if (msgID.equals(lastDeclinedMessage))
            return;
        String destination = JSONUtils.getString(msg, MSG_DEST);
        CoordBean dest = new CoordBean(destination);
        RegionBean region = GenerationLogic.getRegion(dest);
        SquareBean square = GenerationLogic.getSquare(dest);
        int reward = JSONUtils.getInt(msg, MSG_REWARD);
        FeatureBean feature = FeatureLogic.getFeature(region, square, context);
        AudioMessageBean text = new AudioMessageBean(8, CompanionsModelConst.TEXT_WOULD_YOU_TAKE_A_MESSAGE_TO_XXX_FOR_YYY_GOLD, 
                feature.getName(), reward);
        JSONObject question = new JSONObject();
        question.put(QueryLogic.QUERY_TEXT, text.toJSON());
        question.put(QueryLogic.QUERY_ACTION, "getMessage");
        question.put(QueryLogic.QUERY_ID, msgID);
        user.getMetadata().put(CompUserBean.META_QUESTION, question);
        CompIOLogic.saveUser(user);
        DebugUtils.trace("Adding question: "+question.toJSONString());
    }

    public static void doYesGetMessage(CompContextBean context, String id)
    {
        CompOperationLogic.fillContext(context);
        JSONObject msg = context.getFeature().getRoomToMessage().get(context.getRoom().getID());
        JSONObject currentMessages = JSONUtils.getObject(context.getUser().getMetadata(), USER_MSG_CURRENT);
        if (currentMessages == null)
        {
            currentMessages = new JSONObject();
            context.getUser().getMetadata().put(USER_MSG_CURRENT, currentMessages);
        }
        currentMessages.put(id, msg);
        context.getUser().getMetadata().remove(CompUserBean.META_QUESTION);
        context.addMessage(CompanionsModelConst.TEXT_MESSAGE_TAKEN);
        CompIOLogic.saveUser(context.getUser());
    }

    public static void doNoGetMessage(CompContextBean context, String id)
    {
        context.getUser().getMetadata().put(USER_MSG_DECLINED, id);
        context.getUser().getMetadata().remove(CompUserBean.META_QUESTION);
        context.addMessage(CompanionsModelConst.TEXT_OK_WE_LL_SKIP_THIS_MESSAGE);
        CompIOLogic.saveUser(context.getUser());
    }

}
