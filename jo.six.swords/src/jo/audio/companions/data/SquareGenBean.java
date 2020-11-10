package jo.audio.companions.data;

import java.util.ArrayList;
import java.util.List;

import jo.audio.util.ArrayType;
import jo.audio.util.model.data.AudioMessageBean;

public class SquareGenBean extends SquareBean
{
    private CoordBean mOrds;
    private List<AudioMessageBean> mDescription;
    private int mTerrain;
    private int mTerrainDepth;
    private float mAltitude;
    private float mCityScore;
    private float mFortScore;
    private int mFeature;
    private boolean mRoadNorth;
    private boolean mRoadSouth;
    private boolean mRoadEast;
    private boolean mRoadWest;
    private int mChallenge;
    private int mChallenge2;
    private List<SignPost> mSignposts = new ArrayList<>();
    private DemenseBean mDemense;

    // utilities
    @Override
    public int hashCode()
    {
        return mOrds.hashCode();
    }
    
    @Override
    public String toString()
    {
        return "["+mOrds+", terrain="+mTerrain+", feature="+mFeature+", challenge="+mChallenge+"]";
    }

    public void setDescription(AudioMessageBean description)
    {
        if (mDescription == null)
            mDescription = new ArrayList<>();
        mDescription.add(description);
    }

    // getters and setters
    
    public CoordBean getOrds()
    {
        return mOrds;
    }
    public void setOrds(CoordBean ords)
    {
        mOrds = ords;
    }
    public int getTerrain()
    {
        return mTerrain;
    }
    public void setTerrain(int terrain)
    {
        mTerrain = terrain;
    }
    public int getFeature()
    {
        return mFeature;
    }
    public void setFeature(int feature)
    {
        mFeature = feature;
    }
    public int getRoadNorth()
    {
        return mRoadNorth ? T_ROAD : T_NONE;
    }
    public void setRoadNorth(int roadNorth)
    {
        mRoadNorth = roadNorth != T_NONE;
    }
    public int getRoadSouth()
    {
        return mRoadSouth ? T_ROAD : T_NONE;
    }
    public void setRoadSouth(int roadSouth)
    {
        mRoadSouth = roadSouth != T_NONE;
    }
    public int getRoadEast()
    {
        return mRoadEast ? T_ROAD : T_NONE;
    }
    public void setRoadEast(int roadEast)
    {
        mRoadEast = roadEast != T_NONE;
    }
    public int getRoadWest()
    {
        return mRoadWest ? T_ROAD : T_NONE;
    }
    public void setRoadWest(int roadWest)
    {
        mRoadWest = roadWest != T_NONE;
    }

    public float getAltitude()
    {
        return mAltitude;
    }

    public void setAltitude(float altitude)
    {
        mAltitude = altitude;
    }

    public float getCityScore()
    {
        return mCityScore;
    }

    public void setCityScore(float cityScore)
    {
        mCityScore = cityScore;
    }

    public int getChallenge()
    {
        return mChallenge;
    }

    public void setChallenge(int challenge)
    {
        mChallenge = challenge;
    }

    public float getFortScore()
    {
        return mFortScore;
    }

    public void setFortScore(float fortScore)
    {
        mFortScore = fortScore;
    }

    public int getTerrainDepth()
    {
        return mTerrainDepth;
    }

    public void setTerrainDepth(int terrainDepth)
    {
        mTerrainDepth = terrainDepth;
    }

    public int getChallenge2()
    {
        return mChallenge2;
    }

    public void setChallenge2(int challenge2)
    {
        mChallenge2 = challenge2;
    }

    @ArrayType(type=SignPost.class)
    public List<SignPost> getSignposts()
    {
        return mSignposts;
    }

    public void setSignposts(List<SignPost> signposts)
    {
        mSignposts = signposts;
    }

    @Override
    public int getRiverNorth()
    {
        return R_NONE;
    }

    @Override
    public int getRiverSouth()
    {
        return R_NONE;
    }

    @Override
    public int getRiverEast()
    {
        return R_NONE;
    }

    @Override
    public int getRiverWest()
    {
        return R_NONE;
    }

    public DemenseBean getDemense()
    {
        return mDemense;
    }

    public void setDemense(DemenseBean demense)
    {
        mDemense = demense;
    }

    public List<AudioMessageBean> getDescription()
    {
        return mDescription;
    }

    public void setDescription(List<AudioMessageBean> description)
    {
        mDescription = description;
    }
}
