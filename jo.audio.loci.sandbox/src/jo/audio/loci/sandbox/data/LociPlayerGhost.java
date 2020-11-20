package jo.audio.loci.sandbox.data;

import org.json.simple.JSONObject;

public class LociPlayerGhost extends LociPlayer
{
    public static final String PROFILE = "playerGhost";
    
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
