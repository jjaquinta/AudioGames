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
        talk("slantwise", "New Room", "new place", "exit to the South");
        talk("look", "New Room", "new place", "exit to the South");
        talk("South", "Entrance Hall", "slantwise");
        talk("dig south to New Room", "You dig a passage");
        talk("look", "south");
        talk("South", "New Room");
        talk("South", "Entrance Hall", "slantwise");
    }
    @Test
    void testExitNegative()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("dig north", "You dig a passage");
        talk("dig north", "already a passage");
        talk("set north to private", "set to private");
        talk("north", "New Room");
        talk("name here as North Room", "Name changed");
        talk("south", "Entrance Hall");
        talk("dig south", "You dig a passage");
        talk("south", "New Room");
        talk("name here as South Room", "Name changed");
        mToken = null;
        talk("register Wobble with Wibble", "Welcome Wobble");
        talk("look", "North", "South");
        talk("North", "cannot go");
        talk("South", "south room");
    }
}
