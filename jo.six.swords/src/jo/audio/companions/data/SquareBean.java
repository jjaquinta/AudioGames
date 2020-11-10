package jo.audio.companions.data;

import java.util.List;

import org.json.simple.IJSONAble;
import org.json.simple.JSONObject;

import jo.audio.companions.logic.CompConstLogic;
import jo.audio.util.ArrayType;
import jo.audio.util.model.data.AudioMessageBean;

public abstract class SquareBean
{
    public static final int T_NONE = 0;
    public static final int T_TRACK = 1;
    public static final int T_ROAD = 2;
    public static final int T_HIGHWAY = 3;
    public static final int T_BRIDGE = 4;
    
    public static final int R_NONE = 0;
    public static final int R_BROOK = 1;
    public static final int R_STREAM = 2;
    public static final int R_RIVER = 3;
    
    private long mLastUsed;

    // utilities
    
    public boolean isAnyRoads()
    {
        return isRoadEast() || isRoadNorth() || isRoadWest() || isRoadSouth();
    }
    
    public boolean isAnyRivers()
    {
        return isRiverEast() || isRiverNorth() || isRiverWest() || isRiverSouth();
    }
    
    public int countRoads()
    {
        int count = 0;
        if (isRoadEast())
            count++;
        if (isRoadWest())
            count++;
        if (isRoadNorth())
            count++;
        if (isRoadSouth())
            count++;
        return count;
    }
    
    public boolean isRoad(int dir)
    {
        switch (dir)
        {
            case CompOperationBean.NORTH:
                return isRoadNorth();
            case CompOperationBean.SOUTH:
                return isRoadSouth();
            case CompOperationBean.EAST:
                return isRoadEast();
            case CompOperationBean.WEST:
                return isRoadWest();
        }
        return false;
    }
    
    public boolean isTown()
    {
        return CompConstLogic.isTown(getFeature());
    }
    
    public boolean isCastle()
    {
        return CompConstLogic.isCastle(getFeature());
    }
    
    public boolean isRuin()
    {
        return CompConstLogic.isRuin(getFeature());
    }
    
    public void addSign(CoordBean dest, int dist, int dir)
    {
        SignPost sign = new SignPost();
        sign.setDestination(dest);
        sign.setDistance(dist);
        sign.setDirection(dir);
        getSignposts().add(sign);
    }
    
    public void removeDescription(AudioMessageBean msg)
    {
        if (getDescription() == null)
            return;
        getDescription().remove(msg);
    }

    // getters
    
    public abstract CoordBean getOrds();
    public abstract int getTerrain();
    public abstract int getFeature();
    public abstract void setFeature(int feature);
    public abstract List<AudioMessageBean> getDescription();
    public abstract int getRoadNorth();
    public abstract int getRoadSouth();
    public abstract int getRoadEast();
    public abstract int getRoadWest();
    public abstract int getRiverNorth();
    public abstract int getRiverSouth();
    public abstract int getRiverEast();
    public abstract int getRiverWest();
    public abstract float getAltitude();
    public abstract int getChallenge();
    public abstract int getChallenge2();
    public abstract int getTerrainDepth();
    public abstract DemenseBean getDemense();
    @ArrayType(type=SignPost.class)
    public abstract List<SignPost> getSignposts();

    public boolean isRoadNorth() { return getRoadNorth() != T_NONE; }
    public boolean isRoadSouth() { return getRoadSouth() != T_NONE; }
    public boolean isRoadEast() { return getRoadEast() != T_NONE; }
    public boolean isRoadWest() { return getRoadWest() != T_NONE; }
    public boolean isRiverNorth() { return getRiverNorth() != R_NONE; }
    public boolean isRiverSouth() { return getRiverSouth() != R_NONE; }
    public boolean isRiverEast() { return getRiverEast() != R_NONE; }
    public boolean isRiverWest() { return getRiverWest() != R_NONE; }
    
    public class SignPost implements IJSONAble
    {
        private CoordBean mDestination;
        private int       mDirection;
        private int       mDistance;

        // utilities
        @Override
        public String toString()
        {
            return "[dest="+mDestination+", dir="+mDirection+", dist="+mDistance+"]";
        }

        @Override
        public JSONObject toJSON()
        {
            JSONObject json = new JSONObject();
            json.put("destination", mDestination.toJSON());
            json.put("direction", mDirection);
            json.put("distance", mDistance);
            return json;
        }

        @Override
        public void fromJSON(JSONObject o)
        {
            mDestination = new CoordBean();
            mDestination.fromJSON((JSONObject)o.get("destination"));
            mDirection = ((Number)o.get("direction")).intValue();
            mDistance = ((Number)o.get("distance")).intValue();
        }

        // getters and setters
        public CoordBean getDestination()
        {
            return mDestination;
        }
        public void setDestination(CoordBean destination)
        {
            mDestination = destination;
        }
        public int getDirection()
        {
            return mDirection;
        }
        public void setDirection(int direction)
        {
            mDirection = direction;
        }
        public int getDistance()
        {
            return mDistance;
        }
        public void setDistance(int distance)
        {
            mDistance = distance;
        }
    }

    public long getLastUsed()
    {
        return mLastUsed;
    }

    public void setLastUsed(long lastUsed)
    {
        mLastUsed = lastUsed;
    }
}
