package jo.audio.thieves.tools.editor.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.data.TLocations;

public class EditorLocationLogic
{
    public static List<TLocations> getLocations()
    {
        List<TLocations> names = new ArrayList<>();
        names.addAll(EditorSettingsLogic.getInstance().getSpecificLocations().values());
        Collections.sort(names);
        return names;
    }
    
    public static TLocations getLocation(String location)
    {
        Map<String, TLocations> sl = EditorSettingsLogic.getInstance().getSpecificLocations();
        TLocations loc = sl.get(location);
        return loc;
    }
    
    public static boolean isLocation(String location)
    {
        return EditorSettingsLogic.getInstance().getSpecificLocations().containsKey(location);
    }

    public static void selectLocation(TLocations newLocation)
    {
        EditorSettings es = EditorSettingsLogic.getInstance();
        if (newLocation == es.getSelectedLocation())
            return;
        es.setSelectedLocation(newLocation);
        es.fireMonotonicPropertyChange("houses");
        if (newLocation.getTemplates().size() > 0)
            EditorHouseLogic.selectHouse(newLocation.getTemplates().get(0));
        else
            EditorHouseLogic.selectHouse(null);
    }
    
    public static void addLocation()
    {
        
    }
    
    public static void deleteLocation()
    {
        
    }
}
