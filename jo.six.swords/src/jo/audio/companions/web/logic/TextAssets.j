package jo.audio.companions.web.logic;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.logic.CompOperationLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.data.InteractionModelBean;
import jo.audio.util.model.logic.ModelResolveLogic;
import jo.audio.util.model.logic.ParseModelLogic;

public class TextAssets
{
    private static InteractionModelBean mModel;
    
    static
    {
        loadModel();
    }
    
    private static void loadModel()
    {
        try
        {
            FeatureLogic.isStaticFeature(new CoordBean(), null); // force loading
            mModel = ParseModelLogic.parse("resource://jo/audio/companions/slu/Companions.model");
            CompOperationBean op = new CompOperationBean();
            op.setOperation(CompOperationBean.TEXT);
            CompContextBean context = CompOperationLogic.operate(op);
            loadText(context.getTextModel());
            //loadText(TextLogic.getText());
        }
        catch (IOException e)
        {
            Logger.getAnonymousLogger().log(Level.FINEST, "Error loading text assets");
            Logger.getAnonymousLogger().log(Level.FINEST, e);
        }
    }
    private static void loadText(JSONObject textModel)
    {
        for (String lang : textModel.keySet())
        {
            JSONObject dict = JSONUtils.getObject(textModel, lang);
            for (String key : dict.keySet())
            {
                JSONArray vals = JSONUtils.getArray(dict, key);
                if (vals == null)
                    System.out.println("Why is "+lang+":"+key+" null?");
                for (int i = 0; i < vals.size(); i++)
                    mModel.addText(lang, key, (String)vals.get(i));
            }
        }

    }
    
    public static String expandInserts(String txt)
    {
        String msg = ModelResolveLogic.expandInserts(mModel, "en_US", BaseUserState.RND, null, txt);
        return msg;
    }
    
    public static String expandInserts(AudioMessageBean txt)
    {
        if (txt.getIdent().indexOf("{") >= 0)
            return expandInserts(txt.getIdent());
        String msg = ModelResolveLogic.resolve(mModel, "en_US", BaseUserState.RND, null, txt.getIdent(), txt.getArgs());
        return msg;
    }
    
    public static Map<String,List<String>> getText(String lang)
    {
        return mModel.getText().get(lang);
    }
}
