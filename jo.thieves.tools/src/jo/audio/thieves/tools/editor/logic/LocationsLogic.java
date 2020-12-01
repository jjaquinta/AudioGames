package jo.audio.thieves.tools.editor.logic;

import java.awt.Color;

import jo.audio.thieves.tools.editor.data.TApature;
import jo.audio.thieves.tools.editor.data.TLocation;
import jo.audio.thieves.tools.editor.data.TLocations;
import jo.util.utils.MapUtils;

public class LocationsLogic
{
    public static TLocation getLocation(TLocations locs, char ch)
    {
        String id = locs.getIDMap().getProperty(String.valueOf(ch));
        for (TLocation loc : locs.getLocations())
            if (loc.getID().equals(id))
                return loc;
        return null;
    }

    public static Color getColor(TLocation tile)
    {
        TLocations loc = EditorSettingsLogic.getInstance().getSelectedLocation();
        String ch = (String)MapUtils.getKey(loc.getIDMap(), tile.getID());
        if (ch == null)
            return null;
        String val = loc.getColorMap().getProperty(ch);
        int rgb = Integer.parseInt(val.substring(1), 16);
        Color c = new Color(rgb);
        return c;
    }

    public static String getChar(TLocation tile)
    {
        TLocations loc = EditorSettingsLogic.getInstance().getSelectedLocation();
        String ch = (String)MapUtils.getKey(loc.getIDMap(), tile.getID());
        return ch;
    }

    public static String getChar(TApature apature)
    {
        TLocations loc = EditorSettingsLogic.getInstance().getSelectedLocation();
        String ch = (String)MapUtils.getKey(loc.getIDMap(), apature.getID());
        return ch;
    }

    public static Color getColor(TApature apature)
    {
        TLocations loc = EditorSettingsLogic.getInstance().getSelectedLocation();
        String ch = (String)MapUtils.getKey(loc.getIDMap(), apature.getID());
        String val = loc.getColorMap().getProperty(ch);
        int rgb = Integer.parseInt(val.substring(1), 16);
        Color c = new Color(rgb);
        return c;
    }

}
