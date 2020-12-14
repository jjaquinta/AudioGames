package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;

class LookTest extends TestBase
{
    @Test
    void testIntersection()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("look", "Dirty Junction", "Maple", "Park", "Pine");
        talk("look here", "Dirty Junction", "Maple", "Park", "Pine");
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
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("Maple Street", "Dirty Junction", "Gravel Intersection");
        talk("look", "Dirty Junction", "Gravel Intersection");
        talk("look here", "Dirty Junction", "Gravel Intersection");
        talk("look Dirty Junction", "Dirty Junction");
        talk("look Gravel Intersection", "Gravel Intersection");
        talk("look Dirty", "Dirty Junction");
        talk("look Gravel", "Gravel Intersection");
        talk("look Junction", "Dirty Junction");
        talk("look Intersection", "Gravel Intersection");
    }

    @Test
    void testHouse()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("Maple Street", "Dirty Junction", "Gravel Intersection");
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
        unlock("w");
        talk("open w", "you open");
        talk("w", "Living Room");
        talk("open n", "you open");
        talk("n", "Bedroom");
        talk("look north", "Window", "wavy glass");
        talk("look Window", "Window", "wavy glass");
        talk("look through Window", "Window", "!wavy glass", "through the window", "side yard", "grass and weeds");
        talk("w", "Bedroom", "There is Bed here");
        talk("look bed", "pine", "cotton sheets");
    }

    @Test
    void testBeds()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("Maple Street", "Dirty Junction", "Gravel Intersection");
        talk("enter 1", "brick walkway");
        talk("n", "Front Yard");
        talk("n", "Front Yard");
        talk("w", "Side Yard");
        talk("open south", "open the window");
        talk("s", "Bedroom");
        talk("w", "bedroom");
        talk("look bed", "fine cotton");
        talk("e", "bedroom");
        talk("n", "Side Yard");
        talk("e", "Front Yard");
        talk("s", "Front Yard");
        talk("s", "Front Yard");
        traverse("Maple", "Gravel Intersection", "Rosemary Lane", "Cobbled Circle", "herring Street", "Gravel Crossway",
                "Kill Hare Street", "Dirty Junction", "Oath Road");
        talk("enter 1", "Front Yard");
        talk("n", "Front Yard");
        talk("n", "Front Yard");
        talk("w", "Side Yard");
        talk("open south", "open the window");
        talk("s", "Bedroom");
        talk("look bed", "straw mattress");
        talk("n", "Side Yard");
        talk("e", "Front Yard");
        talk("s", "Front Yard");
        talk("s", "Front Yard");
        traverse("Oath Road", "Muddy Crossway", "Matthew Bridge", "Muddy Intersection", "Primrose Lane", "Dirty Crossway",
                "Ford Road", "Dirty Cross", "Main Street", "Gravel Circle", "Cross Street", "Cobbled Cross",
                "Linden Street", "Paved Junction", "Clyde Road", "Flagged Square", "Dame Street", "Elegant Junction", "Mountjoy Way");
        talk("enter 1", "brick walkway");
        talk("w", "Front Porch");
        up("Roof");
        talk("w", "Roof");
        talk("n", "Roof");
        talk("n", "Roof");
        talk("open west", "open the window");
        talk("west", "Bedroom");
        talk("s", "bedroom");
        talk("look bed", "silk sheets");
    }
}
