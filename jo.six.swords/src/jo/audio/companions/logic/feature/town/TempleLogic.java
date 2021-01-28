package jo.audio.companions.logic.feature.town;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.app.CompApplicationHandler;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.GeoBean;
import jo.audio.companions.data.MissionBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.data.build.PFeatureBean;
import jo.audio.companions.data.build.PRoomBean;
import jo.audio.companions.logic.BadgeLogic;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.CompIOLogic;
import jo.audio.companions.logic.ExperienceLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.audio.companions.logic.UserLogic;
import jo.audio.companions.logic.feature.DigOptions;
import jo.audio.companions.logic.feature.PantheonLogic;
import jo.audio.companions.logic.gen.MissionLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.compedit.data.CompEditModuleBean;
import jo.audio.compedit.logic.CompEditIOLogic;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.logic.ModelResolveLogic;
import jo.util.utils.obj.IntegerUtils;

public class TempleLogic
{
    private static final String TO_POOL_LOCK = "{\"expr\":\"@getValue(GOD_?0?) <  1\",\"trueMessage\":{\"message\":\"TEMPLE_BLOCK\"}}";
    private static final String AT_POOL_ENTER = "{\"if\":[\"getValue(GOD_?0?)\", \">\", 0],\"then\":{\"if\":\"cureAll\",\"then\":\"respond(GOLDEN_GLOW)\"}}";
    private static final String TO_INFIRMARY_LOCK = "{\"expr\":\"@getValue(GOD_?0?) <  3\",\"trueMessage\":{\"message\":\"TEMPLE_BLOCK\"}}";
    private static final String AT_INFIRMARY_ENTER = "{\"if\":[\"getValue(GOD_?0?)\", \">\", 0],\"then\":{\"if\":\"healAll\",\"then\":\"respond(GOLDEN_GLOW)\"}}";
    private static final String TO_RECRUIT1_LOCK = "{\"expr\":\"@getValue(GOD_?0?) <  3\",\"trueMessage\":{\"message\":\"TEMPLE_BLOCK\"}}";
    private static final String AT_RECRUIT1_ENTER = "{\"if\":[\"getValue(PATRON_DEITY)\", \"!=\", ?0?],\"then\":\"respond(If you want to take XXX as your patron deity, {{A_GOD#?0?}})\"}";
    private static final String AT_RECRUIT2_ENTER = "{\"if\":[\"getValue(PATRON_DEITY)\", \"!=\", ?0?],\"then\":[\"function(patronize, ?0?)\"]}";
    private static final String TO_NAVE_LOCK = "{\"expr\":\"@getValue(PATRON_DEITY) !=  ?0?\",\"trueMessage\":{\"message\":\"TEMPLE_BLOCK\"}}";
    private static final String AT_TITHE2_ENTER = "{\"cmd\": \"function(tithe, ?0?)\"}";
    private static final String AT_MISSION_ENTER = "{\"cmd\": \"function(templeMissionStart, ?0?)\"}";
    private static final String AT_PRESTIGE_ENTER = "{\"cmd\": \"function(templePrestigeReport, ?0?)\"}";

