package jo.audio.loci.sandbox.verb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.utils.ResponseUtils;
import jo.audio.loci.sandbox.data.LociPlayer;

public class VerbHelpRoom extends Verb
{
    private Map<String, List<String>> mHelpTexts = new HashMap<>();
    
    public VerbHelpRoom()
    {
        super("help,\\?", ".*", null, null);
        //addHelp("help", "dummy");
        addHelp("commands", "dummy");
        addHelp("create,create container", "create container named <name> - Make a new container with the given name.");
        addHelp("create,create item", "create item named <name> - Make a new item with the given name.");
    }
    
    private void addHelp(String keys, String value)
    {
        for (StringTokenizer st = new StringTokenizer(keys,","); st.hasMoreTokens(); )
        {
            String key = st.nextToken().toLowerCase();
            List<String> values = mHelpTexts.get(key);
            if (values == null)
            {
                values = new ArrayList<>();
                mHelpTexts.put(key, values);
            }
            values.add(value);
        }
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        String text = context.getDirectObjectText();
        switch (text.toLowerCase())
        {
            case "help":
            {
                List<String> keys = new ArrayList<>();
                keys.addAll(mHelpTexts.keySet());
                Collections.sort(keys);
                player.addMessage("You can ask for help on "+ResponseUtils.wordListOR(keys)+".");
            }
            default:
                List<String> helps = mHelpTexts.get(text.toLowerCase());
                if (helps != null)
                    player.addMessage(helps.toArray(new String[0]));
                else
                    player.addMessage("Try 'help help' for a list of what you can get help on.");
                break;
        }
    }
}
