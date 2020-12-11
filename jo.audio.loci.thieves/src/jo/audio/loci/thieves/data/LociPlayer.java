package jo.audio.loci.thieves.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.utils.ResponseUtils;
import jo.audio.loci.thieves.logic.MessageLogic;
import jo.audio.thieves.slu.ThievesModelConst;
import jo.util.utils.ArrayUtils;
import jo.util.utils.obj.StringUtils;

public class LociPlayer extends LociThing
{
    public static final String ID_PASSWORD = "password";
    public static final String ID_MESSAGES = "messages";
    public static final String ID_MORE = "more";
    public static final String ID_MESSAGES_FETCHED = "messagesFetched";
    public static final String ID_LAST_ACTIVE = "lastActive";
    public static final String ID_ONLINE = "online";
    public static final String ID_PROMPT_FREQUENCY = "promptFrequency";

    public static final String ID_RACE = "race";
    public static final String ID_GENDER = "gender";
    public static final String ID_STR = "str";
    public static final String ID_INT = "int";
    public static final String ID_WIS = "wis";
    public static final String ID_CON = "con";
    public static final String ID_DEX = "dex";
    public static final String ID_CHA = "cha";
    public static final String ID_LEVEL = "level";
    public static final String ID_XP = "xp";
    public static final String ID_HIT_POINTS = "hitPoints";
    public static final String ID_CURRENT_HIT_POINTS = "currentHitPoints";
    public static final String ID_STANDING = "standing";
    
    public LociPlayer(String uri)
    {
        super(uri);       
    }
    
    public LociPlayer(JSONObject json)
    {
        super(json);
    }

    // utils
    public void addMessage(String... msgs)
    {
        if (msgs == null)
            return;
        if (msgs.length == 0)
            return;
        synchronized (this)
        {
            String[] oldMessages = getMessages();
            if (oldMessages == null)
                oldMessages = new String[0];
            String[] oldMore = getMore();
            if (oldMore == null)
                oldMore = new String[0];
            if (getMessagesFetched())
            {
                oldMore = new String[0];
                setMessagesFetched(false);
            }
            List<String> newMessages = new ArrayList<>();
            List<String> newMore = new ArrayList<>();
            ArrayUtils.addAll(newMessages, oldMessages);
            ArrayUtils.addAll(newMore, oldMore);
            for (String m : msgs)
                if (m != null)
                {
                    m = ThievesModelConst.expand(m);
                    if (m.startsWith("+"))
                        newMore.add(m.substring(1));
                    else
                        newMessages.add(m);
                }
            setMessages(newMessages.toArray(new String[0]));
            setMore(newMore.toArray(new String[0]));
        }
    }
    
    public void addMore()
    {
        synchronized (this)
        {
            String[] oldMessages = getMessages();
            if (oldMessages == null)
                oldMessages = new String[0];
            String[] oldMore = getMore();
            if (oldMore == null)
                oldMore = new String[0];
            List<String> newMessages = new ArrayList<>();
            List<String> newMore = new ArrayList<>();
            ArrayUtils.addAll(newMessages, oldMessages);
            for (String m : oldMore)
            {
                if (m.startsWith("+"))
                    newMore.add(m.substring(1));
                else
                    newMessages.add(m);
            }
            setMessages(newMessages.toArray(new String[0]));
            setMore(newMore.toArray(new String[0]));
        }
    }
    
    public String[] getAndClearMessages()
    {
        synchronized (this)
        {
            List<String> oldMessages = new ArrayList<>();
            ArrayUtils.addAll(oldMessages, getMessages());
            String[] newMessages = new String[0];
            String[] more = getMore();
            setMessages(newMessages);
            setMessagesFetched(true);
            List<String> reply = new ArrayList<>();
            for (String msg : oldMessages)
            {
                msg = MessageLogic.processMessage(this, msg);
                if (!StringUtils.isTrivial(msg))
                    reply.add(msg);
            }
            if ((reply.size() > 0) && (more != null) && (more.length > 0))
                reply.add(MessageLogic.processMessage(this, "<<Say more for additional info.|5|More?>>"));
            return reply.toArray(new String[0]);
        }
    }

    public void promnoteToAdmin()
    {
        mProperties.put(ID_DATA_PROFILE, LociPlayerAdmin.class.getSimpleName());
        DataStoreLogic.save(this);
        DataStoreLogic.clearCache();
    }
    
    public LociLocality getContainedByObject()
    {
        return (LociLocality)DataStoreLogic.load(getContainedBy());
    }

    public int getStrModified()
    {
        return getStr(); //getStatModified(mSTR, CompEffectTypeBean.STRENGTH_PLUS, CompEffectTypeBean.STRENGTH_SET);
    }

    public int getIntModified()
    {
        return getInt(); //getStatModified(mINT, CompEffectTypeBean.INTELLIGENCE_PLUS, CompEffectTypeBean.INTELLIGENCE_SET);
    }

    public int getWisModified()
    {
        return getWis(); //getStatModified(mWIS, CompEffectTypeBean.WISDOM_PLUS, CompEffectTypeBean.WISDOM_SET);
    }

    public int getConModified()
    {
        return getCon(); //getStatModified(mCON, CompEffectTypeBean.CONSTITUTION_PLUS, CompEffectTypeBean.CONSTITUTION_SET);
    }

    public int getDexModified()
    {
        return getDex(); //getStatModified(mDEX, CompEffectTypeBean.DEXTERITY_PLUS, CompEffectTypeBean.DEXTERITY_SET);
    }

