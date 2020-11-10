package jo.audio.companions.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompEffectInstanceBean;
import jo.audio.companions.data.CompEffectTypeBean;
import jo.audio.companions.data.CompEncounterBean;
import jo.audio.companions.data.CompIdentBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompMonsterInstanceBean;
import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.FeatureInstanceBean;
import jo.audio.companions.data.GeoBean;
import jo.audio.companions.data.ItemSelectBean;
import jo.audio.companions.data.LocationBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.build.CastleLogic;
import jo.audio.companions.logic.build.PermitLogic;
import jo.audio.companions.logic.effect.PotionLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.StringUtils;

public class UserLogic
{
    public static CompUserBean getUser(String id)
    {
        if (!id.startsWith("compuser://"))
            id = "compuser://"+id;
        return CompIOLogic.getUserFromURI(id);
    }

    public static CompUserBean newInstance(CompIdentBean id, String flags)
    {
        String uri = "compuser://"+id.getUserID();
        CompUserBean user = new CompUserBean();
        user.setURI(uri);
        if (flags.toLowerCase().indexOf("cirrane") >= 0)
            user.setLocation(CompConstLogic.INITIAL_LOCATION_CIRRANE);
        else if (flags.toLowerCase().indexOf("irl") >= 0)
            user.setLocation(CompConstLogic.INITIAL_LOCATION_IRL);
        else if (flags.toLowerCase().indexOf("ice") >= 0)
            user.setLocation(CompConstLogic.INITIAL_LOCATION_ICE);
        else
            user.setLocation(CompConstLogic.INITIAL_ENUMA_LOCATION);
        for (int i = 0; i < CompConstLogic.INITIAL_COMPANIONS; i++)
        {
            CompCompanionBean comp = CompanionLogic.newInstance(CompConstLogic.RACE_HUMAN);
            addCompanion(user, comp);
        }
        user.setActiveCompanion(user.getCompanions().get(0).getID());
        CompIOLogic.saveUser(user);
        return user;
    }

    private static void addCompanion(CompUserBean user, CompCompanionBean comp)
    {
        user.getCompanions().add(comp);
        // record items
        String[] itemIDs = new String[comp.getItems().size()];
        int[] itemQs = new int[comp.getItems().size()];
        for (int i = 0; i < itemIDs.length; i++)
        {
            CompItemInstanceBean item = comp.getItems().get(i);
            itemIDs[i] = item.getID();
            itemQs[i] = item.getQuantity();
        }
        // remove from companion
        for (int i = 0; i < itemIDs.length; i++)
        {
            CompanionLogic.doRemoveItem(comp, itemIDs[i], itemQs[i]);
            UserLogic.doAddItem(user, itemIDs[i], itemQs[i]);
        }
        // move back to companion
        for (int i = 0; i < itemIDs.length; i++)
        {
            UserLogic.doRemoveItem(user, itemIDs[i], itemQs[i]);
            CompanionLogic.doAddItem(user, comp, itemIDs[i], itemQs[i]);
        }
    }

    public static void doAddItem(CompUserBean user, String id, int amnt)
    {
        ItemLogic.addItem(user.getItems(), id, amnt);
    }
    
    public static void doRemoveItem(CompUserBean user, String id, int amnt)
    {
        ItemLogic.removeItem(user.getItems(), id, amnt);
    }

    public static void moveTo(CompContextBean context, String location)
    {
        CompUserBean user = context.getUser();
        LocationBean ord = new LocationBean(location);
        if (StringUtils.isTrivial(ord.getRoomID()))
            moveToStrategic(context, user, ord);
        else
            moveToTactical(context, user, ord);
    }

    public static void move(CompContextBean context, int dir)
    {
        CompUserBean user = context.getUser();
        LocationBean ord = new LocationBean(user.getLocation());
        if (StringUtils.isTrivial(ord.getRoomID()))
            moveStrategic(context, user, ord, dir);
        else
            moveTactical(context, user, dir);
    }
    
