package jo.audio.util.model.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.audio.util.PhoneticMatchLogic;

public class FuzzyDictionary
{
    private Map<String, List<String>> mDictionary;
    private Map<String, String> mDirectToAlias = new HashMap<String, String>();
    private Map<String, String> mCaverToAlias = new HashMap<String, String>();
    private Map<String, String> mDoubleToAlias = new HashMap<String, String>();
    
    public FuzzyDictionary(Map<String, List<String>> dictionary)
    {
        mDictionary = dictionary;
        index();
    }
    
    private void index()
    {
        for (String alias : mDictionary.keySet())
        {
            List<String> words = mDictionary.get(alias);
            for (String word : words)
            {
                mDirectToAlias.put(word.toLowerCase(), alias);
                mCaverToAlias.put(PhoneticMatchLogic.toCaverphone(word), alias);
                mDoubleToAlias.put(PhoneticMatchLogic.toDoubleMetaphone(word), alias);
            }
        }
    }
    

    public String findMatch(String word)
    {
        return findMatch(word, null);
    }

    public String findMatch(String word, StringBuffer debug)
    {
        if (word == null)
            return null;
        word = word.toLowerCase();
        String w = mDirectToAlias.get(word);
        if (w != null)
            return w;
        if (debug != null)
        {
            debug.append("Can't find direct match for '"+word+"'. Options are:\n");
            for (String key : mDirectToAlias.keySet())
                debug.append(key+"->"+mDirectToAlias.get(key)+"\n");
        }
        w = mDoubleToAlias.get(PhoneticMatchLogic.toDoubleMetaphone(word));
        if (w != null)
            return w;
        if (debug != null)
        {
            debug.append("Can't find double metaphone match for '"+PhoneticMatchLogic.toDoubleMetaphone(word)+"'. Options are:\n");
            for (String key : mDoubleToAlias.keySet())
                debug.append(key+"->"+mDoubleToAlias.get(key)+"\n");
        }
        w = mCaverToAlias.get(PhoneticMatchLogic.toCaverphone(word));
        if (w != null)
            return w;
        if (debug != null)
        {
            debug.append("Can't find caverphone match for '"+PhoneticMatchLogic.toCaverphone(word)+"'. Options are:\n");
            for (String key : mCaverToAlias.keySet())
                debug.append(key+"->"+mCaverToAlias.get(key)+"\n");
        }
        return null;
    }

}
