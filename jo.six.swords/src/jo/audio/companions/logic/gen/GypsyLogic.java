package jo.audio.companions.logic.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.common.data.SLDataBean;
import jo.audio.common.logic.CommonIOLogic;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.DomainBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.audio.companions.logic.ItemLogic;
import jo.audio.companions.logic.feature.town.ShopLogic;
import jo.audio.companions.tools.gui.edit.data.PFeatureBean;
import jo.audio.companions.tools.gui.edit.data.PRoomBean;
import jo.audio.compedit.data.CompEditModuleBean;
import jo.audio.compedit.logic.CompEditIOLogic;
import jo.audio.util.BaseUserState;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.LongUtils;

public class GypsyLogic
{
    public static final long UPDATE_FREQUENCY = 15*60*1000L;
    private static final String GYPSY_URI = "gypsy_caravans";
    private static final String GYPSY_DATA_URI = "sixswords://gypsy_caravans";
    private static final String GYPSY_LAST_RUN = "lastRun";
    
    public static Thread startBackgroundDaemon()
    {
        DebugUtils.trace("Starting Gypsy Thread");
        if (!setup())
        {
            DebugUtils.trace("Gypsy setup fail. Aborting thread");
            return null;
        }
        Thread t = new Thread("Gypsy Update") { public void run() { 
            doGypsyDaemon();
        } };
        t.setDaemon(true);
        t.start();
        return t;
    }
    
    private static boolean setup()
    {
        // ensure data record
        SLDataBean data = CommonIOLogic.getDataFromURI(GYPSY_DATA_URI);
        if (data == null)
        {
            data = new SLDataBean();
            data.setKey(GYPSY_DATA_URI);
            data.getSecondaryValues().put(GYPSY_LAST_RUN, String.valueOf(System.currentTimeMillis()));
            CommonIOLogic.saveData(data);
        }
        // ensure module
        CompEditModuleBean mod = CompEditIOLogic.getModuleFromURI(GYPSY_URI);
        if (mod == null)
            return false;   // TODO: create module
        return true;
    }
    
    private static void doGypsyDaemon()
    {
        for (;;)
        {
            try
            {
                GypsyLogic.doGypsySleep();
                GypsyLogic.doUpdateGypsies();
            }
            catch (Throwable t)
            {
                DebugUtils.trace("Error processing gypsies", t);
            }
        }
    }
    
