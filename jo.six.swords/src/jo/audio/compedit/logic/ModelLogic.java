package jo.audio.compedit.logic;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;
import org.json.simple.parser.ParseException;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.GeoBean;
import jo.audio.companions.data.build.PFeatureBean;
import jo.audio.companions.data.build.PRoomBean;
import jo.audio.compedit.data.CompEditContextBean;
import jo.audio.compedit.data.CompEditModuleBean;
import jo.audio.compedit.data.CompEditOperationBean;
import jo.audio.compedit.slu.CompEditModelConst;
import jo.util.utils.obj.StringUtils;

public class ModelLogic
{
    public static void newFeature(CompEditContextBean context, String featureName)
    {
        CompEditModuleBean module = context.getModule();
        if (module == null)
        {
            module = new CompEditModuleBean();
            String id = context.getUser().getURI();
            module.setUser(id);
            int o = id.indexOf("://");
            if (o >= 0)
                id = id.substring(o + 3);
            id = "module://"+id;
            module.setID(id);
        }        
        PFeatureBean feature = new PFeatureBean();
        feature.setOID((int)System.currentTimeMillis());
        feature.setName(featureName);
        feature.setLocation("");
        PRoomBean room = new PRoomBean();
        room.setID("room"+System.currentTimeMillis());
        room.setName("Entrance");
        room.setDescription("This is an empty room.");
        room.setType(CompRoomBean.TYPE_SCENIC);
        feature.getRooms().add(room);
        feature.setEntranceID(room.getID());
        module.getFeatures().add(feature);
        CompEditIOLogic.saveModule(module);
        context.getLocation().setModuleID(module.getID());
        context.getLocation().setFeatureID(String.valueOf(feature.getOID()));
        context.getLocation().setRoomID(room.getID());
        context.getUser().setLocation(context.getLocation().getURI());
        CompEditIOLogic.saveUser(context.getUser());
    }
    public static void nameFeature(CompEditContextBean context, String featureName)
    {
        CompEditModuleBean module = context.getModule();
        if (module == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_MODULE_LOADED);
            context.setError(true);
            return;
        }
        PFeatureBean feature = context.getFeature();
        if (feature == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_FEATURE_LOADED);
            context.setError(true);
            return;
        }
        feature.setName(featureName);
        CompEditIOLogic.saveModule(module);
    }
    public static void setLocation(CompEditContextBean context, int x, int y)
    {
        CompEditModuleBean module = context.getModule();
        if (module == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_MODULE_LOADED);
            context.setError(true);
            return;
        }
        PFeatureBean feature = context.getFeature();
        if (feature == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_FEATURE_LOADED);
            context.setError(true);
            return;
        }
        GeoBean location;
        if (feature.getLocation() != null)
            location = new GeoBean(feature.getLocation());
        else
            location = new GeoBean();
        
        location.setX(x);
        location.setY(y);
        feature.setLocation(location.toString());
        CompEditIOLogic.saveModule(module);
    }
    public static void setDimension(CompEditContextBean context, int z)
    {
        CompEditModuleBean module = context.getModule();
        if (module == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_MODULE_LOADED);
            context.setError(true);
            return;
        }
        PFeatureBean feature = context.getFeature();
        if (feature == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_FEATURE_LOADED);
            context.setError(true);
            return;
        }
        GeoBean location;
        if (feature.getLocation() != null)
            location = new GeoBean(feature.getLocation());
        else
            location = new GeoBean();
        location.setZ(z);
        feature.setLocation(location.toString());
        CompEditIOLogic.saveModule(module);
    }
    public static void setEnabledBy(CompEditContextBean context, String enabledBy)
    {
        CompEditModuleBean module = context.getModule();
        if (module == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_MODULE_LOADED);
            context.setError(true);
            return;
        }
        PFeatureBean feature = context.getFeature();
        if (feature == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_FEATURE_LOADED);
            context.setError(true);
            return;
        }
        feature.setEnabledBy(enabledBy);
        CompEditIOLogic.saveModule(module);
    }
    public static void nameRoom(CompEditContextBean context, String roomName)
    {
        CompEditModuleBean module = context.getModule();
        if (module == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_MODULE_LOADED);
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
        room.setName(roomName);
        CompEditIOLogic.saveModule(module);
    }
    public static void describeRoom(CompEditContextBean context, String roomDescription)
    {
        CompEditModuleBean module = context.getModule();
        if (module == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_MODULE_LOADED);
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
        room.setDescription(roomDescription);
        CompEditIOLogic.saveModule(module);
    }
    public static void mdRoom(CompEditContextBean context, String key, String val)
    {
        CompEditModuleBean module = context.getModule();
        if (module == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_MODULE_LOADED);
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
        if (room.getParams() == null)
            room.setParams(new JSONObject());
        if (val == null)
            room.getParams().remove(key);
        else if (val.startsWith("{"))
            try
            {
                room.getParams().put(key, JSONUtils.PARSER.parse(val));
            }
            catch (ParseException e)
            {
                room.getParams().put(key, val);
            }
        else
            room.getParams().put(key, val);
        CompEditIOLogic.saveModule(module);
    }
    public static void digRoom(CompEditContextBean context, int dir)
    {
        CompEditModuleBean module = context.getModule();
        if (module == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_MODULE_LOADED);
            context.setError(true);
            return;
        }
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
        String sDir = null;
        String sRevDir = null;
        switch (dir)
        {
            case CompEditOperationBean.NORTH:
                sDir = PRoomBean.DIRS[0];
                sRevDir = PRoomBean.DIRS[1];
                break;
            case CompEditOperationBean.SOUTH:
                sDir = PRoomBean.DIRS[1];
                sRevDir = PRoomBean.DIRS[0];
                break;
            case CompEditOperationBean.EAST:
                sDir = PRoomBean.DIRS[2];
                sRevDir = PRoomBean.DIRS[3];
                break;
            case CompEditOperationBean.WEST:
                sDir = PRoomBean.DIRS[3];
                sRevDir = PRoomBean.DIRS[2];
                break;
        }
        String id = room.getDir(sDir);
        if (!StringUtils.isTrivial(id))
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_CANT_DIG_DIR, sDir);
            context.setError(true);
            return;
        }
        PRoomBean room2 = new PRoomBean();
        room2.setID("room"+System.currentTimeMillis());
        room2.setName("New Room");
        room2.setDescription("This is an empty room.");
        room2.setType(CompRoomBean.TYPE_SCENIC);
        feature.getRooms().add(room2);
        room.setDir(sDir, room2.getID());
        room2.setDir(sRevDir, room.getID());
        CompEditIOLogic.saveModule(module);
        context.getLocation().setModuleID(module.getID());
        context.getLocation().setFeatureID(String.valueOf(feature.getOID()));
        context.getLocation().setRoomID(room2.getID());
        context.getUser().setLocation(context.getLocation().getURI());
        CompEditIOLogic.saveUser(context.getUser());
    }
    public static void unlinkRoom(CompEditContextBean context, int dir)
    {
        CompEditModuleBean module = context.getModule();
        if (module == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_MODULE_LOADED);
            context.setError(true);
            return;
        }
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
        String sDir = null;
        String sRevDir = null;
        switch (dir)
        {
            case CompEditOperationBean.NORTH:
                sDir = PRoomBean.DIRS[0];
                sRevDir = PRoomBean.DIRS[1];
                break;
            case CompEditOperationBean.SOUTH:
                sDir = PRoomBean.DIRS[1];
                sRevDir = PRoomBean.DIRS[0];
                break;
            case CompEditOperationBean.EAST:
                sDir = PRoomBean.DIRS[2];
                sRevDir = PRoomBean.DIRS[3];
                break;
            case CompEditOperationBean.WEST:
                sDir = PRoomBean.DIRS[3];
                sRevDir = PRoomBean.DIRS[2];
                break;
        }
        String id = room.getDir(sDir);
        if (StringUtils.isTrivial(id))
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_CANT_UNDIG_DIR, sDir);
            context.setError(true);
            return;
        }
        PRoomBean room2 = feature.findRoom(room.getDir(id));
        room.setDir(sDir, null);
        if ((room2 != null) && room.getID().equals(room2.getDir(sRevDir)))
            room2.setDir(sRevDir, null);
        CompEditIOLogic.saveModule(module);
    }
    public static void linkRoom(CompEditContextBean context, int dir, String roomID)
    {
        CompEditModuleBean module = context.getModule();
        if (module == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_MODULE_LOADED);
            context.setError(true);
            return;
        }
        PFeatureBean feature = context.getFeature();
        if (feature == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_FEATURE_LOADED);
            context.setError(true);
            return;
        }
        PRoomBean room1 = context.getRoom();
        if (room1 == null)
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_ROOM_LOADED);
            context.setError(true);
            return;
        }
        PRoomBean room2 = feature.findRoom(roomID);
        if ((room2 == null) && !"$exit".equals(roomID))
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_NO_ROOM_LOCATED, roomID);
            context.setError(true);
            return;
        }
        String sDir = null;
        String sRevDir = null;
        switch (dir)
        {
            case CompEditOperationBean.NORTH:
                sDir = PRoomBean.DIRS[0];
                sRevDir = PRoomBean.DIRS[1];
                break;
            case CompEditOperationBean.SOUTH:
                sDir = PRoomBean.DIRS[1];
                sRevDir = PRoomBean.DIRS[0];
                break;
            case CompEditOperationBean.EAST:
                sDir = PRoomBean.DIRS[2];
                sRevDir = PRoomBean.DIRS[3];
                break;
            case CompEditOperationBean.WEST:
                sDir = PRoomBean.DIRS[3];
                sRevDir = PRoomBean.DIRS[2];
                break;
        }
        String id = room1.getDir(sDir);
        if (!StringUtils.isTrivial(id))
        {
            context.addMessage(CompEditModelConst.TEXT_ERROR_CANT_DIG_DIR, sDir);
            context.setError(true);
            return;
        }
        if (room2 == null)
            room1.setDir(sDir, "$exit");
        else
        {
            room1.setDir(sDir, room2.getID());
            room2.setDir(sRevDir, room1.getID());
        }
        CompEditIOLogic.saveModule(module);
    }
}
