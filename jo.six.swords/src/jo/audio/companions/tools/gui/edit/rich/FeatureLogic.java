package jo.audio.companions.tools.gui.edit.rich;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.tools.gui.edit.data.PFeatureBean;
import jo.audio.companions.tools.gui.edit.data.PModuleBean;
import jo.audio.companions.tools.gui.edit.data.PRoomBean;
import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.util.model.cmd.ExternalizeStrings;
import jo.util.utils.obj.StringUtils;

public class FeatureLogic
{
    public static String[] getTexts(PModuleBean module, String id)
    {
        Map<String,String[]> enUS = module.getText().get("en_US");
        if (enUS == null)
            return new String[] { "" };
        if (!enUS.containsKey(id))
            return new String[] { "" };
        return enUS.get(id);
    }
    public static String getText(PModuleBean module, String key)
    {
        String[] texts = getTexts(module, key);
        if (texts.length > 0)
            return texts[0];
        return "";
    }
    public static String setTexts(PModuleBean module, String[] texts)
    {
        String id = ExternalizeStrings.makeConst(texts[0], true);
        Map<String,String[]> enUS = module.getText().get("en_US");
        if (enUS == null)
        {
            enUS = new HashMap<>();
            module.getText().put("en_US", enUS);
        }
        enUS.put(id, texts);
        return id;
    }
    public static String setText(PModuleBean module, String text)
    {
        if (StringUtils.isTrivial(text))
            return null;
        String[] texts = new String[] { text };
        return setTexts(module, texts);
    }
    public static PRoomBean findRoom(PFeatureBean feature, String id)
    {
        if (id == null)
            return null;
        if (feature == null)
            return null;
        for (PRoomBean room : feature.getRooms())
        {
            if (id.equals(room.getID()))
                return room;
        }
        return null;
    }
    public static PFeatureBean findFeature(RuntimeBean rt, long id)
    {
        PModuleBean module = rt.getLocationRich();
        if (module == null)
            return null;
        if (id == -1)
            return null;
        if (id == 0)
            return rt.getSelectedFeature();
        for (PFeatureBean feature : module.getFeatures())
            if (feature.getOID() == id)
                return feature;
        return null;
    }
    public static String getParam(PRoomBean room, String key)
    {
        if (room.getParams() == null)
            return "";
        JSONObject params = room.getParams();
        if (!params.containsKey(key))
            return "";
        return params.getString(key).toString();
    }
    public static void setParam(PRoomBean room, String key, String txt)
    {
        if (room.getParams() == null)
            room.setParams(new JSONObject());
        JSONObject params = room.getParams();
        params.put(key, txt);
    }
    public static String getParamDuration(PRoomBean room, String key)
    {
        String txt = getParam(room, key);
        if (StringUtils.isTrivial(txt))
            return txt;
        long ticks = Long.parseLong(txt);
        if (ticks == 0)
            return "";
        if (ticks%1000L != 0L)
            return ticks+"ms";
        ticks /= 1000L;
        if (ticks%60L != 0L)
            return ticks+"s";
        ticks /= 60L;
        if (ticks%60L != 0L)
            return ticks+"m";
        ticks /= 60L;
        return ticks+"h";
    }
    public static void setParamDuration(PRoomBean room, String key, String txt)
    {
        if (StringUtils.isTrivial(txt))
            setParam(room, key, txt);
        else if (txt.endsWith("ms"))
            setParam(room, key, txt.substring(0, txt.length() - 2));
        else if (txt.endsWith("s"))
            setParam(room, key, String.valueOf(Long.parseLong(txt.substring(0, txt.length() - 1))*1000L));
        else if (txt.endsWith("m"))
            setParam(room, key, String.valueOf(Long.parseLong(txt.substring(0, txt.length() - 1))*60L*1000L));
        else if (txt.endsWith("g"))
            setParam(room, key, String.valueOf(Long.parseLong(txt.substring(0, txt.length() - 1))*60L*60L*1000L));
        else
            setParam(room, key, txt);
    }
    public static JSONObject nameToJSON(String id)
    {
        JSONObject text = new JSONObject();
        text.put("ident", id);
        return text;
    }
    
    public static String nameFromJSON(JSONObject obj, String key)
    {
        JSONObject text = JSONUtils.getObject(obj, key);
        if (text == null)
            return null;
        return text.getString("ident");
    }
    
