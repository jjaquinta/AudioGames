package jo.audio.companions.app;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.app.logic.BuyLogic;
import jo.audio.companions.app.logic.CombatLogic;
import jo.audio.companions.app.logic.DefaultLogic;
import jo.audio.companions.app.logic.HelpLogic;
import jo.audio.companions.app.logic.LookLogic;
import jo.audio.companions.app.logic.OperationLogic;
import jo.audio.companions.app.logic.TeamLogic;
import jo.audio.companions.app.logic.WhatLogic;
import jo.audio.companions.app.logic.WhoLogic;
import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.ItemLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioRequestBean;
import jo.util.utils.obj.StringUtils;

public class BaseStateHandler extends CompStateHandler
{
    @Override
    public void handleWelcome(BaseUserState s)
    {
        CompState state = (CompState)s;
        if (CombatLogic.isInCombat(state))
        {
            state.setState(CompState.STATE_COMBAT);
            getApp().getStateHandler(state).handleWelcome(state);
            return;
        }
        if (state.getUser().isQuestion())
        {
            if (state.getUser().isNSEWQuestion())
                state.setState(CompState.STATE_DIRECTION);
            else
                state.setState(CompState.STATE_QUESTION);
            getApp().getStateHandler(state).handleWelcome(state);
            return;
        }
        long lastInteraction = state.getContext().getUser()
                .getLastInteraction();
        long now = System.currentTimeMillis();
        if ((now - lastInteraction) / (6L * 60 * 60 * 1000) >= 1)
        {
            state.getApplication().log("Playing sound");
            if (state.isCirrane())
                state.respond(CompanionsModelConst.TEXT_INTRO_SOUND_CIRRANE);
            else if (state.isIreland())
                state.respond(CompanionsModelConst.TEXT_INTRO_SOUND_IRELAND);
            else if (state.isIceland())
                state.respond(CompanionsModelConst.TEXT_INTRO_SOUND_ICELAND);
            else
                state.respond(CompanionsModelConst.TEXT_INTRO_SOUND);
        }
        if (lastInteraction <= 0)
        {
            state.getApplication().log("Playing intro");
            if (state.isCirrane())
                state.respond(CompanionsModelConst.TEXT_INTRO_TO_CIRRANE);
            else if (state.isIreland())
                state.respond(CompanionsModelConst.TEXT_INTRO_TO_IRELAND);
            else if (state.isIceland())
                state.respond(CompanionsModelConst.TEXT_INTRO_TO_ICELAND);
            else
                state.respond(CompanionsModelConst.TEXT_INTRO_TO_COMPANIONS);
        }
        else
        {
            state.getApplication().log("Playing welcome");
            if (state.isCirrane())
                state.respond(CompanionsModelConst.TEXT_WELCOME_TO_CIRRANE);
            else if (state.isIreland())
                state.respond(CompanionsModelConst.TEXT_WELCOME_TO_IRELAND);
            else if (state.isIceland())
                state.respond(CompanionsModelConst.TEXT_WELCOME_TO_ICELAND);
            else if (state.isSubscriber())
                state.respond(CompanionsModelConst.TEXT_WELCOME_SUBSCRIBER);
            else
                state.respond(CompanionsModelConst.TEXT_WELCOME_TO_COMPANIONS);
            if ((state.getUser().getInteractions() > 20) && !state.isCirrane())
                HelpLogic.addNewNews(state);
            LookLogic.doLook(state);
        }
        state.getResponse().setLinkOutName(state.resolve(CompanionsModelConst.TEXT_FORUM));
        state.getResponse().setLinkOutURL(
                "http://starlanes.freeforums.net/board/5/6-swords-discussion");
    }
    
