package jo.audio.loci.thieves.data;

import org.json.simple.JSONObject;

public class LociPlayerAdmin extends LociPlayer
{
    public static final String PROFILE = LociPlayerAdmin.class.getSimpleName();
    
    public LociPlayerAdmin(String uri)
    {
        super(uri, PROFILE);        
        setVerbProfile("VerbProfilePlayerAdmin");
    }
    
    public LociPlayerAdmin(JSONObject json)
    {
        super(json);
        setVerbProfile("VerbProfilePlayerAdmin");
    }

    // utils
    
    // getters and setters
}
