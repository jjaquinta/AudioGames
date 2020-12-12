package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;

class AdminTest extends TestBase
{

    @Test
    void promote()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("dump me", "unable to execute");
        talk("logout", "Thank you for playing");
        mToken = null;
        promoteToAdmin();
        talk("look", "outside of the city");
        talk("login Wibble Wobble", "Welcome Back Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("dump me", "!unable to execute");
    }

    @Test
    void xp()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("logout", "Thank you for playing");
        mToken = null;
        promoteToAdmin();
        talk("look", "outside of the city");
        talk("login Wibble Wobble", "Welcome Back Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("add 1 xp", "one");
        talk("more", "You now have a total of 1", "more to reach level 2");
        talk("add 10 xp", "10");
        talk("more", "You now have a total of 11", "more to reach level 2");
        talk("add 100 xp", "100");
        talk("more", "You now have a total of 111", "more to reach level 2");
        talk("add 1000 xp", "1000");
        talk("more", "You now have a total of 1111", "more to reach level 2");
        talk("add 1000 xp", "1000", "level 2");
        talk("more", "You now have a total of 2111", "more to reach level 3");
    }
}
