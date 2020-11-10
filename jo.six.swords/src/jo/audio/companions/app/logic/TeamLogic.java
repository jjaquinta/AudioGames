package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

public class TeamLogic
{

    public static void doActivate(CompState state, String whom)
    {
        CompCompanionBean comp = MatchLogic
                .resolveCompanion(state, state.getContext().getUser(), whom, false);
        if (comp == null)
        {
            if (StringUtils.isTrivial(whom))
            {
                if (state.getContext().getCompanion() == null)
                {
                    state.respondRaw(state.getApplication().pickExample(state.getRequest().getLanguage(), CompanionsModelConst.INTENT_ACTIVATE));
                }
                else
                {
                    state.respond(
                            CompanionsModelConst.TEXT_YOUR_ACTIVE_COMPANION_IS_XXX,
                            state.getContext().getCompanion().getName());
                    state.respond(CompanionsModelConst.TEXT_MORE_SOUND);
                    state.setMoreIntent(CompanionsModelConst.INTENT_ACTIVATE);
                    state.setMoreDepth(0);
                }
            }
            else
                state.respond(CompanionsModelConst.TEXT_I_DONT_KNOW_WHO_XXX_IS,
                        whom);
            return;
        }
        if (comp.getID()
                .equals(state.getContext().getUser().getActiveCompanion()))
        {
            state.respond(
                    CompanionsModelConst.TEXT_YOUR_ACTIVE_COMPANION_ALREADY_WAS_XXX,
                    comp.getName());
            return;
        }
        OperationLogic.activate(state, comp);
    }

    public static void doInventory(CompState state, String whom)
    {
        int filter = -1;
        if (state.getContext().getRoom() != null)
        {
            CompRoomBean room = state.getContext().getRoom();
            if ("shop".equals(room.getType()) && (room.getParams() != null) && room.getParams().containsKey("itemType"))
                filter = IntegerUtils.parseInt(room.getParams().get("itemType"));
        }
        if (StringUtils.isTrivial(whom))
        {
            doCommonInventory(state, filter);
            return;
        }
        CompCompanionBean comp = MatchLogic
                .resolveCompanion(state, state.getContext().getUser(), whom, false);
        if (comp == null)
        {
            state.respond(CompanionsModelConst.TEXT_IM_NOT_SURE_WHO_XXX_IS,
                    whom);
            List<String> compNames = ListLogic.companions(
                    state.getContext().getUser().getCompanions(), null);
            if (compNames.size() == 1)
            {
                state.respond(CompanionsModelConst.TEXT_YOUR_COMPANION_IS_XXX,
                        compNames.get(0));
            }
            else if (compNames.size() > 1)
            {
                state.respond(CompanionsModelConst.TEXT_YOUR_COMPANIONS_ARE_XXX,
                        ResponseUtils.wordList(compNames));
            }
            return;
        }
        reportCarrying(state, comp, filter);
    }

