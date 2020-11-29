package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;

class MoreTest extends TestBase
{

    @Test
    void basic()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("look me", "Say More");
        talk("more", "say more");
        talk("more", "!say more");
        talk("more", "nothing further");
        talk("look here", "Say More");
        talk("more", "!say more");
        talk("Maple");
        talk("look here", "Say More");
        talk("more", "!say more");
    }
    @Test
    void negative()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("more", "!say more");
        talk("more", "nothing further");
        talk("look me", "Say More");
        talk("more", "say more");
        talk("more", "!say more");
        talk("more", "nothing further");
        talk("look pine");
        talk("more", "nothing further");
    }
}
