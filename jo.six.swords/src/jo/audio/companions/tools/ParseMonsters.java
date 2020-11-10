package jo.audio.companions.tools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.util.utils.io.FileUtils;

public class ParseMonsters
{
    private static final String[] FREQs      = { "Very Rare", "Rare",
            "Uncommon", "Common", };
    public static final String[] TYPEs      = { "Animal", "Demi-Human",
            "Demon", "Devil", "Dinosaur", "Dragon", "Sylvan-or-Faerie", "Giant",
            "Golem", "Humanoid", "Lycanthrope", "Men", "Other", "Undead", };
    private static final String[] NAME_MARKs = { "None", "." };
    private static final String[] SIZEs      = { "Huge", "Large", "Medium",
            "Small" };

    @SuppressWarnings("unchecked")
    public static void main(String[] argv) throws IOException
    {
        File monsterTypes = new File("C:\\Users\\IBM_ADMIN\\git\\TsaTsaTzuAlexa\\jo.audio.companions\\src\\jo\\audio\\companions\\slu\\monsterTypes.json");
        JSONObject monsterObj = JSONUtils.readJSON(monsterTypes);
        JSONArray monsterList = JSONUtils.getArray(monsterObj, "monsterTypes");
        Map<String,JSONObject> monsterIndex = new HashMap<>();
        for (JSONObject monster : JSONUtils.arrayToObjects(monsterList))
            monsterIndex.put(monster.getString("name"), monster);
        // Name Type Freq #Enc Size Move AC HD #AT DMG SpAT SpDF MR Lair INT AL
        // Level XP Page Treasure
        String txt = FileUtils.readFileAsString(
                "C:\\Users\\IBM_ADMIN\\git\\TsaTsaTzuAlexa\\jo.audio.companions\\src\\jo\\audio\\companions\\slu\\osric_monster_listing.txt");
        txt = convertSylvan(txt);
        txt = txt.replace('\n', ' ');
        txt = txt.replace('\r', ' ');
        txt = txt.replace('\t', ' ');
        txt = txt.replaceAll("  ", " ");
        txt = txt.replaceAll("  ", " ");
        txt = txt.replaceAll("  ", " ");
        txt = txt.replaceAll("  ", " ");
        // deriveTypes(txt);
        splitMonsters(txt, monsterIndex);
        monsterList.clear();
        String[] names = monsterIndex.keySet().toArray(new String[0]);
        Arrays.sort(names);
        for (String name : names)
            monsterList.add(monsterIndex.get(name));
        JSONUtils.writeJSON(monsterTypes, monsterObj);
    }

    private static String convertSylvan(String txt)
    {
        for (;;)
        {
            int o = txt.indexOf("Sylvan or");
            if (o < 0)
                break;
            String faerie = txt.substring(o + 12, o + 12 + 6);
            if (faerie.equals("Faerie"))
            {
                txt = txt.substring(0, o) + "Sylvan-or-Faerie"
                        + txt.substring(o + 12 + 6);
            }
            else
            {
                System.err.println("Unknown Sylvan:");
                System.err.println(txt.substring(o - 30, o + 120));
            }
        }
        return txt;
    }

