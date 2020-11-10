package jo.ipa.logic;

import java.util.ArrayList;
import java.util.List;

public class IPADictionary
{
    private List<IPADictionary>   mIncludes = new ArrayList<>();
    private List<IPAWord>  mWords = new ArrayList<>();
    
    // utilities
    public List<IPAWord> getAllWords()
    {
        List<IPAWord> words = new ArrayList<>();
        words.addAll(mWords);
        for (IPADictionary also : mIncludes)
            words.addAll(also.getAllWords());
        return words;
    }
    
    // getters and setters
    
    public List<IPAWord> getWords()
    {
        return mWords;
    }
    public void setWords(List<IPAWord> words)
    {
        mWords = words;
    }
    public List<IPADictionary> getIncludes()
    {
        return mIncludes;
    }

    public void setIncludes(List<IPADictionary> includes)
    {
        mIncludes = includes;
    }
}
