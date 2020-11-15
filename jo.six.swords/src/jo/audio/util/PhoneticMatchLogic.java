package jo.audio.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.AbstractCaverphone;
import org.apache.commons.codec.language.Caverphone2;
import org.apache.commons.codec.language.DoubleMetaphone;

public class PhoneticMatchLogic
{
    public static int findMatchIdx(String word, List<String> vocab)
    {
        String match = findMatch(word, vocab);
        if (match == null)
            return -1;
        int off = vocab.indexOf(match);
        return off;
    }

    public static String findMatch(String word, List<String> vocab)
    {
        if (word == null)
            return null;
        String w = exactMatch(word, vocab);
        if (w != null)
            return w;
        w = doubleMetaphoneMatch(word, vocab);
        if (w != null)
            return w;
        w = caverphoneMatch(word, vocab);
        if (w != null)
            return w;
        w = ordinalMatch(word, vocab);
        if (w != null)
            return w;
        w = overlapMatch(word, vocab);
        if (w != null)
            return w;
        w = firstCharMatch(word, vocab);
        if (w != null)
            return w;
        return null;
    }

    public static String exactMatch(String word, List<String> vocab)
    {
        for (String w : vocab)
            if (w.equalsIgnoreCase(word))
                return w;
        return null;
    }

    public static DoubleMetaphone mDoubleMetaphone = new DoubleMetaphone();
    
    public static String doubleMetaphoneMatch(String word, List<String> vocab)
    {
        if (word == null)
            return null;
        String wordEncoded = toDoubleMetaphone(word);
        if (wordEncoded == null)
            return null;
        for (String w : vocab)
        {
            if (w == null)
                continue;
            String wEncoded = toDoubleMetaphone(w);
            if (wEncoded == null)
                continue;
            try
            {
                if (mDoubleMetaphone.isDoubleMetaphoneEqual(wordEncoded, wEncoded, false))
                    return w;
                if (mDoubleMetaphone.isDoubleMetaphoneEqual(wordEncoded, wEncoded, true))
                    return w;
            }
            catch (NullPointerException e)
            {   // Sometimes isDoubleMetaphoneEqual throws a NPE. I have no idea why.
                continue;
            }
        }
        return null;
    }

    public static String toDoubleMetaphone(String word)
    {
        String wordEncoded = mDoubleMetaphone.encode(word);
        return wordEncoded;
    }

    public static AbstractCaverphone mCaverphone = new Caverphone2();
    
    public static String caverphoneMatch(String word, List<String> vocab)
    {
        if (word == null)
            return null;
        try
        {
            String wordEncoded = toCaverphone(word);
            if (wordEncoded == null)
                return null;
            for (String w : vocab)
            {
                String wEncoded = toCaverphone(w);
                if (mCaverphone.isEncodeEqual(wordEncoded, wEncoded))
                    return w;
            }
        }
        catch (Exception e)
        {
        }
        return null;
    }

    public static String toCaverphone(String word)
    {
        String wordEncoded;
        try
        {
            wordEncoded = mCaverphone.encode(word);
            return wordEncoded;
        }
        catch (EncoderException e)
        {
            return null;
        }
    }

    public static String ordinalMatch(String word, List<String> vocab)
    {
        Integer idx = EnumerationUtils.getEnumeration(word);
        if (idx != null)
            if ((idx <= vocab.size()) && (idx > 0))
                return vocab.get(idx - 1);
        return null;
    }

    public static String overlapMatch(String word, List<String> vocab)
    {
        String best = null;
        int bestv = -1;
        for (String v : vocab)
        {
            int m = countWordMatches(word, v);
            if (m == 0)
                continue;
            if ((best == null) || (m > bestv))
            {
                best = v;
                bestv = m;
            }
        }
        return best;
    }

    public static String firstCharMatch(String word, List<String> vocab)
    {
        if (word.length() == 0)
            return null;
        String target = null;
        char firstLetter = Character.toLowerCase(word.charAt(0));
        for (String test : vocab)
        {
            char ch = Character.toLowerCase(test.charAt(0));
            if (firstLetter == ch)
            {
                if (target == null)
                    target = test;
                else
                {
                    target = null;
                    break; // ambiguous
                }
            }
        }
        return target;
    }

    public static void main(String[] argv)
    {
        List<String> words = new ArrayList<>();
        words.add("attack drone");
        words.add("attacker drone");
        words.add("attack drones");
        words.add("attacker drones");
        words.add("attackers");
        words.add("attacker");
        words.add("defense drone");
        words.add("defender drones");
        words.add("defense drones");
        words.add("defenders");
        words.add("defender");
        words.add("factory drone");
        words.add("factory drones");
        words.add("factories");
        words.add("factory");
        words.add("axion cannon");
        words.add("axion");
        words.add("cannon");
        words.add("ion shield");
        words.add("ion");
        words.add("shield");
        words.add("flight deck");
        words.add("flight");
        words.add("deck");
        words.add("hanger");
        words.add("hydroponics");
        words.add("hydro");
        words.add("mine");
        words.add("solar cells");
        words.add("solar");
        words.add("cells");
        Set<String> range = new HashSet<>();
        System.out.println("DoubleMetaphone:");
        for (String word : words)
            System.out.println(word+" -> \""+mDoubleMetaphone.encode(word)+"\"");
        range.clear();
        System.out.println("Caverphone:");
        for (String word : words)
            try
            {
                System.out.println(word+" -> \""+mCaverphone.encode(word)+"\"");
            }
            catch (EncoderException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }
    
    public static boolean isAnyWordMatch(String s1, String s2)
    {
        List<String> w1 = new ArrayList<>();
        for (StringTokenizer st = new StringTokenizer(s1.toLowerCase(), " \t,."); st.hasMoreTokens(); )
            w1.add(st.nextToken());
        for (StringTokenizer st = new StringTokenizer(s2.toLowerCase(), " \t,."); st.hasMoreTokens(); )
            if (s1.contains(st.nextToken()))
                return true;
        return false;
    }
    
    public static int countWordMatches(String s1, String s2)
    {
        if (s1.equalsIgnoreCase(s2))
            return 100;
        List<String> w1 = new ArrayList<>();
        int count = 0;
        for (StringTokenizer st = new StringTokenizer(s1.toLowerCase(), " \t,."); st.hasMoreTokens(); )
            w1.add(st.nextToken());
        for (StringTokenizer st = new StringTokenizer(s2.toLowerCase(), " \t,."); st.hasMoreTokens(); )
            if (s1.contains(st.nextToken()))
                count++;
        return count;
    }
}
