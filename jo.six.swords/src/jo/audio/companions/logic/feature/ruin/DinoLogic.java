package jo.audio.companions.logic.feature.ruin;

import java.util.Random;

import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.feature.RuinLogic;

public class DinoLogic
{
    public static void generateDino(FeatureBean feature, SquareBean sq, Random rnd)
    {
        feature.setSubType(CompConstLogic.FEATURE_SUB_DINO);
        feature.getName().setIdent("{{DINO_NAME#"+rnd.nextInt(9937)+"}}");
        feature.setMonsterTreasure(true);
        int numRooms = DiceRollBean.roll(rnd, 2, 4, 1);
        DenLogic.determineDenRooms(feature, rnd, numRooms, DINO_ROOMS);
        RuinLogic.sortRoomsByDepth(feature);
        RuinLogic.populateRoomsByDepth(feature, 2, null);
        feature.setMonsterType("Dinosaur");
    }

    
    private static String[] DINO_ROOMS = {
            "dino1", "dino2", "dino3", "dino4", "dino5", "dino6", "dino7", "dino8",
    };
}