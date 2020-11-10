package jo.ipa.logic;

public class IPAComp implements Comparable<IPAComp>
{
    public static final int TEXT = 0;
    public static final int IPA = 1;
    public static final int CAVERPHONE = 2;
    public static final int DOUBLE_METAPHONE = 3;
    
    private IPAWord mWord1;
    private IPAWord mWord2;
    private float   mDistance;
    private int     mMethod;
    
    // utils
    @Override
    public String toString()
    {
        return "IPAComp[method="+mMethod+", dist="+mDistance+", word1="+mWord1+", word2="+mWord2+"]";
    }
    
    @Override
    public int compareTo(IPAComp o)
    {
        return (int)Math.signum(getDistance() - o.getDistance());
    }
    
    public float getAdjustedDistance()
    {
        return mDistance*mMethod;
    }
    
    // getters and setters
    
    public IPAWord getWord1()
    {
        return mWord1;
    }
    public void setWord1(IPAWord word1)
    {
        mWord1 = word1;
    }
    public IPAWord getWord2()
    {
        return mWord2;
    }
    public void setWord2(IPAWord word2)
    {
        mWord2 = word2;
    }
    public float getDistance()
    {
        return mDistance;
    }
    public void setDistance(float distance)
    {
        mDistance = distance;
    }

    public int getMethod()
    {
        return mMethod;
    }

    public void setMethod(int method)
    {
        mMethod = method;
    }
}
