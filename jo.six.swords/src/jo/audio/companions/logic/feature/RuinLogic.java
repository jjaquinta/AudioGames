package jo.audio.companions.logic.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.feature.ruin.CastleLogic;
import jo.audio.companions.logic.feature.ruin.DemonLogic;
import jo.audio.companions.logic.feature.ruin.DenLogic;
import jo.audio.companions.logic.feature.ruin.DevilLogic;
import jo.audio.companions.logic.feature.ruin.DinoLogic;
import jo.audio.companions.logic.feature.ruin.DragonLogic;
import jo.audio.companions.logic.feature.ruin.GiantLogic;
import jo.audio.companions.logic.feature.ruin.GroveLogic;
import jo.audio.companions.logic.feature.ruin.MineLogic;
import jo.audio.companions.logic.feature.ruin.TempleLogic;
import jo.audio.companions.logic.feature.ruin.TempleTemplate;
import jo.audio.util.BaseUserState;
import jo.util.utils.obj.StringUtils;

public class RuinLogic
{
    public static final long MONSTER_TIMEOUT = 15*60*1000L; 

    private static final int[][] SUB_TYPE_TABLE = {
    // TERRAIN_PLAINS = 0;
            { 0,0,0, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_RUIN, CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_RUIN,
                CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_DRAGON, CompConstLogic.FEATURE_SUB_DEVIL,
            },
    // TERRAIN_HILLS = 1;
            { 0,0,0, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_RUIN, CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_RUIN,
                CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_GIANT, CompConstLogic.FEATURE_SUB_GIANT,
                CompConstLogic.FEATURE_SUB_GIANT, CompConstLogic.FEATURE_SUB_DRAGON, CompConstLogic.FEATURE_SUB_DEMON,
            },
    // TERRAIN_MOUNTAINS = 2;
            { 0,0,0, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_RUIN, CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_RUIN,
                CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_GIANT, CompConstLogic.FEATURE_SUB_GIANT,
                CompConstLogic.FEATURE_SUB_GIANT, CompConstLogic.FEATURE_SUB_DRAGON, CompConstLogic.FEATURE_SUB_DEVIL,
            },
    // TERRAIN_ARTIC = 3;
            { 0,0,0, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_RUIN, CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_RUIN,
                CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_GIANT, CompConstLogic.FEATURE_SUB_GIANT,
                CompConstLogic.FEATURE_SUB_GIANT, CompConstLogic.FEATURE_SUB_DRAGON, CompConstLogic.FEATURE_SUB_DEMON,
            },
    // TERRAIN_FOREST = 4;
            { 0,0,0, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_RUIN, CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_RUIN,
                CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_GROVE, CompConstLogic.FEATURE_SUB_GROVE,
                CompConstLogic.FEATURE_SUB_GROVE, CompConstLogic.FEATURE_SUB_DRAGON, CompConstLogic.FEATURE_SUB_DEVIL,
            },
    // TERRAIN_DESERT = 5;
            { 0,0,0, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_RUIN, CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_RUIN,
                CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_DEVIL,
                CompConstLogic.FEATURE_SUB_DEMON, CompConstLogic.FEATURE_SUB_DRAGON, CompConstLogic.FEATURE_SUB_DEMON,
            },
    // TERRAIN_JUNGLE = 6;
            { 0,0,0, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_RUIN, CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_RUIN,
                CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_GROVE, CompConstLogic.FEATURE_SUB_GROVE,
                CompConstLogic.FEATURE_SUB_GROVE, CompConstLogic.FEATURE_SUB_DRAGON, CompConstLogic.FEATURE_SUB_DEVIL,
            },
    // TERRAIN_SWAMP = 7;
            { 0,0,0, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_RUIN, CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_RUIN,
                CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_DINO, CompConstLogic.FEATURE_SUB_DINO,
                CompConstLogic.FEATURE_SUB_DINO, CompConstLogic.FEATURE_SUB_DRAGON, CompConstLogic.FEATURE_SUB_DEMON,
            },
    // TERRAIN_FRESHWATER = 8;
            { 0,0,0, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_RUIN, CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_RUIN,
                CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_DRAGON, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_DEVIL,
            },
    // TERRAIN_SALTWATER = 9;
            { 0,0,0, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_RUIN, CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_RUIN,
                CompConstLogic.FEATURE_SUB_DEN, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE,
                CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_MINE, CompConstLogic.FEATURE_SUB_TEMPLE,
                CompConstLogic.FEATURE_SUB_DRAGON, CompConstLogic.FEATURE_SUB_TEMPLE, CompConstLogic.FEATURE_SUB_DEMON,
            },
    };
    
