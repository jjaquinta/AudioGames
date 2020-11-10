package jo.audio.companions.logic.feature.town;

import java.util.List;
import java.util.Random;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.feature.DigOptions;

public class PubLogic
{

    static void addPubs(SquareBean sq, FeatureBean feature, Random rnd,
            int numPubs, List<DigOptions> sites)
    {
        while (numPubs-- > 0)
        {
            DigOptions site = sites.get(rnd.nextInt(sites.size()));
            sites.remove(site);
    
            String roomID = "pub";
            for (DemenseBean dem = sq.getDemense(); dem != null; dem = dem.getLiege())
                roomID = "pub_"+dem.getID()+","+roomID;
            CompRoomBean pub = FeatureLogic.makeRoom(roomID, feature, null);
            String name = pub.getName().getIdent();
            name = name.replace("?0?", String.valueOf(rnd.nextInt(9997)));
            pub.getName().setIdent(name);
            site.from.setDirection(site.dir, pub.getID());
            pub.setDirection(TownLogic.opposite(site.dir), site.from.getID());
        }
    }

}
