package jo.audio.loci.thieves.data;

import org.json.simple.JSONObject;

public class LociItem extends LociThing
{
    public LociItem(String uri)
    {
        super(uri);
    }
    
    public LociItem(JSONObject json)
    {
        super(json);
    }
    
    // utilities
    @Override
    public String[] getExtendedDescription(LociPlayer wrt)
    {
        String[] desc = super.getExtendedDescription(wrt);
        return desc;
    }
    
    // getters and setters
}
