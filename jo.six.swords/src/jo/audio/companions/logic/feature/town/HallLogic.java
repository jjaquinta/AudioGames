package jo.audio.companions.logic.feature.town;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.CompanionLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.feature.DigOptions;

public class HallLogic
{

    @SuppressWarnings("unchecked")
    static void addHalls(FeatureBean feature, Random rnd,
            List<Integer> halls, List<DigOptions> sites)
    {
        while (halls.size() > 0)
        {
            int hallType = halls.get(0);
            halls.remove(0);
            DigOptions site = sites.get(rnd.nextInt(sites.size()));
            sites.remove(site);
    
            JSONObject params = new JSONObject();
            params.put("race", hallType);
            JSONArray hires = new JSONArray();
            params.put("hires", hires);
            for (int i = 0; i < 3; i++)
            {
                CompCompanionBean hire = CompanionLogic.newInstance(hallType);
                hires.add(hire.toJSON());
            }
    
            CompRoomBean hall = FeatureLogic.getRoom("fightersguild");
            hall.setID(hall.getID()+halls.size());
            site.from.setDirection(site.dir, hall.getID());
            hall.setDirection(TownLogic.opposite(site.dir), site.from.getID());
            hall.setParams(params);
            feature.getRooms().add(hall);
        }
    }

    static List<Integer> determineHalls(RegionBean region, int type,
            Random rnd)
    {
        List<Integer> halls = new ArrayList<>();
        int specificHalls = 0;
        int rndHalls = 0;
        switch (type)
        {
            case CompConstLogic.FEATURE_HAMLET:
                specificHalls = rnd.nextInt(2);
                rndHalls = 0;
                break;
            case CompConstLogic.FEATURE_VILLAGE:
                specificHalls = 1;
                rndHalls = rnd.nextInt(2);
                break;
            case CompConstLogic.FEATURE_TOWN:
                specificHalls = 1 + rnd.nextInt(2);
                rndHalls = rnd.nextInt(2);
                break;
            case CompConstLogic.FEATURE_CITY:
                specificHalls = 2 + rnd.nextInt(3);
                rndHalls = 2;
                break;
        }
        while (specificHalls-- > 0)
            halls.add(region.getPredominantRace());
        while (rndHalls-- > 0)
            switch (region.getPredominantRace())
            {
                case CompConstLogic.RACE_HUMAN:
                    if (rnd.nextBoolean())
                        halls.add(CompConstLogic.RACE_DWARF);
                    else
                        halls.add(CompConstLogic.RACE_ELF);
                    break;
                case CompConstLogic.RACE_DWARF:
                    if (rnd.nextBoolean())
                        halls.add(CompConstLogic.RACE_HUMAN);
                    else
                        halls.add(CompConstLogic.RACE_ELF);
                    break;
                case CompConstLogic.RACE_ELF:
                    if (rnd.nextBoolean())
                        halls.add(CompConstLogic.RACE_DWARF);
                    else
                        halls.add(CompConstLogic.RACE_HUMAN);
                    break;
                case CompConstLogic.RACE_MIXED:
                    if (rnd.nextBoolean())
                        halls.add(CompConstLogic.RACE_DWARF);
                    else
                        halls.add(CompConstLogic.RACE_HUMAN);
                    break;
            }
        return halls;
    }

}
