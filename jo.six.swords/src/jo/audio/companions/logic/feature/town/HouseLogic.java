package jo.audio.companions.logic.feature.town;

import java.util.List;
import java.util.Random;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.logic.CompIOLogic;
import jo.audio.companions.logic.ExperienceLogic;
import jo.audio.companions.logic.feature.DigOptions;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.BooleanUtils;

public class HouseLogic
{
    static void addVacantLot(FeatureBean feature, Random rnd, List<DigOptions> sites)
    {
        System.out.println("House at "+feature.getLocation());
        DigOptions site = sites.get(rnd.nextInt(sites.size()));
        sites.remove(site);
        int north = site.dir;
        //int south = TownLogic.opposite(site.dir);
        int west = TownLogic.left(site.dir);
        int east = TownLogic.right(site.dir);
    
        CompRoomBean houseEntrance = TownLogic.extendRoom(feature, site.from, "houseEntrance", north);
        CompRoomBean houseHall = TownLogic.extendRoom(feature, houseEntrance, "houseHall", north);
        CompRoomBean at = houseHall;
        for (int i = 0; i < CompItemTypeBean.TYPES.length; i += 2)
        {
            at = TownLogic.extendRoom(feature, at, "houseSortHall", north);
            addStoreType(feature, at, west, CompItemTypeBean.TYPES[i]);
            if (i + 1 < CompItemTypeBean.TYPES.length)
                addStoreType(feature, at, east, CompItemTypeBean.TYPES[i+1]);
        }
    }

    private static void addStoreType(FeatureBean feature, CompRoomBean hall, int dir, int type)
    {
        CompRoomBean store = TownLogic.extendRoom(feature, hall, "houseSortRoom", dir);
        store.getName().setArgs(new Object[] { new AudioMessageBean("TYPE_"+CompItemTypeBean.TYPE_NAMES[type]) });
        store.getDescription().setArgs(new Object[] { new AudioMessageBean("TYPE_"+CompItemTypeBean.TYPE_NAMES[type]) });
        store.getParams().put("itemType", type);
    }

    private static boolean isOwner(CompContextBean context)
    {
        String key = getOwnerKey(context);
        boolean owner = BooleanUtils.parseBoolean(context.getUser().getMetadata().get(key));
        DebugUtils.trace("HouseLogic.isOwner, owner("+key+")="+owner);
        return owner;
    }

    public static String getOwnerKey(CompContextBean context)
    {
        String key = "OWNER_"+context.getLocation().toString();
        int o = key.indexOf("/houseEntrance");
        if (o > 0)
        {
            key = key.substring(0, o);
            CompRoomBean room = context.getRoom();
            for (int dir = 0; dir < 4; dir++)
                if ((room.getDirection(dir) != null) && room.getDirection(dir).startsWith("houseHall"))
                {
                    key += "/"+room.getDirection(dir);
                    break;
                }
        }
        return key;
    }
    
    private static void setOwner(CompContextBean context)
    {
        String key = getOwnerKey(context);
        context.getUser().getMetadata().put(key, Boolean.TRUE);
    }
    
    public static void postEnterEntrance(CompContextBean context)
    {
        boolean owner = isOwner(context);
        if (owner)
            context.addMessage(CompanionsModelConst.TEXT_WELCOME_TO_YOUR_HOME);
        else
            context.addMessage(CompanionsModelConst.TEXT_YOU_CAN_PURCHASE_THIS_HOME_BY_CONTINUING_INWARDS);
    }

    public static void postEnterHall(CompContextBean context)
    {
        boolean owner = isOwner(context);
        if (!owner)
        {
            ExperienceLogic.addGold(context.getUser(), -10000);
            setOwner(context);
            CompIOLogic.saveUser(context.getUser());
            context.addMessage(CompanionsModelConst.TEXT_CONGRATULATIONS_THE_HOUSE_IS_NOW_YOURS);
        }
    }
}
