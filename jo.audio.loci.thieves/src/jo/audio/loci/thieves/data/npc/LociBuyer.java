package jo.audio.loci.thieves.data.npc;

import org.json.simple.JSONObject;

import jo.audio.loci.thieves.data.LociPlayer;

public class LociBuyer extends LociNPC
{
    public static final String ID_TYPE = "type";

    public LociBuyer(String uri)
    {
        super(uri);
    }
    
    public LociBuyer(JSONObject json)
    {
        super(json);
    }
    
    // utilities
    @Override
    public String[] getExtendedDescription(LociPlayer wrt)
    {
        String[] desc = super.getExtendedDescription(wrt);
        return desc;
    }
    
    // getters and setters
    
    public String getType()
    {
        return getString(ID_TYPE);
    }
    
    public void setType(String value)
    {
        setString(ID_TYPE, value);
    }
}
