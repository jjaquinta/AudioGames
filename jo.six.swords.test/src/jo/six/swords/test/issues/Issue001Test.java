package jo.six.swords.test.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import jo.six.swords.test.Base;

public class Issue001Test extends Base
{
    @Test
    void testAboutViaCharacterName() throws IOException
    {
        transact(null, "Welcome", "who");
        transact("about Jerome", "Jerome has 7 hit points");
        transact("Jerome", "Jerome has 7 hit points");
    }

}
