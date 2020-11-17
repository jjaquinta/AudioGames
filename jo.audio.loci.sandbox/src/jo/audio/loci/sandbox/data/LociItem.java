package jo.audio.loci.sandbox.data;

import org.json.simple.JSONObject;

public class LociItem extends LociThing
{
    public static final String PROFILE = "item";

    public LociItem(String uri)
    {
        super(uri, PROFILE);
        setVerbProfile("VerbProfileItem");
    }
    
    public LociItem(String uri, String profile)
    {
        super(uri, profile);
        setVerbProfile("VerbProfileItem");
    }
    
    public LociItem(JSONObject json)
    {
        super(json);
        setVerbProfile("VerbProfileItem");
    }
    
    // utilities
    @Override
    public String[] getExtendedDescription()
    {
        String[] desc = super.getExtendedDescription();
        return desc;
    }
    
    // getters and setters
}