    @Override
    public void addReprompts(BaseUserState s)
    {
        super.addReprompts(s);
        CompState state = (CompState)s;
        if (state.getContext().getUser().isQuestion())
        {
            if (state.getContext().getUser().isNSEWQuestion())
                state.setState(CompState.STATE_DIRECTION);
            else
                state.setState(CompState.STATE_QUESTION);
            state.respond(state.getUser().getQuestionText());
        }
        if ((state.getMore() != null) && (state.getMore().size() > 0))
            state.respond(CompanionsModelConst.TEXT_MORE_SOUND);
        String flags = null;
        if ((flags == null) || !flags.contains("lite"))
        {
            try
            {
                if (state.getContext().getRoom() != null)
                    state.getResponse().setCardTitle(state.resolve(state.getContext().getRoom().getName()));
                else
                {
                    String sqid = CompConstLogic.TERRAIN_NAMES[state.getContext().getSquare().getTerrain()]
                            +"_"+state.getContext().getSquare().getTerrainDepth();
                    state.getResponse().setCardTitle(state.resolve("{{"+sqid+"}}"));
                }
                String loc = URLEncoder.encode(state.getUser().getLocation(), "utf-8");
                state.getResponse().setCardImageSmall(CompApplicationHandler.MAP_URL+"?size=S&scale=96&ords="+loc);
                state.getResponse().setCardImageLarge(CompApplicationHandler.MAP_URL+"?size=L&scale=128&ords="+loc);
                state.getResponse().setCardImageHero(CompApplicationHandler.MAP_URL+"?size=H&scale=48&ords="+loc);
            }
            catch (UnsupportedEncodingException e)
            {
            }
        }
    }
    
    @Override
    public void handleOption(BaseUserState s)
    {
        CompState state = (CompState)s;
        CompRoomBean room = state.getContext().getRoom();
        if (room != null)
            if (CompRoomBean.TYPE_FIGHTERS_GUILD.equals(room.getType()))
                doHire(state, s.getRequest().getRawText());
            else if (CompRoomBean.TYPE_ITEM_SHOP.equals(room.getType()))
                doBuy(state, s.getRequest().getRawText());
    }
    public void doLook(CompState state)
    {
        LookLogic.doLook(state, null, null, true);
        if (!state.prompt(CompanionsModelConst.INTENT_LOOK,
                CompanionsModelConst.TEXT_AT_THE_END_OF_THIS_MESSAGE_YOU_WILL_HEAR_THIS_SHORT_TONE))
            state.prompt(
                    CompanionsModelConst.INTENT_NORTH + ","
                            + CompanionsModelConst.INTENT_SOUTH + ","
                            + CompanionsModelConst.INTENT_EAST + ","
                            + CompanionsModelConst.INTENT_WEST,
                    CompanionsModelConst.TEXT_YOU_CAN_SAY_NORTH_SOUTH_EAST_OR_WEST_TO_TRAVEL_IN_THAT_DIRECTION);
    }

    public void doMore(CompState state)
    {
        if ((state.getMore() != null) && (state.getMore().size() > 0))
        {
            state.getApplication().log("More queue size='"+state.getMore().size());
            state.respond(state.getMore().get(0));
            state.getMore().remove(0);
            return;
        }
        state.getApplication().log("More intent='"+state.getMoreIntent()+"', depth="+state.getMoreDepth());
        if (state.getMoreIntent() == null)
        {
            if (state.getLastIntent() == null)
            {
                state.respond(
                        CompanionsModelConst.TEXT_IM_SORRY_I_DONT_HAVE_ANY_MORE_INFORMATION_FOR_YOU_ON_THAT_SUBJECT);
                return;
            }
            state.setMoreIntent(state.getLastIntent());
            state.setMoreDepth(1);
        }
        else
            state.setMoreDepth(1 + state.getMoreDepth());
        switch (state.getMoreIntent())
        {
            case CompanionsModelConst.INTENT_LOOK:
            case CompanionsModelConst.INTENT_MOVE:
            case CompanionsModelConst.INTENT_NORTH:
            case CompanionsModelConst.INTENT_SOUTH:
            case CompanionsModelConst.INTENT_EAST:
            case CompanionsModelConst.INTENT_WEST:
                LookLogic.doMoreLook(state);
                break;
            case CompanionsModelConst.INTENT_ACTIVATE:
                WhoLogic.doMoreActivate(state);
            default:
                HelpLogic.helpBase(state);
                break;
        }
    }

