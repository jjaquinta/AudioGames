package jo.audio.companions.logic.feature.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.feature.RuinLogic;

public class DungeonLogic
{
    private static final String[] STAIR_IDS = new String[] {
            "DUNGEON_STAIRS_1"
    };
    
    public static void generateDungeon(FeatureBean feature, SquareBean sq, Random rnd)
    {
        feature.setSubType(CompConstLogic.FEATURE_SUB_DUNGEON);
        feature.getName().setIdent("{{DUNGEON_NAME#"+rnd.nextInt(9937)+"}}");
        feature.setMonsterTreasure(true);
        feature.setMonsterPopulous(true);
        int numLevels = DiceRollBean.roll(rnd, 1, 8);
        //System.out.println("Dungeon levels: "+numLevels);
        CompRoomBean lastExit = null;
        for (int l = 0; l < numLevels; l++)
        {
            //System.out.println("Level: "+(l+1));
            DungeonLevel level = determineDungeonLevel(feature, rnd, l);
            if (lastExit != null)
            {
                CompRoomBean entrance = level.getEntrance();
                CompRoomBean stairs = makeStairs(feature, rnd, l);
                lastExit.setEast(stairs.getID());
                stairs.setWest(lastExit.getID());
                stairs.setEast(entrance.getID());
                entrance.setWest(stairs.getID());
            }
            else
            {
                level.getEntrance().setWest("$exit");
                feature.setEntranceID(level.getEntrance().getID());
            }
            lastExit = level.getExit();
        }
        lastExit.setEast("$exit");
        RuinLogic.sortRoomsByDepth(feature);
        RuinLogic.populateRoomsByDepth(feature, 3, null);
        System.out.println("DUNGEON: "+sq.getOrds());
    }

    private static DungeonLevel determineDungeonLevel(FeatureBean feature, Random rnd, int l)
    {
        DungeonLevel level = new DungeonLevel(feature, rnd, l);
        //System.out.println("Size: "+level.getGridWidth()+"x"+level.getGridHeight());
        List<CompRoomBean> entrances = new ArrayList<>();
        List<CompRoomBean> exits = new ArrayList<>();
        level.populateRooms(entrances, exits);
        level.populateCorridors();
        level.setEntrance(entrances.get(rnd.nextInt(entrances.size())));
        level.setExit(exits.get(rnd.nextInt(exits.size())));
        //System.out.println("Entrance: "+level.getEntrance().getParams().getString(CompRoomBean.MD_X)+","+level.getEntrance().getParams().getString(CompRoomBean.MD_Y));
        //System.out.println("Exit: "+level.getExit().getParams().getString(CompRoomBean.MD_X)+","+level.getExit().getParams().getString(CompRoomBean.MD_Y));
        level.connectLevel();
        level.addFeatures();
        return level;
    }

    private static CompRoomBean makeStairs(FeatureBean feature, Random rnd, int l)
    {
        CompRoomBean to = FeatureLogic.getRoom(STAIR_IDS[rnd.nextInt(STAIR_IDS.length)]);
        to.setID(to.getID()+feature.getRooms().size());
        JSONObject params = new JSONObject();
        to.setParams(params);
        params.put(CompRoomBean.MD_ENCOUNTER_CHALLENGE, l);
        params.put(CompRoomBean.MD_WAIT_TIME, RuinLogic.MONSTER_TIMEOUT);
        params.put(CompRoomBean.MD_POPULATE, "skip");
        to.getName().setArgs(new Object[] { l, l + 1 });
        to.getDescription().setArgs(new Object[] { l, l + 1 });
        feature.getRooms().add(to);
        return to;
    }
}