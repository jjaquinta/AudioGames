package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;

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

}
