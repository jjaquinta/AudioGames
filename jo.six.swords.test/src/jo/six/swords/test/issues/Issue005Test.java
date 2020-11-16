package jo.six.swords.test.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import jo.audio.companions.slu.CompanionsModelConst;
import jo.six.swords.test.Base;
import jo.util.utils.DebugUtils;

public class Issue005Test extends Base
{
    @Test
    void testWield() throws IOException
    {
        transact(null, "Welcome", "who");
        transact("who", "Your active companion is Jerome", "Paris", "Honesty", "Peyton", "Fellowship of the Bad Swords", "inventory");
        transact("inventory", "The group is carrying", 
                "$"+CompanionsModelConst.TEXT_YOU_HAVE_NO_GOLD);
        DebugUtils.debug = true;
        transact("east", "$"+CompanionsModelConst.TEXT_YOU_TRAVEL_EAST, 
                "$"+CompanionsModelConst.TEXT_AROUND_YOU_CONTINUES_XXX, 
                "$"+CompanionsModelConst.TEXT_FOLLOW_STRAIGHT_THE_ROAD_GOES_XXX_YYY, 
                "fight");
        DebugUtils.debug = false;
        transact("fight", "Peyton", "Jerome", "Paris", "Honesty", "You have 3 enemies left");
        transact("kill", "Peyton", "Jerome", "Paris", "Honesty", "You have 2 enemies left");
        transact("kill Merchant", "Peyton", "Jerome", "Paris", 
                "$"+CompanionsModelConst.TEXT_THE_XXX_RUNS_AWAY,
                "$"+CompanionsModelConst.TEXT_YOU_SEARCH_THE_BODIES_AND_FIND_XXX,
                "$"+CompanionsModelConst.TEXT_YOU_GAIN_XXX_EXPERIENCE_POINTS);
        west();
        south("You come upon eight Kobold");
        transact("run away", "You run away from the combat");
        south();
        south();
        transact("enter", "You can see Main Street to the East");
        transact("east", "North of you lies Weapon Shop");
        transact("north", "To the West you can see Window");
        transact("west", "To the North you can see Pole Arm for 6");
        transact("north", "You buy 1 Pole Arm for 6 gold pieces");
        // standard
        transact("use Pole Arm", "Jerome is now using Pole Arm");
        transact("north", "You buy 1 Pole Arm for 6 gold pieces");
        transact("activate Honesty", "Your active companion is now Honesty");
        // alias 1
        transact("wear Pole Arm", "Honesty is now using Pole Arm");
        transact("north", "You buy 1 Pole Arm for 6 gold pieces");
        transact("activate Paris", "Your active companion is now Paris");
        // alias 2
        transact("wield Pole Arm", "Paris is now using Pole Arm");
        transact("north", "You buy 1 Pole Arm for 6 gold pieces");
        transact("activate Peyton", "Peyton is now your active companion");
        // alternative name
        transact("wield PoleArm", "Peyton is now using Pole Arm");
    }

}
