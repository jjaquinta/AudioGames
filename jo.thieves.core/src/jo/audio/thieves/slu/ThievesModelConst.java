package jo.audio.thieves.slu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.thieves.logic.LocationLogic;
import jo.util.utils.MathUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

public class ThievesModelConst
{
    public static final String TEXT_NORTH = "NORTH";
    public static final String TEXT_NORTHEAST = "NORTHEAST";
    public static final String TEXT_NORTHWEST = "NORTHWEST";
    public static final String TEXT_SOUTH = "SOUTH";
    public static final String TEXT_SOUTHEAST = "SOUTHEAST";
    public static final String TEXT_SOUTHWEST = "SOUTHWEST";
    public static final String TEXT_EAST = "EAST";
    public static final String TEXT_WEST = "WEST";
    public static final String TEXT_UP = "UP";
    public static final String TEXT_DOWN = "DOWN";

    private static Map<String,String[]> mModel = null;
    
    private static void loadModel()
    {
        if (mModel != null)
            return;
        try
        {
            JSONObject json = JSONUtils.readJSON("resource://jo/audio/thieves/slu/place_names.model");
            mModel = new HashMap<>();
            JSONObject model = JSONUtils.getObject(json, "text.en_US");
            for (String key : model.keySet())
            {
                JSONArray values = JSONUtils.getArray(model, key);
                String[] texts = new String[values.size()];
                for (int i = 0; i < values.size(); i++)
                    texts[i] = values.get(i).toString();
                mModel.put(key, texts);
            }
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
    }
    
    public static String[] getTexts(String id)
    {
        loadModel();
        return mModel.get(id);
    }
    
    public static String getText(String id, int 
            off)
    {
        String[] texts = getTexts(id);
        if (texts == null)
            return null;
        if (off < 0)
            return texts[LocationLogic.getCity().getRND().nextInt(texts.length)];
        return texts[off%texts.length];
    }
    
    public static String expand(String inbuf)
    {
        String outbuf = StringUtils.process(inbuf, "{{", "}}", (id) -> expandPayload(id));
        return outbuf;
    }
    
    private static String expandPayload(String text)
    {
        int o = text.indexOf("#");
        if (o < 0)
            return getText(text, -1);
        String key = text.substring(0, o);
        String arg = text.substring(o + 1);
        if (arg.indexOf('.') >= 0)
        {
            double d = DoubleUtils.parseDouble(arg);
            if (d > 1.0)
                d = 1.0;
            else if (d < 0)
                d = 0.0;
            String[] texts = getTexts(key);
            int idx = (int)Math.round(MathUtils.interpolate(d, 0.0, 1.0, 0, texts.length - 1));
            return texts[idx];
        }
        return getText(key, IntegerUtils.parseInt(arg));
    }
}
