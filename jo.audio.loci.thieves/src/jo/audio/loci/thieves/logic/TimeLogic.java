package jo.audio.loci.thieves.logic;

import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociLocality;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociSquare;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.logic.ThievesConstLogic;

public class TimeLogic
{
    public static void moveCheck(LociPlayer player, LociLocality location)
    {
        updateHiding(player, location);
        updateSilent(player, location);
        long lastMove = player.getLastMoveCheck();
        long thisMove = ThievesConstLogic.gameTime();
        if (crossedBoundary(lastMove, thisMove, ThievesConstLogic.HEAL_TIME))
            doHeal(player);
        notifySun(player, lastMove, thisMove);
        notifyMoon(player, lastMove, thisMove);
        player.setLastMoveCheck(thisMove);
    }
    
    public static void updateSilent(LociPlayer player, LociLocality location)
    {
        if (location == null)
            location = (LociLocality)DataStoreLogic.load(player.getContainedBy());
        if (!(location instanceof LociSquare))
        {
            player.setSilent(true);
            return;
        }
        LociSquare loc = (LociSquare)location;
        PSquare sq = loc.getURIObject().getThis();
        int mod = sq.getMoveSilentlyMod();
        boolean silent = SkillLogic.rollMoveSilently(player, mod);
        player.setSilent(silent);
        if (silent)
            return;
        // alert nearby
        for (int dir : ThievesConstLogic.ORTHOGONAL_DIRS)
            ObserverLogic.alertNearby(loc, dir);
        ObserverLogic.alertNearby(loc, -1);
    }
    
    private static void updateHiding(LociPlayer player, LociLocality location)
    {
        if (!(location instanceof LociSquare))
        {
            player.setHiding(true);
            return;
        }
        LociSquare loc = (LociSquare)location;
        PSquare sq = loc.getURIObject().getThis();
        int mod = sq.getHideInShadowsMod();
        if (!sq.getInside())
            mod += TimeLogic.getHideInShadowsMod();
        boolean hiding = SkillLogic.rollHideShadows(player, mod);
        if (hiding)
            player.addMessage("+You are hidden in shadows.");
        player.setHiding(hiding);
    }
    
    private static int getHideInShadowsMod()
    {
        int hour = ThievesConstLogic.gameHour(ThievesConstLogic.gameTime());
        double a = (hour/24.0)*Math.PI*2;
        double m = Math.cos(a);
        int mod = -(int)(m*15);
        return mod;
    }

    private static boolean crossedBoundary(long tick1, long tick2, long threshold)
    {
        return (tick1/threshold) != (tick2/threshold);
    }
    
    private static void doHeal(LociPlayer player)
    {
        if (player.getCurrentHitPoints() >= player.getHitPoints())
            return;
        player.setCurrentHitPoints(player.getCurrentHitPoints() + 1);
        if (player.getCurrentHitPoints() == player.getHitPoints())
            player.addMessage("You are fully healed.");
    }

    private static void notifyMoon(LociPlayer player, long oldTime, long newTime)
    {
        int oldHour = ThievesConstLogic.getHourOfMoon(oldTime);
        int newHour = ThievesConstLogic.getHourOfMoon(newTime);
        if (oldHour != newHour)
        {
            int phase = ThievesConstLogic.getMoonPhase(newTime);
            String tphase = "{{MOON_PHASE#"+phase+"}}";
            switch (newHour)
            {
                case 6:
                    player.addMessage("The "+tphase+" is rising over the horizon.");
                    break;
                case 12:
                    player.addMessage("The "+tphase+" is directy overhead.");
                    break;
                case 18:
                    player.addMessage("the "+tphase+" is setting.");
                    break;
            }
        }
    }

    private static void notifySun(LociPlayer player, long oldTime, long newTime)
    {
        int oldHour = (int)((oldTime/60)%24);
        int newHour = (int)((newTime/60)%24);
        if (oldHour != newHour)
        {
            switch (newHour)
            {
                case 0:
                    player.addMessage("It is midnight.");
                    player.addMessage("<<You can say 'about time' at any point to get the exact time.|5|>>");
                    break;
                case 5:
                    player.addMessage("It's just before dawn.");
                    break;
                case 6:
                    player.addMessage("The sun is coming up.");
                    player.addMessage("<<You can say 'about time' at any point to get the exact time.|5|>>");
                    break;
                case 12:
                    player.addMessage("It's just gone noon.");
                    break;
                case 17:
                    player.addMessage("The sun is getting low on the horizon.");
                    break;
                case 18:
                    player.addMessage("The sun has just set.");
                    player.addMessage("<<You can say 'about time' at any point to get the exact time.|5|>>");
                    break;
            }
        }
    }

}
