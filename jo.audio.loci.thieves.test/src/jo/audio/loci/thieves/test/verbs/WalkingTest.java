package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;

class WalkingTest extends TestBase
{

    @Test
    void amble()
    {
        talk("look", "nebulous grey area");
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("Maple Street", "Flagged Square", "Paved Junction");
        talk("Paved Junction", "Maple Street", "Rosemary Lane");
        talk("Rosemary Lane", "Paved Junction", "Paved Circle");
        talk("Paved Circle", "Rosemary Lane", "Herring Street");
    }

    @Test
    void down()
    {
        talk("look", "nebulous grey area");
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("down", "Pine Street");
        talk("down", "High Class Junction");
        talk("down", "Hay Street");
        talk("down", "High Class Junction");
        talk("down", "Oath Road");
        talk("down", "Elegant Crossway");
        talk("down", "Matthew Bridge", "Aberjona");
    }

    @Test
    void up()
    {
        talk("look", "nebulous grey area");
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("up", "Maple Street");
        talk("up", "Paved Junction");
        talk("up", "Peacock Lane");
        talk("up", "Gravel Square");
        talk("up", "Cherry Lane");
    }

}
