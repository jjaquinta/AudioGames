package jo.audio.loci.thieves.verbs.misc;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.util.utils.obj.StringUtils;

public class VerbTime extends Verb
{
    public VerbTime()
    {
        super("time", null, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        doTime(player);
    }
    
    public static void doTime(LociPlayer player)
    {
        long tick = ThievesConstLogic.gameTime();
        int hour = (int)((tick/60)%24);
        int minute = (int)(tick%60);
        String time = "";
        String ampm = null;
        if (minute == 0)
        {
            if (hour == 0)
            {
                time = "midnight";
                ampm = "";
            }
            else if (hour == 12)
            {
                time = "noon";
                ampm = ".";
            }
            else
                time = (hour+" O'Clock");
        }
        else
        {
            if ((hour == 0) || (hour == 12))
                time = "12";
            else
                time = String.valueOf(hour);
            time += ":"+StringUtils.zeroPrefix(minute, 2);
        }
        if (ampm == null)
            if (hour < 12)
                ampm = " a.m.";
            else
                ampm = " p.m.";
        player.addMessage("The time is "+time+ampm);
    }
}
