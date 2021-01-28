package jo.audio.companions.tools.gui.edit.rich;

import jo.audio.companions.data.build.PFeatureBean;
import jo.audio.companions.data.build.PModuleBean;
import jo.audio.companions.data.build.PRoomBean;
import jo.audio.companions.logic.feature.BuildLogic;
import jo.audio.companions.tools.gui.edit.data.RuntimeBean;

public class FeatureLogic extends BuildLogic
{
    public static PFeatureBean findFeature(RuntimeBean rt, long id)
    {
        PModuleBean module = rt.getLocationRich();
        if (module == null)
            return null;
        if (id == -1)
            return null;
        if (id == 0)
            return rt.getSelectedFeature();
        for (PFeatureBean feature : module.getFeatures())
            if (feature.getOID() == id)
                return feature;
        return null;
    }
    public static void removeRoom(RuntimeBean runtime, long oid, String id)
    {
        PFeatureBean feature = findFeature(runtime, oid);
        if (feature == null)
            return;
        PRoomBean room = findRoom(feature, id);
        if (room == null)
            return;
        feature.getRooms().remove(room);
        feature.fireMonotonicPropertyChange("rooms", feature.getRooms());
        if (runtime.getSelectedRoom() == room)
            runtime.setSelectedFeature(null);
        for (PRoomBean r : feature.getRooms())
        {
            if (id.equals(r.getNorth()))
                r.setNorth("");
            if (id.equals(r.getSouth()))
                r.setSouth("");
            if (id.equals(r.getEast()))
                r.setEast("");
            if (id.equals(r.getWest()))
                r.setWest("");
        }
    }
    public static void setFeatureName(RuntimeBean runtime, long oid,
            String text)
    {
        PFeatureBean feature = findFeature(runtime, oid);
        if (feature == null)
            return;
        feature.setName(text);
        runtime.getLocationRich().fireMonotonicPropertyChange("features", runtime.getLocationRich().getFeatures());
    }
    public static void setFeatureLocation(RuntimeBean runtime, long oid,
            String text)
    {
        PFeatureBean feature = findFeature(runtime, oid);
        if (feature == null)
            return;
        feature.setLocation(text);
    }
    public static void setFeatureEnabledBy(RuntimeBean runtime, long oid,
            String text)
    {
        PFeatureBean feature = findFeature(runtime, oid);
        if (feature == null)
            return;
        feature.setEnabledBy(text);
    }
    public static void setModuleID(RuntimeBean runtime, String text)
    {
        runtime.getLocationRich().setID(text);        
    }
    public static void setModuleName(RuntimeBean runtime, String text)
    {
        runtime.getLocationRich().setName(text);        
    }
    public static void setModuleAccount(RuntimeBean runtime, String text)
    {
        runtime.getLocationRich().setAccount(text);        
    }
    public static void setModuleEnabledBy(RuntimeBean runtime, String text)
    {
        runtime.getLocationRich().setEnabledBy(text);        
    }
    public static void setModuleAuthor(RuntimeBean runtime, String text)
    {
        runtime.getLocationRich().setAuthor(text);        
    }
}
