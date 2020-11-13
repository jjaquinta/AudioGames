package jo.audio.loci.sandbox.test;

import org.junit.jupiter.api.Test;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.logic.ExecuteLogic;

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
    }

}
