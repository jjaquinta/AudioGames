package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;

class AdminTest extends TestBase
{

    @Test
    void promote()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("dump me", "unable to execute");
        talk("logout", "Thank you for playing");
        mToken = null;
        promoteToAdmin();
        talk("look", "outside of the city");
        talk("login Wibble Wobble", "Welcome Back Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("dump me", "!unable to execute");
    }

    @Test
    void xp()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("logout", "Thank you for playing");
        mToken = null;
        promoteToAdmin();
        talk("look", "outside of the city");
        talk("login Wibble Wobble", "Welcome Back Wibble", "Flagged Square", "Maple", "Park", "Pine");
        talk("xp 1", "one");
        talk("more", "You now have a total of 1", "more to reach level 2");
        talk("xp 10", "10");
        talk("more", "You now have a total of 11", "more to reach level 2");
        talk("xp 100", "100");
        talk("more", "You now have a total of 111", "more to reach level 2");
        talk("xp 1000", "1000");
        talk("more", "You now have a total of 1111", "more to reach level 2");
        talk("xp 1000", "1000", "level 2");
        talk("more", "You now have a total of 2111", "more to reach level 3");
    }
}
