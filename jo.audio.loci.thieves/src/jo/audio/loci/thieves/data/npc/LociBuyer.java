package jo.audio.loci.thieves.data.npc;

import org.json.simple.JSONObject;

import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociTreasure;
import jo.audio.loci.thieves.logic.PlayerLogic;
import jo.util.utils.MathUtils;

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

    private int calculateStolenWorth(LociPlayer player, LociTreasure item)
    {
        double evaluation = MathUtils.interpolate(player.getLevel(), 0, 10, .4, .9);
        if (evaluation > .95)
            evaluation = .95;
        return (int)(item.getValue()*evaluation);
    }

    public void registerPurchase(LociPlayer player, LociTreasure item, int paid)
    {
        if ("stolen".equals(item.getType()))
        {
            int credit = item.getValue() - paid;
            if (credit > 0)
                player.setStanding(player.getStanding() + credit);
            if (paid > 0)
                PlayerLogic.addXP(player, paid);
        }
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

    public int calculateWorth(LociPlayer player, LociTreasure item)
    {
        if ("stolen".equals(item.getType()))
            return calculateStolenWorth(player, item);
        return 0;
    }
}
