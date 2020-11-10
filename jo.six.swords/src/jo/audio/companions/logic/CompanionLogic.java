package jo.audio.companions.logic;

import java.util.Arrays;
import java.util.Iterator;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.util.BaseUserState;
import jo.util.utils.MathUtils;
import jo.util.utils.obj.IntegerUtils;

public class CompanionLogic
{
    private static long mNextID = System.currentTimeMillis();
    
    public static CompCompanionBean newInstance(int race)
    {
        CompCompanionBean comp = new CompCompanionBean();
        comp.setID("comp://"+mNextID++);
        comp.setRace(race);
        comp.setGender(BaseUserState.RND.nextBoolean() ? 0 : 1);
        comp.setName(NameLogic.getName(race, comp.isMale()));
        int[] stats = new int[6];
        for (int i = 0; i < stats.length; i++)
            stats[i] = rollStat();
        Arrays.sort(stats);
        comp.setSTR(stats[5]);
        comp.setCON(stats[4]);
        comp.setDEX(stats[3]);
        comp.setINT(stats[2]);
        comp.setWIS(stats[1]);
        comp.setCHA(stats[0]);
        if (comp.getRace() == CompConstLogic.RACE_DWARF)
        {
            comp.setCON(comp.getCON() + 1);
            comp.setCHA(comp.getCHA() - 1);
            if (comp.getSTR() < 8)
                comp.setSTR(8);
            if (comp.getSTR() > 18)
                comp.setSTR(18);
            if (comp.getDEX() < 3)
                comp.setDEX(3);
            if (comp.getDEX() > 17)
                comp.setDEX(17);
            if (comp.getCON() < 12)
                comp.setCON(12);
            if (comp.getCON() > 19)
                comp.setCON(19);
            if (comp.getINT() < 3)
                comp.setINT(3);
            if (comp.getINT() > 18)
                comp.setINT(18);
            if (comp.getWIS() < 3)
                comp.setWIS(3);
            if (comp.getWIS() > 18)
                comp.setWIS(18);
            if (comp.getCHA() < 3)
                comp.setCHA(3);
            if (comp.getCHA() > 16)
                comp.setCHA(16);
        }
        if (comp.getRace() == CompConstLogic.RACE_ELF)
        {
            comp.setCON(comp.getCON() - 1);
            comp.setDEX(comp.getDEX() + 1);
            if (comp.getSTR() < 3)
                comp.setSTR(3);
            if (comp.getSTR() > 18)
                comp.setSTR(18);
            if (comp.getDEX() < 7)
                comp.setDEX(7);
            if (comp.getDEX() > 19)
                comp.setDEX(19);
            if (comp.getCON() < 8)
                comp.setCON(8);
            if (comp.getCON() > 19)
                comp.setCON(19);
            if (comp.getINT() < 8)
                comp.setINT(8);
            if (comp.getINT() > 18)
                comp.setINT(18);
            if (comp.getWIS() < 3)
                comp.setWIS(3);
            if (comp.getWIS() > 18)
                comp.setWIS(18);
            if (comp.getCHA() < 8)
                comp.setCHA(8);
            if (comp.getCHA() > 18)
                comp.setCHA(18);
        }
        if (comp.getSTR() == 18)
            comp.setSTRpc(BaseUserState.RND.nextInt(100)+1);
        // class
        comp.setClazz(CompConstLogic.CLASS_FIGHTER);
        comp.setLevel(1);
        comp.setHitPoints(BaseUserState.RND.nextInt(10)+1+getHitPointBonus(comp));
        comp.setWeaponProficiences(4);
        comp.setNonProficientPenalty(-2);
        String[] items = CompConstLogic.INITIAL_ITEMS[BaseUserState.RND.nextInt(CompConstLogic.INITIAL_ITEMS.length)];
        for (String item : items)
        {
            int o = item.indexOf(':');
            int quan = 1;
            if (o > 0)
            {
                quan = IntegerUtils.parseInt(item.substring(o + 1));
                item = item.substring(0, o);
            }
            CompItemInstanceBean inst = ItemLogic.createInstance(item, quan);
            comp.getItems().add(inst);
            CompItemTypeBean type = inst.getType();
            if ((type.getType() == CompItemTypeBean.TYPE_HAND) || (type.getType() == CompItemTypeBean.TYPE_HURLED) || (type.getType() == CompItemTypeBean.TYPE_LAUNCHER))
                comp.getProficiencies().add(type.getBaseID());
        }
        comp.setCurrentHitPoints(comp.getHitPoints());
        return comp;
    }
    
