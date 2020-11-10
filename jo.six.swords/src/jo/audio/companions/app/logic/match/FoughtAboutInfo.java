package jo.audio.companions.app.logic.match;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;

public class FoughtAboutInfo extends AboutInfo
{

    @Override
    public List<AudioMessageBean> response(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        CompUserBean user = state.getUser();
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_FOUGHT_XXX_BATTLES_AND_WON_YYY_OF_THEM,
                user.getTotalFights(), user.getTotalWins()));
        return msgs;
    }

}
