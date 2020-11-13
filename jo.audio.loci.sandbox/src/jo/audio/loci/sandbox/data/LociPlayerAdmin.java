package jo.audio.loci.sandbox.data;

import org.json.simple.JSONObject;

import jo.audio.loci.sandbox.vprofile.VerbProfilePlayerAdmin;

public class LociPlayerAdmin extends LociPlayer
{
    public static final String PROFILE = "playerAdmin";
    
    public LociPlayerAdmin(String uri)
    {
        super(uri, PROFILE);        
        setVerbProfile(VerbProfilePlayerAdmin.class);
    }
    
    public LociPlayerAdmin(JSONObject json)
    {
        super(json);
        setVerbProfile(VerbProfilePlayerAdmin.class);
    }

    // utils
    
    // getters and setters
}
