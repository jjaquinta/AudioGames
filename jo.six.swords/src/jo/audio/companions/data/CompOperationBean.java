package jo.audio.companions.data;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

public class CompOperationBean implements IJSONAble
{
    public static final int QUERY = 0; // flags = str1
    public static final int MOVE = 1; // direction = int1
    public static final int ACTIVATE = 2; // id = str1
    public static final int EQUIP = 3; // companionid = str1, itemid = str2, amount = num1
    public static final int UNEQUIP = 4; // companionid = str1, itemid = str2, amount = num1
    public static final int FIGHT = 5; // noparams
    public static final int ABANDON_COMBAT = 6; // noparams
    public static final int ENTER = 7; // noparams
    public static final int BUY = 8; // itemid = str1, amount = num1
    public static final int SELL = 9; // itemid = str1, amount = num1
    public static final int HIRE = 10; // companionid = str1
    public static final int FIRE = 11; // companionid = str1
    public static final int DEBUG = 12; // command = str1
    public static final int TEXT = 13; // noparams
    public static final int ANSWER = 14; // yes/no = num1
    public static final int GETNEWS = 15; // noparams
    public static final int CONSUMENEWS = 16; // msg = num1
    public static final int SLEEP = 17; // noparams
    public static final int LINK = 18; // supportID= str1, supportPass = str2, email = str3, name = str4

    // direction
    public static final int NORTH = 1;
    public static final int SOUTH = 2;
    public static final int EAST = 3;
    public static final int WEST = 4;

    // query
    public static final int YES = 1;
    public static final int NO = 2;
    public static final int CANCEL = 3;
    
    // query flags
    public static final String RANKS = "{ranks}";
    public static final String NEARBY = "{nearby}";
    
    private int     mOperation;
    private String  mIdentID;
    private String  mFlags;
    
    private long    mNumParam1;
    private long    mNumParam2;
    private long    mNumParam3;
    private long    mNumParam4;
    private String  mStrParam1;
    private String  mStrParam2;
    private String  mStrParam3;
    private String  mStrParam4;

    // utility functions
    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("operation", mOperation);
        json.put("identID", mIdentID);
        json.put("flags", mFlags);
        json.put("numParam1", mNumParam1);
        json.put("numParam2", mNumParam2);
        json.put("numParam3", mNumParam3);
        json.put("numParam4", mNumParam4);
        json.put("strParam1", mStrParam1);
        json.put("strParam2", mStrParam2);
        json.put("strParam3", mStrParam3);
        json.put("strParam4", mStrParam4);
        return json;
    }
    @Override
    public void fromJSON(JSONObject o)
    {
        mOperation = JSONUtils.getInt(o, "operation");
        mIdentID = JSONUtils.getString(o, "identID");
        mFlags = JSONUtils.getString(o, "flags");
        mNumParam1 = JSONUtils.getLong(o, "numParam1");
        mNumParam2 = JSONUtils.getLong(o, "numParam2");
        mNumParam3 = JSONUtils.getLong(o, "numParam3");
        mNumParam4 = JSONUtils.getLong(o, "numParam4");
        mStrParam1 = JSONUtils.getString(o, "strParam1");
        mStrParam2 = JSONUtils.getString(o, "strParam2");
        mStrParam3 = JSONUtils.getString(o, "strParam3");
        mStrParam4 = JSONUtils.getString(o, "strParam4");
    }

    
    // getters and setters
    
    public int getOperation()
    {
        return mOperation;
    }
    public void setOperation(int operation)
    {
        mOperation = operation;
    }
    public long getNumParam1()
    {
        return mNumParam1;
    }
    public void setNumParam1(long numParam1)
    {
        mNumParam1 = numParam1;
    }
    public long getNumParam2()
    {
        return mNumParam2;
    }
    public void setNumParam2(long numParam2)
    {
        mNumParam2 = numParam2;
    }
    public long getNumParam3()
    {
        return mNumParam3;
    }
    public void setNumParam3(long numParam3)
    {
        mNumParam3 = numParam3;
    }
    public long getNumParam4()
    {
        return mNumParam4;
    }
    public void setNumParam4(long numParam4)
    {
        mNumParam4 = numParam4;
    }
    public String getStrParam1()
    {
        return mStrParam1;
    }
    public void setStrParam1(String strParam1)
    {
        mStrParam1 = strParam1;
    }
    public String getStrParam2()
    {
        return mStrParam2;
    }
    public void setStrParam2(String strParam2)
    {
        mStrParam2 = strParam2;
    }
    public String getStrParam3()
    {
        return mStrParam3;
    }
    public void setStrParam3(String strParam3)
    {
        mStrParam3 = strParam3;
    }
    public String getStrParam4()
    {
        return mStrParam4;
    }
    public void setStrParam4(String strParam4)
    {
        mStrParam4 = strParam4;
    }
    public String getIdentID()
    {
        return mIdentID;
    }
    public void setIdentID(String identID)
    {
        mIdentID = identID;
    }
    public String getFlags()
    {
        return mFlags;
    }
    public void setFlags(String flags)
    {
        mFlags = flags;
    }
}