    public static void generateRuin(FeatureBean feature, SquareBean sq, Random rnd, List<String> expansions, boolean athiest)
    {
        generateRuin(feature, sq, rnd, expansions, athiest, SUB_TYPE_TABLE);
    }
    
    public static void generateRuin(FeatureBean feature, SquareBean sq, Random rnd, List<String> expansions, boolean athiest,
            int[][] subTypeTable)
    {
        int roll = DiceRollBean.roll(rnd, 3, 6);
        roll += sq.getTerrainDepth();
        roll += sq.getChallenge();
        while (roll > 20)
            roll -= 18;
        int subType = subTypeTable[sq.getTerrain()][roll];
        System.out.println(sq.getOrds()+" -> "+CompConstLogic.FEATURE_SUB_NAMES[subType]+" roll="+roll+", depth="+sq.getTerrainDepth()
            +" cr="+sq.getChallenge());
        switch (subType)
        {
            case CompConstLogic.FEATURE_SUB_MINE:
                MineLogic.generateMine(feature, sq, rnd);
                break;
            case CompConstLogic.FEATURE_SUB_RUIN:
                CastleLogic.generateCastle(feature, sq, rnd);
                break;
            case CompConstLogic.FEATURE_SUB_TEMPLE:
                TempleLogic.generateTemple(feature, sq, rnd, expansions);
                break;
            case CompConstLogic.FEATURE_SUB_DEN:
                DenLogic.generateDen(feature, sq, rnd);
                break;
            case CompConstLogic.FEATURE_SUB_DEMON:
                DemonLogic.generateDemon(feature, sq, rnd);
                break;
            case CompConstLogic.FEATURE_SUB_DEVIL:
                DevilLogic.generateDevil(feature, sq, rnd);
                break;
            case CompConstLogic.FEATURE_SUB_DINO:
                DinoLogic.generateDino(feature, sq, rnd);
                break;
            case CompConstLogic.FEATURE_SUB_GROVE:
                GroveLogic.generateGrove(feature, sq, rnd);
                break;
            case CompConstLogic.FEATURE_SUB_GIANT:
                GiantLogic.generateGiantHall(feature, sq, rnd, expansions);
                break;
            case CompConstLogic.FEATURE_SUB_DRAGON:
                DragonLogic.generateDragonLair(feature, sq, rnd);
                break;
        }
//        System.out.println("Feature: "+feature.getName("en_US"));
//        for (CompRoomBean room : feature.getRooms())
//            System.out.println("\t"+room.getID());
    }

    public static void populateRoomsByDepth(FeatureBean feature, int div, Object postBoss)
    {
        List<CompRoomBean> monsterRooms = new ArrayList<>();
        for (CompRoomBean room : feature.getRooms())
            if (isEncounterChallenge(room) && !isPopulateSkip(room))
                monsterRooms.add(room);
        int cr = monsterRooms.size()/div;
        for (int idx = monsterRooms.size() - 1; idx > 0; idx -= div)
        {
            CompRoomBean r = monsterRooms.get(idx);
            r.setType(CompRoomBean.TYPE_ENCOUNTER);
            r.getParams().put(CompRoomBean.MD_ENCOUNTER_CHALLENGE, cr--);
            r.getParams().put(CompRoomBean.MD_WAIT_TIME, RuinLogic.MONSTER_TIMEOUT);
            if (idx == monsterRooms.size() - 1)
            {
                r.getParams().put("boss", true);
                if (postBoss != null)
                    r.getParams().put("postCombat", postBoss);
            }
        }
    }
    
    private static boolean isEncounterChallenge(CompRoomBean room)
    {
        if (room.getParams() == null)
            return false;
        return room.getParams().containsKey(CompRoomBean.MD_ENCOUNTER_CHALLENGE);
    }
    
    private static boolean isPopulateSkip(CompRoomBean room)
    {
        if (room.getParams() == null)
            return false;
        return "skip".equalsIgnoreCase((String)room.getParams().get(CompRoomBean.MD_POPULATE));
    }

