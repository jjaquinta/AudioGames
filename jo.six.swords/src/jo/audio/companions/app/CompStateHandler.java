package jo.audio.companions.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.companions.app.logic.IntentLogic;
import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.logic.ItemLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioOptionBean;
import jo.audio.util.model.data.AudioResponseBean;
import jo.audio.util.model.data.IntentDefBean;
import jo.audio.util.model.data.IntentReqBean;
import jo.audio.util.model.data.PhraseSegmentBean;
import jo.audio.util.model.data.SlotSegmentBean;
import jo.audio.util.model.data.TextSegmentBean;
import jo.audio.util.model.data.UtteranceBean;
import jo.audio.util.model.logic.ModelToExamples;
import jo.audio.util.state.logic.StateHandler;
import jo.util.utils.ArrayUtils;
import jo.util.utils.obj.StringUtils;

public abstract class CompStateHandler extends StateHandler
{
    @Override
    public void addExpectedIntents(BaseUserState s)
    {
        CompState state = (CompState)s;
        state.getResponse().getExpectedContexts().addAll(IntentLogic.getValidIntents(state));
        state.getResponse().getSuggestions().addAll(IntentLogic.getSuggestions(state));
        if ((state.getResponse().getOutputSpeechText().indexOf(CompApplicationHandler.mMoreSound) >= 0)
                || ((state.getMore() != null) && (state.getMore().size() > 0)))
            state.getResponse().getSuggestions().add("more");
        if (state.getContext().getRoom() != null)
            if (state.getContext().getRoom().getType().equals(CompRoomBean.TYPE_ITEM_SHOP))
                addShopOptions(state);
            else if (state.getContext().getRoom().getType().equals(CompRoomBean.TYPE_FIGHTERS_GUILD))
                addHiringOptions(state);
    }
    
    private void addShopOptions(CompState state)
    {
        JSONObject params = state.getContext().getRoom().getParams();
        JSONArray items = (JSONArray)params.get("items");
        if (items != null)
            for (int i = 0; i < items.size(); i++)
            {
                String id = (String)items.get(i);
                CompItemTypeBean type = ItemLogic.getItemType(id);
                AudioOptionBean option = new AudioOptionBean();
                option.getKeys().add("buy "+type.getName());
                option.setTitle(type.getName());
                StringBuffer desc = new StringBuffer(type.getName());
                if (type.getCount() > 1)
                    desc.insert(0, type.getCount()+" ");
                if (type.getCost() > 0)
                    desc.append(" for "+(int)type.getCost());
                option.setDescription(desc.toString());
                state.getResponse().getOptions().add(option);
            }
        if (state.getResponse().getOptions().size() > 0)
        {
            state.getResponse().setOptionMode(AudioResponseBean.OPTION_LIST);
            state.getResponse().setOptionTitle(state.resolve(CompanionsModelConst.TEXT_OPTION_BUY));
        }
    }
    
    private void addHiringOptions(CompState state)
    {
        JSONObject params = state.getContext().getRoom().getParams();
        JSONArray hires = (JSONArray)params.get("hires");
        for (int i = 0; i < hires.size(); i++)
        {
            JSONObject json = (JSONObject)hires.get(i);
            CompCompanionBean hire = new CompCompanionBean();
            hire.fromJSON(json);
            if (state.getUser().getCompanion(hire.getID()) != null)
                continue;
            AudioOptionBean option = new AudioOptionBean();
            String name = state.expandInserts(hire.getName());
            option.getKeys().add("hire "+name);
            option.setTitle(name);
            StringBuffer desc = new StringBuffer(name);
            desc.append(", "+hire.getHitPoints()+"hp");
            desc.append(", S:"+hire.getSTRModified());
            desc.append(", C:"+hire.getCONModified());
            desc.append(", D:"+hire.getDEXModified());
            desc.append(", I:"+hire.getINTModified());
            desc.append(", W:"+hire.getWISModified());
            desc.append(", C:"+hire.getCHAModified());
            option.setDescription(desc.toString());
            state.getResponse().getOptions().add(option);
        }
        if (state.getResponse().getOptions().size() > 0)
        {
            state.getResponse().setOptionMode(AudioResponseBean.OPTION_LIST);
            state.getResponse().setOptionTitle(state.resolve(CompanionsModelConst.TEXT_OPTION_HIRE));
        }
    }

