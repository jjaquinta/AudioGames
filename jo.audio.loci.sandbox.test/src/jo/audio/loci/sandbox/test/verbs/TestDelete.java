package jo.audio.loci.sandbox.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.sandbox.test.TestBase;

class TestDelete extends TestBase
{
    @Test
    void testDeleteItem()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("create item named Urim", "create", "Urim");
        talk("inventory", "Urim");
        talk("delete Urim", "You deleted");
        talk("inventory", "!Urim");
    }
    @Test
    void testDeleteContainer()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("create item named Urim", "create", "Urim");
        talk("create container named Thummim", "create", "Thummim");
        talk("inventory", "Urim", "Thummim");
        talk("open Thummim", "Thummim", "open");
        talk("put Urim in Thummim", "Urim", "Thummim");
        talk("inventory", "!Urim", "Thummim");
        talk("delete Thummim", "You deleted");
        talk("look", "Urim", "!Thummim");
    }
    @Test
    void testDeleteRoom()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("dig north", "north");
        talk("north", "New Room");
        talk("delete here", "You deleted");
        talk("look", "Entrance Hall");
    }
    @Test
    void testDeleteExit()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("dig north", "north");
        talk("north", "New Room");
        talk("look", "south");
        talk("delete south", "You deleted");
        talk("look", "!south");
    }
    @Test
    void testDeleteItemNegative()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("delete me", "You cannot");
        talk("delete here", "You cannot");
        talk("create item named Urim", "create", "Urim");
        talk("create item named Thummim", "create", "Thummim");
        talk("inventory", "Urim", "Thummim");
        talk("set Thummim to public");
        talk("drop Urim", "dropped");
        talk("drop Thummim", "dropped");
        talk("look", "Urim", "Thummim");
        mToken = null;
        talk("register Wobble with Wibble", "Welcome", "Wobble");
        talk("look", "Urim", "Thummim");
        talk("delete Urim", "cannot");
        talk("delete Thummim", "deleted");
        talk("delete Wibble", "cannot");
    }
    @Test
    void testDeleteExitNegative()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("dig north", "north");
        talk("north", "New Room");
        talk("set south to private");
        mToken = null;
        talk("register Wobble with Wibble", "Welcome", "Wobble");
        talk("look", "north");
        talk("north", "New Room");
        talk("delete south", "cannot");
        talk("home", "Entrance Hall");
        talk("delete north", "cannot");
    }
}
