package jo.audio.util.model.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jo.audio.util.model.data.IntentDefBean;
import jo.audio.util.model.data.InteractionModelBean;
import jo.audio.util.model.data.PhraseSegmentBean;
import jo.audio.util.model.data.SlotBean;
import jo.audio.util.model.data.SlotSegmentBean;
import jo.audio.util.model.data.TextSegmentBean;
import jo.audio.util.model.data.UtteranceBean;

public class ModelToExamples
{
    private static Random RND = new Random(0);
    
    public static List<String> createExamples(InteractionModelBean model, String lang)
    {
        List<String> examples = new ArrayList<>();
        for (IntentDefBean intent : model.getIntents().values())
            examples.addAll(createExamples(model, intent, lang));
        return examples;
    }
    
    public static List<String> createExamples(InteractionModelBean model, IntentDefBean intent, String lang)
    {
        List<String> examples = new ArrayList<>();
        for (UtteranceBean utterance : intent.getUtterances().get(lang))
            examples.addAll(createExamples(model, lang, utterance));
        return examples;
    }
    
    public static List<String> createExamples(InteractionModelBean model, String lang, UtteranceBean utterance)
    {
        List<String> examples = new ArrayList<>();
        StringBuffer example = new StringBuffer();
        createExamples(examples, example, model, lang, utterance, 0);
        return examples;
    }
    
    private static void createExamples(List<String> examples, StringBuffer example, InteractionModelBean model, String lang, UtteranceBean utterance, int idx)
    {
        if (idx == utterance.getPhrase().size())
        {
            examples.add(sanitize(example.toString()));
            return;
        }
        int len = example.length();
        if ((len > 0) && (example.charAt(len - 1) != ' '))
            example.append(' ');
        PhraseSegmentBean seg = utterance.getPhrase().get(idx);
        if (seg instanceof TextSegmentBean)
        {
            TextSegmentBean tseg = (TextSegmentBean)seg;
            example.append(tseg.getText());
            createExamples(examples, example, model, lang, utterance, idx + 1);
        }
        else
        {
            SlotSegmentBean sseg = (SlotSegmentBean)seg;
            if (sseg.getSlot().getClassification() == SlotBean.CUSTOM)
            {
                if (sseg.getSlot().getHint() != null)
                    example.append(sseg.getSlot().getHint());
                else 
                {
                    String dictName = sseg.getSlot().getDictionary();
                    Map<String,List<String>> dict = model.getDictionary(dictName, lang);
                    addValues(examples, example, model, lang, utterance, idx, len, dict.keySet());
                }
            }
            else if (sseg.getSlot().getClassification() == SlotBean.BUILT_IN)
            {
                String type = sseg.getSlot().getType();
                if ("NUMBER".equals(type))
                    addValues(examples, example, model, lang, utterance, idx, len, SAMPLE_NUMBER);
                else if ("US_FIRST_NAME".equals(type) || "FirstName".equals(type))
                    addValues(examples, example, model, lang, utterance, idx, len, SAMPLE_US_FIRST_NAME);
                else if ("US_CITY".equals(type))
                    addValues(examples, example, model, lang, utterance, idx, len, SAMPLE_US_CITY);
                else
                    throw new IllegalArgumentException("Unhandled build in value, slot="+sseg.getSlot().getName()+", type="+type);
            }
        }
        example.setLength(len);
    }

    private static void addValues(List<String> examples, StringBuffer example,
            InteractionModelBean model, String lang, UtteranceBean utterance, int idx, int len, Collection<String> values)
    {
        ArrayList<String> selectValues = new ArrayList<>();
        selectValues.addAll(values);
        while (selectValues.size() > 20)
            selectValues.remove(RND.nextInt(selectValues.size()));
        for (String value : selectValues)
        {
            if (!example.toString().endsWith(" "))
                example.append(" ");
            example.append(value);
            createExamples(examples, example, model, lang, utterance, idx + 1);
            example.setLength(len);
        }
    }
    
