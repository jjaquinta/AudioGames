package jo.audio.loci.thieves.logic.house;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.stores.DiskCache;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociSquare;
import jo.audio.loci.thieves.stores.SquareStore;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.audio.thieves.logic.gen.HouseLogic;
import jo.util.utils.obj.LongUtils;

public class HomeLogic
{
    private static DiskCache   mDisk = new DiskCache(DiskStore.PREFIX, "houses");
    
    private static final int SHIFT_NIGHT = 0;
    private static final int SHIFT_MORNING = 1;
    private static final int SHIFT_WORK = 2;
    private static final int SHIFT_EVENING = 3;

    private static JSONObject getHouseData(House house)
    {
        String uri = DiskStore.PREFIX + house.getStreet()+"/"+house.getHouseNumber();
        JSONObject data = mDisk.loadJSON(uri);
        if (data == null)
        {
            data = new JSONObject();
            data.put("uri", uri);
        }
        return data;
    }
    
    private static void setHouseData(House house, JSONObject data)
    {
        mDisk.saveJSON(data);
    }
    
    public static void prepareHouse(Street street, int houseNumber)
    {
        House house = HouseLogic.getHouse(street, houseNumber);
        JSONObject data = getHouseData(house);
        long lastUpdate = LongUtils.parseLong(data.get("lastUpdate"));
        long now = ThievesConstLogic.gameTime();
        int lastShift = getShift(lastUpdate);
        int nowShift = getShift(now);
        if ((now - lastUpdate > ThievesConstLogic.ONE_DAY) || (lastShift != nowShift))
        {
            boolean updated = updateShift(house, data, nowShift);
            if (updated)
            {
                data.put("lastUpdate", now);
                setHouseData(house, data);
            }
        }
    }
    
    private static boolean updateShift(House house, JSONObject data, int shift)
    {
        Map<String, LociSquare> scene = new HashMap<>();
        boolean players = scanHouse(house, scene);
        if (players)
            return false;
        // set up observers
        // TODO:
        return true;
    }
    
    private static boolean scanHouse(House house, Map<String, LociSquare> scene)
    {
        SquareStore store = SquareStore.getInstance();
        for (String key : house.getTemplate().getLocations().keySet())
        {
            PLocationRef ref = house.getLocation(key);
            if (!ref.isSquare())
                continue;
            String uri = SquareStore.makeURI(house, key);
            LociSquare square = (LociSquare)store.load(uri);
            scene.put(key, square);
            for (String u : square.getContains())
                if (u.indexOf("/player/") > 0)
                {
                    LociPlayer player = (LociPlayer)DataStoreLogic.load(u);
                    if (player.getOnline())
                        return true; // someone is here, don't mess with it
                }
        }
        return false;
    }
    
    private static int getShift(long time)
    {
        int hour = ThievesConstLogic.gameHour(time);
        switch (hour)
        {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return SHIFT_NIGHT;
            case 6:
            case 7:
            case 8:
                return SHIFT_MORNING;
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                return SHIFT_WORK;
            case 18:
            case 19:
            case 20:
                return SHIFT_EVENING;
            case 21:
            case 22:
            case 23:
                return SHIFT_NIGHT;
        }
        throw new IllegalStateException();
    }
}
