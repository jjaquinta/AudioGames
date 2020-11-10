package jo.audio.companions.logic.feature.ruin;

import java.util.Random;

import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.feature.RuinLogic;

public class GroveLogic
{
    public static void generateGrove(FeatureBean feature, SquareBean sq, Random rnd)
    {
        feature.setSubType(CompConstLogic.FEATURE_SUB_DINO);
        feature.getName().setIdent("{{GROVE_NAME#"+rnd.nextInt(9937)+"}}");
        feature.setMonsterTreasure(true);
        int numRooms = DiceRollBean.roll(rnd, 2, 4, 1);
        DenLogic.determineDenRooms(feature, rnd, numRooms, GROVE_ROOMS);
        if (sq.getOrds().getZ() == CompConstLogic.DIM_ENUMA)
            DenLogic.addRoom(feature, rnd, "grove_to_ireland");
        else if (sq.getOrds().getZ() == CompConstLogic.DIM_IRELAND)
            DenLogic.addRoom(feature, rnd, "grove_from_ireland");
        RuinLogic.sortRoomsByDepth(feature);
        RuinLogic.populateRoomsByDepth(feature, 2, null);
        feature.setMonsterType("Sylvan-or-Faerie");
    }

    private static String[] GROVE_ROOMS = {
            "grove1", "grove2", "grove3", "grove4", "grove5", "grove6", "grove7", "grove8",
    };
    
}