package jo.audio.companions.app.logic.match;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.app.logic.OperationLogic;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;

public class NearbyAboutInfo extends AboutInfo
{

    @Override
    public List<AudioMessageBean> response(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        CompContextBean context = OperationLogic.queryNearby(state);
        List<AudioMessageBean> features = context.getNearbyFeatures();
        if (features.size() == 0)
        {
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_NOTHING_NEARBY));            
        }
        else
        {
            while (features.size() > 0)
            {
                if (features.size() >= 3)
                {
                    msgs.add(AudioMessageBean.group(features.get(0), features.get(1), features.get(2)));
                    features.remove(2);
                    features.remove(1);
                    features.remove(0);
                }
                else if (features.size() == 2)
                {
                    msgs.add(AudioMessageBean.group(features.get(0), features.get(1)));
                    features.remove(1);
                    features.remove(0);
                }
                else
                {
                    msgs.add(features.get(0));
                    features.remove(0);
                }
            }
        }
        return msgs;
    }

}
