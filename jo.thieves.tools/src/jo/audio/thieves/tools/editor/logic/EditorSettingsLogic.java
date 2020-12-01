package jo.audio.thieves.tools.editor.logic;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.thieves.tools.data.RuntimeBean;
import jo.audio.thieves.tools.editor.data.EditorSettings;
import jo.audio.thieves.tools.editor.data.TLocations;
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
            if ("-p".equals(rt.getArgs()[i]))
                es.setProjectDir(new File(rt.getArgs()[++i]));
        if (es.getProjectDir() == null)
            es.setProjectDir(new File("C:\\Users\\IBM_ADMIN\\git\\TsaTsaTzuAlexa\\jo.audio.thieves"));
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
        if (es.getSelectedLocation() != null)
            props.put("6thieves.tools.editor.selectedLocation", es.getSelectedLocation().getPath());
        else
            props.remove("6thieves.tools.editor.selectedLocation");
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
        if (props.containsKey("6thieves.tools.editor.selectedLocation"))
        {
            String selectedLocation = props.getProperty("6thieves.tools.editor.selectedLocation");
            es.setSelectedLocation(EditorLocationLogic.getLocation(selectedLocation));
        }
        if (props.containsKey("6thieves.tools.editor.selectedHouse"))
        {
            String selectedHouse = props.getProperty("6thieves.tools.editor.selectedHouse");
            es.setSelectedHouse(EditorHouseLogic.getHouse(selectedHouse));
        }
        if ((es.getSelectedLocation() == null) && (es.getSpecificLocations().size() > 0))
            es.setSelectedLocation(es.getSpecificLocations().values().iterator().next());
        if ((es.getSelectedHouse() == null) && (es.getSelectedLocation() != null) && (es.getSelectedLocation().getTemplates().size() > 0))
            es.setSelectedHouse(es.getSelectedLocation().getTemplates().get(0));
    }
    
    private static void load() throws Exception
    {
        EditorSettings es = getInstance();
        File sluDir = new File(es.getProjectDir(), "src/jo/audio/thieves/slu");
        File locationTypesFile = new File(sluDir, "locationTypes.json");
        //JSONObject locationTypes = JSONUtils.readJSON(locationTypesFile);
        JSONObject jlocationTypes = (JSONObject)JSONUtils.PARSER.parse(FileUtils.readFileAsString(locationTypesFile.toString(), "utf-8"));
        TLocations locationTypes = new TLocations();
        locationTypes.setPath("<root>");
        locationTypes.fromJSON(jlocationTypes);
        es.setGlobalLocations(locationTypes);
        JSONArray includes = JSONUtils.getArray(jlocationTypes, "include");
        for (int i = 0; i < includes.size(); i++)
        {
            String include = (String)includes.get(i);
            File includeFile = new File(sluDir, include);
            JSONObject jincludeLocations = JSONUtils.readJSON(includeFile);
            String includeName = include;
            if (includeName.startsWith("locations/"))
                includeName = includeName.substring(10);
            if (includeName.endsWith(".json"))
                includeName = includeName.substring(0, includeName.length() - 5);
            TLocations includeLocations = new TLocations();
            includeLocations.setPath(includeName);
            includeLocations.fromJSON(jincludeLocations);
            //includeLocations.fillPlaces(locationTypes);
            es.getSpecificLocations().put(includeName, includeLocations);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static void save() throws IOException
    {
        EditorSettings es = getInstance();
        File sluDir = new File(es.getProjectDir(), "src/jo/audio/thieves/slu");
        File locationTypesFile = new File(sluDir, "locationTypes.json");
        JSONObject json = es.getGlobalLocations().toJSON();
        JSONArray jinclude = new JSONArray();
        json.put("include", jinclude);
        for (String includeName : es.getSpecificLocations().keySet())
        {
            JSONObject includeLocations = es.getSpecificLocations().get(includeName).toJSON();
            String include = "locations/"+includeName+".json";
            File includeFile = new File(sluDir, include);
            //JSONUtils.writeJSON(includeFile, includeLocations);
            FileUtils.writeFile(JSONUtils.toFormattedString(includeLocations), includeFile);
            jinclude.add(include);            
        }
        //JSONUtils.writeJSON(locationTypesFile, json);
        FileUtils.writeFile(JSONUtils.toFormattedString(json), locationTypesFile);
    }
}
