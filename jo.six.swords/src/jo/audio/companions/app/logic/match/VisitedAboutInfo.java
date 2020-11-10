package jo.audio.companions.app.logic.match;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;

public class VisitedAboutInfo extends AboutInfo
{

    @Override
    public List<AudioMessageBean> response(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        CompUserBean user = state.getUser();
        int arg1 = ResponseUtils.countList(user.getVisitList());
        int arg2 = (int)user.getMaxGoldPieces();
        AudioMessageBean msg = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_VISITED_XXX_LOCATIONS_AND_CARRIED_A_MAXIMUM_OF_YYY_GOLD_PIECES, 
                arg1, arg2);
        msgs.add(msg);
        return msgs;
    }

}
