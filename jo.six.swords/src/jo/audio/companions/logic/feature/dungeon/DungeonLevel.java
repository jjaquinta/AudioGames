package jo.audio.companions.logic.feature.dungeon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.feature.RuinLogic;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

class DungeonLevel
{
    private static final int CORRIDOR_CHANCE = 66;
    private static final int ROOM_CHANCE = 50;
    private static final String[] ROOM_IDS = new String[] {
            "DUNGEON_ROOM_1",
            "DUNGEON_ROOM_2",
            "DUNGEON_ROOM_3",
            "DUNGEON_ROOM_4",
            "DUNGEON_ROOM_5",
            "DUNGEON_ROOM_6",
            "DUNGEON_ROOM_7",
            "DUNGEON_ROOM_8",
            "DUNGEON_ROOM_9",
            "DUNGEON_ROOM_10",
            "DUNGEON_ROOM_11",
            "DUNGEON_ROOM_12",
            "DUNGEON_ROOM_13",
            "DUNGEON_ROOM_14",
            "DUNGEON_ROOM_15",
            "DUNGEON_ROOM_16",
            "DUNGEON_ROOM_17",
            "DUNGEON_ROOM_18",
            "DUNGEON_ROOM_19",
            "DUNGEON_ROOM_20",
            "DUNGEON_ROOM_21",
            "DUNGEON_ROOM_22",
            "DUNGEON_ROOM_23",
            "DUNGEON_ROOM_24",
            "DUNGEON_ROOM_25",
            "DUNGEON_ROOM_26",
            "DUNGEON_ROOM_27",
            "DUNGEON_ROOM_28",
            "DUNGEON_ROOM_29",
            "DUNGEON_ROOM_30",
            "DUNGEON_ROOM_31",
            "DUNGEON_ROOM_32",
            "DUNGEON_ROOM_33",
            "DUNGEON_ROOM_34",
            "DUNGEON_ROOM_35",
            "DUNGEON_ROOM_36",
            "DUNGEON_ROOM_37",
            "DUNGEON_ROOM_38",
            "DUNGEON_ROOM_39",
            "DUNGEON_ROOM_40",
    };
    private static final String[] CORRIDOR_EW_IDS = new String[] {
            "DUNGEON_CORRIDOR_EW_1",
            "DUNGEON_CORRIDOR_EW_2",
            "DUNGEON_CORRIDOR_EW_3",
            "DUNGEON_CORRIDOR_EW_4",
            "DUNGEON_CORRIDOR_EW_5",
    };
    private static final String[] CORRIDOR_NS_IDS = new String[] {
            "DUNGEON_CORRIDOR_EW_1",
            "DUNGEON_CORRIDOR_EW_2",
            "DUNGEON_CORRIDOR_EW_3",
            "DUNGEON_CORRIDOR_EW_4",
            "DUNGEON_CORRIDOR_EW_5",
    };
    private static final String[] JUNCTION_IDS = new String[] {
            "DUNGEON_JUNCTION_1"
    };
    private static final int[] DX = new int[] { 0, 0, 1, -1 };
    private static final int[] DY = new int[] { -1, 1, 0, 0 };
    
    private FeatureBean mFeature;
    private Random mRnd;
    private int mL;
    private CompRoomBean[][] mLevel;
    private CompRoomBean mEntrance;
    private CompRoomBean mExit;
    private int mRoomWidth;
    private int mRoomHeight;
    private int mGridWidth;
    private int mGridHeight;
    
    // constructor
    public DungeonLevel(FeatureBean feature, Random rnd, int l)
    {
        mFeature = feature;
        mRnd = rnd;
        mL = l;
        mRoomWidth = DiceRollBean.roll(mRnd, 2, 2);
        mRoomHeight = DiceRollBean.roll(mRnd, 2, 2);
        mGridWidth = mRoomWidth*2 - 1;
        mGridHeight = mRoomHeight*2 - 1;
        mLevel = new CompRoomBean[mGridWidth][mGridHeight];
    }
    
