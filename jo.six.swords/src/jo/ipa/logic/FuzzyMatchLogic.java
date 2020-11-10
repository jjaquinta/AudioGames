package jo.ipa.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import jo.util.utils.obj.DoubleUtils;

public class FuzzyMatchLogic
{
    public static void addToDictionary(IPADictionary dict, Object md, String... words)
    {
        for (String word : words)
            for (StringTokenizer st = new StringTokenizer(word, ","); st.hasMoreTokens(); )
            {
                String w = st.nextToken().trim();
                IPAWord ipa = CMULogic.toIPA(w);
                if (ipa == null)
                    continue;
                dict.getWords().add(ipa);
                ipa.setMetadata(md);
            }
    }
    
    public static List<IPAComp> findMatches(IPADictionary dict, String source, int max)
    {
        IPAWord src = CMULogic.toIPA(source);
        List<IPAComp> matches = new ArrayList<>();
        for (IPAWord test : dict.getAllWords())
        {
            IPAComp c = LevenshteinDistance.compare(src, test);
            //BaseServlet.weblog("test comapre "+c);
            if (max < 0)
                matches.add(c);
            else
            {
                if (matches.size() < max)
                    addInPlace(matches, c);
                else if (c.getAdjustedDistance() < matches.get(matches.size() - 1).getAdjustedDistance())
                {
                    addInPlace(matches, c);
                    matches.remove(matches.size() - 1);
                }
            }
        }
        if (max < 0)
            Collections.sort(matches);
        return matches;
    }

    private static void addInPlace(List<IPAComp> matches, IPAComp c)
    {
        for (int i = 0; i < matches.size(); i++)
            if (matches.get(i).getAdjustedDistance() > c.getAdjustedDistance())
            {
                matches.add(i, c);
                return;
            }
        matches.add(c);
    }

    public static IPAComp resolveMatches(List<IPAComp> matches)
    {
        if (matches.size() == 0)
            return null;
        IPAComp first = matches.get(0);
        if (matches.size() == 1)
            if (first.getDistance() <= 2.0)
                return first;
            else
                return null;
        if (DoubleUtils.equals(first.getDistance(), 0))
            return first;
        IPAComp second = matches.get(1);
        if (DoubleUtils.equals(first.getAdjustedDistance(), second.getAdjustedDistance()))
        {
            for (int i = matches.size() - 1; i > 2; i--)
                if (DoubleUtils.equals(first.getAdjustedDistance(), matches.get(i).getAdjustedDistance()))
                    matches.remove(i);
            return null;
        }
        if (first.getDistance() <= 2.0)
            return first;
        return null;
    }
}
