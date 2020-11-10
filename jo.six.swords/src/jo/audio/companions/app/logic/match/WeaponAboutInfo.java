package jo.audio.companions.app.logic.match;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.app.logic.WhoLogic;
import jo.audio.companions.data.CompState;
import jo.audio.util.model.data.AudioMessageBean;

public class WeaponAboutInfo extends AboutInfo
{

    @Override
    public List<AudioMessageBean> response(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        msgs.add(WhoLogic.getWhoWeapons(state));
        return msgs;
    }

}
