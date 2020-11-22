package jo.audio.loci.sandbox.verb;

import java.util.List;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.sandbox.data.LociExit;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociRoom;

public class VerbDig extends Verb
{
    public VerbDig()
    {
        super("dig", ".*", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        String direction = context.getDirectObjectText();
        String primaryDirection = direction;
        int o = primaryDirection.indexOf(',');
        if (o > 0)
            primaryDirection = primaryDirection.substring(0, o);
        String antiDirection = "Anti-"+direction;
        switch (primaryDirection.toLowerCase())
        {
            case "north":
                direction = "North,n";
                antiDirection = "South,s";
                break;
            case "south":
                direction = "South,s";
                antiDirection = "North,n";
                break;
            case "east":
                direction = "East,e";
                antiDirection = "West,w";
                break;
            case "west":
                direction = "West,w";
                antiDirection = "East,e";
                break;
            case "up":
                direction = "Up,u";
                antiDirection = "Down,d";
                break;
            case "down":
                direction = "Down,d";
                antiDirection = "Up,u";
                break;
            case "north-east":
            case "northeast":
            case "north east":
                direction = "Northeast,North-east,north east,ne";
                antiDirection = "Southwest,South-west,south west,sw";
                break;
            case "south-west":
            case "southwest":
            case "south west":
                direction = "Southwest,South-west,south west,sw";
                antiDirection = "Northeast,North-east,north east,ne";
                break;
            case "south-east":
            case "southeast":
            case "south east":
                direction = "Southeast,South-east,south east,se";
                antiDirection = "Northwest,North-west,north west,nw";
                break;
            case "north-west":
            case "northwest":
            case "north west":
                direction = "Northwest,north-west,north west,nw";
                antiDirection = "Southeast,South-east,south east,se";
                break;
        }
        LociRoom oldRoom = (LociRoom)DataStoreLogic.load(player.getContainedBy());
        if (!oldRoom.isAccessible(player))
        {
            player.addMessage("You cannot dig from here.");
            return;
        }
        List<LociExit> exits = oldRoom.getContainsStuff(LociExit.class);
        for (LociExit exit : exits)
            if (exit.getPrimaryName().equalsIgnoreCase(primaryDirection))
            {
                player.addMessage("There is already a passage leading in direction "+direction+".");
                return;
            }
        LociExit toNew = new LociExit(DiskStore.PREFIX+"exits/"+System.currentTimeMillis());
        toNew.setName(direction);
        toNew.setDescription("A rough hewn passage.");
        toNew.setOwner(player.getURI());
        toNew.setPublic(true);
        LociRoom newRoom = new LociRoom(DiskStore.PREFIX+"rooms/"+System.currentTimeMillis());
        newRoom.setName("New Room");
        newRoom.setDescription("A bright, shiny, new place.");
        newRoom.setOwner(player.getURI());
        LociExit fromNew = new LociExit(DiskStore.PREFIX+"exits/"+System.currentTimeMillis());
        fromNew.setName(antiDirection);
        fromNew.setDescription("A rough hewn passage.");
        fromNew.setOwner(player.getURI());
        fromNew.setPublic(true);
        // link everything up
        toNew.setDestination(newRoom.getURI());
        fromNew.setDestination(oldRoom.getURI());
        ContainmentLogic.add(oldRoom, toNew);
        ContainmentLogic.add(newRoom, fromNew);
        player.addMessage("You dig a passage to the "+direction);
    }
}
