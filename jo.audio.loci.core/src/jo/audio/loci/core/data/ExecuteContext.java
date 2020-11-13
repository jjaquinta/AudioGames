package jo.audio.loci.core.data;

import java.util.ArrayList;
import java.util.List;

public class ExecuteContext
{
    private String           mCommand;
    private LociBase         mInvoker;
    private List<LociObject> mVisibleTo = new ArrayList<LociObject>();
    private String           mVerbText;
    private String           mDirectObjectText;
    private String           mPrepositionText;
    private String           mIndirectObjectText;
    private Verb             mMatchedVerb;
    private LociBase         mMatchedVerbHost;
    private LociBase         mMatchedDirectObject;
    private LociBase         mMatchedIndirectObject;
    private boolean          mSuccess;

    // getters and setters

    public String getCommand()
    {
        return mCommand;
    }

    public void setCommand(String Command)
    {
        mCommand = Command;
    }

    public LociBase getInvoker()
    {
        return mInvoker;
    }

    public void setInvoker(LociBase invoker)
    {
        mInvoker = invoker;
    }

    public List<LociObject> getVisibleTo()
    {
        return mVisibleTo;
    }

    public void setVisibleTo(List<LociObject> visibleTo)
    {
        mVisibleTo = visibleTo;
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
}
