package jo.audio.loci.thieves.test.verbs;

import org.junit.jupiter.api.Test;

import jo.audio.loci.thieves.test.TestBase;
import jo.util.utils.obj.StringUtils;

class GuildTest extends TestBase
{

    @Test
    void amble()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        traverse("Pine", "Dirty Square", "Hay Street", "Dirty Junction", "Oath Road");
        talk("look 13", "Guild Hall");
        talk("enter 13", "Front Yard");
        talk("open west", "open the");
        talk("west", "plain room");
        talk("west", "common room");
        talk("north", "prim desk");
    }
    @Test
    void loot()
    {
        talk("register Wibble with Wobble", "Welcome Wibble", "Dirty Junction", "Maple", "Park", "Pine");
        traverse("Maple", "Gravel", "Peacock", "Paved", "Cherry");
        talk("enter 1", "Front Yard");
        talk("w", "Front Porch");
        unlock("west");
        talk("open west", "you open");
        talk("w", "Living room");
        talk("w", "Living room");
        talk("w", "Living room");
        talk("s", "Living room");
        talk("s", "Living room", "There is chest here");
        talk("open chest", "is locked");
        unlock("chest");
        talk("look chest", "iron bound");
        talk("open chest", "you open");
        talk("look chest", "iron bound", "Inside are", "and", "gold");
        String item1 = StringUtils.extract(mLastReply.toLowerCase(), "inside are ", " and");
        talk("take "+item1+" from chest", "you take");
        talk("Look at "+item1, "worth");
        talk("take gold from chest", "you take");
        talk("Look at gold", " gold");
        int gold1 = getDynamicGold();
        talk("inventory", item1, gold1+" gold");
        talk("n", "Living Room");
        talk("n", "Living Room");
        talk("e", "Living Room");
        talk("e", "Living Room");
        talk("e", "Front Porch");
        talk("e", "Front Yard");
        talk("e", "Cherry");
        talk("enter 2", "Front Yard");
        talk("s", "Front Porch");
        talk("e", "Front Porch");
        unlock("s");
        talk("open s", "You open");
        talk("s", "Living Room");
        talk("w", "Living Room");
        talk("s", "Living Room");
        unlock("chest");
        talk("open chest", "you open");
        talk("look chest", "iron bound", "Inside are ", " and", " gold");
        String item2 = StringUtils.extract(mLastReply.toLowerCase(), "inside are ", " and");
        int gold2 = getDynamicGold();
        talk("take "+item2+" from chest", "you take");
        talk("Look at "+item2, "worth");
        talk("inventory", item1, item2, gold1+" gold");
        talk("take gold from chest", "you take");
        talk("inventory", item2, (gold1+gold2)+" gold");
        talk("Look at gold", (gold1+gold2)+" gold");
        talk("n", "Living Room");
        talk("e", "Living Room");
        talk("n", "Front Porch");
        talk("n", "Front Yard");
        talk("w", "Front Yard");
        talk("n", "Cherry");
        traverse("Paved", "Edgewood", "Bricked", "Belmont", "Cobbled", "Mulberry",
                "Dirty", "Hay", "Dirty Junction", "Oath");
        talk("enter 13", "Front Yard");
        talk("open west", "open the");
        talk("west", "plain room");
        talk("west", "common room");
        talk("north", "prim desk");
        talk("look Louis", "buy your stolen goods");
        talk("inventory", item1);
        talk("ask louis to appraise "+item1, "Louis would give you", item1);
        int gold3 = getDynamicGold();
        talk("help standing", "unofficial");
        talk("sell "+item1+" to louis", gold3+" gold");
        talk("inventory", "!"+item1, (gold1+gold2+gold3)+" gold");
        talk("help standing", "inferior");
        talk("ask louis about "+item2, "Louis would give you", item2);
        int gold4 = getDynamicGold();
        talk("sell "+item2+" to louis", "for "+gold4);
        talk("more", "now have a total of "+(gold3+gold4), "you need "+(1250-gold3-gold4)+" more to reach level 2");
        talk("inventory", "!"+item2, (gold1+gold2+gold3+gold4)+" gold");
        talk("help standing", "!unofficial");
    }
}
