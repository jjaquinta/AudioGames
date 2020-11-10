package jo.audio.companions.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

public class FeatureInstanceBean
{
    private FeatureBean mFeature;
    private Map<String, String> mRoomToMonster = new HashMap<>();
    private Map<String, JSONObject> mRoomToMessage = new HashMap<>();
    private Set<String> mEncountersTriggered = new HashSet<>();
    private Map<String, CompMonsterInstanceBean[]> mEncountersUnfinished = new HashMap<>();

    // utilities
    
    // getters and setters
    public FeatureBean getFeature()
    {
        return mFeature;
    }
    public void setFeature(FeatureBean feature)
    {
        mFeature = feature;
    }
    public Map<String, String> getRoomToMonster()
    {
        return mRoomToMonster;
    }
    public void setRoomToMonster(Map<String, String> roomToMonster)
    {
        mRoomToMonster = roomToMonster;
    }
    public Set<String> getEncountersTriggered()
    {
        return mEncountersTriggered;
    }
    public void setEncountersTriggered(Set<String> encountersTriggered)
    {
        mEncountersTriggered = encountersTriggered;
    }
    public Map<String, CompMonsterInstanceBean[]> getEncountersUnfinished()
    {
        return mEncountersUnfinished;
    }
    public void setEncountersUnfinished(
            Map<String, CompMonsterInstanceBean[]> encountersUnfinished)
    {
        mEncountersUnfinished = encountersUnfinished;
    }
    public Map<String, JSONObject> getRoomToMessage()
    {
        return mRoomToMessage;
    }
    public void setRoomToMessage(Map<String, JSONObject> roomToMessage)
    {
        mRoomToMessage = roomToMessage;
    }
}
