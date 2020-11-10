package jo.audio.companions.app.data;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.data.CompState;
import jo.audio.util.model.data.AudioMessageBean;

public class AboutInfoStatic extends AboutInfo
{
    private String   mKey;
    private Object[] mArgs;

    public AboutInfoStatic(String key, Object... args)
    {
        mKey = key;
        mArgs = args;
    }
    
    @Override
    public List<AudioMessageBean> response(CompState state)
    {
        List<AudioMessageBean> msgs = new ArrayList<>();
        msgs.add(new AudioMessageBean(mKey, mArgs));
        return msgs;
    }

}
