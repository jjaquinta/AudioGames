package jo.audio.compedit.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.build.PFeatureBean;
import jo.audio.companions.data.build.PRoomBean;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;

public class CompEditContextBean implements IJSONAble
{
    private CompEditIdentBean mID;
    private CompEditUserBean mUser;
    private CompEditOperationBean mLastOperation;
    private boolean     mError;
    private List<AudioMessageBean> mMessages = new ArrayList<>();
    private CompEditLocationBean mLocation;
    private CompEditModuleBean  mModule;
    private PFeatureBean        mFeature;
    private PRoomBean           mRoom;

    public CompEditContextBean()
    {
        mLastOperation = null;
    }
    
    // utility functions
    
    public void addMessage(String ident, Object... args)
    {
        addMessage(0, ident, args);
    }
    
    public void addMessage(int priority, String ident, Object... args)
    {
        AudioMessageBean msg = new AudioMessageBean();
        msg.setPriority(priority);
        msg.setIdent(ident);
        msg.setArgs(args);
        DebugUtils.trace("CompContextBean - addMessage(priority="+priority+", ident="+ident+" -> "+msg+")");
        addMessage(msg);
    }

    public void addMessageIfNew(String ident, Object... args)
    {
        AudioMessageBean msg = new AudioMessageBean();
        msg.setIdent(ident);
        msg.setArgs(args);
        addMessageIfNew(msg);
    }

    public void addMessage(AudioMessageBean msg)
    {
        mMessages.add(msg);
        DebugUtils.trace("CompContextBean - addMessage("+msg+")");
    }

    public void addMessageIfNew(AudioMessageBean msg)
    {
        for (AudioMessageBean m : mMessages)
            if (m.equals(msg))
                return;
        addMessage(msg);
    }

    public void setLastOperationError(String lastOperationError, Object... args)
    {
        mError = true;
        addMessage(lastOperationError, args);
    }

    public void setOperationMessage(String operationMessage, Object... args)
    {
        addMessage(operationMessage, args);
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        if (mID != null)
            json.put("id", mID.toJSON());
        if (mUser != null)
            json.put("user", mUser.toJSON());
        json.put("error", mError);
        if (mLastOperation != null)
            json.put("lastOperation", mLastOperation.toJSON());
        if (mMessages != null)
            json.put("messages", JSONUtils.toJSON(mMessages.toArray()));
        return json;
    }

    @Override
    public void fromJSON(JSONObject o)
    {
        // TODO Auto-generated method stub
        
    }

    public CompEditOperationBean getLastOperation()
    {
        return mLastOperation;
    }

    public void setLastOperation(CompEditOperationBean lastOperation)
    {
        mLastOperation = lastOperation;
    }

    public CompEditUserBean getUser()
    {
        return mUser;
    }

    public void setUser(CompEditUserBean coreUser)
    {
        mUser = coreUser;
    }
    
    public List<AudioMessageBean> getMessages()
    {
        return mMessages;
    }

    public void setMessages(List<AudioMessageBean> messages)
    {
        mMessages = messages;
    }

    public boolean isError()
    {
        return mError;
    }

    public void setError(boolean error)
    {
        mError = error;
    }

    public CompEditIdentBean getID()
    {
        return mID;
    }

    public void setID(CompEditIdentBean iD)
    {
        mID = iD;
    }

    public CompEditModuleBean getModule()
    {
        return mModule;
    }

    public void setModule(CompEditModuleBean module)
    {
        mModule = module;
    }

    public PFeatureBean getFeature()
    {
        return mFeature;
    }

    public void setFeature(PFeatureBean feature)
    {
        mFeature = feature;
    }

    public PRoomBean getRoom()
    {
        return mRoom;
    }

    public void setRoom(PRoomBean room)
    {
        mRoom = room;
    }

    public CompEditLocationBean getLocation()
    {
        return mLocation;
    }

    public void setLocation(CompEditLocationBean location)
    {
        mLocation = location;
    }
}