    public void doWho(CompState state)
    {
        WhoLogic.reportWho(state, true);
        if (!state.prompt(CompanionsModelConst.INTENT_MORE,
                CompanionsModelConst.TEXT_AT_THE_END_OF_THIS_MESSAGE_YOU_WILL_HEAR_THIS_SHORT_TONE))
            if (!state.prompt(CompanionsModelConst.INTENT_INVENTORY,
                    CompanionsModelConst.TEXT_YOU_CAN_SAY_INVENTORY_TO_FIND_OUT_WHAT_YOU_ARE_CARRYING)
                    && (state.getUser().getCompanions().size() > 0))
                state.prompt(CompanionsModelConst.INTENT_ACTIVATE,
                        CompanionsModelConst.TEXT_YOU_CAN_SAY_ACTIVATE_XXX_TO_CHANGE_THE_ACTIVE_COMPANION_TO_XXX,
                        state.getUser().getCompanions().get(0).getName());
    }

    public void doActivate(CompState state, String whom)
    {
        TeamLogic.doActivate(state, whom);
        boolean didit = false;
        if ((state.getUser().getCompanions().size() > 1) && (state.getUser().getItems().size() > 0))
        {
            didit = state.prompt(CompanionsModelConst.INTENT_EQUIP,
                CompanionsModelConst.TEXT_YOU_CAN_SAY_EQUIP_XXX_WITH_YYY_TO_ASK_A_COMPANION_WE_WEILD_OR_WEAR_A_CERTAIN_ITEM,
                state.getUser().getCompanions().get(1).getName(),
                state.getUser().getItems().get(0).getType().getName());
        }
        if (!didit && (state.getUser().getCompanions().size() > 0))
            state.prompt(CompanionsModelConst.INTENT_INVENTORY,
                    CompanionsModelConst.TEXT_YOU_CAN_SAY_INVENTORY_XXX_TO_FIND_OUT_WHAT_THEY_ARE_CARRYING,
                    state.getUser().getCompanions().get(0).getName());
    }

    public void doInventory(CompState state, String whom, String type)
    {
        log("doInventory(whom=" + whom + ", type=" + type + ")");
        if ((whom == null) && (type != null))
            whom = type;
        TeamLogic.doInventory(state, whom);
        boolean didPrompt = false;
        if (CompConstLogic.INITIAL_LOCATION
                .equals(state.getUser().getLocation()))
            didPrompt = state.prompt(CompanionsModelConst.INTENT_EAST,
                    CompanionsModelConst.TEXT_YOU_CAN_MOVE_AROUND_BY_SAYING_NORTH_SOUTH_EAST_OR_WEST);
        if (!didPrompt && (state.getUser().getItems().size() > 0))
        {
            CompItemTypeBean item = state.getUser().getItems().get(0).getType();
            if (item.isEquipable() && (state.getUser().getCompanions().size() > 0))
                didPrompt = state.prompt(CompanionsModelConst.INTENT_EQUIP,
                        CompanionsModelConst.TEXT_YOU_CAN_SAY_EQUIP_XXX_WITH_YYY_TO_ASK_A_COMPANION_WE_WEILD_OR_WEAR_A_CERTAIN_ITEM,
                        state.getUser().getCompanions().get(0).getName(),
                        item.getName());
        }
    }

    public void doEquip(CompState state, String whom, String what)
    {
        log("doEquip(whom=" + whom + ", what=" + what + ")");
        if (StringUtils.isTrivial(whom) && StringUtils.isTrivial(what) && !StringUtils.isTrivial(state.getRequest().getRawText()))
        {
            Map<String,String> newSlots = reparseSlots(state);
            if (newSlots != null)
            {
                whom = newSlots.get("whom");
                what = newSlots.get("what");
                log("doEquip(whom=" + whom + ", what=" + what + ") reparse");
            }
        }

        TeamLogic.doEquip(state, whom, what);
        if ((state.getUser().getCompanions().size() > 0) && (state.getUser().getCompanions().get(0).getItems().size() > 0))
            if (!state.prompt(CompanionsModelConst.INTENT_INVENTORY,
                    CompanionsModelConst.TEXT_YOU_CAN_SAY_INVENTORY_XXX_TO_FIND_OUT_WHAT_THEY_ARE_CARRYING,
                    state.getUser().getCompanions().get(0).getName()))
                state.prompt(CompanionsModelConst.INTENT_UNEQUIP,
                        CompanionsModelConst.TEXT_YOU_CAN_SAY_TAKE_XXX_FROM_YYY_TO_UNEQIP_AN_ITEM_FROM_A_COMPANION,
                        state.getUser().getCompanions().get(0).getItems().get(0)
                                .getType().getName(),
                        state.getUser().getCompanions().get(0).getName());
    }

