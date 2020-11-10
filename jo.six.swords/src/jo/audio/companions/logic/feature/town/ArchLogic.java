package jo.audio.companions.logic.feature.town;

import java.util.List;
import java.util.Random;

import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.feature.DigOptions;

public class ArchLogic
{
    private static final String TO_ARCH_LOCK = "{\"expr\":\"$premium == false\",\"trueExpr\":{\"expr\":\"$canPremium == false\",\"trueMessage\":{\"message\":\"PREMIUM_BLOCK\"}, \"falseMessage\":{\"message\":\"PREMIUM_OPTIN\"}}}";

    static void addArch(FeatureBean feature, Random rnd,
            List<DigOptions> sites)
    {
        DigOptions site = sites.get(rnd.nextInt(sites.size()));
        sites.remove(site);
    
        CompRoomBean entry = FeatureLogic.getRoom("triPremiumEntry");
        if (entry == null)
            throw new IllegalStateException("Can't find room 'triPremiumEntry'");
        entry.setID(entry.getID()+feature.getRooms().size());
        site.from.setDirection(site.dir, entry.getID());
        entry.setDirection(TownLogic.opposite(site.dir), site.from.getID());
        feature.getRooms().add(entry);
        
        TownLogic.extendRoom(feature, entry, "triPremiumArch", site.dir);
        entry.setDirectionLock(site.dir, JSONUtils.readJSONString(TO_ARCH_LOCK));

    }

}
