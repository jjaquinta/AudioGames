package jo.audio.thieves.tools.data;

import java.io.File;
import java.util.Properties;

import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.util.beans.PCSBean;

public class RuntimeBean extends PCSBean
{
    private String[]       mArgs;
    private boolean        mAnyChange;
    private Properties     mProps = new Properties();
    private String         mStatus;
    private Throwable      mLastError;
    // other area settings
    private EditorSettings mEditorSettings;
    // persistant values
    private File           mLastDirectory;

    // utilities

    // getters and setters

    public boolean isAnyChange()
    {
        return mAnyChange;
    }

    public void setAnyChange(boolean anyChange)
    {
        mAnyChange = anyChange;
    }

    public Properties getProps()
    {
        return mProps;
    }

    public void setProps(Properties props)
    {
        mProps = props;
    }

    public File getLastDirectory()
    {
        return mLastDirectory;
    }

    public void setLastDirectory(File lastDirectory)
    {
        mLastDirectory = lastDirectory;
    }

    public EditorSettings getEditorSettings()
    {
        return mEditorSettings;
    }

    public void setEditorSettings(EditorSettings editorSettings)
    {
        mEditorSettings = editorSettings;
    }

    public String[] getArgs()
    {
        return mArgs;
    }

    public void setArgs(String[] args)
    {
        mArgs = args;
    }

    public String getStatus()
    {
        return mStatus;
    }

    public void setStatus(String status)
    {
        queuePropertyChange("status", mStatus, status);
        mStatus = status;
        firePropertyChange();
    }

    public Throwable getLastError()
    {
        return mLastError;
    }

    public void setLastError(Throwable lastError)
    {
        mLastError = lastError;
    }
}
