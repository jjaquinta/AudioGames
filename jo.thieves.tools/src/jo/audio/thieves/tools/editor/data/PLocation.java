package jo.audio.thieves.tools.editor.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

public class PLocation implements Comparable<PLocation>
{
    private JSONObject         mRaw;
    private String             mPrefix    = "";
    private String             mPath      = "";
    private Map<String, PTile> mLocations = new HashMap<>();
    private Map<String, PTile> mApatures  = new HashMap<>();
    private Map<String, PTile> mIDMap     = new HashMap<>();
    private List<PHouse>       mTemplates = new ArrayList<>();

    // utilities
    @SuppressWarnings("unchecked")
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        if (!"<root>".equals(mPrefix))
            json.put("prefix", mPrefix);
        JSONObject text = JSONUtils.getObject(mRaw, "text");
        json.put("text", text);
        JSONObject enUS = new JSONObject();
        text.put("en_US", enUS);
        JSONObject idMap = new JSONObject();
        json.put("idMap", idMap);
        JSONObject colorMap = new JSONObject();
        json.put("colorMap", colorMap);
        toJSONTile(json, "locations", mLocations.values(), enUS, colorMap);
        toJSONTile(json, "apatures", mApatures.values(), enUS, colorMap);
        for (String key : mIDMap.keySet())
        {
            PTile val = mIDMap.get(key);
            if ((key != null) && (val != null))
                idMap.put(key, val.getID());
        }
        JSONArray jtemplates = new JSONArray();
        json.put("templates", jtemplates);
        for (PHouse template : mTemplates)
        {
            JSONObject jtemplate = template.toJSON();
            jtemplates.add(jtemplate);
        }
        return json;
    }

    @SuppressWarnings("unchecked")
    private void toJSONTile(JSONObject json, String key,
            Collection<PTile> tiles, JSONObject enUS, JSONObject colorMap)
    {
        JSONArray jtiles = new JSONArray();
        json.put(key, jtiles);
        for (PTile tile : tiles)
        {
            if (tile.isPlaceholder())
                continue;
            jtiles.add(tile.toJSON());
            putText(enUS, tile.getID() + "_NAME", tile.getName());
            putText(enUS, tile.getID() + "_DESC", tile.getDescription());
            if ((tile.getChar() != null) && (tile.getColor() != null))
                colorMap.put(tile.getChar(),
                        "#" + Integer.toHexString(tile.getColor().getRGB()&0xFFFFFF));
        }
    }

    @SuppressWarnings("unchecked")
    private void putText(JSONObject enUS, String key, String value)
    {
        if (value == null)
            return;
        JSONArray val = new JSONArray();
        for (StringTokenizer st = new StringTokenizer(value, "|"); st
                .hasMoreTokens();)
            val.add(st.nextToken());
        enUS.put(key, val);
    }

    public void fromJSON(JSONObject json)
    {
        mRaw = json;
        Map<String, PTile> tiles = new HashMap<>();
        mPrefix = JSONUtils.getString(json, "prefix");
        if (mPrefix == null)
            mPrefix = "<root>";
        getTiles(json, "locations", PTile.LOCATION, tiles);
        getTiles(json, "apatures", PTile.APATURE, tiles);
        JSONObject text = JSONUtils.getObject(json, "text.en_US");
        for (PTile tile : tiles.values())
        {
            tile.setName(getText(text, tile.getID() + "_NAME"));
            tile.setDescription(getText(text, tile.getID() + "_DESC"));
        }
        JSONObject idMap = JSONUtils.getObject(json, "idMap");
        if (idMap != null)
            for (String key : idMap.keySet())
            {
                String val = idMap.getString(key);
                PTile tile = tiles.get(val);
                if (tile != null)
                {
                    if (tile.getChar() != null)
                        mIDMap.put(tile.getChar(), tile);
                    tile.setChar(key);
                }
                else // placeholder
                {
                    tile = new PTile();
                    tile.setID(val);
                    tile.setChar(key);
                    tile.setPlaceholder(true);
                    tiles.put(tile.getID(), tile);
                }
            }
        JSONObject colorMap = JSONUtils.getObject(json, "colorMap");
        if (colorMap != null)
            for (String key : colorMap.keySet())
            {
                String val = colorMap.getString(key);
                if (val == null)
                    continue;
                int rgb = Integer.parseInt(val.substring(1), 16);
                Color c = new Color(rgb);
                String id = idMap.getString(key);
                PTile tile = tiles.get(id);
                tile.setColor(c);
            }
        for (PTile tile : tiles.values())
        {
            if (tile.getType() == PTile.LOCATION)
                mLocations.put(tile.getID(), tile);
            else
                mApatures.put(tile.getID(), tile);
            if (tile.getChar() != null)
                mIDMap.put(tile.getChar(), tile);
        }
        JSONArray templates = JSONUtils.getArray(json, "templates");
        if (templates != null)
            for (int i = 0; i < templates.size(); i++)
            {
                JSONObject jtemplate = (JSONObject)templates.get(i);
                PHouse template = new PHouse();
                template.fromJSON(jtemplate);
                mTemplates.add(template);
            }
    }

    private String getText(JSONObject text, String key)
    {
        Object t = text.get(key);
        if (t == null)
            return null;
        if (t instanceof String)
            return (String)t;
        if (t instanceof JSONArray)
        {
            StringBuffer sb = new StringBuffer();
            JSONArray a = (JSONArray)t;
            for (int i = 0; i < a.size(); i++)
            {
                if (i > 0)
                    sb.append("|");
                sb.append(a.get(i).toString());
            }
            return sb.toString();
        }
        return t.toString();
    }

    private static void getTiles(JSONObject json, String key, int type,
            Map<String, PTile> tiles)
    {
        JSONArray jtiles = JSONUtils.getArray(json, key);
        if (jtiles == null)
            return;
        for (int i = 0; i < jtiles.size(); i++)
        {
            PTile tile = new PTile();
            tile.fromJSON((JSONObject)jtiles.get(i));
            tile.setType(type);
            tiles.put(tile.getID(), tile);
        }
    }

    public void fillPlaces(PLocation parent)
    {
        for (PTile tile : mApatures.values())
            if (tile.isPlaceholder()
                    && parent.getApatures().containsKey(tile.getID()))
            {
                PTile pTile = parent.getApatures().get(tile.getID());
                tile.fromJSON(pTile.toJSON());
                tile.setType(pTile.getType());
                tile.setName(pTile.getName());
                tile.setDescription(pTile.getDescription());
            }
    }

    @Override
    public int compareTo(PLocation o)
    {
        return mPrefix.compareTo(o.getPrefix());
    }

    @Override
    public String toString()
    {
        return mPrefix;
    }

    // getters and setters

    public String getPrefix()
    {
        return mPrefix;
    }

    public void setPrefix(String prefix)
    {
        mPrefix = prefix;
    }

    public Map<String, PTile> getLocations()
    {
        return mLocations;
    }

    public void setLocations(Map<String, PTile> locations)
    {
        mLocations = locations;
    }

    public Map<String, PTile> getApatures()
    {
        return mApatures;
    }

    public void setApatures(Map<String, PTile> apatures)
    {
        mApatures = apatures;
    }

    public List<PHouse> getTemplates()
    {
        return mTemplates;
    }

    public void setTemplates(List<PHouse> templates)
    {
        mTemplates = templates;
    }

    public Map<String, PTile> getIDMap()
    {
        return mIDMap;
    }

    public void setIDMap(Map<String, PTile> iDMap)
    {
        mIDMap = iDMap;
    }

    public String getPath()
    {
        return mPath;
    }

    public void setPath(String path)
    {
        mPath = path;
    }
}
