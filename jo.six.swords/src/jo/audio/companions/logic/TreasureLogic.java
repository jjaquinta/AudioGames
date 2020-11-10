package jo.audio.companions.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompTreasuresBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.StringUtils;

public class TreasureLogic
{
    private static final String MAGIC_ITEM = "magic item";
    private static final String COPPER = "cp";
    private static final String ELECTRUM = "ep";
    private static final String GOLD = "gp";
    private static final String SCROLL = "scroll";
    private static final String POTION = "potion";
    private static final String JEWELRY = "jewelry";
    private static final String SILVER = "sp";
    private static final String WEAPON = "weapon";
    private static final String GEM = "gem";
    private static final String PLATINUM = "pp";

    public static void rollIndividualTreasures(CompMonsterTypeBean monst, int num, Random rnd, 
            CompTreasuresBean loot)
    {
        //log("Rolling individual treasures for "+num+" x "+monst.getName());
        JSONArray treasures = (JSONArray)monst.getDetails().get("treasures");
        rollTreasures(num, rnd, loot, treasures);
    }

    public static void rollTreasures(int num, Random rnd,
            CompTreasuresBean loot, JSONArray treasures)
    {
        for (int i = 0; i < treasures.size(); i++)
        {
            JSONObject treasure = (JSONObject)treasures.get(i);
            boolean inLair = BooleanUtils.parseBoolean(treasure.get("inLair"));
            if (inLair)
                continue;
            for (int j = 0; j < num; j++)
                rollTreasure(treasure, rnd, loot);
        }
    }

    public static void rollLairTreasure(CompMonsterTypeBean monst, Random rnd, 
            CompTreasuresBean loot)
    {
        JSONArray treasures = (JSONArray)monst.getDetails().get("treasures");
        for (int i = 0; i < treasures.size(); i++)
        {
            JSONObject treasure = (JSONObject)treasures.get(i);
            boolean inLair = BooleanUtils.parseBoolean(treasure.get("inLair"));
            if (!inLair)
                continue;
            rollTreasure(treasure, rnd, loot);
        }
    }

    public static void addMessages(CompTreasuresBean loot)
    {
        List<AudioMessageBean> msgs = new ArrayList<AudioMessageBean>();
        addPieces(msgs, loot.getCopperPieces(), CompanionsModelConst.TEXT_XXX_COPPER_PIECE, CompanionsModelConst.TEXT_XXX_COPPER_PIECES);
        addPieces(msgs, loot.getSilverPieces(), CompanionsModelConst.TEXT_XXX_SILVER_PIECE, CompanionsModelConst.TEXT_XXX_SILVER_PIECES);
        addPieces(msgs, loot.getElectrumPieces(), CompanionsModelConst.TEXT_XXX_ELECTRUM_PIECE, CompanionsModelConst.TEXT_XXX_ELECTRUM_PIECES);
        addPieces(msgs, loot.getGoldPieces(), CompanionsModelConst.TEXT_XXX_GOLD_PIECE, CompanionsModelConst.TEXT_XXX_GOLD_PIECES);
        addPieces(msgs, loot.getPlatinumPieces(), CompanionsModelConst.TEXT_XXX_PLATINUM_PIECE, CompanionsModelConst.TEXT_XXX_PLATINUM_PIECES);
        addPieces(msgs, loot.getGemPieces(), CompanionsModelConst.TEXT_XXX_GEM, CompanionsModelConst.TEXT_XXX_GEMS);
        addPieces(msgs, loot.getJewelryPieces(), CompanionsModelConst.TEXT_XXX_JEWELRY, CompanionsModelConst.TEXT_XXX_JEWELRYS);
        for (CompItemTypeBean item : loot.getItems().keySet())
        {
            int q = loot.getItems().get(item);
            if (q == 1)
                msgs.add(new AudioMessageBean(AudioMessageBean.RAW, item.getName()));
            else
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_YYY, q, item.getName()));
        }
        if (msgs.size() == 0)
            return;
        if ((int)loot.getTotalValue() == loot.getGoldPieces())
            loot.setMessage(AudioMessageBean.and(msgs));
        else
            loot.setMessage(new AudioMessageBean(
                    CompanionsModelConst.TEXT_XXX_COMMA_YYY,
                    AudioMessageBean.and(msgs),
                    new AudioMessageBean(CompanionsModelConst.TEXT_WORTH_A_TOTAL_OF_XXX_GOLD, (int)loot.getTotalValue())
                    ));
    }
    
    private static void addPieces(List<AudioMessageBean> msgs, int q, String singular, String plural)
    {
        if (q == 1)
            msgs.add(new AudioMessageBean(singular, q));
        else if (q > 1)
            msgs.add(new AudioMessageBean(plural, q));
    }
    
