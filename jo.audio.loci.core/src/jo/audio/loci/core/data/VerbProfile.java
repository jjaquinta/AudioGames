package jo.audio.loci.core.data;

import java.util.ArrayList;

import java.util.List;

public class VerbProfile
{
    private String  mName = getClass().getSimpleName();
    private String  mExtends = "";
    private List<String>    mVerbs = new ArrayList<String>();

    public VerbProfile()
    {        
    }

    // utils
    public static VerbProfile build(String name)
    {
        VerbProfile vp = new VerbProfile();
        vp.setName(name);
        return vp;
    }
    
    public VerbProfile setExtends(Class<? extends VerbProfile> clazz)
    {
        setExtends(clazz.getSimpleName());
        return this;
    }
    
    public VerbProfile setExtendsName(String extendsName)
    {
        setExtends(extendsName);
        return this;
    }

    public VerbProfile addVerbs(Class<?>... clazzes)
    {
        for (Class<?> clazz : clazzes)
            getVerbs().add(clazz.getSimpleName());
        return this;
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
