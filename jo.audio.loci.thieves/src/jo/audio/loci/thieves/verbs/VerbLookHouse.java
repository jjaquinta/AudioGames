package jo.audio.loci.thieves.verbs;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociStreet;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.logic.gen.HouseLogic;
import jo.util.utils.obj.IntegerUtils;

public class VerbLookHouse extends Verb
{
    public VerbLookHouse()
    {
        super("look,l", "[0-9]*", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        int houseNum = IntegerUtils.parseInt(context.getDirectObjectText());
        LociStreet street = (LociStreet)DataStoreLogic.load(player.getContainedBy());        
        if ((houseNum >= 1) && (houseNum <= street.getStreet().getHouses()))
        {
            House house = HouseLogic.getHouse(street.getStreet(), houseNum);
            player.addMessage("You see a "+house.getTemplate().getName()+".");
            player.addMessage(house.getTemplate().getDescription());
        }
        else
        {
            if (street.getStreet().getHouses() == 0)
                player.addMessage("There are no houses here.");
            else
            {
                player.addMessage("There is no address of that number here.");
                player.addMessage("The houses run from 1 to "+street.getStreet().getHouses()+".");
            }
        }
    }
}
