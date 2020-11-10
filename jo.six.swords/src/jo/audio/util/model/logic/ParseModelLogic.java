package jo.audio.util.model.logic;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.util.model.data.IntentDefBean;
import jo.audio.util.model.data.InteractionModelBean;
import jo.audio.util.model.data.SlotBean;
import jo.audio.util.model.data.SlotSegmentBean;
import jo.audio.util.model.data.TextSegmentBean;
import jo.audio.util.model.data.UtteranceBean;

public class ParseModelLogic
{
    public static InteractionModelBean parse(String uri) throws IOException
    {
        JSONObject rawModel = JSONUtils.readJSON(uri);
        if (rawModel == null)
            throw new IOException("No model found at '"+uri+"'");
        return parse(rawModel);
    }
    public static InteractionModelBean parse(JSONObject rawModel)
    {
        InteractionModelBean model = new InteractionModelBean();
        JSONUtils.stripComments(rawModel, "@@", null);
        model.setRawModel(rawModel);
        
        parsePreamble(model);
        parseDictionaries(model);
        parseIntents(model);
        parseText(model);
        parseDefaultUtterances(model);
        
        return model;
    }
    
    private static void parseText(InteractionModelBean model)
    {
        JSONObject text = JSONUtils.getObject(model.getRawModel(), "text");
        if (text == null)
            throw new IllegalArgumentException("Cannot find text!");
        for (String lang : text.keySet())
        {
            JSONObject lookup = JSONUtils.getObject(text, lang);
            for (String key : lookup.keySet())
            {
                Object o = lookup.get(key);
                if (o instanceof String)
                    model.addText(lang, key, (String)o);
                else if (o instanceof JSONArray)
                    for (Object val : (JSONArray)o)
                        model.addText(lang, key, (String)val);
                else
                    throw new IllegalArgumentException("Expected string or array in text."+lang+"."+key);
            }
        }
    }
    
    private static void parsePreamble(InteractionModelBean model)
    {
        JSONArray langs = JSONUtils.getArray(model.getRawModel(), "supportedLanguages");
        if (langs == null)
            throw new IllegalArgumentException("Cannot find supportedLanguages!");
        for (int i = 0; i < langs.size(); i++)
            model.getSupportedLanguages().add((String)langs.get(i));
        JSONObject displayName = JSONUtils.getObject(model.getRawModel(), "displayName");
        if (displayName == null)
            throw new IllegalArgumentException("Cannot find displayName!");
        for (String key : displayName.keySet())
        {
            String val = (String)displayName.get(key);
            model.getDisplayName().put(key, val);
        }
        model.setAppName(JSONUtils.getString(model.getRawModel(), "appName"));
        if (model.getAppName() == null)
            throw new IllegalArgumentException("Cannot find appName!");
        JSONObject invocationName = JSONUtils.getObject(model.getRawModel(), "invocationName");
        if (invocationName == null)
            throw new IllegalArgumentException("Cannot find invocationName!");
        for (String key : invocationName.keySet())
        {
            String val = (String)invocationName.get(key);
            model.getInvocationName().put(key, val);
        }
        model.setURL(JSONUtils.getString(model.getRawModel(), "url"));
        if (model.getURL() == null)
            throw new IllegalArgumentException("Cannot find url!");
        model.setWebsiteURL(JSONUtils.getString(model.getRawModel(), "website"));
        model.setTermsURL(JSONUtils.getString(model.getRawModel(), "termsAndConditions"));
        model.setPrivacyURL(JSONUtils.getString(model.getRawModel(), "privacyStatement"));
        JSONObject description = JSONUtils.getObject(model.getRawModel(), "description");
        if (description == null)
            System.err.println("Cannot find description!");
        else
            for (String key : description.keySet())
            {
                String val = (String)description.get(key);
                model.getDescription().put(key, val);
            }
        JSONObject keywords = JSONUtils.getObject(model.getRawModel(), "keywords");
        if (keywords == null)
            System.err.println("Cannot find keywords!");
        else
            for (String key : keywords.keySet())
            {
                String val = (String)keywords.get(key);
                model.getKeywords().put(key, val);
            }
    }
    
