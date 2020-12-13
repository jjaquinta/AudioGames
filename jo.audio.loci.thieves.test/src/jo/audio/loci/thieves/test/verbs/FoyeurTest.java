package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.test.TestBase;

class FoyeurTest extends TestBase
{

    @Test
    void login()
    {
        talk("look", "outside of the city");
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("logout", "Thank you for playing");
        mToken = null;
        talk("look", "outside of the city");
        talk("login Wibble Wobble", "Welcome Back Wibble", "Dirty Junction", "Maple", "Park", "Pine");
    }

    @Test
    void persistence()
    {
        talk("look", "outside of the city");
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("Maple Street", "Dirty Junction", "Gravel Intersection");
        talk("logout", "Thank you for playing");
        mToken = null;
        DataStoreLogic.clearCache();
        talk("look", "outside of the city");
        talk("login Wibble Wobble", "Welcome Back Wibble", "Dirty Junction", "Maple Street", "Gravel Intersection");
        talk("dump here", "");
        talk("dump me", "");
        talk("enter 1", "brick walkway");
        talk("logout", "Thank you for playing");
        mToken = null;
        DataStoreLogic.clearCache();
        talk("look", "outside of the city");
        talk("login Wibble Wobble", "brick walkway");
        unlock("w");
        talk("open w", "you open");
        talk("west", "main living area");
    }

}
