package jo.audio.loci.thieves.data;

import org.json.simple.JSONObject;

public class LociLocality extends LociThing
{
    public LociLocality(String uri)
    {
        super(uri);
    }
    
    public LociLocality(JSONObject json)
    {
        super(json);
    }
    
    // utilities
    public void say(String primaryMessage, String altURI, String altMessage)
    {
        for (LociPlayer p : getContainsStuff(LociPlayer.class))
            if (p.getURI().equals(altURI))
                p.addMessage(altMessage);
            else
                p.addMessage(primaryMessage);
    }

    // getters and setters
    
}
