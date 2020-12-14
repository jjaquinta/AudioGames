package jo.audio.loci.thieves.data.npc;

import org.json.simple.JSONObject;

import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociThing;

public class LociNPC extends LociThing
{
    public static final int GENDER_ANIMAL = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;
    
    public static final String ID_GENDER = "gender";

    public LociNPC(String uri)
    {
        super(uri);
    }
    
    public LociNPC(JSONObject json)
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

    protected String heSheIt()
    {
        switch (getGender())
        {
            case GENDER_ANIMAL:
                return "It";
            case GENDER_MALE:
                return "He";
            case GENDER_FEMALE:
                return "She";
        }
        throw new IllegalStateException();
    }

    protected String hisHerIts()
    {
        switch (getGender())
        {
            case GENDER_ANIMAL:
                return "its";
            case GENDER_MALE:
                return "His";
            case GENDER_FEMALE:
                return "Her";
        }
        throw new IllegalStateException();
    }
    
    // getters and setters
    
    public int getGender()
    {
        return getInt(ID_GENDER);
    }
    
    public void setGender(int value)
    {
        setInt(ID_GENDER, value);
    }
}