    static void addTemples(SquareBean sq, FeatureBean feature, Random rnd,
            List<Integer> temples, List<DigOptions> sites)
    {
        while (temples.size() > 0)
        {
            int templeType = temples.get(0);
            temples.remove(0);
            DigOptions site = sites.get(rnd.nextInt(sites.size()));
            sites.remove(site);
    
            List<String> expansions = new ArrayList<>();
            expansions.add(String.valueOf(templeType));
            CompRoomBean temple = TownLogic.extendRoom(feature, site.from, "temple"+templeType, site.dir, expansions);

            if (isPool(sq, rnd, templeType))
            {
                temple.setDirectionLock(site.dir, constitute(TO_POOL_LOCK, templeType));
                CompRoomBean templePool = TownLogic.extendRoom(feature, temple, "templePool_"+templeType+",templePool", site.dir, expansions);
                templePool.putParam(CompRoomBean.MD_POST_ENTER, constitute(AT_POOL_ENTER, templeType));

                if (isInfirmary(sq, rnd, templeType))
                {
                    templePool.setDirectionLock(site.dir, constitute(TO_INFIRMARY_LOCK, templeType));
                    CompRoomBean templeInfirmary = TownLogic.extendRoom(feature, templePool, "templeInfirmary_"+templeType+",templeInfirmary", site.dir, expansions);
                    templeInfirmary.putParam(CompRoomBean.MD_POST_ENTER, constitute(AT_INFIRMARY_ENTER, templeType));
                }
            }
            int left = TownLogic.left(site.dir);
            if (isConfessional(sq, rnd, templeType))
            {
                temple.setDirectionLock(left, constitute(TO_RECRUIT1_LOCK, templeType));
                CompRoomBean templeRecruit1 = TownLogic.extendRoom(feature, temple, "templeRecruit1_"+templeType+",templeRecruit1", left, expansions);
                templeRecruit1.putParam(CompRoomBean.MD_POST_ENTER, constitute(AT_RECRUIT1_ENTER, templeType));
                
                CompRoomBean templeRecruit2 = TownLogic.extendRoom(feature, templeRecruit1, "templeRecruit2_"+templeType+",templeRecruit2", left, expansions);
                templeRecruit2.putParam(CompRoomBean.MD_POST_ENTER, constitute(AT_RECRUIT2_ENTER, templeType));
            }
            int right = TownLogic.right(site.dir);
            if (isNave(sq, rnd, templeType))
            {
                int opposite = TownLogic.opposite(site.dir);
                temple.setDirectionLock(right, constitute(TO_NAVE_LOCK, templeType));
                CompRoomBean nave1 = TownLogic.extendRoom(feature, temple, "templeNave"+templeType+",templeNave", right, expansions);
                
                CompRoomBean templeTithe1 = TownLogic.extendRoom(feature, nave1, "templeTithe1_"+templeType+",templeTithe1", site.dir, expansions);
                
                CompRoomBean templeTithe2 = TownLogic.extendRoom(feature, templeTithe1, "templeTithe2_"+templeType+",templeTithe2", site.dir, expansions);
                templeTithe2.putParam(CompRoomBean.MD_POST_ENTER, constitute(AT_TITHE2_ENTER, templeType));
                
                CompRoomBean templeMission = TownLogic.extendRoom(feature, nave1, "templeMission_"+templeType+",templeMission", opposite, expansions);
                templeMission.putParam(CompRoomBean.MD_POST_ENTER, constitute(AT_MISSION_ENTER, templeType));

                CompRoomBean nave2 = TownLogic.extendRoom(feature, nave1, "templeNave"+templeType+",templeNave", right, expansions);
                CompRoomBean templePrestige1 = TownLogic.extendRoom(feature, nave2, "templePrestige1_"+templeType+",templePrestige1", site.dir, expansions);
                templePrestige1.putParam(CompRoomBean.MD_POST_ENTER, constitute(AT_PRESTIGE_ENTER, templeType));

            }
        }
    }

    private static final float[] POOL_CHANCES = { .50f, .75f, .87f, .93f };
    
    private static boolean isPool(SquareBean sq, Random rnd, int god)
    {
        int mag = sq.getFeature() - CompConstLogic.FEATURE_HAMLET; // 0 = HAMLET, 3 = CITY
        float chance = POOL_CHANCES[mag];
        chance *= PantheonLogic.GOD_POWER[god]/100.0f;
        return rnd.nextFloat() < chance;
    }

    private static final float[] INF_CHANCES = { .25f, .50f, .75f, .93f };

    private static boolean isInfirmary(SquareBean sq, Random rnd, int god)
    {
        int mag = sq.getFeature() - CompConstLogic.FEATURE_HAMLET; // 0 = HAMLET, 3 = CITY
        float chance = INF_CHANCES[mag];
        chance *= PantheonLogic.GOD_POWER[god]/100.0f;
        return rnd.nextFloat() < chance;
    }

    private static final float[] CONF_CHANCES = { .25f, .50f, .75f, 1.00f };

    private static boolean isConfessional(SquareBean sq, Random rnd, int god)
    {
        if (sq.getOrds().getZ() != 2)
            return false;
        int mag = sq.getFeature() - CompConstLogic.FEATURE_HAMLET; // 0 = HAMLET, 3 = CITY
        float chance = CONF_CHANCES[mag];
        chance *= PantheonLogic.GOD_POWER[god]/100.0f;
        return rnd.nextFloat() < chance;
    }

    private static final float[] NAVE_CHANCES = { .50f, .75f, .90f, 1.00f };

    private static boolean isNave(SquareBean sq, Random rnd, int god)
    {
        if (sq.getOrds().getZ() != 2)
            return false;
        int mag = sq.getFeature() - CompConstLogic.FEATURE_HAMLET; // 0 = HAMLET, 3 = CITY
        float chance = NAVE_CHANCES[mag];
        chance *= PantheonLogic.GOD_POWER[god]/100.0f;
        return rnd.nextFloat() < chance;
    }
    