    private static String sanitize(String txt)
    {
        int o = -1;
        for (;;)
        {
            o = txt.indexOf('.', o+1);
            if (o < 0)
                break;
            if (o < 2)
                continue;
            if (!Character.isWhitespace(txt.charAt(o - 2)))
                continue;
            if (!Character.isAlphabetic(txt.charAt(o - 1)))
                continue;
            if ((o < txt.length() - 2) && !Character.isWhitespace(txt.charAt(o + 1)))
                continue;
            txt = txt.substring(0, o) + txt.substring(o + 1);
        }
        return txt;
    }
    
    private static final List<String> SAMPLE_NUMBER = new ArrayList<>();
    static
    {
        SAMPLE_NUMBER.add("0");
        SAMPLE_NUMBER.add("1");
        SAMPLE_NUMBER.add("2");
        SAMPLE_NUMBER.add("3");
        SAMPLE_NUMBER.add("4");
        SAMPLE_NUMBER.add("5");
        SAMPLE_NUMBER.add("6");
        SAMPLE_NUMBER.add("7");
        SAMPLE_NUMBER.add("8");
        SAMPLE_NUMBER.add("9");
        SAMPLE_NUMBER.add("10");
        SAMPLE_NUMBER.add("11");
        SAMPLE_NUMBER.add("12");
        SAMPLE_NUMBER.add("13");
        SAMPLE_NUMBER.add("14");
        SAMPLE_NUMBER.add("15");
        SAMPLE_NUMBER.add("16");
        SAMPLE_NUMBER.add("17");
        SAMPLE_NUMBER.add("18");
        SAMPLE_NUMBER.add("19");
        SAMPLE_NUMBER.add("20");
        SAMPLE_NUMBER.add("30");
        SAMPLE_NUMBER.add("40");
        SAMPLE_NUMBER.add("50");
        SAMPLE_NUMBER.add("60");
        SAMPLE_NUMBER.add("70");
        SAMPLE_NUMBER.add("80");
        SAMPLE_NUMBER.add("90");
        SAMPLE_NUMBER.add("100");
        SAMPLE_NUMBER.add("200");
        SAMPLE_NUMBER.add("300");
        SAMPLE_NUMBER.add("400");
        SAMPLE_NUMBER.add("500");
        SAMPLE_NUMBER.add("600");
        SAMPLE_NUMBER.add("700");
        SAMPLE_NUMBER.add("800");
        SAMPLE_NUMBER.add("900");
        SAMPLE_NUMBER.add("1000");
    }
    
    private static final List<String> SAMPLE_US_FIRST_NAME = new ArrayList<>();
    static
    {
        SAMPLE_US_FIRST_NAME.add("Emma");
        SAMPLE_US_FIRST_NAME.add("Olivia");
        SAMPLE_US_FIRST_NAME.add("Sophia");
        SAMPLE_US_FIRST_NAME.add("Isabella");
        SAMPLE_US_FIRST_NAME.add("Ava");
        SAMPLE_US_FIRST_NAME.add("Mia");
        SAMPLE_US_FIRST_NAME.add("Emily");
        SAMPLE_US_FIRST_NAME.add("Abigail");
        SAMPLE_US_FIRST_NAME.add("Madison");
        SAMPLE_US_FIRST_NAME.add("Charlotte");
        SAMPLE_US_FIRST_NAME.add("Harper");
        SAMPLE_US_FIRST_NAME.add("Sofia");
        SAMPLE_US_FIRST_NAME.add("Avery");
        SAMPLE_US_FIRST_NAME.add("Elizabeth");
        SAMPLE_US_FIRST_NAME.add("Amelia");
        SAMPLE_US_FIRST_NAME.add("Evelyn");
        SAMPLE_US_FIRST_NAME.add("Ella");
        SAMPLE_US_FIRST_NAME.add("Chloe");
        SAMPLE_US_FIRST_NAME.add("Victoria");
        SAMPLE_US_FIRST_NAME.add("Aubrey");
        SAMPLE_US_FIRST_NAME.add("Grace");
        SAMPLE_US_FIRST_NAME.add("Noah");
        SAMPLE_US_FIRST_NAME.add("Liam");
        SAMPLE_US_FIRST_NAME.add("Mason");
        SAMPLE_US_FIRST_NAME.add("Jacob");
        SAMPLE_US_FIRST_NAME.add("William");
        SAMPLE_US_FIRST_NAME.add("Ethan");
        SAMPLE_US_FIRST_NAME.add("Michael");
        SAMPLE_US_FIRST_NAME.add("Alexander");
        SAMPLE_US_FIRST_NAME.add("James");
        SAMPLE_US_FIRST_NAME.add("Daniel");
        SAMPLE_US_FIRST_NAME.add("Elijah");
        SAMPLE_US_FIRST_NAME.add("Benjamin");
        SAMPLE_US_FIRST_NAME.add("Logan");
        SAMPLE_US_FIRST_NAME.add("Aiden");
        SAMPLE_US_FIRST_NAME.add("Jayden");
        SAMPLE_US_FIRST_NAME.add("Matthew");
        SAMPLE_US_FIRST_NAME.add("Jackson");
        SAMPLE_US_FIRST_NAME.add("David");
        SAMPLE_US_FIRST_NAME.add("Lucas");
        SAMPLE_US_FIRST_NAME.add("Joseph");
        SAMPLE_US_FIRST_NAME.add("Anthony");
        SAMPLE_US_FIRST_NAME.add("Andrew");
        SAMPLE_US_FIRST_NAME.add("Samuel");
        SAMPLE_US_FIRST_NAME.add("Gabriel");
        SAMPLE_US_FIRST_NAME.add("Joshua");
    }
    