    // MK3
    public static void sortRoomsByDepth(FeatureBean feature)
    {
        // determine depths
        final Map<String, Integer> depths = new HashMap<String, Integer>();
        Map<String, CompRoomBean> todo = new HashMap<>();
        String entranceID = feature.getEntranceID();
        if (entranceID == null)
            entranceID = feature.getRooms().get(0).getID();
        todo.put(entranceID, FeatureLogic.findRoom(feature, entranceID));
        depths.put(entranceID, 1);
        while (todo.size() > 0)
        {
            String roomID = todo.keySet().iterator().next();
            CompRoomBean room = todo.get(roomID);
            if (room == null)
                System.err.println("Cannot find room ID "+roomID+" in "+feature.getLocation());
            todo.remove(roomID);
            int depth = depths.get(roomID);
            for (int dir = 0; dir < 4; dir++)
            {
                String id = room.getDirection(dir);
                if (StringUtils.isTrivial(id) || id.startsWith("$") || depths.containsKey(id))
                    continue;
                depths.put(id, depth + 1);
                todo.put(id, FeatureLogic.findRoom(feature, id));
            }
        }
        // sort rooms
        Collections.sort(feature.getRooms(), new Comparator<CompRoomBean>() {
            @Override
            public int compare(CompRoomBean o1, CompRoomBean o2)
            {
                Integer d1 = depths.get(o1.getID());
                if (d1 == null)
                {
                    System.err.println("Disconnected room: "+o1.getID());
                    d1 = 999;
                    depths.put(o1.getID(), d1);
                }
                Integer d2 = depths.get(o2.getID());
                if (d2 == null)
                {
                    System.err.println("Disconnected room: "+o2.getID());
                    d2 = 999;
                    depths.put(o2.getID(), d2);
                }
                return d1 - d2;
            }
        });
    }

    /* MK 2
    public static void sortRoomsByDepth(FeatureBean feature)
    {
        // determine depths
        final Map<String, Integer> depths = new HashMap<String, Integer>();
        for (;;)
        {
            boolean anyNew = false;
            for (CompRoomBean r : feature.getRooms())
            {
                Integer minDepth = null;
                for (String next : new String[] { r.getNorth(), r.getSouth(), r.getEast(), r.getWest() })
                    if ("$exit".equals(next))
                        minDepth = 0;
                    else if (depths.containsKey(next))
                    {
                        if (minDepth == null)
                            minDepth = depths.get(next) + 1;
                        else if (depths.get(next) + 1 < minDepth)
                            minDepth = depths.get(next) + 1;
                    }
                if (minDepth != null)
                {
                    if (!minDepth.equals(depths.get(r.getID())))
                        anyNew = true;
                    depths.put(r.getID(), minDepth);
                }
            }
            if (!anyNew)
                break;
        }
        // sort rooms
        Collections.sort(feature.getRooms(), new Comparator<CompRoomBean>() {
            @Override
            public int compare(CompRoomBean o1, CompRoomBean o2)
            {
                Integer d1 = depths.get(o1.getID());
                if (d1 == null)
                {
                    System.err.println("Disconnected room: "+o1.getID());
                    d1 = 999;
                    depths.put(o1.getID(), d1);
                }
                Integer d2 = depths.get(o2.getID());
                if (d2 == null)
                {
                    System.err.println("Disconnected room: "+o2.getID());
                    d2 = 999;
                    depths.put(o2.getID(), d2);
                }
                return d1 - d2;
            }
        });
    }
    */

    /* MK 1
    public static void sortRoomsByDepth(FeatureBean feature)
    {
        List<CompRoomBean> rooms = new ArrayList<>();
        Set<String> done = new HashSet<>();
        // first pass, all exits
        //int depth = 0;
        for (CompRoomBean r : feature.getRooms())
            for (String next : new String[] { r.getNorth(), r.getSouth(), r.getEast(), r.getWest() })
                if ("$exit".equals(next))
                {
                    rooms.add(r);
                    done.add(r.getID());
                    //System.out.println("Depth "+depth+":"+r.getID());
                    break;
                }
        int lastEnd = 0;
        while (rooms.size() < feature.getRooms().size())
        {
            //depth++;
            boolean doneAny = false;
            int end = rooms.size();
            for (int i = lastEnd; i < end; i++)
            {
                CompRoomBean r1 = rooms.get(i);
                for (String next : new String[] { r1.getNorth(), r1.getSouth(), r1.getEast(), r1.getWest() })
                    if ((next != null) && !done.contains(next))
                    {
                        CompRoomBean r2 = FeatureLogic.findRoom(feature, next);
                        if (r2 != null)
                        {
                            rooms.add(r2);
                            done.add(r2.getID());
                            //System.out.println("Depth "+depth+":"+r2.getID()+" via "+r1.getID());
                            doneAny = true;
                        }
                    }
            }
            lastEnd = end;
            if (!doneAny)
                System.err.println("Error sorting rooms!");
        }
        feature.getRooms().clear();
        feature.getRooms().addAll(rooms);
    }
    */

