package jo.audio.compedit.data;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

public class CompEditOperationBean implements IJSONAble
{
    public static final int QUERY = 0; // flags = str1
    public static final int MOVE = 1; // direction = num1
    public static final int SELECT_FEATURE = 2; // featureID = num1
    public static final int NEW_FEATURE = 3; // featureName = str1
    public static final int NAME_FEATURE = 4; // featureName = str1
    public static final int NAME_ROOM = 5; // roomName = str1
    public static final int DESCRIBE_ROOM = 6; // roomDescription = str1
    public static final int DIG_ROOM = 7; // direction = num1
    public static final int MD_ROOM = 8; // key = str1, val = str2
    public static final int SET_LOCATION = 9; // x = num1, y = num2
    public static final int SET_DIMENSION = 10; // z = num1
    public static final int SET_ENABLEDBY = 11; // enableBy = str1
    public static final int DEBUG = 12; // command = str1
    public static final int LINK_ROOM = 13; // direction = num1, roomID = str1
    public static final int UNLINK_ROOM = 14; // direction = num1

    // direction
    public static final int NORTH = 1;
    public static final int SOUTH = 2;
    public static final int EAST = 3;
    public static final int WEST = 4;

    // query
    public static final int YES = 1;
    public static final int NO = 2;
    public static final int CANCEL = 3;
    
    private int     mOperation;
    private String  mIdentID;
    
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
}