    private static void splitMonsters(String txt, Map<String,JSONObject> monsterIndex)
    {
        int[] idx = new int[1];
        for (;;)
        {
            // Frequency
            int freqStart = findNextOf(txt, FREQs, idx);
            if (freqStart < 0)
                break;
            int freqEnd = freqStart + FREQs[idx[0]].length();
            String freq = txt.substring(freqStart, freqEnd).trim();
            // Type
            String name = txt.substring(0, freqStart).trim();
            txt = txt.substring(freqEnd);
            int typeStart = endsWith(name, TYPEs);
            if (typeStart < 0)
                continue;
            String type = name.substring(typeStart).trim();
            // Name
            name = name.substring(0, typeStart).trim();
            int nameStart = lastIndexOf(name, NAME_MARKs);
            if (nameStart >= 0)
                name = name.substring(nameStart).trim();
            name = name.replace('\n', ' ');
            name = name.replaceAll("\\[.*\\]", "").trim();
            name = name.replaceAll("\\(.*\\)", "").trim();
            int o = name.indexOf(',');
            if (o >= 0)
            {
                String fore = name.substring(0, o).trim();
                String aft = name.substring(o + 1).trim();
                name = aft + " " + fore;
            }
            JSONObject monster = monsterIndex.get(name);
            if (monster == null)
            {
                monster = new JSONObject();
                monsterIndex.put(name, monster);
            }
            monster.put("name", name);
            monster.put("type", type);
            monster.put("freq", freq);
            txt = extractNumberEncountered(txt, monster);
            txt = extractMove(txt, monster);
            txt = extractAC(txt, monster);
            txt = extractHD(txt, monster);
            txt = extractNumAttacks(txt, monster);
            txt = extractAttacks(txt, monster);
            
            if ("Dinosaur".equals(type))
                monster.put("terrain", addToList(monster.getString("terrain"), "Swamp"));
            if ("Sylvan-or-Faerie".equals(type))
                monster.put("terrain", addToList(monster.getString("terrain"), "Forest"));
            if ("Demon".equals(type))
                monster.put("terrain", addToList(monster.getString("terrain"), "Mountains"));
            if ("Devil".equals(type))
                monster.put("terrain", addToList(monster.getString("terrain"), "Mountains"));
            if ("Giant".equals(type))
                monster.put("terrain", addToList(monster.getString("terrain"), "Mountains"));
        }

        System.out.println("\"monsters\":[");
        for (JSONObject m : monsterIndex.values())
        {
            // System.out.println(m.toJSONString()+",");
            System.out.println(m.get("name") + ": " + m.get("att"));
        }
        System.out.println("]");
        System.out.println(monsterIndex.size() + " monsters");
    }

    private static String extractAttacks(String txt, JSONObject monster)
    {
        int o = txt.indexOf(' ');
        String numAtt = txt.substring(0, o);
        txt = txt.substring(o).trim();
        monster.put("att", numAtt);
        return txt;
    }

    private static String extractNumAttacks(String txt, JSONObject monster)
    {
        int o = txt.indexOf(' ');
        String numAtt = txt.substring(0, o);
        txt = txt.substring(o).trim();
        monster.put("numAtt", numAtt);
        return txt;
    }

    private static String extractAC(String txt, JSONObject monster)
    {
        int o = txt.indexOf(' ');
        String ac = txt.substring(0, o);
        txt = txt.substring(o).trim();
        o = ac.indexOf('/');
        if (o >= 0)
            ac = ac.substring(0, o);
        monster.put("ac", ac);
        return txt;
    }

    private static String extractHD(String txt, JSONObject monster)
    {
        int o = txt.indexOf(' ');
        String hd = txt.substring(0, o);
        txt = txt.substring(o).trim();
        if (txt.startsWith("hp"))
        {
            hd += "hp";
            txt = txt.substring(2).trim();
        }
        monster.put("HD", hd);
        if (txt.startsWith("to "))
        {
            txt = txt.substring(3);
            txt = skipNumbers(txt).trim();
        }
        if (!Character.isDigit(txt.charAt(0)) && !txt.startsWith("None"))
            System.err.println("Didn't find end of HD for "
                    + monster.get("name") + ": " + txt.substring(0, 120));
        return txt;
    }

    private static String extractNumberEncountered(String txt,
            JSONObject monster)
    {
        int[] idx = new int[1];
        int sizeStart = findNextOf(txt, SIZEs, idx);
        String enc = txt.substring(0, sizeStart).trim();
        txt = txt.substring(sizeStart);
        enc = enc.replaceAll("\\(.*\\)", "").trim();
        int o = enc.indexOf(" or ");
        if (o >= 0)
            enc = enc.substring(0, o).trim();
        o = enc.indexOf(";");
        if (o >= 0)
            enc = enc.substring(0, o).trim();
        monster.put("enc", enc);
        // size
        String size = SIZEs[idx[0]];
        monster.put("size", size);
        txt = txt.substring(size.length()).trim();
        if (txt.startsWith("("))
        {
            o = txt.indexOf(')');
            txt = txt.substring(o + 1).trim();
        }
        if (txt.startsWith("to "))
        {
            txt = txt.substring(3);
            sizeStart = findNextOf(txt, SIZEs, idx);
            txt = txt.substring(SIZEs[idx[0]].length()).trim();
        }
        return txt;
    }