    private static JSONObject constitute(String jsonStr, int templeType)
    {
        jsonStr = jsonStr.replace("?0?", String.valueOf(templeType));
        JSONObject json = JSONUtils.readJSONString(jsonStr);
        return json;
    }

    static List<Integer> determineTemples(RegionBean region, SquareBean sq, int type,
            Random rnd)
    {
        List<Integer> temples = new ArrayList<>();
        int numTemples = 0;
        switch (type)
        {
            case CompConstLogic.FEATURE_HAMLET:
                numTemples = rnd.nextInt(2);
                break;
            case CompConstLogic.FEATURE_VILLAGE:
                numTemples = 1 + rnd.nextInt(2);
                break;
            case CompConstLogic.FEATURE_TOWN:
                numTemples = 2 + rnd.nextInt(3);
                break;
            case CompConstLogic.FEATURE_CITY:
                numTemples = 3 + rnd.nextInt(4);
                break;
        }
        if (numTemples > 0)
        {
            int[] gods = PantheonLogic.getGods(sq.getDemense());
            for (int g = gods[0]; g < gods[1]; g++)
                temples.add(g);
            while (temples.size() > numTemples)
            {
                int r = Math.max(rnd.nextInt(temples.size()), rnd.nextInt(temples.size()));
                temples.remove(r);
            }
        }
        return temples;
    }

    public static void patronize(CompContextBean context, int god)
    {
        CompUserBean user = context.getUser();
        context.getUser().getMetadata().put("PATRON_DEITY", String.valueOf(god));
        context.addMessage(CompanionsModelConst.TEXT_XXX_IS_NOW_YOUR_PATRON_DEITY, "{{A_GOD#"+god+"}}");
        MissionBean mission = MissionLogic.getMission(user, "temple/mission/");
        if (mission != null)
            clearMission(user, mission);
        CompIOLogic.saveUser(user);
    }
    
    public static void tithe(CompContextBean context, int god)
    {
        CompUserBean user = context.getUser();
        float gold = user.getGoldPieces();
        if (gold < 10000.0)
        {
            context.addMessage(CompanionsModelConst.TEXT_THANKS_FOR_THE_OFFER_BUT_XXX_FEELS_YOU_NEED_THE_MONEY_MORE,
                    "{{A_GOD#"+god+"}}");
            return;
        }
        int prestige = IntegerUtils.parseInt(user.getMetadata().get("PRESTIGE_"+god));        
        int amount = (int)(gold/10);
        prestige += CompConstLogic.PRESTIGE_TITHE;
        ExperienceLogic.addGold(user, -amount);
        user.getMetadata().put("PRESTIGE_"+god, prestige);
        context.addMessage(CompanionsModelConst.TEXT_XXX_THANKS_YOU_FOR_YOUR_GENEROUS_DONATION,
                "{{A_GOD#"+god+"}}");
        BadgeLogic.updateBadges(context, user);
        CompIOLogic.saveUser(user);
    }
    
    public static void prestige(CompContextBean context, int god)
    {
        CompUserBean user = context.getUser();
        int blessings = IntegerUtils.parseInt(user.getMetadata().get("GOD_"+god));        
        int prestige = IntegerUtils.parseInt(user.getMetadata().get("PRESTIGE_"+god));        
        context.addMessage(CompanionsModelConst.TEXT_YOU_CONTEMPLATE_XXX_AND_IT_IS_REVEALED_TO_YOU_THAT_YOU_HAVE_GAINED_YYY_PRESTIGE,
                "{{A_GOD#"+god+"}}", blessings, prestige);
    }

    public static void startMission(CompContextBean context, int god)
    {
        CompUserBean user = context.getUser();
        MissionBean mission = MissionLogic.getMission(user, "temple/mission/");
        if (mission != null)
        {
            if (mission.getMessage() != null)
                context.addMessage(mission.getMessage());
            return;
        }
        switch(BaseUserState.RND.nextInt(2))
        {
            case 0: // hermit mission
                missionHermitStart(context, user, god);
                break;
            case 1: // demon mission
                missionDemonStart(context, user, god);
                break;
        }
    }

    public static void endMission(CompContextBean context, int god)
    {
        CompUserBean user = context.getUser();
        MissionBean mission = MissionLogic.getMission(user, "temple/mission/");
        if (mission == null)
            return;
        if (!user.getLocation().startsWith(mission.getLocation().toString()))
            return;
        switch(mission.getType())
        {
            case "temple/mission/hermit": 
                missionHermitEnd(context, user, god, mission);
                break;
            case "temple/mission/demon": 
                missionDemonEnd(context, user, god, mission);
                break;
        }
    }
    
