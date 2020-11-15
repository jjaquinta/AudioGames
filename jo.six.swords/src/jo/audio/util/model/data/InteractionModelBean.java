package jo.audio.util.model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

import jo.util.utils.DebugUtils;

public class InteractionModelBean
{
    private JSONObject  mRawModel;
    private List<String> mSupportedLanguages = new ArrayList<>();
    private Map<String, String> mDisplayName = new HashMap<>();
    private Map<String, String> mInvocationName = new HashMap<>();
    private String mAppName;
    private String mURL;
    private Map<String,Map<String, Map<String, List<String>>>> mDictionaries;
    private Map<String,Map<String, FuzzyDictionary>> mFuzzyDictionaries;
    private Map<String, IntentDefBean> mIntents;
    private Set<String> mCustomSlots;
    private List<UtteranceBean> mUtterances;
    private List<IntentDefBean> mIntentsWithExamples;
    private Map<String,Map<String, List<String>>> mText;
    private Map<String, String> mDescription = new HashMap<>();
    private String mWebsiteURL;
    private String mTermsURL;
    private String mPrivacyURL;
    private Map<String, String> mKeywords = new HashMap<>();
    
    public InteractionModelBean()
    {
        mDictionaries = new HashMap<>();
        mFuzzyDictionaries = new HashMap<>();
        mIntents = new HashMap<>();
        mCustomSlots = new HashSet<>();
        mUtterances = new ArrayList<>();
        mIntentsWithExamples = new ArrayList<>();
        mText = new HashMap<>();
    }
    
    // utility functions
    public void addText(String lang, String key, String value)
    {
        Map<String,List<String>> lookup = mText.get(lang);
        if (lookup == null)
        {
            lookup = new HashMap<>();
            mText.put(lang, lookup);
        }
        List<String> options = lookup.get(key);
        if (options == null)
        {
            options = new ArrayList<>();
            lookup.put(key, options);
        }
        if (!options.contains(value))
            options.add(value);
    }
    
    public void replaceText(String lang, String key, List<String> options)
    {
        Map<String,List<String>> lookup = mText.get(lang);
        if (lookup == null)
        {
            lookup = new HashMap<>();
            mText.put(lang, lookup);
        }
        lookup.put(key, options);
    }
    
    public boolean isText(String lang, String key)
    {
        int o = key.indexOf("#");
        if (o >= 0)
            key = key.substring(0, o);
        Map<String,List<String>> lookup = mText.get(lang);
        if (lookup == null)
        {
            lookup = mText.get("en_US");
            if (lookup == null)
                return false;
        }
        List<String> options = lookup.get(key);
        if (options == null)
        {
            lookup = mText.get("en_US");
            if (lookup == null)
                return false;
            options = lookup.get(key);
            if (options == null)
                return false;
        }
        return true;
    }
    
    public List<String> getText(String lang, String key)
    {
        //DebugUtils.trace("InteractionModelBean.getText(lang="+lang+", key="+key+")");
        Map<String,List<String>> lookup = mText.get(lang);
        if (lookup == null)
        {
            //DebugUtils.trace("InteractionModelBean.getText no value in '"+lang+"'");
            lookup = mText.get("en_US");
            if (lookup == null)
            {
                //DebugUtils.trace("InteractionModelBean.getText no value in 'en_US' either!");
                return new ArrayList<>();
            }
        }
        List<String> options = getFuzzyText(lookup, key);
        //DebugUtils.trace("InteractionModelBean.getText getFuzzytext='"+options+"'");
        if (options == null)
        {
            lookup = mText.get("en_US");
            if (lookup == null)
            {
                //DebugUtils.trace("InteractionModelBean.getText no value in 'en_US' either!");
                return new ArrayList<>();
            }
            options = getFuzzyText(lookup, key);
            if (options == null)
            {
                //DebugUtils.trace("InteractionModelBean.getText no options for '"+lookup+"'");
                return new ArrayList<>();
            }
        }
        return options;
    }
    
    private List<String> getFuzzyText(Map<String,List<String>> lookup, String key)
    {
        List<String> options = lookup.get(key);
        if (options != null)
            return options;
        for (String k : lookup.keySet())
            if (k.endsWith("$"+key))
                return lookup.get(k);
        return null;
    }
    
    public Map<String, Map<String, List<String>>> getDictionary(String dictionary)
    {
        return mDictionaries.get(dictionary);
    }

    public Map<String,List<String>> getDictionary(String dictionary, String lang)
    {
        Map<String, Map<String, List<String>>> dicts = mDictionaries.get(dictionary);
        if (dicts == null)
            throw new IllegalStateException("No such dictionary '"+dictionary+"'");
        Map<String, List<String>> dict = dicts.get(lang);
        if (dict == null)
        {
            dict = dicts.get("en_US");
            if (dict == null)
                DebugUtils.critical("Cannot find dictionary=%s, lang=%s in model", dictionary, lang);
        }        
        return dict;
    }

    public List<String> getDictionary(String dictionary, String lang, String word)
    {
        Map<String, List<String>> dict = getDictionary(dictionary, lang);
        if (dict == null)
            return null;
        return dict.get(word);
    }
    
    public void addToDictionary(String dictionary, String lang, String word, String synonym)
    {
        //System.out.println("Adding "+dictionary+"/"+lang+"/"+word+"/"+synonym);
        Map<String, Map<String, List<String>>> dict1 = getDictionary(dictionary);
        if (dict1 == null)
        {
            dict1 = new HashMap<>();
            mDictionaries.put(dictionary, dict1);
        }
        Map<String, List<String>> dict2 = dict1.get(lang);
        if (dict2 == null)
        {
            dict2 = new HashMap<>();
            dict1.put(lang, dict2);
        }
        List<String> dict3 = dict2.get(word);
        if (dict3 == null)
        {
            dict3 = new ArrayList<>();
            dict2.put(word, dict3);
        }
        dict3.add(synonym);
    }