    public static void addAllBut(List<DigOptions> options, CompRoomBean to,
            int opposite)
    {
        for (int dir = 0; dir < 4; dir++)
            if (dir != opposite)
                options.add(new DigOptions(to, dir));
    }
    
    public static int findFreeExit(CompRoomBean room, Random rnd)
    {
        List<Integer> exits = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            if (room.getDirection(i) == null)
                exits.add(i);
        if (exits.size() == 0)
            return -1;
        return exits.get(rnd.nextInt(exits.size()));
    }
    
    public static List<DigOptions> determineCityRooms(FeatureBean feature, Random rnd, DemenseBean dem, List<String> expansions, int numRooms,
            TempleTemplate[] templates)
    {
        TempleTemplate template = null;
        List<TempleTemplate> candidates = new ArrayList<>();
        for (TempleTemplate t : templates)
            if (numRooms <= t.getExtensionsNum())
                candidates.add(t);
        if (candidates.size() > 3)
        {
            Collections.sort(candidates, new Comparator<TempleTemplate>() {
                @Override
                public int compare(TempleTemplate o1, TempleTemplate o2)
                {
                    return o1.getExtensionsNum() - o2.getExtensionsNum();
                }
            });
            while (candidates.size() > 3)
                candidates.remove(3);
        }
        if (candidates.size() == 0)
        {
            System.err.println("Cannot find template to match "+numRooms);
            for (int i = 0; i < templates.length; i++)
                System.err.println("#"+i+" min="+templates[i].minRooms+", max="+templates[i].maxRooms+", extensions="+templates[i].getExtensionsNum());
            template = templates[0];
        }
        else
            template = candidates.get(rnd.nextInt(candidates.size()));
        return determineRooms(feature, rnd, dem, expansions, numRooms,
                null, template);
    }
    
    public static List<DigOptions> determineTempleRooms(FeatureBean feature, Random rnd, DemenseBean dem, List<String> expansions, int numRooms,
            TempleTemplate[] templates, String[] additionalRoomIDs)
    {
        TempleTemplate template = null;
        for (TempleTemplate t : templates)
            if ((numRooms >= t.minRooms) && (numRooms <= t.maxRooms))
                if ((template == null) || BaseUserState.RND.nextBoolean())
                    template = t;
        if (template == null)
        {
            System.err.println("Cannot find template to match "+numRooms+" for "+additionalRoomIDs[0]);
            for (int i = 0; i < templates.length; i++)
                System.err.println("#"+i+" min="+templates[i].minRooms+", max="+templates[i].maxRooms+", extensions="+templates[i].getExtensionsNum());
            template = templates[0];
        }
        return determineRooms(feature, rnd, dem, expansions, numRooms,
                additionalRoomIDs, template);
    }

    public static List<DigOptions> determineRooms(FeatureBean feature,
            Random rnd, DemenseBean dem, List<String> expansions, int numRooms,
            String[] additionalRoomIDs, TempleTemplate template)
    {
        CompRoomBean[][] rooms = new CompRoomBean[template.template.length][];
        expansions.add(String.valueOf(PantheonLogic.randomGod(rnd, dem, 1f)));
        // make rooms
        numRooms = createFromTemplate(feature, numRooms, template, rooms);
        List<DigOptions> options = connectRooms(template, rooms);
        if (additionalRoomIDs != null)
            numRooms = roundOutRooms(feature, rnd, numRooms, options, additionalRoomIDs);
        return options;
    }

    private static int createFromTemplate(FeatureBean feature, int numRooms,
            TempleTemplate template, CompRoomBean[][] rooms)
    {
        for (int y = 0; y < template.template.length; y++)
        {
            rooms[y] = new CompRoomBean[template.template[y].length];
            for (int x = 0; x < template.template[y].length; x++)
            {
                String id = template.ids[y][x];
                if (id != null)
                {
                    rooms[y][x] = FeatureLogic.getRoom(id);
                    if (rooms[y][x] == null)
                        System.err.println("No room with ID '"+id+"'");
                    rooms[y][x].setID(rooms[y][x].getID()+feature.getRooms().size());
                    rooms[y][x].setParams(new JSONObject());
                    rooms[y][x].getParams().put(CompRoomBean.MD_ENCOUNTER_CHALLENGE, 0);
                    rooms[y][x].getParams().put(CompRoomBean.MD_WAIT_TIME, RuinLogic.MONSTER_TIMEOUT);
                    feature.getRooms().add(rooms[y][x]);
                    numRooms--;
                }
            }
        }
        return numRooms;
    }

