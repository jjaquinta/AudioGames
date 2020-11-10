package jo.audio.compedit.logic;

import jo.audio.companions.tools.gui.edit.data.PFeatureBean;
import jo.audio.companions.tools.gui.edit.data.PRoomBean;
import jo.audio.compedit.data.CompEditContextBean;
import jo.audio.compedit.data.CompEditIdentBean;
import jo.audio.compedit.data.CompEditLocationBean;
import jo.audio.compedit.data.CompEditOperationBean;
import jo.audio.compedit.data.CompEditUserBean;
import jo.audio.compedit.slu.CompEditModelConst;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class UserLogic
{
    public static CompEditUserBean getUser(String id)
    {
        if (!id.startsWith("compedituser://"))
            id = "compedituser://"+id;
        return CompEditIOLogic.getUserFromURI(id);
    }

    public static CompEditUserBean newInstance(CompEditIdentBean id)
    {
        String uri = "compedituser://"+id.getUserID();
        CompEditUserBean user = new CompEditUserBean();
        user.setURI(uri);
        user.setLocation(CompEditConstLogic.INITIAL_LOCATION);
        CompEditIOLogic.saveUser(user);
        return user;
    }

    public static void move(CompEditContextBean context, int dir)
    {
        CompEditUserBean user = context.getUser();
        PFeatureBean feature = context.getFeature();
        if (feature == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_FEATURE_LOADED);
            context.setError(true);
            return;
        }
        PRoomBean room = context.getRoom();
        if (room == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_ROOM_LOADED);
            context.setError(true);
            return;
        }
        DebugUtils.trace("move(dir="+dir+")");
        String newID = null;
        switch (dir)
        {
            case CompEditOperationBean.NORTH:
                newID = room.getNorth();
                break;
            case CompEditOperationBean.SOUTH:
                newID = room.getSouth();
                break;
            case CompEditOperationBean.EAST:
                newID = room.getEast();
                break;
            case CompEditOperationBean.WEST:
                newID = room.getWest();
                break;
            default:
                throw new IllegalStateException("Unknown dir="+dir);
        }
        if (StringUtils.isTrivial(newID))
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_ROOM_LOADED);
            context.setError(true);
            return;
        }
        PRoomBean newRoom = feature.findRoom(newID);
        if (newRoom == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_ROOM_LOCATED, newID);
            context.setError(true);
            return;
        }
        CompEditLocationBean loc = context.getLocation();
        user.setOldLocation(loc.getURI());
        loc.setRoomID(newRoom.getID());
        DebugUtils.trace("move to id="+newID+".");
        user.setLocation(loc.getURI());
        user.setLastMoveDirection(dir);
        CompEditIOLogic.saveUser(user);
    }

    public static void resetUser(CompEditUserBean user)
    {
        user.setLocation(CompEditConstLogic.INITIAL_LOCATION);
        
    }

    public static void selectFeature(CompEditContextBean context,
            long oid)
    {
        PFeatureBean feature = null;
        for (PFeatureBean f : context.getModule().getFeatures())
            if (f.getOID() == oid)
            {
                feature = f;
                break;
            }
        if (feature == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_SUCH_FEATURE, oid);
            context.setError(true);
            return;
        }
        CompEditUserBean user = context.getUser();
        context.getLocation().setFeatureID(String.valueOf(oid));
        context.getLocation().setRoomID(feature.getEntranceID());
        user.setLocation(context.getLocation().getURI());
        CompEditIOLogic.saveUser(user);
    }
}