    public void doUnEquip(CompState state, String whom, String what)
    {
        log("doUnEquip(whom=" + whom + ", what=" + what + ")");
        if (StringUtils.isTrivial(whom) && StringUtils.isTrivial(what) && !StringUtils.isTrivial(state.getRequest().getRawText()))
        {
            Map<String,String> newSlots = reparseSlots(state);
            if (newSlots != null)
            {
                whom = newSlots.get("whom");
                what = newSlots.get("what");
                log("doUnEquip(whom=" + whom + ", what=" + what + ") reparse");
            }
        }

        TeamLogic.doUnEquip(state, whom, what);
    }

    public void doBuy(CompState state, String what)
    {
        log("doBuy(what=" + what + ")");
        BuyLogic.doBuy(state, what);
        if (!state.prompt(CompanionsModelConst.INTENT_INVENTORY,
                CompanionsModelConst.TEXT_YOU_CAN_SAY_INVENTORY_TO_FIND_OUT_WHAT_YOU_ARE_CARRYING)
                && state.getUser().getItems().size() > 0)
            state.prompt(CompanionsModelConst.INTENT_EQUIP,
                    CompanionsModelConst.TEXT_YOU_CAN_SAY_EQUIP_XXX_WITH_YYY_TO_ASK_A_COMPANION_WE_WEILD_OR_WEAR_A_CERTAIN_ITEM,
                    state.getUser().getCompanions().get(0).getName(),
                    state.getUser().getItems().get(0).getType().getName());
    }

    public void doSell(CompState state, String what)
    {
        log("doSell(what=" + what + ")");
        BuyLogic.doSell(state, what);
        state.prompt(CompanionsModelConst.INTENT_INVENTORY,
                CompanionsModelConst.TEXT_YOU_CAN_SAY_INVENTORY_TO_FIND_OUT_WHAT_YOU_ARE_CARRYING);
    }

    private void addMoveHelp(CompState state)
    {
        if ((state.getState() == CompState.STATE_BASE)
                && (state.getContext().getFeature() != null))
        {
            if (state.getContext().getRoom() == null)
            { // at something, but not in something
                if (state.getContext().getSquare().isRuin())
                    if (state.prompt("ruin",
                            CompanionsModelConst.TEXT_AROUND_THE_LANDSCAPE_ARE_RUINS_WHERE_MONSTERS_LURK))
                        return;
                if (state.getContext().getSquare().isCastle())
                    if (state.prompt("castle",
                            CompanionsModelConst.TEXT_CASTLES_FORTS_AND_OUTPOSTS_GUARD_THE_DANGEROUS_AREAS_OF_THE_REALM))
                        return;
                if (state.getContext().getSquare().isTown())
                    if (state.prompt("town",
                            CompanionsModelConst.TEXT_CITIES_AND_TOWNS_LIE_ALONG_THE_ROADS_OF_THE_REALM))
                        return;
                state.prompt(CompanionsModelConst.INTENT_ENTER,
                        CompanionsModelConst.TEXT_YOU_CAN_SAY_ENTER_TO_GO_INTO_XXX,
                        state.getContext().getFeature().getFeature().getName());
            }
            else
            {
                if (CompRoomBean.TYPE_FIGHTERS_GUILD
                        .equals(state.getContext().getRoom().getType()))
                {
                    JSONObject[] hires = JSONUtils
                            .toObjectArray((JSONArray)(state.getContext()
                                    .getRoom().getParams().get("hires")));
                    CompCompanionBean hire = new CompCompanionBean();
                    hire.fromJSON(hires[0]);
                    if (!state.prompt(CompanionsModelConst.INTENT_HIRE,
                            CompanionsModelConst.TEXT_YOU_CAN_SAY_HIRE_XXX_TO_ADD_A_NEW_COMPANION_TO_YOUR_TEAM,
                            hire.getName()))
                        if (state.getContext().getCompanion() != null)
                            state.prompt(CompanionsModelConst.INTENT_FIRE,
                                CompanionsModelConst.TEXT_YOU_CAN_SAY_FIRE_XXX_TO_REMOVE_A_COMPANION_FROM_YOUR_TEAM,
                                state.getContext().getCompanion().getName());
                }
                if (CompRoomBean.TYPE_ITEM_SHOP
                        .equals(state.getContext().getRoom().getType())
                        && state.getContext().getRoom().getParams().containsKey("items"))
                {
                    String[] items = JSONUtils.toStringArray((JSONArray)(state
                            .getContext().getRoom().getParams().get("items")));
                    CompItemTypeBean item = ItemLogic.getItemType(items[0]);
                    if (!state.prompt(CompanionsModelConst.INTENT_BUY,
                            CompanionsModelConst.TEXT_YOU_CAN_SAY_BUY_XXX_TO_PURCHASE_AN_ITEM_FROM_THIS_SHOP,
                            item.getName()))
                        state.prompt(CompanionsModelConst.INTENT_SELL,
                                CompanionsModelConst.TEXT_YOU_CAN_SAY_SELL_XXX_TO_SELL_AN_APPROPRIATE_ITEM_TO_THIS_SHOP,
                                item.getName());
                }
            }
        }
    }

