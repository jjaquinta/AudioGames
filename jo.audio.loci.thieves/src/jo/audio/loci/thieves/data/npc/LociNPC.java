package jo.audio.loci.thieves.data.npc;

import org.json.simple.JSONObject;

import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociThing;

public class LociNPC extends LociThing
{
    public LociNPC(String uri)
    {
        super(uri);
    }
    
    public LociNPC(JSONObject json)
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
