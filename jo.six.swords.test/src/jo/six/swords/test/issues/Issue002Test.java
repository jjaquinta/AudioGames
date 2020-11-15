package jo.six.swords.test.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import jo.six.swords.test.Base;

public class Issue002Test extends Base
{
    @Test
    void testMisspelledSouth() throws IOException
    {
        transact(null, "Welcome", "who");
        transact("south", "South it is");
        transact("soth", "Southward we go");
    }

    @Test
    void testMisspelledMore() throws IOException
    {
        transact(null, "Welcome", "who");
        transact("more", "You can move by stating the direction you wish to move");
        transact("mroe", "Say 'who' to list your companions");
    }

    @Test
    void testMisspelledInventory() throws IOException
    {
        transact(null, "Welcome", "who");
        transact("inventory", "The group is carrying");
        transact("inventorz", "The group is carrying");
    }

}
