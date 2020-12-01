package jo.audio.thieves.tools.editor.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jo.util.beans.PCSBean;

public class EditorSettings extends PCSBean
{
    private File                    mProjectDir;
    private TLocations              mGlobalLocations;
    private Map<String, TLocations> mSpecificLocations = new HashMap<>();
    private TLocation               mSelectedTile;
    private TApature                mSelectedApature;
    // persistent settings
    private TLocations              mSelectedLocation;
    private TTemplate               mSelectedHouse;

    // utilities

    // getters and setters

    public TLocations getGlobalLocations()
    {
        return mGlobalLocations;
    }

    public void setGlobalLocations(TLocations globalLocations)
    {
        mGlobalLocations = globalLocations;
    }

    public Map<String, TLocations> getSpecificLocations()
    {
        return mSpecificLocations;
    }

    public void setSpecificLocations(Map<String, TLocations> specificLocations)
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

    public TLocations getSelectedLocation()
    {
        return mSelectedLocation;
    }

    public void setSelectedLocation(TLocations selectedLocation)
    {
        queuePropertyChange("selectedLocation", mSelectedLocation,
                selectedLocation);
        mSelectedLocation = selectedLocation;
        firePropertyChange();
    }

    public TTemplate getSelectedHouse()
    {
        return mSelectedHouse;
    }

    public void setSelectedHouse(TTemplate selectedHouse)
    {
        queuePropertyChange("selectedHouse", mSelectedHouse, selectedHouse);
        mSelectedHouse = selectedHouse;
        firePropertyChange();
    }

    public TLocation getSelectedTile()
    {
        return mSelectedTile;
    }

    public void setSelectedTile(TLocation selectedTile)
    {
        queuePropertyChange("selectedTile", mSelectedTile, selectedTile);
        mSelectedTile = selectedTile;
        firePropertyChange();
    }

    public TApature getSelectedApature()
    {
        return mSelectedApature;
    }

    public void setSelectedApature(TApature selectedApature)
    {
        queuePropertyChange("selectedApature", mSelectedApature, selectedApature);
        mSelectedApature = selectedApature;
        firePropertyChange();
    }
}
