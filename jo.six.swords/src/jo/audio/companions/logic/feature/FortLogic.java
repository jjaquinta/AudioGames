package jo.audio.companions.logic.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;

public class FortLogic
{

    public static void generateCastle(RegionBean region, SquareBean square, FeatureBean feature, int type, Random rnd)
    {
        switch (type)
        {
            case CompConstLogic.FEATURE_CASTLE:
                feature.getName().setIdent("{{CASTLE_NAME#"+rnd.nextInt(9937)+"}}");
                break;
            case CompConstLogic.FEATURE_FORT:
                feature.getName().setIdent("{{FORT_NAME#"+rnd.nextInt(9937)+"}}");
                break;
            case CompConstLogic.FEATURE_OUTPOST:
                feature.getName().setIdent("{{OUTPOST_NAME#"+rnd.nextInt(9937)+"}}");
                break;
        }
        CompRoomBean gatehouse = FeatureLogic.getRoom("castleGatehouse");
        CompRoomBean courtyard = FeatureLogic.getRoom("castleCourtyard");
        CompRoomBean office = FeatureLogic.getRoom("castleOffice");
        addBounties(office, region, square);
        
        gatehouse.setNorth(courtyard.getID());
        courtyard.setSouth(gatehouse.getID());
        gatehouse.setSouth("$exit");
        courtyard.setWest(office.getID());
        office.setEast(courtyard.getID());
        
        feature.getRooms().add(gatehouse);
        feature.getRooms().add(courtyard);
        feature.getRooms().add(office);

        if (square.getOrds().getZ() == CompConstLogic.DIM_CIRRANE)
        {
            CompRoomBean library = LibraryLogic.makeLibrary(feature);
            courtyard.setEast(library.getID());
            library.setWest(courtyard.getID());
            feature.getRooms().add(library);
        }
    }

    @SuppressWarnings("unchecked")
    private static void addBounties(CompRoomBean office, RegionBean region,
            final SquareBean square)
    {
        final List<SquareBean> ruins = new ArrayList<>();
        for (int x = 0; x < CompConstLogic.SQUARES_PER_REGION; x++)
            for (int y = 0; y < CompConstLogic.SQUARES_PER_REGION; y++)
            {
                SquareBean s = region.getSquare(x, y);
                if (s == null)
                    System.out.println("WTF? region="+region.getOrds()+", sq="+x+","+y);
                if (s.getFeature() == CompConstLogic.FEATURE_RUIN)
                    ruins.add(s);
            }
        Collections.sort(ruins, new Comparator<SquareBean>() {
            @Override
            public int compare(SquareBean o1, SquareBean o2)
            {
                int d1 = square.getOrds().dist(o1.getOrds());
                int d2 = square.getOrds().dist(o2.getOrds());
                return d1 - d2;
            }
        });
        JSONObject params = office.getParams();
        if (params == null)
        {
            params = new JSONObject();
            office.setParams(params);
        }
        JSONArray bounties = new JSONArray();
        params.put("bounties", bounties);
        for (int i = Math.min(ruins.size(), 3) - 1; i >= 0; i--)
        {
            SquareBean s = ruins.get(i);
            JSONObject bounty = new JSONObject();
            bounties.add(bounty);
            bounty.put("ords", s.getOrds().toString());
            bounty.put("reward", Math.pow(2, s.getChallenge() - 1)*1000);
        }
    }

}
