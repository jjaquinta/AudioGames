package jo.audio.loci.sandbox.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.sandbox.test.TestBase;

class TestScenario extends TestBase
{
    @Test
    void testWalkthrough()
    {
        talk("look", "Foyeur", "nebulous grey area", "Amadan");
        talk("help", "list of commands");
        talk("?", "list of commands");
        talk("look Amadan", "Amadan", "transparent entity");
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("look", "Entrance Hall", "wonderful, welcoming", "Wibble");
        mToken = null;
        mUserName = null;
        mPassword = null;
        talk("look", "Foyeur", "nebulous grey area", "Amadan");
        talk("login Wibble with Wobble", "Welcome back");
        talk("look", "Entrance Hall", "wonderful, welcoming", "Wibble");
        talk("look me", "Wibble", "non-descript");
        talk("describe me as Tall, statuesque, and devilishly handsome nerd", "description changed");
        talk("look me", "Wibble", "statuesque");
    }

}