    public int getChaModified()
    {
        return getCha(); //getStatModified(mCHA, CompEffectTypeBean.CHARISMA_PLUS, CompEffectTypeBean.CHARISMA_SET);
    }

    @Override
    public String[] getExtendedDescription(LociPlayer wrt)
    {
        List<String> desc = new ArrayList<>();
        ArrayUtils.addAll(desc, super.getExtendedDescription(wrt));
        int hp = getHitPoints();
        int chp = getCurrentHitPoints();
        if (chp == hp)
            desc.add("+You are at full health with "+chp+" hit points, and are experience level "+getLevel()+".");
        else
            desc.add("+You have "+chp+" out of "+hp+" hit points, and are experience level "+getLevel()+".");
        List<String> stats = new ArrayList<>();
        stats.add("strength of "+getStr());
        stats.add("dexterity of "+getDex());
        stats.add("constitution of "+getCon());
        stats.add("intelligence of "+getInt());
        stats.add("wisdom of "+getWis());
        stats.add("charisma of "+getCha());
        desc.add("++You have "+ResponseUtils.wordList(stats)+".");
        List<String> bonus = new ArrayList<>();
        if (getStr() != getStrModified())
            bonus.add("strength of "+getStrModified());
        if (getDex() != getDexModified())
            bonus.add("dexterity of "+getDexModified());
        if (getCon() != getConModified())
            bonus.add("constitution of "+getConModified());
        if (getInt() != getIntModified())
            bonus.add("intelligence of "+getIntModified());
        if (getWis() != getWisModified())
            bonus.add("wisdom of "+getWisModified());
        if (getCha() != getChaModified())
            bonus.add("charisma of "+getChaModified());
        if (bonus.size() > 0)
            desc.add("++You currently have "+ResponseUtils.wordList(stats)+".");
        return desc.toArray(new String[0]);
    }
    
    // getters and setters
    
    public String getPassword()
    {
        return getString(ID_PASSWORD);
    }
    
    public void setPassword(String value)
    {
        setString(ID_PASSWORD, value);
    }
    
    public String[] getMessages()
    {
        return getStringArray(ID_MESSAGES);
    }
    
    public void setMessages(String[] value)
    {
        setStringArray(ID_MESSAGES, value);
    }
    
    public String[] getMore()
    {
        return getStringArray(ID_MORE);
    }
    
    public void setMore(String[] value)
    {
        setStringArray(ID_MORE, value);
    }
    
    public long getLastActive()
    {
        return getLong(ID_LAST_ACTIVE);
    }
    
    public void setLastActive(long value)
    {
        setLong(ID_LAST_ACTIVE, value);
    }
    
    public boolean getOnline()
    {
        return getBoolean(ID_ONLINE);
    }
    
    public void setOnline(boolean value)
    {
        setBoolean(ID_ONLINE, value);
    }
    
    public String getRace()
    {
        return getString(ID_RACE);
    }
    
    public void setRace(String value)
    {
        setString(ID_RACE, value);
    }
    
    public String getGender()
    {
        return getString(ID_GENDER);
    }
    
    public void setGender(String value)
    {
        setString(ID_GENDER, value);
    }
    
    public int getStr()
    {
        return getInt(ID_STR);
    }
    
    public void setStr(int value)
    {
        setInt(ID_STR, value);
    }
    
    public int getInt()
    {
        return getInt(ID_INT);
    }
    
    public void setInt(int value)
    {
        setInt(ID_INT, value);
    }
    
    public int getWis()
    {
        return getInt(ID_WIS);
    }
    
    public void setWis(int value)
    {
        setInt(ID_WIS, value);
    }
    
    public int getCon()
    {
        return getInt(ID_CON);
    }
    
    public void setCon(int value)
    {
        setInt(ID_CON, value);
    }
    
    public int getDex()
    {
        return getInt(ID_DEX);
    }
    
    public void setDex(int value)
    {
        setInt(ID_DEX, value);
    }
    
    public int getCha()
    {
        return getInt(ID_CHA);
    }
    
    public void setCha(int value)
    {
        setInt(ID_CHA, value);
    }
    
    public int getLevel()
    {
        return getInt(ID_LEVEL);
    }
    
    public void setLevel(int value)
    {
        setInt(ID_LEVEL, value);
    }
    
    public int getXP()
    {
        return getInt(ID_XP);
    }
    
    public void setXP(int value)
    {
        setInt(ID_XP, value);
    }
    
    public int getHitPoints()
    {
        return getInt(ID_HIT_POINTS);
    }
    
    public void setHitPoints(int value)
    {
        setInt(ID_HIT_POINTS, value);
    }
    
    public int getCurrentHitPoints()
    {
        return getInt(ID_CURRENT_HIT_POINTS);
    }
    
    public void setCurrentHitPoints(int value)
    {
        setInt(ID_CURRENT_HIT_POINTS, value);
    }
    
    public boolean getMessagesFetched()
    {
        return getBoolean(ID_MESSAGES_FETCHED);
    }
    
    public void setMessagesFetched(boolean value)
    {
        setBoolean(ID_MESSAGES_FETCHED, value);
    }
    
    public int getPromptFrequency(String prompt)
    {
        return getInt(ID_PROMPT_FREQUENCY+"."+StringUtils.escape(prompt, "\"\'"));
    }
    
    public void setPromptFrequency(String prompt, int value)
    {
        setInt(ID_PROMPT_FREQUENCY+"."+StringUtils.escape(prompt, "\"\'"), value);
    }
    
    public int getStanding()
    {
        return getInt(ID_STANDING);
    }
    
    public void setStanding(int value)
    {
        setInt(ID_STANDING, value);
    }
}
