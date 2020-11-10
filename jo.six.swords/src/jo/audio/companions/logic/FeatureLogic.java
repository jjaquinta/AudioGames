package jo.audio.companions.logic;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.FeatureInstanceBean;
import jo.audio.companions.data.GeoBean;
import jo.audio.companions.data.LocationBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.compedit.data.CompEditModuleBean;
import jo.audio.compedit.logic.CompEditIOLogic;
import jo.audio.util.BaseUserState;
import jo.audio.util.FromJSONLogic;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.beans.CacheBySize;
import jo.util.beans.CacheByTimeout;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.ResourceUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.StringUtils;

public class FeatureLogic
{
    private static final String PREFIX = "room$";
    private static final CacheBySize<CoordBean, FeatureBean> mFeatureCache = new CacheBySize<>(32);
    private static final CacheByTimeout<String, FeatureInstanceBean> mInstanceCache = new CacheByTimeout<>(20*60*1000L);
    private static final Map<String, JSONObject> mRoomTypeIndex = new HashMap<>();
    private static final Map<String, FeatureBean> mStaticFeatureIndex = new HashMap<>();
    private static long mDynamicFeatureLastRead = 0L;
    private static final Set<String> mDynamicFeatures = new HashSet<>();
    private static long DYNAMIC_FEATURE_RELOAD = 60*60*1000L; // once an hour
    private static String[] MODULES = {
            "content/swamp_city.json",
            "content/arches.json"
    };
    static
    {
        FeatureLogic.readRooms();
        FeatureLogic.readStaticModules();
    }
    
    private static void readRooms()
    {
        readRoomsFrom("roomTypes.json");
    }
    
