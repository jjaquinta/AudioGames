package jo.audio.loci.sandbox.data;

import org.json.simple.JSONObject;

public class LociPlayerAdmin extends LociPlayer
{
    public LociPlayerAdmin(String uri)
    {
        super(uri);        
    }
    
    public LociPlayerAdmin(JSONObject json)
    {
        super(json);
    }

    // utils
    
    // getters and setters
}
