package jo.audio.loci.thieves.test.scenarios;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.TypeAheadContext;
import jo.audio.loci.core.logic.ExecuteLogic;
import jo.audio.loci.thieves.test.TestBase;

public class RandomTest extends TestBase
{
    @Test
    void randomWalk()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        talk("xyzzy", "I am unable to execute");
        LociBase player = mLastContext.getInvoker();
        for (int i = 0; i < 1000; i++)
        {
            TypeAheadContext typeAheads = ExecuteLogic.typeAhead(player);
            Set<String> all = new HashSet<>();
            all.addAll(typeAheads.getCommands());
            Assert.assertEquals("Duplicate commands returned", typeAheads.getCommands().size(), all.size());
            System.out.println(i+": "+typeAheads.getCommands().size()+" choices");
            String cmd;
            do {
                cmd = typeAheads.getCommands().get(mRandom.nextInt(typeAheads.getCommands().size()));
            } while (cmd.startsWith("dump") || cmd.startsWith("debug"));
            int o = cmd.indexOf(".*");
            if (o >= 0)
                cmd = cmd.substring(0, o) + randomWord() + cmd.substring(o + 2);
            o = cmd.indexOf("[0-9]*");
            if (o >= 0)
                cmd = cmd.substring(0, o) + randomNumber() + cmd.substring(o + 6);
            talk(cmd, "!I am unable to execute");
        }
    }
}