    private static void readRoomsFrom(String source)
    {
        try
        {
            InputStream is = ResourceUtils.loadSystemResourceStream(source, CompanionsModelConst.class);
            JSONObject json = (JSONObject)JSONUtils.PARSER.parse(new InputStreamReader(is, "utf-8"));
            is.close();
            JSONArray items = JSONUtils.getArray(json, "roomTypes");
            if (items != null)
                for (int i = 0; i < items.size(); i++)
                {
                    JSONObject item = (JSONObject)items.get(i);
                    mRoomTypeIndex.put((String)item.getString("ID"), item);
                }
            JSONObject text = JSONUtils.getObject(json, "text");
            if (text != null)
                TextLogic.addText(text, PREFIX);
            JSONArray include = JSONUtils.getArray(json, "include");
            if (include != null)
                for (int i = 0; i < include.size(); i++)
                    readRoomsFrom((String)include.get(i));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static CompRoomBean makeRoom(String type, FeatureBean feature, String name)
    {
        CompRoomBean room = getRoom(type);
        room.setID(room.getID()+"$"+feature.getRooms().size());
        if (name != null)
            room.getName().setIdent(name);
        feature.getRooms().add(room);
        return room;
    }
    
    public static CompRoomBean getRoom(String type)
    {
        JSONObject data = null;
        for (StringTokenizer st = new StringTokenizer(type, ","); st.hasMoreTokens(); )
        {
            data = mRoomTypeIndex.get(st.nextElement());
            if (data != null)
                break;
        }
        if (data == null)
            return null;
        CompRoomBean room = new CompRoomBean();
        room.fromJSON(data);
        room.setParams((JSONObject)JSONUtils.deepCopy(room.getParams()));
        if (room.getName().getIdent() != null)
            room.getName().setIdent(PREFIX+room.getName().getIdent());
        if (room.getDescription().getIdent() != null)
            room.getDescription().setIdent(PREFIX+room.getDescription().getIdent());
        return room;
    }
    
    public static FeatureInstanceBean getFeatureInstance(CompContextBean context)
    {
        CompUserBean user = context.getUser();
        LocationBean loc = new LocationBean(user.getLocation());
        String key = user.getURI()+"?loc="+loc.getX()+","+loc.getY()+","+loc.getZ();
        FeatureInstanceBean inst = mInstanceCache.getFromCache(key);
        if (inst != null)
            return inst;
        RegionBean region = GenerationLogic.getRegion(loc);
        SquareBean sq = GenerationLogic.getSquare(loc);
        FeatureBean feature = getFeature(region, sq, context);
        if (feature == null)
            return null;
        //DebugUtils.trace("FeatureLogic.getFreatureInstance(create "+key+")");
        inst = new FeatureInstanceBean();
        inst.setFeature(feature);
        int ch = user.getChallengeLevel() + sq.getChallenge2();
        CompMonsterTypeBean primary;
        if (feature.getMonsterType() != null)
            primary = MonsterLogic.getFromType(ch, feature.getMonsterType());
        else
            primary = MonsterLogic.findMonster(sq, user.getChallengeLevel(), feature.getMonsterTreasure(), feature.getMonsterPopulous());
        //DebugUtils.trace("FeatureLogic.getFreatureInstance, primary="+primary.getID()+".");
        for (CompRoomBean room : feature.getRooms())
        {
            if (CompRoomBean.TYPE_ENCOUNTER.equals(room.getType()) && (room.getParams() != null))
            {
                String monster = (String)room.getParams().get(CompRoomBean.MD_ENCOUNTER_ID);
                if (monster == null)
                {
                    if (!room.getParams().containsKey(CompRoomBean.MD_ENCOUNTER_CHALLENGE))
                        continue;
                    Integer chMod = JSONUtils.getInt(room.getParams(), CompRoomBean.MD_ENCOUNTER_CHALLENGE);
                    monster = MonsterLogic.getAltMonster(primary.getID(), chMod);
                    //DebugUtils.trace("FeatureLogic.getFreatureInstance, mapping "+room.getID()+"->"+chMod+"->="+monster);
                }
                inst.getRoomToMonster().put(room.getID(), monster);
            }
            if ((room.getParams() != null) && BooleanUtils.parseBoolean(room.getParams().get("messageSource")))
                MessageLogic.addMessageInstance(inst, region, sq, room);
        }        
        mInstanceCache.addToCache(key, inst);
        return inst;
    }
    
    public static SquareBean findTownOrCastle(RegionBean region, SquareBean not)
    {
        List<SquareBean> squares = new ArrayList<>(); 
        for (SquareBean[] s1 : region.getSquares())
            for (SquareBean s2 : s1)
                if (s2.isAnyRoads() && (s2.isTown() || s2.isCastle()))
                    if ((not == null) || !not.getOrds().equals(s2.getOrds()))
                        squares.add(s2);
        if (squares.size() > 0)
            return squares.get(BaseUserState.RND.nextInt(squares.size()));
        return null;
    }

    private static boolean isEnabled(FeatureBean feature, CompContextBean context)
    {
        if (context == null)
            return true;
        //DebugUtils.trace("FeatureLogic.isEnabled(flags="+context.getLastOperation().getFlags()+", enabledBy="+feature.getEnabledBy()+")");
        if (StringUtils.isTrivial(feature.getEnabledBy()))
            return true;
        boolean enabled = BooleanUtils.parseBoolean(RoomLogic.eval(context, feature.getEnabledBy()));
        return enabled;
        /*
        if (StringUtils.isTrivial(context.getLastOperation().getFlags()))
            return false;
        return context.getLastOperation().getFlags().indexOf(feature.getEnabledBy()) >= 0;
        */
    }
    
    public static boolean isStaticFeature(CoordBean ords, CompContextBean context)
    {
        readDynamicModules(false);
        String key = ords.getX()+","+ords.getY()+","+ords.getZ();
        //DebugUtils.trace("FeatureLogic.isStaticFeature("+key+")");
        if (!mStaticFeatureIndex.containsKey(key))
        {
            //DebugUtils.trace("FeatureLogic.isStaticFeature nope");
            return false;
        }
        boolean enabled = isEnabled(mStaticFeatureIndex.get(key), context);
        //DebugUtils.trace("FeatureLogic.isStaticFeature feature enabled="+enabled);
        return enabled;
    }
    
    public static FeatureBean getFeature(RegionBean region, SquareBean sq, CompContextBean context)
    {
        String key = sq.getOrds().getX()+","+sq.getOrds().getY()+","+sq.getOrds().getZ();
        //DebugUtils.trace("FeatureLogic.getFeature("+key+")");
        if (mStaticFeatureIndex.containsKey(key))
        {
            FeatureBean feature = mStaticFeatureIndex.get(key);
            //DebugUtils.trace("FeatureLogic.getFeature, staticFeature="+feature.getName()+", params="+feature.getParams());
            if (isEnabled(feature, context))
            {
                //DebugUtils.trace("FeatureLogic.getFeature, enabled!");
                return feature;
            }
            else
            {
                //DebugUtils.trace("FeatureLogic.getFeature, not enabled.");
                return null;
            }
        }
        if (sq.getFeature() == CompConstLogic.FEATURE_NONE)
        {
            //DebugUtils.trace("FeatureLogic.getFeature, no square feature.");
            return null;
        }
        if (region == null)
            region = GenerationLogic.getRegion(sq.getOrds());
        FeatureBean feature = mFeatureCache.getFromCache(sq.getOrds());
        if (feature == null)
        {
            feature = GenerationLogic.getFeature(region, sq, (context == null) ? false : context.getUser().isTag("athiest"));
            mFeatureCache.addToCache(sq.getOrds(), feature);
        }
        //DebugUtils.trace("FeatureLogic.getFeature, square feature="+feature.getName()+".");
        return feature;
    }

    public static CompRoomBean findRoom(FeatureBean feature, String roomID)
    {
        if (StringUtils.isTrivial(roomID))
            return null;
        for (CompRoomBean room : feature.getRooms())
            if (room.getID().equals(roomID))
                return room;
        return null;
    }

    public static String getEntrance(FeatureBean feature, int lastMoveDirection)
    {
        for (CompRoomBean room : feature.getRooms())
        {
            switch (lastMoveDirection)
            {
                case CompOperationBean.NORTH:
                    if ("$exit".equals(room.getSouth()))
                        return room.getID();
                case CompOperationBean.SOUTH:
                    if ("$exit".equals(room.getNorth()))
                        return room.getID();
                case CompOperationBean.EAST:
                    if ("$exit".equals(room.getWest()))
                        return room.getID();
                case CompOperationBean.WEST:
                    if ("$exit".equals(room.getEast()))
                        return room.getID();
            }
        }
        return feature.getEntranceID();
    }

    private static void readStaticModules()
    {
        for (String uri : MODULES)
            try
            {
                InputStream is = ResourceUtils.loadSystemResourceStream(uri, CompanionsModelConst.class);
                JSONObject json = (JSONObject)JSONUtils.PARSER.parse(new InputStreamReader(is, "utf-8"));
                is.close();
                addModule(json);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        readDynamicModules(true); // bootstrap
    }

    public static void addModule(JSONObject json)
    {
        String id = makeID(json.getString("id"))+"$";
        //DebugUtils.trace("FeatureLogic.addModule("+id+")");
        Set<String> mappedText = TextLogic.addText(JSONUtils.getObject(json, "text"), id);
        String enabledBy = json.getString("enabledBy");
        String account = json.getString("account");
        JSONArray features = JSONUtils.getArray(json, "features");
        //DebugUtils.trace("FeatureLogic.addModule #features="+features.size());
        for (int i = 0; i < features.size(); i++)
        {
            JSONObject feature = (JSONObject)features.get(i);
            readModuleFeatures(id, enabledBy, account, feature, mappedText);
        }
        JSONArray monsters = JSONUtils.getArray(json, "monsters");
        if (monsters != null)
            MonsterLogic.indexMonsters(monsters);
    }

    private static String makeID(String txt)
    {
        StringBuffer sb = new StringBuffer();
        for (char c : txt.toCharArray())
            if (c == '.')
                sb.append("_");
            else
                sb.append(c);
        return sb.toString();
    }
    
    public static void readDynamicModules(boolean force)
    {
        // TODO: track which loaded, so if one is removed from the data set it gets removed
        if (!force)
            if (System.currentTimeMillis() < mDynamicFeatureLastRead + DYNAMIC_FEATURE_RELOAD)
                return;
        for (String id : mDynamicFeatures)
            mStaticFeatureIndex.remove(id);
        Set<String> oldFeatures = new HashSet<>();
        oldFeatures.addAll(mStaticFeatureIndex.keySet());
        List<CompEditModuleBean> modules = CompEditIOLogic.getAllModules();
        for (CompEditModuleBean module : modules)
        {
            //DebugUtils.trace("FeatureLogic.readDynamicModules module="+module.getURI()+", #features="+module.getFeatures().size());
            //for (PFeatureBean feature : module.getFeatures())
            //{
                //DebugUtils.trace("FeatureLogic.readDynamicModules   feature="+feature.getName());
                //DebugUtils.trace("FeatureLogic.readDynamicModules  location="+feature.getLocation());
                //DebugUtils.trace("FeatureLogic.readDynamicModules    params="+feature.getParams());
            //}
            JSONObject json = module.toJSON();
            //DebugUtils.trace("FeatureLogic.readDynamicModules json module features="+((JSONArray)json.get("features")).size());
            addModule(json);
        }
        mDynamicFeatures.clear();
        for (String id : mStaticFeatureIndex.keySet())
            if (!oldFeatures.contains(id))
                mDynamicFeatures.add(id);
        mDynamicFeatureLastRead = System.currentTimeMillis();
        mFeatureCache.clear();
        mInstanceCache.clear();
    }
    
    private static void readModuleFeatures(String id, String enableBy, String account, JSONObject json, Set<String> mappedText)
    {
        FeatureBean feature = new FeatureBean();
        FromJSONLogic.fromJSON(feature, json);
        if (StringUtils.isTrivial(feature.getLocation()))
        {
            //DebugUtils.trace("FeatureLogic.readModuleFeatures, no location in "+json.toJSONString());
            return;
        }
        CoordBean location = new CoordBean(feature.getLocation());
        feature.setLocation(location.getX()+","+location.getY()+","+location.getZ());
        DebugUtils.trace("FeatureLogic.readModuleFeatures("+feature.getLocation()+")");
        if (feature.getEnabledBy() == null)
            feature.setEnabledBy(enableBy);
        if (feature.getAccount() == null)
            feature.setAccount(account);
        if (mappedText.contains(feature.getName().getIdent()))
            feature.getName().setIdent(id+feature.getName().getIdent());
        for (CompRoomBean room : feature.getRooms())
        {
            if (mappedText.contains(room.getName().getIdent()))
                room.getName().setIdent(id+room.getName().getIdent());
            if (mappedText.contains(room.getDescription().getIdent()))
                room.getDescription().setIdent(id+room.getDescription().getIdent());
            if (room.getParams() != null)
            {
                if (room.getParams().containsKey(CompRoomBean.MD_ENCOUNTER_ANNOUNCE))
                    if (mappedText.contains(room.getParams().getString(CompRoomBean.MD_ENCOUNTER_ANNOUNCE)))
                        room.getParams().put(CompRoomBean.MD_ENCOUNTER_ANNOUNCE, id+room.getParams().getString(CompRoomBean.MD_ENCOUNTER_ANNOUNCE));
                //DebugUtils.trace("FeatureLogic.readModuleFeatures room="+room.getID()+", params="+room.getParams().toJSONString());
            }
            //else
            //    DebugUtils.trace("FeatureLogic.readModuleFeatures room="+room.getID()+", no params");
        }
        mStaticFeatureIndex.put(feature.getLocation(), feature);
    }

    public static void fillNearbyFeatures(CompContextBean context, int radius)
    {
        GeoBean location = new GeoBean(context.getUser().getLocation());
        context.getNearbyFeatures().clear();
        final Map<AudioMessageBean, Integer> distances = new HashMap<>();
        for (int dx = -radius; dx <= radius; dx++)
            for (int dy = -radius; dy <= radius; dy++)
            {
                if ((dx == 0) && (dy == 0))
                    continue;
                CoordBean ords = new CoordBean(location.getX() + dx, location.getY() + dy, location.getZ());
                RegionBean rgn = GenerationLogic.getRegion(ords);
                SquareBean sq = GenerationLogic.getSquare(ords);
                FeatureBean f = FeatureLogic.getFeature(rgn, sq, context);
                if (f != null)
                {
                    AudioMessageBean msg;
                    if (dx == 0)
                    {
                        msg =
                                new AudioMessageBean(CompanionsModelConst.TEXT_XXX_MILES_TO_THE_YYY_IS_ZZZ,
                                        Math.abs(dy), (dy < 0 ? CompanionsModelConst.TEXT_NORTH : CompanionsModelConst.TEXT_SOUTH),
                                f.getName());
                    }
                    else if (dy == 0)
                    {
                        msg =
                                new AudioMessageBean(CompanionsModelConst.TEXT_XXX_MILES_TO_THE_YYY_IS_ZZZ,
                                        Math.abs(dx), (dx < 0 ? CompanionsModelConst.TEXT_WEST : CompanionsModelConst.TEXT_EAST),
                                f.getName());
                    }
                    else if (BaseUserState.RND.nextBoolean())
                    {
                        msg =
                                new AudioMessageBean(CompanionsModelConst.TEXT_XXX_MILES_TO_THE_YYY_AND_AAA_MILES_TO_THE_BBB_IS_ZZZ,
                                        Math.abs(dy), (dy < 0 ? CompanionsModelConst.TEXT_NORTH : CompanionsModelConst.TEXT_SOUTH),
                                        Math.abs(dx), (dx < 0 ? CompanionsModelConst.TEXT_WEST : CompanionsModelConst.TEXT_EAST),
                                f.getName());
                    }
                    else
                    {
                        msg =
                                new AudioMessageBean(CompanionsModelConst.TEXT_XXX_MILES_TO_THE_YYY_AND_AAA_MILES_TO_THE_BBB_IS_ZZZ,
                                        Math.abs(dx), (dx < 0 ? CompanionsModelConst.TEXT_WEST : CompanionsModelConst.TEXT_EAST),
                                        Math.abs(dy), (dy < 0 ? CompanionsModelConst.TEXT_NORTH : CompanionsModelConst.TEXT_SOUTH),
                                f.getName());
                    }
                    context.getNearbyFeatures().add(msg);
                    distances.put(msg, Math.abs(dx) + Math.abs(dy));
                }
            }
        Collections.sort(context.getNearbyFeatures(), new Comparator<AudioMessageBean>() {
            @Override
            public int compare(AudioMessageBean o1, AudioMessageBean o2)
            {
                int d1 = distances.get(o1);
                int d2 = distances.get(o2);
                return d1 - d2;
            }
        });
    }
    
    public static Map<String,FeatureBean> getStaticFeatureIndex()
    {
        return mStaticFeatureIndex;
    }
}
