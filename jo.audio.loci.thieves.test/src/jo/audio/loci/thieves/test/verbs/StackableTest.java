package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;

class StackableTest extends TestBase
{
    @Test
    void testGold()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        traverse("Maple Street", "Gravel", "Peacock", "Paved", "Cherry");
        talk("enter 1", "Front Yard");
        talk("w", "Front Porch");
        unlock("west");
        talk("open west", "you open");
        talk("w", "Living room");
        talk("w", "Living room");
        talk("w", "Living room");
        talk("s", "Living room");
        talk("s", "Living room", "There is chest here");
        unlock("chest");
        talk("open chest", "you open");
        talk("logout", "Thank you for playing");
        mToken = null;
        promoteToAdmin();
        talk("login Wibble with Wobble", "Welcome back Wibble");
        talk("inventory", "don't carry anything");
        talk("add 100 gold");
        talk("inventory", "100 gold");
        talk("add 99 gold");
        talk("inventory", "199 gold");
        talk("logout", "Thank you for playing");
        mToken = null;
        demoteFromAdmin();
        talk("login Wibble with Wobble", "Welcome back Wibble");
        talk("look gold", "199 gold");
        talk("look 199 gold", "199 gold");
        talk("look 1 gold", "199 gold");
        talk("drop gold", "199 gold");
        talk("take gold", "199 gold");
        talk("drop 9 gold", "you dropped 9 gold");
        talk("inventory", "190 gold");
        talk("look", "9 gold");
        talk("take 5 gold", "you picked up 5 gold");
        talk("inventory", "195 gold");
        talk("look", "4 gold");
        talk("put 10 gold in chest", "You cannot put 10 of 4 items into"); // ambiguity
        talk("take 4 gold", "you picked up");
        talk("inventory", "199 gold");
        talk("look chest", "Inside");
        int gold1 = getDynamicGold();
        talk("put 10 gold in chest", "You put 10 gold into Chest");
        talk("inventory", "189 gold");
        talk("look chest", (10+gold1)+" gold");
        talk("take 5 gold from chest", "you take 5 gold");
        talk("inventory", "194 gold");
        talk("look chest", (gold1+5)+" gold");
    }
}
