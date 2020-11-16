package jo.audio.util.model.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import jo.audio.util.model.data.IntentDefBean;
import jo.audio.util.model.data.InteractionModelBean;
import jo.audio.util.model.data.PhraseSegmentBean;
import jo.audio.util.model.data.SlotBean;
import jo.audio.util.model.data.SlotSegmentBean;
import jo.audio.util.model.data.TextSegmentBean;
import jo.audio.util.model.data.UtteranceBean;

public class ModelToRegex
{
    public static Map<String,List<String>> createRegex(InteractionModelBean model, String lang, String target)
    {
        Map<String,List<String>> regexes = new HashMap<>();
        for (IntentDefBean intent : model.getIntents().values())
            regexes.put(intent.getIntent(), createRegex(model, intent, lang, target));
        return regexes;
    }
    
    public static List<String> createRegex(InteractionModelBean model, IntentDefBean intent, String lang, String target)
    {
        List<String> regexes = new ArrayList<>();
        for (UtteranceBean utterance : intent.getUtterances().get(lang))
        {
            if ((target != null) && utterance.getTags().containsKey("only") && !target.equalsIgnoreCase(utterance.getTags().getProperty("only")))
                continue;
            regexes.add(createRegex(model, lang, utterance));
        }
        return regexes;
    }
    
    public static String createRegex(InteractionModelBean model, String lang, UtteranceBean utterance)
    {
        StringBuffer regex = new StringBuffer();
        for (int i = 0; i < utterance.getPhrase().size(); i++)
        {
            if ((i > 0) && !regex.toString().endsWith("\\s+"))
                regex.append("\\s+");
            PhraseSegmentBean seg = utterance.getPhrase().get(i);
            if (seg instanceof TextSegmentBean)
            {
                TextSegmentBean tseg = (TextSegmentBean)seg;
                appendLiteral(regex, tseg.getText());
            }
            else
            {
                SlotSegmentBean sseg = (SlotSegmentBean)seg;
                regex.append("(");
                if (sseg.getSlot().getClassification() == SlotBean.CUSTOM)
                {
                    String dictName = sseg.getSlot().getDictionary();
                    if (dictName.equalsIgnoreCase("SearchQuery") || (dictName.equalsIgnoreCase("anything")))
                        appendAny(utterance, regex, i);
                    else
                    {
                        Map<String,List<String>> dict = model.getDictionary(dictName, lang);
                        if (dict.size() > 24)
                            appendAny(utterance, regex, i);
                        else
                        {
                            // blank OK = empty slot
                            for (String word : dict.keySet())
                            {
                                regex.append("|");
                                appendLiteral(regex, word);
                            }
                        }
                    }
                }
                else if (sseg.getSlot().getClassification() == SlotBean.BUILT_IN)
                {
                    if ("NUMBER".equals(sseg.getSlot().getType()))
                    {
                        regex.append("\\d*");
                    }
                    else if ("US_FIRST_NAME".equals(sseg.getSlot().getType()) || "FirstName".equals(sseg.getSlot().getType())
                            || "US_CITY".equals(sseg.getSlot().getType()))
                    {
                        if (i + 1 >= utterance.getPhrase().size())
                            regex.append("[a-zA-Z ']*");
                        else
                            regex.append("[a-zA-Z']*");
                    }
                    else
                        throw new IllegalArgumentException("Unhandled built in value, slot="+sseg.getSlot().getName()+", type="+sseg.getSlot().getType());
                }
                regex.append(")");
            }
        }
        return regex.toString();
    }

    private static void appendAny(UtteranceBean utterance, StringBuffer regex,
            int i)
    {
        if (i + 1 >= utterance.getPhrase().size())
            regex.append(".*");
        else
            regex.append("[^ ]*");
    }

    private static void appendLiteral(StringBuffer regex, String text)
    {
        for (StringTokenizer st = new StringTokenizer(text, " \t\n\r\f"); st.hasMoreTokens(); )
        {
            String chunk = st.nextToken();
            if (chunk.endsWith("."))
                regex.append(chunk.substring(0,  1));
            else
                regex.append(Pattern.quote(chunk));
            if (st.hasMoreTokens())
                regex.append("\\s+");
        }
    }
    
    /*
    private static String toSimpleString(String text)
    {
        StringBuffer sb = new StringBuffer();
        for (char c : text.toCharArray())
            if (Character.isWhitespace(c) || Character.isAlphabetic(c) || Character.isDigit(c))
                sb.append(c);
        return sb.toString();
    }
    */
}
