package jo.audio.companions.logic.feature.town;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.ItemLogic;
import jo.audio.companions.logic.feature.DigOptions;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;

public class ShopLogic
{
    public static void addShops(FeatureBean feature, Random rnd,
            List<Integer> shops, List<DigOptions> sites, int status)
    {
        while (shops.size() > 0)
        {
            int shopType = shops.get(0);
            shops.remove(0);
            DigOptions site = sites.get(rnd.nextInt(sites.size()));
            sites.remove(site);
            addShop(feature, rnd, shops.size(), site, status, shopType);
        }
    }

    public static void addShop(FeatureBean feature, Random rnd,
            int suffix, DigOptions site, int status,
            int shopType)
    {
        Collection<CompItemTypeBean> itemIndex = getItems(rnd, status,
                shopType);
        // create rooms
        CompRoomBean shop = FeatureLogic.getRoom("shop"+CompItemTypeBean.TYPE_NAMES[shopType]);
        shop.setID(shop.getID()+suffix);
        CompRoomBean window = FeatureLogic.getRoom("shop_window");
        window.setID(window.getID()+suffix);
        CompRoomBean display = FeatureLogic.getRoom("shop_display");
        display.setID(display.getID()+suffix);
        CompRoomBean back = FeatureLogic.getRoom("shop_back");
        back.setID(back.getID()+suffix);
        // connect rooms
        int fore = site.dir;
        int left = TownLogic.left(fore);
        int right = TownLogic.right(fore);
        int aft = TownLogic.opposite(fore);
        site.from.setDirection(fore, shop.getID());
        shop.setDirection(aft, site.from.getID());
        shop.setDirection(fore, back.getID());
        back.setDirection(aft, shop.getID());
        shop.setDirection(left, window.getID());
        window.setDirection(right, shop.getID());
        shop.setDirection(right, display.getID());
        display.setDirection(left, shop.getID());
        // add items
        Iterator<CompItemTypeBean> i = itemIndex.iterator();
        window.setParams(makeItemParams(shopType, i, left));
        display.setParams(makeItemParams(shopType, i, right));
        back.setParams(makeItemParams(shopType, i, fore));
        // register rooms
        feature.getRooms().add(shop);
        feature.getRooms().add(window);
        feature.getRooms().add(display);
        feature.getRooms().add(back);
    }

    private static JSONObject makeItemParams(int shopType, Iterator<CompItemTypeBean> i, int entryDir)
    {
        JSONObject params = new JSONObject();
        if (!i.hasNext())
            return params;
        params.put("itemType", shopType);

        List<CompItemTypeBean> items = new ArrayList<>();
        items.add(i.next());
        if (i.hasNext())
            items.add(i.next());
        if (i.hasNext())
            items.add(i.next());
        for (int j = 0; j < items.size(); j++)
        {
            CompItemTypeBean item = items.get(j);
            int dir = entryDir;
            if (j == 1)
                dir = TownLogic.left(dir);
            else if (j == 2)
                dir = TownLogic.right(dir);
            JSONObject lock = new JSONObject();
            lock.put("expr", "@function(buy,\'"+item.getID()+"\')");
            AudioMessageBean name = new AudioMessageBean(CompanionsModelConst.TEXT_XXX_FOR_YYY_GOLD,
                    item.getName(), (int)item.getCost());          
            JSONObject desc = name.toJSON();
            params.put(CompRoomBean.DIR_LOCK[dir], lock);
            params.put(CompRoomBean.DIR_DESC[dir], desc);
            params.put(CompRoomBean.DIR_ITEM[dir], item.getID());
        }
        return params;
    }
    
    private static List<CompItemTypeBean> getItems(Random rnd, int status,
            int shopType)
    {
        List<CompItemTypeBean> types = ItemLogic.getItemTypes(shopType, status);
        int numItems = Math.min(9, types.size());
        Map<String,CompItemTypeBean> itemIndex = new HashMap<>();
        for (int i = 0; i < types.size(); i++)
            if (types.get(i).isMustHave())
                addItem(types, itemIndex, i);
        while ((itemIndex.size() < numItems) && (types.size() > 0))
        {
            int idx = rnd.nextInt(types.size());
            addItem(types, itemIndex, idx);
        }
        List<CompItemTypeBean> items = new ArrayList<>();
        items.addAll(itemIndex.values());
        Collections.sort(items, new Comparator<CompItemTypeBean>() {
            @Override
            public int compare(CompItemTypeBean o1, CompItemTypeBean o2)
            {
                return (int)Math.signum(o1.getCost() - o2.getCost());
            }
        });
        return items;
    }

    public static void addItem(List<CompItemTypeBean> types,
            Map<String, CompItemTypeBean> itemIndex, int idx)
    {
        CompItemTypeBean type = types.get(idx);
        types.remove(idx);
        if (type.getCost() < 0)
            return; // not for sale
        if (itemIndex.containsKey(type.getBaseID()))
        {
            CompItemTypeBean alt = itemIndex.get(type.getBaseID());
            if (type.getMagic() > alt.getMagic())
                itemIndex.put(type.getBaseID(), type);
        }
        else
            itemIndex.put(type.getBaseID(), type);
    }

    static List<Integer> determineShops(FeatureBean feature, int type,
            Random rnd)
    {
        List<Integer> shops = new ArrayList<>();
        int specificShops = 0;
        int rndShops = 0;
        int validTypes = 0;
        for (boolean b : CompItemTypeBean.GENERATE)
            if (b)
                validTypes++;
        switch (type)
        {
            case CompConstLogic.FEATURE_HAMLET:
                feature.getName().setIdent("{{HAMLET_NAME#"+rnd.nextInt(9973)+"}}");
                specificShops = validTypes/4;
                rndShops = rnd.nextInt(validTypes/4);
                break;
            case CompConstLogic.FEATURE_VILLAGE:
                feature.getName().setIdent("{{VILLAGE_NAME#"+rnd.nextInt(9973)+"}}");
                specificShops = validTypes/2;
                rndShops = rnd.nextInt(validTypes/2);
                break;
            case CompConstLogic.FEATURE_TOWN:
                feature.getName().setIdent("{{TOWN_NAME#"+rnd.nextInt(9973)+"}}");
                specificShops = validTypes*3/4;
                rndShops = rnd.nextInt(validTypes*3/4);
                break;
            case CompConstLogic.FEATURE_CITY:
                feature.getName().setIdent("{{CITY_NAME#"+rnd.nextInt(9973)+"}}");
                specificShops = validTypes;
                rndShops = rnd.nextInt(validTypes);
                break;
        }
        for (int t : CompItemTypeBean.TYPES)
            if (CompItemTypeBean.GENERATE[t])
                shops.add(t);
        while (shops.size() > specificShops)
            shops.remove(rnd.nextInt(shops.size()));
        while (shops.size() < specificShops + rndShops)
        {
            int shop = rnd.nextInt(CompItemTypeBean.TYPES.length);
            if (!CompItemTypeBean.GENERATE[shop])
                continue;
            shops.add(shop);
        }
        if (type == CompConstLogic.FEATURE_CITY)
        {
            shops.add(CompItemTypeBean.TYPE_CASTLE);
            shops.add(CompItemTypeBean.TYPE_PERMIT);
        }
        return shops;
    }
}