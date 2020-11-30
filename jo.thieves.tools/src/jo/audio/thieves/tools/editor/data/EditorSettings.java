package jo.audio.thieves.tools.editor.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jo.util.beans.PCSBean;

public class EditorSettings extends PCSBean
{
    private File                   mProjectDir;
    private PLocation              mGlobalLocations;
    private Map<String, PLocation> mSpecificLocations = new HashMap<>();
    private PTile                  mSelectedTile;
    // persistent settings
    private PLocation              mSelectedLocation;
    private PHouse                 mSelectedHouse;

    // utilities

    // getters and setters

    public PLocation getGlobalLocations()
    {
        return mGlobalLocations;
    }

    public void setGlobalLocations(PLocation globalLocations)
    {
        mGlobalLocations = globalLocations;
    }

    public Map<String, PLocation> getSpecificLocations()
    {
        return mSpecificLocations;
    }

    public void setSpecificLocations(Map<String, PLocation> specificLocations)
    {
        mSpecificLocations = specificLocations;
    }

    public File getProjectDir()
    {
        return mProjectDir;
    }

    public void setProjectDir(File projectDir)
    {
        mProjectDir = projectDir;
    }

    public PLocation getSelectedLocation()
    {
        return mSelectedLocation;
    }

    public void setSelectedLocation(PLocation selectedLocation)
    {
        queuePropertyChange("selectedLocation", mSelectedLocation,
                selectedLocation);
        mSelectedLocation = selectedLocation;
        firePropertyChange();
    }

    public PHouse getSelectedHouse()
    {
        return mSelectedHouse;
    }

    public void setSelectedHouse(PHouse selectedHouse)
    {
        queuePropertyChange("selectedHouse", mSelectedHouse, selectedHouse);
        mSelectedHouse = selectedHouse;
        firePropertyChange();
    }

    public PTile getSelectedTile()
    {
        return mSelectedTile;
    }

    public void setSelectedTile(PTile selectedTile)
    {
        queuePropertyChange("selectedTile", mSelectedTile, selectedTile);
        mSelectedTile = selectedTile;
        firePropertyChange();
    }
}
