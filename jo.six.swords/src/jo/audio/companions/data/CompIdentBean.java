package jo.audio.companions.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.util.FromJSONLogic;
import jo.audio.util.IIOBean;
import jo.audio.util.ToJSONLogic;
import jo.util.logic.CSVLogic;

public class CompIdentBean implements IIOBean
{
    private String  mURI;
    private String  mUserID;
    private String  mGivenName;
    private String  mFamilyName;
    private String  mDisplayName;
    private String  mCoarseLocation;
    private String  mPreciseLocation;
    private String  mEmail;
    private String  mPassword;

    public CompIdentBean()
    {
    }

    // utility functions

    @Override
    public JSONObject toJSON()
    {
        return ToJSONLogic.toJSONFromBean(this);
    }

    @Override
    public void fromJSON(JSONObject o)
    {
        FromJSONLogic.fromJSON(this, o);
    }

    private static final String[] mUserColumns = {
            "URI",
            "UserID",
            "GivenName",
            "FamilyName",
            "DisplayName",
            "CoarseLocation",
            "PreciseLocation",
            "Email",
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
        line.add(getUserID());
        line.add(getEmail());
        line.add(getGivenName());
        line.add(getFamilyName());
        line.add(getDisplayName());
        line.add(getCoarseLocation());
        line.add(getPreciseLocation());
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
            setUserID(line[idx++]);
            setEmail(line[idx++]);
            setGivenName(line[idx++]);
            setFamilyName(line[idx++]);
            setDisplayName(line[idx++]);
            setCoarseLocation(line[idx++]);
            setPreciseLocation(line[idx++]);
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {   // older data does not have all fields           
        }
    }

    @Override
    public File getArchiveFile()
    {
        if (System.getProperty("starlanes.dir.reports") != null)
            return new File(System.getProperty("starlanes.dir.reports"), "idents.csv");   
        else
            return new File("\\\\frey\\share\\Shared Mobile\\Virtual Assistants\\Echo\\starlanes\\logs\\idents.csv");   
    }

    @Override
    public File getActiveFile()
    {
        if (System.getProperty("starlanes.dir.reports") != null)
            return new File(System.getProperty("starlanes.dir.reports"), "active_idents.csv");   
        else
            return new File("\\\\frey\\share\\Shared Mobile\\Virtual Assistants\\Echo\\starlanes\\logs\\active_idents.csv");   
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

    public String getUserID()
    {
        return mUserID;
    }

    public void setUserID(String userID)
    {
        mUserID = userID;
    }

    public String getGivenName()
    {
        return mGivenName;
    }

    public void setGivenName(String givenName)
    {
        mGivenName = givenName;
    }

    public String getFamilyName()
    {
        return mFamilyName;
    }

    public void setFamilyName(String familyName)
    {
        mFamilyName = familyName;
    }

    public String getDisplayName()
    {
        return mDisplayName;
    }

    public void setDisplayName(String displayName)
    {
        mDisplayName = displayName;
    }

    public String getCoarseLocation()
    {
        return mCoarseLocation;
    }

    public void setCoarseLocation(String coarseLocation)
    {
        mCoarseLocation = coarseLocation;
    }

    public String getPreciseLocation()
    {
        return mPreciseLocation;
    }

    public void setPreciseLocation(String preciseLocation)
    {
        mPreciseLocation = preciseLocation;
    }

    public String getEmail()
    {
        return mEmail;
    }

    public void setEmail(String email)
    {
        mEmail = email;
    }

    public String getPassword()
    {
        return mPassword;
    }

    public void setPassword(String password)
    {
        mPassword = password;
    }
    
}
