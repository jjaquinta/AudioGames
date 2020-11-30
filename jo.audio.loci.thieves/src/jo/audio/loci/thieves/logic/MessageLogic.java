package jo.audio.loci.thieves.logic;

import jo.audio.loci.thieves.data.LociPlayer;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

public class MessageLogic
{    
    public static String processMessage(LociPlayer player, String msg)
    {
        msg = StringUtils.process(msg, "<<", ">>", (txt) -> processMessageDirective(player, txt));
        return msg;
    }
    
    private static String processMessageDirective(LociPlayer player, String inbuf)
    {
        String[] args = inbuf.split("\\|");
        switch (args[0])
        {
            case "$d":
            case "degrade":
                return processDegradeDirective(player, args, 1);
            default:
                return processDegradeDirective(player, args, 0);
        }
    }
    
    private static String processDegradeDirective(LociPlayer player, String[] args, int off)
    {
        String key = args[off];
        int freq = player.getPromptFrequency(key);
        String prompt = key;
        while (++off < args.length)
        {
            int threshold = IntegerUtils.parseInt(args[off]);
            if (freq < threshold)
                break;
            prompt = args[++off];
        }
        player.setPromptFrequency(key, freq + 1);
        return prompt;
    }
}
