package jo.six.swords.test.basics;

import java.io.IOException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import jo.audio.companions.data.CompEncounterBean;
import jo.audio.companions.data.CompMonsterInstanceBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.six.swords.test.Base;

public class FightBugTest extends Base
{
    @Test
    void testLaunch() throws IOException
    {
        transact(null, "Welcome", "who");
        int doubleFights = 10;
        for (;;)
        {
            transact("about wilhemina");
            Assert.assertTrue("Expected a fight", isFightStart());
            CompMonsterInstanceBean firstMonster = getMonster();
            fightUntilOver();
            transact("sleep");
            if (isFightStart())
            {
                CompMonsterInstanceBean secondMonster = getMonster();
                System.out.println("First: "+firstMonster.getID()+", second: "+secondMonster.getID());
                transact("fight");
                if (isFightOver())
                {
                    if (firstMonster.getID().equals(secondMonster.getID()))
                        System.out.println("Quack");
                    doubleFights--;
                    if (doubleFights == 0)
                        break;
                }
                else
                    fightUntilOver();
            }
            transact("about adolph");
        }
    }

    private CompMonsterInstanceBean getMonster()
    {
        CompUserBean user = getUser();
        CompEncounterBean encounter = user.getEncounter();
        CompMonsterInstanceBean monster = encounter.getMonsters().get(0);
        return monster;
    }
    
    private boolean isFightStart()
    {
        return isHeard("$"+CompanionsModelConst.TEXT_THEY_ATTACK)
                || isHeard("$"+CompanionsModelConst.TEXT_IT_ATTACKS);
    }

    private void fightUntilOver() throws IOException
    {
        for (;;)
        {
            transact("fight");
            if (isFightOver())
                break;
        }
    }

    private boolean isFightOver()
    {
        return isHeard("$"+CompanionsModelConst.TEXT_THE_FIGHT_IS_OVER)
                || isHeard("$"+CompanionsModelConst.TEXT_EVERYONE_DIED);
    }

}
