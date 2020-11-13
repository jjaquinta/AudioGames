package jo.audio.loci.core.data;

import java.util.ArrayList;

import java.util.List;

public class VerbProfile
{
    private String  mName = getClass().getSimpleName();
    private String  mExtends = "";
    private List<String>    mVerbs = new ArrayList<String>();

    // utils
    public void setExtends(Class<? extends VerbProfile> clazz)
    {
        setExtends(clazz.getSimpleName());
    }

    public void addVerbs(Class<?>... clazzes)
    {
        for (Class<?> clazz : clazzes)
            getVerbs().add(clazz.getSimpleName());
    }
    
    // getters and setters
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public String getExtends()
    {
        return mExtends;
    }
    public void setExtends(String extends1)
    {
        mExtends = extends1;
    }
    public List<String> getVerbs()
    {
        return mVerbs;
    }
    public void setVerbs(List<String> verbs)
    {
        mVerbs = verbs;
    }
}
