package jo.audio.companions.data;

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
import org.json.simple.JSONUtils;

import jo.audio.companions.logic.QueryLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.FromJSONLogic;
import jo.audio.util.IIOBean;
import jo.audio.util.ToJSONLogic;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.logic.CSVLogic;
import jo.util.utils.obj.StringUtils;

public class CompUserBean implements IIOBean
{
    public static final String TAG_ADMIN = "admin";
    public static final String TAG_BRONZE_VISIT = "visitBronze";
    public static final String TAG_SILVER_VISIT = "visitSilver";
    public static final String TAG_GOLD_VISIT = "visitGold";
    public static final String TAG_PLATINUM_VISIT = "visitPlatinum";
    public static final String TAG_BRONZE_RANGER = "rangerBronze";
    public static final String TAG_SILVER_RANGER = "rangerSilver";
    public static final String TAG_GOLD_RANGER = "rangerGold";
    public static final String TAG_PLATINUM_RANGER = "rangerPlatinum";
    public static final String TAG_BRONZE_TRAVELLER = "travellerBronze";
    public static final String TAG_SILVER_TRAVELLER = "travellerSilver";
    public static final String TAG_GOLD_TRAVELLER = "travellerGold";
    public static final String TAG_PLATINUM_TRAVELLER = "travellerPlatinum";
    public static final String TAG_BRONZE_BANKER = "bankerBronze";
    public static final String TAG_SILVER_BANKER = "bankerSilver";
    public static final String TAG_GOLD_BANKER = "bankerGold";
    public static final String TAG_PLATINUM_BANKER = "bankerPlatinum";
    
