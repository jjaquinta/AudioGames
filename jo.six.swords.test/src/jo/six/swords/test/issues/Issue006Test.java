package jo.six.swords.test.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import jo.six.swords.test.Base;

public class Issue006Test extends Base
{
    @Test
    void testWield() throws IOException
    {
        transact(null, "Welcome", "who");
        transact("who", "Your active companion is Jerome", "Paris", "Honesty", "Peyton", "Fellowship of the Bad Swords", "inventory");
        transact("about level", "Everyone is first level");
        transact("level", "Everyone is first level");
    }

}
