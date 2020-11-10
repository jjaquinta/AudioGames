package jo.audio.companions.logic.feature.ruin;

import java.util.List;
import java.util.Random;

import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.feature.RuinLogic;

public class GiantLogic
{

    public static void generateGiantHall(FeatureBean feature, SquareBean sq, Random rnd, List<String> expansions)
    {
        feature.setSubType(CompConstLogic.FEATURE_SUB_GIANT);
        feature.getName().setIdent("{{GIANT_HALL_NAME#"+rnd.nextInt(9937)+"}}");
        feature.setMonsterType("Giant");
        int numRooms = DiceRollBean.roll(rnd, 3, 6);
        RuinLogic.determineTempleRooms(feature, rnd, sq.getDemense(), expansions, numRooms,
                GIANT_TEMPLATES, GIANT_ROOMS);
        if (sq.getOrds().getZ() == CompConstLogic.DIM_ENUMA)
            DenLogic.addRoom(feature, rnd, "giant_to_iceland");
        else if (sq.getOrds().getZ() == CompConstLogic.DIM_ICELAND)
            DenLogic.addRoom(feature, rnd, "giant_from_iceland");
        RuinLogic.sortRoomsByDepth(feature);
        //JSONArray postCombat = new JSONArray();
        //postCombat.add("divineBlessing("+expansions.get(0)+")");
        RuinLogic.populateRoomsByDepth(feature, 3, null);
        feature.rotate(90*rnd.nextInt(4));
    }
    
    private static final String[][] GIANT1_TEMPLATE = {    // 3 - 10 rooms
            { "giantEntryHall>^.", "<giantFeastHall^.>", "<giantChiefRoom^.>", },
    };
    
    private static final String[][] GIANT2_TEMPLATE = {    // 9 - 12 rooms
            { "giantBattlements.>", "<giantTower^.>", "<giantBattlements." },
            { "giantEntryHall^.>", null, "<giantFeastHall^.>", "<giantChiefRoom" },
            { "giantBattlements^>", "<giantTower^.>", "<giantBattlements^" },
    };
    
    private static final String[][] GIANT3_TEMPLATE = {    // 10 - 18 rooms
            { "giantEntryHall^.>", "<giantFeastHall^.>", "<giantThroneRoom^.>", "<giantChiefRoom>", "<giantTreasureRoom" },
            { "giantBattlements^.", null, "giantBattlements^.", },
            { "<giantTower^.>", "<giantBattlements>", "<giantTower^.>", },
    };
    
    private static final String[][] GIANT4_TEMPLATE = {    // 8 - 12 rooms
            { null, "giantLarder.", null},
            { "giantEntryHall^.>", "<giantFeastHall^.>", "<giantChiefRoom>", "<giantTreasureRoom" },
            { null, "<giantTemple^.>", null},
            { null, "giantPriestRoom^", null},
    };
    
    private static final TempleTemplate[] GIANT_TEMPLATES = {
            new TempleTemplate(GIANT4_TEMPLATE, "giantEntryHall"),
            new TempleTemplate(GIANT3_TEMPLATE, "giantEntryHall"),
            new TempleTemplate(GIANT2_TEMPLATE, "giantEntryHall"),
            new TempleTemplate(GIANT1_TEMPLATE, "giantEntryHall"),
    };

    private static final String[] GIANT_ROOMS = {
            "giantSmithy",
            "giantArmory",
            "giantNursery",
            "giantBrewery",
            "giantSlaveQuarters",
            "giantButchery",
            "giantStable",
            "giantForge",
    };
}