    public static final Map<String, String> CORE_TAG_HTML = new HashMap<>();
    static
    {
        CORE_TAG_HTML.put(CompUserBean.TAG_ADMIN, "<span title='system administrator'>admin</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_BRONZE_VISIT, "<span style='color:#cd7f32' title='bronze level visitor'>visitor</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_SILVER_VISIT, "<span style='color:#c0c0c0' title='silver level visitor'>visitor</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_GOLD_VISIT, "<span style='color:#ffd700' title='gold level visitor'>visitor</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_PLATINUM_VISIT, "<span style='color:#00ffff' title='platinum level visitor'>visitor</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_BRONZE_RANGER, "<span style='color:#cd7f32' title='bronze level ranger'>ranger</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_SILVER_RANGER, "<span style='color:#c0c0c0' title='silver level ranger'>ranger</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_GOLD_RANGER, "<span style='color:#ffd700' title='gold level ranger'>ranger</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_PLATINUM_RANGER, "<span style='color:#00ffff' title='platinum level ranger'>ranger</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_BRONZE_TRAVELLER, "<span style='color:#cd7f32' title='bronze level traveller'>traveller</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_SILVER_TRAVELLER, "<span style='color:#c0c0c0' title='silver level traveller'>traveller</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_GOLD_TRAVELLER, "<span style='color:#ffd700' title='gold level traveller'>traveller</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_PLATINUM_TRAVELLER, "<span style='color:#00ffff' title='platinum level traveller'>traveller</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_BRONZE_BANKER, "<span style='color:#cd7f32' title='bronze level banker'>banker</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_SILVER_BANKER, "<span style='color:#c0c0c0' title='silver level banker'>banker</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_GOLD_BANKER, "<span style='color:#ffd700' title='gold level banker'>banker</span>");
        CORE_TAG_HTML.put(CompUserBean.TAG_PLATINUM_BANKER, "<span style='color:#00ffff' title='platinum level philanthropist'>philanthropist</span>");
    }
    public static final Map<String, String> USER_TAG_KEYS = new HashMap<>();
    static
    {
        USER_TAG_KEYS.put(CompUserBean.TAG_ADMIN, "Administrator");
        USER_TAG_KEYS.put(CompUserBean.TAG_BRONZE_VISIT, CompanionsModelConst.TEXT_VISIT_BRONZE);
        USER_TAG_KEYS.put(CompUserBean.TAG_SILVER_VISIT, CompanionsModelConst.TEXT_VISIT_SILVER);
        USER_TAG_KEYS.put(CompUserBean.TAG_GOLD_VISIT, CompanionsModelConst.TEXT_VISIT_GOLD);
        USER_TAG_KEYS.put(CompUserBean.TAG_PLATINUM_VISIT, CompanionsModelConst.TEXT_VISIT_PLATINUM);
        USER_TAG_KEYS.put(CompUserBean.TAG_BRONZE_RANGER, CompanionsModelConst.TEXT_RANGER_BRONZE);
        USER_TAG_KEYS.put(CompUserBean.TAG_SILVER_RANGER, CompanionsModelConst.TEXT_RANGER_SILVER);
        USER_TAG_KEYS.put(CompUserBean.TAG_GOLD_RANGER, CompanionsModelConst.TEXT_RANGER_GOLD);
        USER_TAG_KEYS.put(CompUserBean.TAG_PLATINUM_RANGER, CompanionsModelConst.TEXT_RANGER_PLATINUM);
        USER_TAG_KEYS.put(CompUserBean.TAG_BRONZE_TRAVELLER, CompanionsModelConst.TEXT_TRAVELLER_BRONZE);
        USER_TAG_KEYS.put(CompUserBean.TAG_SILVER_TRAVELLER, CompanionsModelConst.TEXT_TRAVELLER_SILVER);
        USER_TAG_KEYS.put(CompUserBean.TAG_GOLD_TRAVELLER, CompanionsModelConst.TEXT_TRAVELLER_GOLD);
        USER_TAG_KEYS.put(CompUserBean.TAG_PLATINUM_TRAVELLER, CompanionsModelConst.TEXT_TRAVELLER_PLATINUM);
        USER_TAG_KEYS.put(CompUserBean.TAG_BRONZE_BANKER, CompanionsModelConst.TEXT_BANKER_BRONZE);
        USER_TAG_KEYS.put(CompUserBean.TAG_SILVER_BANKER, CompanionsModelConst.TEXT_BANKER_SILVER);
        USER_TAG_KEYS.put(CompUserBean.TAG_GOLD_BANKER, CompanionsModelConst.TEXT_BANKER_GOLD);
        USER_TAG_KEYS.put(CompUserBean.TAG_PLATINUM_BANKER, CompanionsModelConst.TEXT_BANKER_PLATINUM);
    }
    
    public static final String META_QUESTION = "question";
    public static final String META_MARK = "permit_mark";
    
    private String  mURI;
    private String  mLocation;
    private String  mOldLocation;
    private int     mInteractions;
    private long    mLastInteraction;
    private int     mLastMoveDirection;
    private Map<String,Long> mIntentFrequency = new HashMap<>();
    private Map<String,Long> mIntentTimestamp = new HashMap<>();
    // badge tracking
    private String  mTags;
    private String  mVisitList;
    private String  mKillList;
    private String  mBossKillList;
    private String  mBountyList;
    private float   mMaxDistance;
    private float   mTotalDistance;
    private float   mTotalEffort;
    private int     mTotalKills;
    private int     mTotalFights;
    private int     mTotalWins;
    private int     mTotalTime;
    private String  mActiveCompanion;
    private List<CompCompanionBean> mCompanions = new ArrayList<>();
    private List<CompCompanionBean> mDeadCompanions = new ArrayList<>();
    private List<CompCompanionBean> mReallyDeadCompanions = new ArrayList<>();
    private List<CompItemInstanceBean> mItems = new ArrayList<>();
    private CompEncounterBean   mEncounter = new CompEncounterBean();
    private float mGoldPieces;
    private float mMaxGoldPieces;
    private int     mLastMessage;
    private JSONObject mMetadata;
    private String  mSupportIdent;
    private String  mSupportPassword;
    // tale streamer
    private int     mTaleStreamUntil; // stop when interactions reaches this level
    private String  mTaleStreamStyle;
    private String  mTaleStreamVoice;
    private String  mTaleStreamPitch;
    private String  mTaleStreamRate;
    private long    mSubscribedUnitl;
    // transient
    private Set<String> mTagsCache;
    
    public CompUserBean()
    {
        mMetadata = new JSONObject();
    }
    
    // utility functions
    
    public boolean isTaleStreamer()
    {
        return (mTaleStreamUntil >= mInteractions) || !StringUtils.isTrivial(mTaleStreamVoice);
    }
    
    public JSONObject getQuestion()
    {
        if (mMetadata == null)
            return null;
        Object q = mMetadata.get(META_QUESTION);
        if (q == null)
            return null;
        if (!(q instanceof JSONObject))
            return null;
        JSONObject question = (JSONObject)q;
        if (!question.containsKey("text"))
            return null;
        return question;
    }
    
    public boolean isQuestion()
    {
        return getQuestion() != null;
    }
    
    public boolean isYNQuestion()
    {
        JSONObject q = getQuestion();
        return (q != null) && !QueryLogic.QUERY_TYPE_NSEW.equals(q.getString(QueryLogic.QUERY_TYPE));
    }
    
    public boolean isNSEWQuestion()
    {
        JSONObject q = getQuestion();
        return (q != null) && QueryLogic.QUERY_TYPE_NSEW.equals(q.getString(QueryLogic.QUERY_TYPE));
    }
    
    public AudioMessageBean getQuestionText()
    {
        JSONObject q = getQuestion();
        if (q == null)
            return null;
        AudioMessageBean msg = new AudioMessageBean();
        JSONObject text = JSONUtils.getObject(q, "text");
        msg.fromJSON(text);
        return msg;
    }

    public List<CompCompanionBean> getAllCompanions()
    {
        List<CompCompanionBean> comps = new ArrayList<>();
        comps.addAll(mCompanions);
        comps.addAll(mDeadCompanions);
        return comps;
    }
    
    public int getChallengeLevel()
    {
        int teamCR = 0;
        for (CompCompanionBean companion : getCompanions())
            teamCR += companion.getEffectiveLevel();
        teamCR /= 6;
        return teamCR;
    }
    
    public float getPCHitPoints()
    {
        int current = 0;
        int total = 0;
        for (CompCompanionBean comp : getCompanions())
        {
            current += comp.getCurrentHitPoints();
            total += comp.getEffectiveHitPoints();
        }
        for (CompCompanionBean comp : getDeadCompanions())
            total += comp.getEffectiveHitPoints();
        return (float)current/(float)total;
    }
    
    public boolean isNearlyDead()
    {
        return getPCHitPoints() <= .1f;
    }
    
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
    
    public CompCompanionBean getCompanion(String id)
    {
        for (CompCompanionBean c : getCompanions())
            if (c.getID().equals(id))
                return c;
        return null;
    }
    
    public CompCompanionBean getAnyCompanion(String id)
    {
        CompCompanionBean comp = getCompanion(id);
        if (comp != null)
            return comp;
        for (CompCompanionBean c : getDeadCompanions())
            if (c.getID().equals(id))
                return c;
        return null;
    }
    
    public CompItemInstanceBean getItem(String id)
    {
        for (CompItemInstanceBean c : getItems())
            if (c.getID().equals(id))
                return c;
        return null;
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
            "Visit List",
            "Tags",
            "MaxDistance",
            "TotalDistance",
            "TotalEffort",
            "TotalKills",
            "KillList",
            "TotalFights",
            "TotalWins",
            "MaxGold",
            "LastMoveDirection",
            "TotalTime",
            "LastMessage",
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
        line.add(getVisitList());
        line.add(getTags());
        line.add(getMaxDistance());
        line.add(getTotalDistance());
        line.add(getTotalEffort());
        line.add(getTotalKills());
        line.add(getKillList());
        line.add(getTotalFights());
        line.add(getTotalWins());
        line.add(getMaxGoldPieces());
        line.add(getLastMoveDirection());
        line.add(getLastMessage());
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
            setVisitList(line[idx++]);
            setTags(line[idx++]);
            setMaxDistance(Float.parseFloat(line[idx++]));
            setTotalDistance(Float.parseFloat(line[idx++]));
            setTotalEffort(Float.parseFloat(line[idx++]));
            setTotalKills(Integer.parseInt(line[idx++]));
            setKillList(line[idx++]);
            setTotalFights(Integer.parseInt(line[idx++]));
            setTotalWins(Integer.parseInt(line[idx++]));
            setMaxGoldPieces(Float.parseFloat(line[idx++]));
            setLastMoveDirection(Integer.parseInt(line[idx++]));
            setLastMessage(Integer.parseInt(line[idx++]));
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
        return new File("\\\\frey\\share\\Shared Mobile\\Virtual Assistants\\Skills and Agents\\Six Swords\\logs\\compusers.csv");   
    }

    @Override
    public File getActiveFile()
    {
        return new File("\\\\frey\\share\\Shared Mobile\\Virtual Assistants\\Skills and Agents\\Six Swords\\logs\\active_compusers.csv");   
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

    public String getVisitList()
    {
        return mVisitList;
    }

    public void setVisitList(String visitList)
    {
        mVisitList = visitList;
    }

    public float getMaxDistance()
    {
        return mMaxDistance;
    }

    public void setMaxDistance(float maxDistance)
    {
        mMaxDistance = maxDistance;
    }

    public float getTotalDistance()
    {
        return mTotalDistance;
    }

    public void setTotalDistance(float totalDistance)
    {
        mTotalDistance = totalDistance;
    }

    public float getTotalEffort()
    {
        return mTotalEffort;
    }

    public void setTotalEffort(float totalEffort)
    {
        mTotalEffort = totalEffort;
    }

    public List<CompCompanionBean> getCompanions()
    {
        return mCompanions;
    }

    public void setCompanions(List<CompCompanionBean> companions)
    {
        mCompanions = companions;
    }

    public List<CompItemInstanceBean> getItems()
    {
        return mItems;
    }

    public void setItems(List<CompItemInstanceBean> items)
    {
        mItems = items;
    }

    public String getActiveCompanion()
    {
        return mActiveCompanion;
    }

    public void setActiveCompanion(String activeCompanion)
    {
        mActiveCompanion = activeCompanion;
    }

    public CompEncounterBean getEncounter()
    {
        return mEncounter;
    }

    public void setEncounter(CompEncounterBean encounter)
    {
        mEncounter = encounter;
    }

    public List<CompCompanionBean> getDeadCompanions()
    {
        return mDeadCompanions;
    }

    public void setDeadCompanions(List<CompCompanionBean> deadCompanions)
    {
        mDeadCompanions = deadCompanions;
    }

    public float getGoldPieces()
    {
        return mGoldPieces;
    }

    public void setGoldPieces(float goldPieces)
    {
        mGoldPieces = goldPieces;
    }

    public Map<String, Long> getIntentFrequency()
    {
        return mIntentFrequency;
    }

    public void setIntentFrequency(Map<String, Long> intentFrequency)
    {
        mIntentFrequency = intentFrequency;
    }

    public String getKillList()
    {
        return mKillList;
    }

    public void setKillList(String killList)
    {
        mKillList = killList;
    }

    public int getTotalKills()
    {
        return mTotalKills;
    }

    public void setTotalKills(int totalKills)
    {
        mTotalKills = totalKills;
    }

    public int getTotalFights()
    {
        return mTotalFights;
    }

    public void setTotalFights(int totalFights)
    {
        mTotalFights = totalFights;
    }

    public int getTotalWins()
    {
        return mTotalWins;
    }

    public void setTotalWins(int totalWins)
    {
        mTotalWins = totalWins;
    }

    public float getMaxGoldPieces()
    {
        return mMaxGoldPieces;
    }

    public void setMaxGoldPieces(float maxGoldPieces)
    {
        mMaxGoldPieces = maxGoldPieces;
    }

    public int getLastMoveDirection()
    {
        return mLastMoveDirection;
    }

    public void setLastMoveDirection(int lastMoveDirection)
    {
        mLastMoveDirection = lastMoveDirection;
    }

    public String getBossKillList()
    {
        return mBossKillList;
    }

    public void setBossKillList(String bossKillList)
    {
        mBossKillList = bossKillList;
    }

    public String getBountyList()
    {
        return mBountyList;
    }

    public void setBountyList(String bountyList)
    {
        mBountyList = bountyList;
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

    public int getTotalTime()
    {
        return mTotalTime;
    }

    public void setTotalTime(int totalTime)
    {
        mTotalTime = totalTime;
    }

    public int getLastMessage()
    {
        return mLastMessage;
    }

    public void setLastMessage(int lastMessage)
    {
        mLastMessage = lastMessage;
    }

    public Map<String, Long> getIntentTimestamp()
    {
        return mIntentTimestamp;
    }

    public void setIntentTimestamp(Map<String, Long> intentTimestamp)
    {
        mIntentTimestamp = intentTimestamp;
    }

    public int getTaleStreamUntil()
    {
        return mTaleStreamUntil;
    }

    public void setTaleStreamUntil(int taleStreamUntil)
    {
        mTaleStreamUntil = taleStreamUntil;
    }

    public String getTaleStreamStyle()
    {
        return mTaleStreamStyle;
    }

    public void setTaleStreamStyle(String taleStreamStyle)
    {
        mTaleStreamStyle = taleStreamStyle;
    }

    public String getTaleStreamVoice()
    {
        return mTaleStreamVoice;
    }

    public void setTaleStreamVoice(String taleStreamVoice)
    {
        mTaleStreamVoice = taleStreamVoice;
    }

    public String getTaleStreamPitch()
    {
        return mTaleStreamPitch;
    }

    public void setTaleStreamPitch(String taleStreamPitch)
    {
        mTaleStreamPitch = taleStreamPitch;
    }

    public String getTaleStreamRate()
    {
        return mTaleStreamRate;
    }

    public void setTaleStreamRate(String taleStreamRate)
    {
        mTaleStreamRate = taleStreamRate;
    }

    public String getSupportIdent()
    {
        return mSupportIdent;
    }

    public void setSupportIdent(String supportIdent)
    {
        mSupportIdent = supportIdent;
    }

    public List<CompCompanionBean> getReallyDeadCompanions()
    {
        return mReallyDeadCompanions;
    }

    public void setReallyDeadCompanions(
            List<CompCompanionBean> reallyDeadCompanions)
    {
        mReallyDeadCompanions = reallyDeadCompanions;
    }

    public String getSupportPassword()
    {
        return mSupportPassword;
    }

    public void setSupportPassword(String supportPassword)
    {
        mSupportPassword = supportPassword;
    }

    public long getSubscribedUnitl()
    {
        return mSubscribedUnitl;
    }

    public void setSubscribedUnitl(long subscribedUnitl)
    {
        mSubscribedUnitl = subscribedUnitl;
    }
}
