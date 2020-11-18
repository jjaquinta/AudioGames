package jo.audio.loci.sandbox.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.sandbox.test.TestBase;

class TestDig extends TestBase
{
    @Test
    void testHappyPath()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("dig north", "You dig a passage");
        talk("look", "north");
        talk("look north", "rough hewn");
        talk("name north as slantwise", "name changed");
        talk("look", "slantwise");
        talk("look slantwise", "rough hewn");
        talk("describe slantwise as A crack in the wall leads into bent space time.", "description changed");
        talk("look slantwise", "space time");
        talk("set slantwise to private", "is now set to private");
        talk("set slantwise to public", "is now set to public");
    }
}
