package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;

class LookTest extends TestBase
{
    @Test
    void testIntersection()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("look", "Flagged Square", "Maple", "Park", "Pine");
        talk("look here", "Flagged Square", "Maple", "Park", "Pine");
        talk("look maple street", "Maple Street", "69 houses");
        talk("look park street", "Park Street", "103 houses");
        talk("look pine street", "Pine Street", "103 houses");
        talk("look maple", "Maple Street", "69 houses");
        talk("look park", "Park Street", "103 houses");
        talk("look pine", "Pine Street", "103 houses");
    }
    
    @Test
    void testStreet()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("Maple Street", "Flagged Square", "Paved Junction");
        talk("look", "Flagged Square", "Paved Junction");
        talk("look here", "Flagged Square", "Paved Junction");
        talk("look flagged square", "Flagged Square");
        talk("look paved junction", "paved junction");
        talk("look flagged", "Flagged Square");
        talk("look paved", "paved junction");
        talk("look square", "Flagged Square");
        talk("look junction", "paved junction");
    }

    @Test
    void testHouse()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("Maple Street", "Flagged Square", "Paved Junction");
        talk("look 1", "Simple House", "single story", "small rooms");
        talk("look 100", "no address", "houses run from");
        talk("look 0", "no address", "houses run from");
        talk("look -1", "unable to execute");
        talk("look one", "unable to execute");
        talk("enter 1", "brick walkway");
        talk("look", "brick walkway");
        talk("look here", "brick walkway");
        talk("look west", "Outer Door", "sturdy oak", "closed", "locked");
        talk("look w", "Outer Door", "sturdy oak", "closed", "locked");
        talk("look Outer Door", "Outer Door", "sturdy oak", "closed", "locked");
        talk("look through Outer Door", "Outer Door", "sturdy oak", "closed", "locked");
        talk("unlock w", "you unlock");
        talk("open w", "you open");
        talk("w", "Living Room");
        talk("open n", "you open");
        talk("n", "Bedroom");
        talk("look north", "Window", "wavy glass");
        talk("look Window", "Window", "wavy glass");
        talk("look through Window", "Window", "!wavy glass", "through the window", "side yard", "grass and weeds");
        talk("w", "Bedroom", "There is Bed here");
        talk("look bed", "pine", "homespun");
    }
}
