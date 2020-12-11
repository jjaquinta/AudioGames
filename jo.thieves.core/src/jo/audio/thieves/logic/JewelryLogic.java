package jo.audio.thieves.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class JewelryLogic
{
    private static final Map<String, int[]> JEWELRY = new HashMap<>();
    private static int TOTAL_WEIGHTS;
    static
    {
        JEWELRY.put("Amulet", new int[] { 3,4,7,9,10});
        JEWELRY.put("Anklet", new int[] { 1,3,6,8,9,10});
        JEWELRY.put("Arm-ring", new int[] { 3,4,6,8,9,10});
        JEWELRY.put("Belt", new int[] { 3,4,6,8,10});
        JEWELRY.put("Box", new int[] { 2,4,7,9,10});
        JEWELRY.put("Bracelet", new int[] { 5,3,6,8,9,10});
        JEWELRY.put("Brooch", new int[] { 3,3,6,8,9,10});
        JEWELRY.put("Buckle", new int[] { 3,4,7,9,10});
        JEWELRY.put("Chain", new int[] { 2,4,7,9,10});
        JEWELRY.put("Chalice", new int[] { 2,3,6,8,9,10});
        JEWELRY.put("Choker", new int[] { 3,4,7,9,10});
        JEWELRY.put("Clasp", new int[] { 2,4,6,8,10});
        JEWELRY.put("Comb", new int[] { 3,3,6,8,9,10});
        JEWELRY.put("Coronet", new int[] { 2,1,2,4,8,9,10});
        JEWELRY.put("Crown", new int[] { 1,1,2,3,4,7,10});
        JEWELRY.put("Diadem", new int[] { 2,3,6,8,9,10});
        JEWELRY.put("Earring", new int[] { 6,3,6,8,9,10});
        JEWELRY.put("Goblet", new int[] { 3,3,6,8,9,10});
        JEWELRY.put("Idol", new int[] { 2,2,4,7,9,10});
        JEWELRY.put("Knife", new int[] { 3,4,7,9,10});
        JEWELRY.put("Locket", new int[] { 4,3,5,7,9,10});
        JEWELRY.put("Medal", new int[] { 2,4,7,10});
        JEWELRY.put("Medallion", new int[] { 4,3,6,9,10});
        JEWELRY.put("Necklace", new int[] { 4,3,6,8,9,10});
        JEWELRY.put("Pendant", new int[] { 4,3,6,8,9,10});
        JEWELRY.put("Pin", new int[] { 4,3,6,8,9,10});
        JEWELRY.put("Orb", new int[] { 1,1,2,3,5,8,10});
        JEWELRY.put("Ring", new int[] { 8,3,6,8,9,10});
        JEWELRY.put("Sceptre", new int[] { 1,1,2,3,5,8,10});
        JEWELRY.put("Seal", new int[] { 4,4,7,9,10});
        JEWELRY.put("Statuette", new int[] { 2,3,6,8,9,10});
        JEWELRY.put("Tiara", new int[] { 1,3,6,8,9,10});
        JEWELRY.put("Toe-ring", new int[] { 2,3,6,8,9,10});
        JEWELRY.put("Weapon-hilt", new int[] { 3,3,6,8,9,10});
        TOTAL_WEIGHTS = 0;
        for (int[] values : JEWELRY.values())
            TOTAL_WEIGHTS += values[0];
    }
    
    public static int rollJewelry(Random rnd, StringBuffer name)
    {
        if (rnd == null)
            rnd = LocationLogic.getCity().getRND();
        int roll1 = rnd.nextInt(TOTAL_WEIGHTS);
        int roll2 = rnd.nextInt(10);
        int[] values = null;
        for (String key : JEWELRY.keySet())
        {
            values = JEWELRY.get(key);
            roll1 -= values[0];
            if (roll1 < 0)
            {
                name.append(key);
                break;
            }
        }
        int value = 0;
        if (roll2 < values[1])
        {
            name.insert(0, "Silver ");
            value = DiceLogic.d(1, 10, 0, rnd)*100;
        }
        else if (roll2 < values[2])
        {
            name.insert(0, "Silver and Gold ");
            value = DiceLogic.d(2, 6, 0, rnd)*100;
        }
        else if (roll2 < values[3])
        {
            name.insert(0, "Golden ");
            value = DiceLogic.d(3, 6, 0, rnd)*100;
        }
        else if (roll2 < values[4])
        {
            name.insert(0, "Silver and Gem ");
            value = DiceLogic.d(5, 6, 0, rnd)*100;
        }
        else if (roll2 < values[5])
        {
            name.insert(0, "Gold and Gem ");
            value = DiceLogic.d(2, 4, 0, rnd)*1000;
        }
        else
        {
            name.insert(0, "Exceptional ");
            value = DiceLogic.d(2, 6, 0, rnd)*1000;
        }
        return value;
    }
}
