package jo.audio.compedit.logic;

import jo.audio.companions.tools.gui.edit.data.PFeatureBean;
import jo.audio.companions.tools.gui.edit.data.PRoomBean;
import jo.audio.compedit.data.CompEditContextBean;
import jo.audio.compedit.data.CompEditIdentBean;
import jo.audio.compedit.data.CompEditLocationBean;
import jo.audio.compedit.data.CompEditModuleBean;
import jo.audio.compedit.data.CompEditOperationBean;
import jo.audio.compedit.data.CompEditUserBean;
import jo.audio.compedit.slu.CompEditModelConst;
import jo.audio.util.ToJSONLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class CompEditOperationLogic
{
    // base function
    
    public static CompEditContextBean operate(CompEditOperationBean op)
    {
        log(ToJSONLogic.toJSON(op).toJSONString());
        final CompEditContextBean context = new CompEditContextBean();
        initializeCompContext(op, context);
        switch (op.getOperation())
        {
            case CompEditOperationBean.QUERY:
                break;
            case CompEditOperationBean.MOVE:
                UserLogic.move(context, (int)op.getNumParam1());
                break;
            case CompEditOperationBean.SELECT_FEATURE:
                UserLogic.selectFeature(context, op.getNumParam1());
                break;
            case CompEditOperationBean.NEW_FEATURE:
                ModelLogic.newFeature(context, op.getStrParam1());
                break;
            case CompEditOperationBean.NAME_FEATURE:
                ModelLogic.nameFeature(context, op.getStrParam1());
                break;
            case CompEditOperationBean.SET_LOCATION:
                ModelLogic.setLocation(context, (int)op.getNumParam1(), (int)op.getNumParam2());
                break;
            case CompEditOperationBean.SET_DIMENSION:
                ModelLogic.setDimension(context, (int)op.getNumParam1());
                break;
            case CompEditOperationBean.SET_ENABLEDBY:
                ModelLogic.setEnabledBy(context, op.getStrParam1());
                break;
            case CompEditOperationBean.NAME_ROOM:
                ModelLogic.nameRoom(context, op.getStrParam1());
                break;
            case CompEditOperationBean.MD_ROOM:
                ModelLogic.mdRoom(context, op.getStrParam1(), op.getStrParam2());
                break;
            case CompEditOperationBean.DESCRIBE_ROOM:
                ModelLogic.describeRoom(context, op.getStrParam1());
                break;
            case CompEditOperationBean.DIG_ROOM:
                ModelLogic.digRoom(context, (int)op.getNumParam1());
                break;
            case CompEditOperationBean.LINK_ROOM:
                ModelLogic.linkRoom(context, (int)op.getNumParam1(), op.getStrParam1());
                break;
            case CompEditOperationBean.UNLINK_ROOM:
                ModelLogic.unlinkRoom(context, (int)op.getNumParam1());
                break;
            case CompEditOperationBean.DEBUG:
                CompEditOperationLogic.debug(context, op.getStrParam1());
                break;
            default:
                context.setLastOperationError(CompEditModelConst.TEXT_UNKNOWN_OPERATION_XXX, op.getOperation());
        }
        fillContext(context);
        return context;
    }

    public static void initializeCompContext(CompEditOperationBean op,
            final CompEditContextBean context)
    {
        CompEditIdentBean id = IdentLogic.getIdent(op.getIdentID());
        if (id == null)
        {
            id = IdentLogic.newInstance(op.getIdentID(), op.getStrParam2(), op.getStrParam3());
            log("Creating new ident="+op.getIdentID());
            log("with name="+op.getStrParam2()+", email="+op.getStrParam3());
        }
        else
        {
            IdentLogic.updateIdent(id, op.getStrParam3(), op.getStrParam2(), null);
            log("Updating ident="+op.getIdentID());
            //log("with name="+op.getStrParam2()+", email="+op.getStrParam3());
        }
        CompEditUserBean acct = UserLogic.getUser(id.getEmail() != null ? id.getEmail() : id.getUserID());
        if (acct == null)
        {
            acct = UserLogic.newInstance(id);
            log("Creating new account with id="+id.getUserID());
        }
        else
            log("Retrieved account with id="+id.getUserID());
        context.setUser(acct);
        fillContextLocation(context, acct);
        context.setID(id);
        context.setLastOperation(op);
    }

    public static void fillContextLocation(final CompEditContextBean context,
            CompEditUserBean user)
    {
        log("user loc="+user.getLocation());
        CompEditLocationBean loc = new CompEditLocationBean(user.getLocation());
        if (loc.equals(context.getLocation()))
            return;
        context.setLocation(loc);
        if (!StringUtils.isTrivial(loc.getModuleID()))
        {
            log("load module="+loc.getModuleID());
            CompEditModuleBean module = CompEditIOLogic.getModuleFromURI(loc.getModuleID());
            context.setModule(module);
            if (module != null)
            {
                log("loaded module");
                log("load feature="+loc.getFeatureID());
                for (PFeatureBean feature : module.getFeatures())
                    if (String.valueOf(feature.getOID()).equals(loc.getFeatureID()))
                    {
                        context.setFeature(feature);
                        log("loaded feature");
                        break;
                    }
                if (context.getFeature() != null)
                {
                    log("load room="+loc.getRoomID());
                    for (PRoomBean room : context.getFeature().getRooms())
                        if (String.valueOf(room.getID()).equals(loc.getRoomID()))
                        {
                            context.setRoom(room);
                            log("loaded room");
                            break;
                        }
                    if (context.getRoom() == null)
                        log("no room loaded");
                }
                else
                    log("no feature loaded");
            }
            else
                log("no module loaded");
        }
    }
    
    public static void fillContext(CompEditContextBean context)
    {
        if (context.getUser() == null)
            return;
        fillContextLocation(context, context.getUser());
    }
    
    private static void log(String msg)
    {
        DebugUtils.trace(msg);
    }
    
    private static void debug(CompEditContextBean context, String command)
    {
    }
}
