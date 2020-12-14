package jo.audio.loci.thieves.logic.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.stores.DiskCache;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociSquare;
import jo.audio.loci.thieves.data.npc.LociObserver;
import jo.audio.loci.thieves.stores.SquareStore;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.logic.DiceLogic;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.audio.thieves.logic.gen.HouseLogic;
import jo.audio.thieves.slu.ThievesModelConst;
import jo.util.utils.obj.LongUtils;

public class HomeLogic
{
    private static DiskCache mDisk         = new DiskCache(DiskStore.PREFIX,
            "houses");

    private static final int SHIFT_NIGHT   = 0;
    private static final int SHIFT_MORNING = 1;
    private static final int SHIFT_WORK    = 2;
    private static final int SHIFT_EVENING = 3;

    private static void getHouseData(HouseData h)
    {
        String uri = DiskStore.PREFIX + h.mHouse.getStreet() + "/"
                + h.mHouse.getHouseNumber();
        h.mData = mDisk.loadJSON(uri);
        if (h.mData == null)
        {
            h.mData = new JSONObject();
            h.mData.put("uri", uri);
        }
    }

    private static void setHouseData(HouseData h)
    {
        mDisk.saveJSON(h.mData);
    }

    public static void prepareHouse(Street street, int houseNumber)
    {
        HouseData h = new HouseData();
        h.mHouse = HouseLogic.getHouse(street, houseNumber);
        getHouseData(h);
        long lastUpdate = LongUtils.parseLong(h.mData.get("lastUpdate"));
        h.mNow = ThievesConstLogic.gameTime();
        int lastShift = getShift(lastUpdate);
        h.mShift = getShift(h.mNow);
        if ((h.mNow - lastUpdate > ThievesConstLogic.ONE_DAY)
                || (lastShift != h.mShift))
        {
            boolean updated = updateShift(h);
            if (updated)
            {
                h.mData.put("lastUpdate", h.mNow);
                setHouseData(h);
            }
        }
    }

    private static boolean updateShift(HouseData h)
    {
        boolean players = h.scanHouse();
        if (players)
            return false;
        // set up observers
        h.cleanOutObservers();
        h.calcPopulation();
        h.addAnimals();
        // TODO:
        return true;
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
    
    private static final Set<String> YARDS = new HashSet<>();
    static
    {
        YARDS.add("BACK_YARD");
        YARDS.add("COURTYARD");
        YARDS.add("BACK_YARD");
        YARDS.add("GARDEN");
        YARDS.add("SIDE_YARD");
        YARDS.add("WALKWAY");
    }
    
    private static final Set<String> BEDROOMS = new HashSet<>();
    static
    {
        BEDROOMS.add("BED_ROOM");
    }
    
    private static final Set<String> WORKSHOPS = new HashSet<>();
    static
    {
        WORKSHOPS.add("BREESWAY");
        WORKSHOPS.add("KITCHEN");
        WORKSHOPS.add("OFFICE");
        WORKSHOPS.add("PARLOR");
        WORKSHOPS.add("STABLE");
        WORKSHOPS.add("STUDY");
        WORKSHOPS.add("WORKSHOP");
    }
    
    private static final Set<String> LIVING = new HashSet<>();
    static
    {
        LIVING.add("BALCONY");
        LIVING.add("BATHROOM");
        LIVING.add("DINING_ROOM");
        LIVING.add("LIVING_ROOM");
    }
    
    public static boolean isYard(PLocationRef sq)
    {
        return YARDS.contains(sq.getID());
    }
    
    public static boolean isBedroom(PLocationRef sq)
    {
        return BEDROOMS.contains(sq.getID());
    }
    
    public static boolean isWorkshop(PLocationRef sq)
    {
        return WORKSHOPS.contains(sq.getID());
    }
    
    public static boolean isLiving(PLocationRef sq)
    {
        return LIVING.contains(sq.getID());
    }
}

class HouseData
{
    House                   mHouse;
    JSONObject              mData;
    long                    mNow;
    int                     mShift;
    Map<String, LociSquare> mScene = new HashMap<>();
    Map<String, String>     mObservers = new HashMap<>();
    List<String>            mBeds = new ArrayList<>();
    List<String>            mYards = new ArrayList<>();
    List<String>            mWorkAreas = new ArrayList<>();
    List<String>            mLivingAreas = new ArrayList<>();
    List<String>            mBedRooms = new ArrayList<>();
    int                     mPopulation;
    int                     mAdults;
    int                     mChildren;
    int                     mAnimals;
    int                     mGuards;
    
    
    public void calcPopulation()
    {        
        mPopulation = mBeds.size();
        if (mHouse.getPosh() < .25)
            mPopulation *= 2;
        else if (mHouse.getPosh() < .75)
            mPopulation += mPopulation/2;
        mChildren = mPopulation/2;
        mAdults = mPopulation - mChildren;
        mAnimals = mChildren;
        if (mHouse.getPosh() > .75)
        {
            mGuards = mAnimals/2;
        }
        else
            mGuards = 0;
    }

    public boolean scanHouse()
    {
        SquareStore store = SquareStore.getInstance();
        for (String key : mHouse.getTemplate().getLocations().keySet())
        {
            PLocationRef ref = mHouse.getLocation(key);
            if (!ref.isSquare())
                continue;
            String uri = SquareStore.makeURI(mHouse, key);
            LociSquare square = (LociSquare)store.load(uri);
            mScene.put(key, square);
            for (String u : square.getContains())
                if (u.indexOf("/player/") > 0)
                {
                    LociPlayer player = (LociPlayer)DataStoreLogic.load(u);
                    if (player.getOnline())
                        return true; // someone is here, don't mess with it
                }
                else if (u.indexOf("/bed/") > 0)
                    mBeds.add(u);
                else if (u.indexOf("/observer/") > 0)
                    mObservers.put(key, u);
            if (HomeLogic.isYard(ref))
                mYards.add(key);
            if (HomeLogic.isBedroom(ref))
                mBedRooms.add(key);
            if (HomeLogic.isLiving(ref))
                mLivingAreas.add(key);
            if (HomeLogic.isWorkshop(ref))
                mWorkAreas.add(key);
        }
        return false;
    }
    
    public void cleanOutObservers()
    {
        for (String key : mObservers.keySet())
        {
            String squareURI = SquareStore.makeURI(mHouse, key);
            String observerURI = mObservers.get(key);
            LociSquare square = (LociSquare)DataStoreLogic.load(squareURI);
            LociObserver observer = (LociObserver)DataStoreLogic.load(observerURI);
            ContainmentLogic.remove(square, observer);
            DataStoreLogic.delete(observer);
        }
        mObservers.clear();
    }
    
    public void addAnimals()
    {
        for (int i = 0; i < mAnimals; i++)
        {
            String key = DiceLogic.oneOf(mYards);
            String squareURI = SquareStore.makeURI(mHouse, key);
            LociSquare square = (LociSquare)DataStoreLogic.load(squareURI);
            String observerURI = DiskStore.PREFIX+"observer/animal/"+System.currentTimeMillis();
            LociObserver observer = new LociObserver(observerURI);
            int type = DiceLogic.d(1000, 1);
            observer.setName("{{DOG_NAMES#"+type+"}}|dog");
            observer.setDescription("{{DOG_DESCRIPTIONS#"+type+"}}");
            observer.setType(LociObserver.TYPE_ANIMAL);
            observer.setAlertness(DiceLogic.d(4, 1) - 1);
            ContainmentLogic.add(square, observer);
            System.out.println(ThievesModelConst.expand(observer.getName())+" added to "+key);
        }
    }
}
