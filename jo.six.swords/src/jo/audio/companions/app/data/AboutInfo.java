package jo.audio.companions.app.data;

import java.util.List;

import jo.audio.companions.data.CompState;
import jo.audio.util.model.data.AudioMessageBean;

public abstract class AboutInfo
{
    public abstract List<AudioMessageBean> response(CompState state);
}
