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
import jo.util.utils.obj.StringUtils;

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
        LociRoom oldRoom = (LociRoom)DataStoreLogic.load(player.getContainedBy());
        if (!StringUtils.equals(player.getURI(), oldRoom.getOwner()) && !oldRoom.getPublic())
        {
            player.addMessage("You cannot dig from here.");
            return;
        }
        List<LociExit> exits = oldRoom.getContainsStuff(LociExit.class);
        for (LociExit exit : exits)
            if (exit.getName().equalsIgnoreCase(direction))
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
        if ("north".equalsIgnoreCase(direction))
            fromNew.setName("South");
        else if ("south".equalsIgnoreCase(direction))
            fromNew.setName("North");
        else if ("east".equalsIgnoreCase(direction))
            fromNew.setName("West");
        else if ("west".equalsIgnoreCase(direction))
            fromNew.setName("East");
        else if ("up".equalsIgnoreCase(direction))
            fromNew.setName("Down");
        else if ("down".equalsIgnoreCase(direction))
            fromNew.setName("Up");
        else
            fromNew.setName("Anti-"+direction);
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
