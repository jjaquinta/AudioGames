package jo.audio.companions.tools;

import java.util.List;
import java.util.Map;

import jo.audio.companions.tools.gui.map.MapAssets;

public class LangExport
{
    private String[]    mArgs;
    
    private MapAssets mAssets = new MapAssets();
        
    public LangExport(String[] args)
    {
        mArgs = args;
    }
    
    public void run()
    {
        parseArgs();
        Map<String,List<String>> text = mAssets.getText("en_US");
        for (String key : text.keySet())
            for (String val : text.get(key))
                System.out.println(val);
    }
    
    private void parseArgs()
    {
        for (int i = 0; i < mArgs.length; i++)
            ;
    }
    
    public static void main(String[] args)
    {
        LangExport app = new LangExport(args);
        app.run();
    }
}