    private static void parseDictionaries(InteractionModelBean model)
    {
        JSONObject dictionaries = (JSONObject)model.getRawModel().get("dictionaries");
        if (dictionaries == null)
            return; // no dictionaries
        for (Object key : dictionaries.keySet())
        {
            String name = (String)key;
            if (model.getDictionary(name) == null)
                parseDictionary(model, dictionaries, name);
        }
    }

    private static void parseDictionary(InteractionModelBean model,
            JSONObject dictionaries, String name)
    {
        JSONArray ws = (JSONArray)dictionaries.get(name);
        String lang = "en_US";
        int o = name.indexOf("_");
        if (o >= 0)
        {
            lang = name.substring(o + 1);
            name = name.substring(0, o);
        }
        if (ws == null)
            ws = (JSONArray)dictionaries.get(name);
        if (ws == null)
            System.err.println("Cannot find dictionary \""+name+"\" ("+lang+")");
        for (Object w : ws)
        {
            if (w instanceof JSONArray)
            {
                JSONArray a = (JSONArray)w;
                String k = a.get(0).toString();
                model.addToDictionary(name, lang, k, k);
                for (int i = 1; i < a.size(); i++)
                    model.addToDictionary(name, lang, k, a.get(i).toString());
            }
            else
            {
                String valueStr = w.toString();
                if (valueStr.startsWith("~"))
                {
                    String subName = valueStr.substring(1);
                    if (!model.getDictionaries().containsKey(subName))
                        parseDictionary(model, dictionaries, subName+"_"+lang);
                    Map<String,List<String>> words = model.getDictionary(subName, lang);
                    for (String k : words.keySet())
                    {
                        List<String> vs = words.get(k);
                        for (String v : vs)
                            model.addToDictionary(name, lang, k, v);
                    }
                }
                else
                {
                    StringTokenizer st = new StringTokenizer(valueStr, ":");
                    String k = st.nextToken();
                    model.addToDictionary(name, lang, k, k);
                    while (st.hasMoreTokens())
                        model.addToDictionary(name, lang, k, st.nextToken());
                }
            }
        }
    }
    
    private static void parseDefaultUtterances(InteractionModelBean model)
    {
        parseDefaultUtterances(model, "en_US", BUILT_IN_INTENTS_EN_US);
        parseDefaultUtterances(model, "de_DE", BUILT_IN_INTENTS_DE_DE);
    }
    
    private static void parseDefaultUtterances(InteractionModelBean model, String lang, String[] utterances)
    {
        if (!model.getSupportedLanguages().contains(lang))
            return;
        for (String inbuf : utterances)
            try
            {
                int o = inbuf.indexOf('\t');
                if (o < 0)
                    throw new IllegalArgumentException();
                String intent = inbuf.substring(0, o);
                String utterance = inbuf.substring(o + 1);
                IntentDefBean i = model.getIntent(intent);
                if (i != null)
                {
                    UtteranceBean u = parseUtterance(i, utterance);
                    i.addUtterance(lang, u);
                }
            }
            catch (IllegalArgumentException e)
            {
                // NOOP, in case intent is not there
            }
    }
    
    private static UtteranceBean parseUtterance(IntentDefBean intent, String inbuf)
    {
        UtteranceBean utterance = new UtteranceBean();
        utterance.setIntent(intent);
        int o = inbuf.indexOf('[');
        if (o >= 0)
        {
            String tags = inbuf.substring(o + 1);
            inbuf = inbuf.substring(0, o);
            for (StringTokenizer st = new StringTokenizer(tags, ",]"); st.hasMoreTokens(); )
            {
                String kv = st.nextToken();
                int o2 = kv.indexOf('=');
                if (o2 < 0)
                    utterance.getTags().put(kv, "true");
                else
                    utterance.getTags().put(kv.substring(0, o), kv.substring(o + 1));
            }
        }
        utterance.setRawUtterance(inbuf);
        while (inbuf.length() > 0)
            if (inbuf.charAt(0) == '{')
            {
                int end = inbuf.indexOf('}');
                if (end < 0)
                    throw new IllegalArgumentException("Can't find end of slot '"+inbuf+"'");
                String slotPhrase = inbuf.substring(1, end);
                inbuf = inbuf.substring(end + 1).trim();
                SlotSegmentBean slotSeg = new SlotSegmentBean();
                for (SlotBean slot : utterance.getIntent().getSlots())
                    if (slot.getName().equalsIgnoreCase(slotPhrase))
                    {
                        slotSeg.setSlot(slot);
                        break;
                    }
                if (slotSeg.getSlot() == null)
                    throw new IllegalArgumentException("Unknown slot '"+slotPhrase+"' in '"+inbuf+"' of intent "+intent.getIntent());
                utterance.getPhrase().add(slotSeg);
            }
            else
            {
                int end = inbuf.indexOf('{');
                if (end < 0)
                    end = inbuf.length();
                TextSegmentBean textSeg = new TextSegmentBean();
                textSeg.setText(inbuf.substring(0, end).trim().toLowerCase());
                inbuf = inbuf.substring(end).trim();
                utterance.getPhrase().add(textSeg);
            }
        return utterance;
    }

