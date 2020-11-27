package jo.audio.loci.thieves.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class LociFoyeur extends LociThing
{
    public static final String PROFILE = LociFoyeur.class.getSimpleName();
    
    public LociFoyeur(String uri)
    {
        super(uri, PROFILE);        
        init();
    }
    
    public LociFoyeur(JSONObject json)
    {
        super(json);
        init();
    }

    private void init()
    {
        setVerbProfile("VerbProfileFoyeur");
    }
    
    @Override
    public String[] getExtendedDescription(LociPlayer wrt)
    {
        List<String> desc = new ArrayList<String>();
        desc.add(getPrimaryName()+".");
        desc.add(getDescription());
        return desc.toArray(new String[0]);
    }

    // getters and setters
    
}
