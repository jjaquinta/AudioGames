package jo.audio.companions.logic.feature.ruin;

import java.util.Random;

import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.feature.RuinLogic;

public class DemonLogic
{
    public static void generateDemon(FeatureBean feature, SquareBean sq, Random rnd)
    {
        feature.setSubType(CompConstLogic.FEATURE_SUB_DEMON);
        feature.getName().setIdent("{{DEMON_NAME#"+rnd.nextInt(9937)+"}}");
        feature.setMonsterTreasure(true);
        int numRooms = DiceRollBean.roll(rnd, 2, 4, 1);
        DenLogic.determineDenRooms(feature, rnd, numRooms, DEMON_ROOMS);
        RuinLogic.sortRoomsByDepth(feature);
        RuinLogic.populateRoomsByDepth(feature, 2, null);
        feature.setMonsterType("Demon");
   }

    private static String[] DEMON_ROOMS = {
            "demon1", "demon2", "demon3", "demon4", "demon5", "demon6", "demon7", "demon8",
    };
}