package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;

class GuildTest extends TestBase
{

    @Test
    void amble()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        traverse("Pine", "Dirty Square", "Hay Street", "Dirty Junction", "Oath Road");
        talk("look 13", "Guild Hall");
        talk("enter 13", "Front Yard");
        talk("open west", "open the");
        talk("west", "plain room");
        talk("west", "common room");
        talk("north", "prim desk");
    }
}
