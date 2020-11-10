package jo.audio.companions.logic.feature.ruin;

import java.util.Random;

import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.feature.RuinLogic;

public class CastleLogic
{
    public static void generateCastle(FeatureBean feature, SquareBean sq, Random rnd)
    {
        feature.setSubType(CompConstLogic.FEATURE_SUB_RUIN);
        feature.getName().setIdent("{{RUIN_NAME#"+rnd.nextInt(9937)+"}}");
        feature.setMonsterTreasure(true);
        feature.setMonsterPopulous(true);
        int numRooms = DiceRollBean.roll(rnd, 3, 6);
        RuinLogic.determineCastleRooms(feature, rnd, numRooms, "ruinEntry", "ruinCourtyard", RUIN_ROOMS);
        RuinLogic.sortRoomsByDepth(feature);
        RuinLogic.populateRoomsByDepth(feature, 3, null);
    }

    private static final String[] RUIN_ROOMS = {
    "ruinAntechamber",
    "ruinArmory",
    "ruinAudience",
    "ruinAviary",
    "ruinBanquet",
    "ruinBarracks",
    "ruinBath",
    "ruinBedroom",
    "ruinBeastiary",
    "ruinCell",
    "ruinChantry",
    "ruinChapel",
    "ruinCistern",
    "ruinClassroom",
    "ruinCloset",
    "ruinConjuringroom",
    "ruinCorridor",
    "ruinCrypt",
    "ruinDiningroom",
    "ruinDivinationroom",
    "ruinDormitory",
    "ruinDressingroom",
    "ruinGallery",
    "ruinGameroom",
    "ruinGuardroom",
    "ruinHall",
    "ruinGreathall",
    "ruinHallway",
    "ruinSeraglio",
    "ruinKennel",
    "ruinKitchen",
    "ruinLabratory",
    "ruinLibrary",
    "ruinLounge",
    "ruinMeditation",
    "ruinObservatory",
    "ruinOffice",
    "ruinPantry",
    "ruinPrison",
    "ruinRefectory",
    "ruinRobingroom",
    "ruinShrine",
    "ruinSittingroom",
    "ruinSmithy",
    "ruinStable",
    "ruinStorage",
    "ruinVault",
    "ruinStudy",
    "ruinTemple",
    "ruinThroneroom",
    "ruinTorturechamber",
    "ruinTrophyroom",
    "ruinToilet",
    "ruinWell",
    };
}