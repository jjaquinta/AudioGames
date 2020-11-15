package jo.six.swords.test.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import jo.six.swords.test.Base;

public class Issue003Test extends Base
{
    @Test
    void testCastle() throws IOException
    {
        transact(null, "Welcome", "who");
        transact("east", "East it is");
        transact("east", "Eastward we go");
        transact("north", "Bolbec Castle");
        transact("Bolbec Castle", "High walls arch above you");
        transact("north", "West of you lies Office");
        transact("west", "The business of the castle is conducted in this room");
        transact("more", "Making the area 7 miles West and 5 miles North of here safe will earn you 1000 gold pieces");
        transact("east", "To the West you can see Office");
        transact("office", "The business of the castle is conducted in this room");
        transact("bounties", "Making the area 7 miles West and 5 miles North of here safe will earn you 1000 gold pieces");
    }
}
