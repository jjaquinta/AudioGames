package jo.audio.companions.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.util.model.data.AudioMessageBean;

public class CompContextBean implements IJSONAble
{
    private CompIdentBean mID;
    private CompUserBean mUser;
    private CompCompanionBean mCompanion;
    private GeoBean mLocation;
    private DomainBean mDomain;
    private RegionBean mRegion;
    private SquareBean mSquare;
    private SquareBean mSquareNorth;
    private SquareBean mSquareSouth;
    private SquareBean mSquareEast;
    private SquareBean mSquareWest;
    private FeatureInstanceBean mFeature;
    private CompRoomBean mRoom;
    private CompMonsterTypeBean mSpoor;
    private CompOperationBean mLastOperation;
    private boolean     mError;
    private boolean     mNewText;
    private boolean     mPremium;
    private boolean     mCanPremium;
    private List<AudioMessageBean> mMessages = new ArrayList<>();
    private List<String> mActiveUsers = new ArrayList<>();
    private JSONObject mTextModel;
    private Map<String, Integer> mRanks;
    private List<AudioMessageBean> mSignpostNames = new ArrayList<>();
    private List<AudioMessageBean> mMessageDestinations = new ArrayList<>();
    private List<AudioMessageBean> mNearbyFeatures = new ArrayList<>();
    // weather
    private int mCloudCover;
    private int mPrecipitation;

    public CompContextBean()
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
        //DebugUtils.trace("CompContextBean - addMessage(priority="+priority+", ident="+ident+" -> "+msg+")");
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
        //DebugUtils.trace("CompContextBean - addMessage("+msg+")");
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
    
    public boolean isRoomParam(String path)
    {
        return getRoomParam(path) != null;
    }
    
    public Object getRoomParam(String path)
    {
        if (getRoom() == null)
            return null;
        return JSONUtils.get(getRoom().getParams(), path);
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

    public CompOperationBean getLastOperation()
    {
        return mLastOperation;
    }

    public void setLastOperation(CompOperationBean lastOperation)
    {
        mLastOperation = lastOperation;
    }

    public List<String> getActiveUsers()
    {
        return mActiveUsers;
    }

    public void setActiveUsers(List<String> activeUsers)
    {
        mActiveUsers = activeUsers;
    }

    public CompUserBean getUser()
    {
        return mUser;
    }

    public void setUser(CompUserBean coreUser)
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

    public CompIdentBean getID()
    {
        return mID;
    }

    public void setID(CompIdentBean iD)
    {
        mID = iD;
    }

    public DomainBean getDomain()
    {
        return mDomain;
    }

    public void setDomain(DomainBean domain)
    {
        mDomain = domain;
    }

    public RegionBean getRegion()
    {
        return mRegion;
    }

    public void setRegion(RegionBean region)
    {
        mRegion = region;
    }

    public SquareBean getSquare()
    {
        return mSquare;
    }

    public void setSquare(SquareBean square)
    {
        mSquare = square;
    }

    public CompCompanionBean getCompanion()
    {
        return mCompanion;
    }

    public void setCompanion(CompCompanionBean companion)
    {
        mCompanion = companion;
    }

    public SquareBean getSquare(int dir)
    {
        switch (dir)
        {
            case CompOperationBean.NORTH:
                return mSquareNorth;
            case CompOperationBean.SOUTH:
                return mSquareSouth;
            case CompOperationBean.EAST:
                return mSquareEast;
            case CompOperationBean.WEST:
                return mSquareWest;
        }
        throw new IllegalArgumentException("Bad dir="+dir);
    }

    public SquareBean getSquareNorth()
    {
        return mSquareNorth;
    }

    public void setSquareNorth(SquareBean squareNorth)
    {
        mSquareNorth = squareNorth;
    }

    public SquareBean getSquareSouth()
    {
        return mSquareSouth;
    }

    public void setSquareSouth(SquareBean squareSouth)
    {
        mSquareSouth = squareSouth;
    }

    public SquareBean getSquareEast()
    {
        return mSquareEast;
    }

    public void setSquareEast(SquareBean squareEast)
    {
        mSquareEast = squareEast;
    }

    public SquareBean getSquareWest()
    {
        return mSquareWest;
    }

    public void setSquareWest(SquareBean squareWest)
    {
        mSquareWest = squareWest;
    }

    public CompMonsterTypeBean getSpoor()
    {
        return mSpoor;
    }

    public void setSpoor(CompMonsterTypeBean spoor)
    {
        mSpoor = spoor;
    }

    public FeatureInstanceBean getFeature()
    {
        return mFeature;
    }

    public void setFeature(FeatureInstanceBean feature)
    {
        mFeature = feature;
    }

    public GeoBean getLocation()
    {
        return mLocation;
    }

    public void setLocation(GeoBean location)
    {
        mLocation = location;
    }

    public CompRoomBean getRoom()
    {
        return mRoom;
    }

    public void setRoom(CompRoomBean room)
    {
        mRoom = room;
    }

    public JSONObject getTextModel()
    {
        return mTextModel;
    }

    public void setTextModel(JSONObject textModel)
    {
        mTextModel = textModel;
    }

    public Map<String, Integer> getRanks()
    {
        return mRanks;
    }

    public void setRanks(Map<String, Integer> ranks)
    {
        mRanks = ranks;
    }

    public List<AudioMessageBean> getSignpostNames()
    {
        return mSignpostNames;
    }

    public void setSignpostNames(List<AudioMessageBean> signpostNames)
    {
        mSignpostNames = signpostNames;
    }

    public List<AudioMessageBean> getMessageDestinations()
    {
        return mMessageDestinations;
    }

    public void setMessageDestinations(List<AudioMessageBean> messageDestinations)
    {
        mMessageDestinations = messageDestinations;
    }

    public boolean isNewText()
    {
        return mNewText;
    }

    public void setNewText(boolean newText)
    {
        mNewText = newText;
    }

    public List<AudioMessageBean> getNearbyFeatures()
    {
        return mNearbyFeatures;
    }

    public void setNearbyFeatures(List<AudioMessageBean> nearbyFeatures)
    {
        mNearbyFeatures = nearbyFeatures;
    }

    public boolean isPremium()
    {
        return mPremium;
    }

    public void setPremium(boolean premium)
    {
        mPremium = premium;
    }

    public boolean isCanPremium()
    {
        return mCanPremium;
    }

    public void setCanPremium(boolean canPremium)
    {
        mCanPremium = canPremium;
    }

    public int getCloudCover()
    {
        return mCloudCover;
    }

    public void setCloudCover(int cloudCover)
    {
        mCloudCover = cloudCover;
    }

    public int getPrecipitation()
    {
        return mPrecipitation;
    }

    public void setPrecipitation(int precipitation)
    {
        mPrecipitation = precipitation;
    }
}
