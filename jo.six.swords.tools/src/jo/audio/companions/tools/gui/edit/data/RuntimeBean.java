package jo.audio.companions.tools.gui.edit.data;

import java.io.File;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.companions.data.build.PFeatureBean;
import jo.audio.companions.data.build.PModuleBean;
import jo.audio.companions.data.build.PRoomBean;
import jo.audio.compedit.data.CompEditModuleBean;
import jo.util.beans.PCSBean;

public class RuntimeBean extends PCSBean
{
    private File                     mLastDirectory;
    private File                     mLocationFile;
    private String                   mLocationText;
    private JSONObject               mLocationJSON;
    private PModuleBean              mLocationRich;
    private CompEditModuleBean       mLocationDynamo;
    private boolean                  mLoaded;
    private List<CompEditModuleBean> mDynamoModules;
    private PFeatureBean             mSelectedFeature;
    private PRoomBean                mSelectedRoom;

    public File getLastDirectory()
    {
        return mLastDirectory;
    }

    public void setLastDirectory(File lastDirectory)
    {
        queuePropertyChange("lastDirectory", mLastDirectory, lastDirectory);
        mLastDirectory = lastDirectory;
        firePropertyChange();
    }

    public File getLocationFile()
    {
        return mLocationFile;
    }

    public void setLocationFile(File locationFile)
    {
        queuePropertyChange("locationFile", mLocationFile, locationFile);
        mLocationFile = locationFile;
        firePropertyChange();
    }

    public String getLocationText()
    {
        return mLocationText;
    }

    public void setLocationText(String locationText)
    {
        queuePropertyChange("locationText", mLocationText, locationText);
        mLocationText = locationText;
        firePropertyChange();
    }

    public JSONObject getLocationJSON()
    {
        return mLocationJSON;
    }

    public void setLocationJSON(JSONObject locationJSON)
    {
        queuePropertyChange("locationJSON", mLocationJSON, locationJSON);
        mLocationJSON = locationJSON;
        firePropertyChange();
    }

    public boolean isLoaded()
    {
        return mLoaded;
    }

    public void setLoaded(boolean loaded)
    {
        queuePropertyChange("loaded", mLoaded, loaded);
        mLoaded = loaded;
        firePropertyChange();
    }

    public PModuleBean getLocationRich()
    {
        return mLocationRich;
    }

    public void setLocationRich(PModuleBean locationRich)
    {
        queuePropertyChange("locationRich", mLocationRich, locationRich);
        mLocationRich = locationRich;
        firePropertyChange();
    }

    public List<CompEditModuleBean> getDynamoModules()
    {
        return mDynamoModules;
    }

    public void setDynamoModules(List<CompEditModuleBean> dynamoModules)
    {
        queuePropertyChange("dynamoModules", mDynamoModules, dynamoModules);
        mDynamoModules = dynamoModules;
        firePropertyChange();
    }

    public CompEditModuleBean getLocationDynamo()
    {
        return mLocationDynamo;
    }

    public void setLocationDynamo(CompEditModuleBean locationDynamo)
    {
        queuePropertyChange("locationDynamo", mLocationDynamo, locationDynamo);
        mLocationDynamo = locationDynamo;
        firePropertyChange();
    }

    public PFeatureBean getSelectedFeature()
    {
        return mSelectedFeature;
    }

    public void setSelectedFeature(PFeatureBean selectedFeature)
    {
        queuePropertyChange("selectedFeature", mSelectedFeature, selectedFeature);
        mSelectedFeature = selectedFeature;
        firePropertyChange();
    }

    public PRoomBean getSelectedRoom()
    {
        return mSelectedRoom;
    }

    public void setSelectedRoom(PRoomBean selectedRoom)
    {
        queuePropertyChange("selectedRoom", mSelectedRoom, selectedRoom);
        mSelectedRoom = selectedRoom;
        firePropertyChange();
    }
}
