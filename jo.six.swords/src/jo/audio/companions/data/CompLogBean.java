package jo.audio.companions.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.util.FromJSONLogic;
import jo.audio.util.IIOBean;
import jo.audio.util.ToJSONLogic;
import jo.util.logic.CSVLogic;

public class CompLogBean implements IIOBean
{
    public static final String KILL = "kill";
    public static final String TREASURE = "treasure";
    public static final String BUY = "buy";
    public static final String SELL = "sell";
    public static final String USE = "use";
    public static final String BOSS = "boss";
    public static final String TS = "talestreamer";
    
    private String  mURI;
    private long    mTime;
    private String  mLocation;
    private String  mAction;
    private String  mObject;
    private String  mAmount;
    private String  mComments;
    
    public CompLogBean()
    {
    }
    
    // utility functions

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

    private static final String[] mUserColumns = {
            "URI",
            "Time",
            "Location",
            "Action",
            "Object",
            "Amount",
            "Comments",
    };

    @Override
    public String[] toCSVHeader()
    {
        return mUserColumns;
    }

    @Override
    public String toCSV()
    {
        List<Object> line = new ArrayList<>();
        line.add(getURI());
        line.add(getTime());
        line.add(getLocation());
        line.add(getAction());
        line.add(getObject());
        line.add(getAmount());
        line.add(getComments());
        return CSVLogic.toCSVLine(line);
    }


    @Override
    public void fromCSV(String csv)
    {
        String[] line = CSVLogic.splitCSVLine(csv);
        int idx = 0;
        try
        {
            setURI(line[idx++]);
            setTime(Long.parseLong(line[idx++]));
            setLocation(line[idx++]);
            setAction(line[idx++]);
            setObject(line[idx++]);
            setAmount(line[idx++]);
            setComments(line[idx++]);
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {   // older data does not have all fields           
        }
        catch (NumberFormatException ex)
        {
            throw new IllegalArgumentException("Unexpected format of '"+line[idx]+"'", ex);
        }
    }


    @Override
    public File getArchiveFile()
    {
        return new File("\\\\frey\\share\\Shared Mobile\\Virtual Assistants\\Skills and Agents\\Six Swords\\logs\\complogs.csv");   
    }

    @Override
    public File getActiveFile()
    {
        return null;   
    }

    // getters and setters

    public String getURI()
    {
        return mURI;
    }

    public void setURI(String uRI)
    {
        mURI = uRI;
    }

    public long getTime()
    {
        return mTime;
    }

    public void setTime(long time)
    {
        mTime = time;
    }

    public String getLocation()
    {
        return mLocation;
    }

    public void setLocation(String location)
    {
        mLocation = location;
    }

    public String getAction()
    {
        return mAction;
    }

    public void setAction(String action)
    {
        mAction = action;
    }

    public String getObject()
    {
        return mObject;
    }

    public void setObject(String object)
    {
        mObject = object;
    }

    public String getAmount()
    {
        return mAmount;
    }

    public void setAmount(String amount)
    {
        mAmount = amount;
    }

    public String getComments()
    {
        return mComments;
    }

    public void setComments(String comments)
    {
        mComments = comments;
    }
}
