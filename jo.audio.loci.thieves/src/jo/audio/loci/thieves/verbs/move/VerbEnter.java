package jo.audio.loci.thieves.verbs.move;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.Verb;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.data.LociLocality;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociStreet;
import jo.audio.loci.thieves.stores.HouseStore;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.logic.gen.HouseLogic;
import jo.util.utils.obj.IntegerUtils;

public class VerbEnter extends Verb
{
    public VerbEnter()
    {
        super("enter,house,enter house,go into", ".*", null, null);
    }

    @Override
    public void execute(ExecuteContext context)
    {
        LociPlayer player = (LociPlayer)context.getInvoker();
        int houseNum = IntegerUtils.parseInt(context.getDirectObjectText());
        LociStreet street = (LociStreet)DataStoreLogic.load(player.getContainedBy());
        int numHouses = street.getStreet().getHouses();
        if ((houseNum < 0) || (houseNum > numHouses))
        {
            if (numHouses < 1)
                player.addMessage("There are no buildings to enter on this street.");
            else
                player.addMessage("The buildings here number 1 to "+numHouses+".");
            return;
        }
        House house = HouseLogic.getHouse(street.getStreet(), houseNum);
        String destination = HouseStore.PREFIX+street.getStreet().getID()+":"+houseNum+"/"+house.getEntry();
        LociLocality newRoom = (LociLocality)DataStoreLogic.load(destination);
        VerbGoImplicit.transition(player, null, street, newRoom);
    }
}
