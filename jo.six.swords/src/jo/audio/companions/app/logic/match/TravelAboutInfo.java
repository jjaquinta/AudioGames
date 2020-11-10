package jo.audio.companions.app.logic.match;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;

public class TravelAboutInfo extends AboutInfo
{

    @Override
    public List<AudioMessageBean> response(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        CompUserBean user = state.getUser();
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_TRAVELED_A_TOTAL_OF_XXX_MILES_AND_RANGED_UP_TO_YYY_MILES_FROM_YOUR_STARTING_POINT,
                (int)user.getTotalDistance(), (int)user.getMaxDistance()));
        return msgs;
    }

}
