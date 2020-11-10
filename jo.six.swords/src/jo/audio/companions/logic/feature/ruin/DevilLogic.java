package jo.audio.companions.logic.feature.ruin;

import java.util.Random;

import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.feature.RuinLogic;

public class DevilLogic
{
    public static void generateDevil(FeatureBean feature, SquareBean sq, Random rnd)
    {
        feature.setSubType(CompConstLogic.FEATURE_SUB_DEVIL);
        feature.getName().setIdent("{{DEVIL_NAME#"+rnd.nextInt(9937)+"}}");
        feature.setMonsterTreasure(true);
        int numRooms = DiceRollBean.roll(rnd, 2, 4, 1);
        DenLogic.determineDenRooms(feature, rnd, numRooms, DEVIL_ROOMS);
        RuinLogic.sortRoomsByDepth(feature);
        RuinLogic.populateRoomsByDepth(feature, 2, null);
        feature.setMonsterType("Devil");
    }
    
    private static String[] DEVIL_ROOMS = {
            "devil1", "devil2", "devil3", "devil4", "devil5", "devil6", "devil7", "devil8",
    };
}