package jo.audio.companions.tools.gui.edit.logic;

import jo.audio.companions.data.build.PFeatureBean;
import jo.audio.companions.data.build.PRoomBean;
import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.edit.rich.FeatureLogic;

public class SelectionLogic
{
    public static void selectFeature(RuntimeBean rt, long oid)
    {
        PFeatureBean feature = FeatureLogic.findFeature(rt, oid);
        if (rt.getSelectedFeature() != feature)
        {
            rt.setSelectedFeature(feature);
            if (feature == null)
                SelectionLogic.selectRoom(rt, null);
            else
                SelectionLogic.selectRoom(rt, feature.getEntranceID());
        }
    }
    public static void selectRoom(RuntimeBean rt, String id)
    {
        PRoomBean room = FeatureLogic.findRoom(rt.getSelectedFeature(), id);
        if (rt.getSelectedRoom() != room)
            rt.setSelectedRoom(room);
    }
}
