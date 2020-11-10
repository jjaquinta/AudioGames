package jo.audio.companions.app.logic.match;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONUtils;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;

public class BlessAboutInfo extends AboutInfo
{

    @Override
    public List<AudioMessageBean> response(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        CompUserBean user = state.getUser();
        int blessingsNum = JSONUtils.getInt(user.getMetadata(), "BLESSINGS_NUM");
        int who = ResponseUtils.countList(JSONUtils.getString(user.getMetadata(), "BLESSINGS_WHO"));
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_BEEN_BLESSED_XXX_TIMES_BY_YYY_DEITIES,
                blessingsNum, who));
        return msgs;
    }

}