    private static void moveStrategic(CompContextBean context, CompUserBean user, LocationBean ord, int dir)
    {
        DebugUtils.trace("moveStrategic(ord="+ord+")");
        SquareBean s1 = GenerationLogic.getSquare(ord);
        float e1 = CompConstLogic.TABLE_TERRAIN_COST[s1.getTerrain()];
        switch (dir)
        {
            case CompOperationBean.NORTH:
                e1 /= CompConstLogic.ROAD_DIVISOR[s1.getRoadNorth()];
                ord = new LocationBean(ord.north());
                break;
            case CompOperationBean.SOUTH:
                e1 /= CompConstLogic.ROAD_DIVISOR[s1.getRoadSouth()];
                ord = new LocationBean(ord.south());
                break;
            case CompOperationBean.EAST:
                e1 /= CompConstLogic.ROAD_DIVISOR[s1.getRoadEast()];
                ord = new LocationBean(ord.east());
                break;
            case CompOperationBean.WEST:
                e1 /= CompConstLogic.ROAD_DIVISOR[s1.getRoadWest()];
                ord = new LocationBean(ord.west());
                break;
            default:
                throw new IllegalStateException("Unknown dir="+dir);
        }
        DebugUtils.trace("move to ord="+ord+".");
        SquareBean s2 = GenerationLogic.getSquare(ord);
        if (((s2.getTerrain() == CompConstLogic.TERRAIN_FRESHWATER) || (s2.getTerrain() == CompConstLogic.TERRAIN_SALTWATER))
                && !s2.isAnyRoads())
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_TERRAIN);
            return;
        }
        float e2 = CompConstLogic.TABLE_TERRAIN_COST[s2.getTerrain()];
        float delta = s2.getAltitude() - s1.getAltitude();
        float deltaStep = 6;
        if (ord.getZ() == 2)
            deltaStep = 2800;
        switch (dir)
        {
            case CompOperationBean.NORTH:
                if (s1.isRoadSouth())
                    e2 /= 2;
                if (delta >= deltaStep)
                    context.addMessage(CompanionsModelConst.TEXT_YOU_TRAVEL_UP_TO_THE_NORTH);
                else if (delta <= -deltaStep)
                    context.addMessage(CompanionsModelConst.TEXT_YOU_TRAVEL_DOWN_TO_THE_NORTH);
                else
                    context.addMessage(CompanionsModelConst.TEXT_YOU_TRAVEL_NORTH);
                break;
            case CompOperationBean.SOUTH:
                if (s1.isRoadNorth())
                    e1 /= 2;
                if (delta >= deltaStep)
                    context.addMessage(CompanionsModelConst.TEXT_YOU_TRAVEL_UP_TO_THE_SOUTH);
                else if (delta <= -deltaStep)
                    context.addMessage(CompanionsModelConst.TEXT_YOU_TRAVEL_DOWN_TO_THE_SOUTH);
                else
                    context.addMessage(CompanionsModelConst.TEXT_YOU_TRAVEL_SOUTH);
                break;
            case CompOperationBean.EAST:
                if (s1.isRoadWest())
                    e1 /= 2;
                if (delta >= deltaStep)
                    context.addMessage(CompanionsModelConst.TEXT_YOU_TRAVEL_UP_TO_THE_EAST);
                else if (delta <= -deltaStep)
                    context.addMessage(CompanionsModelConst.TEXT_YOU_TRAVEL_DOWN_TO_THE_EAST);
                else
                    context.addMessage(CompanionsModelConst.TEXT_YOU_TRAVEL_EAST);
                break;
            case CompOperationBean.WEST:
                if (s1.isRoadEast())
                    e1 /= 2;
                if (delta >= deltaStep)
                    context.addMessage(CompanionsModelConst.TEXT_YOU_TRAVEL_UP_TO_THE_WEST);
                else if (delta <= -deltaStep)
                    context.addMessage(CompanionsModelConst.TEXT_YOU_TRAVEL_DOWN_TO_THE_WEST);
                else
                    context.addMessage(CompanionsModelConst.TEXT_YOU_TRAVEL_WEST);
                break;
            default:
                throw new IllegalStateException("Unknown dir="+dir);
        }
        if (ord.getZ() == 0)
        {
            int radius = Math.abs(ord.getX() - CompConstLogic.INITIAL_LOCATION_X) + Math.abs(ord.getY() - CompConstLogic.INITIAL_LOCATION_Y);
            user.setMaxDistance(Math.max(user.getMaxDistance(), radius));
        }
        user.setTotalDistance(user.getTotalDistance() + CompConstLogic.DISTANCE_TACTICAL);
        user.setTotalEffort(user.getTotalEffort() + e1/2 + e2/2);
        user.setLastMoveDirection(dir);
        moveToStrategic(context, user, ord);
    }

    public static void moveToStrategic(CompContextBean context,
            CompUserBean user, LocationBean ord)
    {
        String oldLocation = user.getLocation();
        CompOperationLogic.fillContext(context);
        UserLogic.passTheTime(context, user, CompConstLogic.TIME_STRATEGIC);
        user.setOldLocation(oldLocation);
        user.setLocation(ord.toString());
        checkCombat(context, user);
        BadgeLogic.updateBadges(context, user);
        CompIOLogic.saveUser(user);
        DebugUtils.trace("saved location="+user.getLocation()+".");
        CompOperationLogic.fillContext(context);
        if (context.getFeature() != null)
        {
            DebugUtils.trace("Checking auto-enter="+context.getFeature().getFeature().getParam("autoEnter")+".");
            boolean autoEnter = BooleanUtils.parseBoolean(RoomLogic.eval(context, context.getFeature().getFeature().getParam("autoEnter")));
            DebugUtils.trace("Checking auto-enter => "+autoEnter+".");
            if (autoEnter)
                enter(context);
        }
    }

    // cure hit points
    public static boolean fullyCureCompanions(CompContextBean context, CompUserBean user)
    {
        boolean anyHeal = false;
        for (CompCompanionBean comp : user.getCompanions())
            if (comp.getCurrentHitPoints() != comp.getEffectiveHitPoints())
            {
                comp.setCurrentHitPoints(comp.getEffectiveHitPoints());
                anyHeal = true;
            }
        for (CompCompanionBean comp : user.getDeadCompanions().toArray(new CompCompanionBean[0]))
        {
            comp.setCurrentHitPoints(comp.getEffectiveHitPoints());
            user.getDeadCompanions().remove(comp);
            user.getCompanions().add(comp);
            anyHeal = true;
        }
        if (anyHeal)
            context.addMessage(CompanionsModelConst.TEXT_THE_WHOLE_PARTY_IS_BACK_ON_FULL_HIT_POINTS);
        return anyHeal;
    }

    // cure energy drain
    public static boolean fullyHealCompanions(CompContextBean context, CompUserBean user)
    {
        boolean anyHeal = false;
        for (CompCompanionBean comp : user.getAllCompanions())
            for (Iterator<CompEffectInstanceBean> i = comp.getEffects().iterator(); i.hasNext(); )
            {
                CompEffectInstanceBean e = i.next();
                if (e.getID().equals(CompEffectTypeBean.LEVEL_DRAIN))
                {
                    i.remove();
                    anyHeal = true;
                }
            }
        if (anyHeal)
            context.addMessage(CompanionsModelConst.TEXT_YOU_FEEL_YOUR_LIFE_ENERGY_RESTORED);
        return anyHeal;
    }

    public static boolean healCompanion(CompContextBean context, CompUserBean user, CompCompanionBean comp, int amnt)
    {
        if (comp.getCurrentHitPoints() >= comp.getEffectiveHitPoints())
            return false;
        comp.setCurrentHitPoints(comp.getCurrentHitPoints() + amnt);
        if (comp.getCurrentHitPoints() > comp.getEffectiveHitPoints())
            comp.setCurrentHitPoints(comp.getEffectiveHitPoints());
        if ((comp.getCurrentHitPoints() > 0) && user.getDeadCompanions().contains(comp))
        {
            user.getDeadCompanions().remove(comp);
            user.getCompanions().add(comp);
            context.addMessage(CompanionsModelConst.TEXT_XXX_REGAINS_CONCIOUSNESS, comp);
        }
        else
            if (comp.getCurrentHitPoints() >= comp.getEffectiveHitPoints())
            {
                context.addMessage(CompanionsModelConst.TEXT_XXX_IS_BACK_ON_FULL_HIT_POINTS,
                        comp.getName(), amnt);
            }
            else
            {
                context.addMessage(CompanionsModelConst.TEXT_XXX_IS_HEALED_FOR_YYY_HIT_POINTS_AND_IS_NOW_ON_ZZZ,
                        comp.getName(), amnt, comp.getCurrentHitPoints());
            }
        return true;
    }

    public static void enter(CompContextBean context)
    {
        CompOperationLogic.fillContext(context);
        CompUserBean user = context.getUser();
        String oldLocation = user.getLocation();
        LocationBean ord = context.getLocation();
        if (!StringUtils.isTrivial(ord.getRoomID()))
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_HAVE_ALREADY_ENTERED_THE_PLACE);
            return;
        }
        FeatureInstanceBean feature = FeatureLogic.getFeatureInstance(context);
        if (feature == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_THERE_IS_NO_PLACE_HERE_TO_ENTER);
            return;
        }
        context.setFeature(feature);
        String id = FeatureLogic.getEntrance(feature.getFeature(), user.getLastMoveDirection());
        ord.setRoomID(id);
        user.setLocation(ord.toString());
        user.setOldLocation(oldLocation);
        String visitList = ResponseUtils.addToList(user.getVisitList(), feature.getFeature().getName().getIdent());
        if (visitList.equals(user.getVisitList()))
            context.addMessage(CompanionsModelConst.TEXT_WELCOME_BACK_TO_XXX, feature.getFeature().getName());
        else
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_ENTER_XXX, feature.getFeature().getName());
            user.setVisitList(visitList);
        }
        CompOperationLogic.fillContext(context);
        checkCombatTactical(context, user);
        DebugUtils.trace("UserLogic.enter cheking postEnter="+context.isRoomParam(CompRoomBean.MD_POST_ENTER)+"/"+context.getRoom().getParams());
        if (context.isRoomParam(CompRoomBean.MD_POST_ENTER))
        {
            DebugUtils.trace("UserLogic.enter postEnter="+context.getRoomParam(CompRoomBean.MD_POST_ENTER));
            MacroLogic.executeSimple(context, context.getRoomParam(CompRoomBean.MD_POST_ENTER));
            DebugUtils.trace("UserLogic.enter postEnter done.");
        }
        CompIOLogic.saveUser(user);
    }
    
    private static void moveTactical(CompContextBean context, CompUserBean user, int dir)
    {
        DebugUtils.trace("moveTactical(loc="+user.getLocation()+", dir="+dir+")");
        CompOperationLogic.fillContext(context);
        if (context.getFeature() == null) // oops!
        {
            moveStrategic(context, user, context.getLocation(), dir);
            return;
        }
        FeatureBean feature = context.getFeature().getFeature();
        CompRoomBean r1 = FeatureLogic.findRoom(feature, context.getLocation().getRoomID());
        if (r1 == null)
            throw new IllegalArgumentException("Unknown room '"+context.getLocation()+"'");
        if (RoomLogic.checkRoomLock(context, user, r1, dir))
            return;
        String r2ID = null;
        switch (dir)
        {
            case CompOperationBean.NORTH:
                r2ID = r1.getNorth();
                break;
            case CompOperationBean.SOUTH:
                r2ID = r1.getSouth();
                break;
            case CompOperationBean.EAST:
                r2ID = r1.getEast();
                break;
            case CompOperationBean.WEST:
                r2ID = r1.getWest();
                break;
            default:
                throw new IllegalStateException("Unknown dir="+dir);
        }
        if (r2ID == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_CAN_T_GO_THAT_WAY);
            return;
        }
        if ("$exit".equals(r2ID))
        {
            context.getLocation().setRoomID("");
            context.addMessage(CompanionsModelConst.TEXT_YOU_EXIT_XXX, feature.getName());
            context.setRoom(null);
        }
        else
        {
            context.getLocation().setRoomID(r2ID);
            CompRoomBean r2 = FeatureLogic.findRoom(feature, context.getLocation().getRoomID());
            if (r2 == null)
                throw new IllegalArgumentException("Unknown room '"+context.getLocation()+"'");
            context.setRoom(r2);
            user.setLocation(context.getLocation().toString());
            checkBounty(context, user, r2);
            checkCombat(context, user);
            DebugUtils.trace("UserLogic.moveTactical cheking postEnter="+context.isRoomParam(CompRoomBean.MD_POST_ENTER));
            if (context.isRoomParam(CompRoomBean.MD_POST_ENTER))
                MacroLogic.executeSimple(context, context.getRoomParam(CompRoomBean.MD_POST_ENTER));
            if (feature.getAccount() != null)
                AccountingLogic.credit(feature.getAccount(), user.getURI(), 1);
        }
        user.setTotalDistance(user.getTotalDistance() + CompConstLogic.DISTANCE_TACTICAL);
        moveToTactical(context, user, context.getLocation());
    }

    public static void moveToTactical(CompContextBean context,
            CompUserBean user, LocationBean location)
    {
        String oldLocation = user.getLocation();
        UserLogic.passTheTime(context, user, CompConstLogic.TIME_TACTICAL);
        user.setLocation(location.toString());
        user.setOldLocation(oldLocation);
        BadgeLogic.updateBadges(context, user);
        CompIOLogic.saveUser(user);
    }
    
    public static void passTheTime(CompContextBean context, CompUserBean user, int time)
    {
        int now = user.getTotalTime() + time;
        user.setTotalTime(now);
        for (CompCompanionBean comp: user.getCompanions())
            for (Iterator<CompEffectInstanceBean> i = comp.getEffects().iterator(); i.hasNext(); )
            {
                CompEffectInstanceBean effect = i.next();
                if (effect.isTimeBased())
                    if (effect.getTimeExpiry() < now)
                        i.remove();
            }
        healCompanions(context, user, time/CompConstLogic.ONE_HOUR);
    }

    private static void healCompanions(CompContextBean context, CompUserBean user, int cure)
    {
        List<String> regained = new ArrayList<>();
        List<String> full = new ArrayList<>();
        boolean anyNotMax = false;
        for (CompCompanionBean comp : user.getCompanions())
        {
            if (comp.getCurrentHitPoints() < comp.getEffectiveHitPoints())
            {
                comp.setCurrentHitPoints(comp.getCurrentHitPoints() + cure);
                if (comp.getCurrentHitPoints() >= comp.getEffectiveHitPoints())
                {
                    comp.setCurrentHitPoints(comp.getEffectiveHitPoints());
                    full.add(comp.getName());
                }
                else
                    anyNotMax = true;
            }
        }
        for (CompCompanionBean comp : user.getDeadCompanions().toArray(new CompCompanionBean[0]))
        {
            comp.setCurrentHitPoints(comp.getCurrentHitPoints() + cure);
            if (comp.getCurrentHitPoints() >= comp.getEffectiveHitPoints())
            {
                comp.setCurrentHitPoints(comp.getEffectiveHitPoints());
                full.add(comp.getName());
            }
            else
                anyNotMax = true;
            if (comp.getCurrentHitPoints() > 0)
            {
                regained.add(comp.getName());
                user.getDeadCompanions().remove(comp);
                user.getCompanions().add(comp);
            }
        }
        if (regained.size() == 1)
            context.addMessage(CompanionsModelConst.TEXT_XXX_REGAINS_CONCIOUSNESS, regained.get(0));
        else if (regained.size() > 1)
            context.addMessage(CompanionsModelConst.TEXT_XXX_REGAIN_CONCIOUSNESS, ResponseUtils.wordList(regained));
        if (full.size() > 0)
        {
            if (!anyNotMax)
                context.addMessage(CompanionsModelConst.TEXT_THE_WHOLE_PARTY_IS_BACK_ON_FULL_HIT_POINTS);
            else if (full.size() == 1)
                context.addMessage(CompanionsModelConst.TEXT_XXX_IS_BACK_ON_FULL_HIT_POINTS, full.get(0));
            else
                context.addMessage(CompanionsModelConst.TEXT_XXX_ARE_BACK_ON_FULL_HIT_POINTS, ResponseUtils.wordList(full));
        }
    }

    private static void checkBounty(CompContextBean context, CompUserBean user, CompRoomBean room)
    {
        if (room.getParams() == null)
            return;
        if (!room.getParams().containsKey("bounties"))
            return;
        JSONArray bounties = JSONUtils.getArray(room.getParams(), "bounties");
        for (int i = 0; i < bounties.size(); i++)
        {
            JSONObject bounty = (JSONObject)bounties.get(i);
            String ords = JSONUtils.getString(bounty, "ords");
            if ((StringUtils.safe(user.getBossKillList()).indexOf(ords) >= 0)
                    && (StringUtils.safe(user.getBountyList()).indexOf(ords) < 0))
            {
                int reward = JSONUtils.getInt(bounty, "reward");
                context.addMessage(CompanionsModelConst.TEXT_KILL_BOSS);
                context.addMessage(CompanionsModelConst.TEXT_CONGRATULATIONS_YOU_EARNED_A_XXX_BOUNTY, reward);
                user.setGoldPieces(user.getGoldPieces() + reward);
                user.setBountyList(ResponseUtils.addToList(user.getBountyList(), ords));
                ExperienceLogic.addXP(context, reward);
            }
        }
    }
    
    private static boolean checkCombat(CompContextBean context, CompUserBean user)
    {
        GeoBean ord = new GeoBean(user.getLocation());
        context.setLocation(ord);
        CompOperationLogic.fillContext(context);
        DebugUtils.trace("UserLogic.checkCombat(loc="+ord
                +", feature="+(context.getFeature() == null ? "null" : (context.getFeature().getFeature().getName().toString()))+""
                +", room="+(context.getRoom() == null ? "null" : (context.getRoom().getName().toString()))+")");
        if ((context.getFeature() == null) || (context.getRoom() == null))
            return checkCombatStrategic(context, user);
        else
        {
            RoomLogic.checkRoomEffect(context, user, context.getRoom());
            return checkCombatTactical(context, user);
        }
    }
    
    private static boolean checkCombatTactical(CompContextBean context, CompUserBean user)
    {
        FeatureInstanceBean feature = context.getFeature();
        if (feature == null)
            return false;
        CompRoomBean r = context.getRoom();
        if (r == null)
            return false;
        String id = feature.getRoomToMonster().get(r.getID());
        //DebugUtils.trace("UserLogic.checkCombatTactical(room="+r.getID()+", monst="+id+")");
        if (id == null)
            return false;
        CompMonsterTypeBean monster = null;
        if (id != null)
        {
            monster = MonsterLogic.getAnyMonsterType(id);
            if (monster == null)
                DebugUtils.trace("UserLogic.checkCombatTactical(monst="+id+") - can't find monster");
            else
                DebugUtils.trace("UserLogic.checkCombatTactical(monst="+id+") - found "+monster.getID()+"/"+monster.getName());
        }
        //if ("random".equals(id))
        if (monster == null)
            monster = MonsterLogic.getFromTerrain(context.getSquare(), user.getChallengeLevel());
        CompEncounterBean encounter = user.getEncounter();
        encounter.setRound(1);
        encounter.getMonsters().clear();
        if (!CompRoomBean.TYPE_ENCOUNTER.equals(r.getType()))
        {
            int roll = BaseUserState.RND.nextInt(6);
            if (roll > 0)
            {
                if (roll <= 1)
                    context.setSpoor(monster);
                return false;
            }
        }
        else // room encounter
        {
            if (!RoomLogic.roomRegen(user, context.getLocation(), r))
                return false;
            if (context.getFeature().getEncountersUnfinished().containsKey(context.getRoom().getID()))
            {
                CompMonsterInstanceBean[] ms = context.getFeature().getEncountersUnfinished().get(context.getRoom().getID());
                if (ms != null)
                    for (CompMonsterInstanceBean m : ms)
                        encounter.getMonsters().add(m);
                return false;
            }
        }
        // number encountered 
        int num;
        String encNum = JSONUtils.getString(r.getParams(), CompRoomBean.MD_ENCOUNTER_NUMBER);
        if (StringUtils.isTrivial(encNum))
            num = monster.getEncRoll().roll(BaseUserState.RND);
        else
        {
            DiceRollBean roll = new DiceRollBean(encNum);
            num = roll.roll(BaseUserState.RND);
        }
        for (int i = 0; i < num; i++)
            encounter.getMonsters().add(MonsterLogic.createInstance(context.getUser(), monster.getID()));
        addExtra(user, monster, encounter);
        // treasure
        encounter.getTreasures().clear();
        String encTreasure = JSONUtils.getString(r.getParams(), CompRoomBean.MD_ENCOUNTER_TREASURE);
        DiceRollBean treasure = null;
        if (encTreasure != null)
            treasure = new DiceRollBean(encTreasure);
        if (treasure != null)
        {
            if (treasure.getMult() == 0)
                treasure.setNumber(treasure.getNumber()*num);
            encounter.getTreasures().add(treasure);
        }
        // treasure items
        encounter.getTreasureItems().clear();
        JSONObject encTreasureItem = JSONUtils.getObject(r.getParams(), CompRoomBean.MD_ENCOUNTER_TREASURE_ITEM);
        ItemSelectBean treasureItem = null;
        if (encTreasureItem != null)
        {
            treasureItem = new ItemSelectBean();
            treasureItem.fromJSON(encTreasureItem);
        }
        if (treasureItem != null)
            encounter.getTreasureItems().add(treasureItem);
        
        String encAnnounce = JSONUtils.getString(r.getParams(), CompRoomBean.MD_ENCOUNTER_ANNOUNCE);
        if (StringUtils.isTrivial(encAnnounce))
            encounter.setAnnounce(null);
        else
            encounter.setAnnounce(encAnnounce);
        calculateEncounterTotals(encounter);
        user.setTotalFights(user.getTotalFights() + 1);
        DebugUtils.trace("Tactical Combat started");
        return true;
    }

    public static boolean summonMonsterTactical(CompContextBean context, CompMonsterTypeBean monster, int num,
            String encTreasure, JSONObject encTreasureItem, String encAnnounce)
    {
        CompUserBean user = context.getUser();
        CompEncounterBean encounter = user.getEncounter();
        encounter.setRound(1);
        encounter.getMonsters().clear();
        CompRoomBean room = context.getRoom();
        FeatureInstanceBean feature = context.getFeature();
        if ((feature == null) || (room == null))
            return false;
        if ((feature.getEncountersUnfinished() != null) && feature.getEncountersUnfinished().containsKey(room.getID()))
        {
            CompMonsterInstanceBean[] ms = feature.getEncountersUnfinished().get(room.getID());
            if (ms != null)
                for (CompMonsterInstanceBean m : ms)
                    encounter.getMonsters().add(m);
        }
        for (int i = 0; i < num; i++)
            encounter.getMonsters().add(MonsterLogic.createInstance(context.getUser(), monster.getID()));
        // treasure
        encounter.getTreasures().clear();
        DiceRollBean treasure = null;
        if (encTreasure != null)
            treasure = new DiceRollBean(encTreasure);
        if (treasure != null)
        {
            if (treasure.getMult() == 0)
                treasure.setNumber(treasure.getNumber()*num);
            encounter.getTreasures().add(treasure);
        }
        // treasure items
        encounter.getTreasureItems().clear();
        ItemSelectBean treasureItem = null;
        if (encTreasureItem != null)
        {
            treasureItem = new ItemSelectBean();
            treasureItem.fromJSON(encTreasureItem);
        }
        if (treasureItem != null)
            encounter.getTreasureItems().add(treasureItem);
        
        if (StringUtils.isTrivial(encAnnounce))
            encounter.setAnnounce(null);
        else
            encounter.setAnnounce(encAnnounce);
        calculateEncounterTotals(encounter);
        user.setTotalFights(user.getTotalFights() + 1);
        DebugUtils.trace("Summon Monster Tactical Combat started");
        return true;
    }
    
    private static void addExtra(CompUserBean user, CompMonsterTypeBean monster,
            CompEncounterBean encounter)
    {
        addLeaders(user, monster, encounter);
        addAlsoAppearing(user, monster, encounter);
    }
    
    private static void addLeaders(CompUserBean user, CompMonsterTypeBean monster,
            CompEncounterBean encounter)
    {
        if ((monster.getDetails() != null) && monster.getDetails().containsKey("leaders"))
        {
            DebugUtils.trace("Monster "+monster.getID()+" has leaders.");
            JSONArray leaders = JSONUtils.getArray(monster.getDetails(), "leaders");
            for (int j = 0; j < leaders.size(); j++)
            {
                JSONObject leader = (JSONObject)leaders.get(j);
                int forevery = leader.getInt("forEvery");
                String name = leader.getString("name");
                DebugUtils.trace("Leader #"+j+" is '"+name+"' forevery "+forevery+".");
                for (int k = forevery; k < encounter.getMonsters().size(); k += forevery)
                {
                    DebugUtils.trace("Adding '"+name+"' at "+k+".");
                    encounter.getMonsters().add(k, MonsterLogic.createInstance(user, name));
                }
            }
        }
        else
            DebugUtils.trace("Monster "+monster.getID()+" has no leaders.");
    }

    private static void addAlsoAppearing(CompUserBean user, CompMonsterTypeBean monster,
            CompEncounterBean encounter)
    {
        int num = encounter.getMonsters().size();
        if ((monster.getDetails() != null) && monster.getDetails().containsKey("appearsWith"))
        {
            DebugUtils.trace("Monster "+monster.getID()+" has appearsWith.");
            JSONArray appearsWith = JSONUtils.getArray(monster.getDetails(), "appearsWith");
            for (int j = 0; j < appearsWith.size(); j++)
            {
                JSONObject appearing = (JSONObject)appearsWith.get(j);
                int ifmorethan = appearing.getInt("ifMoreThan");
                int chance = appearing.getInt("chance");
                if (chance > 0)
                    if (BaseUserState.RND.nextInt(100) >= chance)
                        continue;
                String name = appearing.getString("name");
                DebugUtils.trace("Appears With #"+j+" is '"+name+"' ifmorethan "+ifmorethan+".");
                if (num >= ifmorethan)
                {
                    CompMonsterTypeBean type = MonsterLogic.getMonsterType(name);
                    if (type == null)
                        DebugUtils.trace("Cannot find '"+name+"' in monster index!");
                    else
                    {
                        int add = type.getEnc(BaseUserState.RND);
                        DebugUtils.trace("Adding "+add+" x '"+name+"'");
                        while (add-- > 0)
                            encounter.getMonsters().add(MonsterLogic.createInstance(user, name));
                    }
                }
            }
        }
        else
            DebugUtils.trace("Monster "+monster.getID()+" has no leaders.");
    }
    
    private static boolean checkCombatStrategic(CompContextBean context, CompUserBean user)
    {
        SquareBean sq = context.getSquare();
        if (sq.isTown() || sq.isCastle())
            return false;
        int roll = BaseUserState.RND.nextInt(60);
        int target = 10;
        if (CompConstLogic.isNightime(user.getTotalTime()))
            target *= 2;
        if (roll > target)
        {
            if (roll <= target + 10)
            {
                CompMonsterTypeBean monster = MonsterLogic.getFromTerrain(sq, user.getChallengeLevel());
                context.setSpoor(monster);
                user.getEncounter().setLastSpoorID(monster.getID());
                user.getEncounter().setLastSpoorInteraction(user.getInteractions());
            }
            return false;
        }
        createStrategicCombat(user, sq);
        return true;
    }

    static void createStrategicCombat(CompUserBean user, SquareBean sq)
    {
        CompEncounterBean encounter = user.getEncounter();
        encounter.setRound(1);
        encounter.getMonsters().clear();
        CompMonsterTypeBean monster;
        if (!StringUtils.isTrivial(encounter.getLastSpoorID()) && (user.getInteractions() <= encounter.getLastSpoorInteraction() + 1))
            monster = MonsterLogic.getMonsterType(encounter.getLastSpoorID());
        else
            monster = MonsterLogic.getFromTerrain(sq, user.getChallengeLevel());
        int num = monster.getEncRoll().roll(BaseUserState.RND);
        for (int i = 0; i < num; i++)
            encounter.getMonsters().add(MonsterLogic.createInstance(user, monster.getID()));
        addExtra(user, monster, encounter);
        encounter.getTreasures().clear();
        encounter.getTreasureItems().clear();
        calculateEncounterTotals(encounter);
        for (CompCompanionBean companion : user.getCompanions())
            companion.setLimitedAttackUse(0);
        user.setTotalFights(user.getTotalFights() + 1);
        DebugUtils.trace("Strategic Combat started");
    }

    private static void calculateEncounterTotals(CompEncounterBean encounter)
    {
        int initialHits = 0;
        for (CompMonsterInstanceBean monst : encounter.getMonsters())
            initialHits += monst.getFullHitPoints();
        encounter.setInitialHits(initialHits);
        encounter.setMorale(BaseUserState.RND.nextFloat()*.2f - .1f); // +/- 10% morale
    }
    
    public static void activate(CompContextBean context, String id)
    {
        CompUserBean user = context.getUser();
        CompCompanionBean comp = user.getCompanion(id);
        if (comp == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_COMPANION_ID_SPECIFIED_XXX, id);
            return;
        }
        user.setActiveCompanion(comp.getID());
        user.getCompanions().remove(comp);
        user.getCompanions().add(0, comp); // move to front
        context.addMessage(CompanionsModelConst.TEXT_YOUR_ACTIVE_COMPANION_IS_NOW_XXX, comp.getName());
        CompIOLogic.saveUser(user);
    }

    public static void equip(CompContextBean context, String companionID, String itemID, int amnt)
    {
        DebugUtils.trace("UserLogic.equip(itemID="+itemID+", amnt="+amnt+", comp="+companionID);
        CompUserBean user = context.getUser();
        CompItemInstanceBean item = user.getItem(itemID);
        if (item == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ITEM_ID_SPECIFIED_XXX, itemID);
            return;
        }
        if (CompItemTypeBean.TYPE_AMMO == item.getType().getType())
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ITEM_AMMO);
            return;
        }
        if (CompItemTypeBean.TYPE_CASTLE == item.getType().getType())
        {
            CastleLogic.equipCastle(context, user, item);
            return;
        }
        if (CompItemTypeBean.TYPE_PERMIT == item.getType().getType())
        {
            PermitLogic.equipPermit(context, user, item);
            return;
        }
        if ((amnt <= 0) || (amnt > item.getQuantity()))
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ITEM_AMOUNT, amnt);
            return;
        }
        CompCompanionBean comp;
        if (CompItemTypeBean.TYPE_POTION == item.getType().getType())
            comp = user.getAnyCompanion(companionID);
        else
            comp = user.getCompanion(companionID);
        if (comp == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_COMPANION_ID_SPECIFIED_XXX, companionID);
            return;
        }
        if (CompItemTypeBean.TYPE_POTION == item.getType().getType())
            PotionLogic.equipPotion(context, itemID, amnt, user, comp, item);
        else if (!StringUtils.isTrivial(item.getName()))
            equipNamedItem(context, item, user, comp);
        else
            equipNormalItem(context, itemID, amnt, user, comp, item);
        CompIOLogic.saveUser(user);
    }

    private static void equipNormalItem(CompContextBean context, String itemID,
            int amnt, CompUserBean user, CompCompanionBean comp,
            CompItemInstanceBean item)
    {
        UserLogic.doRemoveItem(user, itemID, amnt);
        CompanionLogic.doAddItem(user, comp, itemID, amnt);
        context.addMessage(CompanionsModelConst.TEXT_XXX_IS_NOW_USING_YYY, comp.getName(), item.getFullName());
        if (item.getType().isLearnable())
            if (!comp.getProficiencies().contains(item.getType().getBaseID()))
            {
                if (comp.getProficiencies().size() == comp.getWeaponProficiences())
                {
                    String prof = comp.getOldestProficiency();
                    if (prof != null)
                        comp.getProficiencies().remove(prof);
                }
                if (comp.getProficiencies().size() < comp.getWeaponProficiences())
                {
                    comp.getProficiencies().add(item.getType().getBaseID());
                    context.addMessage(CompanionsModelConst.TEXT_XXX_TAKES_A_PROFICIENCY_IN_YYY, comp.getName(), item.getBaseType().getName());
                }
            }
    }

    private static void equipNamedItem(CompContextBean context, CompItemInstanceBean item, CompUserBean user, CompCompanionBean comp)
    {
        user.getItems().remove(item);
        comp.getItems().add(0, item);
        context.addMessage(CompanionsModelConst.TEXT_XXX_IS_NOW_USING_YYY, comp.getName(), item.getFullName());
        if (item.getType().isLearnable())
            if (!comp.getProficiencies().contains(item.getType().getBaseID()))
            {
                if (comp.getProficiencies().size() == comp.getWeaponProficiences())
                {
                    String prof = comp.getOldestProficiency();
                    if (prof != null)
                        comp.getProficiencies().remove(prof);
                }
                if (comp.getProficiencies().size() < comp.getWeaponProficiences())
                {
                    comp.getProficiencies().add(item.getType().getBaseID());
                    context.addMessage(CompanionsModelConst.TEXT_XXX_TAKES_A_PROFICIENCY_IN_YYY, comp.getName(), item.getBaseType().getName());
                }
            }
    }

    public static void unequip(CompContextBean context, String companionID, String itemID, int amnt)
    {
        CompUserBean user = context.getUser();
        CompCompanionBean comp = user.getCompanion(companionID);
        if (comp == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_COMPANION_ID_SPECIFIED_XXX, companionID);
            return;
        }
        CompItemInstanceBean item = comp.getItem(itemID);
        if (item == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ITEM_ID_SPECIFIED_XXX, itemID);
            return;
        }
        if ((amnt <= 0) || (amnt > item.getQuantity()))
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_ITEM_AMOUNT, amnt);
            return;
        }
        if (!StringUtils.isTrivial(item.getName()))
        {
            comp.getItems().remove(item);
            user.getItems().add(0, item);
        }
        else
        {
            CompanionLogic.doRemoveItem(comp, itemID, amnt);
            UserLogic.doAddItem(user, itemID, amnt);
        }
        context.addMessage(CompanionsModelConst.TEXT_XXX_IS_NO_LONGER_USING_YYY, comp.getName(), item.getFullName());
        CompIOLogic.saveUser(user);
    }

    public static void resetUser(CompUserBean user, String flags)
    {
        user.getDeadCompanions().clear();
        user.getItems().clear();
        user.getCompanions().clear();
        user.getEncounter().setRound(-1);
        user.getEncounter().getMonsters().clear();
        if (flags.toLowerCase().indexOf("cirrane") >= 0)
            user.setLocation(CompConstLogic.INITIAL_LOCATION_CIRRANE);
        else
            user.setLocation(CompConstLogic.INITIAL_LOCATION);
        for (int i = 0; i < CompConstLogic.INITIAL_COMPANIONS; i++)
        {
            CompCompanionBean comp = CompanionLogic.newInstance(CompConstLogic.RACE_HUMAN);
            addCompanion(user, comp);
        }
        user.setActiveCompanion(user.getCompanions().get(0).getID());
        
    }

    public static void hire(CompContextBean context, String id)
    {
        CompOperationLogic.fillContext(context);
        CompRoomBean shop = context.getRoom();
        if ((shop == null) || !"hall".equals(shop.getType()))
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_CAN_ONLY_HIRE_PEOPLE_IN_A_GUILD_HALL);
            context.setError(true);
            return;
        }
        CompUserBean user = context.getUser();
        if (user.getCompanions().size() >= CompConstLogic.MAX_COMPANIONS)
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_CAN_ONLY_HIRE_PEOPLE_IN_A_GUILD_HALL, CompConstLogic.MAX_COMPANIONS);
            context.setError(true);
            return;
        }
        JSONObject[] hires = JSONUtils.toObjectArray((JSONArray)(shop.getParams().get("hires")));
        CompCompanionBean hire = null;
        for (int i = 0; i < hires.length; i++)
        {
            CompCompanionBean h = new CompCompanionBean();
            h.fromJSON(hires[i]);
            if (h.getID().equals(id))
            {
                hire = h;
                break;
            }
        }
        if (hire == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_COMPANION_ID_SPECIFIED_XXX, id);
            context.setError(true);
            return;
        }
        addCompanion(user, hire);
        context.addMessage(CompanionsModelConst.TEXT_XXX_JOINS_YOUR_COMPANY, hire.getName());
        CompIOLogic.saveUser(user);
        context.setError(false);
    }

    public static void fire(CompContextBean context, String id)
    {
        CompUserBean user = context.getUser();
        CompCompanionBean comp = user.getAnyCompanion(id);
        if (comp == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_COMPANION_ID_SPECIFIED_XXX, id);
            context.setError(true);
            return;
        }
        AudioMessageBean q = new AudioMessageBean(CompanionsModelConst.TEXT_ARE_YOU_SURE_YOU_WANT_TO_FIRE_XXX, comp.getName());
        JSONObject question = new JSONObject();
        question.put(QueryLogic.QUERY_TEXT, q.toJSON());
        question.put(QueryLogic.QUERY_ACTION, "fire");
        question.put(QueryLogic.QUERY_ID, id);
        if (user.getMetadata() == null)
            user.setMetadata(new JSONObject());
        user.getMetadata().put(CompUserBean.META_QUESTION, question);
        CompIOLogic.saveUser(user);
        context.setError(false);
    }
    
    public static void doYesFire(CompContextBean context, String id)
    {
        CompUserBean user = context.getUser();
        CompCompanionBean comp = user.getAnyCompanion(id);
        if (comp == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_COMPANION_ID_SPECIFIED_XXX, id);
            context.setError(true);
            return;
        }

        if (user.getCompanions().contains(comp)) {
            user.getCompanions().remove(comp);
        } else if (user.getDeadCompanions().contains(comp)) {
            user.getDeadCompanions().remove(comp);
        } else {
            context.addMessage(CompanionsModelConst.TEXT_BAD_COMPANION_ID_SPECIFIED_XXX, id);
            context.setError(true);
            return;
        }

        if (id.equals(user.getActiveCompanion()))
            if (user.getCompanions().size() > 0)
                user.setActiveCompanion(user.getCompanions().get(0).getID());
            else
                user.setActiveCompanion(null);
        context.addMessage(CompanionsModelConst.TEXT_YOU_PART_COMPANY_WITH_XXX, comp.getName());
        int numCompanions = context.getUser().getCompanions().size();
        context.addMessage(CompanionsModelConst.TEXT_YOU_NOW_HAVE_XXX_OUT_OF_YYY_COMPANIONS, numCompanions, CompConstLogic.MAX_COMPANIONS);
        if (numCompanions == CompConstLogic.MAX_COMPANIONS)
            context.addMessage(CompanionsModelConst.TEXT_YOU_CANT_HIRE_ANY_MORE);
        else
            context.addMessage(CompanionsModelConst.TEXT_YOU_CAN_HIRE_XXX_MORE, CompConstLogic.MAX_COMPANIONS - numCompanions);
        user.getMetadata().remove(CompUserBean.META_QUESTION);
        CompIOLogic.saveUser(user);
        context.setError(false);
    }
    
    public static void doNoFire(CompContextBean context, String id)
    {
        CompUserBean user = context.getUser();
        CompCompanionBean comp = user.getCompanion(id);
        if (comp == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_BAD_COMPANION_ID_SPECIFIED_XXX, id);
            context.setError(true);
            return;
        }
        context.addMessage(CompanionsModelConst.TEXT_YOU_DECIDE_NOT_TO_PART_COMPANY_WITH_XXX, comp.getName());
        user.getMetadata().remove(CompUserBean.META_QUESTION);
        CompIOLogic.saveUser(user);
        context.setError(false);
    }

    public static void consumeNews(CompContextBean context, int lastMessage)
    {
        context.getUser().setLastMessage(lastMessage);
        CompIOLogic.saveUser(context.getUser());
    }

    public static void sleep(CompContextBean context)
    {
        CompOperationLogic.fillContext(context);
        CompUserBean user = context.getUser();
        if (checkCombat(context, user))
        {
            context.addMessage(CompanionsModelConst.TEXT_YOUR_SLEEP_IS_INTERRUPTED);
            return;
        }
        int sleepTime;
        if (CompConstLogic.isDaytime(user.getTotalTime()))
        {
            sleepTime = CompConstLogic.ONE_HOUR*8;
            context.addMessage(CompanionsModelConst.TEXT_YOU_SLEEP_FOR_EIGHT_HOURS);
        }
        else
        {
            int minute = CompConstLogic.getMinuteOfDay(user.getTotalTime());
            if (minute < 6*CompConstLogic.ONE_HOUR)
                sleepTime = 6*CompConstLogic.ONE_HOUR - minute;
            else
                sleepTime = (CompConstLogic.ONE_DAY - minute) + 6*CompConstLogic.ONE_HOUR;
            context.addMessage(CompanionsModelConst.TEXT_YOU_SLEEP_FOR_THE_REST_OF_THE_NIGHT);
        }        
        UserLogic.passTheTime(context, user, sleepTime);            
        CompIOLogic.saveUser(user);
    }

    public static AudioMessageBean makeLonLatMessage(GeoBean loc)
    {
        return makeLonLatMessage(loc, CompanionsModelConst.TEXT_YOU_ARE_AT_AAA_DEGREES_BBB_MINUTES_CCC_LATITUDE_AND_DDD_DEGREES_EEE_MINUTES_FFF_LONGITUDE);
    }

    public static AudioMessageBean makeLonLatMessage(GeoBean loc, String ident)
    {
        Object[] args = new Object[6];
        int lat = loc.getLattitude();
        int lon = loc.getLongitude();
        args[0] = Math.abs(lat)/60;
        args[1] = Math.abs(lat)%60;
        args[2] = (lat < 0) ? "{{North}}" : "{{South}}";
        args[3] = Math.abs(lon)/60;
        args[4] = Math.abs(lon)%60;
        args[5] = (lon < 0) ? "{{West}}" : "{{East}}";
        return new AudioMessageBean(ident, args);
    }
}
