package jo.six.swords.test.basics;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import jo.six.swords.test.Base;

public class LaunchTest extends Base
{
    @Test
    void testLaunch() throws IOException
    {
        transact(null, "Welcome", "who");
        transact("who", "Your active companion is Jerome", "Paris", "Honesty", "Peyton", "Fellowship of the Bad Swords", "inventory");
        transact("inventory", "The group is carrying", "You're broke", "east");
        transact("east", "East it is", "Fields continues to surround you", "The road you have been following goes East", "fight");
        transact("fight", "Peyton", "Jerome", "Paris", "Honesty", "You have 3 enemies left");
        transact("fight", "Peyton", "Jerome", "Paris", "Honesty", "You have 2 enemies left");
        transact("fight", "Peyton", "Jerome", "Paris", 
                "You chase the Merchant from the battle field",
                "you come across 47 copper pieces, 7 electrum pieces, 13 gold pieces and 8 platinum pieces, worth a total of 56 gold",
                "You gain 55 experience points");
    }

}
