package jo.audio.loci.thieves.logic;

import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociApature;
import jo.audio.loci.thieves.data.LociSquare;
import jo.audio.loci.thieves.data.npc.LociObserver;
import jo.audio.loci.thieves.stores.ApatureStore;
import jo.audio.loci.thieves.verbs.move.VerbGoImplicit;
import jo.audio.thieves.data.template.PLocationRef;

public class ObserverLogic
{
    
    public static void alertNearby(LociSquare loc, int dir)
    {
        LociApature ap = null;
        LociSquare nearby = null;
        if (dir >= 0)
        {
            PLocationRef apRef = loc.getURIObject().getApatureRef(dir);
            if (apRef == null)
                return;
            if ("EXIT".equals(apRef.getID()))
                return;
            ApatureStore.ApatureURI apURI = ApatureStore.makeURI(loc.getURIObject().mHouse, apRef, dir);
            ap = (LociApature)DataStoreLogic.load(apURI.toURI());
            nearby = ap.getDestinationObject();
            if (nearby == null)
                return; // exit?
        }
        else
            nearby = loc;
        for (String obsURI : nearby.getContains())
            if (obsURI.indexOf("/observer/") > 0)
            {
                LociObserver obs = (LociObserver)DataStoreLogic.load(obsURI);
                int omod = 0;
                if (ap != null)
                    if (ap.getApatureObject().getOpenable() && !ap.getOpen())
                        omod = -15;
                if (obs.rollNotice(omod))
                    observerNotices(obs, nearby, ap, loc);
            }
    }
 
    private static void observerNotices(LociObserver obs, LociSquare obsLocation, LociApature ap, LociSquare noiseLocation)
    {
        int alertness = obs.getAlertness();
        if (alertness < LociObserver.ALERT_ALARMED)
        {
            obs.setAlertness(++alertness);
            if (obsLocation == noiseLocation)
                if (alertness == LociObserver.ALERT_ALARMED)
                    obsererAnnounceStopThief(obs, obsLocation);
                else if (alertness == LociObserver.ALERT_ALERT)
                    obsererAnnounceWhoGoesThere(obs, obsLocation);
            return;
        }
        if (obsLocation == noiseLocation)
        {
            obsererAnnounceStopThief(obs, obsLocation);
            return;
        }
        // try to move
        if (ap.getOpen())
        {
            VerbGoImplicit.move(obs, obsLocation, noiseLocation);
            return;
        }
        if (ap.getLocked())
        {
            if (obs.getType().equals(LociObserver.TYPE_ANIMAL))
                return; // animals can't unlock doors
            if (obs.getType().equals(LociObserver.TYPE_CHILD))
                return; // children can't unlock doors
            ap.setLocked(false);
            obsLocation.say(obs.getPrimaryName()+" unlocks "+ap.getPrimaryName()+".", null, null);
            if (obsLocation != noiseLocation)
                noiseLocation.say(ap.getPrimaryName()+" is unlocked.", null, null);
            return;
        }
        if (obs.getType().equals(LociObserver.TYPE_ANIMAL))
            return; // animals can't open doors
        ap.setOpen(true);
        obsLocation.say(obs.getPrimaryName()+" opens "+ap.getPrimaryName()+".", null, null);
        if (obsLocation != noiseLocation)
            noiseLocation.say(ap.getPrimaryName()+" opens.", null, null);
    }

    private static void obsererAnnounceStopThief(LociObserver obs,
            LociSquare obsLocation)
    {
        if (obs.getType().equals(LociObserver.TYPE_ANIMAL))
            obsLocation.say(obs.getPrimaryName()+" says \"Bark, bark!\".", null, null);
        else
            obsLocation.say(obs.getPrimaryName()+" says \"Stop, thief!\".", null, null);
    }

    private static void obsererAnnounceWhoGoesThere(LociObserver obs,
            LociSquare obsLocation)
    {
        if (obs.getType().equals(LociObserver.TYPE_ANIMAL))
            obsLocation.say(obs.getPrimaryName()+" says \"Bark!\".", null, null);
        else if (obs.getType().equals(LociObserver.TYPE_CHILD))
            obsLocation.say(obs.getPrimaryName()+" says \"What are you doing here?\".", null, null);
        else if (obs.getType().equals(LociObserver.TYPE_RESIDENT))
            obsLocation.say(obs.getPrimaryName()+" says \"Get out of my house!\".", null, null);
        else
            obsLocation.say(obs.getPrimaryName()+" says \"Who goes there?\".", null, null);
    }

}