    public static int roundOutRooms(FeatureBean feature, Random rnd,
            int numRooms, List<DigOptions> options, String[] additionalRoomIDs)
    {
        while ((options.size() > 0) && (numRooms > 0))
        {
            int idx = rnd.nextInt(options.size());
            DigOptions opt = options.get(idx);
            options.remove(idx);
            int opposite = CompRoomBean.opposite(opt.dir);
            CompRoomBean to = FeatureLogic.getRoom(additionalRoomIDs[rnd.nextInt(additionalRoomIDs.length)]);
            RuinLogic.addAllBut(options, to, opposite);
            to.setID(to.getID()+feature.getRooms().size());
            JSONObject params = new JSONObject();
            to.setParams(params);
            params.put(CompRoomBean.MD_ENCOUNTER_CHALLENGE, 0);
            params.put(CompRoomBean.MD_WAIT_TIME, RuinLogic.MONSTER_TIMEOUT);
            opt.from.setDirection(opt.dir, to.getID());
            to.setDirection(opposite, opt.from.getID());
            feature.getRooms().add(to);
            numRooms--;
        }
        return numRooms;
    }

    public static List<DigOptions> connectRooms(TempleTemplate template,
            CompRoomBean[][] rooms)
    {
        List<DigOptions> options = new ArrayList<>();
        for (int y = 0; y < template.template.length; y++)
        {
            for (int x = 0; x < template.template[y].length; x++)
                if (rooms[y][x] != null)
                {
                    if (template.isEntry(x, y))
                        rooms[y][x].setDirection(template.getEntryDir(x, y), "$exit");
                    String id = template.template[y][x];
                    if (id.indexOf("<") >= 0)
                    {
                        if (rooms[y][x-1] != null)
                            rooms[y][x].setWest(rooms[y][x-1].getID());
                        else
                            options.add(new DigOptions(rooms[y][x], 3));
                    }
                    if (id.indexOf(">") >= 0)
                    {
                        if (rooms[y][x+1] != null)
                            rooms[y][x].setEast(rooms[y][x+1].getID());
                        else
                            options.add(new DigOptions(rooms[y][x], 2));
                    }
                    if (id.indexOf("^") >= 0)
                    {
                        if (rooms[y-1][x] != null)
                            rooms[y][x].setNorth(rooms[y-1][x].getID());
                        else
                            options.add(new DigOptions(rooms[y][x], 0));
                    }
                    if (id.indexOf(".") >= 0)
                    {
                        if (rooms[y+1][x] != null)
                            rooms[y][x].setSouth(rooms[y+1][x].getID());
                        else
                            options.add(new DigOptions(rooms[y][x], 1));
                    }
                }
        }
        return options;
    }


    public static void determineCastleRooms(FeatureBean feature, Random rnd, int numRooms, 
            String entryID, String courtyardID, String[] roomIDs)
    {
        CompRoomBean entry = FeatureLogic.getRoom(entryID);
        entry.setID(entry.getID()+feature.getRooms().size());
        entry.setSouth("$exit");
        feature.getRooms().add(entry);
        CompRoomBean courtyard = FeatureLogic.getRoom(courtyardID);
        courtyard.setID(courtyard.getID()+feature.getRooms().size());
        courtyard.setSouth(entry.getID());
        entry.setNorth(courtyard.getID());
        feature.getRooms().add(courtyard);
        List<DigOptions> options = new ArrayList<>();
        options.add(new DigOptions(courtyard, 0));
        options.add(new DigOptions(courtyard, 2));
        options.add(new DigOptions(courtyard, 3));
        while ((options.size() > 0) && (numRooms > 0))
        {
            int idx = rnd.nextInt(options.size());
            DigOptions opt = options.get(idx);
            options.remove(idx);
            int opposite = CompRoomBean.opposite(opt.dir);
            CompRoomBean to = FeatureLogic.getRoom(roomIDs[rnd.nextInt(roomIDs.length)]);
            RuinLogic.addAllBut(options, to, opposite);
            to.setID(to.getID()+feature.getRooms().size());
            JSONObject params = new JSONObject();
            to.setParams(params);
            params.put(CompRoomBean.MD_ENCOUNTER_CHALLENGE, 0);
            params.put(CompRoomBean.MD_WAIT_TIME, RuinLogic.MONSTER_TIMEOUT);
            opt.from.setDirection(opt.dir, to.getID());
            to.setDirection(opposite, opt.from.getID());
            feature.getRooms().add(to);
            numRooms--;
        }
    }
}

