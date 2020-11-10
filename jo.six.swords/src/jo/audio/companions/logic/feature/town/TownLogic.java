package jo.audio.companions.logic.feature.town;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.feature.DigOptions;
import jo.audio.companions.logic.gen.FeaturesLogic;

public class TownLogic
{
    public static void generateCity(RegionBean region, SquareBean sq, FeatureBean feature, int type, Random rnd, List<String> expansions, boolean athiest)
    {
        // status goes from 0 (Hamlet in an anarchy) to 8 (City in Empire) 
        int status = region.getGovernmentalStructure();
        status += (type - CompConstLogic.FEATURE_HAMLET);
        List<Integer> shops = ShopLogic.determineShops(feature, type, rnd);
        List<Integer> halls = HallLogic.determineHalls(region, type, rnd);
        List<Integer> temples = (athiest ? new ArrayList<>() : TempleLogic.determineTemples(region, sq, type, rnd));
        int numPubs = rnd.nextInt(type - CompConstLogic.FEATURE_HAMLET + 1) + 1;
        boolean postOffice = false; //rnd.nextInt(type - CompConstLogic.FEATURE_HAMLET + 1) != 0;
        boolean bardCollege = (type == CompConstLogic.FEATURE_TOWN) || (type == CompConstLogic.FEATURE_CITY);
        boolean vacantLot = ((type == CompConstLogic.FEATURE_VILLAGE) || (type == CompConstLogic.FEATURE_TOWN) || (type == CompConstLogic.FEATURE_CITY))
                && CompConstLogic.isPremium(sq.getOrds());

        int numSites = shops.size() + halls.size() + temples.size() + numPubs + (postOffice ? 1 : 0) 
                + (bardCollege ? 1 : 0)
                + (vacantLot ? 1 : 0);
        if (type == CompConstLogic.FEATURE_CITY)
            numSites++; // wizard shop
        if ((sq.getOrds().getZ() == 0) && ((type == CompConstLogic.FEATURE_CITY) || (type == CompConstLogic.FEATURE_TOWN)))
            numSites++; // tri-form arch
        List<DigOptions> sites = LayoutLogic.buildTown(sq, feature, numSites, rnd, expansions);
        ShopLogic.addShops(feature, rnd, shops, sites, status);
        HallLogic.addHalls(feature, rnd, halls, sites);
        TempleLogic.addTemples(sq, feature, rnd, temples, sites);
        PubLogic.addPubs(sq, feature, rnd, numPubs, sites);
        if (postOffice)
            PostOfficeLogic.addPostOffice(feature, rnd, sites);
        if (bardCollege)
            BardLogic.addBardCollege(feature, rnd, sites);
        if (vacantLot)
            HouseLogic.addVacantLot(feature, rnd, sites);
        if (type == CompConstLogic.FEATURE_CITY)
            WizardLogic.addWizardShop(feature, rnd, sites);
        if ((sq.getOrds().getZ() == 0) && ((type == CompConstLogic.FEATURE_CITY) || (type == CompConstLogic.FEATURE_TOWN)))
            ArchLogic.addArch(feature, rnd, sites);
    }

    static CompRoomBean extendRoom(FeatureBean feature, CompRoomBean oldRoom, String newRoomID,
            int dir)
    {
        return extendRoom(feature, oldRoom, newRoomID, dir, null);
    }
    static CompRoomBean extendRoom(FeatureBean feature, CompRoomBean oldRoom, String newRoomID,
            int dir, List<String> expansions)
    {
        CompRoomBean newRoom = null;
        for (StringTokenizer st = new StringTokenizer(newRoomID, ","); st.hasMoreTokens(); )
        {
            newRoom = FeatureLogic.getRoom(st.nextToken());
            if (newRoom != null)
                break;
        }
        if (newRoom == null)
        {
            throw new IllegalStateException("Can't find room '"+newRoomID+"'");
        }
        newRoom.setID(newRoom.getID()+feature.getRooms().size());
        oldRoom.setDirection(dir, newRoom.getID());
        newRoom.setDirection(opposite(dir), oldRoom.getID());
        feature.getRooms().add(newRoom);
        if ((newRoom.getParams() != null) && (dir != 0))
        {
            JSONObject[] locks = new JSONObject[4];
            for (int d = 0; d < 4; d++)
            {
                locks[d] = newRoom.getDirectionLock(d);
                newRoom.setDirectionLock(d, null);
            }
            if (locks[CompRoomBean.DIR_NORTH] != null)
                newRoom.setDirectionLock(dir, locks[CompRoomBean.DIR_NORTH]);
            if (locks[CompRoomBean.DIR_SOUTH] != null)
                newRoom.setDirectionLock(opposite(dir), locks[CompRoomBean.DIR_SOUTH]);
            if (locks[CompRoomBean.DIR_EAST] != null)
                newRoom.setDirectionLock(right(dir), locks[CompRoomBean.DIR_EAST]);
            if (locks[CompRoomBean.DIR_WEST] != null)
                newRoom.setDirectionLock(left(dir), locks[CompRoomBean.DIR_WEST]);
        }
        FeaturesLogic.insertExpansions(newRoom, expansions);
        return newRoom;
    }

    public static int opposite(int dir)
    {
        switch (dir)
        {
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
                return 3;
            case 3:
                return 2;
        }
        throw new IllegalArgumentException();
    }

    public static int left(int dir)
    {
        switch (dir)
        {
            case 0:
                return 3;
            case 1:
                return 2;
            case 2:
                return 0;
            case 3:
                return 1;
        }
        throw new IllegalArgumentException();
    }

    public static int right(int dir)
    {
        return opposite(left(dir));
    }
}