package jo.audio.companions.logic.feature.town;

import java.util.List;
import java.util.Random;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.feature.DigOptions;

public class PostOfficeLogic
{

    static void addPostOffice(FeatureBean feature, Random rnd,
            List<DigOptions> sites)
    {
        DigOptions site = sites.get(rnd.nextInt(sites.size()));
        sites.remove(site);
    
        CompRoomBean po = FeatureLogic.getRoom("postOffice");
        String name = po.getName().getIdent();
        name = name.replace("?0?", String.valueOf(rnd.nextInt(9997)));
        po.getName().setIdent(name);
        site.from.setDirection(site.dir, po.getID());
        po.setDirection(TownLogic.opposite(site.dir), site.from.getID());
        feature.getRooms().add(po);
    }

}
