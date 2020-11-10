package jo.audio.companions.logic.gen;

import java.util.Random;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.FeatureLogic;

public class VortexLogic
{

    public static void generateVortex(FeatureBean feature, SquareBean sq,
            Random rnd)
    {
        CompRoomBean vortex = FeatureLogic.getRoom("vortex");
        feature.setName(vortex.getName());
        feature.setParam("autoEnter", "true");
        feature.setMonsterTreasure(false);
        feature.setMonsterPopulous(false);
        feature.getRooms().add(vortex);
    }

}
