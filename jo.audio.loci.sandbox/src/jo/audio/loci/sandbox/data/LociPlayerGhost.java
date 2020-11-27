package jo.audio.loci.sandbox.data;

import org.json.simple.JSONObject;

public class LociPlayerGhost extends LociPlayer
{
    public LociPlayerGhost(String uri)
    {
        super(uri);        
    }
    
    public LociPlayerGhost(JSONObject json)
    {
        super(json);
    }

    // utils
    
    // getters and setters
}
