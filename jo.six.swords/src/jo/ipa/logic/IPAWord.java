package jo.ipa.logic;

public class IPAWord
{
    private String  mWord;
    private String  mIPA;
    private String[] mAllVariants;
    private String  mSource;
    private boolean mSynthIPA;
    private String  mMetaphone;
    private String  mCaverphone;
    private Object  mMetadata;
    
    public IPAWord()
    {        
    }

    public IPAWord(IPAWord w)
    {        
        mWord = w.mWord;
        mIPA = w.mIPA;
        mSource = w.mSource;
        mAllVariants = w.mAllVariants;
    }
    
    // utilties
    
    @Override
    public String toString()
    {
        return mWord;
    }
    
    public void append(IPAWord w)
    {
        mWord += w.mWord;
        mIPA += w.mIPA;
        if (mSource == null)
            mSource = w.mSource;
        else
            mSource += w.mSource;
        if (mAllVariants == null)
            mAllVariants = w.mAllVariants;
        else if (w.mAllVariants != null)
        {
            String[] newAllVariants = new String[mAllVariants.length*w.mAllVariants.length];
            for (int i = 0; i < mAllVariants.length; i++)
                for (int j = 0; j < w.mAllVariants.length; j++)
                {
                    String v = mAllVariants[i] + w.mAllVariants[j];
                    newAllVariants[i*w.mAllVariants.length + j] = v;
                }
            mAllVariants = newAllVariants;
        }
        mSynthIPA = mSynthIPA || w.mSynthIPA;
    }
    
    public boolean isWild()
    {
        return "*".equals(mSource);
    }
    
    // getters and setters
    
    public String getWord()
    {
        return mWord;
    }
    public void setWord(String word)
    {
        mWord = word;
    }
    public String getIPA()
    {
        return mIPA;
    }
    public void setIPA(String iPA)
    {
        mIPA = iPA;
    }
    public String getSource()
    {
        return mSource;
    }
    public void setSource(String source)
    {
        mSource = source;
    }

    public String[] getAllVariants()
    {
        return mAllVariants;
    }

    public void setAllVariants(String[] allVariants)
    {
        mAllVariants = allVariants;
    }

    public boolean isSynthIPA()
    {
        return mSynthIPA;
    }

    public void setSynthIPA(boolean synthIPA)
    {
        mSynthIPA = synthIPA;
    }

    public String getMetaphone()
    {
        return mMetaphone;
    }

    public void setMetaphone(String metaphone)
    {
        mMetaphone = metaphone;
    }

    public String getCaverphone()
    {
        return mCaverphone;
    }

    public void setCaverphone(String caverphone)
    {
        mCaverphone = caverphone;
    }

    public Object getMetadata()
    {
        return mMetadata;
    }

    public void setMetadata(Object metadata)
    {
        mMetadata = metadata;
    }
}
