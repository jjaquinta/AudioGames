package jo.audio.companions.tools.gui.edit.logic;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.build.PModuleBean;
import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.compedit.data.CompEditModuleBean;
import jo.audio.compedit.logic.CompEditIOLogic;
import jo.util.utils.io.FileUtils;
import jo.util.utils.obj.PropertiesLogic;

public class RuntimeLogic
{

    public static RuntimeBean newInstance()
    {
        RuntimeBean rt = new RuntimeBean();
        File propsDir = new File(System.getProperty("user.home"), ".jo");
        File propsFile = new File(propsDir, "companions.properties");
        if (propsFile.exists())
        {
            try
            {
                Properties props = PropertiesLogic.readProperties(propsFile);
                if (props.containsKey("6swords.tools.lastDirectory"))
                    rt.setLastDirectory(new File(props.getProperty("6swords.tools.lastDirectory")));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return rt;
    }

    public static void openFile(RuntimeBean rt, File file)
    {
        try
        {
            JSONObject raw = JSONUtils.readJSON(file);
            PModuleBean rich = new PModuleBean();
            rich.fromJSON(raw);
            String rawText = JSONUtils.toFormattedString(raw);
            rt.setLastDirectory(file.getParentFile());
            rt.setLocationFile(file);
            rt.setLocationJSON(raw);
            rt.setLocationRich(rich);
            rt.setLocationText(rawText);
            rt.setLocationDynamo(null);
            rt.setLoaded(true);
            if (rich.getFeatures().size() > 0)
            {
                rt.setSelectedFeature(rich.getFeatures().get(0));
                if (rt.getSelectedFeature().getRooms().size() > 0)
                    rt.setSelectedRoom(rt.getSelectedFeature().getRooms().get(0));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void shutdown(RuntimeBean rt)
    {
        File propsDir = new File(System.getProperty("user.home"), ".jo");
        File propsFile = new File(propsDir, "companions.properties");
        Properties props = new Properties();
        if (rt.getLastDirectory() != null)
            props.put("6swords.tools.lastDirectory", rt.getLastDirectory().toString());
        try
        {
            PropertiesLogic.writeProperties(props, propsFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void updateLocationText(RuntimeBean rt, String txt)
    {
        try
        {
            JSONObject json = (JSONObject)JSONUtils.PARSER.parse(txt);
            PModuleBean rich = new PModuleBean();
            rich.fromJSON(json);
            rt.setLocationJSON(json);
            rt.setLocationRich(rich);
            rt.setLocationText(txt);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void updateLocationJSON(RuntimeBean rt, JSONObject json)
    {
        PModuleBean rich = new PModuleBean();
        rich.fromJSON(json);
        rt.setLocationJSON(json);
        rt.setLocationRich(rich);
        rt.setLocationText(JSONUtils.toFormattedString(json));
    }

    public static void updateLocationRich(RuntimeBean rt, PModuleBean rich)
    {
        rt.setLocationRich(rich);
        rt.setLocationJSON(rich.toJSON());
        rt.setLocationText(JSONUtils.toFormattedString(rt.getLocationJSON()));
    }

    public static void close(RuntimeBean rt)
    {
        rt.setLoaded(false);
        rt.setLocationFile(null);
        rt.setLocationJSON(null);
        rt.setLocationRich(null);
        rt.setLocationText(null);
    }

    public static void saveAs(RuntimeBean runtime, File f)
    {
        runtime.setLocationFile(f);
        runtime.setLastDirectory(f.getParentFile());
        save(runtime);
    }

    public static void save(RuntimeBean runtime)
    {
        if (runtime.getLocationDynamo() != null)
        {
            PModuleBean rich = runtime.getLocationRich();
            JSONObject json = rich.toJSON();
            CompEditModuleBean mod = (CompEditModuleBean)runtime.getLocationDynamo();
            mod.fromJSON(json);
            CompEditIOLogic.saveModule(mod);
        }
        else
        {
            JSONObject json = runtime.getLocationRich().toJSON();
            try
            {
                FileUtils.writeFile(json.toJSONString(), runtime.getLocationFile());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void updateDynamoModules(RuntimeBean runtime)
    {
        List<CompEditModuleBean> modules = CompEditIOLogic.getAllModules();
        runtime.setDynamoModules(modules);
    }

    public static void newDynamoModule(RuntimeBean runtime)
    {
        // TODO Auto-generated method stub
        
    }

    public static void openDynamoModule(RuntimeBean rt,
            CompEditModuleBean module)
    {
        JSONObject json = module.toJSON();
        String rawText = JSONUtils.toFormattedString(json);
        rt.setLastDirectory(null);
        rt.setLocationFile(null);
        rt.setLocationJSON(json);
        rt.setLocationRich(module);
        rt.setLocationText(rawText);
        rt.setLocationDynamo(module);
        if (module.getFeatures().size() > 0)
        {
            rt.setSelectedFeature(module.getFeatures().get(0));
            if (rt.getSelectedFeature().getRooms().size() > 0)
                rt.setSelectedRoom(rt.getSelectedFeature().getRooms().get(0));
        }
        rt.setLoaded(true);
    }
}
