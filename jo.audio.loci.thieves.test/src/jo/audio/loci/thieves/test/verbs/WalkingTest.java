package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;

class WalkingTest extends TestBase
{

    @Test
    void amble()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("Maple Street", "Dirty Junction", "Gravel Intersection");
        talk("Gravel Intersection", "Maple Street", "Rosemary Lane");
        talk("Rosemary Lane", "Gravel Intersection", "Cobbled Circle");
        talk("Cobbled Circle", "Rosemary Lane", "Herring Street");
    }
    @Test
    void ambleAbbreviations()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("Maple", "Dirty Junction", "Gravel Intersection");
        talk("Gravel", "Maple Street", "Rosemary Lane");
        talk("Rosemary", "Gravel Intersection", "Cobbled Circle");
        talk("Cobbled", "Rosemary Lane", "Herring Street");
    }

    @Test
    void down()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("down", "Pine Street");
        talk("down", "Dirty Square");
        talk("down", "Hay Street");
        talk("down", "Dirty Junction");
        talk("down", "Oath Road");
        talk("down", "Muddy Crossway");
        talk("down", "Matthew Bridge", "Aberjona");
    }

    @Test
    void up()
    {
        talk("look", "outside of the city");
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("up", "Maple Street");
        talk("up", "Gravel Intersection");
        talk("up", "Peacock Lane");
        talk("up", "Paved Square");
        talk("up", "Cherry Lane");
    }

    @Test
    void circleTheHouse()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("Maple Street", "Dirty Junction", "Gravel Intersection");
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
        up("simple rim");
        talk("east", "simple rim");
        down("grass and weeds");
        talk("east", "brick walkway");
        talk("north", "brick walkway");
        up("simple rim");
        talk("north", "simple rim");
        down("brick walkway");
        talk("Maple", "Dirty Junction", "Gravel Intersection");
    }


    @Test
    void throughTheHouse()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("Maple Street", "Dirty Junction", "Gravel Intersection");
        talk("enter 1", "brick walkway");
        unlock("w");
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
        unlock("w");
        talk("open w", "you open");
        talk("w", "garden");
    }
}
