package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.ItemLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.PhoneticMatchLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class BuyLogic
{

    public static void doBuy(CompState state, String othing)
    {
        CompRoomBean shop = state.getContext().getRoom();
        if ((shop == null) || (shop.getParams() == null) || !shop.getParams().containsKey("items"))
        {
            state.respond(CompanionsModelConst.TEXT_YOU_CAN_ONLY_BUY_ITEMS_IN_A_SHOP);
            return;
        }
        String thing = simplifyThing(othing);
        if (StringUtils.isTrivial(thing))
        {
            state.respond(CompanionsModelConst.TEXT_YOU_NEED_TO_SAY_WHAT_ITEM_YOU_WANT_TO_BUY);
            LookLogic.doLookShop(state, shop.getParams());
            return;
        }
        String[] items = JSONUtils.toStringArray((JSONArray)(shop.getParams().get("items")));
        List<String> vocab = new ArrayList<>();
        for (String id : items)
        {
            CompItemTypeBean itemType = ItemLogic.getItemType(id);
            if (!itemType.getBaseID().equals(itemType.getID()))
                itemType = ItemLogic.getItemType(itemType.getBaseID());
            vocab.add(itemType.getName().toLowerCase());
        }
        int idx = PhoneticMatchLogic.findMatchIdx(thing, vocab);
        if (idx < 0)
        {
            DebugUtils.trace("Couldn't find a '"+othing+"' -> '"+thing+"' among "+StringUtils.toCommaString(vocab.toArray(new String[0])));
            state.respond(CompanionsModelConst.TEXT_YOU_CANT_BUY_A_XXX_IN_THIS_SHOP, othing);
            return;
        }
        CompItemTypeBean type = ItemLogic.getItemType(items[idx]);
        float cost = Math.abs(type.getCost());
        CompUserBean user = state.getContext().getUser();
        if (cost > user.getGoldPieces())
        {
            state.respond(CompanionsModelConst.TEXT_YOU_DONT_HAVE_ENOUGH_GOLD_TO_BUY_XXX_YYY, type.getCount(), type.getName());
            state.respond(CompanionsModelConst.TEXT_YOU_ONLY_HAVE_XXX_GOLD_PIECES, (int)user.getGoldPieces());
            return;
        }
        OperationLogic.buy(state, type, type.getCount());
    }
    
    private static final String[] PREFIXES = {
            "plus",
            "cold",
            "iron",
            "frost",
            "flaming",
            "werebane",
            "where",
            "demonbane",
            "demon",
            "bane",
            "anti-devil",
            "anti",
            "aunty",
            "devil",
            "undead",
            "slaying",
    };
    
    private static final String[] SUFFIXES = {
            "of",
            "dragon",
            "giant",
            "dragonslayer",
            "giantslayer",
    };
    
    private static String simplifyThing(String thing)
    {
        if (thing == null)
            return thing;
        thing = thing.toLowerCase();
        for (String prefix : PREFIXES)
            if (thing.startsWith(prefix+" "))
                thing = thing.substring(prefix.length()).trim();
        while ((thing.length() > 0) && Character.isDigit(thing.charAt(0)))
            thing = thing.substring(1);
        for (String suffix : SUFFIXES)
        {
            int o = thing.indexOf(" "+suffix);
            if (o < 0)
                continue;
            thing = thing.substring(0, o).trim();
        }
        return thing;
    }

    public static void doSell(CompState state, String thing)
    {
        List<String> vocab = new ArrayList<>();
        for (CompItemInstanceBean item : state.getContext().getUser().getItems())
            vocab.add(item.getType().getName());
        int idx = PhoneticMatchLogic.findMatchIdx(thing, vocab);
        state.getApplication().log("'"+thing+"' matches in "+vocab+" with "+idx);
        if (idx < 0)
        {
            state.respond(CompanionsModelConst.TEXT_YOU_DONT_HAVE_XXX_YYY, 1, thing);
            return;
        }
        CompItemInstanceBean item = state.getContext().getUser().getItems().get(idx);
        state.getApplication().log("item "+item.getType().getName()+" quantity="+item.getQuantity()+" count="+item.getType().getCount());
        int q = Math.min(item.getQuantity(),  item.getType().getCount());
//        if (item.getQuantity() < item.getType().getCount())
//        {
//            state.respond(CompanionsModelConst.TEXT_YOU_DONT_HAVE_XXX_YYY, item.getType().getCount(), thing);
//            return;
//        }
        CompRoomBean shop = state.getContext().getRoom();
        if ((shop == null) || (shop.getParams() == null) || (shop.getParams().get("itemType") == null))
        {
            state.getApplication().log("user location ="+state.getContext().getUser().getLocation()+", square feature="+state.getContext().getSquare().getFeature());
            state.respond(CompanionsModelConst.TEXT_YOU_CAN_ONLY_SELL_ITEMS_IN_A_SHOP);
            return;
        }
        int shopType = ((Number)(shop.getParams().get("itemType"))).intValue();
        state.getApplication().log("shop type="+shopType+", item type="+item.getType().getType());
        if ((shopType >= 0) && (shopType != item.getType().getType()))
        {
            state.respond(CompanionsModelConst.TEXT_YOU_CANT_SELL_THAT_ITEM_IN_THIS_SHOP);
            return;
        }
        OperationLogic.sell(state, item, q);
    }

    public static void doHire(CompState state, String whom)
    {
        CompRoomBean shop = state.getContext().getRoom();
        if ((shop == null) || !CompRoomBean.TYPE_FIGHTERS_GUILD.equals(shop.getType()))
        {
            state.respond(CompanionsModelConst.TEXT_YOU_CAN_ONLY_HIRE_PEOPLE_IN_A_GUILD_HALL);
            return;
        }
        CompUserBean user = state.getContext().getUser();
        if (user.getAllCompanions().size() >= CompConstLogic.MAX_COMPANIONS)
        {
            state.respond(CompanionsModelConst.TEXT_GUILD_RULES_ALLOW_FOR_A_MAXIMUM_OF_XXX_COMPANIONS, CompConstLogic.MAX_COMPANIONS);
            return;
        }
        if (StringUtils.isTrivial(whom))
        {
            state.respond(CompanionsModelConst.TEXT_PLEASE_SAY_HIRE_AND_THE_NAME_OF_THE_COMPANION_YOU_WISH_TO_HIRE);
            return;
        }
        JSONObject[] hires = JSONUtils.toObjectArray((JSONArray)(shop.getParams().get("hires")));
        List<String> names = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < hires.length; i++)
        {
            CompCompanionBean hire = new CompCompanionBean();
            hire.fromJSON(hires[i]);
            if (state.getUser().getCompanion(hire.getID()) != null)
                continue;
            String name = hire.getName();
            name = state.expandInserts(name);
            names.add(name);
            ids.add(hire.getID());
        }
        int idx = PhoneticMatchLogic.findMatchIdx(whom, names);
        if (idx < 0)
        {
            state.respond(CompanionsModelConst.TEXT_THERE_ISNT_ANYONE_CALLED_XXX_AVAILABLE_FOR_HIRE_HERE, whom);
            return;
        }
        CompContextBean context = OperationLogic.hire(state, ids.get(idx));
        if (!context.isError())
        {
            int numCompanions = context.getUser().getAllCompanions().size();
            state.respond(CompanionsModelConst.TEXT_YOU_NOW_HAVE_XXX_OUT_OF_YYY_COMPANIONS, numCompanions, CompConstLogic.MAX_COMPANIONS);
            if (numCompanions == CompConstLogic.MAX_COMPANIONS)
                state.respond(CompanionsModelConst.TEXT_YOU_CANT_HIRE_ANY_MORE);
            else
                state.respond(CompanionsModelConst.TEXT_YOU_CAN_HIRE_XXX_MORE, CompConstLogic.MAX_COMPANIONS - numCompanions);
            if (state.getContext().getCompanion() != null)
                state.prompt(CompanionsModelConst.INTENT_FIRE,
                    CompanionsModelConst.TEXT_YOU_CAN_SAY_FIRE_XXX_TO_REMOVE_A_COMPANION_FROM_YOUR_TEAM,
                        state.getContext().getCompanion().getName());
        }
    }

}
