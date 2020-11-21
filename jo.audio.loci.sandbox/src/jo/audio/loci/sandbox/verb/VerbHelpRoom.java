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
        addHelp("delete", "delete <item> - Destroy an item, exit or room.");
        addHelp("describe", "describe <item> as <text> - Set the description on an object.");
        addHelp("dig", "dig <direction>, optional to <destination> - Create a passage to a new or existing room.");
        addHelp("drop", "drop <item> - Move an item from your inventory to the room.");
        addHelp("go", "go <direction>, or just <direction> - Move through an exit to a new room.");
        addHelp("inventory", "inventory - List the items you are holding.");
        addHelp("look", "look <item> - Describe an object in more detail.");
        addHelp("name", "name <item> as <text> - Set the name of an object.");
        addHelp("open", "open <container> - Open a container so you can put items in, or take them out.");
        addHelp("take,pick up,get", "take <item> - Move an item from the room to your inventory.");
        addHelp("put", "put <item> in <container> - Move an item from your inventory into an open container.");
        addHelp("say", "say <text> - Pass a message to everyone in the room.");
        addHelp("set", "set <item> to public or private - Change the priviledge on an object.");
        addHelp("shut,close", "shut <container> - Shut a container to prevent items being put in, or taken out.");
        addHelp("take", "take <item> out of <container> - Move an item from an open container into your inventory.");
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
                listCommands(player);
                break;
            default:
                List<String> helps = mHelpTexts.get(text.toLowerCase());
                if (helps != null)
                    player.addMessage(helps.toArray(new String[0]));
                else
                    listCommands(player);
                break;
        }
    }

    private void listCommands(LociPlayer player)
    {
        List<String> keys = new ArrayList<>();
        keys.addAll(mHelpTexts.keySet());
        Collections.sort(keys);
        player.addMessage("You can ask for help on "+ResponseUtils.wordListOR(keys)+".");
    }
}
