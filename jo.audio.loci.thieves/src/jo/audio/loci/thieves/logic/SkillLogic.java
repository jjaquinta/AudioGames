package jo.audio.loci.thieves.logic;

import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.thieves.logic.LocationLogic;
import jo.audio.thieves.logic.ThievesConstLogic;

public class SkillLogic
{
    public static boolean rollClimbWalls(LociPlayer player, int mod)
    {
        int target = modClimbWalls(player) + mod;
        int roll = LocationLogic.getCity().getRND().nextInt(100) + 1;
        player.addMessage("+You needed a "+target+" and rolled a "+roll+".");
        return roll <= target;
    }
    public static boolean rollFindTraps(LociPlayer player)
    {
        int target = modFindTraps(player);
        int roll = LocationLogic.getCity().getRND().nextInt(100) + 1;
        player.addMessage("+You needed a "+target+" and rolled a "+roll+".");
        return roll <= target;
    }
    public static boolean rollHearNoise(LociPlayer player)
    {
        int target = modHearNoise(player);
        int roll = LocationLogic.getCity().getRND().nextInt(100) + 1;
        player.addMessage("+You needed a "+target+" and rolled a "+roll+".");
        return roll <= target;
    }
    public static boolean rollHideShadows(LociPlayer player, int mod)
    {
        int target = modHideShadows(player) + mod;
        int roll = LocationLogic.getCity().getRND().nextInt(100) + 1;
        return roll <= target;
    }
    public static boolean rollMoveSilently(LociPlayer player, int mod)
    {
        int target = modMoveSilently(player) + mod;
        int roll = LocationLogic.getCity().getRND().nextInt(100) + 1;
        //player.addMessage("+You needed a "+target+" and rolled a "+roll+".");
        return roll <= target;
    }
    public static boolean rollOpenLocks(LociPlayer player, int mod)
    {
        int target = modOpenLocks(player) + mod;
        int roll = LocationLogic.getCity().getRND().nextInt(100) + 1;
        player.addMessage("+You needed a "+target+" and rolled a "+roll+".");
        return roll <= target;
    }
    public static boolean rollPickPockets(LociPlayer player)
    {
        int target = modPickPockets(player);
        int roll = LocationLogic.getCity().getRND().nextInt(100) + 1;
        player.addMessage("+You needed a "+target+" and rolled a "+roll+".");
        return roll <= target;
    }
    public static boolean rollReadLanguages(LociPlayer player)
    {
        int target = modReadLanguages(player);
        int roll = LocationLogic.getCity().getRND().nextInt(100) + 1;
        player.addMessage("+You needed a "+target+" and rolled a "+roll+".");
        return roll <= target;
    }

