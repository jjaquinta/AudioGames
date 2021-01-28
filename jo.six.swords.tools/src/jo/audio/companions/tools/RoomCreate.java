package jo.audio.companions.tools;

import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class RoomCreate
{
    private static final String PREIFX = "dragon";
    
    private static final String[] ROOM_NAMES = {
            "Cave Entrance",
            "Bone Yard",
            "Hatchery",
            "Tunnel1",
            "Tunnel2",
            "Tunnel3",
            "Tunnel4",
            "Shedding Room",
            "Hall of the Ancestors",
            "Treasure Room",
            "Sleeping Room",
            "Altar",
            "Eyrie",
            "Supplicant's Room",
            "Coal Shed",
            "Well",
            "Kennel",
            "Empty Room",
    };
    
    @SuppressWarnings("unchecked")
    public static void main(String[] argv)
    {
        JSONObject json = new JSONObject();
        JSONArray roomTypes = new JSONArray();
        json.put("roomTypes", roomTypes);
        JSONObject text = new JSONObject();
        json.put("text", text);
        JSONObject enus = new JSONObject();
        text.put("en_US", enus);
        for (String roomName : ROOM_NAMES)
        {
            String ucase = PREIFX.toUpperCase();
            String lcase = PREIFX.toLowerCase();
            for (StringTokenizer st = new StringTokenizer(roomName, " "); st.hasMoreTokens(); )
            {
                String word = st.nextToken();
                ucase += "_" + word.toUpperCase();
                lcase += Character.toUpperCase(word.charAt(0))+word.substring(1).toLowerCase();
            }
            JSONObject roomType = new JSONObject();
            roomTypes.add(roomType);
            JSONObject name = new JSONObject();
            roomType.put("name", name);
            name.put("ident", ucase);
            JSONObject description = new JSONObject();
            roomType.put("description", description);
            description.put("ident", ucase+"_DESC");
            roomType.put("ID", lcase);
            roomType.put("type", "scenic");
            JSONArray nameText = new JSONArray();
            enus.put(ucase, nameText);
            nameText.add(roomName+".");
            JSONArray descText = new JSONArray();
            enus.put(ucase+"_DESC", descText);
            descText.add(roomName+" description.");
        }
        System.out.println(json.toJSONString());
    }
}
