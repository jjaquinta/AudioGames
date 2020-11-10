package jo.audio.companions.logic;

import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;
import org.json.simple.parser.ParseException;

import jo.audio.common.logic.CommonIOLogic;

public class TextLogic
{
    private static final JSONObject mText = new JSONObject();
    
    public static Set<String> addText(JSONObject text, String prefix)
    {
        Set<String> added = new HashSet<>();
        for (String lang : text.keySet())
        {
            JSONObject newLangText = (JSONObject)text.get(lang);
            JSONObject oldLangText = (JSONObject)mText.get(lang);
            if (oldLangText == null)
            {
                oldLangText = new JSONObject();
                mText.put(lang, oldLangText);
            }
            for (String key : newLangText.keySet())
            {
                oldLangText.put(prefix+key, newLangText.get(key));
                added.add(key);
                //DebugUtils.trace("Adding "+lang+" "+prefix+key+" as "+newLangText.getString(key));
            }
        }
        return added;
    }
    
    public static JSONObject getText()
    {
        // force loading
        FeatureLogic.getRoom("xyzzy");
        String adHocText = CommonIOLogic.getDataPrimaryValue("sixswords://text");
        if (adHocText != null)
            try
            {
                JSONObject json = (JSONObject)JSONUtils.PARSER.parse(adHocText);
                //DebugUtils.trace("Replacing with "+json.toJSONString());
                addText(json, "");
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        return mText;
    }
}