    public static int getHitPointBonus(CompCompanionBean comp)
    {
        switch (comp.getCONModified())
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
        dice[0] = BaseUserState.RND.nextInt(6)+1;
        dice[1] = BaseUserState.RND.nextInt(6)+1;
        dice[2] = BaseUserState.RND.nextInt(6)+1;
        dice[3] = BaseUserState.RND.nextInt(6)+1;
        Arrays.sort(dice);
        return dice[1] + dice[2] + dice[3];
    }

    public static void doRemoveItem(CompCompanionBean comp,
            String id, int amnt)
    {
        ItemLogic.removeItem(comp.getItems(), id, amnt);
    }

    public static void doAddItem(CompUserBean user, CompCompanionBean comp,
            String id, int amnt)
    {
        ItemLogic.addItem(comp.getItems(), id, amnt);
        rationalizeItems(user, comp);
    }
    
    private static void rationalizeItems(CompUserBean user, CompCompanionBean comp)
    {
        boolean hasArmor = false;
        boolean hasShield = false;
        int handsUsed = 0;
        for (Iterator<CompItemInstanceBean> i = comp.getItems().iterator(); i.hasNext(); )
        {
            CompItemInstanceBean i2 = i.next();
            boolean remove = false;
            switch (i2.getType().getType())
            {
                case CompItemTypeBean.TYPE_AMMO:
                    remove = true;
                    break;
                case CompItemTypeBean.TYPE_ARMOR:
                    if (hasArmor)
                        remove = true;
                    else
                        hasArmor = true;
                    break;
                case CompItemTypeBean.TYPE_HAND:
                case CompItemTypeBean.TYPE_HURLED:
                case CompItemTypeBean.TYPE_LAUNCHER:
                    if (handsUsed  + i2.getType().getHandsNeeded() > 2)
                        remove = true;
                    break;
                case CompItemTypeBean.TYPE_SHIELD:
                    if ((handsUsed + 1 > 2) || hasShield)
                        remove = true;
                    else
                    {
                        hasShield = true;
                        handsUsed++;
                    }
                    break;
            }
            if (remove)
            {
                i.remove();
                UserLogic.doAddItem(user, i2.getID(), i2.getQuantity());
            }
        }
    }

    public static boolean isXPBonus(CompCompanionBean player)
    {
        if (player.getClazz() == CompConstLogic.CLASS_FIGHTER)
            return player.getSTR() >= 16;
        throw new IllegalArgumentException("Class "+player.getClazz()+" not supported.");
    }
    
    public static int levelForXP(CompCompanionBean player, int xp)
    {
        if (xp < 0)
            xp = player.getExperiencePoints();
        int[] thresholds = CompConstLogic.TABLE_XP_FOR_CLASS[player.getClazz()];
        for (int i = 0; i < thresholds.length; i++)
            if (xp < thresholds[i])
                return i;
        return thresholds.length;
    }
    
    public static float analogLevelForXP(CompCompanionBean player, int xp)
    {
        if (xp < 0)
            xp = player.getExperiencePoints();
        if (xp <= 0)
            return 1;
        int[] thresholds = CompConstLogic.TABLE_XP_FOR_CLASS[player.getClazz()];
        for (int i = 0; i < thresholds.length; i++)
            if (xp < thresholds[i])
                return MathUtils.interpolate(xp, thresholds[i-1], thresholds[i], i, i+1);
        return thresholds.length;
    }

    public static DiceRollBean getHPForLevel(CompCompanionBean player)
    {
        if (player.getClazz() == CompConstLogic.CLASS_FIGHTER)
        {
            DiceRollBean roll;
            if (player.getLevel() < 10)
                roll = new DiceRollBean(1, 10);
            else
                roll = new DiceRollBean(0, 0, 3);
            int mod = CompanionLogic.getHitPointBonus(player);
            if (mod != 0)
                roll.setMod(roll.getMod() + mod);
            return roll;
        }
        throw new IllegalArgumentException("Class "+player.getClazz()+" not supported.");
    }
}
