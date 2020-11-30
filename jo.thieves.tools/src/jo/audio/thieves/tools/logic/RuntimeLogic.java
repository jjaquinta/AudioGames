package jo.audio.thieves.tools.logic;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import jo.audio.thieves.tools.data.RuntimeBean;
import jo.audio.thieves.tools.editor.logic.EditorSettingsLogic;
import jo.util.utils.obj.PropertiesLogic;
import jo.util.utils.obj.StringUtils;

public class RuntimeLogic
{
    private static RuntimeBean  mRuntime = null;
    
    public static void init(String[] args)
    {
        if (mRuntime != null)
            return;
        mRuntime = new RuntimeBean();
        mRuntime.setArgs(args);
        File propsDir = new File(System.getProperty("user.home"), ".jo");
        File propsFile = new File(propsDir, "thieves.properties");
        if (propsFile.exists())
        {
            try
            {
                Properties props = PropertiesLogic.readProperties(propsFile);
                mRuntime.setProps(props);
                fromProps();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        // other area inits
        EditorSettingsLogic.init();
    }
    
    public static RuntimeBean getInstance()
    {
        if (mRuntime == null)
            RuntimeLogic.init(new String[0]);
        return mRuntime;
    }
    
    public static void save()
    {
        
        // other area saves
        try
        {
            EditorSettingsLogic.save();
        }
        catch (IOException e)
        {
            RuntimeLogic.error(e);
        }
    }
    
    public static void shutdown()
    {
        toProps();
        // other area shutdowns
        EditorSettingsLogic.shutdown();

        File propsDir = new File(System.getProperty("user.home"), ".jo");
        File propsFile = new File(propsDir, "thieves.properties");
        try
        {
            PropertiesLogic.writeProperties(mRuntime.getProps(), propsFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void toProps()
    {
        if (mRuntime.getLastDirectory() != null)
            mRuntime.getProps().put("6thieves.tools.lastDirectory", mRuntime.getLastDirectory().toString());
        else
            mRuntime.getProps().remove("6thieves.tools.lastDirectory");
    }

    private static void fromProps()
    {
        Properties props = mRuntime.getProps();
        if (props.containsKey("6thieves.tools.lastDirectory"))
            mRuntime.setLastDirectory(new File(props.getProperty("6thieves.tools.lastDirectory")));
    }
    
    public static void error(Throwable t)
    {
        mRuntime.setLastError(t);
        if (StringUtils.isTrivial(t.getMessage()))
            mRuntime.setStatus(t.getMessage());
        else
            mRuntime.setStatus(t.getLocalizedMessage());
        t.printStackTrace();
    }

    public static void status(String txt)
    {
        mRuntime.setStatus(txt);
    }
}
