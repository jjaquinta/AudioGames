package jo.audio.loci.core.data;

import java.util.regex.Matcher;

public class ExecuteContext extends InvocationContext
{
    private String           mCommand;
    private String           mVerbText;
    private String           mDirectObjectText;
    private String           mPrepositionText;
    private String           mIndirectObjectText;
    private Matcher          mVerbMatcher;
    private Matcher          mDirectObjectMatcher;
    private Matcher          mPrepositionMatcher;
    private Matcher          mIndirectObjectMatcher;
    private Verb             mMatchedVerb;
    private LociBase         mMatchedVerbHost;
    private LociBase         mMatchedDirectObject;
    private LociBase         mMatchedIndirectObject;
    private boolean          mSuccess;

    // utils
    
    public void set(ExecuteContext c)
    {
        super.set(c);
        setCommand(c.getCommand());
        setVerbText(c.getVerbText());
        setDirectObjectText(c.getDirectObjectText());
        setPrepositionText(c.getPrepositionText());
        setIndirectObjectText(c.getIndirectObjectText());
        setMatchedVerb(c.getMatchedVerb());
        setMatchedVerbHost(c.getMatchedVerbHost());
        setMatchedDirectObject(c.getMatchedDirectObject());
        setMatchedIndirectObject(c.getMatchedIndirectObject());
        setSuccess(c.isSuccess());
    }
    
    @Override
    public String toString()
    {
        StringBuffer txt = new StringBuffer();
        if (mMatchedVerb != null)
            txt.append(mMatchedVerb.getID()+"::");
        txt.append(mVerbText);
        txt.append("/");
        if (mMatchedDirectObject != null)
            txt.append(mMatchedDirectObject.getClass().getSimpleName()+"::");
        txt.append(mDirectObjectText);
        txt.append("/");
        txt.append(mPrepositionText);
        txt.append("/");
        if (mMatchedIndirectObject != null)
            txt.append(mMatchedIndirectObject.getClass().getSimpleName()+"::");
        txt.append(mIndirectObjectText);
        return txt.toString();
    }
    
    // getters and setters

    public String getCommand()
    {
        return mCommand;
    }

    public void setCommand(String Command)
    {
        mCommand = Command;
    }

    public String getVerbText()
    {
        return mVerbText;
    }

    public void setVerbText(String verbText)
    {
        mVerbText = verbText;
    }

    public String getDirectObjectText()
    {
        return mDirectObjectText;
    }

    public void setDirectObjectText(String directObjectText)
    {
        mDirectObjectText = directObjectText;
    }

    public String getPrepositionText()
    {
        return mPrepositionText;
    }

    public void setPrepositionText(String prepositionText)
    {
        mPrepositionText = prepositionText;
    }

    public String getIndirectObjectText()
    {
        return mIndirectObjectText;
    }

    public void setIndirectObjectText(String indirectObjectText)
    {
        mIndirectObjectText = indirectObjectText;
    }

    public Verb getMatchedVerb()
    {
        return mMatchedVerb;
    }

    public void setMatchedVerb(Verb matchedVerb)
    {
        mMatchedVerb = matchedVerb;
    }

    public LociBase getMatchedDirectObject()
    {
        return mMatchedDirectObject;
    }

    public void setMatchedDirectObject(LociBase matchedDirectObject)
    {
        mMatchedDirectObject = matchedDirectObject;
    }

    public LociBase getMatchedIndirectObject()
    {
        return mMatchedIndirectObject;
    }

    public void setMatchedIndirectObject(LociBase matchedIndirectObject)
    {
        mMatchedIndirectObject = matchedIndirectObject;
    }

    public LociBase getMatchedVerbHost()
    {
        return mMatchedVerbHost;
    }

    public void setMatchedVerbHost(LociBase matchedVerbHost)
    {
        mMatchedVerbHost = matchedVerbHost;
    }

    public boolean isSuccess()
    {
        return mSuccess;
    }

    public void setSuccess(boolean success)
    {
        mSuccess = success;
    }

    public Matcher getVerbMatcher()
    {
        return mVerbMatcher;
    }

    public void setVerbMatcher(Matcher verbMatcher)
    {
        mVerbMatcher = verbMatcher;
    }

    public Matcher getDirectObjectMatcher()
    {
        return mDirectObjectMatcher;
    }

    public void setDirectObjectMatcher(Matcher directObjectMatcher)
    {
        mDirectObjectMatcher = directObjectMatcher;
    }

    public Matcher getPrepositionMatcher()
    {
        return mPrepositionMatcher;
    }

    public void setPrepositionMatcher(Matcher prepositionMatcher)
    {
        mPrepositionMatcher = prepositionMatcher;
    }

    public Matcher getIndirectObjectMatcher()
    {
        return mIndirectObjectMatcher;
    }

    public void setIndirectObjectMatcher(Matcher indirectObjectMatcher)
    {
        mIndirectObjectMatcher = indirectObjectMatcher;
    }
}
