package jo.audio.loci.thieves.verbs.move;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.thieves.data.LociApature;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.loci.thieves.data.LociLocality;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociSquare;
import jo.audio.loci.thieves.data.LociThing;
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
        LociThing agent = findExit(oldRoom);
        if (agent == null)
        {
            player.addMessage("You cannot go in that direction.");
            return;
        }
        if (agent instanceof LociExit)
        {
            LociExit exit = (LociExit)agent;
            LociLocality newRoom = exit.getDestinationObject();
            VerbGoImplicit.transition(player, agent, oldRoom, newRoom);
        }
        else if (agent instanceof LociApature)
        {
            LociApature exit = (LociApature)agent;
            VerbGoImplicit.transition(player, agent, exit.getSourceObject(), exit.getDestinationObject());
        }
    }
    
    private LociThing findExit(LociThing room)
    {
        if (room instanceof LociSquare)
            return findSquareExit((LociSquare)room);
        else
            return findSurfaceExit(room);
    }
    
    private LociExit findSurfaceExit(LociThing room)
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
    
    private LociThing findSquareExit(LociSquare room)
    {
        LociApature[] exits = room.getContainsStuff(LociApature.class).toArray(new LociApature[0]);
        for (LociApature e : exits)
            if (e.getDirection() == mCardinality)
                return e;
        return null;
    }
}
