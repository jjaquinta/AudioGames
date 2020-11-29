package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;

class WalkingTest extends TestBase
{

    @Test
    void amble()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("Maple Street", "Flagged Square", "Paved Junction");
        talk("Paved Junction", "Maple Street", "Rosemary Lane");
        talk("Rosemary Lane", "Paved Junction", "Paved Circle");
        talk("Paved Circle", "Rosemary Lane", "Herring Street");
    }
    @Test
    void ambleAbbreviations()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("Maple", "Flagged Square", "Paved Junction");
        talk("Paved", "Maple Street", "Rosemary Lane");
        talk("Rosemary", "Paved Junction", "Paved Circle");
        talk("Paved", "Rosemary Lane", "Herring Street");
    }

    @Test
    void down()
    {
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
        talk("look", "outside of the city");
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("up", "Maple Street");
        talk("up", "Paved Junction");
        talk("up", "Peacock Lane");
        talk("up", "Gravel Square");
        talk("up", "Cherry Lane");
    }

    @Test
    void circleTheHouse()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("Maple Street", "Flagged Square", "Paved Junction");
        talk("enter 1", "brick walkway");
        talk("north", "brick walkway");
        talk("north", "brick walkway");
        talk("west", "grass and weeds");
        talk("west", "grass and weeds");
        talk("west", "grass and weeds");
        talk("south", "garden");
        talk("south", "garden");
        talk("south", "garden");
        talk("south", "grass and weeds");
        talk("east", "grass and weeds");
        talk("up", "simple rim");
        talk("east", "simple rim");
        talk("down", "grass and weeds");
        talk("east", "brick walkway");
        talk("north", "brick walkway");
        talk("up", "simple rim");
        talk("north", "simple rim");
        talk("down", "brick walkway");
        talk("Maple", "Flagged Square", "Paved Junction");
    }


    @Test
    void throughTheHouse()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("Maple Street", "Flagged Square", "Paved Junction");
        talk("enter 1", "brick walkway");
        talk("unlock w", "you unlock");
        talk("open w", "you open");
        talk("w", "main living area");
        talk("open n", "you open");
        talk("n", "personal sleeping room");
        talk("w", "personal sleeping room");
        talk("e", "personal sleeping room");
        talk("s", "main living area");
        talk("w", "Meals are prepared here");
        talk("s", "ceramic bath and chamber pot");
        talk("n", "Meals are prepared here");
        talk("unlock w", "you unlock");
        talk("open w", "you open");
        talk("w", "garden");
    }
}
