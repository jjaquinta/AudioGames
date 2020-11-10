package jo.audio.util.state.logic;

import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.IntentReqBean;

public abstract class ActionHandler
{
    public abstract void handle(BaseUserState state, IntentReqBean inputSymbol);
}
