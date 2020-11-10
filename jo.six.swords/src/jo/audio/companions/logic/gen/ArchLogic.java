package jo.audio.companions.logic.gen;

import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.FeatureLogic;

public class ArchLogic
{

    public static void generateArch(FeatureBean feature, SquareBean sq,
            Random rnd, JSONObject archSpecs)
    {
        JSONObject archLocation = (JSONObject)archSpecs.get(sq.getOrds().toString());
        JSONArray archConnections = (JSONArray)archLocation.get("arches");
        int idx = (int)(System.currentTimeMillis()/(15*60*1000L));
        String arch1location = (String)archConnections.get((idx+0)%archConnections.size());
        String arch2location = (String)archConnections.get((idx+1)%archConnections.size());
        String arch3location = (String)archConnections.get((idx+2)%archConnections.size());
        JSONObject arch1 = (JSONObject)archSpecs.get(arch1location);
        JSONObject arch2 = (JSONObject)archSpecs.get(arch2location);
        JSONObject arch3 = (JSONObject)archSpecs.get(arch3location);

        CompRoomBean entry = FeatureLogic.getRoom("triEntry");
        entry.getName().getArgs()[0] = "{{"+archLocation.getString("name")+"}}";
        entry.getDescription().getArgs()[0] = "{{"+archLocation.getString("name")+"}}";
        entry.getDescription().getArgs()[1] = "{{"+arch1.getString("name")+"}}";
        entry.getDescription().getArgs()[2] = "{{"+arch2.getString("name")+"}}";
        entry.getDescription().getArgs()[3] = "{{"+arch3.getString("name")+"}}";
        CompRoomBean arch = FeatureLogic.getRoom("triArch");
        CompRoomBean one = FeatureLogic.getRoom("triOne");
        one.getName().getArgs()[0] = "{{"+arch1.getString("name")+"}}";
        ((JSONObject)JSONUtils.get(one.getParams(), "effects.2")).put("location", arch1location);
        CompRoomBean two = FeatureLogic.getRoom("triTwo");
        two.getName().getArgs()[0] = "{{"+arch2.getString("name")+"}}";
        ((JSONObject)JSONUtils.get(two.getParams(), "effects.2")).put("location", arch2location);
        CompRoomBean three = FeatureLogic.getRoom("triThree");
        three.getName().getArgs()[0] = "{{"+arch3.getString("name")+"}}";
        ((JSONObject)JSONUtils.get(three.getParams(), "effects.2")).put("location", arch3location);
        System.out.println("Arch Location: "+sq.getOrds().toString()+" / "+archLocation.getString("name"));
        System.out.println("  Dest "+((idx+0)%archConnections.size())+": "+arch1location+" / "+arch1.getString("name")+" / "+one.toJSON().toJSONString());
        System.out.println("  Dest "+((idx+1)%archConnections.size())+": "+arch2location+" / "+arch2.getString("name")+" / "+two.toJSON().toJSONString());
        System.out.println("  Dest "+((idx+2)%archConnections.size())+": "+arch3location+" / "+arch3.getString("name")+" / "+three.toJSON().toJSONString());

        feature.setName(entry.getName());
        feature.setMonsterTreasure(false);
        feature.setMonsterPopulous(false);
        feature.getRooms().add(entry);
        feature.getRooms().add(arch);
        feature.getRooms().add(one);
        feature.getRooms().add(two);
        feature.getRooms().add(three);
    }

}
