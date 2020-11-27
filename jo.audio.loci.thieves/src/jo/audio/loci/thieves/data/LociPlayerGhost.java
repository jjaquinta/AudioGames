package jo.audio.loci.thieves.data;

import org.json.simple.JSONObject;

public class LociPlayerGhost extends LociPlayer
{
    public static final String PROFILE = LociPlayerGhost.class.getSimpleName();
    
    public LociPlayerGhost(String uri)
    {
        super(uri, PROFILE);        
        setVerbProfile("VerbProfilePlayerGhost");
    }
    
    public LociPlayerGhost(JSONObject json)
    {
        super(json);
        setVerbProfile("VerbProfilePlayerGhost");
    }

    // utils
    
    // getters and setters
}
