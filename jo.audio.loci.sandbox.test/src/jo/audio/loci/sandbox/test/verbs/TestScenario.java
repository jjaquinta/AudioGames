package jo.audio.loci.sandbox.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.sandbox.test.TestBase;

class TestScenario extends TestBase
{
    @Test
    void testWalkthrough()
    {
        talk("look", "Foyeur", "nebulous grey area", "Amadan");
        talk("help", "You can ask for help on");
        talk("?", "You can ask for help on");
        talk("look Amadan", "Amadan", "transparent entity");
        talk("register Wibble Wobble", "Welcome Wibble");
        talk("look", "Entrance Hall", "Wibble");
        mToken = null;
        mUserName = null;
        mPassword = null;
        talk("look", "Foyeur", "nebulous grey area", "Amadan");
        talk("login Wibble Wobble", "Welcome back");
        talk("look", "Entrance Hall", "Wibble");
        talk("look me", "Wibble", "non-descript");
        talk("describe me as Tall, statuesque, and devilishly handsome nerd", "description changed");
        talk("look me", "Wibble", "statuesque");
    }

}
