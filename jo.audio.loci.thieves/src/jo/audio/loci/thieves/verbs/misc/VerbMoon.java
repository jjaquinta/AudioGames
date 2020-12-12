package jo.audio.loci.thieves.verbs.misc;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.thieves.logic.ThievesConstLogic;

public class VerbMoon extends Verb
{
    public VerbMoon()
    {
        super("moon", null, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        doMoon(player);
    }
    
    public static void doMoon(LociPlayer player)
    {
        long newTime = ThievesConstLogic.gameTime();
        int moonHour = ThievesConstLogic.getHourOfMoon(newTime);
        int phase = ThievesConstLogic.getMoonPhase(newTime);
        String tphase = "{{MOON_PHASE#"+phase+"}}";
        switch (moonHour)
        {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                player.addMessage("The "+tphase+" is below the horizon.");
                break;
            case 5:
                player.addMessage("The "+tphase+" is just about to rise.");
                break;
            case 6:
                player.addMessage("The "+tphase+" has just risen over the horizon.");
                break;
            case 7:
            case 8:
            case 9:
            case 10:
                player.addMessage("The "+tphase+" is rising in the sky.");
                break;
            case 11:
                player.addMessage("The "+tphase+" is almost at its highest point.");
                break;
            case 12:
                player.addMessage("The "+tphase+" is directy overhead.");
                break;
            case 13:
                player.addMessage("The "+tphase+" is just past its highest point.");
                break;
            case 14:
            case 15:
            case 16:
                player.addMessage("The "+tphase+" is sinking towards the horizon.");
                break;
            case 17:
                player.addMessage("The "+tphase+" is nearing the horizon.");
                break;
            case 18:
                player.addMessage("the "+tphase+" is setting.");
                break;
            case 19:
                player.addMessage("the "+tphase+" has just set.");
                break;
            case 20:
            case 21:
            case 22:
            case 23:
                player.addMessage("The "+tphase+" is below the horizon.");
                break;
        }
    }
}
