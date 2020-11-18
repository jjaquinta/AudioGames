package jo.audio.loci.sandbox.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.sandbox.test.TestBase;

class TestPrivate extends TestBase
{
    @Test
    void testSet()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("create item named Urim", "create", "Urim");
        talk("set Urim to public", "is now set to public");
        talk("set Urim to public", "is already set to public");
        talk("set Urim to private", "is now set to private");
        talk("set Urim to private", "is already set to private");
    }
}
