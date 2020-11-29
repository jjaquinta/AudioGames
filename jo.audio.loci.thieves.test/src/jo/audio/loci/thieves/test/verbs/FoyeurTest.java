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
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("logout", "Thank you for playing");
        mToken = null;
        talk("look", "outside of the city");
        talk("login Wibble Wobble", "Welcome Back Wibble", "Flagged Square", "Maple", "Park", "Pine");
    }

    @Test
    void persistence()
    {
        talk("look", "outside of the city");
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("Maple Street", "Flagged Square", "Paved Junction");
        talk("logout", "Thank you for playing");
        mToken = null;
        DataStoreLogic.clearCache();
        talk("look", "outside of the city");
        talk("login Wibble Wobble", "Welcome Back Wibble", "Flagged Square", "Maple Street", "Paved Junction");
        talk("dump here", "");
        talk("dump me", "");
        talk("enter 1", "brick walkway");
        talk("logout", "Thank you for playing");
        mToken = null;
        DataStoreLogic.clearCache();
        talk("look", "outside of the city");
        talk("login Wibble Wobble", "brick walkway");
        talk("unlock w", "you unlock");
        talk("open w", "you open");
        talk("west", "main living area");
    }

}