    @Override
    public void addReprompts(BaseUserState state)
    {
        String lang = state.getRequest().getLanguage();
        Set<String> actions = IntentLogic.getExampledIntents((CompState)state);
        if ((actions == null) || (actions.size() == 0))
            return;
        String intentID = ArrayUtils.getRandom(actions, BaseUserState.RND);
        IntentDefBean intent  = state.getApplication().getModel().getIntent(intentID);
        List<String> examples = intent.getWorkedExamples().get(lang);
        if (examples == null)
        {
            examples = ModelToExamples.createExamples(getApp().getModel(), intent, lang);
            intent.getWorkedExamples().put(lang, examples);
        }
        if (examples.size() > 0)
            state.reprompt("TRY_SAYING_XXX", examples.get(BaseUserState.RND.nextInt(examples.size())));
    }
    
    // re-extract text from single any slot
    public String reparseAny(BaseUserState state)
    {
        String rawText = state.getRequest().getRawText();
        if (StringUtils.isTrivial(rawText))
            return null;
        //log("Reparsing '"+rawText+"'");
        IntentReqBean intent = state.getRequest().getIntent();
        IntentDefBean def = getApp().getModel().getIntent(intent.getIntentID());
        List<UtteranceBean> utts = def.getUtterances(state.getRequest().getLanguage());
        for (UtteranceBean u : utts)
        {
            //log("Trying '"+u+"'");
            String raw = rawText;
            List<PhraseSegmentBean> phrases = u.getPhrase();
            if (phrases.get(0) instanceof TextSegmentBean)
            {
                String prefix = ((TextSegmentBean)phrases.get(0)).getText();
                if (!raw.toLowerCase().startsWith(prefix.toLowerCase()))
                {
                    //log("'"+raw.toLowerCase()+"' not prefixed with '"+prefix.toLowerCase()+"'");
                    continue;
                }
                raw = raw.substring(prefix.length()).trim();
            }
            if (phrases.get(phrases.size() - 1) instanceof TextSegmentBean)
            {
                String suffix = ((TextSegmentBean)phrases.get(phrases.size() - 1)).getText();
                if (!raw.toLowerCase().endsWith(suffix.toLowerCase()))
                {
                    //log("'"+raw.toLowerCase()+"' not prefixed with '"+suffix.toLowerCase()+"'");
                    continue;
                }
                raw = raw.substring(raw.length() - suffix.length()).trim();
            }
            log("Reparsed slot as '"+raw+"'");
            return raw;
        }
        return null;
    }
    
    // re-extract text from single any slot
    public Map<String,String> reparseSlots(BaseUserState state)
    {
        String raw = state.getRequest().getRawText();
        if (StringUtils.isTrivial(raw))
            return null;
        log("reparsing '"+raw+"'");
        Map<String,String> slots = new HashMap<String, String>();
        IntentReqBean intent = state.getRequest().getIntent();
        IntentDefBean def = getApp().getModel().getIntent(intent.getIntentID());
        List<UtteranceBean> utts = def.getUtterances(state.getRequest().getLanguage());
        for (UtteranceBean u : utts)
        {
            log("testing '"+u+"'");
            List<PhraseSegmentBean> phrases = u.getPhrase();
            String inbuf = raw;
            slots.clear();
            boolean match = true;
            for (int i = 0; i < phrases.size(); i++)
            {
                PhraseSegmentBean p = phrases.get(i);
                if (p instanceof TextSegmentBean)
                {
                    String prefix = ((TextSegmentBean)p).getText();
                    if (!inbuf.toLowerCase().startsWith(prefix.toLowerCase()))
                    {
                        log("no match of '"+inbuf+"' against "+p);
                        match = false;
                        break;
                    }
                    inbuf = inbuf.substring(prefix.length()).trim();
                }
                else if (p instanceof SlotSegmentBean)
                {
                    if (i + 1 == phrases.size())
                    {   // last slot
                        if (StringUtils.isTrivial(inbuf))
                        {
                            log("no match  with "+p+", nothing left");
                            match = false;
                            break; // nothing in slot
                        }
                        slots.put(((SlotSegmentBean)p).getSlot().getName(), inbuf);
                        inbuf = "";
                    }
                    else
                    {
                        if (!(phrases.get(i+1) instanceof TextSegmentBean))
                        {
                            log("no match of '"+inbuf+"' against "+p+", double slots not supported.");
                            match = false;
                            break; // we can't handle two slots without spacing text
                        }
                        TextSegmentBean p2 = (TextSegmentBean)phrases.get(i+1);
                        int o = inbuf.toLowerCase().indexOf(p2.getText().toLowerCase());
                        if (o < 0)
                        {
                            log("no match of '"+inbuf+"' against "+p+", no terminal text of "+phrases.get(i+1));
                            match = false;
                            break; // no bounding text
                        }
                        slots.put(((SlotSegmentBean)p).getSlot().getName(), inbuf.substring(0, o).trim());
                        inbuf = inbuf.substring(o);
                    }
                }
                log("matched '"+inbuf+"' with "+p+".");
            }
            if ((inbuf.length() == 0) && match)
                return slots;
        }
        return null;
    }
}
