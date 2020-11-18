package jo.audio.loci.sandbox.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.sandbox.test.TestBase;

class TestInventory extends TestBase
{
    @Test
    void testWalkthrough()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("inventory", "don't carry anything");
        talk("create item named Urim", "create", "Urim");
        talk("inventory", "Urim");
        talk("create item named Thummim", "create", "Thummim");
        talk("inventory", "Urim", "Thummim");
        talk("name Urim as Super-Urim", "changed");
        talk("inventory", "Super-Urim", "Thummim");
        talk("describe Super-Urim as A clear crystal with tiny pinpricks of light in it.", "changed");
        talk("look Super-Urim", "crystal");
        talk("drop Super-Urim", "You dropped Super-Urim");
        talk("look", "Super-Urim");
        talk("drop Thummim", "You dropped Thummim");
        talk("look", "Thummim", "Super-Urim");
        talk("drop Thummim", "You are not carrying");
        talk("pick up Thummim", "You are now carrying Thummim");
        talk("i", "Thummim");
        talk("pick up Super-Urim", "You are now carrying Super-Urim");
        talk("i", "Thummim", "Super-Urim");
    }

    @Test
    void testContainers()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("inventory", "don't carry anything");
        talk("create container named Urim", "create", "Urim");
        talk("inventory", "Urim");
        talk("create container named Thummim", "create", "Thummim");
        talk("inventory", "Urim", "Thummim");
    }
}
