package jo.audio.companions.logic.gen;

import java.util.Random;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.FeatureLogic;

public class DimArchLogic
{

    public static void generateArch(FeatureBean feature, SquareBean sq,
            Random rnd)
    {
        CompRoomBean entry = FeatureLogic.getRoom("dimArchEntry");
        CompRoomBean arch = FeatureLogic.getRoom("dimArchPortal");
        feature.setName(entry.getName());
        feature.setMonsterTreasure(false);
        feature.setMonsterPopulous(false);
        feature.getRooms().add(entry);
        feature.getRooms().add(arch);
    }

}