    // utilities

    public void populateRooms(List<CompRoomBean> entrances, List<CompRoomBean> exits)
    {
        for (int x = 0; x < mRoomWidth; x++)
            for (int y = 0; y < mRoomHeight; y++)
                if (DiceRollBean.roll(mRnd, 100) < ROOM_CHANCE)
                {
                    set(x*2, y*2, makeRoom());
                    if (x == 0)
                    {
                        entrances.add(get(x*2, y*2));
                        //System.out.println("+Entrance: "+entrances.get(entrances.size()-1).getParams().getString(CompRoomBean.MD_X)+","+entrances.get(entrances.size()-1).getParams().getString(CompRoomBean.MD_Y));
                    }
                    else if (x == mRoomWidth - 1)
                    {
                        exits.add(get(x*2, y*2));
                        //System.out.println("+Exit: "+exits.get(exits.size()-1).getParams().getString(CompRoomBean.MD_X)+","+exits.get(exits.size()-1).getParams().getString(CompRoomBean.MD_Y));
                    }
                }
        if (entrances.size() == 0)
        {
            entrances.add(makeRoom());
            int ex = 0;
            int ey = mRnd.nextInt(mRoomHeight)*2;
            set(ex, ey, entrances.get(0));
            //System.out.println("+entrance: "+ex+","+ey+" - "+entrances.get(entrances.size()-1).getParams().getString(CompRoomBean.MD_X)+","+entrances.get(entrances.size()-1).getParams().getString(CompRoomBean.MD_Y));
        }
        if (exits.size() == 0)
        {
            exits.add(makeRoom());
            int ex = (mRoomWidth - 1)*2;
            int ey = mRnd.nextInt(mRoomHeight)*2;
            set(ex, ey, exits.get(0));
            //System.out.println("+exit: "+ex+","+ey+" - "+exits.get(exits.size()-1).getParams().getString(CompRoomBean.MD_X)+","+exits.get(exits.size()-1).getParams().getString(CompRoomBean.MD_Y));
        }
    }

    public void populateCorridors()
    {
        for (int x = 0; x < mRoomWidth - 1; x++)
            for (int y = 0; y < mRoomHeight - 1; y++)
            {
                if (DiceRollBean.roll(mRnd, 100) < CORRIDOR_CHANCE)
                    set(x*2+1, y*2, makeCorridor(CORRIDOR_EW_IDS));
                if (DiceRollBean.roll(mRnd, 100) < CORRIDOR_CHANCE)
                    set(x*2, y*2+1, makeCorridor(CORRIDOR_NS_IDS));
            }
    }

    public void connectLevel()
    {
        for (;;)
        {
            Map<String,CompRoomBean> roomIndex = new HashMap<>();
            for (int x = 0; x < mGridWidth; x++)
                for (int y = 0; y < mGridHeight; y++)
                    if (get(x, y) != null)
                        roomIndex.put(get(x, y).getID(), get(x, y));
            //debugLevel();
            Set<CompRoomBean> unconnected = new HashSet<>(); 
            Set<CompRoomBean> connected = new HashSet<>();
            unconnected.addAll(roomIndex.values());
            connect(mEntrance, roomIndex, unconnected, connected);
            //System.out.println("Connected: "+connected.size()+", unconnected="+unconnected.size());
            if (unconnected.size() == 0)
                break;
            boolean changed = connectAdjacent(connected, unconnected);
            if (!changed)
                changed = connectNearby(connected, unconnected);
            if (!changed)
            {
                System.out.println("*** Can't connect level!");
                break;
            }
        }
    }
    
