package jo.audio.companions.app.logic.match;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.data.CompState;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.obj.StringUtils;

public class AchievementAboutInfo extends AboutInfo
{

    @Override
    public List<AudioMessageBean> response(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        CompUserBean user = state.getUser();
        if (!StringUtils.isTrivial(user.getTags()))
        {
            List<String> badges = new ArrayList<>();
            for (StringTokenizer st = new StringTokenizer(user.getTags(), " "); st.hasMoreElements(); )
            {
                String tag = st.nextToken();
                if (CompUserBean.USER_TAG_KEYS.containsKey(tag))
                    badges.add("{{"+CompUserBean.USER_TAG_KEYS.get(tag)+"}}");
            }
            if (badges.size() > 0)
            {
                AudioMessageBean list = new AudioMessageBean(AudioMessageBean.AND, badges.toArray());
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_ACHIEVEMENTS_ARE_XXX, user.getSupportIdent(), list));
            }
        }
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_VISIT_HELP));
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_TRAVELLER_HELP));
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_BANKER_HELP));
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_RANGER_HELP));
        return msgs;
    }

}
