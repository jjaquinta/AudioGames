package jo.audio.loci.thieves.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.thieves.slu.ThievesModelConst;

public class LociPlayer extends LociThing
{
    public static final String PROFILE = "player";
    
    public static final String ID_PASSWORD = "password";
    public static final String ID_MESSAGES = "messages";
    public static final String ID_LAST_ACTIVE = "lastActive";
    public static final String ID_ONLINE = "online";
    
    public LociPlayer(String uri)
    {
        super(uri, PROFILE);       
        setVerbProfile("VerbProfilePlayer");
    }
    
    public LociPlayer(String uri, String profile)
    {
        super(uri, profile);       
        setVerbProfile("VerbProfilePlayer");
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
            List<String> newMessages = new ArrayList<>();
            for (String m : oldMessages)
                newMessages.add(m);
            for (String m : msgs)
                if (m != null)
                {
                    m = ThievesModelConst.expand(m);
                    newMessages.add(m);
                }
            setMessages(newMessages.toArray(new String[0]));
        }
    }
    
    public String[] getAndClearMessages()
    {
        synchronized (this)
        {
            String[] oldMessages = getMessages();
            String[] newMessages = new String[0];
            setMessages(newMessages);
            return oldMessages;
        }
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
}