    private static String extractMove(String txt, JSONObject monster)
    {
        String move;
        if (txt.startsWith("None"))
        {
            move = "0";
            txt = txt.substring(4).trim();
        }
        else if (txt.startsWith("See Page"))
        {
            move = "0";
            txt = txt.substring(8).trim();
        }
        else
        {
            int o = txt.indexOf(" ft");
            move = txt.substring(0, o).trim();
            txt = txt.substring(o + 3).trim();
            if (txt.startsWith(",") || txt.startsWith("/")
                    || txt.startsWith("or "))
            {
                txt = txt.substring(1).trim();
                o = txt.indexOf(" ft");
                txt = txt.substring(o + 3).trim();
            }
            if (txt.startsWith("flying"))
                txt = txt.substring(6).trim();
            if (txt.startsWith("burrowing"))
                txt = txt.substring(9).trim();
            if (txt.startsWith("swimming"))
                txt = txt.substring(8).trim();
            if (txt.startsWith("hovering"))
                txt = txt.substring(8).trim();
            if (txt.startsWith("loping"))
                txt = txt.substring(6).trim();
            if (txt.startsWith("swimming"))
                txt = txt.substring(8).trim();
            if (txt.startsWith("in web"))
                txt = txt.substring(6).trim();
            if (txt.startsWith("underwater"))
                txt = txt.substring(10).trim();
            if (txt.startsWith("swim"))
                txt = txt.substring(4).trim();
            if (txt.startsWith("(") || txt.startsWith(", ("))
            {
                o = txt.indexOf(')');
                txt = txt.substring(o + 1).trim();
            }
        }
        monster.put("move", move);
        return txt;
    }

//    private static void deriveTypes(String txt)
//    {
//        Set<String> types = new HashSet<>();
//        int[] idx = new int[1];
//        for (;;)
//        {
//            int o = findNextOf(txt, FREQs, idx);
//            if (o < 0)
//                break;
//            String type = txt.substring(0, o).trim();
//            txt = txt.substring(o + FREQs[idx[0]].length());
//            o = type.lastIndexOf(' ');
//            if (o >= 0)
//                type = type.substring(o + 1).trim();
//            if (type.endsWith(","))
//                continue;
//            types.add(type);
//        }
//        String[] ts = types.toArray(new String[0]);
//        Arrays.sort(ts);
//        System.out.println("Types:");
//        for (String t : ts)
//            System.out.println("\"" + t + "\",");
//    }

    private static String skipNumbers(String txt)
    {
        for (int i = 0; i < txt.length(); i++)
            if (!Character.isDigit(txt.charAt(i)))
                return txt.substring(i);
        return "";
    }

    private static int endsWith(String txt, String[] ends)
    {
        for (String end : ends)
            if (txt.endsWith(end))
                return txt.length() - end.length();
        return -1;
    }

    private static int findNextOf(String txt, String[] pats, int[] idx)
    {
        int best = txt.length();
        for (int i = 0; i < pats.length; i++)
        {
            String pat = pats[i];
            int o = txt.indexOf(pat);
            if ((o >= 0) && (o < best))
            {
                best = o;
                idx[0] = i;
            }
        }
        if (best == txt.length())
            return -1;
        return best;
    }

    private static int lastIndexOf(String txt, String[] pats)
    {
        int best = 0;
        for (int i = 0; i < pats.length; i++)
        {
            String pat = pats[i];
            int o = txt.lastIndexOf(pat);
            if (o >= 0)
            {
                o += pats[i].length();
                if (o > best)
                    best = o;
            }
        }
        if (best == 0)
            return -1;
        return best;
    }
    
    private static String addToList(String list, String item)
    {
        StringBuffer newList = new StringBuffer();
        if (list != null)
            for (StringTokenizer st = new StringTokenizer(list, ","); st.hasMoreTokens(); )
            {
                String i = st.nextToken();
                if (i.equals(item))
                    item = null;
                if (newList.length() > 0)
                    newList.append(",");
                newList.append(i);
            }
        if (item != null)
        {
            if (newList.length() > 0)
                newList.append(",");
            newList.append(item);
        }
        return newList.toString();
    }
}