    public static void parseIntents(InteractionModelBean model)
    {
        JSONArray intents;
        try
        {
            intents = (JSONArray)model.getRawModel().get("intents");
        }
        catch (ClassCastException e)
        {
                throw new IllegalArgumentException("Expected intents JSON entity to be an array.");
        }
        if (intents == null)
            throw new IllegalArgumentException("No intents member of intent schema");
        for (Object intent : intents)
        {
            IntentDefBean i = new IntentDefBean();
            JSONObject jIntent;
            try
            {
                jIntent = (JSONObject)intent;
            }
            catch (ClassCastException e)
            {
                throw new IllegalArgumentException("Expected intent array JSON entity to be an object."+intent);
            }
            try
            {
                i.setIntent((String)jIntent.get("intent"));
            }
            catch (ClassCastException e)
            {
                throw new IllegalArgumentException("Expected intent definition to be a string."+jIntent);
            }
            i.setTarget((String)jIntent.get("target"));
            i.setTargetLang((String)jIntent.get("target_lang"));
            if (jIntent.containsKey("context"))
                i.setContext(jIntent.getString("context"));
            else
                i.setContext(i.getIntent().toLowerCase());
            parseSlots(model, i, jIntent);
            parseUtterances(model, i, jIntent);
            parseExamples(i, jIntent);
            model.setIntent(i.getIntent(), i);
        }
    }

    private static void parseExamples(IntentDefBean i, JSONObject jIntent)
    {
        JSONObject allExamples;
        try
        {
            allExamples = (JSONObject)(jIntent.get("examples"));
        }
        catch (ClassCastException e)
        {
            throw new IllegalArgumentException("Expected examples JSON entity to be an object."+jIntent.toString());
        }
        if (allExamples == null)
            return;
        for (String lang : allExamples.keySet())
        {
            JSONArray examples;
            try
            {
                examples = (JSONArray)(allExamples.get(lang));
            }
            catch (ClassCastException e)
            {
                throw new IllegalArgumentException("Expected "+lang+" JSON entity to be an array."+jIntent.toString());
            }
            for (Object example : examples)
                i.addExample(lang, example.toString());
        }
    }

    private static void parseUtterances(InteractionModelBean model,
            IntentDefBean i, JSONObject jIntent)
    {
        JSONObject allUtterances;
        try
        {
            allUtterances = (JSONObject)(jIntent.get("utterances"));
        }
        catch (ClassCastException e)
        {
            throw new IllegalArgumentException("Expected utterances JSON entity to be an object."+jIntent.toString());
        }
        if (allUtterances == null)
            return;
        for (String lang : allUtterances.keySet())
        {
            JSONArray utterances;
            try
            {
                utterances = (JSONArray)(allUtterances.get(lang));
            }
            catch (ClassCastException e)
            {
                throw new IllegalArgumentException("Expected "+lang+" JSON entity to be an array."+jIntent.toString());
            }
            if (utterances == null)
                throw new IllegalArgumentException("Could not find "+lang+" utterances for intent "+i.getIntent()+". "+jIntent.toString());
            for (Object utterance : utterances)
            {
                UtteranceBean u = parseUtterance(i, utterance.toString());
                model.getUtterances().add(u);
                i.addUtterance(lang, u);
            }
        }
    }

