package jo.audio.loci.sandbox.data;

import org.json.simple.JSONObject;

public class LociRoomFoyeur extends LociRoom
{
    public LociRoomFoyeur(String uri)
    {
        super(uri);        
        init();
    }
    
    public LociRoomFoyeur(JSONObject json)
    {
        super(json);
        init();
    }

    private void init()
    {
    }

    // getters and setters
    
}
