package jo.audio.compedit.data;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.simple.JSONObject;

import jo.audio.util.FromJSONLogic;
import jo.audio.util.IIOBean;
import jo.audio.util.ToJSONLogic;
import jo.util.logic.CSVLogic;

public class CompEditUserBean implements IIOBean
{
    private String  mURI;
    private String  mLocation;
    private String  mOldLocation;
    private int     mInteractions;
    private long    mLastInteraction;
    private int     mLastMoveDirection;
    private Map<String,Long> mIntentFrequency = new HashMap<>();
    private Map<String,Long> mIntentTimestamp = new HashMap<>();
    private List<String> mFeatures = new ArrayList<>();
    // badge tracking
    private String  mTags;
    private JSONObject mMetadata;
    // transient
    private Set<String> mTagsCache;
    
    public CompEditUserBean()
    {
        mMetadata = new JSONObject();
    }
    
    // utility functions
    public boolean isTag(String tag)
    {
        if (mTags == null)
            return false;
        if (mTagsCache == null)
        {
            mTagsCache = new HashSet<>();
            for (StringTokenizer st = new StringTokenizer(mTags, " "); st.hasMoreTokens(); )
                mTagsCache.add(st.nextToken().toLowerCase());
        }
        return mTagsCache.contains(tag.toLowerCase());
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

    private static final String[] mUserColumns = {
            "URI",
            "Location",
            "Interactions",
            "Last Interaction",
            "Tags",
            "LastMoveDirection",
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
        Date lastInteraction = new Date(getLastInteraction());
        line.add(getURI());
        line.add(getLocation());
        line.add(getInteractions());
        line.add(mDATE.format(lastInteraction));
        line.add(getTags());
        line.add(getLastMoveDirection());
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
            setLocation(line[idx++]);
            setInteractions(Integer.parseInt(line[idx++]));
            setLastInteraction(mDATE.parse(line[idx++]).getTime());
            setTags(line[idx++]);
            setLastMoveDirection(Integer.parseInt(line[idx++]));
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {   // older data does not have all fields           
        }
        catch (ParseException ex)
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
        return new File("\\\\frey\\share\\Shared Mobile\\Virtual Assistants\\Echo\\startrade\\logs\\compusers.csv");   
    }

    @Override
    public File getActiveFile()
    {
        return new File("\\\\frey\\share\\Shared Mobile\\Virtual Assistants\\Echo\\startrade\\logs\\active_compusers.csv");   
    }

    // getters and setters
    public String getLocation()
    {
        return mLocation;
    }
    public void setLocation(String location)
    {
        mLocation = location;
    }

    public String getURI()
    {
        return mURI;
    }

    public void setURI(String uRI)
    {
        mURI = uRI;
    }

    public int getInteractions()
    {
        return mInteractions;
    }

    public void setInteractions(int interactions)
    {
        mInteractions = interactions;
    }

    public long getLastInteraction()
    {
        return mLastInteraction;
    }

    public void setLastInteraction(long lastInteraction)
    {
        mLastInteraction = lastInteraction;
    }

    public String getTags()
    {
        return mTags;
    }

    public void setTags(String tags)
    {
        mTags = tags;
        mTagsCache = null;
    }

    public Map<String, Long> getIntentFrequency()
    {
        return mIntentFrequency;
    }

    public void setIntentFrequency(Map<String, Long> intentFrequency)
    {
        mIntentFrequency = intentFrequency;
    }
    public int getLastMoveDirection()
    {
        return mLastMoveDirection;
    }

    public void setLastMoveDirection(int lastMoveDirection)
    {
        mLastMoveDirection = lastMoveDirection;
    }
    public String getOldLocation()
    {
        return mOldLocation;
    }

    public void setOldLocation(String oldLocation)
    {
        mOldLocation = oldLocation;
    }

    public JSONObject getMetadata()
    {
        return mMetadata;
    }

    public void setMetadata(JSONObject metadata)
    {
        mMetadata = metadata;
    }

    public List<String> getFeatures()
    {
        return mFeatures;
    }

    public void setFeatures(List<String> features)
    {
        mFeatures = features;
    }

    public Map<String, Long> getIntentTimestamp()
    {
        return mIntentTimestamp;
    }

    public void setIntentTimestamp(Map<String, Long> intentTimestamp)
    {
        mIntentTimestamp = intentTimestamp;
    }
}
