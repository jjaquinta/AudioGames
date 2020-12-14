package jo.audio.loci.thieves.data.npc;

import org.json.simple.JSONObject;

import jo.audio.loci.thieves.data.LociPlayer;

public class LociObserver extends LociNPC
{
    public static final int ALERT_DEEP_SLEEP = 0;
    public static final int ALERT_LIGHT_SLEEP = 1;
    public static final int ALERT_FOCUSED = 2;
    public static final int ALERT_AWARE = 3;
    public static final int ALERT_ALERT = 4;
    public static final int ALERT_ALARMED = 5;
    
    public static final String TYPE_RESIDENT = "resident";
    public static final String TYPE_CHILD = "child";
    public static final String TYPE_GUARD = "guard";
    public static final String TYPE_ANIMAL = "animal";
    
    public static final String ID_TYPE = "type";
    public static final String ID_ALERTNESS = "alertness";

    public LociObserver(String uri)
    {
        super(uri);
    }
    
    public LociObserver(JSONObject json)
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
    
    public int getAlertness()
    {
        return getInt(ID_ALERTNESS);
    }
    
    public void setAlertness(int value)
    {
        setInt(ID_ALERTNESS, value);
    }
}
