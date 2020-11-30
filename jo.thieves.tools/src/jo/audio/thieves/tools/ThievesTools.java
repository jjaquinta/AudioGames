package jo.audio.thieves.tools;

import jo.audio.thieves.tools.logic.RuntimeLogic;

public class ThievesTools
{

    public static void main(String[] args)
    {
        RuntimeLogic.init(args);
        ToolsFrame app = new ToolsFrame();
        app.setSize(1024, 768);
        app.setVisible(true);
    }

}
