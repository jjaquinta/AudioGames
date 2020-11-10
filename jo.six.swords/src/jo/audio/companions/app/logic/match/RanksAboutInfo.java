package jo.audio.companions.app.logic.match;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.app.logic.OperationLogic;
import jo.audio.companions.data.CompState;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.EnumerationUtils;
import jo.audio.util.model.data.AudioMessageBean;

public class RanksAboutInfo extends AboutInfo
{

    @Override
    public List<AudioMessageBean> response(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        if (state.getContext().getRanks() == null)
            OperationLogic.queryRanks(state, state.getLinkedName(), state.getLinkedEmail());        
        if (state.getContext().getRanks().size() > 0)
        {
            List<AudioMessageBean> ranks = new ArrayList<>();
            for (String rankID : state.getContext().getRanks().keySet())
            {
                int rankValue = state.getContext().getRanks().get(rankID);
                AudioMessageBean msg = new AudioMessageBean(CompanionsModelConst.TEXT_XXX_AT_YYY, 
                        EnumerationUtils.toOrdinal(rankValue),
                        "{{RANK_"+rankID+"}}");
                ranks.add(msg);
            }
            AudioMessageBean list = new AudioMessageBean(AudioMessageBean.AND, ranks.toArray());
            msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_ARE_RANKED_XXX, list));
        }
        return msgs;
    }

}