    private boolean connectNearby(Set<CompRoomBean> connected, Set<CompRoomBean> unconnected)
    {
        //System.out.println("connectNearby");
        boolean changed = false;
        List<Adjacent> connections = new ArrayList<>();
        for (CompRoomBean cRoom : connected)
        {
            int cx = IntegerUtils.parseInt(cRoom.getParams().get(CompRoomBean.MD_X));
            int cy = IntegerUtils.parseInt(cRoom.getParams().get(CompRoomBean.MD_Y));
            for (CompRoomBean uRoom : unconnected)
            {
                int ux = IntegerUtils.parseInt(uRoom.getParams().get(CompRoomBean.MD_X));
                int uy = IntegerUtils.parseInt(uRoom.getParams().get(CompRoomBean.MD_Y));
                int d = Math.abs(cx - ux) + Math.abs(cy - uy);
                Adjacent a = new Adjacent();
                a.c = cRoom;
                a.u = uRoom;
                a.d = d;
                connections.add(a);
            }
        }
        Collections.sort(connections, new Comparator<Adjacent>() {
            @Override
            public int compare(Adjacent o1, Adjacent o2)
            {
                return o1.d - o2.d;
            }
        });
        for (Adjacent a : connections)
        {
            changed = conectTowards(changed, a.d, a.c, a.u);
            if (changed)
                break;
        }
        return changed;
    }

    public boolean conectTowards(boolean changed, int dist,
            CompRoomBean bestCRoom, CompRoomBean bestURoom)
    {
        int cx = IntegerUtils.parseInt(bestCRoom.getParams().get(CompRoomBean.MD_X));
        int cy = IntegerUtils.parseInt(bestCRoom.getParams().get(CompRoomBean.MD_Y));
        int ux = IntegerUtils.parseInt(bestURoom.getParams().get(CompRoomBean.MD_X));
        int uy = IntegerUtils.parseInt(bestURoom.getParams().get(CompRoomBean.MD_Y));
        //System.out.println("closest "+cx+","+cy+" -"+dist+"-> "+ux+","+uy);
        if (cx != ux)
        {
            int d = (int)Math.signum(ux - cx);
            if (isEWCorridor(cx, cy) && (get(cx+d, cy) == null))
            {
                //System.out.println("junction "+cx+","+cy+" -> "+(cx+d)+","+cy);
                set(cx+d, cy, makeJunction());
                changed = true;
            }
            else if (isRoom(cx, cy) && (get(cx+d, cy) == null))
            {
                //System.out.println("corridor "+cx+","+cy+" -> "+(cx+d)+","+cy);
                set(cx+d, cy, makeCorridor(CORRIDOR_EW_IDS));
                changed = true;
            }
            else if (isEWCorridor(ux, uy) && (get(ux-d, uy) == null))
            {
                //System.out.println("junction "+ux+","+uy+" -> "+(ux-d)+","+uy);
                set(ux-d, uy, makeJunction());
                changed = true;
            }
            else if (isRoom(ux, uy) && (get(ux-d, uy) == null))
            {
                //System.out.println("corridor "+ux+","+uy+" -> "+(ux-d)+","+uy);
                set(ux-d, uy, makeCorridor(CORRIDOR_EW_IDS));
                changed = true;
            }
        }
        if (!changed && (cy != uy))
        {
            int d = (int)Math.signum(uy - cy);
            if (isNSCorridor(cx, cy) && (get(cx, cy+d) == null))
            {
                //System.out.println("junction "+cx+","+cy+" -> "+cx+","+(cy+d));
                set(cx, cy+d, makeJunction());
                changed = true;
            }
            else if (isRoom(cx, cy) && (get(cx, cy+d) == null))
            {
                //System.out.println("corridor "+cx+","+cy+" -> "+cx+","+(cy+d));
                set(cx, cy+d, makeCorridor(CORRIDOR_NS_IDS));
                changed = true;
            }
            else if (isNSCorridor(ux, uy) && (get(ux, uy-d) == null))
            {
                //System.out.println("junction "+ux+","+uy+" -> "+ux+","+(uy-d));
                set(ux, uy-d, makeJunction());
                changed = true;
            }
            else if (isRoom(ux, uy) && (get(ux, uy-d) == null))
            {
                //System.out.println("corridor "+ux+","+uy+" -> "+ux+","+(uy-d));
                set(ux, uy-d, makeCorridor(CORRIDOR_NS_IDS));
                changed = true;
            }
        }
        return changed;
    }
    
