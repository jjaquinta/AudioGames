package jo.audio.loci.thieves.verbs.move;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.loci.thieves.data.LociLocality;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.thieves.logic.ThievesConstLogic;

public class VerbGoDir extends Verb
{
    private int mCardinality;
    
    public VerbGoDir(String name, int cardinality)
    {
        super(name, null, null, null);
        mCardinality = cardinality;
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociLocality oldRoom = player.getContainedByObject();
        LociExit exit = findExit(oldRoom);
        if (exit == null)
        {
            player.addMessage("You cannot go in that direction.");
            return;
        }
        LociLocality newRoom = exit.getDestinationObject();
        VerbGoImplicit.transition(player, exit, oldRoom, newRoom);
    }
    
    private LociExit findExit(LociLocality room)
    {
        LociExit[] exits = room.getContainsStuff(LociExit.class).toArray(new LociExit[0]);
        if (mCardinality == ThievesConstLogic.UP)
        {
            LociExit best = null;
            for (LociExit e : exits)
                if ((best == null) || (e.getElevation() > best.getElevation()))
                    best = e;
            return best;
        }
        if (mCardinality == ThievesConstLogic.DOWN)
        {
            LociExit best = null;
            for (LociExit e : exits)
                if ((best == null) || (e.getElevation() < best.getElevation()))
                    best = e;
            return best;
        }
        // look for exact match
        for (LociExit e : exits)
            if (e.getDirection() == mCardinality)
                return e;
        return null;
    }
}