    public static int modClimbWalls(LociPlayer player)
    {
        return baseClimbWalls(player.getLevel()) + dexClimbWalls(player.getDexModified()) + raceClimbWalls(player.getRace());
    }
    public static int modFindTraps(LociPlayer player)
    {
        return baseFindTraps(player.getLevel()) + dexFindTraps(player.getDexModified()) + raceFindTraps(player.getRace());
    }
    public static int modHearNoise(LociPlayer player)
    {
        return baseHearNoise(player.getLevel()) + dexHearNoise(player.getDexModified()) + raceHearNoise(player.getRace());
    }
    public static int modHideShadows(LociPlayer player)
    {
        return baseHideShadows(player.getLevel()) + dexHideShadows(player.getDexModified()) + raceHideShadows(player.getRace());
    }
    public static int modMoveSilently(LociPlayer player)
    {
        return baseMoveSilently(player.getLevel()) + dexMoveSilently(player.getDexModified()) + raceMoveSilently(player.getRace());
    }
    public static int modOpenLocks(LociPlayer player)
    {
        return baseOpenLocks(player.getLevel()) + dexOpenLocks(player.getDexModified()) + raceOpenLocks(player.getRace());
    }
    public static int modPickPockets(LociPlayer player)
    {
        return basePickPockets(player.getLevel()) + dexPickPockets(player.getDexModified()) + racePickPockets(player.getRace());
    }
    public static int modReadLanguages(LociPlayer player)
    {
        return baseReadLanguages(player.getLevel()) + dexReadLanguages(player.getDexModified()) + raceReadLanguages(player.getRace());
    }
    public static int baseClimbWalls(int level)
    {
        return 78 + level*2;
    }
    public static int baseFindTraps(int level)
    {
        return 21 + level*4;
    }
    public static int baseHearNoise(int level)
    {
        return 7 + level*3;
    }
    public static int baseHideShadows(int level)
    {
        return 15 + level*5;
    }
    public static int baseMoveSilently(int level)
    {
        return 15 + level*5;
    }
    public static int baseOpenLocks(int level)
    {
        return 26 + level*4;
    }
    public static int basePickPockets(int level)
    {
        return 31 + level*4;
    }
    public static int baseReadLanguages(int level)
    {
        return 0 + level*5;
    }
    public static int dexClimbWalls(int dex)
    {
        return 0;
    }
    public static int dexFindTraps(int dex)
    {
        if (dex < 12)
            return -5*(12 - dex);
        else if (dex > 16)
            return 5*(dex - 16);
        else
            return 0;
    }
    public static int dexHearNoise(int dex)
    {
        return 0;
    }
    public static int dexHideShadows(int dex)
    {
        if (dex < 11)
            return -5*(11 - dex);
        else if (dex > 16)
            return 5*(dex - 16);
        else
            return 0;
    }
    public static int dexMoveSilently(int dex)
    {
        if (dex < 13)
            return -5*(13 - dex);
        else if (dex > 16)
            return 5*(dex - 16);
        else
            return 0;
    }
    public static int dexOpenLocks(int dex)
    {
        if (dex < 11)
            return -5*(11 - dex);
        else if (dex > 15)
            return 5*(dex - 15);
        else
            return 0;
    }
    public static int dexPickPockets(int dex)
    {
        if (dex < 12)
            return -5*(12 - dex);
        else if (dex > 17)
            return 5*(dex - 17);
        else
            return 0;
    }
    public static int dexReadLanguages(int dex)
    {
        return 0;
    }
    public static int raceClimbWalls(String race)
    {
        switch (race)
        {
            case ThievesConstLogic.RACE_DWARF:
                return -10;
            case ThievesConstLogic.RACE_ELF:
                return -5;
            case ThievesConstLogic.RACE_GNOME:
                return -15;
            case ThievesConstLogic.RACE_HALF_ELF:
                return 0;
            case ThievesConstLogic.RACE_HALFLING:
                return -15;
            case ThievesConstLogic.RACE_HALF_ORC:
                return 5;
            case ThievesConstLogic.RACE_HUMAN:
                return 5;
        }
        throw new IllegalStateException();
    }
    public static int raceFindTraps(String race)
    {
        switch (race)
        {
            case ThievesConstLogic.RACE_DWARF:
                return 15;
            case ThievesConstLogic.RACE_ELF:
                return 5;
            case ThievesConstLogic.RACE_GNOME:
                return 0;
            case ThievesConstLogic.RACE_HALF_ELF:
                return 0;
            case ThievesConstLogic.RACE_HALFLING:
                return 0;
            case ThievesConstLogic.RACE_HALF_ORC:
                return 5;
            case ThievesConstLogic.RACE_HUMAN:
                return 0;
        }
        throw new IllegalStateException();
    }
    public static int raceHearNoise(String race)
    {
        switch (race)
        {
            case ThievesConstLogic.RACE_DWARF:
                return 0;
            case ThievesConstLogic.RACE_ELF:
                return 5;
            case ThievesConstLogic.RACE_GNOME:
                return 5;
            case ThievesConstLogic.RACE_HALF_ELF:
                return 0;
            case ThievesConstLogic.RACE_HALFLING:
                return 5;
            case ThievesConstLogic.RACE_HALF_ORC:
                return 5;
            case ThievesConstLogic.RACE_HUMAN:
                return 0;
        }
        throw new IllegalStateException();
    }
    public static int raceHideShadows(String race)
    {
        switch (race)
        {
            case ThievesConstLogic.RACE_DWARF:
                return 0;
            case ThievesConstLogic.RACE_ELF:
                return 10;
            case ThievesConstLogic.RACE_GNOME:
                return 0;
            case ThievesConstLogic.RACE_HALF_ELF:
                return 5;
            case ThievesConstLogic.RACE_HALFLING:
                return 15;
            case ThievesConstLogic.RACE_HALF_ORC:
                return 0;
            case ThievesConstLogic.RACE_HUMAN:
                return 0;
        }
        throw new IllegalStateException();
    }
    public static int raceMoveSilently(String race)
    {
        switch (race)
        {
            case ThievesConstLogic.RACE_DWARF:
                return -5;
            case ThievesConstLogic.RACE_ELF:
                return 5;
            case ThievesConstLogic.RACE_GNOME:
                return 0;
            case ThievesConstLogic.RACE_HALF_ELF:
                return 0;
            case ThievesConstLogic.RACE_HALFLING:
                return 15;
            case ThievesConstLogic.RACE_HALF_ORC:
                return 0;
            case ThievesConstLogic.RACE_HUMAN:
                return 0;
        }
        throw new IllegalStateException();
    }
    public static int raceOpenLocks(String race)
    {
        switch (race)
        {
            case ThievesConstLogic.RACE_DWARF:
                return 15;
            case ThievesConstLogic.RACE_ELF:
                return -5;
            case ThievesConstLogic.RACE_GNOME:
                return 10;
            case ThievesConstLogic.RACE_HALF_ELF:
                return 0;
            case ThievesConstLogic.RACE_HALFLING:
                return 0;
            case ThievesConstLogic.RACE_HALF_ORC:
                return 5;
            case ThievesConstLogic.RACE_HUMAN:
                return 5;
        }
        throw new IllegalStateException();
    }
    public static int racePickPockets(String race)
    {
        switch (race)
        {
            case ThievesConstLogic.RACE_DWARF:
                return 0;
            case ThievesConstLogic.RACE_ELF:
                return 5;
            case ThievesConstLogic.RACE_GNOME:
                return 0;
            case ThievesConstLogic.RACE_HALF_ELF:
                return 10;
            case ThievesConstLogic.RACE_HALFLING:
                return 5;
            case ThievesConstLogic.RACE_HALF_ORC:
                return -5;
            case ThievesConstLogic.RACE_HUMAN:
                return 0;
        }
        throw new IllegalStateException();
    }
    public static int raceReadLanguages(String race)
    {
        switch (race)
        {
            case ThievesConstLogic.RACE_DWARF:
                return -5;
            case ThievesConstLogic.RACE_ELF:
                return 10;
            case ThievesConstLogic.RACE_GNOME:
                return 0;
            case ThievesConstLogic.RACE_HALF_ELF:
                return 0;
            case ThievesConstLogic.RACE_HALFLING:
                return -5;
            case ThievesConstLogic.RACE_HALF_ORC:
                return -10;
            case ThievesConstLogic.RACE_HUMAN:
                return 0;
        }
        throw new IllegalStateException();
    }
}
