package jo.audio.companions.app.logic.match;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.app.logic.LookLogic;
import jo.audio.companions.data.CompState;
import jo.audio.util.model.data.AudioMessageBean;

public class LocationAboutInfo extends AboutInfo
{

    @Override
    public List<AudioMessageBean> response(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        AudioMessageBean msg = LookLogic.makeLonLatMessage(state);
        msgs.add(msg);
        return msgs;
    }

}
