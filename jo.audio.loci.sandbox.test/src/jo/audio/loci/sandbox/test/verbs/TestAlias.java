package jo.audio.loci.sandbox.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.sandbox.test.TestBase;

class TestAlias extends TestBase
{
    @Test
    void testItemAlias()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("create item named orange", "You have created a orange");
        talk("look at orange", "Small", "blue", "thing");
        talk("name orange with orange,blue", "Name changed");
        talk("look at orange", "Small", "blue", "thing");
        talk("look at blue", "Small", "blue", "thing");
        talk("inventory", "orange", "!blue");
        talk("name orange with blue,orange", "Name changed");
        talk("inventory", "!orange", "blue");
    }
    @Test
    void testExitAlias()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("dig niggle", "You dig a passage");
        talk("niggle", "New Room");
        talk("Anti-niggle", "Entrance Hall");
        talk("name niggle with niggle,n");
        talk("niggle", "New Room");
        talk("name Anti-niggle with sniggle,s");
        talk("sniggle", "Entrance Hall");
        talk("n", "New Room");
        talk("s", "Entrance Hall");
    }
    @Test
    void testExit2Alias()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("dig north", "You dig a passage");
        talk("n", "New Room");
        talk("s", "Entrance Hall");
    }
}