    public FuzzyDictionary getFuzzyDictionary(String dictionary, String lang)
    {
        if (!mFuzzyDictionaries.containsKey(dictionary))
            mFuzzyDictionaries.put(dictionary, new HashMap<>());
        if (!mFuzzyDictionaries.get(dictionary).containsKey(lang))
            mFuzzyDictionaries.get(dictionary).put(lang, new FuzzyDictionary(getDictionary(dictionary, lang)));
        FuzzyDictionary dict = mFuzzyDictionaries.get(dictionary).get(lang);
        if (dict == null)
        {
            dict = mFuzzyDictionaries.get(dictionary).get("en_US");
            if (dict == null)
                DebugUtils.error("Cannot find fuzzy dictionary=%s, lang=%s", dictionary, lang);
        }
        return dict;
    }

    public IntentDefBean getIntent(String id)
    {
        String oid = id;
        if (mIntents.containsKey(id))
            return mIntents.get(id);
        id = id.toLowerCase();
        if (mIntents.containsKey(id))
            return mIntents.get(id);
        if (id.startsWith("amazon."))
        {
            id = id.substring(7);
            if (mIntents.containsKey(id))
                return mIntents.get(id);
        }
        if (id.endsWith("intent"))
        {
            id = id.substring(0, id.length() - 6);
            if (mIntents.containsKey(id))
                return mIntents.get(id);
        }
        if (!oid.toLowerCase().endsWith("intent") && !oid.equals("input.unknown"))
        {   // don't print error if default intent, or unknown
            System.err.println("Cannot find intent '"+oid+"'");
            for (String k : mIntents.keySet())
                System.err.println("  ="+k);
        }
        return null;
    }

    public void setIntent(String id, IntentDefBean intent)
    {
        id = id.toLowerCase();
        if (id.startsWith("amazon."))
            id = id.substring(7);
        if (id.endsWith("intent"))
            id = id.substring(0, id.length() - 6);
        mIntents.put(id, intent);
    }
    
    // getters and setters
    
    public JSONObject getRawModel()
    {
        return mRawModel;
    }
    public void setRawModel(JSONObject rawModel)
    {
        mRawModel = rawModel;
    }
    public Map<String, IntentDefBean> getIntents()
    {
        return mIntents;
    }
    public void setIntents(Map<String, IntentDefBean> intents)
    {
        mIntents = intents;
    }
    public Set<String> getCustomSlots()
    {
        return mCustomSlots;
    }
    public void setCustomSlots(Set<String> customSlots)
    {
        mCustomSlots = customSlots;
    }
    public List<UtteranceBean> getUtterances()
    {
        return mUtterances;
    }
    public void setUtterances(List<UtteranceBean> utterances)
    {
        mUtterances = utterances;
    }

    public List<IntentDefBean> getIntentsWithExamples()
    {
        return mIntentsWithExamples;
    }

    public void setIntentsWithExamples(List<IntentDefBean> intentsWithExamples)
    {
        mIntentsWithExamples = intentsWithExamples;
    }

    public Map<String,Map<String, Map<String, List<String>>>> getDictionaries()
    {
        return mDictionaries;
    }

    public void setDictionaries(Map<String,Map<String, Map<String, List<String>>>> dictionaries)
    {
        mDictionaries = dictionaries;
    }

    public List<String> getSupportedLanguages()
    {
        return mSupportedLanguages;
    }

    public void setSupportedLanguages(List<String> supportedLanguages)
    {
        mSupportedLanguages = supportedLanguages;
    }

    public Map<String, String> getDisplayName()
    {
        return mDisplayName;
    }

    public void setDisplayName(Map<String, String> displayName)
    {
        mDisplayName = displayName;
    }

    public String getAppName()
    {
        return mAppName;
    }

    public void setAppName(String appName)
    {
        mAppName = appName;
    }

    public Map<String, String> getInvocationName()
    {
        return mInvocationName;
    }

    public void setInvocationName(Map<String, String> invocationName)
    {
        mInvocationName = invocationName;
    }

    public String getURL()
    {
        return mURL;
    }

    public void setURL(String uRL)
    {
        mURL = uRL;
    }

    public Map<String, Map<String, List<String>>> getText()
    {
        return mText;
    }

    public void setText(Map<String, Map<String, List<String>>> text)
    {
        mText = text;
    }

    public Map<String, Map<String, FuzzyDictionary>> getFuzzyDictionaries()
    {
        return mFuzzyDictionaries;
    }

    public void setFuzzyDictionaries(
            Map<String, Map<String, FuzzyDictionary>> fuzzyDictionaries)
    {
        mFuzzyDictionaries = fuzzyDictionaries;
    }

    public Map<String, String> getDescription()
    {
        return mDescription;
    }

    public void setDescription(Map<String, String> description)
    {
        mDescription = description;
    }

    public String getWebsiteURL()
    {
        return mWebsiteURL;
    }

    public void setWebsiteURL(String websiteURL)
    {
        mWebsiteURL = websiteURL;
    }

    public String getTermsURL()
    {
        return mTermsURL;
    }

    public void setTermsURL(String termsURL)
    {
        mTermsURL = termsURL;
    }

    public String getPrivacyURL()
    {
        return mPrivacyURL;
    }

    public void setPrivacyURL(String privacyURL)
    {
        mPrivacyURL = privacyURL;
    }

    public Map<String, String> getKeywords()
    {
        return mKeywords;
    }

    public void setKeywords(Map<String, String> keywords)
    {
        mKeywords = keywords;
    }
}
