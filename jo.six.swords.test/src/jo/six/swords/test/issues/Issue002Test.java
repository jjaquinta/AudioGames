package jo.six.swords.test.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import jo.audio.companions.slu.CompanionsModelConst;
import jo.six.swords.test.Base;

public class Issue002Test extends Base
{
    @Test
    void testMisspelledSouth() throws IOException
    {
        transact(null, "Welcome", "who");
        transact("south", "$"+CompanionsModelConst.TEXT_YOU_TRAVEL_SOUTH);
        transact("soth", "$"+CompanionsModelConst.TEXT_YOU_TRAVEL_SOUTH);
    }

    //@Test
    void testMisspelledMore() throws IOException
    {
        transact(null, "Welcome", "who");
        transact("look");
        //transact("more", "The sign post");
        //transact("look");
        transact("mroe", "The sign post");
    }

    @Test
    void testMisspelledInventory() throws IOException
    {
        transact(null, "Welcome", "who");
        transact("inventory", "The group is carrying");
        transact("inventorz", "The group is carrying");
    }

}
