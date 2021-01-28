package jo.audio.compedit.data;

import java.io.File;

import jo.audio.companions.data.build.PModuleBean;
import jo.audio.util.IIOBean;

public class CompEditModuleBean extends PModuleBean implements IIOBean
{
    private String  mUser;

    @Override
    public String[] toCSVHeader()
    {
        return null;
    }

    @Override
    public String toCSV()
    {
        return null;
    }

    @Override
    public void fromCSV(String csv)
    {
    }

    @Override
    public File getArchiveFile()
    {
        return null;
    }

    @Override
    public File getActiveFile()
    {
        return null;
    }

    @Override
    public String getURI()
    {
        return getID();
    }

    public String getUser()
    {
        return mUser;
    }

    public void setUser(String user)
    {
        mUser = user;
    }

}