    private void moveDirection(CompState state, int dir)
    {
        if (state.getContext().getRoom() == null)
        {
            if (cantMove(state, state.getContext().getSquare(dir)))
                return;
        }
        else
        {
            if (StringUtils.isTrivial(state.getContext().getRoom().getDirection(dir - 1))
                    && (state.getContext().getRoom().getDirectionLock(dir - 1) == null))
            {
                state.respond(CompanionsModelConst.TEXT_YOU_CAN_T_GO_THAT_WAY);
                return;
            }
        }
        SquareBean oldSquare = state.getContext().getSquare();
        int oldTime = state.getContext().getUser().getTotalTime();
        OperationLogic.move(state, dir);
        SquareBean newSquare = state.getContext().getSquare();
        LookLogic.doLook(state, oldSquare, oldTime, false);
        if ((oldSquare.getDemense() != newSquare.getDemense()) && (newSquare.getDemense() != null))
            state.respond(
                    CompanionsModelConst.TEXT_YOU_HAVE_CROSSED_OVER_INTO_XXX,
                    newSquare.getDemense().getFullName());
        CombatLogic.checkCombat(state);
        addMoveHelp(state);
    }

    public void doNorth(CompState state)
    {
        moveDirection(state, CompOperationBean.NORTH);
    }

    public void doSouth(CompState state)
    {
        moveDirection(state, CompOperationBean.SOUTH);
    }

    public void doEast(CompState state)
    {
        moveDirection(state, CompOperationBean.EAST);
    }

    public void doWest(CompState state)
    {
        moveDirection(state, CompOperationBean.WEST);
    }

    public void doMove(CompState state, String dir)
    {
        if ("north".equalsIgnoreCase(dir))
            moveDirection(state, CompOperationBean.NORTH);
        else if ("south".equalsIgnoreCase(dir))
            moveDirection(state, CompOperationBean.SOUTH);
        else if ("east".equalsIgnoreCase(dir))
            moveDirection(state, CompOperationBean.EAST);
        else if ("west".equalsIgnoreCase(dir))
            moveDirection(state, CompOperationBean.WEST);
        else
            state.respond(
                    CompanionsModelConst.TEXT_IM_NOT_SURE_WHAT_DIRECTION_THAT_IS);
    }

    public void doEnter(CompState state)
    {
        if (!StringUtils
                .isTrivial(state.getContext().getLocation().getRoomID()))
        {
            state.respond(
                    CompanionsModelConst.TEXT_YOU_HAVE_ALREADY_ENTERED_THE_PLACE);
            return;
        }
        if (state.getContext().getFeature() == null)
        {
            state.respond(
                    CompanionsModelConst.TEXT_THERE_IS_NO_PLACE_HERE_TO_ENTER);
            return;
        }
        OperationLogic.enter(state);
        LookLogic.doLook(state);
        CombatLogic.checkCombat(state);
    }

