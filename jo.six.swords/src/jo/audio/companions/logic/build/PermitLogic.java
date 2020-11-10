package jo.audio.companions.logic.build;

import org.json.simple.JSONObject;

import jo.audio.common.logic.io.DriverLogic;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.FeatureInstanceBean;
import jo.audio.companions.logic.CompIOLogic;
import jo.audio.companions.logic.CompOperationLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.QueryLogic;
import jo.audio.companions.logic.UserLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.companions.tools.gui.edit.data.PFeatureBean;
import jo.audio.companions.tools.gui.edit.data.PRoomBean;
import jo.audio.compedit.data.CompEditModuleBean;
import jo.audio.compedit.logic.CompEditIOLogic;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class PermitLogic
{
    private static final int SAVE_MODULE = 0x01;
    private static final int USE_ITEM = 0x02;
    private static final int EXIT_QUESTION = 0x04;
    
    public static void equipPermit(CompContextBean context, CompUserBean user,
            CompItemInstanceBean item)
    {
        CompOperationLogic.fillContext(context);
        if ((context.getFeature() != null) && (context.getRoom() != null))
        {
            if ("permitMark".equals(item.getID()))
                PermitLogic.doPlaceMark(context, user, item);
            else
                PermitLogic.queryPlacePermit(context, user, item);
        }
        else
            context.addMessage(CompanionsModelConst.TEXT_THIS_CAN_ONLY_BE_USED_INSIDE_A_FEATURE_YOU_OWN);
    }

    public static void doPlaceMark(CompContextBean context, CompUserBean user, CompItemInstanceBean item)
    {
        CompRoomBean r = context.getRoom();
        if (r == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_THIS_CAN_ONLY_BE_USED_INSIDE_A_FEATURE_YOU_OWN);
            return;
        }
        user.getMetadata().put(CompUserBean.META_MARK, r.getID());
        
        context.addMessage(CompanionsModelConst.TEXT_THE_CURRENT_POSITION_HAS_BEEN_MARKED);
    
        UserLogic.doRemoveItem(user, item.getID(), 1);
        DebugUtils.trace("Saving user");
        CompIOLogic.saveUser(user);
        CompIOLogic.logUse(user, item.getID(), 1);
        context.setError(false);
    }

    static void queryPlacePermit(CompContextBean context, CompUserBean user,
            CompItemInstanceBean item)
    {
        if ((user.getMetadata() == null) || StringUtils.isTrivial(user.getMetadata().getString(CompUserBean.META_MARK)))
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_MUST_FIRST_MARK_A_LOCATION_BEFORE_YOU_CAN_USE_A_PERMIT);
            return;
        }
        CompRoomBean here = context.getRoom();
        if (!StringUtils.isTrivial(here.getNorth()) && !StringUtils.isTrivial(here.getSouth())
                && !StringUtils.isTrivial(here.getEast()) && !StringUtils.isTrivial(here.getWest()))
        {
            context.addMessage(CompanionsModelConst.TEXT_ALL_EXITS_FROM_THIS_ROOM_ARE_TAKEN);
            return;
        }
        FeatureInstanceBean f = context.getFeature();
        if (!user.getURI().equals(f.getFeature().getAccount()))
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_DO_NOT_OWN_THIS_CASTLE);
            return;
        }
        AudioMessageBean q = new AudioMessageBean(CompanionsModelConst.TEXT_PLEASE_SAY_WHICH_DIRECTION_YOU_WOULD_LIKE_TO_ADD_A_XXX_IN, 
                item.getFullName());
    
        JSONObject question = new JSONObject();
        question.put(QueryLogic.QUERY_TEXT, q.toJSON());
        question.put(QueryLogic.QUERY_TYPE, QueryLogic.QUERY_TYPE_NSEW);
        question.put(QueryLogic.QUERY_ACTION, "createPermit");
        question.put(QueryLogic.QUERY_ID, item.getID());
        if (user.getMetadata() == null)
            user.setMetadata(new JSONObject());
        user.getMetadata().put(CompUserBean.META_QUESTION, question);
        //context.addMessage(q);
        DebugUtils.trace("Saving user");
        CompIOLogic.saveUser(user);
        context.setError(false);
    }

    public static void doDirPermit(CompContextBean context, String id,
            int odir)
    {
        CompOperationLogic.fillContext(context);
        CompUserBean user = context.getUser();
        CompItemInstanceBean item = user.getItem(id);
        if (item == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ITEM_ID_SPECIFIED_XXX, id);
            context.setError(true);
            return;
        }
        String sdir = null;
        int rdir = odir - 1;
        switch (odir)
        {
            case CompOperationBean.NORTH:
                sdir = CompanionsModelConst.TEXT_NORTH;
                break;
            case CompOperationBean.SOUTH:
                sdir = CompanionsModelConst.TEXT_SOUTH;
                break;
            case CompOperationBean.EAST:
                sdir = CompanionsModelConst.TEXT_EAST;
                break;
            case CompOperationBean.WEST:
                sdir = CompanionsModelConst.TEXT_WEST;
                break;
            default:
                context.addMessage(CompanionsModelConst.TEXT_BAD_DIRECTION_ID_SPECIFIED_XXX, odir);
                context.setError(true);
                return;
        }
        CompRoomBean currentRoomLive = context.getRoom();
        if (isMarkPermit(item.getID()))
            if (!StringUtils.isTrivial(currentRoomLive.getDirection(rdir)))
            {
                context.addMessage(CompanionsModelConst.TEXT_SOMETHING_HAS_ALREADY_BEEN_BUILD_IN_DIRECTION_XXX, "{{"+sdir+"}}");
                return;
            }
        String moduleID = "compmodule://"+user.getURI().substring(11);
        CompEditModuleBean module = CompEditIOLogic.getModuleFromURI(moduleID);
        if (module == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_MODULE_ID_SPECIFIED_XXX, moduleID);
            context.setError(true);
            return;
        }
        PFeatureBean feature = null;
        for (int i = 0; i < module.getFeatures().size(); i++)
        {
            PFeatureBean f = module.getFeatures().get(i);
            CoordBean c = new CoordBean(f.getLocation());
            if ((c.getX() == context.getLocation().getX()) && (c.getY() == context.getLocation().getY()) && (c.getZ() == context.getLocation().getZ()))
            {
                feature = f;
                break;
            }
        }
        if (feature == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_FEATURE_ID_SPECIFIED_XXX, context.getLocation().toString());
            context.setError(true);
            return;
        }
        PRoomBean fromRoom = feature.findRoom(currentRoomLive.getID());
        if (fromRoom == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ROOM_ID_SPECIFIED_XXX, item.getType().getID());
            context.setError(true);
            return;
        }
        //DebugUtils.trace("PermitLogic: found source room "+currentRoomLive.getID()+" as '"+fromRoom.getName()+"'.");            
        String mark = null;
        PRoomBean toRoom = null;
        if (isMarkPermit(item.getID()))
        {
            mark = user.getMetadata().getString(CompUserBean.META_MARK);
            toRoom = feature.findRoom(mark);
            if (toRoom == null)
            {
                DebugUtils.trace("Can't find "+mark+" in this feature.");            
                context.addMessage(CompanionsModelConst.TEXT_MARKED_ROOM_IS_NOT_IN_THIS_STRUCTURE);
                context.setError(true);
                return;
            }
        }
        //DebugUtils.trace("PermitLogic: found destination room "+mark+" as '"+toRoom.getName()+"'.");            

        int action = SAVE_MODULE|USE_ITEM|EXIT_QUESTION;
        switch (item.getID())
        {
            case "permitDoor":
                if (!StringUtils.isTrivial(toRoom.getDir(CompRoomBean.opposite(rdir))))
                {
                    context.addMessage(CompanionsModelConst.TEXT_SOMETHING_HAS_ALREADY_BEEN_BUILD_IN_DIRECTION_XXX, "{{"+sdir+"}}");
                    return;
                }
                fromRoom.setDir(rdir, toRoom.getID());
                toRoom.setDir(CompRoomBean.opposite(rdir), fromRoom.getID());
                context.addMessage(CompanionsModelConst.TEXT_WOOSH_A_XXX_APPEARS_TO_THE_YYY, item.getFullName(), "{{"+sdir+"}}");
                break;
            case "permitDoorOneWay":
                fromRoom.setDir(rdir, toRoom.getID());
                context.addMessage(CompanionsModelConst.TEXT_WOOSH_A_XXX_APPEARS_TO_THE_YYY, item.getFullName(), "{{"+sdir+"}}");
                break;
            case "permitDoorLowLevel":
                action = addLevelDoor(context, fromRoom, rdir, 3);
                break;
            case "permitDoorMediumLevel":
                action = addLevelDoor(context, fromRoom, rdir, 6);
                break;
            case "permitDoorHighLevel":
                action = addLevelDoor(context, fromRoom, rdir, 9);
                break;
            default:
                context.addMessage(CompanionsModelConst.TEXT_BAD_ITEM_ID_SPECIFIED_XXX, item.getID());
                context.setError(true);
                return;
        }
        if ((action&SAVE_MODULE) != 0)
        {
            DebugUtils.trace("Saving module");
            DebugUtils.trace(module.toJSON().toJSONString());
            DriverLogic.setSingleThreaded(true);
            CompEditIOLogic.saveModule(module);
            DriverLogic.setSingleThreaded(false);
            FeatureLogic.readDynamicModules(true);
            context.setNewText(true);
        }
        if ((action&USE_ITEM) != 0)
            UserLogic.doRemoveItem(user, item.getID(), 1);
        if ((action&EXIT_QUESTION) != 0)
            user.getMetadata().remove(CompUserBean.META_QUESTION);
        DebugUtils.trace("Saving user");
        CompIOLogic.saveUser(user);
        CompIOLogic.logUse(user, item.getID(), 1);
        context.setError(false);
    }

    private static int addLevelDoor(CompContextBean context, PRoomBean room, int dir, int level)
    {
        if (StringUtils.isTrivial(room.getDir(dir)))
        {
            context.addMessage(CompanionsModelConst.TEXT_THERE_IS_NO_DOOR_IN_THAT_DIRECTION);
            return 0;
        }
        JSONObject doorLock = new JSONObject();
        doorLock.put("expr", "@getValue($user.challengeLevel) < "+level);
        JSONObject poolTrueMessage = new JSONObject();
        poolTrueMessage.put("message", CompanionsModelConst.TEXT_YOU_ARENT_HIGH_ENOUGH_LEVEL_TO_GO_THROUGH_THAT_DOOR);
        doorLock.put("trueMessage", poolTrueMessage);
        room.setDirectionLock(dir, doorLock);
        context.addMessage(CompanionsModelConst.TEXT_THAT_DOOR_IS_BLOCKED_TO_ALL_BUT_LEVEL_XXX_AND_HIGHER, level);
        return SAVE_MODULE|USE_ITEM|EXIT_QUESTION;
    }

    private static boolean isMarkPermit(String permit)
    {
        switch (permit)
        {
            case "permitDoor":
            case "permitDoorOneWay":
                return true;
            case "permitDoorLowLevel":
            case "permitDoorMediumLevel":
            case "permitDoorHighLevel":
                return false;
        }
        return false;
    }
}
