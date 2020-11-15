package jo.audio.loci.sandbox.test;

import org.junit.jupiter.api.Test;

class TestInventory extends TestBase
{
    @Test
    void testWalkthrough()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("inventory", "don't carry anything");
        talk("create thing named Urim", "create", "Urim");
        talk("inventory", "Urim");
        talk("create thing named Thummim", "create", "Thummim");
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

}
