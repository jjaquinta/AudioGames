package jo.audio.loci.sandbox.test;

import org.junit.jupiter.api.Test;

import jo.util.utils.DebugUtils;

class TestItems extends TestBase
{
    @Test
    void testItem()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("inventory", "don't carry anything");
        talk("create item named Urim", "create", "Urim");
        talk("inventory", "Urim");
        talk("look", "!Urim");
        talk("drop Urim", "dropped", "Urim");
        talk("inventory", "!Urim");
        talk("look", "Urim");
        talk("pick up Urim", "carrying", "Urim");
        talk("inventory", "Urim");
        talk("look", "!Urim");
    }
    @Test
    void testItemNegative()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("inventory", "don't carry anything");
        talk("create item named Urim", "create", "Urim");
        talk("drop me", "you cannot");
        talk("drop Urim", "dropped", "Urim");
        talk("drop Urim", "you are not carrying");
        talk("pick up me", "you cannot");
        talk("pick up Urim", "carrying", "Urim");
        talk("pick up Urim", "not in a position to be picked up");
    }
    @Test
    void testContainer()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("inventory", "don't carry anything");
        talk("create container named Urim", "create", "Urim");
        talk("inventory", "Urim");
        talk("create item named Thummim", "create", "Thummim");
        talk("inventory", "Urim", "Thummim");
        talk("open Urim", "open", "Urim");
        talk("put Thummim in Urim", "put", "Thummim", "Urim");
        talk("inventory", "Urim", "!Thummim");
        talk("look at Urim", "Urim", "Thummim");
        talk("remove Thummim from Urim", "take", "Thummim", "Urim");
        talk("inventory", "Urim", "Thummim");
        talk("look at Urim", "Urim", "!Thummim");
    }
    @Test
    void testOpenNegative()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("inventory", "don't carry anything");
        talk("create container named Urim", "create", "Urim");
        talk("open Urim", "open", "Urim");
        talk("open Urim", "already open");
        talk("close Urim", "close", "Urim");
        talk("close Urim", "already closed");
    }
    @Test
    void testContainerNegative()
    {
        talk("register Wibble with Wobble", "Welcome Wibble");
        talk("create container named Urim", "create", "Urim");
        talk("create item named Thummim", "create", "Thummim");
        talk("put me in Urim", "That is not something you can move");
        talk("put Thummim in Urim", "is not open");
        talk("open Urim", "open", "Urim");
        talk("put Thummim in Urim", "put", "Thummim", "Urim");
        talk("put Urim in Urim", "You can't put something in itself");
        talk("close Urim", "close", "Urim");
        talk("take tea out of Urim", "there is no");
        talk("take Thummim out of Urim", "not open");
        talk("open Urim", "open", "Urim");
        talk("take Thummim out of Urim", "You take", "out of");
        talk("take Thummim out of Urim", "There is no");
    }
}
