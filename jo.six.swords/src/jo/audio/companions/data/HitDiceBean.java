package jo.audio.companions.data;

import java.util.Random;

public class HitDiceBean
{
    private DiceRollBean    mHitDice;
    private DiceRollBean    mSupplemental;
    
    // constructors
    public HitDiceBean(String txt)
    {
        if ("0.5".equals(txt))
            mHitDice = new DiceRollBean(1, 4);
        else if ("1-1".equals(txt))
            mHitDice = new DiceRollBean(1, 8, -1);
        else
        {
            int o1 = txt.indexOf('d');
            int o2 = txt.indexOf('+');
            int o3 = txt.indexOf('-');
            if ((o3 > 0) && (o1 < 0) && (o2 < 0))
            {
                mHitDice = new DiceRollBean(0, 0, 0);
                mSupplemental = new DiceRollBean(txt);
            }
            else
            {
                if (txt.endsWith("hp") && (o1 < 0) && (o2 < 0))
                    mHitDice = new DiceRollBean(0, 0, Integer.parseInt(txt.substring(0, txt.length() - 2)));
                else
                {
                    if (o1 < 0)
                    {
                        if (o2 >= 0)
                            txt = txt.substring(0, o2) + "d8" + txt.substring(o2);
                        else
                            txt += "d8";
                    }
                    else if ((o1 >= 0) && (o2 >= 0) && (o2 < o1))
                    {
                        mSupplemental = new DiceRollBean(txt.substring(o2 + 1));
                        txt = txt.substring(0, o2);
                    }
                    mHitDice = new DiceRollBean(txt);
                }
            }
        }
        mSupplemental = null;
    }
    
    // utilities

    @Override
    public String toString()
    {
        String str = mHitDice.toString();
        if (mSupplemental != null)
            str += " + "+mSupplemental.toString();
        return str;
    }
    
    public int roll(Random rnd)
    {
        int tot = mHitDice.roll(rnd);
        if (mSupplemental != null)
            tot += DiceRollBean.roll(rnd, mSupplemental.roll(rnd), 8);
        return tot;
    }
    
    public float average()
    {
        float tot = mHitDice.average();
        if (mSupplemental != null)
            tot += DiceRollBean.average((int)mSupplemental.average(), 8);
        return tot;
    }
    
    // getters and setters
    public DiceRollBean getHitDice()
    {
        return mHitDice;
    }
    public void setHitDice(DiceRollBean hitDice)
    {
        mHitDice = hitDice;
    }
    public DiceRollBean getSupplemental()
    {
        return mSupplemental;
    }
    public void setSupplemental(DiceRollBean supplemental)
    {
        mSupplemental = supplemental;
    }
}