    public static void doCommonInventory(CompState state, int filter)
    {
        if (!CompanionsModelConst.INTENT_ACTIVATE.equalsIgnoreCase(state.getLastIntent()))
        {
            appendInventory(state, filter);
            int gold = (int)state.getContext().getUser().getGoldPieces();
            if (gold > 0)
                state.respond(CompanionsModelConst.TEXT_YOU_HAVE_XXX_GOLD_PIECES, gold);
            else
                state.respond(CompanionsModelConst.TEXT_YOU_HAVE_NO_GOLD);
        }
        if (state.getContext().getCompanion() != null)
            reportCarrying(state, state.getContext().getCompanion(), filter);
        List<AudioMessageBean> msgs = state.getContext().getMessageDestinations();
        if (msgs.size() > 0)
        {
            AudioMessageBean posts = new AudioMessageBean(AudioMessageBean.AND, new Object[msgs.size()]);
            for (int i = 0; i < msgs.size(); i++)
            {
                AudioMessageBean msg = msgs.get(i);
                posts.getArgs()[i] = msg;
            }
            AudioMessageBean messages;
            if (msgs.size() == 1)
                messages = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_A_MESSAGE_FOR_XXX, posts);
            else
                messages = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_A_MESSAGES_FOR_XXX, posts);
            state.respond(messages);
        }
    }

    public static void appendInventory(CompState state, int filter)
    {
        List<String> items = ListLogic
                .itemInstances(state.getContext().getUser().getItems(), filter);
        DebugUtils.trace("TeamLogic.doInventory - Items size="+items.size());
        if (items.size() > 0)
        {
            if (items.size() < CompConstLogic.INVENTORY_CHUNK)
            {
                state.respond(
                    CompanionsModelConst.TEXT_THE_GROUP_IS_CARRYING_XXX,
                    ResponseUtils.wordList(items));
            }
            else
            {
                state.respond(
                        CompanionsModelConst.TEXT_THE_GROUP_IS_CARRYING_XXX,
                        ResponseUtils.wordList(items.toArray(), CompConstLogic.INVENTORY_CHUNK, true));
                List<AudioMessageBean> more = new ArrayList<>();
                for (;;)
                {
                    for (int i = 0; i < CompConstLogic.INVENTORY_CHUNK; i++)
                        if (items.size() > 0)
                            items.remove(0);
                    if (items.size() == 0)
                    {
                        break;
                    }
                    more.add(new AudioMessageBean(CompanionsModelConst.TEXT_THE_GROUP_IS_ALSO_CARRYING_XXX,
                            ResponseUtils.wordList(items.toArray(), CompConstLogic.INVENTORY_CHUNK, true)));
                }
                state.setMore(more.toArray(new AudioMessageBean[0]));
            }
        }
        else
            state.respond(
                    CompanionsModelConst.TEXT_THE_GROUP_IS_NOT_CARRYING_ANYTHING);
    }
    
    private static List<CompItemInstanceBean> getEquipable(CompUserBean user)
    {
        List<CompItemInstanceBean> equippable = new ArrayList<>();
        for (CompItemInstanceBean item : user.getItems())
            if (item.getType().getType() != CompItemTypeBean.TYPE_AMMO)
                equippable.add(item);
        return equippable;
    }

    public static void doEquip(CompState state, String whom, String what)
    {
        CompItemInstanceBean item = null;
        if (!StringUtils.isTrivial(what))
            item = MatchLogic.resolveItem(getEquipable(state.getUser()), what);
        state.getApplication().log("what resolves to "+((item == null)?"nothing":item.getFullName()));

        CompCompanionBean comp;
        if ((item != null) && (item.getType().getType() == CompItemTypeBean.TYPE_POTION))
            comp = MatchLogic.resolveCompanion(state, state.getContext().getUser(), whom, true);
        else
            comp = MatchLogic.resolveCompanion(state, state.getContext().getUser(), whom, false);
        if (!StringUtils.isTrivial(whom) && (comp == null))
        {
            if (item == null)
            {
                item = MatchLogic.resolveItem(getEquipable(state.getUser()), whom);
                state.getApplication().log("Cross resolving whom to "+((item == null)?"nothing":item.getFullName()));
                if (item == null)
                {
                    List<AudioMessageBean> msgs = WhatLogic.resolveWhat(state, whom);
                    if (msgs.size() > 0)
                    {
                        state.respond(msgs.get(0));
                        msgs.remove(0);
                        state.setMore(msgs.toArray(new AudioMessageBean[0]));
                        return;
                    }
                    else
                        state.respond(CompanionsModelConst.TEXT_I_DONT_KNOW_WHAT_OR_WHO_XXX_IS,
                            whom);
                }
            }
            else
                state.respond(CompanionsModelConst.TEXT_IM_NOT_SURE_WHO_XXX_IS,
                        whom);
            comp = state.getContext().getCompanion();
        }
        if ((comp == null) && !StringUtils.isTrivial(what))
            comp = state.getContext().getCompanion();

        state.getApplication().log("whom resolves to "+((comp == null)?"nobody":comp.getName()));
        if (comp == null)
        {
            appendInventory(state, -1);
        }
        else if (item == null)
        {
            reportCarrying(state, comp, -1);
        }
        else
        {
            if (CompItemTypeBean.TYPE_AMMO == item.getType().getType())
                state.respond(CompanionsModelConst.TEXT_BAD_ITEM_AMMO);
            else
            {
                OperationLogic.equip(state, comp, item, 1);
            }
        }
    }

    private static void reportCarrying(CompState state, CompCompanionBean comp, int filter)
    {
        List<CompItemInstanceBean> items = new ArrayList<>();
        items.addAll(comp.getItems());
        if (filter >= 0)
            for (Iterator<CompItemInstanceBean> i = items.iterator(); i.hasNext(); )
                if (i.next().getType().getType() != filter)
                    i.remove();
        if (items.size() > 0)
        {
            List<String> itemNames = ListLogic.itemInstances(items);
            state.respond(CompanionsModelConst.TEXT_XXX_IS_CARRYING_YYY,
                    comp.getName(), AudioMessageBean.and(itemNames));
            for (CompItemInstanceBean item : items)
            {
                List<AudioMessageBean> msgs = new ArrayList<>();
                WhatLogic.whatItemType(msgs, item.getType());
                // wrap segments into sentences
                for (int i = 0; i < msgs.size(); i++)
                {
                    AudioMessageBean msg = msgs.get(i);
                    msg = new AudioMessageBean(CompanionsModelConst.TEXT_SENTANCE, msg);
                    msgs.remove(i);
                    msgs.add(i, msg);
                }
                if (!StringUtils.isTrivial(item.getName()))
                {
                    msgs.add(0, new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_A_YYY,
                            item.getName(), item.getType().getName()));
                }
                state.addMore(msgs);
            }
        }
        else
            state.respond(
                    CompanionsModelConst.TEXT_XXX_IS_NOT_CARRYING_ANYTHING,
                    comp.getName());
    }

    public static void doUnEquip(CompState state, String whom, String what)
    {
        CompCompanionBean comp = MatchLogic
                .resolveCompanion(state, state.getContext().getUser(), whom, false);
        if (!StringUtils.isTrivial(whom) && (comp == null))
        {
            state.respond(CompanionsModelConst.TEXT_IM_NOT_SURE_WHO_XXX_IS,
                    whom);
            comp = state.getContext().getCompanion();
        }
        if (comp == null)
        {
            if (state.getContext().getCompanion() != null)
                comp = state.getContext().getCompanion();
            else if (state.getUser().getAnyCompanion(state.getUser().getActiveCompanion()) != null)
                comp = state.getUser().getAnyCompanion(state.getUser().getActiveCompanion());
            else if (state.getUser().getCompanions().size() > 0)
                comp = state.getUser().getCompanions().get(0);
            else
            {
                state.respond(CompanionsModelConst.TEXT_YOU_DONT_HAVE_ANY_COMPANIONS);
                return;
            }
        }
        CompItemInstanceBean item = null;
        if (!StringUtils.isTrivial(what))
        {
            item = MatchLogic.resolveItem(comp, what);
            if (item == null)
            {
                state.respond(CompanionsModelConst.TEXT_XXX_IS_NOT_CARRYING_YYY,
                        comp.getName(), what);
                reportCarrying(state, comp, -1);
                return;
            }
        }
        else
        {
            state.respond(
                    CompanionsModelConst.TEXT_YOU_NEED_TO_TELL_ME_WHAT_YOU_WANT_TO_UNEQUIP);
            return;
        }
        OperationLogic.unequip(state, comp, item, 1);
    }

    public static void doFire(CompState state, String whom)
    {
        CompCompanionBean comp = MatchLogic
                .resolveCompanion(state, state.getContext().getUser(), whom, false);
        if (comp == null)
        {
            if (!StringUtils.isTrivial(whom))
                state.respond(CompanionsModelConst.TEXT_I_DONT_KNOW_WHO_XXX_IS,
                        whom);
            WhoLogic.reportWho(state, false);
            return;
        }
        OperationLogic.fire(state, comp);
    }

}
