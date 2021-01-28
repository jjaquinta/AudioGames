package jo.audio.companions.tools;

import java.io.File;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.util.utils.io.FileUtils;

public class LangAdd
{
    private String[]    mArgs;
    private String      mNewLang;
    private File        mInFile;
    private File        mOutFile;
    private boolean     mPretty = false;
        
    public LangAdd(String[] args)
    {
        mArgs = args;
    }
    
    public void run() throws IOException
    {
        parseArgs();
        JSONObject json = JSONUtils.readJSON(mInFile);
        extendPreface(json);
        extendUtterances(json);
        extendExamples(json);
        extendDictionaries(json);
        extendText(json);
        if (mPretty)
            FileUtils.writeFile(JSONUtils.toFormattedString(json), mOutFile);
        else
            JSONUtils.writeJSON(mOutFile, json);
    }
    
    @SuppressWarnings("unchecked")
    private void extendPreface(JSONObject json)
    {
        JSONArray supportedLanguages = JSONUtils.getArray(json, "supportedLanguages");
        boolean present = false;
        for (int i = 0; i < supportedLanguages.size(); i++)
            if (mNewLang.equals(supportedLanguages.get(i)))
                present = true;
        if (!present)
            supportedLanguages.add(mNewLang);
        JSONObject displayName = JSONUtils.getObject(json, "displayName");
        if (!displayName.containsKey(mNewLang))
            displayName.put(mNewLang, translateFlatText(displayName.getString("en_US")));
        JSONObject invocationName = JSONUtils.getObject(json, "invocationName");
        if (!invocationName.containsKey(mNewLang))
            invocationName.put(mNewLang, translateFlatText(invocationName.getString("en_US")));
    }

    public String translateFlatText(String oldText)
    {
        String newText = oldText+"[ORI:"+oldText+" todo]";
        return newText;
    }

    public String translateSSMLText(String oldText)
    {
        String newText = oldText+"<phoneme>ORI:"+oldText+" [todo]</phoneme>";
        return newText;
    }

    @SuppressWarnings("unchecked")
    private void extendUtterances(JSONObject json)
    {
        JSONArray intents = JSONUtils.getArray(json, "intents");
        for (int i = 0; i < intents.size(); i++)
        {
            JSONObject intent = (JSONObject)intents.get(i);
            JSONObject utterances = JSONUtils.getObject(intent, "utterances");
            if (!utterances.containsKey(mNewLang))
            {
                JSONArray oldUtt = (JSONArray)utterances.get("en_US");
                JSONArray newUtt = new JSONArray();
                for (int j = 0; j < oldUtt.size(); j++)
                    newUtt.add(translateFlatText((String)oldUtt.get(j)));
                utterances.put(mNewLang, newUtt);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void extendExamples(JSONObject json)
    {
        JSONArray intents = JSONUtils.getArray(json, "intents");
        for (int i = 0; i < intents.size(); i++)
        {
            JSONObject intent = (JSONObject)intents.get(i);
            JSONObject examples = JSONUtils.getObject(intent, "examples");
            if ((examples != null) && !examples.containsKey(mNewLang))
            {
                JSONArray oldUtt = (JSONArray)examples.get("en_US");
                JSONArray newUtt = new JSONArray();
                for (int j = 0; j < oldUtt.size(); j++)
                {
                    String oldText = (String)oldUtt.get(j);
                    String newText = translateSSMLText(oldText);
                    newUtt.add(newText);
                }
                examples.put(mNewLang, newUtt);
            }
        }
    }

    private void extendDictionaries(JSONObject json)
    {
        // TODO Auto-generated method stub
        
    }

    @SuppressWarnings("unchecked")
    private void extendText(JSONObject json)
    {
        JSONObject text = JSONUtils.getObject(json, "text");
        JSONObject oldLang = JSONUtils.getObject(text, "en_US");
        JSONObject newLang = JSONUtils.getObject(text, mNewLang);
        if (newLang == null)
        {
            newLang = new JSONObject();
            text.put(mNewLang, newLang);
        }
        for (String key : oldLang.keySet())
        {
            JSONArray oldTxt = (JSONArray)oldLang.get(key);
            JSONArray newTxt = (JSONArray)newLang.get(key);
            if (newTxt != null)
                continue;
            newTxt = new JSONArray();
            newLang.put(key, newTxt);
            for (int j = 0; j < oldTxt.size(); j++)
            {
                String oldText = (String)oldTxt.get(j);
                String newText = translateSSMLText(oldText);
                newTxt.add(newText);
            }
        }
    }

    private void parseArgs()
    {
        for (int i = 0; i < mArgs.length; i++)
            if ("-lang".equals(mArgs[i]))
                mNewLang = mArgs[++i];
            else if ("-in".equals(mArgs[i]))
                mInFile = new File(mArgs[++i]);
            else if ("-out".equals(mArgs[i]))
                mOutFile = new File(mArgs[++i]);
            else if ("-pretty".equals(mArgs[i]))
                mPretty = true;
    }
    
    public static void main(String[] args) throws IOException
    {
        LangAdd app = new LangAdd(args);
        app.run();
    }
}
