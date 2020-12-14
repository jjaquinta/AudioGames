package jo.audio.loci.thieves.data.npc;

import org.json.simple.JSONObject;

import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.thieves.logic.LocationLogic;

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
        String[] desc2 = new String[desc.length + 1];
        System.arraycopy(desc, 0, desc2, 0, desc.length);
        switch (getAlertness())
        {
            case ALERT_DEEP_SLEEP:
                desc2[desc2.length-1] = heSheIt()+" is deeply asleep.";
                break;
            case ALERT_LIGHT_SLEEP:
                desc2[desc2.length-1] = heSheIt()+" is sleeping lightly.";
                break;
            case ALERT_FOCUSED:
                desc2[desc2.length-1] = heSheIt()+" is involved in what "+heSheIt().toLowerCase()+" is doing.";
                break;
            case ALERT_AWARE:
                desc2[desc2.length-1] = heSheIt()+" is observing "+hisHerIts()+" suroundings.";
                break;
            case ALERT_ALERT:
                desc2[desc2.length-1] = heSheIt()+" is watching you closely.";
                break;
            case ALERT_ALARMED:
                desc2[desc2.length-1] = heSheIt()+" is alarmed at your presence.";
                break;
            default:
                throw new IllegalStateException();
        }
        return desc2;
    }

    public boolean rollNotice(int mod)
    {
        mod += (getAlertness() - ALERT_AWARE)*5;
        int target = 50 + mod;
        int roll = LocationLogic.getCity().getRND().nextInt(100) + 1;
        return roll <= target;
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
