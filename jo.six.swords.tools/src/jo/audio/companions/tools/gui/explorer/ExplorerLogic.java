package jo.audio.companions.tools.gui.explorer;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.LocationBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.service.MapStrategicLogic;
import jo.audio.companions.service.MapTacticalLogic;
import jo.util.utils.obj.StringUtils;

public class ExplorerLogic
{
    public static ExplorerBean newInstance()
    {
        ExplorerBean rt = new ExplorerBean();
        rt.setLocation(new LocationBean(CompConstLogic.INITIAL_LOCATION_X, CompConstLogic.INITIAL_LOCATION_Y, 0));
        return rt;
    }

    public static void shutdown(ExplorerBean rt)
    {
    }
    
    public static void zoomIn(ExplorerBean rt)
    {
        rt.setScale(rt.getScale()*2);
    }
    
    public static void zoomOut(ExplorerBean rt)
    {
        if (rt.getScale() > 16)
            rt.setScale(rt.getScale()/2);
    }
    
    private static void doMove(ExplorerBean rt, int stratDX, int stratDY, int tactDir)
    {
        if (StringUtils.isTrivial(rt.getLocation().getRoomID()))
        {
            rt.getLocation().setX(rt.getLocation().getX() + stratDX);
            rt.getLocation().setY(rt.getLocation().getY() + stratDY);
        }
        else
        {
            FeatureBean feature = MapTacticalLogic.getFeature(rt.getLocation());
            CompRoomBean room = MapTacticalLogic.getRoom(feature, rt.getLocation().getRoomID());
            String id = room.getDirection(tactDir);
            if ("$exit".equals(id))
                rt.getLocation().setRoomID(null);
            else
                rt.getLocation().setRoomID(id);
        }
        rt.fireMonotonicPropertyChange("location");
    }
    
    public static void north(ExplorerBean rt)
    {
        doMove(rt, 0, -1, 0);
    }
    
    public static void south(ExplorerBean rt)
    {
        doMove(rt, 0, 1, 1);
    }
    
    public static void east(ExplorerBean rt)
    {
        doMove(rt, 1, 0, 2);
    }
    
    public static void west(ExplorerBean rt)
    {
        doMove(rt, -1, 0, 3);
    }
    
    public static void dimUp(ExplorerBean rt)
    {
        if (rt.getLocation().getZ() >= 2)
            return;
        rt.getLocation().setZ(rt.getLocation().getZ() + 1);
        rt.fireMonotonicPropertyChange("location");
    }
    
    public static void dimDown(ExplorerBean rt)
    {
        if (rt.getLocation().getZ() <= 0)
            return;
        rt.getLocation().setZ(rt.getLocation().getZ() - 1);
        rt.fireMonotonicPropertyChange("location");
    }
    
    public static void home(ExplorerBean rt)
    {
        int z = rt.getLocation().getZ();
        rt.setLocation(new LocationBean(CompConstLogic.INITIAL_DIM_LOCATION_X[z], CompConstLogic.INITIAL_DIM_LOCATION_Y[z], z));
    }
    
    public static boolean enter(ExplorerBean rt)
    {
        SquareBean square = MapStrategicLogic.getSquare(rt.getLocation().getX(), rt.getLocation().getY(), rt.getLocation().getZ());
        if (square.getFeature() == CompConstLogic.FEATURE_NONE)
            return false;
        FeatureBean feature = MapTacticalLogic.getFeature(rt.getLocation());
        rt.getLocation().setRoomID(feature.getEntranceID());
        rt.fireMonotonicPropertyChange("location");
        return true;
    }
}