    private static void missionHermitStart(CompContextBean context, CompUserBean user, int god)
    {
        GeoBean loc = new GeoBean(user.getLocation());
        SquareBean sq = getNearbyLocation(loc, 3, 8);
        MissionBean mission = new MissionBean();
        mission.setType("temple/mission/hermit/"+System.currentTimeMillis());
        mission.setType("temple/mission/hermit");
        mission.setLocation(new GeoBean(sq.getOrds()));
        mission.setModule("compmodule://god:"+god+"/temple/mission/hermit");
        mission.setFeature("Hermit Missions for "+god);
        mission.setExpiryDate(System.currentTimeMillis() + 24*60*60*1000L); // one day
        CompEditModuleBean mod = CompEditIOLogic.getModuleFromURI(mission.getModule());
        if (mod == null)
        {
            mod = new CompEditModuleBean();
            mod.setID(mission.getModule());
            mod.setAccount("SYS");
            mod.setAuthor("System");
            mod.setName(mission.getFeature());
        }
        PFeatureBean feature = new PFeatureBean();
        feature.setParams(new JSONObject());
        feature.getParams().put("expiryDate", mission.getExpiryDate());
        feature.getParams().put("user", user.getURI());
        feature.setLocation(mission.getLocation().toString());
        feature.setName("Hermitage");
        CompRoomBean hermitage = FeatureLogic.getRoom("templeHermitage");
        PRoomBean room = new PRoomBean();
        JSONObject json = hermitage.toJSON();
        room.fromJSON(mod, json);
        JSONUtils.replace(json, "?0?", String.valueOf(god));
        room.setName(ModelResolveLogic.resolve(CompApplicationHandler.getInstance().getModel(), 
                "en_US", 
                BaseUserState.RND, 
                context, 
                hermitage.getName().getIdent(), 
                hermitage.getName().getArgs()));
        room.setDescription(ModelResolveLogic.resolve(CompApplicationHandler.getInstance().getModel(), 
                "en_US", 
                BaseUserState.RND, 
                context, 
                hermitage.getDescription().getIdent(), 
                hermitage.getDescription().getArgs()));
        feature.getRooms().add(room);
        feature.setEntranceID(room.getID());       
        mod.getFeatures().add(feature);        
        CompEditIOLogic.saveModule(mod);
        FeatureLogic.readDynamicModules(true);
        AudioMessageBean msg = new AudioMessageBean(CompanionsModelConst.TEXT_YOUR_MISSION_IS_TO_DELIVER_A_MESSAGE_TO_A_HERMIT_OF_XXX_AT_YYY_LONGITUDE_ZZZ_LATITUDE,
                "{{A_GOD#"+god+"}}", UserLogic.makeLonLatMessage(mission.getLocation(), CompanionsModelConst.TEXT_AAA_DEGREES_BBB_MINUTES_CCC_LATITUDE_AND_DDD_DEGREES_EEE_MINUTES_FFF_LONGITUDE));
        mission.setMessage(msg);
        MissionLogic.setMission(user, mission);
        CompIOLogic.saveUser(user);
        context.addMessage(msg);
    }
    
    private static void missionHermitEnd(CompContextBean context, CompUserBean user, int god, MissionBean mission)
    {
        int prestige = IntegerUtils.parseInt(user.getMetadata().get("PRESTIGE_"+god));        
        prestige += CompConstLogic.PRESTIGE_HERMIT;
        user.getMetadata().put("PRESTIGE_"+god, prestige);
        context.addMessage(CompanionsModelConst.TEXT_THE_HERMIT_THANKS_YOU_FOR_YOUR_MESSAGE_AND_VANISHES,
                "{{A_GOD#"+god+"}}");
        BadgeLogic.updateBadges(context, user);
        clearMission(user, mission);
    }
    