    private static void doGypsySleep()
    {
        long lastRun = LongUtils.parseLong(CommonIOLogic.getDataSecondaryValue(GYPSY_DATA_URI, GYPSY_LAST_RUN));
        long now = System.currentTimeMillis();
        long timeLeft = now - lastRun;
        if (timeLeft > UPDATE_FREQUENCY)
            return;
        try
        {
            long timeToSleep = UPDATE_FREQUENCY - timeLeft;
            DebugUtils.trace("Gypsy lastRun="+lastRun+", now="+now+", time left "+timeLeft+", Sleeping for "+timeToSleep);
            Thread.sleep(timeToSleep);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    private static void doUpdateGypsies()
    {
        DebugUtils.trace("Updating Gypsy Logic!");
        DomainBean domain = GenerationLogic.getDomain(new CoordBean());
        Set<RegionBean> doneRegions = new HashSet<>();
        List<PFeatureBean> toRemove = new ArrayList<>();
        CompEditModuleBean mod = CompEditIOLogic.getModuleFromURI(GYPSY_URI);
        for (PFeatureBean caravan : mod.getFeatures())
        {
            CoordBean l = new CoordBean(caravan.getLocation());
            if ((l.getX() < domain.getOrds().getX()) || (l.getY() < domain.getOrds().getY()) 
                    || (l.getX() >= domain.getOrds().getX() + CompConstLogic.SQUARES_PER_DOMAIN)
                    || (l.getY() >= domain.getOrds().getY() + CompConstLogic.SQUARES_PER_DOMAIN))
            {
                DebugUtils.trace("Caravan "+l+" wandered out of domain :"+domain.getOrds());
                toRemove.add(caravan);
                continue;
            }
            RegionBean r = GenerationLogic.getRegion(l);
            if (doneRegions.contains(r))
            {
                DebugUtils.trace("Caravan "+l+" in region :"+r.getOrds()+" that already has caravan");
                toRemove.add(caravan);
                continue;
            }
            if (!updateCaravan(r, l, caravan))
            {
                DebugUtils.trace("Caravan "+l+" bad update, removing");
                toRemove.add(caravan);
                continue;
            }
            doneRegions.add(r);
        }
        for (int dx = 0; dx < CompConstLogic.REGIONS_PER_DOMAIN; dx++)
            for (int dy = 0; dy < CompConstLogic.REGIONS_PER_DOMAIN; dy++)
            {
                CoordBean o = new CoordBean(domain.getOrds().getX() + dx*CompConstLogic.SQUARES_PER_REGION, 
                        domain.getOrds().getY() + dy*CompConstLogic.SQUARES_PER_REGION, 
                        domain.getOrds().getZ());
                RegionBean r = GenerationLogic.getRegion(o);
                if (doneRegions.contains(r))
                    continue;
                if (r.getGovernmentalStructure() == CompConstLogic.GOVERNMENT_ANARCHY)
                    continue;
                PFeatureBean caravan = new PFeatureBean();
                caravan.fromJSON(mod, mod.getFeatures().get(0).toJSON(mod));
                caravan.setLocation(o.toString());
                DebugUtils.trace("Adding Caravan to region"+r.getOrds());
                if (updateCaravan(r, o, caravan))
                {
                    DebugUtils.trace("Successuful update to Caravan "+caravan.getLocation()+" to region"+r.getOrds());
                    mod.getFeatures().add(caravan);
                }
            }
        for (PFeatureBean c : toRemove)
            mod.getFeatures().remove(c);
        CompEditIOLogic.saveModule(mod);
        long now = System.currentTimeMillis();
        CommonIOLogic.setDataSecondaryValue(GYPSY_DATA_URI, GYPSY_LAST_RUN, String.valueOf(now));
        try
        {
            Thread.sleep(15000L); // latency to let data get written
        }
        catch (InterruptedException e)
        {
        } 
        DebugUtils.trace("Done Gypsy Thread at "+now);
    }
    
    private static boolean updateCaravan(RegionBean r, CoordBean l, PFeatureBean caravan)
    {
        boolean valid = updateLocation(r, l, caravan);
        if (valid)
            updateRooms(caravan);
        return valid;
    }
    
    private static void updateRooms(PFeatureBean caravan)
    {
        PRoomBean shop = null;
        for (PRoomBean r : caravan.getRooms())
        {
            //DebugUtils.trace("Found room id="+r.getID()+", type="+r.getType());
            if (r.getType().equals("shop"))
                shop = r;
        }
        updateShop(shop);
    }
    
    @SuppressWarnings("unchecked")
    private static void updateShop(PRoomBean shop)
    {
        //DebugUtils.trace("Updating shop");
        List<CompItemTypeBean> types = ItemLogic.getAllItemTypes(9);
        JSONObject params = shop.getParams();
        if (params == null)
        {
            params = new JSONObject();
            shop.setParams(params);
        }
        params.put("itemType", -1);
        JSONArray items = new JSONArray();
        params.put("items", items);
        int numItems = Math.min(9, types.size());
        Map<String,CompItemTypeBean> itemIndex = new HashMap<>();
        for (int i = 0; i < types.size(); i++)
            if (types.get(i).isMustHave())
                ShopLogic.addItem(types, itemIndex, i);
        while ((itemIndex.size() < numItems) && (types.size() > 0))
        {
            int idx = BaseUserState.RND.nextInt(types.size());
            ShopLogic.addItem(types, itemIndex, idx);
        }
        for(CompItemTypeBean type : itemIndex.values())
            items.add(type.getID());

    }
    
    private static boolean updateLocation(RegionBean r, CoordBean l, PFeatureBean caravan)
    {
        //DebugUtils.trace("Updating location");
        SquareBean sq = GenerationLogic.getSquare(l);
        if (!sq.isAnyRoads())
        {   //pick random location
            for (;;)
            {
                int x = r.getOrds().getX() + BaseUserState.RND.nextInt(CompConstLogic.SQUARES_PER_REGION);
                int y = r.getOrds().getY() + BaseUserState.RND.nextInt(CompConstLogic.SQUARES_PER_REGION);
                int z = r.getOrds().getZ();
                CoordBean newLoc = new CoordBean(x, y, z);
                SquareBean newSq = GenerationLogic.getSquare(newLoc);
                if (isSuitable(newSq))
                {
                    caravan.setLocation(newLoc.toString());
                    //DebugUtils.trace("moved to "+newLoc);
                    return true;
                }
            }
        }
        else
        {   // move
            List<CoordBean> options = new ArrayList<>();
            if (sq.isRoadNorth())
            {
                CoordBean n = l.north();
                if (isSuitable(n))
                    options.add(n);
                else
                {
                    n = n.north();
                    if (isSuitable(n))
                        options.add(n);
                }
            }
            if (sq.isRoadSouth())
            {
                CoordBean n = l.south();
                if (isSuitable(n))
                    options.add(n);
                else
                {
                    n = n.south();
                    if (isSuitable(n))
                        options.add(n);
                }
            }
            if (sq.isRoadEast())
            {
                CoordBean n = l.east();
                if (isSuitable(n))
                    options.add(n);
                else
                {
                    n = n.east();
                    if (isSuitable(n))
                        options.add(n);
                }
            }
            if (sq.isRoadWest())
            {
                CoordBean n = l.west();
                if (isSuitable(n))
                    options.add(n);
                else
                {
                    n = n.west();
                    if (isSuitable(n))
                        options.add(n);
                }
            }
            if (options.size() == 0)
                return false;
            CoordBean o = options.get(BaseUserState.RND.nextInt(options.size()));
            caravan.setLocation(o.toString());
            //DebugUtils.trace("moved to "+o);
            return true;
        }
    }
    
    private static boolean isSuitable(CoordBean c)
    {
        SquareBean sq = GenerationLogic.getSquare(c);
        return isSuitable(sq);
    }
    
    private static boolean isSuitable(SquareBean sq)
    {
        if (!sq.isAnyRoads())
            return false;
        if (sq.getFeature() != CompConstLogic.FEATURE_NONE)
            return false;
        return true;
    }
}
