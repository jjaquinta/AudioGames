package jo.audio.util.state.logic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jo.audio.util.BaseUserState;
import jo.audio.util.EnumerationUtils;
import jo.audio.util.model.data.AudioRequestBean;
import jo.audio.util.model.data.IntentDefBean;
import jo.audio.util.model.data.IntentReqBean;
import jo.audio.util.model.data.SlotBean;

public class MethodActionHandler extends ActionHandler
{
    private StateHandler    mStateHandler;
    private Method          mIntentHandler;
    private IntentDefBean      mIntentSpec;
    
    public MethodActionHandler(StateHandler stateHandler, Method intentHandler, IntentDefBean intentSpec)
    {
        mStateHandler = stateHandler;
        mIntentHandler = intentHandler;
        mIntentSpec = intentSpec;
    }
    
    private void log(String msg)
    {
        mStateHandler.log(msg);
    }
    
    @Override
    public void handle(BaseUserState state, IntentReqBean inputSymbol)
    {
        log("Invoking method for "+inputSymbol.getIntentID());
        try
        {
            List<Object> args = new ArrayList<>();
            Class<?>[] argTypes = mIntentHandler.getParameterTypes();
            int slotArg = 0;
            for (int i = 0; i < argTypes.length; i++)
            {
                log("Arg #"+i+" type="+argTypes[i].getName());
                if (StateHandler.class.isAssignableFrom(argTypes[i]))
                    args.add(mStateHandler);
                else if (BaseUserState.class.isAssignableFrom(argTypes[i]))
                    args.add(state);
                else if (IntentReqBean.class == argTypes[i])
                    args.add(inputSymbol);
                else if (IntentDefBean.class == argTypes[i])
                    args.add(mIntentSpec);
                else if (AudioRequestBean.class == argTypes[i])
                    args.add(state.getRequest());
                else
                {   // assume slot arg
                    if (slotArg >= mIntentSpec.getSlots().size())
                        throw new IllegalStateException("Cannot work out arg#"+i
                                +" of type "+argTypes[i].getName()
                                +" on "+mStateHandler.getClass().getName()+"."+mIntentHandler.getName()+"()");
                    SlotBean slotSpec = mIntentSpec.getSlots().get(slotArg);
                    String slotValue = inputSymbol.getSlots().getProperty(slotSpec.getName());
                    log("Assuming slot '"+slotSpec.getName()+"', val="+slotValue);
                    if (slotValue == null)
                        args.add(null);
                    else
                    {
                        if (String.class == argTypes[i])
                            args.add(slotValue);
                        else if (Integer.class == argTypes[i])
                            args.add(EnumerationUtils.getEnumeration(slotValue));
                        else
                            throw new IllegalStateException("Cannot convert arg#"+i
                                    +" of type "+argTypes[i].getName()
                                    +" on "+mStateHandler.getClass().getName()+"."+mIntentHandler.getName()+"()"
                                    +" from '"+slotValue+"'");
                    }
                    slotArg++;
                }
            }
            mIntentHandler.invoke(mStateHandler, args.toArray());
        }
        catch (Throwable e)
        {
            mStateHandler.getApp().log(e);
            throw new IllegalStateException("Error invoking "+mStateHandler.getClass().getSimpleName()+"."+mIntentHandler.getName(), e);
        }
    }
}
