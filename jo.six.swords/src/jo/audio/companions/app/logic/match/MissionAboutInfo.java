package jo.audio.companions.app.logic.match;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;

public class MissionAboutInfo extends AboutInfo
{

    @Override
    public List<AudioMessageBean> response(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        CompUserBean user = state.getUser();
        JSONObject mission = JSONUtils.getObject(user.getMetadata(), "MISSION.msg");
        if (mission == null)
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_ARE_NOT_CURRENTLY_ON_A_MISSION));
        else
        {
            AudioMessageBean msg = new AudioMessageBean(mission);
            msgs.add(msg);
        }
        return msgs;
    }

}
