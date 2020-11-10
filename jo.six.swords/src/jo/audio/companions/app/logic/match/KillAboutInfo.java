package jo.audio.companions.app.logic.match;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;

public class KillAboutInfo extends AboutInfo
{

    @Override
    public List<AudioMessageBean> response(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        CompUserBean user = state.getUser();
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_KILLED_XXX_MONSTERS_OF_YYY_DIFFERENT_TYPES,
                user.getTotalKills(), ResponseUtils.countList(user.getKillList())));
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_KILLED_XXX_BOSS_MONSTERS_AND_COLLECTED_YYY_BOUNTIES,
                ResponseUtils.countList(user.getBossKillList()), ResponseUtils.countList(user.getBountyList())));
        return msgs;
    }

}