    private boolean cantMove(CompState state, SquareBean sq)
    {
        if (sq.isAnyRoads())
            return false;
        if (sq.getTerrain() == CompConstLogic.TERRAIN_FRESHWATER)
        {
            state.respond(CompanionsModelConst.TEXT_BAD_TERRAIN_FRESHWATER);
            return true;
        }
        if (sq.getTerrain() == CompConstLogic.TERRAIN_SALTWATER)
        {
            state.respond(CompanionsModelConst.TEXT_BAD_TERRAIN_SALTWATER);
            return true;
        }
        return false;
    }

    public void doHire(CompState state, String whom)
    {
        log("doHire(whom=" + whom + ")");
        if (StringUtils.isTrivial(whom) && !StringUtils.isTrivial(state.getRequest().getRawText()))
        {
            Map<String,String> newSlots = reparseSlots(state);
            if (newSlots != null)
            {
                whom = newSlots.get("whom");
                log("doHire(whom=" + whom + ") reparse");
            }
        }
        BuyLogic.doHire(state, whom);
    }

    public void doFire(CompState state, String whom)
    {
        log("doFire(whom=" + whom + ")");
        if (StringUtils.isTrivial(whom) && !StringUtils.isTrivial(state.getRequest().getRawText()))
        {
            Map<String,String> newSlots = reparseSlots(state);
            if (newSlots != null)
            {
                whom = newSlots.get("whom");
                log("doFire(whom=" + whom + ") reparse");
            }
        }
        TeamLogic.doFire(state, whom);
    }

    public void doHelp(CompState state)
    {
        HelpLogic.helpBase(state);
    }

    public void doNews(CompState state)
    {
        HelpLogic.news(state);
    }

    public void doSleep(CompState state)
    {
        OperationLogic.sleep(state);
        CombatLogic.checkCombat(state);
    }

    public void doWhatIs(CompState state, String thing)
    {
        log("doWhatIs(thing=" + thing + ")");
        if (StringUtils.isTrivial(thing))
            thing = reparseAny(state);
        WhatLogic.doWhat(state, thing);
    }

    public void doWhereIs(CompState state, String what)
    {
        log("doWhereIs(what=" + what + ")");
        if (StringUtils.isTrivial(what))
            what = reparseAny(state);
        doAbout(state, what);
    }

    public void doAbout(CompState state, String whom)
    {
        log("doAbout(whom=" + whom + ")");
        if (StringUtils.isTrivial(whom))
            whom = reparseAny(state);
        WhoLogic.doAbout(state, whom);
        state.getResponse().setLinkOutName(state.resolve(CompanionsModelConst.TEXT_FORUM));
        state.getResponse().setLinkOutURL(
                "http://starlanes.freeforums.net/board/5/6-swords-discussion");
    }

    public void doStop(CompState state)
    {
        state.respond(CompanionsModelConst.TEXT_THANK_YOU_FOR_PLAYING);
        if (state.isCirrane())
            state.respond(CompanionsModelConst.TEXT_CIRRANE_IF_YOU_HAVE_QUESTIONS_OR_SUGGESTIONS);
        else if (state.getRequest().getOriginator() == AudioRequestBean.ALEXA)
        {
            state.respond(CompanionsModelConst.TEXT_ALEXA_IF_YOU_HAVE_QUESTIONS_OR_SUGGESTIONS);
            if (!state.isFlag("lite"))
                if (BaseUserState.RND.nextInt(3) == 0)
                    state.respond(CompanionsModelConst.TEXT_ALEXA_OTHER_GAMES);
        }
        else
        {
            state.respond(CompanionsModelConst.TEXT_ASSISTANT_IF_YOU_HAVE_QUESTIONS_OR_SUGGESTIONS);
            if (BaseUserState.RND.nextInt(3) == 0)
                state.respond(CompanionsModelConst.TEXT_ASSISTANT_OTHER_GAMES);
        }
        state.getResponse().setShouldEndSession(true);
    }

    public void doCancel(CompState state)
    {
        doStop(state);
    }
    
    public void doDefault(CompState state)
    {
        if ("\n".equals(state.getRequest().getRawText()))
        {
            log("Funky enter");
            doEnter(state);
        }
        else
            DefaultLogic.doDefault(state, this);
    }
}
