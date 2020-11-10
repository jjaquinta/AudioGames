package jo.audio.companions.logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.util.utils.ArrayUtils;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;

public class PurchaseLogic
{

    public static void sell(CompContextBean context, String itemID, int amount)
    {
        CompItemTypeBean type = ItemLogic.getItemType(itemID);
        if (type == null)
            throw new IllegalArgumentException("No such item type '"+itemID+"'");
        CompOperationLogic.fillContext(context);
        CompRoomBean shop = context.getRoom();
        if (shop == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_CAN_ONLY_SELL_ITEMS_IN_A_SHOP);
            return;
        }
        int itemType = ((Number)(shop.getParams().get("itemType"))).intValue();
        String shopType = shop.getParams().getString("shopType");
        if ("storage".equals(shopType))
            sellToStorage(context, itemID, amount, type, itemType);
        else
            sellToShop(context, itemID, amount, type, itemType);
    }

    public static void sellToShop(CompContextBean context, String itemID,
            int amount, CompItemTypeBean type, int itemType)
    {
        if ((itemType >= 0) && (itemType != type.getType()))
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_CANT_SELL_THAT_ITEM_IN_THIS_SHOP);
            return;
        }
        CompUserBean user = context.getUser();
        if (!ItemLogic.contains(user.getItems(), itemID, amount))
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_DONT_HAVE_XXX_YYY, amount, type.getName());
            return;
        }
        float cost = Math.abs(type.getCost())*amount/type.getCount();
        ExperienceLogic.addGold(user, cost);
        UserLogic.doRemoveItem(user, itemID, amount);
        context.addMessage(CompanionsModelConst.TEXT_YOU_SELL_XXX_YYY_FOR_ZZZ_GOLD_PIECES, amount, type.getName(), (int)cost);
        context.addMessage(CompanionsModelConst.TEXT_YOU_NOW_HAVE_XXX_GOLD_PIECES, (int)user.getGoldPieces());
        CompIOLogic.saveUser(user);
        CompIOLogic.logSell(user, itemID, amount, cost);
    }

    public static void sellToStorage(CompContextBean context, String itemID,
            int amount, CompItemTypeBean type, int itemType)
    {
        if ((itemType >= 0) && (itemType != type.getType()))
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_CANT_STORE_THAT_ITEM_IN_THIS_ROOM);
            return;
        }
        CompUserBean user = context.getUser();
        if (!ItemLogic.contains(user.getItems(), itemID, amount))
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_DONT_HAVE_XXX_YYY, amount, type.getName());
            return;
        }
        UserLogic.doRemoveItem(user, itemID, amount);
        PurchaseLogic.storeItem(user, itemID, amount);
        context.addMessage(CompanionsModelConst.TEXT_YOU_STORE_XXX_YYY, amount, type.getName());
        CompIOLogic.saveUser(user);
    }

    private static void storeItem(CompUserBean user, String itemID, int amount)
    {
        String loc = user.getLocation();
        JSONObject items = JSONUtils.getObject(user.getMetadata(), "storage."+loc);
        if (items == null)
        {
            items = new JSONObject();
            JSONObject storage = JSONUtils.getObject(user.getMetadata(), "storage");
            if (storage == null)
            {
                storage = new JSONObject();
                user.getMetadata().put("storage", storage);
            }
            storage.put(loc, items);
        }
        int quan = IntegerUtils.parseInt(items.get(itemID));
        quan += amount;
        if (quan == 0)
            items.remove(itemID);
        else
            items.put(itemID, quan);
    }

    public static void buy(CompContextBean context, String itemID, int amount)
    {
        DebugUtils.trace("Buying id="+itemID+", amount="+amount);
        CompItemTypeBean type = ItemLogic.getItemType(itemID);
        if (type == null)
            throw new IllegalArgumentException("No such item type '"+itemID+"'");
        CompOperationLogic.fillContext(context);
        CompRoomBean shop = context.getRoom();
        if (shop == null)
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_CAN_ONLY_BUY_ITEMS_IN_A_SHOP);
            return;
        }
        String shopType = shop.getParams().getString("shopType");
        if ("storage".equals(shopType))
            buyFromStorage(context, itemID, amount, type);
        else
            buyFromShop(context, itemID, amount, type, shop);
    }

    public static void buyFromStorage(CompContextBean context, String itemID,
            int amount, CompItemTypeBean type)
    {
        CompUserBean user = context.getUser();
        String loc = user.getLocation();
        JSONObject items = JSONUtils.getObject(user.getMetadata(), "storage."+loc);
        if (items == null)
            items = new JSONObject();
        if (!items.containsKey(itemID))
        {
            context.addMessage(CompanionsModelConst.TEXT_THAT_ITEM_ISNT_STORED_HERE);
            return;
        }
        int inventory = IntegerUtils.parseInt(items.get(itemID));
        if (amount > inventory)
        {
            context.addMessage(CompanionsModelConst.TEXT_THERE_ARE_ONLY_XXX_OF_THAT_STORED_HERE, inventory);
            return;
        }
        UserLogic.doAddItem(user, itemID, amount);
        PurchaseLogic.storeItem(user, itemID, -amount);
        context.addMessage(CompanionsModelConst.TEXT_YOU_TAKE_XXX_YYY_OUT_OF_STORAGE, amount, type.getName());
        CompIOLogic.saveUser(user);
    }


    public static void buyFromShop(CompContextBean context, String itemID,
            int amount, CompItemTypeBean type, CompRoomBean shop)
    {
        if (!validateItemPresent(itemID, shop))
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_CANT_BUY_THAT_ITEM_IN_THIS_SHOP);
            return;
        }
        float cost = Math.abs(type.getCost())*amount/type.getCount();
        CompUserBean user = context.getUser();
        if (cost > user.getGoldPieces())
        {
            context.addMessage(CompanionsModelConst.TEXT_YOU_DONT_HAVE_ENOUGH_GOLD_TO_BUY_XXX_YYY, amount, type.getName());
            context.addMessage(CompanionsModelConst.TEXT_YOU_ONLY_HAVE_XXX_GOLD_PIECES, (int)user.getGoldPieces());
            return;
        }
        ExperienceLogic.addGold(user, -cost);
        UserLogic.doAddItem(user, itemID, amount);
        context.addMessage(CompanionsModelConst.TEXT_YOU_BUY_XXX_YYY_FOR_ZZZ_GOLD_PIECES, amount, type.getName(), (int)cost);
        context.addMessage(CompanionsModelConst.TEXT_YOU_NOW_HAVE_XXX_GOLD_PIECES, (int)user.getGoldPieces());
        CompIOLogic.saveUser(user);
        CompIOLogic.logBuy(user, itemID, amount, cost);
    }

    private static boolean validateItemPresent(String itemID, CompRoomBean shop)
    {
        DebugUtils.trace("validateItemPresent(id="+itemID+")");
        if (shop.getParams().containsKey("items"))
        {
            String[] items = JSONUtils.toStringArray((JSONArray)(shop.getParams().get("items")));
            if (ArrayUtils.indexOf(items, itemID) >= 0)
            {
                DebugUtils.trace("present in index");
                return true;
            }
        }
        for (int dir = 0; dir < 4; dir++)
            if (itemID.equals(shop.getParams().get(CompRoomBean.DIR_ITEM[dir])))
            {
                DebugUtils.trace("present in direction indicie");
                return true;
            }
        DebugUtils.trace("not present in "+shop.getParams().toJSONString());
        return false;
    }
}
