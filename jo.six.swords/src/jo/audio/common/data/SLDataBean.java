package jo.audio.common.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.util.IIOBean;
import jo.audio.util.ToJSONLogic;
import jo.util.logic.CSVLogic;

public class SLDataBean implements IIOBean
{
    private String  mKey;
    private String  mPrimaryValue;
    private StringStringMap mSecondaryValues;
    
    public SLDataBean()
    {
        mKey = "";
        mPrimaryValue = "";
        mSecondaryValues = new StringStringMap();
    }
    
    @Override
    public String getURI()
    {
        return mKey;
    }
    
    @Override
    public JSONObject toJSON()
    {
        return ToJSONLogic.toJSONFromBean(this);
    }

    @Override
    public void fromJSON(JSONObject o)
    {
    }

    private static final String[] mUserColumns = {
            "key",
            "primaryValue",
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
        line.add(getKey());
        line.add(getPrimaryValue());
        return CSVLogic.toCSVLine(line);
    }

    @Override
    public void fromCSV(String csv)
    {
        String[] line = CSVLogic.splitCSVLine(csv);
        int idx = 0;
        try
        {
            setKey(line[idx++]);
            setPrimaryValue(line[idx++]);
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {   // older data does not have all fields           
        }
        catch (NumberFormatException ex)
        {
            throw new IllegalArgumentException("Unexpected format of '"+csv+"'", ex);
        }
    }

    @Override
    public File getArchiveFile()
    {
        return new File("\\\\frey\\share\\Shared Mobile\\Virtual Assistants\\Echo\\starlanes\\logs\\data.csv");   
    }

    @Override
    public File getActiveFile()
    {
        return null;   
    }

    // getters and setters
    
    public String getKey()
    {
        return mKey;
    }
    public void setKey(String key)
    {
        mKey = key;
    }
    public String getPrimaryValue()
    {
        return mPrimaryValue;
    }
    public void setPrimaryValue(String primaryValue)
    {
        mPrimaryValue = primaryValue;
    }
    public StringStringMap getSecondaryValues()
    {
        return mSecondaryValues;
    }
    public void setSecondaryValues(StringStringMap secondaryValues)
    {
        mSecondaryValues = secondaryValues;
    }
}
