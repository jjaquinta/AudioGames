package jo.audio.companions.app;

import java.net.URLEncoder;
import java.util.List;

import jo.audio.companions.app.logic.CombatLogic;
import jo.audio.companions.app.logic.DefaultLogic;
import jo.audio.companions.app.logic.HelpLogic;
import jo.audio.companions.app.logic.LookLogic;
import jo.audio.companions.app.logic.OperationLogic;
import jo.audio.companions.app.logic.TeamLogic;
import jo.audio.companions.app.logic.WhoLogic;
import jo.audio.companions.data.CompMonsterInstanceBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.data.IntentReqBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class CombatStateHandler extends CompStateHandler
{
    @Override
    public void handleWelcome(BaseUserState s)
    {
        CompState state = (CompState)s;   
        if (!CombatLogic.isInCombat(state))
        {
            state.setState(CompState.STATE_BASE);
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
        long lastInteraction = state.getContext().getUser().getLastInteraction();
        long now = System.currentTimeMillis();
        if ((now - lastInteraction)/(6L*60*60*1000) >= 1)
             state.respond(CompanionsModelConst.TEXT_INTRO_SOUND);
        if (state.isSubscriber())
            state.respond(CompanionsModelConst.TEXT_WELCOME_SUBSCRIBER);
        else if (state.isIreland())
            state.respond(CompanionsModelConst.TEXT_INTRO_SOUND_IRELAND);
        else if (state.isIceland())
            state.respond(CompanionsModelConst.TEXT_INTRO_SOUND_ICELAND);
        else
            state.respond(CompanionsModelConst.TEXT_WELCOME_TO_COMPANIONS);
        reportCombatants(state);
        state.getResponse().setLinkOutName(state.resolve(CompanionsModelConst.TEXT_FORUM));
        state.getResponse().setLinkOutURL(
                "http://starlanes.freeforums.net/board/5/6-swords-discussion");
    }
    
    @Override
    public void addReprompts(BaseUserState s)
    {
        super.addReprompts(s);
        CompState state = (CompState)s;
        if ((state.getMore() != null) && (state.getMore().size() > 0))
            state.respond(CompanionsModelConst.TEXT_MORE_SOUND);
        String flags = null;
        if (((flags == null) || !flags.contains("lite")) && (state.getUser().getEncounter().getMonsters().size() > 0))
        {
            try
            {
                List<String> combatants = CombatLogic.listCombatants(state.getContext().getUser().getEncounter().getMonsters());
                CompMonsterInstanceBean monster = state.getUser().getEncounter().getMonsters().get(0);
                state.getResponse().setCardTitle(StringUtils.capitalize(state.resolve(AudioMessageBean.and(combatants.toArray()))));
                String id = URLEncoder.encode(monster.getType().getName(), "utf-8");
                state.getResponse().setCardImageSmall(CompApplicationHandler.MONST_URL+"?size=S&m="+id);
                state.getResponse().setCardImageLarge(CompApplicationHandler.MONST_URL+"?size=L&m="+id);
                state.getResponse().setCardImageHero(CompApplicationHandler.MONST_URL+"?size=h&m="+id);
            }
            catch (Exception e)
            {
                DebugUtils.trace("error adding reprompts", e);
            }
        }
    }
    
    private void reportOnCombat(CompState state)
    {
        switch (BaseUserState.RND.nextInt(3))
        {
            case 0:
                reportCombatants(state);
                break;
            case 1:
                state.respondRaw(ResponseUtils.wordList(CombatLogic.listFighterArms(state))+".");
                break;
            case 2:
                state.respondRaw(ResponseUtils.wordList(CombatLogic.listFighterHits(state))+".");
                break;
        }
    }

    private void reportCombatants(CompState state)
    {
        List<String> combatants = CombatLogic.listCombatants(state.getContext().getUser().getEncounter().getMonsters());
        state.respond(CompanionsModelConst.TEXT_YOU_ARE_IN_COMBAT_WITH_XXX, ResponseUtils.wordList(combatants));
    }
    
    public void doFight(CompState state)
    {
        OperationLogic.fight(state);
        if (!CombatLogic.isInCombat(state))
        {
            state.setState(CompState.STATE_BASE);
            LookLogic.doLook(state);
            return;
        }
    }

    public void doLook(CompState state)
    {
        LookLogic.doLook(state, null, null, true);
        reportOnCombat(state);
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
        if (state.getMoreIntent() == null)
        {
            state.setMoreIntent(state.getLastIntent());
            if (state.getMoreIntent() == null)
            {
                state.respond(CompanionsModelConst.TEXT_IM_SORRY_I_DONT_HAVE_ANY_MORE_INFORMATION_FOR_YOU_ON_THAT_SUBJECT);
                return;
            }
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
                break;
        }
    }

    public void doWho(CompState state)
    {
        WhoLogic.reportWho(state, true);
        reportOnCombat(state);
    }

    public void doActivate(CompState state, String whom)
    {
        TeamLogic.doActivate(state, whom);
        reportOnCombat(state);
    }

    public void doInventory(CompState state, String whom, String type)
    {
        log("doInventory(whom=" + whom + ", type=" + type + ")");
        if ((whom == null) && (type != null))
            whom = type;
        TeamLogic.doInventory(state, whom);
        reportOnCombat(state);
    }

    public void doEquip(CompState state, String whom, String what)
    {
        log("doEquip(whom=" + whom + ", what=" + what + ")");
        TeamLogic.doEquip(state, whom, what);
        reportOnCombat(state);
    }

    public void doUnEquip(CompState state, String whom, String what)
    {
        log("doUnEquip(whom=" + whom + ", what=" + what + ")");
        TeamLogic.doUnEquip(state, whom, what);
        reportOnCombat(state);
    }

    public void doMove(CompState state)
    {
        // TODO: ask yes/no
        state.respond(CompanionsModelConst.TEXT_YOU_RUN_AWAY_FROM_THE_COMBAT);
        OperationLogic.abandonCombat(state);
        LookLogic.doLook(state);
        state.setState(CompState.STATE_BASE);
    }

    public void doHelp(CompState state)
    {
        HelpLogic.helpCombat(state);
    }

    public void doNews(CompState state)
    {
        HelpLogic.news(state);
    }

    public void doWhatIs(CompState state, String thing)
    {
        doAbout(state, thing);
    }

    public void doWhereIs(CompState state, String thing)
    {
        doAbout(state, thing);
    }

    public void doAbout(CompState state, String whom)
    {
        log("doAbout(whom=" + whom + ")");
        WhoLogic.doAbout(state, whom);
        state.getResponse().setLinkOutName(state.resolve(CompanionsModelConst.TEXT_FORUM));
        state.getResponse().setLinkOutURL(
                "http://starlanes.freeforums.net/board/5/6-swords-discussion");
    }

    public void doStop(CompState state)
    {
        doMove(state);
    }

    public void doCancel(CompState state)
    {
        doMove(state);
    }

    public void doDefault(CompState state, IntentReqBean intent)
    {
        if (intent.getIntentID().equals(CompanionsModelConst.INTENT_SLEEP))
        {
            state.respond(CompanionsModelConst.TEXT_YOU_CANT_SLEEP_DURING_COMBAT);
            return;
        }
        DefaultLogic.doDefault(state, this);
    }
}
