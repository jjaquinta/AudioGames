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
    @Test
    void loot()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        traverse("Maple", "Gravel", "Peacock", "Paved", "Cherry");
        talk("enter 1", "Front Yard");
        talk("w", "Front Porch");
        unlock("west");
        talk("open west", "you open");
        talk("w", "Living room");
        talk("w", "Living room");
        talk("w", "Living room");
        talk("s", "Living room");
        talk("s", "Living room", "There is chest here");
        talk("open chest", "is locked");
        unlock("chest");
        talk("look chest", "iron bound");
        talk("open chest", "you open");
        talk("look chest", "iron bound", "Inside", "silver necklace", "gold");
        talk("take silver necklace from chest", "you take");
        talk("Look at silver necklace", "worth 1000");
        talk("take gold from chest", "you take");
        talk("Look at gold", "20 gold");
        talk("inventory", "Silver necklace", "gold");
        talk("n", "Living Room");
        talk("n", "Living Room");
        talk("e", "Living Room");
        talk("e", "Living Room");
        talk("e", "Front Porch");
        talk("e", "Front Yard");
        talk("e", "Cherry");
        talk("enter 2", "Front Yard");
        talk("s", "Front Porch");
        talk("e", "Front Porch");
        unlock("s");
        talk("open s", "You open");
        talk("s", "Living Room");
        talk("w", "Living Room");
        talk("s", "Living Room");
        unlock("chest");
        talk("open chest", "you open");
        talk("look chest", "iron bound", "Inside", "silver seal", "19 gold");
        talk("take silver seal from chest", "you take");
        talk("Look at silver seal", "worth 1000");
        talk("inventory", "Silver necklace", "silver seal", "20 gold");
        talk("take gold from chest", "you take");
        talk("inventory", "Silver seal", "39 gold");
        talk("Look at gold", "39 gold");
        talk("n", "Living Room");
        talk("e", "Living Room");
        talk("n", "Front Porch");
        talk("n", "Front Yard");
        talk("w", "Front Yard");
        talk("n", "Cherry");
        traverse("Paved", "Edgewood", "Bricked", "Belmont", "Cobbled", "Mulberry",
                "Dirty", "Hay", "Dirty Junction", "Oath");
        talk("enter 13", "Front Yard");
        talk("open west", "open the");
        talk("west", "plain room");
        talk("west", "common room");
        talk("north", "prim desk");
        talk("look Louis", "buy your stolen goods");
        talk("inventory", "Silver necklace");
        talk("sell silver necklace to louis");
        talk("inventory", "!Silver necklace");
    }
}