//    private static void addValuables(List<AudioMessageBean> msgs, int q, String singular, String plural, double value)
//    {
//        if (q == 1)
//            msgs.add(new AudioMessageBean(singular, q, (int)value));
//        else if (q > 1)
//            msgs.add(new AudioMessageBean(plural, q, (int)value));
//    }

    private static void rollTreasure(JSONObject treasure, 
            Random rnd, CompTreasuresBean loot)
    {
        int chance = TreasureLogic.getChance(treasure);
        int roll = rnd.nextInt(100);
        //log("Rolled "+roll+" against chance of "+chance+" for "+treasure.get("type"));
        if (chance <= roll)
            return;
        String number = treasure.get("number").toString();
        DiceRollBean r = new DiceRollBean(number);
        int q = r.roll(rnd);
        //log("Quantity found ="+q);
        String type = (String)treasure.get("type");
        switch (type)
        {
            case COPPER:
                loot.setCopperPieces(loot.getCopperPieces() + q);
                loot.setTotalValue(loot.getTotalValue() + q/100.0);
                break;
            case SILVER:
                loot.setSilverPieces(loot.getSilverPieces() + q);
                loot.setTotalValue(loot.getTotalValue() + q/10.0);
                break;
            case ELECTRUM:
                loot.setElectrumPieces(loot.getElectrumPieces() + q);
                loot.setTotalValue(loot.getTotalValue() + q/2.0);
                break;
            case GOLD:
                loot.setGoldPieces(loot.getGoldPieces() + q);
                loot.setTotalValue(loot.getTotalValue() + q);
                break;
            case PLATINUM:
                loot.setPlatinumPieces(loot.getPlatinumPieces() + q);
                loot.setTotalValue(loot.getTotalValue() + q*5);
                break;
            case GEM:
                loot.setGemPieces(loot.getGemPieces() + q);
                for (int i = 0; i < q; i++)
                {
                    double gv = gemValue(rnd);
                    loot.setGemValue(loot.getGemValue() + gv);
                    loot.setTotalValue(loot.getTotalValue() + gv);
                }
                break;
            case JEWELRY:
                loot.setJewelryPieces(loot.getJewelryPieces() + q);
                for (int i = 0; i < q; i++)
                {
                    double jv = jewelryValue(rnd);
                    loot.setTotalValue(loot.getTotalValue() + jv);
                    loot.setJewelryValue(loot.getJewelryValue() + jv);
                }
                break;
            case MAGIC_ITEM:
                addItem(loot, ItemLogic.getRandomItemByTypes(rnd, true));
                break;
            case SCROLL:
                addItem(loot, ItemLogic.getRandomItemByTypes(rnd, true, CompItemTypeBean.TYPE_CASTLE, CompItemTypeBean.TYPE_PERMIT));
                break;
            case WEAPON:
                addItem(loot, ItemLogic.getRandomItemByTypes(rnd, true, CompItemTypeBean.TYPE_AMMO, CompItemTypeBean.TYPE_ARMOR, 
                        CompItemTypeBean.TYPE_HAND, CompItemTypeBean.TYPE_HURLED, CompItemTypeBean.TYPE_LAUNCHER));
                break;
            case POTION:
                addItem(loot, ItemLogic.getRandomItemByTypes(rnd, true, CompItemTypeBean.TYPE_POTION));
                break;
        }
    }

    private static void addItem(CompTreasuresBean loot, CompItemTypeBean item)
    {
        if (loot.getItems().containsKey(item))
            loot.getItems().put(item, loot.getItems().get(item) + 1);
        else
            loot.getItems().put(item, 1);
    }
    
    private static int[][] JEWELRY_VALUE = {
        { 03, /* Amulet */ 4, 7, 9, 10 },
        { 04, /* Anklet */ 3, 6, 8, 9, 10 },
        { 07, /* Armring */ 4, 6, 8, 9, 10 },
        { 10, /* Belt */ 4, 6, 8, 10 },
        { 12, /* Box */ 4, 7, 9, 10 },
        { 17, /* Bracelet */ 3, 6, 8, 9, 10 },
        { 20, /* Brooch */ 3, 6, 8, 9, 10 },
        { 23, /* Buckle */ 4, 7, 9, 10 },
        { 25, /* Chain */ 4, 7, 9, 10 },
        { 27, /* Chalice */ 3, 6, 8, 9, 10 },
        { 30, /* Choker */ 4, 7, 9, 10 },
        { 32, /* Clasp */ 4, 6, 8, 10 },
        { 35, /* Comb */ 3, 6, 8, 9, 10 },
        { 37, /* Coronet */ 1, 2, 4, 8, 9, 10 },
        { 38, /* Crown */ 1, 2, 3, 4, 7, 10 },
        { 40, /* Diadem */ 3, 6, 8, 9, 10 },
        { 46, /* Earring */ 3, 6, 8, 9, 10 },
        { 49, /* Goblet */ 3, 6, 8, 9, 10 },
        { 51, /* Idol */ 2, 4, 7, 9, 10 },
        { 54, /* Knife */ 4, 7, 9, 10 },
        { 58, /* Locket */ 3, 5, 7, 9, 10 },
        { 60, /* Medal */ 4, 7, 10 },
        { 64, /* Medallion */ 3, 6, 9, 10 },
        { 69, /* Necklace */ 3, 6, 8, 9, 10 },
        { 73, /* Pendant */ 3, 6, 8, 9, 10 },
        { 77, /* Pin */ 3, 6, 8, 9, 10 },
        { 78, /* Orb */ 1, 2, 3, 5, 8, 10 },
        { 87, /* Ring */ 3, 6, 8, 9, 10 },
        { 88, /* Sceptre */ 1, 2, 3, 5, 8, 10 },
        { 92, /* Seal */ 4, 7, 9, 10 },
        { 94, /* Statuette */ 3, 6, 8, 9, 10 },
        { 95, /* Tiara */ 3, 6, 8, 9, 10 },
        { 97, /* Toering */ 3, 6, 8, 9, 10 },
        { 100, /* Weapon-hilt */ 3, 6, 8, 9, 10 },
    };

    private static double jewelryValue(Random rnd)
    {
        int roll1 = rnd.nextInt(100) + 1;
        int roll2 = rnd.nextInt(10) + 1;
        int idx = 1;
        for (int row = 0; row < JEWELRY_VALUE.length; row++)
            if (roll1 <= JEWELRY_VALUE[row][0])
            {
                while (roll2 > JEWELRY_VALUE[row][idx])
                    idx++;
                break;
            }
        double gold = 0;
        switch (idx)
        {
            case 1:
                gold = DiceRollBean.roll(rnd, 1, 10, 0, 100);
                break;
            case 2:
                gold = DiceRollBean.roll(rnd, 2, 6, 0, 100);
                break;
            case 3:
                gold = DiceRollBean.roll(rnd, 3, 6, 0, 100);
                break;
            case 4:
                gold = DiceRollBean.roll(rnd, 5, 6, 0, 100);
                break;
            case 5:
                gold = DiceRollBean.roll(rnd, 2, 4, 0, 1000);
                break;
            case 6:
                gold = DiceRollBean.roll(rnd, 2, 6, 0, 1000);
                break;
        }
        return gold;
    }

    private static double gemValue(Random rnd)
    {
        int roll = rnd.nextInt(100) + 1;
        if (roll <= 30)
            return DiceRollBean.roll(rnd, 4, 4);
        if (roll <= 55)
            return DiceRollBean.roll(rnd, 2, 4, 0, 10);
        if (roll <= 75)
            return DiceRollBean.roll(rnd, 4, 4, 0, 10);
        if (roll <= 90)
            return DiceRollBean.roll(rnd, 2, 4, 0, 100);
        if (roll <= 99)
            return DiceRollBean.roll(rnd, 4, 4, 0, 100);
        return DiceRollBean.roll(rnd, 2, 4, 0, 1000);
    }

    public static void main(String[] argv)
    {
        List<CompMonsterTypeBean> monsterTypes = MonsterLogic.getAllTypes();
        System.out.println(monsterTypes.size()+" items read in.");
        Set<String> types = new HashSet<>();
        types.add(PLATINUM);
        types.add(GEM);
        types.add(WEAPON);
        types.add(SILVER);
        types.add(JEWELRY);
        types.add(POTION);
        types.add(SCROLL);
        types.add(GOLD);
        types.add(ELECTRUM);
        types.add(COPPER);
        types.add(MAGIC_ITEM);

        for (CompMonsterTypeBean m : monsterTypes)
        {
            JSONObject details = m.getDetails();
            if ((details == null) || !details.containsKey("treasures"))
            {
                System.out.println(m.getName()+" - no treasures");
                continue;
            }
            if (!(details.get("treasures") instanceof JSONArray))
            {
                System.out.println(m.getName()+" - treasures not an array");
                continue;
            }
            JSONArray treasures = (JSONArray)details.get("treasures");
            for (int i = 0; i < treasures.size(); i++)
            {
                if (!(treasures.get(i) instanceof JSONObject))
                {
                    System.out.println(m.getName()+" treasure #"+i+", not an object");
                    continue;
                }
                JSONObject treasure = (JSONObject)treasures.get(i);
                if (!treasure.containsKey("chance"))
                {
                    System.out.println(m.getName()+" treasure #"+i+", does not contain chance");
                    continue;
                }
                int chance = TreasureLogic.getChance(treasure);
                if ((chance <= 0) || (chance > 100))
                {
                    System.out.println(m.getName()+" treasure #"+i+", has a bad chance="+treasure.get("chance"));
                    continue;
                }
                boolean inLair = BooleanUtils.parseBoolean(treasure.get("inLair"));
                if ((chance == 100) && inLair)
                {
                    System.out.println(m.getName()+" treasure #"+i+", has a 100% chance, but is also in lair");
                    continue;
                }
                if (!treasure.containsKey("number"))
                {
                    System.out.println(m.getName()+" treasure #"+i+", does not contain number");
                    continue;
                }
                String number = treasure.get("number").toString();
                try
                {
                    DiceRollBean r = new DiceRollBean(number);
                    if (r.average() < 1)
                    {
                        System.out.println(m.getName()+" treasure #"+i+", number '"+number+"' bad average: "+r.average());
                        continue;
                    }
                    if (r.getMult() > 1 && !inLair)
                    {
                        System.out.println(m.getName()+" treasure #"+i+", number '"+number+"' number="+number+" but not inLair");
                        continue;
                    }
                }
                catch (Exception e)
                {
                    System.out.println(m.getName()+" treasure #"+i+", number '"+number+"' bad format: "+e.getLocalizedMessage());
                    continue;
                }
                if (!treasure.containsKey("type"))
                {
                    System.out.println(m.getName()+" treasure #"+i+", does not contain type");
                    continue;
                }
                String type = (String)treasure.get("type");
                if (!types.contains(type))
                {
                    System.out.println(m.getName()+" treasure #"+i+", unknown type "+type);
                    continue;
                }
            }
        }
        /*
        System.out.println("Types:");
        for (String t : types)
            System.out.println("  "+t);
            */
    }

    public static void addTreasure(CompContextBean context,
            CompTreasuresBean loot)
    {
        for (CompItemTypeBean item : loot.getItems().keySet())
        {
            ItemLogic.addItem(context.getUser().getItems(), item.getID(), loot.getItems().get(item));
        }
        int gold = (int)loot.getTotalValue();
        if (gold >= 1)
        {
            ExperienceLogic.addGold(context.getUser(), gold);
            ExperienceLogic.addXP(context, gold);
            CompIOLogic.logTreasure(context.getUser(), gold);
        }
    }

//    private static void log(String msg)
//    {
//        DebugUtils.trace(msg);
//    }
    
    public static int getChance(JSONObject treasure)
    {
        Object ch = treasure.get("chance");
        if  (ch instanceof Number)
            return ((Number)ch).intValue();
        return StringUtils.digitize(ch.toString());
    }
}
