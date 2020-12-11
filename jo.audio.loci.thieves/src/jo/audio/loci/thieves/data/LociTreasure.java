package jo.audio.loci.thieves.data;

import org.json.simple.JSONObject;

public class LociTreasure extends LociItem
{
    public static final String ID_VALUE = "value";
    public static final String ID_TYPE = "type";

    public LociTreasure(String uri)
    {
        super(uri);
    }
    
    public LociTreasure(JSONObject json)
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
    
    public int getValue()
    {
        return getInt(ID_VALUE);
    }
    
    public void setValue(int value)
    {
        setInt(ID_VALUE, value);
    }
    
    public String getType()
    {
        return getString(ID_TYPE);
    }
    
    public void setType(String value)
    {
        setString(ID_TYPE, value);
    }
}
