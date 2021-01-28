package jo.audio.compedit.app.logic;

import jo.audio.companions.data.build.PFeatureBean;
import jo.audio.compedit.data.CompEditContextBean;
import jo.audio.compedit.data.CompEditOperationBean;
import jo.audio.compedit.data.CompEditState;
import jo.audio.compedit.logic.CompEditOperationLogic;
import jo.audio.util.model.data.AudioMessageBean;

public class OperationLogic
{
    // wrapper functions
    public static CompEditContextBean query(CompEditState state, String linkedName, String linkedEmail)
    {
        return doOperation(state, CompEditOperationBean.QUERY, null, linkedName, linkedEmail, null, 0, 0, 0, 0);
    }
    
    public static CompEditContextBean queryRanks(CompEditState state, String linkedName, String linkedEmail)
    {
        return doOperation(state, CompEditOperationBean.QUERY, null, linkedName, linkedEmail, null, 0, 0, 0, 0);
    }
    
    public static CompEditContextBean move(CompEditState state, int dir)
    {
        return doOperation(state, CompEditOperationBean.MOVE, null, null, dir, 0);
    }
    
    public static CompEditContextBean debug(CompEditState state, String command)
    {
        return doOperation(state, CompEditOperationBean.DEBUG, command, null, 0, 0);
    }

    public static CompEditContextBean selectFeature(CompEditState state, PFeatureBean feature)
    {
        return doOperation(state, CompEditOperationBean.SELECT_FEATURE, null, null, feature.getOID(), -1);
    }

    public static CompEditContextBean newFeature(CompEditState state, String text)
    {
        return doOperation(state, CompEditOperationBean.NEW_FEATURE, text, null, -1, -1);
    }

    public static CompEditContextBean setFeatureName(CompEditState state, String text)
    {
        return doOperation(state, CompEditOperationBean.NAME_FEATURE, text, null, -1, -1);
    }

    public static CompEditContextBean setRoomName(CompEditState state, String text)
    {
        return doOperation(state, CompEditOperationBean.NAME_ROOM, text, null, -1, -1);
    }

    public static CompEditContextBean setRoomMetadata(CompEditState state, String key, Object value)
    {
        return doOperation(state, CompEditOperationBean.MD_ROOM, key, (value != null) ? value.toString() : null, -1, -1);
    }

    public static CompEditContextBean setRoomDescription(CompEditState state, String text)
    {
        return doOperation(state, CompEditOperationBean.DESCRIBE_ROOM, text, null, -1, -1);
    }

    public static CompEditContextBean digRoom(CompEditState state, int dir)
    {        
        return doOperation(state, CompEditOperationBean.DIG_ROOM, null, null, dir, -1);
    }
    public static CompEditContextBean linkRoom(CompEditState state, int dir, String roomID)
    {        
        return doOperation(state, CompEditOperationBean.LINK_ROOM, roomID, null, dir, -1);
    }
    public static CompEditContextBean unlinkRoom(CompEditState state, int dir)
    {        
        return doOperation(state, CompEditOperationBean.UNLINK_ROOM, null, null, dir, -1);
    }
    
    public static CompEditContextBean setLocation(CompEditState state, int x, int y)
    {
        return doOperation(state, CompEditOperationBean.SET_LOCATION, null, null, x, y);
    }
    public static CompEditContextBean setDimension(CompEditState state, int z)
    {
        return doOperation(state, CompEditOperationBean.SET_DIMENSION, null, null, z, 0);
    }
    public static CompEditContextBean setEnabledBy(CompEditState state, String enableBy)
    {
        return doOperation(state, CompEditOperationBean.SET_ENABLEDBY, enableBy, null, 0, 0);
    }
    
    private static CompEditContextBean doOperation(CompEditState state, int id, String str1, String str2, long num1, long num2)
    {
        return doOperation(state, id, str1, str2, null, null, num1, num2, 0, 0);
    }
    private static CompEditContextBean doOperation(CompEditState state, int id, String str1, String str2, String str3, String str4, long num1, long num2, long num3, long num4)
    {
        String identID = state.getUserID();
        CompEditOperationBean op = new CompEditOperationBean();
        op.setOperation(id);
        op.setIdentID(identID);
        op.setStrParam1(str1);
        op.setStrParam2(str2);
        op.setStrParam3(str3);
        op.setStrParam4(str4);
        op.setNumParam1(num1);
        op.setNumParam2(num2);
        op.setNumParam3(num3);
        op.setNumParam4(num4);
        CompEditContextBean context = CompEditOperationLogic.operate(op);
        state.setContext(context);
        for (AudioMessageBean resp : context.getMessages())
            state.getApplication().log("OperationLogic - reply = "+resp);
        state.respond(context.getMessages());
        return context;
    }
}
