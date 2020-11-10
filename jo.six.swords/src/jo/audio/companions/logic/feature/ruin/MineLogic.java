package jo.audio.companions.logic.feature.ruin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.feature.DigOptions;
import jo.audio.companions.logic.feature.RuinLogic;

public class MineLogic
{
    public static void generateMine(FeatureBean feature, SquareBean sq, Random rnd)
    {
        feature.setSubType(CompConstLogic.FEATURE_SUB_MINE);
        feature.getName().setIdent("{{MINE_NAME#"+rnd.nextInt(9937)+"}}");
        feature.setMonsterTreasure(true);
        feature.setMonsterPopulous(true);
        int numRooms = DiceRollBean.roll(rnd, 2, 4);
        determineMineRooms(feature, rnd, numRooms);
        RuinLogic.sortRoomsByDepth(feature);
        RuinLogic.populateRoomsByDepth(feature, 2, null);
    }

    private static void determineMineRooms(FeatureBean feature, Random rnd, int numRooms)
    {
        CompRoomBean room = FeatureLogic.getRoom("mineEntrance");
        room.setID(room.getID()+feature.getRooms().size());
        room.setSouth("$exit");
        feature.getRooms().add(room);
        List<DigOptions> options = new ArrayList<>();
        options.add(new DigOptions(room, 0));
        options.add(new DigOptions(room, 2));
        options.add(new DigOptions(room, 3));
        while ((options.size() > 0) && (numRooms > 0))
        {
            int idx = rnd.nextInt(options.size());
            DigOptions opt = options.get(idx);
            options.remove(idx);
            int opposite = CompRoomBean.opposite(opt.dir);
            int shaftLength = rnd.nextInt(3) + 1;
            CompRoomBean from = opt.from;
            for (int i = 0; i < shaftLength; i++)
            {
                CompRoomBean to = FeatureLogic.getRoom("mineShaft");
                to.setID(to.getID()+feature.getRooms().size());
                JSONObject params = new JSONObject();
                to.setParams(params);
                from.setDirection(opt.dir, to.getID());
                to.setDirection(opposite, from.getID());
                feature.getRooms().add(to);
                //numRooms--;
                from = to;
            }
            CompRoomBean to = null;
            switch (rnd.nextInt(4))
            {
                case 0:
                    to = FeatureLogic.getRoom("mineCollapse");
                    break;
                case 1:
                    to = FeatureLogic.getRoom("mineCistern");
                    RuinLogic.addAllBut(options, to, opposite);
                    break;
                case 2:
                    to = FeatureLogic.getRoom("mineGallery");
                    RuinLogic.addAllBut(options, to, opposite);
                    break;
                case 3:
                    to = FeatureLogic.getRoom("mineSmelter");
                    RuinLogic.addAllBut(options, to, opposite);
                    break;
            }
            to.setID(to.getID()+feature.getRooms().size());
            JSONObject params = new JSONObject();
            to.setParams(params);
            params.put(CompRoomBean.MD_ENCOUNTER_CHALLENGE, 0);
            params.put(CompRoomBean.MD_WAIT_TIME, RuinLogic.MONSTER_TIMEOUT);
            from.setDirection(opt.dir, to.getID());
            to.setDirection(opposite, from.getID());
            feature.getRooms().add(to);
            numRooms--;
        }
    }
}
