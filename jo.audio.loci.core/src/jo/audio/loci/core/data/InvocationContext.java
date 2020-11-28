package jo.audio.loci.core.data;

import java.util.ArrayList;
import java.util.List;

public class InvocationContext
{
    private LociBase         mInvoker;
    private List<LociObject> mVisibleTo = new ArrayList<LociObject>();

    // utils
    
    public void set(InvocationContext c)
    {
        setInvoker(c.getInvoker());
        getVisibleTo().addAll(c.getVisibleTo());
    }
    
    // getters and setters

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
}
