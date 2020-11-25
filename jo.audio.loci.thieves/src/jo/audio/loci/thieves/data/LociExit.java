package jo.audio.loci.thieves.data;

import org.json.simple.JSONObject;

public class LociExit extends LociThing
{
    public static final String PROFILE = "exit";
 
    public static final String ID_SOURCE = "source";
    public static final String ID_DESTINATION = "destination";
    public static final String ID_DIRECTION = "direction";
    public static final String ID_ELEVATION = "elevation";
    
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
    
    public String getSource()
    {
        return getString(ID_SOURCE);
    }
    
    public void setSource(String value)
    {
        setString(ID_SOURCE, value);
    }
    
    public int getDirection()
    {
        return getInt(ID_DIRECTION);
    }
    
    public void setDirection(int value)
    {
        setInt(ID_DIRECTION, value);
    }
    
    public int getElevation()
    {
        return getInt(ID_ELEVATION);
    }
    
    public void setElevation(int value)
    {
        setInt(ID_ELEVATION, value);
    }
}
