package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.logic.CompOperationLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.BeanUtils;
import jo.util.utils.obj.IntegerUtils;

public class HelpLogic
{

    public static void helpBase(CompState state)
    {
        state.getApplication().log("Last intent: '"+state.getLastIntent()+"'");
        if (state.getLastIntent() != null)
            switch (state.getLastIntent())
            {
                case CompanionsModelConst.INTENT_ACTIVATE:
                    state.respond(CompanionsModelConst.TEXT_HELP_BASE_ACTIVATE);
                    return;
                case CompanionsModelConst.INTENT_BUY:
                    state.respond(CompanionsModelConst.TEXT_HELP_BASE_BUY);
                    return;
                case CompanionsModelConst.INTENT_ENTER:
                    state.respond(CompanionsModelConst.TEXT_HELP_BASE_ENTER);
                    return;
                case CompanionsModelConst.INTENT_EQUIP:
                    state.respond(CompanionsModelConst.TEXT_HELP_BASE_EQUIP);
                    return;
                case CompanionsModelConst.INTENT_FIRE:
                    state.respond(CompanionsModelConst.TEXT_HELP_BASE_FIRE);
                    return;
                case CompanionsModelConst.INTENT_HIRE:
                    state.respond(CompanionsModelConst.TEXT_HELP_BASE_HIRE);
                    return;
                case CompanionsModelConst.INTENT_INVENTORY:
                    state.respond(CompanionsModelConst.TEXT_HELP_BASE_INVENTORY);
                    return;
                case CompanionsModelConst.INTENT_SELL:
                    state.respond(CompanionsModelConst.TEXT_HELP_BASE_SELL);
                    return;
                case CompanionsModelConst.INTENT_UNEQUIP:
                    state.respond(CompanionsModelConst.TEXT_HELP_BASE_UNEQUIP);
                    return;
                case CompanionsModelConst.INTENT_WHO:
                    state.respond(CompanionsModelConst.TEXT_HELP_BASE_WHO);
                    return;
            }
        state.respond(CompanionsModelConst.TEXT_HELP_BASE_ONE);
        state.setMore(CompanionsModelConst.TEXT_HELP_BASE_TWO, CompanionsModelConst.TEXT_HELP_BASE_THREE, 
                CompanionsModelConst.TEXT_HELP_BASE_FOUR, CompanionsModelConst.TEXT_HELP_BASE_FIVE);
    }

    public static void helpCombat(CompState state)
    {
        state.respond(CompanionsModelConst.TEXT_HELP_COMBAT_ONE);
        state.setMore(CompanionsModelConst.TEXT_HELP_COMBAT_TWO);
    }

    public static void helpQuestion(CompState state)
    {
        if (state.getUser().isNSEWQuestion())
            state.respond(CompanionsModelConst.TEXT_HELP_QUESTION_TWO);
        else
            state.respond(CompanionsModelConst.TEXT_HELP_QUESTION_ONE);
        state.respond(state.getUser().getQuestionText());
    }

    public static void news(CompState state)
    {
        Map<Integer, String> news = getNews();
        Integer[] items = news.keySet().toArray(new Integer[0]);
        Arrays.sort(items);
        List<AudioMessageBean> msgs = new ArrayList<>();
        for (int i = items.length - 1; i >= 0; i--)
        {
            String newsItem = news.get(items[i]);
            newsItem = BeanUtils.insertValues(newsItem, state);
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_SENTANCE, newsItem));
        }
        if (msgs.size() == 0)
            state.respond(CompanionsModelConst.TEXT_THERE_IS_NO_NEWS);
        else
        {
            state.respond(msgs.get(0));
            msgs.remove(0);
            if (msgs.size() > 0)
                state.setMore(msgs.toArray(new AudioMessageBean[0]));
            OperationLogic.consumeNews(state, items[items.length-1]);
        }
    }

    public static Map<Integer, String> getNews()
    {
        CompOperationBean op = new CompOperationBean();
        op.setOperation(CompOperationBean.GETNEWS);
        CompContextBean context = CompOperationLogic.operate(op);
        Map<Integer,String> news = new HashMap<>();
        for (String key : context.getTextModel().keySet())
            news.put(IntegerUtils.parseInt(key), context.getTextModel().getString(key));
        return news;
    }

    public static void addNewNews(CompState state)
    {
        Map<Integer, String> news = getNews();
        boolean anyNew = false;
        for (Integer i : news.keySet())
            if (i > state.getUser().getLastMessage())
            {
                anyNew = true;
                break;
            }
        if (anyNew)
            if (!state.prompt(CompanionsModelConst.INTENT_NEWS, CompanionsModelConst.TEXT_LONG_NEWS))
                state.respond(CompanionsModelConst.TEXT_SHORT_NEWS);
    }
}
