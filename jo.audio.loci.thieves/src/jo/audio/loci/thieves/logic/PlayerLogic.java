package jo.audio.loci.thieves.logic;

import java.util.Arrays;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociPlayerAdmin;
import jo.audio.thieves.logic.LocationLogic;
import jo.audio.thieves.logic.ThievesConstLogic;

public class PlayerLogic
{
    public static LociPlayer getPlayer(String userName)
    {
        LociBase player = DataStoreLogic.findFirst(LociPlayer.class.getSimpleName(), (obj) -> {
            LociPlayer p = (LociPlayer)obj;
            return userName.equalsIgnoreCase(p.getPrimaryName());
            });
        if (player == null)
            player = DataStoreLogic.findFirst(LociPlayerAdmin.class.getSimpleName(), (obj) -> {
                LociPlayer p = (LociPlayer)obj;
                return userName.equalsIgnoreCase(p.getPrimaryName());
                });
        return (LociPlayer)player;
    }
    
    public static void rollUp(LociPlayer comp)
    {
        comp.setRace(ThievesConstLogic.RACE_HUMAN);
        comp.setGender(LocationLogic.getCity().getRND().nextBoolean() ? "male" : "female");
        int[] stats = new int[6];
        for (int i = 0; i < stats.length; i++)
            stats[i] = rollStat();
        Arrays.sort(stats);
        comp.setDex(stats[5]);
        comp.setInt(stats[4]);
        comp.setCha(stats[3]);
        comp.setCon(stats[2]);
        comp.setStr(stats[1]);
        comp.setWis(stats[0]);
        if (comp.getRace().equals(ThievesConstLogic.RACE_DWARF))
            modifyStatsDwarf(comp);
        else if (comp.getRace().equals(ThievesConstLogic.RACE_ELF))
            modifyRaceElf(comp);
        // class
        comp.setLevel(1);
        comp.setXP(0);
        comp.setHitPoints(LocationLogic.getCity().getRND().nextInt(6)+1+getHitPointBonus(comp));
        comp.setCurrentHitPoints(comp.getHitPoints());
    }

    protected static void modifyRaceElf(LociPlayer comp)
    {
        comp.setCon(comp.getCon() - 1);
        comp.setDex(comp.getDex() + 1);
        if (comp.getStr() < 3)
            comp.setStr(3);
        if (comp.getStr() > 18)
            comp.setStr(18);
        if (comp.getDex() < 7)
            comp.setDex(7);
        if (comp.getDex() > 19)
            comp.setDex(19);
        if (comp.getCon() < 8)
            comp.setCon(8);
        if (comp.getCon() > 19)
            comp.setCon(19);
        if (comp.getInt() < 8)
            comp.setInt(8);
        if (comp.getInt() > 18)
            comp.setInt(18);
        if (comp.getWis() < 3)
            comp.setWis(3);
        if (comp.getWis() > 18)
            comp.setWis(18);
        if (comp.getCha() < 8)
            comp.setCha(8);
        if (comp.getCha() > 18)
            comp.setCha(18);
    }

    protected static void modifyStatsDwarf(LociPlayer comp)
    {
        comp.setCon(comp.getCon() + 1);
        comp.setCha(comp.getCha() - 1);
        if (comp.getStr() < 8)
            comp.setStr(8);
        if (comp.getStr() > 18)
            comp.setStr(18);
        if (comp.getDex() < 3)
            comp.setDex(3);
        if (comp.getDex() > 17)
            comp.setDex(17);
        if (comp.getCon() < 12)
            comp.setCon(12);
        if (comp.getCon() > 19)
            comp.setCon(19);
        if (comp.getInt() < 3)
            comp.setInt(3);
        if (comp.getInt() > 18)
            comp.setInt(18);
        if (comp.getWis() < 3)
            comp.setWis(3);
        if (comp.getWis() > 18)
            comp.setWis(18);
        if (comp.getCha() < 3)
            comp.setCha(3);
        if (comp.getCha() > 16)
            comp.setCha(16);
    }
    
    public static int getHitPointBonus(LociPlayer comp)
    {
        switch (comp.getConModified())
        {
            case 25:
                return 8;
            case 24:
            case 23:
                return 7;
            case 22:
            case 21:
                return 6;
            case 20:
            case 19:
                return 5;
            case 18:
                return 4;
            case 17:
                return 3;
            case 16:
                return 2;
            case 15:
                return 1;
            case 6:
                return -1;
            case 5:
                return -1;
            case 4:
                return -1;
            case 3:
                return -2;
            default:
                return 0;
        }
    }
    
    private static int rollStat()
    {
        int[] dice = new int[4];
        dice[0] = LocationLogic.getCity().getRND().nextInt(6)+1;
        dice[1] = LocationLogic.getCity().getRND().nextInt(6)+1;
        dice[2] = LocationLogic.getCity().getRND().nextInt(6)+1;
        dice[3] = LocationLogic.getCity().getRND().nextInt(6)+1;
        Arrays.sort(dice);
        return dice[1] + dice[2] + dice[3];
    }
    
    private static final int[] XP_TO_LEVEL = {
        1250, 2500, 5000, 10000, 20000, 40000, 70000, 110000, 160000,
        220000, 440000, 660000, 880000, 
    };
    
    private static int xpForLevel(int level)
    {
        if (level == 1)
            return 0;
        if (level <= XP_TO_LEVEL.length + 1)
            return XP_TO_LEVEL[level-2];
        else
            return 220000 + (level - 11)*220000;
    }
    
    private static int levelForXP(int xp)
    {
        for (int level = 1; true; level++)
            if (xp < xpForLevel(level))
                return level - 1;
    }
    
    public static void addXP(LociPlayer player, int xpAdd)
    {
        int xp = player.getXP() + xpAdd;
        player.setXP(xp);
        int level = levelForXP(xp);
        if (xp == 1)
            player.addMessage("You gain one experience point.");
        else
            player.addMessage("You gain "+xpAdd+" experience points.");
        player.addMessage("+You now have a total of "+player.getXP()+".");
        while (player.getLevel() < level)
        {
            player.setLevel(player.getLevel() + 1);
            // go up a level
            player.addMessage("You have gone up to level "+player.getLevel()+".");
            int hp = getHitPointBonus(player);
            if (player.getLevel() <= 10)
                hp += LocationLogic.getCity().getRND().nextInt(6)+1;
            else
                hp += 2;
            player.setCurrentHitPoints(player.getCurrentHitPoints() + hp);
            player.setHitPoints(player.getHitPoints() + hp);            
        }
        int needed = xpForLevel(player.getLevel() + 1) - player.getXP();
        player.addMessage("+You need "+needed+" more to reach level "+(player.getLevel()+1)+".");
    }
}
