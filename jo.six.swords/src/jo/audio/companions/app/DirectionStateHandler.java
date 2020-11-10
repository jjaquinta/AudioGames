package jo.audio.companions.app;

import jo.audio.companions.app.logic.HelpLogic;
import jo.audio.companions.app.logic.LookLogic;
import jo.audio.companions.app.logic.OperationLogic;
import jo.audio.companions.app.logic.TeamLogic;
import jo.audio.companions.app.logic.WhoLogic;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioRequestBean;

public class DirectionStateHandler extends CompStateHandler
{
    @Override
    public void handleWelcome(BaseUserState s)
    {
        s.getApplication().log("Direction.handleWelcome");
        CompState state = (CompState)s;   
        if (!state.getUser().isNSEWQuestion())
        {
            s.getApplication().log("Direction.handleWelcome - not a question, redirecting to base state");
            state.setState(CompState.STATE_BASE);
            getApp().getStateHandler(state).handleWelcome(state);
            return;
        }
        s.getApplication().log("Direction.handleWelcome - repeating question");
        state.respond(CompanionsModelConst.TEXT_WELCOME_TO_COMPANIONS);
        // should be there from query
        state.addMediumPause();
        state.respond(state.getUser().getQuestionText());
        state.getResponse().setLinkOutName(state.resolve(CompanionsModelConst.TEXT_FORUM));
        state.getResponse().setLinkOutURL(
                "http://starlanes.freeforums.net/board/5/6-swords-discussion");
    }
    
    @Override
    public void handleOption(BaseUserState s)
    {
        if (s.getRequest().getRawText().toLowerCase().indexOf("north") >= 0)
            doNorth((CompState)s);
        else if (s.getRequest().getRawText().toLowerCase().indexOf("south") >= 0)
            doSouth((CompState)s);
        else if (s.getRequest().getRawText().toLowerCase().indexOf("east") >= 0)
            doEast((CompState)s);
        else if (s.getRequest().getRawText().toLowerCase().indexOf("west") >= 0)
            doWest((CompState)s);
        else
            doNorth((CompState)s);
    }

    public void doDir(CompState state, int dir)
    {
        OperationLogic.direction(state, dir);
        if (state.getContext().getUser().isNSEWQuestion())
            state.respond(state.getUser().getQuestionText());
        else
        {
            OperationLogic.cancel(state);
            state.setState(CompState.STATE_BASE);
            LookLogic.doLook(state);
        }
    }

    public void doNorth(CompState state)
    {
        doDir(state, CompOperationBean.NORTH);
    }

    public void doSouth(CompState state)
    {
        doDir(state, CompOperationBean.SOUTH);
    }

    public void doEast(CompState state)
    {
        doDir(state, CompOperationBean.EAST);
    }

    public void doWest(CompState state)
    {
        doDir(state, CompOperationBean.WEST);
    }

    public void doWho(CompState state)
    {
        WhoLogic.reportWho(state, true);
        state.respond(state.getUser().getQuestionText());
    }

    public void doInventory(CompState state, String whom, String type)
    {
        log("doInventory(whom=" + whom + ", type=" + type + ")");
        if ((whom == null) && (type != null))
            whom = type;
        TeamLogic.doInventory(state, whom);
        state.respond(state.getUser().getQuestionText());
    }

    public void doEquip(CompState state, String whom, String what)
    {
        log("doEquip(whom=" + whom + ", what=" + what + ")");
        TeamLogic.doEquip(state, whom, what);
        state.respond(state.getUser().getQuestionText());
    }

    public void doUnEquip(CompState state, String whom, String what)
    {
        log("doUnEquip(whom=" + whom + ", what=" + what + ")");
        TeamLogic.doUnEquip(state, whom, what);
        state.respond(state.getUser().getQuestionText());
    }

    public void doHelp(CompState state)
    {
        HelpLogic.helpQuestion(state);
    }

    public void doWhatIs(CompState state, String what)
    {
        doAbout(state, what);
    }

    public void doWhereIs(CompState state, String what)
    {
        doAbout(state, what);
    }

    public void doAbout(CompState state, String whom)
    {
        log("doAbout(whom=" + whom + ")");
        WhoLogic.doAbout(state, whom);
        state.getResponse().setLinkOutName(state.resolve(CompanionsModelConst.TEXT_FORUM));
        state.getResponse().setLinkOutURL(
                "http://starlanes.freeforums.net/board/5/6-swords-discussion");
        state.respond(state.getUser().getQuestionText());
    }

    public void doStop(CompState state, AudioRequestBean req)
    {
        OperationLogic.cancel(state);
        state.setState(CompState.STATE_BASE);
        state.respond(CompanionsModelConst.TEXT_CANCELING_QUESTION);
    }

    public void doCancel(CompState state, AudioRequestBean req)
    {
        OperationLogic.cancel(state);
        state.setState(CompState.STATE_BASE);
        state.respond(CompanionsModelConst.TEXT_CANCELING_QUESTION);
    }

    public void doDefault(CompState state)
    {
        if (!state.getUser().isNSEWQuestion())
        {
            state.getApplication().log("Direction.handleWelcome - not a question, redirecting to base state");
            state.setState(CompState.STATE_BASE);
            getApp().getStateHandler(state).handleWelcome(state);
            return;
        }
        HelpLogic.helpQuestion(state);
        //state.respond(state.getUser().getQuestionText());
    }
}
