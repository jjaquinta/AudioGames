package jo.audio.companions.logic.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

import jo.audio.common.logic.io.DriverLogic;
import jo.audio.companions.data.CompCompanionBean;
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

public class CastleLogic
{
    public static void equipCastle(CompContextBean context, CompUserBean user,
            CompItemInstanceBean item)
    {
        CompOperationLogic.fillContext(context);
        if ((context.getFeature() != null) && (context.getRoom() != null))
            queryExtendCastle(context, user, item);
        else
            queryCreateCastle(context, user, item);
    }
    
    private static void queryExtendCastle(CompContextBean context, CompUserBean user,
            CompItemInstanceBean item)
    {
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
        CompRoomBean r = FeatureLogic.getRoom(item.getType().getID());
        if (r == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ROOM_ID_SPECIFIED_XXX, item.getType().getID());
            context.setError(true);
            return;
        }
        AudioMessageBean q = new AudioMessageBean(CompanionsModelConst.TEXT_PLEASE_SAY_WHICH_DIRECTION_YOU_WOULD_LIKE_TO_ADD_A_XXX_IN, 
                r.getName());

        JSONObject question = new JSONObject();
        question.put(QueryLogic.QUERY_TEXT, q.toJSON());
        question.put(QueryLogic.QUERY_TYPE, QueryLogic.QUERY_TYPE_NSEW);
        question.put(QueryLogic.QUERY_ACTION, "createRoom");
        question.put(QueryLogic.QUERY_ID, item.getID());
        if (user.getMetadata() == null)
            user.setMetadata(new JSONObject());
        user.getMetadata().put(CompUserBean.META_QUESTION, question);
        //context.addMessage(q);
        CompIOLogic.saveUser(user);
        context.setError(false);
    }
    
    private static void queryCreateCastle(CompContextBean context, CompUserBean user,
            CompItemInstanceBean item)
    {
        if (context.getFeature() != null)
        {
            if (user.getURI().equals(context.getFeature().getFeature().getAccount()))
            {
                context.addMessage(CompanionsModelConst.TEXT_YOU_HAVE_ALREADY_CREATED_A_CASTLE_HERE);
                return;
            }
            context.addMessage(CompanionsModelConst.TEXT_YOU_CANT_CREATE_A_CASTLE_HERE,
                    context.getFeature().getFeature().getName());
            return;
        }
        CompRoomBean r = FeatureLogic.getRoom(item.getType().getID());
        if (r == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ROOM_ID_SPECIFIED_XXX, item.getType().getID());
            context.setError(true);
            return;
        }
        AudioMessageBean q = new AudioMessageBean(CompanionsModelConst.TEXT_XXX_WILL_BE_THE_FIRST_ROOM_OF_YOUR_CASTLE, 
                r.getName(), r.getName());

        JSONObject question = new JSONObject();
        question.put(QueryLogic.QUERY_TEXT, q.toJSON());
        question.put(QueryLogic.QUERY_TYPE, QueryLogic.QUERY_TYPE_YESNO);
        question.put(QueryLogic.QUERY_ACTION, "createCastle");
        question.put(QueryLogic.QUERY_ID, item.getID());
        if (user.getMetadata() == null)
            user.setMetadata(new JSONObject());
        user.getMetadata().put(CompUserBean.META_QUESTION, question);
        //context.addMessage(q);
        CompIOLogic.saveUser(user);
        FeatureLogic.readDynamicModules(true);
        context.setError(false);
    }
    
    public static void doYesCreateCastle(CompContextBean context, String id)
    {
        CompUserBean user = context.getUser();
        CompItemInstanceBean item = user.getItem(id);
        if (item == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ITEM_ID_SPECIFIED_XXX, id);
            context.setError(true);
            return;
        }
        CompRoomBean r = FeatureLogic.getRoom(item.getType().getID());
        if (r == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ROOM_ID_SPECIFIED_XXX, item.getType().getID());
            context.setError(true);
            return;
        }
        PRoomBean room = new PRoomBean();
        room.setID(r.getID());
        room.setName("{{"+r.getName().getIdent()+"}}");
        room.setDescription("{{"+r.getDescription().getIdent()+"}}");
        room.setType(r.getType());
        room.setParams(r.getParams());
        
        String moduleID = user.getURI();
        CompEditModuleBean module = CompEditIOLogic.getModuleFromURI(moduleID);
        if (module == null)
        {
            module = new CompEditModuleBean();
            module.setID("compmodule://"+user.getURI().substring(11));
            module.setAuthor(user.getURI());
            module.setAccount(user.getURI());
            module.setName("User Castles");
            module.setText(new HashMap<String, Map<String,String[]>>());
            module.setEnabledBy("true");
            DebugUtils.trace("Creating castle in new module");
        }
        else
            DebugUtils.trace("Creating castle in existing module");
        PFeatureBean feature = new PFeatureBean();
        feature.setName(getFeatureName(module, user));
        feature.setEnabledBy("true");
        feature.setLocation(user.getLocation());
        feature.getRooms().add(room);
        feature.setEntranceID(room.getID());
        room.setSouth("$exit");
        module.getFeatures().add(feature);
        DebugUtils.trace("Saving module");
        DriverLogic.setSingleThreaded(true);
        CompEditIOLogic.saveModule(module);
        DriverLogic.setSingleThreaded(false);
        FeatureLogic.readDynamicModules(true);
        context.setNewText(true);
        context.addMessage(CompanionsModelConst.TEXT_WOOSH_A_XXX_APPEARS_BEFORE_YOU, r.getName());

        UserLogic.doRemoveItem(user, item.getID(), 1);
        user.getMetadata().remove(CompUserBean.META_QUESTION);
        DebugUtils.trace("Saving user");
        CompIOLogic.saveUser(user);
        CompIOLogic.logUse(user, item.getID(), 1);
        context.setError(false);
    }
    
    private static String getFeatureName(CompEditModuleBean module, CompUserBean user)
    {
        List<CompCompanionBean> chars = new ArrayList<>();
        chars.addAll(user.getCompanions());
        Collections.sort(chars, new Comparator<CompCompanionBean>() {
            @Override
            public int compare(CompCompanionBean o1, CompCompanionBean o2)
            {
                return o2.getLevel() - o1.getLevel();
            }
        });
        List<String> names = new ArrayList<>();
        if (!StringUtils.isTrivial(user.getSupportIdent()))
            names.add(user.getSupportIdent());
        for (CompCompanionBean c : chars)
            names.add(c.getName());
        Set<String> taken = new HashSet<>();
        for (PFeatureBean feature : module.getFeatures())
            taken.add(feature.getName());
        // TODO: localize
        String name = findNotTaken("Castle %s", names, taken);
        if (name == null)
            name = findNotTaken("Keep of %s", names, taken);
        if (name == null)
            name = findNotTaken("%s's Tower", names, taken);
        if (name == null)
            name = "Yet Aother Castle";
        return name;
    }
    
    private static String findNotTaken(String fmt, List<String> chars, Set<String> taken)
    {
        for (String c : chars)
        {
            String name = String.format(fmt, c);
            if (!taken.contains(name))
                return name;
        }
        return null;
    }
    
    public static void doNoCreateCastle(CompContextBean context, String id)
    {
        CompUserBean user = context.getUser();
        CompItemInstanceBean item = user.getItem(id);
        if (item == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ITEM_ID_SPECIFIED_XXX, id);
            context.setError(true);
            return;
        }
        context.addMessage(CompanionsModelConst.TEXT_YOU_SAVE_THE_XXX_TO_USE_LATER, item.getFullName());
        user.getMetadata().remove(CompUserBean.META_QUESTION);
        CompIOLogic.saveUser(user);
        context.setError(false);
    }

    public static void doDirCreateRoom(CompContextBean context, String id,
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
        CompRoomBean r = FeatureLogic.getRoom(item.getType().getID());
        if (r == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ROOM_ID_SPECIFIED_XXX, item.getType().getID());
            context.setError(true);
            return;
        }
        PRoomBean currentRoomDesign = null;
        for (PRoomBean cr : feature.getRooms())
            if (cr.getID().equals(currentRoomLive.getID()))
            {
                currentRoomDesign = cr;
                break;
            }
        if (currentRoomDesign == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ROOM_ID_SPECIFIED_XXX, item.getType().getID());
            context.setError(true);
            return;
        }
        PRoomBean room = new PRoomBean();
        room.setID(r.getID()+feature.getRooms().size());
        room.setName("{{"+r.getName().getIdent()+"}}");
        room.setDescription("{{"+r.getDescription().getIdent()+"}}");
        room.setType(r.getType());
        room.setParams(r.getParams());

        feature.getRooms().add(room);
        currentRoomDesign.setDir(rdir, room.getID());
        room.setDir(CompRoomBean.opposite(rdir), currentRoomDesign.getID());
        DebugUtils.trace("Saving module");
        DebugUtils.trace(module.toJSON().toJSONString());
        DriverLogic.setSingleThreaded(true);
        CompEditIOLogic.saveModule(module);
        DriverLogic.setSingleThreaded(false);
        FeatureLogic.readDynamicModules(true);
        context.setNewText(true);
        context.addMessage(CompanionsModelConst.TEXT_WOOSH_A_XXX_APPEARS_TO_THE_YYY, r.getName(), "{{"+sdir+"}}");

        UserLogic.doRemoveItem(user, item.getID(), 1);
        user.getMetadata().remove(CompUserBean.META_QUESTION);
        DebugUtils.trace("Saving user");
        CompIOLogic.saveUser(user);
        CompIOLogic.logUse(user, item.getID(), 1);
        context.setError(false);
    }

}
