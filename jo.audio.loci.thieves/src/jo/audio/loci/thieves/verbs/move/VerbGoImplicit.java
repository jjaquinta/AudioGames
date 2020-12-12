package jo.audio.loci.thieves.verbs.move;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.thieves.data.LociApature;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.loci.thieves.data.LociLocality;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociThing;
import jo.audio.loci.thieves.logic.SkillLogic;
import jo.audio.loci.thieves.logic.TimeLogic;
import jo.audio.loci.thieves.verbs.VerbLookBase;
import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.logic.ThievesConstLogic;

public class VerbGoImplicit extends Verb
{
    public VerbGoImplicit()
    {
        super("this", null, null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        LociThing agent = (LociThing)context.getMatchedVerbHost();
        if (agent instanceof LociExit)
        {
            LociExit exit = (LociExit)agent;
            LociLocality oldRoom = player.getContainedByObject();
            LociLocality newRoom = exit.getDestinationObject();
            VerbGoImplicit.transition(player, agent, oldRoom, newRoom);
        }
        else if (agent instanceof LociApature)
        {
            LociApature exit = (LociApature)agent;
            PApature e = exit.getApatureObject();
            int cardinality = exit.getDirection();
            if ((cardinality == ThievesConstLogic.UP) || (cardinality == ThievesConstLogic.DOWN))
                if (SkillLogic.rollClimbWalls(player, e.getClimbWallsMod()))
                {
                    VerbGoImplicit.transition(player, agent, exit.getSourceObject(), exit.getDestinationObject());
                }
                else
                    player.addMessage("You fail your climb check.");
            else
                VerbGoImplicit.transition(player, agent, exit.getSourceObject(), exit.getDestinationObject());
        }
    }

    public static void transition(LociPlayer player, LociThing exit, LociLocality oldRoom, LociLocality newRoom)
    {
        if (exit != null)
        {
            if (!exit.isAccessible(player))
            {
                player.addMessage("You cannot go that way.");
                return;
            }
            if ((exit instanceof LociApature) && !((LociApature)exit).getOpen())
            {
                player.addMessage("The "+exit.getPrimaryName()+" is not open.");
                return;
            }
        }
        oldRoom.say(player.getPrimaryName()+" leaves.", player.getURI(), null);
        ContainmentLogic.remove(oldRoom, player);
        ContainmentLogic.add(newRoom, player);
        newRoom.say(player.getPrimaryName()+" enters from "+oldRoom.getPrimaryName()+".", 
                player.getURI(), "You go to "+newRoom.getPrimaryName()+".");
        VerbLookBase.doLook(player, newRoom);
        TimeLogic.moveCheck(player);
    }
}
