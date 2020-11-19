package jo.audio.loci.sandbox.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.sandbox.test.TestBase;

class TestHelp extends TestBase
{
    @Test
    void testHelp()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("create item named Urim", "create", "Urim");
        talk("set help on Urim to info message one", "info message one");
        talk("help Urim", "info message one");
        talk("set help on me to info message two", "info message two");
        talk("help me", "info message two");
        talk("help", "for a list of");
        talk("help help", "You can ask for help on", "create container", "create item");
        talk("help create", "create container", "create item");
        talk("help create container", "create container", "!create item");
        talk("help create item", "!create container", "create item");
    }
    @Test
    void testHelpNegative()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("set help on here to info message one", "you do not own");
    }
}
