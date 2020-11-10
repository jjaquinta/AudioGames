package jo.audio.companions.service;

import java.awt.image.BufferedImage;

import jo.audio.companions.app.CompApplicationHandler;
import jo.audio.companions.data.LocationBean;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.logic.ModelResolveLogic;
import jo.util.utils.obj.StringUtils;

public class MapLogic
{    
    static final MapAssets mAssets = new MapAssets();

    public static void drawMap(BufferedImage img, LocationBean ords, int scale)
    {
        if (StringUtils.isTrivial(ords.getRoomID()))
            MapStrategicLogic.drawMapStrategic(img, ords, scale);
        else
            MapTacticalLogic.drawMapTactial(img, ords, scale);
    }
    
    public static String getText(AudioMessageBean msg)
    {
        return ModelResolveLogic.resolve(CompApplicationHandler.getInstance().getModel(), 
                "en_US", BaseUserState.RND, null, msg.getIdent(), msg.getArgs());
    }
}
