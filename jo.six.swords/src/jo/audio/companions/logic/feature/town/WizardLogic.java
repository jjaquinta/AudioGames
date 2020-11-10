package jo.audio.companions.logic.feature.town;

import java.util.List;
import java.util.Random;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.feature.DigOptions;

public class WizardLogic
{

    static void addWizardShop(FeatureBean feature, Random rnd,
            List<DigOptions> sites)
    {
        DigOptions site = sites.get(rnd.nextInt(sites.size()));
        sites.remove(site);
    
        CompRoomBean entry = FeatureLogic.getRoom("wizardEntry");
        if (entry == null)
            throw new IllegalStateException("Can't find room 'wizardEntry'");
        entry.setID(entry.getID()+feature.getRooms().size());
        site.from.setDirection(site.dir, entry.getID());
        entry.setDirection(TownLogic.opposite(site.dir), site.from.getID());
        feature.getRooms().add(entry);
        
        CompRoomBean hall1 = TownLogic.extendRoom(feature, entry, "wizardHall1", site.dir);
        CompRoomBean hall2 = TownLogic.extendRoom(feature, hall1, "wizardHall2", site.dir);
        CompRoomBean hall3 = TownLogic.extendRoom(feature, hall2, "wizardHall3", site.dir);
        CompRoomBean str = TownLogic.extendRoom(feature, hall1, "wizardSTR", TownLogic.left(site.dir));
        CompRoomBean con = TownLogic.extendRoom(feature, hall2, "wizardCON", TownLogic.left(site.dir));
        CompRoomBean dex = TownLogic.extendRoom(feature, hall3, "wizardDEX", TownLogic.left(site.dir));
        CompRoomBean intt = TownLogic.extendRoom(feature, hall1, "wizardINT", TownLogic.right(site.dir));
        CompRoomBean wis = TownLogic.extendRoom(feature, hall2, "wizardWIS", TownLogic.right(site.dir));
        CompRoomBean cha = TownLogic.extendRoom(feature, hall3, "wizardCHA", TownLogic.right(site.dir));
        CompRoomBean dead = TownLogic.extendRoom(feature, hall3, "wizardDead", site.dir);
        TownLogic.extendRoom(feature, str, "wizardSTR2", TownLogic.left(site.dir));
        TownLogic.extendRoom(feature, con, "wizardCON2", TownLogic.left(site.dir));
        TownLogic.extendRoom(feature, dex, "wizardDEX2", TownLogic.left(site.dir));
        TownLogic.extendRoom(feature, intt, "wizardINT2", TownLogic.right(site.dir));
        TownLogic.extendRoom(feature, wis, "wizardWIS2", TownLogic.right(site.dir));
        TownLogic.extendRoom(feature, cha, "wizardCHA2", TownLogic.right(site.dir));
        TownLogic.extendRoom(feature, dead, "wizardDead2", site.dir);
    }

}
