package jo.audio.companions.tools.gui.edit.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.IJSONAble;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.util.beans.PCSBean;

public class PModuleBean extends PCSBean implements IJSONAble
{
    private String mID;
    private String mName;
    private String mEnabledBy;
    private String mAuthor;
    private String mAccount;
    private Map<String, Map<String,String[]>> mText = new HashMap<>();
    private List<PMonsterBean> mMonsters = new ArrayList<>();
    private List<PFeatureBean> mFeatures = new ArrayList<>();
    
    // utilities
    @Override
    public String toString()
    {
        String str = mName;
        str += " ("+getRooms()+")";
        return str;
    }
    
    private int getRooms()
    {
        int rooms = 0;
        for (PFeatureBean f : mFeatures)
            rooms += f.getRooms().size();
        return rooms;
    }
    
    @SuppressWarnings("unchecked")
    public JSONObject   toJSON()
    {
        mText.clear();
        JSONObject json = new JSONObject();
        json.put("id", mID);
        json.put("name", mName);
        json.put("enabledBy", mEnabledBy);
        json.put("author", mAuthor);
        json.put("account", mAccount);
        JSONArray monsters = new JSONArray();
        json.put("monsters", monsters);
        for (PMonsterBean monster : mMonsters)
            monsters.add(monster.toJSON());
        JSONArray features = new JSONArray();
        json.put("features", features);
        for (PFeatureBean feature : mFeatures)
            features.add(feature.toJSON(this));
        JSONObject text = new JSONObject();
        json.put("text", text);
        for (String lang : mText.keySet())
        {
            JSONObject text2 = new JSONObject();
            text.put(lang, text2);
            Map<String,String[]> langText = mText.get(lang);
            for (String key : langText.keySet())
            {
                JSONArray jtexts = new JSONArray();
                text2.put(key, jtexts);
                String[] texts = langText.get(key);
                for (String t : texts)
                    jtexts.add(t);
            }
        }
        return json;

    }
    public void fromJSON(JSONObject o)
    {
        mText.clear();
        JSONObject text = JSONUtils.getObject(o, "text");
        for (Object lang : text.keySet())
        {
            Map<String,String[]> langText = new HashMap<>();
            mText.put((String)lang, langText);
            JSONObject jlangText = (JSONObject)text.get(lang);
            for (Object key : jlangText.keySet())
            {
                JSONArray jtexts = (JSONArray)jlangText.get(key);
                String[] texts = new String[jtexts.size()];
                for (int i = 0; i < jtexts.size(); i++)
                    texts[i] = (String)jtexts.get(i);
                langText.put((String)key, texts);
            }
        }
        mID = o.getString("id");
        mName = o.getString("name");
        mEnabledBy = o.getString("enabledBy");
        mAuthor = o.getString("author");
        mAccount = o.getString("account");
        mMonsters.clear();
        for (Object oo : JSONUtils.getArray(o, "monsters"))
        {
            PMonsterBean monster = new PMonsterBean();
            monster.fromJSON((JSONObject)oo);
            mMonsters.add(monster);
        }
        mFeatures.clear();
        for (Object oo : JSONUtils.getArray(o, "features"))
        {
            PFeatureBean features = new PFeatureBean();
            features.fromJSON(this, (JSONObject)oo);
            mFeatures.add(features);
        }
    }
    
    // getters and setters
    
    public String getID()
    {
        return mID;
    }
    public void setID(String iD)
    {
        queuePropertyChange("ID", mID, iD);
        mID = iD;
        firePropertyChange();
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        queuePropertyChange("name", mName, name);
        mName = name;
        firePropertyChange();
    }
    public String getEnabledBy()
    {
        return mEnabledBy;
    }
    public void setEnabledBy(String enabledBy)
    {
        queuePropertyChange("enabledBy", mEnabledBy, enabledBy);
        mEnabledBy = enabledBy;
        firePropertyChange();
    }
    public String getAuthor()
    {
        return mAuthor;
    }
    public void setAuthor(String author)
    {
        queuePropertyChange("author", mAuthor, author);
        mAuthor = author;
        firePropertyChange();
    }
    public String getAccount()
    {
        return mAccount;
    }
    public void setAccount(String account)
    {
        queuePropertyChange("account", mAccount, account);
        mAccount = account;
        firePropertyChange();
    }
    public Map<String, Map<String, String[]>> getText()
    {
        return mText;
    }
    public void setText(Map<String, Map<String, String[]>> text)
    {
        mText = text;
    }
    public List<PMonsterBean> getMonsters()
    {
        return mMonsters;
    }
    public void setMonsters(List<PMonsterBean> monsters)
    {
        mMonsters = monsters;
    }
    public List<PFeatureBean> getFeatures()
    {
        return mFeatures;
    }
    public void setFeatures(List<PFeatureBean> features)
    {
        mFeatures = features;
    }

    
}