    private static final List<String> SAMPLE_US_CITY = new ArrayList<>();
    static
    {
        SAMPLE_US_CITY.add("New York");
        SAMPLE_US_CITY.add("Los Angeles");
        SAMPLE_US_CITY.add("Chicago");
        SAMPLE_US_CITY.add("Houston");
        SAMPLE_US_CITY.add("Philadelphia");
        SAMPLE_US_CITY.add("Phoenix");
        SAMPLE_US_CITY.add("San Antonio");
        SAMPLE_US_CITY.add("San Diego");
        SAMPLE_US_CITY.add("Dallas");
        SAMPLE_US_CITY.add("San Jose");
        SAMPLE_US_CITY.add("Austin");
        SAMPLE_US_CITY.add("Jacksonville");
        SAMPLE_US_CITY.add("San Francisco ");
        SAMPLE_US_CITY.add("Indianapolis");
        SAMPLE_US_CITY.add("Columbus");
        SAMPLE_US_CITY.add("Fort Worth ");
        SAMPLE_US_CITY.add("Charlotte");
        SAMPLE_US_CITY.add("Detroit");
        SAMPLE_US_CITY.add("El Paso");
        SAMPLE_US_CITY.add("Seattle ");
        SAMPLE_US_CITY.add("Denver ");
        SAMPLE_US_CITY.add("Washington");
        SAMPLE_US_CITY.add("Memphis");
        SAMPLE_US_CITY.add("Boston");
        SAMPLE_US_CITY.add("Nashville-Davidson");
        SAMPLE_US_CITY.add("Baltimore");
        SAMPLE_US_CITY.add("Oklahoma City");
        SAMPLE_US_CITY.add("Portland ");
        SAMPLE_US_CITY.add("Las Vegas ");
        SAMPLE_US_CITY.add("Louisville-Jefferson County");
        SAMPLE_US_CITY.add("Milwaukee");
        SAMPLE_US_CITY.add("Albuquerque");
        SAMPLE_US_CITY.add("Tucson");
        SAMPLE_US_CITY.add("Fresno");
        SAMPLE_US_CITY.add("Sacramento");
        SAMPLE_US_CITY.add("Long Beach");
        SAMPLE_US_CITY.add("Kansas City");
        SAMPLE_US_CITY.add("Mesa");
        SAMPLE_US_CITY.add("Atlanta ");
        SAMPLE_US_CITY.add("Virginia Beach");
        SAMPLE_US_CITY.add("Omaha ");
        SAMPLE_US_CITY.add("Colorado Springs");
        SAMPLE_US_CITY.add("Raleigh");
        SAMPLE_US_CITY.add("Miami");
        SAMPLE_US_CITY.add("Oakland");
        SAMPLE_US_CITY.add("Minneapolis");
        SAMPLE_US_CITY.add("Tulsa");
        SAMPLE_US_CITY.add("Cleveland");
        SAMPLE_US_CITY.add("Wichita");
        SAMPLE_US_CITY.add("New Orleans");
        SAMPLE_US_CITY.add("Arlington");
    }
}