    private boolean connectAdjacent(Set<CompRoomBean> connected, Set<CompRoomBean> unconnected)
    {
        boolean changed = false;
        int ox = mRnd.nextInt(mGridWidth);
        int oy = mRnd.nextInt(mGridHeight);
        for (int ix = 0; ix < mGridWidth && !changed; ix++)
        {
            int x = (ox + ix)%mGridWidth;
            for (int iy = 0; iy < mGridHeight && !changed; iy++)
            {
                int y = (oy + iy)%mGridHeight;
                if (get(x, y) != null)
                    continue;
                int[] dirs = getDirs(x, y);
                if (dirs != null)
                {
                    boolean anyConnected = contains(connected, x, y, dirs);
                    boolean anyUnconnected = contains(unconnected, x, y, dirs);
                    if (anyConnected && anyUnconnected)
                    {
                        if (isRoom(x, y))
                            set(x, y, makeJunction());
                        else if (isEWCorridor(x, y))
                            set(x, y, makeCorridor(CORRIDOR_EW_IDS));
                        else if (isNSCorridor(x, y))
                            set(x, y, makeCorridor(CORRIDOR_NS_IDS));
                        //System.out.println("Adding "+get(x, y).getID()+" to "+x+","+y);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }
    
    public void addFeatures()
    {
        // determine feature
        String feature = null;
        if (mRnd.nextInt(3) == 0)
            feature = "DUNGEON_CHEST";
        else if (mRnd.nextInt(4) == 0)
            feature = "DUNGEON_POOL";
        else if (mRnd.nextInt(6) == 0)
            feature = "DUNGEON_THRONE";
        else
            return;
        // place feature
        int ox = mRnd.nextInt(mRoomWidth);
        int oy = mRnd.nextInt(mRoomHeight);
        for (int ix = 0; ix < mRoomWidth; ix++)
        {
            int x = (ox + ix)%mRoomWidth;
            for (int iy = 0; iy < mRoomHeight; iy++)
            {
                int y = (oy + iy)%mRoomHeight;
                CompRoomBean room = get(x*2, y*2);
                if (room == null)
                    continue;
                int odir = mRnd.nextInt(4);
                for (int d = 0; d < 4; d++)
                {
                    int dir = (d + odir)%4;
                    if (StringUtils.isTrivial(room.getDirection(dir)))
                    {
                        CompRoomBean f = makeFeature(feature);
                        room.setDirection(dir, f.getID());
                        f.setDirection(CompRoomBean.opposite(dir), room.getID());
                        if ("DUNGEON_THRONE".equals(feature))
                            System.out.println("Throne at "+mFeature.getLocation()+"/"+room.getID());
                        return;
                    }
                }
            }
        }
    }

    private int[] getDirs(int x, int y)
    {
        int[] dirs = null;
        if (isRoom(x, y))
            dirs = new int[] { CompRoomBean.DIR_NORTH, CompRoomBean.DIR_SOUTH, CompRoomBean.DIR_EAST, CompRoomBean.DIR_WEST };
        else if (isEWCorridor(x, y))
            dirs = new int[] { CompRoomBean.DIR_EAST, CompRoomBean.DIR_WEST };
        else if (isNSCorridor(x, y))
            dirs = new int[] { CompRoomBean.DIR_NORTH, CompRoomBean.DIR_SOUTH };
        return dirs;
    }

    private CompRoomBean get(int x, int y)
    {
        if ((x < 0) || (y < 0))
            return null;
        if ((x >= mGridWidth) || (y >= mGridHeight))
            return null;
        return mLevel[x][y];
    }
    
    private CompRoomBean set(int x, int y, CompRoomBean r)
    {
        if ((x < 0) || (y < 0))
            return null;
        if ((x >= mGridWidth) || (y >= mGridHeight))
            return null;
        mLevel[x][y] = r;
        r.getParams().put(CompRoomBean.MD_X, x);
        r.getParams().put(CompRoomBean.MD_Y, y);
        r.getParams().put(CompRoomBean.MD_Z, mL);
        link(x, y);
        return mLevel[x][y];
    }

    private CompRoomBean makeJunction()
    {
        return makeCorridor(JUNCTION_IDS);
    }

    private CompRoomBean makeCorridor(String[] ids)
    {
        CompRoomBean to = FeatureLogic.getRoom(ids[mRnd.nextInt(ids.length)]);
        to.setID(to.getID()+mFeature.getRooms().size());
        JSONObject params = new JSONObject();
        to.setParams(params);
        params.put(CompRoomBean.MD_ENCOUNTER_CHALLENGE, mL);
        params.put(CompRoomBean.MD_WAIT_TIME, RuinLogic.MONSTER_TIMEOUT);
        int w = DiceRollBean.roll(mRnd, 2, 6)*10;
        int h = (DiceRollBean.roll(mRnd, 2, 2) - 1)*5;
        int a = w*h;
        params.put(CompRoomBean.MD_WIDTH, w);
        params.put(CompRoomBean.MD_HEIGHT, h);
        params.put(CompRoomBean.MD_AREA, a);
        params.put(CompRoomBean.MD_POPULATE, "skip");
        to.getName().setArgs(new Object[] { w, h });
        to.getDescription().setArgs(new Object[] { w, h });
        mFeature.getRooms().add(to);
        return to;
    }

    private CompRoomBean makeRoom()
    {
        CompRoomBean to = FeatureLogic.getRoom(ROOM_IDS[mRnd.nextInt(ROOM_IDS.length)]);
        to.setID(to.getID()+mFeature.getRooms().size());
        JSONObject params = new JSONObject();
        to.setParams(params);
        params.put(CompRoomBean.MD_ENCOUNTER_CHALLENGE, mL);
        params.put(CompRoomBean.MD_WAIT_TIME, RuinLogic.MONSTER_TIMEOUT);
        int w = DiceRollBean.roll(mRnd, 2, 3)*10;
        int h = DiceRollBean.roll(mRnd, 2, 4)*10;
        int a = w*h;
        params.put(CompRoomBean.MD_WIDTH, w);
        params.put(CompRoomBean.MD_HEIGHT, h);
        params.put(CompRoomBean.MD_AREA, a);
        to.getName().setArgs(new Object[] { w, h });
        to.getDescription().setArgs(new Object[] { w, h });
        mFeature.getRooms().add(to);
        return to;
    }

    private CompRoomBean makeFeature(String id)
    {
        CompRoomBean to = FeatureLogic.getRoom(id);
        to.setID(to.getID()+mFeature.getRooms().size());
        JSONObject params = to.getParams();
        params.put(CompRoomBean.MD_ENCOUNTER_CHALLENGE, mL);
        params.put(CompRoomBean.MD_WAIT_TIME, RuinLogic.MONSTER_TIMEOUT);
        params.put(CompRoomBean.MD_POPULATE, "skip");
        mFeature.getRooms().add(to);
        return to;
    }
    
    private void link(int x, int y)
    {
        CompRoomBean r1 = get(x, y);
        if (r1 == null)
            return;
        int[] dirs = getDirs(x, y);
        for (int dir : dirs)
        {
            int x2 = x + DX[dir];
            int y2 = y + DY[dir];
            CompRoomBean r2 = get(x2, y2);
            if (r2 == null)
                continue;
            r1.setDirection(dir, r2.getID());
            r2.setDirection(CompRoomBean.opposite(dir), r1.getID());
        }
    }

    /*
    private void debugLevel()
    {
        for (int y = 0; y < mGridHeight; y++)
        {
            for (int x = 0; x < mGridWidth; x++)
                if (get(x, y) != null)
                {
                    if (get(x, y) == mEntrance)
                        debugRoom(get(x, y), "e");
                    else if (get(x, y) == mExit)
                        debugRoom(get(x, y), "x");
                    else if (isRoom(x, y))
                        debugRoom(get(x, y), "#");
                    else if (isEWCorridor(x, y))
                        debugRoom(get(x, y), "-");
                    else if (isNSCorridor(x, y))
                        debugRoom(get(x, y), "|");
                    else
                        debugRoom(get(x, y), "?");
                }
                else
                    System.out.print("     ");
            System.out.println();
        }
    }
    
    private static void debugRoom(CompRoomBean r, String mark)
    {
        System.out.print(StringUtils.isTrivial(r.getWest()) ? " " : "<");
        System.out.print(StringUtils.isTrivial(r.getNorth()) ? " " : "^");
        System.out.print(mark);
        System.out.print(StringUtils.isTrivial(r.getSouth()) ? " " : "v");
        System.out.print(StringUtils.isTrivial(r.getEast()) ? " " : ">");
    }
    */
    
    private static boolean isRoom(int x, int y)
    {
        return (x%2 == 0) && (y%2 == 0);
    }
    
    private static boolean isEWCorridor(int x, int y)
    {
        return (x%2 == 1) && (y%2 == 0);
    }
    
    private static boolean isNSCorridor(int x, int y)
    {
        return (x%2 == 0) && (y%2 == 1);
    }
    
    private boolean contains(Set<CompRoomBean> set, int x, int y, int... dirs)
    {
        for (int dir : dirs)
        {
            CompRoomBean r = get(x+DX[dir], y+DY[dir]);
            if (r == null)
                continue;
            if (set.contains(r))
                return true;
        }
        return false;
    }
    
    private void connect(CompRoomBean toAdd, Map<String,CompRoomBean> index, Set<CompRoomBean> unconnected, Set<CompRoomBean> connected)
    {
        if (connected.contains(toAdd))
            return;
        unconnected.remove(toAdd);
        connected.add(toAdd);
        for (int dir = 0; dir < 4; dir++)
            if (index.containsKey(toAdd.getDirection(dir)))
                connect(index.get(toAdd.getDirection(dir)), index, unconnected, connected);
    }

    // getters and setters
    
    public FeatureBean getFeature()
    {
        return mFeature;
    }
    public void setFeature(FeatureBean feature)
    {
        mFeature = feature;
    }
    public Random getRnd()
    {
        return mRnd;
    }
    public void setRnd(Random rnd)
    {
        mRnd = rnd;
    }
    public int getL()
    {
        return mL;
    }
    public void setL(int l)
    {
        mL = l;
    }
    public CompRoomBean[][] getLevel()
    {
        return mLevel;
    }
    public void setLevel(CompRoomBean[][] level)
    {
        mLevel = level;
    }
    public CompRoomBean getEntrance()
    {
        return mEntrance;
    }
    public void setEntrance(CompRoomBean entrance)
    {
        mEntrance = entrance;
    }
    public CompRoomBean getExit()
    {
        return mExit;
    }
    public void setExit(CompRoomBean exit)
    {
        mExit = exit;
    }

    public int getRoomWidth()
    {
        return mRoomWidth;
    }

    public void setRoomWidth(int roomWidth)
    {
        mRoomWidth = roomWidth;
    }

    public int getRoomHeight()
    {
        return mRoomHeight;
    }

    public void setRoomHeight(int roomHeight)
    {
        mRoomHeight = roomHeight;
    }

    public int getGridWidth()
    {
        return mGridWidth;
    }

    public void setGridWidth(int gridWidth)
    {
        mGridWidth = gridWidth;
    }

    public int getGridHeight()
    {
        return mGridHeight;
    }

    public void setGridHeight(int gridHeight)
    {
        mGridHeight = gridHeight;
    }
}

class Adjacent
{
    CompRoomBean u;
    CompRoomBean c;
    int          d;
}