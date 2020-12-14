package jo.audio.thieves.logic;

import java.util.List;
import java.util.Random;

public class DiceLogic
{
    public static int d(int die, int num, int mod, Random rnd)
    {
        int total = num + mod;
        for (int i = 0; i < num; i++)
            total += rnd.nextInt(die);
        return total;
    }
    public static int d(int die, int num, int mod)
    {
        return d(die, num, mod, LocationLogic.getCity().getRND());
    }
    public static int d(int die, int num)
    {
        return d(die, num, 0);
    }
    
    public static <T> T oneOf(List<T> things)
    {
        if (things.size() == 0)
            return null;
        int idx = LocationLogic.getCity().getRND().nextInt(things.size());
        return things.get(idx);
    }
}
