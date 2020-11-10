package jo.audio.companions.logic.feature.ruin;

import java.util.List;
import java.util.Random;

import org.json.simple.JSONArray;

import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.feature.RuinLogic;

public class TempleLogic
{

    @SuppressWarnings("unchecked")
    public static void generateTemple(FeatureBean feature, SquareBean sq, Random rnd, List<String> expansions)
    {
        feature.setSubType(CompConstLogic.FEATURE_SUB_TEMPLE);
        feature.getName().setIdent("{{TEMPLE_NAME#"+rnd.nextInt(9937)+"}}");
        feature.setMonsterType("Undead");
        int numRooms = DiceRollBean.roll(rnd, 3, 6);
        RuinLogic.determineTempleRooms(feature, rnd, sq.getDemense(), expansions, numRooms,
                TEMPLE_TEMPLATES, TEMPLE_ROOMS);
        RuinLogic.sortRoomsByDepth(feature);
        JSONArray postCombat = new JSONArray();
        postCombat.add("divineBlessing("+expansions.get(0)+")");
//        postCombat.add("respond(GOD_SMILES)");
//        postCombat.add("increment(GOD_"+expansions.get(0)+")");
//        postCombat.add("increment(BLESSINGS_NUM)");
//        postCombat.add("tag(BLESSINGS_WHO, GOD_"+expansions.get(0)+")");
//        JSONArray doGive = new JSONArray();
//        doGive.add("give("+PantheonLogic.GOD_WEAPONS[IntegerUtils.parseInt(expansions.get(0))]+", 1, {{A_GOD_WEAPON#"+expansions.get(0)+"}})");
//        doGive.add("respond(GOD_GIVES, {{A_GOD_WEAPON#"+expansions.get(0)+"}})");
//        JSONObject giveWeapon = new JSONObject();
//        JSONArray ifExpr = new JSONArray();
//        ifExpr.add("getvalue(GOD_"+expansions.get(0)+")");
//        ifExpr.add("==");
//        ifExpr.add(1);
//        giveWeapon.put("if", ifExpr);
//        giveWeapon.put("then", doGive);
//        postCombat.add(giveWeapon);        
        RuinLogic.populateRoomsByDepth(feature, 3, postCombat);
        feature.rotate(90*rnd.nextInt(4));
    }
    
    private static final String[][] TEMPLE1_TEMPLATE = {    // 2 - 7 rooms
            { "templeAnteChamber>^.", "<templeShrine^.>", },
    };
    
    private static final String[][] TEMPLE2_TEMPLATE = {    // 4 - 9 rooms
            { "templeAnteChamber^.>", "<templeAlterRoom^.>", "<templeSacristy^.>" },
            { null, null, "templeHighPriestChambers^" },
    };
    
    private static final String[][] TEMPLE3_TEMPLATE = {    // 8 - 19 rooms
            { "templeAnteChamber^.>", "<templeHallway^.>", "<templeShrine^.>" },
            { "<templeHallway^.>", null, "<templeHallway^.>", },
            { "<templePriestChambers^.>", "<templeHallway^.>", "<templeAlterRoom^.>", },
    };
    
    private static final String[][] TEMPLE4_TEMPLATE = {    // 14 - 21 rooms
            { null, "<templeHallway^.>", "<templeHallway^>", "<templeHallway^.>", null},
            { "templeAnteChamber>", "<templeShrine>^.", "<templeProcession>", "<templeAlterRoom^.>", "<templeHolyOfHolies" },
            { null, "templeHallway^.>", "<templeHallway.>", "<templeHallway^.>", "<templeHighPriestChambers."},
            { null, null, "templePriestChambers^", "templeRefectory^", null},
    };
    
    private static final TempleTemplate[] TEMPLE_TEMPLATES = {
            new TempleTemplate(TEMPLE4_TEMPLATE, "templeAnteChamber"),
            new TempleTemplate(TEMPLE3_TEMPLATE, "templeAnteChamber"),
            new TempleTemplate(TEMPLE2_TEMPLATE, "templeAnteChamber"),
            new TempleTemplate(TEMPLE1_TEMPLATE, "templeAnteChamber"),
    };

    private static final String[] TEMPLE_ROOMS = {
            "templeBaptistry",
            "templeNave",
            "templeQuire",
            "templePresbytery",
            "templeCloister",
            "templeChapterHall",
            "templeDormitory",
            "templePriory",
            "templeCrypt",
    };
}
