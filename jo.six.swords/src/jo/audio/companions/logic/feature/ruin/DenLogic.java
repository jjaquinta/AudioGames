package jo.audio.companions.logic.feature.ruin;

import java.util.Random;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.feature.RuinLogic;

public class DenLogic
{
    public static void generateDen(FeatureBean feature, SquareBean sq, Random rnd)
    {
        feature.setSubType(CompConstLogic.FEATURE_SUB_DEN);
        feature.getName().setIdent("{{DEN_NAME#"+rnd.nextInt(9937)+"}}");
        feature.setMonsterTreasure(false);
        int numRooms = DiceRollBean.roll(rnd, 1, 4, 2);
        determineDenRooms(feature, rnd, numRooms, DEN_ROOMS);
        RuinLogic.sortRoomsByDepth(feature);
        RuinLogic.populateRoomsByDepth(feature, 2, null);
    }

    private static String[] DEN_ROOMS = {
            "den1", "den2", "den3", "den4",
    };

    public static void determineDenRooms(FeatureBean feature, Random rnd, int numRooms, String[] roomIDs)
    {
        while (feature.getRooms().size() < numRooms)
        {
            String roomID = roomIDs[rnd.nextInt(roomIDs.length)];
            addRoom(feature, rnd, roomID);
        }
    }

    public static CompRoomBean addRoom(FeatureBean feature, Random rnd, String roomID)
    {
        CompRoomBean room = FeatureLogic.getRoom(roomID);
        room.setID(room.getID()+feature.getRooms().size());
        JSONObject params = room.getParams();
        if (params == null)
        {
            params = new JSONObject();
            room.setParams(params);
        }
        params.put(CompRoomBean.MD_ENCOUNTER_CHALLENGE, 0);
        params.put(CompRoomBean.MD_WAIT_TIME, RuinLogic.MONSTER_TIMEOUT);
        if (feature.getRooms().size() == 0)
            room.setSouth("$exit");
        else
        {
            CompRoomBean other;
            for (;;)
            {
                other = feature.getRooms().get(rnd.nextInt(feature.getRooms().size()));
                int exit = RuinLogic.findFreeExit(other, rnd);
                if (exit >= 0)
                {
                    other.setDirection(exit, room.getID());
                    break;
                }
            }
            room.setDirection(rnd.nextInt(4), other.getID());
        }
        feature.getRooms().add(room);
        return room;
    }
}