    private static JSONArray parseSlots(InteractionModelBean model, IntentDefBean i, JSONObject jIntent)
    {
        JSONArray slots;
        try
        {
            slots = (JSONArray)(jIntent.get("slots"));
        }
        catch (ClassCastException e)
        {
            throw new IllegalArgumentException("Expected slots JSON entity to be an array."+jIntent.toString());
        }
        if (slots == null)
            throw new IllegalArgumentException("Expected slots entity ."+jIntent.toString());
        for (Object slot : slots)
        {
            SlotBean s = new SlotBean();
            try
            {
                s.setName((String)((JSONObject)slot).get("name"));
            }
            catch (ClassCastException e)
            {
                throw new IllegalArgumentException("Expected name definition to be a string."+slot.toString());
            }
            try
            {
                s.setType((String)((JSONObject)slot).get("type"));
                if (((JSONObject)slot).containsKey("hint"))
                    s.setHint((String)((JSONObject)slot).get("hint"));
                if ("CUSTOM".equals(s.getType()))
                {
                    s.setClassification(SlotBean.CUSTOM);
                    s.setDictionary((String)((JSONObject)slot).get("dictionary"));
                    if (s.getDictionary() == null)
                        throw new IllegalArgumentException("Expected dictionary for custom type "+slot.toString());
                    if (model.getDictionary(s.getDictionary()) == null)
                        throw new IllegalArgumentException("No dictionary present for custom type "+slot.toString());
                    model.getCustomSlots().add(s.getDictionary());
                }
                else
                {
                    s.setClassification(SlotBean.BUILT_IN);
                }
            }
            catch (ClassCastException e)
            {
                throw new IllegalArgumentException("Expected type definition to be a string."+slot.toString());
            }
            i.getSlots().add(s);
        }
        return slots;
    }
    
    public static final String[] BUILT_IN_INTENTS_EN_US = {
        "CancelIntent\tcancel",
        "CancelIntent\tnever mind",
        "CancelIntent\tforget it",
        "HelpIntent\thelp",
        "HelpIntent\thelp me",
        "HelpIntent\tcan you help me",
        "NextIntent\tnext",
        "NextIntent\tskip",
        "NextIntent\tskip forward",
        "NoIntent\tno",
        "NoIntent\tno thanks",
        "PauseIntent\tpause",
        "PauseIntent\tpause that",
        "PreviousIntent\tgo back",
        "PreviousIntent\tskip back",
        "PreviousIntent\tback up",
        "RepeatIntent\trepeat",
        "RepeatIntent\tsay that again",
        "RepeatIntent\trepeat that",
        "ResumeIntent\tresume",
        "ResumeIntent\tcontinue",
        "ResumeIntent\tkeep going",
        "StartOverIntent\tstart over",
        "StartOverIntent\trestart",
        "StartOverIntent\tstart again",
        "StopIntent\tstop",
        "StopIntent\toff",
        "StopIntent\tshut up",
        "YesIntent\tyes",
        "YesIntent\tyes please",
        "YesIntent\tsure",
    };
    
    public static final String[] BUILT_IN_INTENTS_DE_DE = {
        "CancelIntent\tabbrechen",
        "CancelIntent\tabbreche",
        "CancelIntent\tvergiss es",
        "HelpIntent\tHilfe",
        "HelpIntent\tHilf mir",
        "HelpIntent\tKannst du mir helfen ",
        "NextIntent\tweiter",
        "NextIntent\tbitte geh zum folgenden kapitel",
        "NextIntent\tBitte geh zum n�chsten",
        "NoIntent\tNein",
        "NoIntent\tNein danke",
        "PauseIntent\tpause",
        "PauseIntent\tunterbrechung",
        "PauseIntent\tpausieren",
        "PreviousIntent\tvorheriges",
        "PreviousIntent\tspiele das letzte",
        "RepeatIntent\twiederhole",
        "RepeatIntent\twiederhole das",
        "RepeatIntent\tsag das noch mal ",
        "ResumeIntent\tmach weiter",
        "ResumeIntent\tweiterlesen",
        "StartOverIntent\tvon vorne",
        "StartOverIntent\tneustart",
        "StartOverIntent\tbeginne erneut",
        "StopIntent\tstopp",
        "StopIntent\thör endlich auf",
        "StopIntent\taufhören",
        "YesIntent\tJa",
        "YesIntent\tJa bitte",
    };
}
