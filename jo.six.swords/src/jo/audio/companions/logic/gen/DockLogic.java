package jo.audio.companions.logic.gen;

import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;

public class DockLogic
{

    public static void generateDock(FeatureBean feature, SquareBean sq,
            Random rnd, DockSpec ds)
    {
        feature.getName().setIdent("{{DOCKS#"+rnd.nextInt(9937)+"}}");
        feature.getName().setArgs(new Object[] { new AudioMessageBean(ds.mName) });
        feature.setMonsterTreasure(false);
        feature.setMonsterPopulous(false);
        int forward = 0;
        int left = 3;
        int right = 2;
        int back = 1;
        switch (ds.mDir)
        {
            case "n":
                forward = 0;
                left = 3;
                right = 2;
                back = 1;
                break;
            case "s":
                forward = 1;
                left = 2;
                right = 3;
                back = 0;
                break;
            case "e":
                forward = 2;
                left = 0;
                right = 1;
                back = 3;
                break;
            case "w":
                forward = 3;
                left = 1;
                right = 0;
                back = 2;
                break;
        }
        // entrance
        CompRoomBean landing = FeatureLogic.getRoom("dockLanding");
        landing.setID(landing.getID()+feature.getRooms().size());
        landing.setDirection(back, "$exit");
        feature.getRooms().add(landing);
        for (int idx = 0; idx < ds.mLinks.length; idx += 2)
        {
            // pier
            CompRoomBean pier = FeatureLogic.getRoom("dockPier");
            pier.setID(landing.getID()+feature.getRooms().size());
            pier.setDirection(back, landing.getID());
            landing.setDirection(forward, pier.getID());
            feature.getRooms().add(pier);
            // left
            addBoat(feature, pier, sq.getOrds(), ds.mLinks[idx], left, right, ds.mLinkSpecs[idx]);
            if (idx + 1 < ds.mLinks.length)
                addBoat(feature, pier, sq.getOrds(), ds.mLinks[idx+1], right, left, ds.mLinkSpecs[idx+1]);
            if (idx + 2 == ds.mLinks.length - 1)
            {
                addBoat(feature, pier, sq.getOrds(), ds.mLinks[idx+2], forward, back, ds.mLinkSpecs[idx+2]);
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void addBoat(FeatureBean feature, CompRoomBean pier, CoordBean from, CoordBean to, int forward, int back, DockSpec ds)
    {
        int cost = from.dist(to);
        System.out.println("Adding boat from "+from+" to "+to+"/"+ds.mName+" for "+cost);
        CompRoomBean boatway = FeatureLogic.getRoom("dockBoatway");
        boatway.setID(pier.getID()+feature.getRooms().size());
        boatway.setDirection(back, pier.getID());
        boatway.getName().setArgs(new Object[] { new AudioMessageBean(ds.mName) });
        boatway.getDescription().setArgs(new Object[] { new AudioMessageBean(ds.mName), cost });
        if (boatway.getParams() == null)
            boatway.setParams(new JSONObject());
        JSONObject lock = new JSONObject();
        lock.put("expr", "$user.goldpieces < "+cost);
        JSONObject trueMessage = new JSONObject();
        trueMessage.put("message", CompanionsModelConst.TEXT_YOU_DONT_HAVE_ENOUGH_GOLD);
        lock.put("trueMessage", trueMessage);
        boatway.setDirectionLock(forward, lock);
        pier.setDirection(forward, boatway.getID());
        feature.getRooms().add(boatway);
        CompRoomBean boat = FeatureLogic.getRoom("dockBoat");
        boat.setID(pier.getID()+feature.getRooms().size());
        boat.setDirection(back, boatway.getID());
        boat.getName().setArgs(new Object[] { new AudioMessageBean(ds.mName) });
        boat.getDescription().setArgs(new Object[] { new AudioMessageBean(ds.mName) });
        JSONArray effects = (JSONArray)boat.getParams().get("effects");
        for (int i = 0; i < effects.size(); i++)
            if (effects.get(i) instanceof JSONObject)
            {
                JSONObject effect = (JSONObject)effects.get(i);
                String id = effect.getString("id");
                if ("response".equals(id))
                {
                    if (effect.get("args") instanceof JSONArray)
                    {
                        JSONArray args = (JSONArray)effect.get("args");
                        for (int j = 0; j < args.size(); j++)
                            if ("XXX".equals(args.get(j)))
                            {
                                args.remove(j);
                                args.add(j, "{{"+ds.mName+"}}");
                            }
                    }
                }
                else if ("teleport".equals(id))
                    effect.put("location", to.toString());
                else if ("debit".equals(id))
                    effect.put("amount", cost);
            }
        boatway.setDirection(forward, boat.getID());
        feature.getRooms().add(boat);
    }
}
