package jo.audio.loci.sandbox.data;

import org.json.simple.JSONObject;

public class LociPlayer extends LociThing
{
    public static final String ID_PASSWORD = "password";
    public static final String ID_MESSAGES = "messages";
    public static final String ID_LAST_ACTIVE = "lastActive";
    public static final String ID_ONLINE = "online";
    
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
        synchronized (this)
        {
            String[] oldMessages = getMessages();
            if (oldMessages == null)
                oldMessages = new String[0];
            String[] newMessages = new String[oldMessages.length + msgs.length];
            System.arraycopy(oldMessages, 0, newMessages, 0, oldMessages.length);
            System.arraycopy(msgs, 0, newMessages, oldMessages.length, msgs.length);
            setMessages(newMessages);
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