    private static void missionDemonStart(CompContextBean context, CompUserBean user, int god)
    {
        GeoBean loc = new GeoBean(user.getLocation());
        SquareBean sq = getNearbyLocation(loc, 3, 8);
        MissionBean mission = new MissionBean();
        mission.setType("temple/mission/demon/"+System.currentTimeMillis());
        mission.setType("temple/mission/demon");
        mission.setLocation(new GeoBean(sq.getOrds()));
        mission.setModule("compmodule://god:"+god+"/temple/mission/hermit");
        mission.setFeature("Demon Missions for "+god);
        mission.setExpiryDate(System.currentTimeMillis() + 24*60*60*1000L); // one day
        CompEditModuleBean mod = CompEditIOLogic.getModuleFromURI(mission.getModule());
        if (mod == null)
        {
            mod = new CompEditModuleBean();
            mod.setID(mission.getModule());
            mod.setAccount("SYS");
            mod.setAuthor("System");
            mod.setName(mission.getFeature());
        }
        PFeatureBean feature = new PFeatureBean();
        feature.setParams(new JSONObject());
        feature.getParams().put("expiryDate", mission.getExpiryDate());
        feature.getParams().put("user", user.getURI());
        feature.setLocation(mission.getLocation().toString());
        feature.setName("Pentacle gate");
        feature.setMonsterType("Demon");
        CompRoomBean hermitage = FeatureLogic.getRoom("templePentacle");
        PRoomBean room = new PRoomBean();
        JSONObject json = hermitage.toJSON();
        room.fromJSON(mod, json);
        JSONUtils.replace(json, "?0?", String.valueOf(god));
        room.setName(ModelResolveLogic.resolve(CompApplicationHandler.getInstance().getModel(), 
                "en_US", 
                BaseUserState.RND, 
                context, 
                hermitage.getName().getIdent(), 
                hermitage.getName().getArgs()));
        room.setDescription(ModelResolveLogic.resolve(CompApplicationHandler.getInstance().getModel(), 
                "en_US", 
                BaseUserState.RND, 
                context, 
                hermitage.getDescription().getIdent(), 
                hermitage.getDescription().getArgs()));
        feature.getRooms().add(room);
        feature.setEntranceID(room.getID());       
        mod.getFeatures().add(feature);        
        CompEditIOLogic.saveModule(mod);
        FeatureLogic.readDynamicModules(true);
        AudioMessageBean msg = new AudioMessageBean(CompanionsModelConst.TEXT_YOUR_MISSION_IS_TO_ERADICATE_A_DEMON_INFESTATION_AT_YYY_LONGITUDE_ZZZ_LATITUDE,
                UserLogic.makeLonLatMessage(mission.getLocation()));
        mission.setMessage(msg);
        MissionLogic.setMission(user, mission);
        CompIOLogic.saveUser(user);
        context.addMessage(msg);
    }
    
    private static void missionDemonEnd(CompContextBean context, CompUserBean user, int god, MissionBean mission)
    {
        int prestige = IntegerUtils.parseInt(user.getMetadata().get("PRESTIGE_"+god));        
        prestige += CompConstLogic.PRESTIGE_DEMON;
        user.getMetadata().put("PRESTIGE_"+god, prestige);
        context.addMessage(CompanionsModelConst.TEXT_THE_PENTACLE_GATE_COLLAPSES_AS_THE_DEMONS_ARE_BANISHED);
        BadgeLogic.updateBadges(context, user);
        clearMission(user, mission);
    }

    public static void clearMission(CompUserBean user, MissionBean mission)
    {
        MissionLogic.deleteMission(user, mission.getID());
        CompIOLogic.saveUser(user);
        String uri = mission.getModule();
        String location = mission.getLocation().toString();
        CompEditModuleBean mod = CompEditIOLogic.getModuleFromURI(uri);
        if (mod != null)
        {
            for (PFeatureBean feature : mod.getFeatures())
                if (feature.getLocation().equals(location))
                {
                    mod.getFeatures().remove(feature);
                    CompEditIOLogic.saveModule(mod);
                    FeatureLogic.readDynamicModules(true);
                    break;
                }
        }
    }
    
    private static SquareBean getNearbyLocation(CoordBean loc, int minR, int maxR)
    {
        for (;;)
        {
            int dx = BaseUserState.RND.nextInt(maxR - minR) + minR;
            if (BaseUserState.RND.nextBoolean())
                dx = -dx;
            int dy = BaseUserState.RND.nextInt(maxR - minR) + minR;
            if (BaseUserState.RND.nextBoolean())
                dy = -dy;
            CoordBean ord = new CoordBean(loc.getX() + dx, loc.getY() + dy, loc.getZ());
            SquareBean sq = GenerationLogic.getSquare(ord);
            if (sq.getFeature() != CompConstLogic.FEATURE_NONE)
                continue;
            if ((sq.getTerrain() == CompConstLogic.TERRAIN_SALTWATER) || (sq.getTerrain() == CompConstLogic.TERRAIN_FRESHWATER))
                continue;
            if (sq.isAnyRoads())
                continue;
            return sq;
        }
    }
}
