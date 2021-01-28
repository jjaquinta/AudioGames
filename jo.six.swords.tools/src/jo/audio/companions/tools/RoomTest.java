package jo.audio.companions.tools;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionGenBean;
import jo.audio.companions.data.SquareGenBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;

public class RoomTest
{
    private String[] mArgs;
    
    public RoomTest(String[] args)
    {
        mArgs = args;
    }
    
    public void run()
    {
        parseArgs();
        FeatureBean f;
        RegionGenBean region = new RegionGenBean();
        region.setPredominantRace(CompConstLogic.RACE_HUMAN);
        SquareGenBean sq = new SquareGenBean();
        sq.setOrds(new CoordBean(0, 0));
        sq.setFeature(CompConstLogic.FEATURE_CITY);
        f = FeatureLogic.getFeature(region, sq, null);
        for (CompRoomBean room : f.getRooms())
            testRoom(room);
        sq.setOrds(new CoordBean(1, 1));
        sq.setFeature(CompConstLogic.FEATURE_CASTLE);
        f = FeatureLogic.getFeature(region, sq, null);
        for (CompRoomBean room : f.getRooms())
            testRoom(room);
        sq.setOrds(new CoordBean(2, 2));
        sq.setFeature(CompConstLogic.FEATURE_RUIN);
        f = FeatureLogic.getFeature(region, sq, null);
        for (CompRoomBean room : f.getRooms())
            testRoom(room);
        
    }
    
    private void testRoom(CompRoomBean room)
    {
        JSONObject params = new JSONObject();
        params.put("str", "wibble");
        params.put("num", 3);
        room.setParams(params);
        JSONObject json = room.toJSON();
        CompRoomBean r2 = new CompRoomBean();
        r2.fromJSON(json);
        System.out.println(json.toJSONString());
        System.out.println(r2.toJSON().toJSONString());
    }
    
    private void parseArgs()
    {
        for (int i = 0; i < mArgs.length; i++)
            ;
    }
    
    public static void main(String[] argv)
    {
        RoomTest app = new RoomTest(argv);
        app.run();
    }
}
