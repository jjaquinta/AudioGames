package jo.audio.companions.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

public class RoomConvert
{
    @SuppressWarnings("unchecked")
    public static void main(String[] argv) throws IOException
    {
        File oldRoomFile = new File("C:\\Users\\IBM_ADMIN\\git\\TsaTsaTzuAlexa\\jo.audio.companions\\src\\jo\\audio\\companions\\slu\\oldRoomTypes.json");
        File newRoomFile = new File("C:\\Users\\IBM_ADMIN\\git\\TsaTsaTzuAlexa\\jo.audio.companions\\src\\jo\\audio\\companions\\slu\\roomTypes.json");
        JSONObject oldRooms = JSONUtils.readJSON(oldRoomFile);
        JSONObject newRooms = new JSONObject();
        JSONArray oldRoomTypes = JSONUtils.getArray(oldRooms, "roomTypes");
        JSONArray newRoomTypes = new JSONArray();
        newRooms.put("roomTypes", newRoomTypes);
        JSONObject text = new JSONObject();
        newRooms.put("text", text);
        JSONObject enUS = new JSONObject();
        text.put("en_US", enUS);
        for (int i = 0; i < oldRoomTypes.size(); i++)
        {
            JSONObject oldRoom = (JSONObject)oldRoomTypes.get(i);
            JSONObject newRoom = new JSONObject();
            newRoom.put("ID", oldRoom.get("ID"));
            newRoom.put("type", oldRoom.get("type"));
            convert("name", oldRoom, enUS, newRoom);
            convert("description", oldRoom, enUS, newRoom);
            if (oldRoom.containsKey("params"))
                newRoom.put("params", oldRoom.get("params"));
            newRoomTypes.add(newRoom);
        }
        JSONUtils.writeJSON(newRoomFile, newRooms);
    }
    
    @SuppressWarnings("unchecked")
    private static void convert(String key, JSONObject oldRoom, JSONObject enUS, JSONObject newRoom)
    {
        Object v = JSONUtils.get(oldRoom, key+".value.en_US");
        String name = (v instanceof JSONArray) ? ((JSONArray)v).get(0).toString() : v.toString();
        List<String> args = new ArrayList<>();
        for (;;)
        {
            int o = name.indexOf("{{");
            if (o >= 0)
            {
                int e = name.indexOf("}}", o);
                String arg = name.substring(o, e + 2);
                name = name.substring(0, o) + "%s" + name.substring(e + 2);
                args.add(arg);
            }
            else
            {
                o = name.indexOf("?");
                if (o >= 0)
                {
                    int e = name.indexOf("?", o+1);
                    String arg = name.substring(o, e + 1);
                    name = name.substring(0, o) + "%s" + name.substring(e + 1);
                    args.add(arg);
                }
                else
                    break;
            }            
        }
        
        String nameKey = toKey(name);
        if (enUS.containsKey(nameKey))
            for (int idx = 0; idx < 99; idx++)
                if (!enUS.containsKey(nameKey+idx))
                {
                    nameKey = nameKey+idx;
                    break;
                }
        JSONArray names = new JSONArray();
        names.add(name);
        enUS.put(nameKey, names);
        JSONObject roomVal = new JSONObject();
        roomVal.put("key", nameKey);
        JSONArray a = new JSONArray();
        a.addAll(args);
        if (a.size() > 0)
            roomVal.put("args", a);
        newRoom.put(key, roomVal);
    }
    
    private static String toKey(String text)
    {
        for (int i = 0; i < 3; i++)
        {
            int o = text.indexOf("%s");
            if (o < 0)
                break;
            String name = String.valueOf((char)('X'+i));
            name = name + name + name;
            text = text.substring(0, o) + name + text.substring(o + 2);
        }
        StringBuffer sb = new StringBuffer();
        for (char ch : text.toCharArray())
            if (Character.isAlphabetic(ch))
                sb.append(Character.toUpperCase(ch));
            else if ((sb.length() > 0) && Character.isDigit(ch))
                sb.append(ch);
            else if (!sb.toString().endsWith("_"))
                sb.append("_");
        return sb.toString();

    }
}
