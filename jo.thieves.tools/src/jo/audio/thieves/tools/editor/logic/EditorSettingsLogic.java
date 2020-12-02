package jo.audio.thieves.tools.editor.logic;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.json.simple.JSONUtils;

import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.logic.template.LibraryLogic;
import jo.audio.thieves.tools.data.RuntimeBean;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.logic.RuntimeLogic;
import jo.util.utils.io.FileUtils;

public class EditorSettingsLogic
{
    public static EditorSettings getInstance()
    {
        return RuntimeLogic.getInstance().getEditorSettings();
    }
    
    public static void init()
    {
        RuntimeBean rt = RuntimeLogic.getInstance();
        EditorSettings es = getInstance();
        if (es == null)
        {
            es = new EditorSettings();
            rt.setEditorSettings(es);
        }
        for (int i = 0; i < rt.getArgs().length; i++)
            if ("-l".equals(rt.getArgs()[i]))
                es.setLibraryFile(new File(rt.getArgs()[++i]));
        if (es.getLibraryFile() == null)
            es.setLibraryFile(new File("C:\\Users\\JoJaquinta\\git\\AudioGames\\jo.thieves.core\\src\\jo\\audio\\thieves\\slu\\locationLibrary.json"));
        try
        {
            load();
        }
        catch (Exception e)
        {
            RuntimeLogic.error(e);
        }
        fromProps();
    }
    
    public static void shutdown()
    {
        toProps();

    }

    private static void toProps()
    {
        RuntimeBean rt = RuntimeLogic.getInstance();
        EditorSettings es = getInstance();
        Properties props = rt.getProps();
        if (es.getSelectedHouse() != null)
            props.put("6thieves.tools.editor.selectedHouse", es.getSelectedHouse().getID());
        else
            props.remove("6thieves.tools.editor.selectedHouse");
    }

    private static void fromProps()
    {
        RuntimeBean rt = RuntimeLogic.getInstance();
        EditorSettings es = getInstance();
        Properties props = rt.getProps();
        if (props.containsKey("6thieves.tools.editor.selectedHouse"))
        {
            String selectedHouse = props.getProperty("6thieves.tools.editor.selectedHouse");
            es.setSelectedHouse(EditorHouseLogic.getHouse(selectedHouse));
        }
        if ((es.getSelectedHouse() == null) && (es.getLibrary() != null) && (es.getLibrary().getTemplates().size() > 0))
            es.setSelectedHouse(es.getLibrary().getTemplates().values().iterator().next());
    }
    
    private static void load() throws Exception
    {
        EditorSettings es = getInstance();
        File libFile = es.getLibraryFile();
        PLibrary lib = LibraryLogic.read(libFile);
        es.setLibrary(lib);
    }
    
    public static void save() throws IOException
    {
        EditorSettings es = getInstance();
        File libFile = es.getLibraryFile();
        FileUtils.writeFile(JSONUtils.toFormattedString(es.getLibrary().toJSON()), libFile);
    }
}
