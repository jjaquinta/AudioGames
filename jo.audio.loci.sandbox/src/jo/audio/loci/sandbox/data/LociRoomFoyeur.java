package jo.audio.loci.sandbox.data;

import org.json.simple.JSONObject;

public class LociRoomFoyeur extends LociRoom
{
    public static final String PROFILE = LociRoomFoyeur.class.getSimpleName();
    
    public LociRoomFoyeur(String uri)
    {
        super(uri, PROFILE);        
        init();
    }
    
    public LociRoomFoyeur(JSONObject json)
    {
        super(json);
        init();
    }

    private void init()
    {
        setVerbProfile("VerbProfileRoomFoyeur");
    }

    // getters and setters
    
}
