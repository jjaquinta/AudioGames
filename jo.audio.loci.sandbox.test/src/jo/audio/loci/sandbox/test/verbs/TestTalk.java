package jo.audio.loci.sandbox.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.sandbox.test.TestBase;

class TestTalk extends TestBase
{
    @Test
    void testTalk()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("say hello world", "hello world");
        mToken = null;
        talk("register Wobble with Wibble", "Welcome Wobble", "!hello world");
        talk("look", "Wibble", "!AFK");
        talk("say goodbye world", "goodbye world");
        mToken = null;
        talk("login Wibble with Wobble", "Welcome back Wibble", "goodbye world");
    }

    @Test
    void testLogout()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("say hello world", "hello world");
        talk("logout", "Thank you");
        mToken = null;
        talk("register Wobble with Wibble", "Welcome Wobble", "!hello world");
        talk("look", "!Wibble", "!AFK");
        talk("say goodbye world", "goodbye world");
        mToken = null;
        talk("login Wibble with Wobble", "Welcome back Wibble", "!goodbye world");
    }

}
