package jo.audio.loci.sandbox.data;

import org.json.simple.JSONObject;

public class LociExit extends LociThing
{
    public static final String PROFILE = "exit";
 
    public static final String ID_DESTINATION = "destination";
    
    public LociExit(String uri)
    {
        super(uri, PROFILE);        
        init();
    }
    
    public LociExit(JSONObject json)
    {
        super(json);
        init();
    }

    private void init()
    {
        setVerbProfile("VerbProfileExit");
    }

    // getters and setters
    
    public String getDestination()
    {
        return getString(ID_DESTINATION);
    }
    
    public void setDestination(String value)
    {
        setString(ID_DESTINATION, value);
    }
}
