package jo.audio.companions.app;

import jo.audio.companions.app.logic.HelpLogic;
import jo.audio.companions.app.logic.LookLogic;
import jo.audio.companions.app.logic.OperationLogic;
import jo.audio.companions.app.logic.TeamLogic;
import jo.audio.companions.app.logic.WhoLogic;
import jo.audio.companions.data.CompState;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioRequestBean;

public class QuestionStateHandler extends CompStateHandler
{
    @Override
    public void handleWelcome(BaseUserState s)
    {
        s.getApplication().log("Question.handleWelcome");
        CompState state = (CompState)s;   
        if (!state.getUser().isYNQuestion())
        {
            s.getApplication().log("Question.handleWelcome - not a question, redirecting to base state");
            state.setState(CompState.STATE_BASE);
            getApp().getStateHandler(state).handleWelcome(state);
            return;
        }
        s.getApplication().log("Question.handleWelcome - repeating question");
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
        if (s.getRequest().getRawText().toLowerCase().indexOf("yes") >= 0)
            doYes((CompState)s);
        else
            doNo((CompState)s);
    }

    public void doYes(CompState state)
    {
        OperationLogic.yes(state);
        if (state.getContext().getUser().isYNQuestion())
            state.respond(state.getUser().getQuestionText());
        else
        {
            state.setState(CompState.STATE_BASE);
            LookLogic.doLook(state);
        }
    }

    public void doNo(CompState state)
    {
        OperationLogic.no(state);
        if (state.getContext().getUser().isYNQuestion())
            state.respond(state.getUser().getQuestionText());
        else
        {
            state.setState(CompState.STATE_BASE);
            LookLogic.doLook(state);
        }
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
        if (!state.getUser().isYNQuestion())
        {
            state.getApplication().log("Question.doAbout - not a question, redirecting to base state");
            state.setState(CompState.STATE_BASE);
            return;
        }
        else
            state.respond(state.getUser().getQuestionText());
    }

    public void doStop(CompState state, AudioRequestBean req)
    {
        state.setState(CompState.STATE_BASE);
        getApp().getStateHandler(state).handle(state, req);
        state.respond(CompanionsModelConst.TEXT_CANCELING_QUESTION);
    }

    public void doCancel(CompState state, AudioRequestBean req)
    {
        state.setState(CompState.STATE_BASE);
        getApp().getStateHandler(state).handle(state, req);
        state.respond(CompanionsModelConst.TEXT_CANCELING_QUESTION);
    }

    public void doDefault(CompState state)
    {
        if (!state.getUser().isYNQuestion())
        {
            state.getApplication().log("Question.handleWelcome - not a question, redirecting to base state");
            state.setState(CompState.STATE_BASE);
            getApp().getStateHandler(state).handleWelcome(state);
            return;
        }
        HelpLogic.helpQuestion(state);
        //state.respond(state.getUser().getQuestionText());
    }
}
