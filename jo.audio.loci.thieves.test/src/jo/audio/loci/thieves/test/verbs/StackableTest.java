package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;

class StackableTest extends TestBase
{
    @Test
    void testGold()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
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
        talk("take 5 gold", "you are now carrying 5 gold");
        talk("inventory", "195 gold");
        talk("look", "4 gold");
    }
}
