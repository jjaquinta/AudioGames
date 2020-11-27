package jo.audio.loci.sandbox.data;

import org.json.simple.JSONObject;

import jo.audio.loci.core.data.LociBase;

public class LociCookie extends LociBase
{
    public LociCookie(String uri)
    {
        super(uri);        
    }
    
    public LociCookie(JSONObject json)
    {
        super(json);
    }
}
