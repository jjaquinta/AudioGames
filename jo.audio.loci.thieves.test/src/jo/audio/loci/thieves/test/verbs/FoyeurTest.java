package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.thieves.test.TestBase;
import jo.util.utils.DebugUtils;

class FoyeurTest extends TestBase
{

    @Test
    void login()
    {
        talk("look", "nebulous grey area");
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("logout", "Thank you for playing");
        mToken = null;
        talk("look", "nebulous grey area");
        talk("login Wibble Wobble", "Welcome Back Wibble", "Flagged Square", "Maple", "Park", "Pine");
    }

    @Test
    void persistence()
    {
        talk("look", "nebulous grey area");
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("Maple Street", "Flagged Square", "Paved Junction");
        talk("logout", "Thank you for playing");
        mToken = null;
        DataStoreLogic.clearCache();
        talk("look", "nebulous grey area");
        talk("login Wibble Wobble", "Welcome Back Wibble", "Flagged Square", "Maple Street", "Paved Junction");
        talk("dump here", "");
        talk("dump me", "");
        talk("enter 1", "brick walkway");
        talk("logout", "Thank you for playing");
        mToken = null;
        DataStoreLogic.clearCache();
        talk("look", "nebulous grey area");
        talk("login Wibble Wobble", "brick walkway");
        talk("west", "main living area");
    }

}
