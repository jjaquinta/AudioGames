package jo.audio.companions.logic.feature.ruin;

import java.util.Random;

import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.feature.RuinLogic;

public class DragonLogic
{
    public static void generateDragonLair(FeatureBean feature, SquareBean sq, Random rnd)
    {
        feature.setSubType(CompConstLogic.FEATURE_SUB_RUIN);
        feature.getName().setIdent("{{DRAGON_LAIR_NAME#"+rnd.nextInt(9937)+"}}");
        feature.setMonsterType("Dragon");
        int numRooms = DiceRollBean.roll(rnd, 3, 6);
        RuinLogic.determineCastleRooms(feature, rnd, numRooms, "dragonCaveEntrance", "dragonTunnel1", DRAGON_ROOMS);
        RuinLogic.sortRoomsByDepth(feature);
        RuinLogic.populateRoomsByDepth(feature, 3, null);
    }

    private static final String[] DRAGON_ROOMS = {
          "dragonBoneYard",
          "dragonHatchery",
          "dragonTunnel1",
          "dragonTunnel2",
          "dragonTunnel3",
          "dragonTunnel4",
          "dragonSheddingRoom",
          "dragonHallOfTheAncestors",
          "dragonTreasureRoom",
          "dragonSleepingRoom",
          "dragonAltar",
          "dragonEyrie",
          "dragonSupplicant'sRoom",
          "dragonCoalShed",
          "dragonWell",
          "dragonKennel",
          "dragonEmptyRoom",
        };
}