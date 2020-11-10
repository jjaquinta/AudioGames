package jo.audio.companions.data;

import java.util.Random;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;

public class DiceRollBean implements IJSONAble
{
    private int mNumber;
    private int mDice;
    private int mMod;
    private int mMult;
    
    // constructors
    public DiceRollBean()
    {
        mNumber = 1;
        mDice = 6;
        mMod = 0;
        mMult = 1;
    }
    
    public DiceRollBean(int dice)
    {
        mNumber = 1;
        mDice = dice;
        mMod = 0;
        mMult = 1;
    }
    
    public DiceRollBean(int number, int dice)
    {
        mNumber = number;
        mDice = dice;
        mMod = 0;
        mMult = 1;
    }
    
    public DiceRollBean(int number, int dice, int mod)
    {
        mNumber = number;
        mDice = dice;
        mMod = mod;
        mMult = 1;
    }
    
    public DiceRollBean(int number, int dice, int mod, int mult)
    {
        mNumber = number;
        mDice = dice;
        mMod = mod;
        mMult = mult;
    }

    public DiceRollBean(DiceRollBean r2)
    {
        this(r2.mNumber, r2.mDice, r2.mMod, r2.mMult);
    }
    
    public DiceRollBean(String txt)
    {
        this();
        fromString(txt);
    }
    
    protected void fromString(String txt)
    {
        if (txt == null)
            return;
        int o1 = txt.indexOf('d');
        if (o1 < 0)
        {
            o1 = txt.indexOf('-');
            if (o1 > 0)
            {   // range
                int low = Integer.parseInt(txt.substring(0, o1));
                int high = Integer.parseInt(txt.substring(o1 + 1));
                mMod = low - 1;
                mNumber = 1;
                mDice = high - low + 1;
                return;
            }
            mNumber = 1;
            mDice = 0;
            mMod = Integer.parseInt(txt);
            return;
        }
        if (o1 == 0)
            mNumber = 1;
        else
            mNumber = Integer.parseInt(txt.substring(0, o1));
        int o2 = skipDigits(txt, o1+1);
        if (o2 == o1 + 1)
            throw new IllegalArgumentException("Badly formed dice roll: '"+txt+"', no dice");
        mDice = Integer.parseInt(txt.substring(o1 + 1, o2));
        if (o2 < txt.length())
        {
            int o3 = o2;
            if (txt.charAt(o2) == '+')
            {
                o3 = skipDigits(txt, o2 + 1);
                mMod = Integer.parseInt(txt.substring(o2 + 1, o3));
            }
            else if (txt.charAt(o2) == '-')
            {
                o3 = skipDigits(txt, o2 + 1);
                mMod = Integer.parseInt(txt.substring(o2, o3));
            }
            if (txt.charAt(o2) == 'x')
                mMult = Integer.parseInt(txt.substring(o3 + 1));
        }
    }
    
    private int skipDigits(String txt, int start)
    {
        while (start < txt.length())
        {
            if (!Character.isDigit(txt.charAt(start)))
                break;
            start++;
        }
        return start;
    }
    
    
    // utilities
    
    public int roll(Random rnd)
    {
        return roll(rnd, mNumber, mDice, mMod, mMult);
    }
    
    public static int roll(Random rnd, int dice)
    {
        return roll(rnd, 1, dice, 0, 0);
    }
    public static int roll(Random rnd, int number, int dice)
    {
        return roll(rnd, number, dice, 0, 0);
    }
    public static int roll(Random rnd, int number, int dice, int mod)
    {
        return roll(rnd, number, dice, mod, 0);
    }
    
    public static int roll(Random rnd, int number, int dice, int mod, int mult)
    {
        int tot = 0;
        if (dice > 1)
        {
            tot = number;
            for (int i = 0; i < number; i++)
                tot += rnd.nextInt(dice);
        }
        tot += mod;
        if (mult != 0)
            tot *= mult;
        return tot;
    }
    
    public float average()
    {
        return average(mNumber, mDice, mMod, mMult);
    }
    
    public static float average(int dice)
    {
        return average(1, dice, 0, 0);
    }
    
    public static float average(int number, int dice)
    {
        return average(number, dice, 0, 0);
    }
    
    public static float average(int number, int dice, int mod)
    {
        return average(number, dice, mod, 0);
    }
    
    public static float average(int number, int dice, int mod, int mult)
    {
        float tot = 0;
        if (dice != 1)
        {
            for (int i = 0; i < number; i++)
                tot += (dice+1)/2.0;
        }
        tot += mod;
        if (mult != 0)
            tot *= mult;
        return tot;
    }
    
    public float upperQ()
    {
        return upperQ(mNumber, mDice, mMod, mMult);
    }
    
    public static float upperQ(int number, int dice, int mod, int mult)
    {
        float tot = 0;
        if (dice != 1)
        {
            for (int i = 0; i < number; i++)
                tot += (dice+1)*.75;
        }
        tot += mod;
        if (mult != 0)
            tot *= mult;
        return tot;
    }
    
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (mNumber > 1)
            sb.append(mNumber);
        sb.append("d");
        sb.append(mDice);
        if (mMod > 0)
        {
            sb.append("+");
            sb.append(mMod);
        }
        else if (mMod < 0)
            sb.append(mMod);
        if (mMult > 1)
        {
            sb.append("x");
            sb.append(mMult);
        }
        return sb.toString();
    }
    
    @Override
    public JSONObject toJSON()
    {
        return ToJSONLogic.toJSONFromBean(this);
    }
    
    @Override
    public void fromJSON(JSONObject json)
    {
        FromJSONLogic.fromJSON(this, json);
    }
    
    // getters and setters
    
    public int getNumber()
    {
        return mNumber;
    }
    public void setNumber(int number)
    {
        mNumber = number;
    }
    public int getDice()
    {
        return mDice;
    }
    public void setDice(int dice)
    {
        mDice = dice;
    }
    public int getMod()
    {
        return mMod;
    }
    public void setMod(int mod)
    {
        mMod = mod;
    }
    public int getMult()
    {
        return mMult;
    }
    public void setMult(int mult)
    {
        mMult = mult;
    }
}