    public static PRoomBean newRoom(PModuleBean module, PFeatureBean feature)
    {
        Set<String> ids = new HashSet<>();
        for (PRoomBean room : feature.getRooms())
            ids.add(room.getID());
        int i = feature.getRooms().size();
        String id = "";
        for (;;)
        {
            id = module.getID() + i;
            if (!ids.contains(id))
                break;
            i++;
        }
        PRoomBean room = new PRoomBean();
        room.setID(id);
        room.setName("New Room");
        room.setName("Shiny new room.");
        room.setType("scenic");
        feature.getRooms().add(room);
        feature.fireMonotonicPropertyChange("rooms", feature.getRooms());
        return room;
    }
    public static PRoomBean[][] getGrid(PFeatureBean feature)
    {
        List<List<PRoomBean>> rooms = new ArrayList<>();
        Set<String> done = new HashSet<>();
        Set<String> todo = new HashSet<>();
        String start = feature.getEntranceID();
        PRoomBean r = FeatureLogic.findRoom(feature, start);
        if (r == null)
            return null;
        addRoom(rooms, r, 0, 0);
        for (;;)
        {
            done.add(r.getID());
            for (String dir : PRoomBean.DIRS)
            {
                String dirID = r.getDir(dir);
                if (StringUtils.isTrivial(dirID))
                    continue;
                PRoomBean r2 = FeatureLogic.findRoom(feature, dirID);
                if (r2 == null)
                    continue;
                if (done.contains(dirID))
                    continue;
                todo.add(dirID);
                addRelative(rooms, r, dir, r2);
            }
            if (todo.size() == 0)
                break;
            String next = todo.iterator().next();
            r = FeatureLogic.findRoom(feature, next);
            todo.remove(next);
        }
        int maxx = 0;
        for (int y = 0; y < rooms.size(); y++)
            maxx = Math.max(maxx, rooms.get(y).size());
        PRoomBean[][] grid = new PRoomBean[rooms.size()][maxx];
        for (int y = 0; y < rooms.size(); y++)
        {
            List<PRoomBean> row = rooms.get(y);
            for (int x = 0; x < row.size(); x++)
            {
                grid[y][x] = row.get(x);
                if (grid[y][x] != null)
                    System.out.print(" X");
                else
                    System.out.print(" .");
            }
            System.out.println();
        }
        return grid;
    }
    private static boolean addRelative(List<List<PRoomBean>> rooms, PRoomBean r1,
            String dir, PRoomBean r2)
    {
        int[] r1xy = findRoom(rooms, r1);
        int nx = r1xy[0];
        int ny = r1xy[1];
        switch (dir)
        {
            case PRoomBean.NORTH:
                ny--;
                break;
            case PRoomBean.SOUTH:
                ny++;
                break;
            case PRoomBean.EAST:
                nx++;
                break;
            case PRoomBean.WEST:
                nx--;
                break;
        }
        return addRoom(rooms, r2, nx, ny);
    }
    
    private static boolean addRoom(List<List<PRoomBean>> rooms, PRoomBean room, int x, int y)
    {
        while (y < 0)
        {
            rooms.add(0, new ArrayList<>());
            y++;
        }
        while (rooms.size() < y + 1)
            rooms.add(new ArrayList<>());
        List<PRoomBean> row = rooms.get(y);
        while (x < 0)
        {
            for (List<PRoomBean> r : rooms)
                r.add(0, null);
            x++;
        }
        while (row.size() < x + 1)
            row.add(null);
        if (row.get(x) != null)
            return false;
        row.remove(x);
        row.add(x, room);
        return true;
    }
    
    private static int[] findRoom(List<List<PRoomBean>> rooms, PRoomBean r1)
    {
        for (int yy = 0; yy < rooms.size(); yy++)
            for (int xx = 0; xx < rooms.get(yy).size(); xx++)
            {
                PRoomBean r = rooms.get(yy).get(xx);
                if (r == r1)
                    return new int[] { xx, yy };
            }
        return null;
    }
    public static void removeRoom(RuntimeBean runtime, long oid, String id)
    {
        PFeatureBean feature = findFeature(runtime, oid);
        if (feature == null)
            return;
        PRoomBean room = findRoom(feature, id);
        if (room == null)
            return;
        feature.getRooms().remove(room);
        feature.fireMonotonicPropertyChange("rooms", feature.getRooms());
        if (runtime.getSelectedRoom() == room)
            runtime.setSelectedFeature(null);
        for (PRoomBean r : feature.getRooms())
        {
            if (id.equals(r.getNorth()))
                r.setNorth("");
            if (id.equals(r.getSouth()))
                r.setSouth("");
            if (id.equals(r.getEast()))
                r.setEast("");
            if (id.equals(r.getWest()))
                r.setWest("");
        }
    }
    public static void setFeatureName(RuntimeBean runtime, long oid,
            String text)
    {
        PFeatureBean feature = findFeature(runtime, oid);
        if (feature == null)
            return;
        feature.setName(text);
        runtime.getLocationRich().fireMonotonicPropertyChange("features", runtime.getLocationRich().getFeatures());
    }
    public static void setFeatureLocation(RuntimeBean runtime, long oid,
            String text)
    {
        PFeatureBean feature = findFeature(runtime, oid);
        if (feature == null)
            return;
        feature.setLocation(text);
    }
    public static void setFeatureEnabledBy(RuntimeBean runtime, long oid,
            String text)
    {
        PFeatureBean feature = findFeature(runtime, oid);
        if (feature == null)
            return;
        feature.setEnabledBy(text);
    }
    public static void setModuleID(RuntimeBean runtime, String text)
    {
        runtime.getLocationRich().setID(text);        
    }
    public static void setModuleName(RuntimeBean runtime, String text)
    {
        runtime.getLocationRich().setName(text);        
    }
    public static void setModuleAccount(RuntimeBean runtime, String text)
    {
        runtime.getLocationRich().setAccount(text);        
    }
    public static void setModuleEnabledBy(RuntimeBean runtime, String text)
    {
        runtime.getLocationRich().setEnabledBy(text);        
    }
    public static void setModuleAuthor(RuntimeBean runtime, String text)
    {
        runtime.getLocationRich().setAuthor(text);        
    }
}
